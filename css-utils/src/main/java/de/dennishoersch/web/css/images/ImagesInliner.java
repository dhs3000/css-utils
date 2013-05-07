/*
 * Copyright 2012-2013 Dennis Hörsch.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dennishoersch.web.css.images;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 *
 * http://css-tricks.com/data-uris/
 *
 * @author hoersch
 */
public class ImagesInliner {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ImagesInliner.class.getName());

    private static final Pattern _WITH_URL = Pattern.compile("url\\(([^\\)]*)\\)");

    private static final Joiner JOINER = Joiner.on("\n");

    public static String inline(String stylesheet) throws IOException {
        return inlineImages(new StringReader(stylesheet));
    }

    private static String inlineImages(Reader stylesheet) throws IOException {
        BufferedReader reader = new BufferedReader(stylesheet);
        List<String> lines = Lists.newArrayList();
        String line;
        while ((line = reader.readLine()) != null) {
            String line_ = inlineIfNeccessary(line);
            lines.add(line_);
        }
        return JOINER.join(lines);
    }

    private static String inlineIfNeccessary(String line) throws IOException {
        String result = line;
        Matcher matcher = _WITH_URL.matcher(line);
        while (matcher.find()) {
            String url = matcher.group(1);
            String inlined = inlineUrl(url.replace("'", "").replace("\"", ""));
            if (inlined != null) {
                inlined = "url(data:" + inlined + ")";
                result = StringUtils.replaceOnce(result, matcher.group(), inlined);
            }
        }
        return result;
    }

    private static String inlineUrl(String url) throws IOException {
        Path path;
        if (url.startsWith("http")) {
            path = downloadToTmp(url);
        } else {
            path = Paths.get(url);
        }

        String contentType = Files.probeContentType(path);
        if (!contentType.contains("image")) {
            return null;
        }

        byte[] bytes = Files.readAllBytes(path);

        return contentType + ";base64," + org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }

    private static Path downloadToTmp(String url) throws IOException {
        try {
            URL weburl = new URL(url);
            byte[] bytes = IOUtils.toByteArray(weburl.openStream());

            Path tempFile = Files.createTempFile("__" + ImagesInliner.class.getSimpleName() + "_", "");
            Files.write(tempFile, bytes);

            return tempFile;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "url of wrong format: '" + url + "'!", e);
            throw e;
        }
    }
}
