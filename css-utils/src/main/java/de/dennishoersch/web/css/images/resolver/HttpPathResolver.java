/*
 * Copyright 2012-2013 the original author or authors.
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
package de.dennishoersch.web.css.images.resolver;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author hoersch
 */
public class HttpPathResolver implements URLPathResolver {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(HttpPathResolver.class.getName());

    @Override
    public Path resolve(String url) throws IOException {
        if (!url.startsWith("http")) {
            return null;
        }
        return downloadToTmp(url);
    }

    private static Path downloadToTmp(String url) throws IOException {
        try {
            URL weburl = new URL(url);
            byte[] bytes = IOUtils.toByteArray(weburl.openStream());

            Path tempFile = Files.createTempFile("__" + HttpPathResolver.class.getSimpleName() + "_", "");
            Files.write(tempFile, bytes);

            return tempFile;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "url of wrong format: '" + url + "'!", e);
            throw e;
        }
    }
}
