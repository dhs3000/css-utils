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
import java.nio.file.Path;

/**
 * A resolver of URLs to local files.
 * @author hoersch
 */
public interface URLPathResolver {

    /**
     * Resolves the given URL to a local path. The content of the given URL might be downloaded to a temporary file.
     * @param url
     * @throws IOException
     * @return the path to the content of the given URL
     */
    Path resolve(String url) throws IOException;
}
