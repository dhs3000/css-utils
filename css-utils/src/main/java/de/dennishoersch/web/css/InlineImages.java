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
package de.dennishoersch.web.css;

import java.io.IOException;

import de.dennishoersch.web.css.images.ImagesInliner;

/**
 * Inlines all referenced images. Uses data-URIs for this purpose.
 * Images must be refered whether locally relative to the execution directory or via 'http://'...
 *
 * @author hoersch
 */
public class InlineImages {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String css = CmdLineUtil.readArgumentsOrStdIn(args);

        String result = ImagesInliner.inline(css);

        System.out.println(result);
    }
}
