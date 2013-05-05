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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

import de.dennishoersch.web.css.parser.CssContainer;
import de.dennishoersch.web.css.parser.CssParser;

/**
 * Reads the input to be parsed directly from System.in if no arguments are
 * given, otherwise all arguments have to refer existing files to be read and
 * concatenated.
 * <p>Prints the result to System.out</p>
 *
 * @author hoersch
 */
public class Normalizer {

    public static void main(String[] args) throws IOException {
        List<InputSupplier<? extends InputStream>> input = Lists.newArrayList();
        if (args.length > 0) {
            for (String file : args) {
                File f = new File(file);
                if (!f.exists()) {
                    throw new IllegalArgumentException("File '" + file + "' does not exist!");
                }
                if (!f.canRead()) {
                    throw new IllegalArgumentException("File '" + file + "' can't be read!");
                }
                input.add(Files.newInputStreamSupplier(f));
            }
        } else {
            input.add(newInputStreamSupplier(System.in));
        }

        byte[] data = ByteStreams.toByteArray(ByteStreams.join(input));
        String css = new String(data);

        CssContainer cssContainer = CssParser.parse(css);

        System.out.println(cssContainer);
    }

    private static InputSupplier<InputStream> newInputStreamSupplier(final InputStream inputStream) {
        return new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                return inputStream;
            }
        };
    }
}
