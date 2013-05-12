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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.dennishoersch.web.css.images.resolver.FilesystemPathResolver;
import de.dennishoersch.web.css.images.resolver.HttpPathResolver;
import de.dennishoersch.web.css.images.resolver.URLPathResolver;

/**
 * Inlines all images in a stylesheet as data-URIs.
 * <p>
 * If used with the helper {@link ImagesInliner#inline(String)} the images has to be on the local file system.
 * </p>
 * <p>
 * See also for example <a href="http://css-tricks.com/data-uris/">http://css-tricks.com/data-uris/</a>.
 * </p>
 *
 * @author hoersch
 */
public class ImagesInliner {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ImagesInliner.class.getName());

    private static final Pattern _URL = Pattern.compile("url\\(([^\\)]*)\\)");

    private static final Joiner _LINE_JOINER = Joiner.on("\n");

    private final URLPathResolver _pathResolver;

    private ImagesInliner(URLPathResolver pathResolver) {
        _pathResolver = pathResolver;
    }

    /**
     *
     * @param pathResolver
     * @return a new ImagesInliner with the given resolver
     */
    public static ImagesInliner with(URLPathResolver... pathResolver) {
        class MultipleTypePathResolver implements URLPathResolver {

            private final URLPathResolver[] _pathResolver;

            public MultipleTypePathResolver(URLPathResolver... resolver) {
                _pathResolver = resolver;
            }

            @Override
            public Path resolve(String url) throws IOException {
                for (URLPathResolver resolver : _pathResolver) {
                    Path resolved = resolver.resolve(url);
                    if (resolved != null) {
                        return resolved;
                    }
                }
                return null;
            }
        }
        return new ImagesInliner(new MultipleTypePathResolver(pathResolver));
    }

    /**
     * Inlines all images. They must be located whether as http URL or in the local filesystem.
     *
     * @param stylesheet
     * @return the stylesheet with all images inlined
     * @throws IOException
     */
    public static String inline(String stylesheet) throws IOException {
        return with(new HttpPathResolver(), new FilesystemPathResolver()).process(stylesheet);
    }

    public String process(String stylesheet) throws IOException {
        return inlineImages(new StringReader(stylesheet));
    }

    private String inlineImages(Reader stylesheet) throws IOException {
        BufferedReader reader = new BufferedReader(stylesheet);
        List<String> lines = Lists.newArrayList();
        String line;
        while ((line = reader.readLine()) != null) {
            String line_ = inlineIfNeccessary(line);
            lines.add(line_);
        }
        return _LINE_JOINER.join(lines);
    }

    private String inlineIfNeccessary(String line) throws IOException {
        String result = line;
        Matcher matcher = _URL.matcher(line);
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

    private String inlineUrl(String url) throws IOException {
        Path path = _pathResolver.resolve(url);

        if (path == null) {
            logger.log(Level.WARNING, "Could not inline URL '" + url + "'!");
            return null;
        }

        String contentType = Files.probeContentType(path);
        if (!contentType.contains("image")) {
            return null;
        }

        byte[] bytes = Files.readAllBytes(path);

        return contentType + ";base64," + org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
    }

}
