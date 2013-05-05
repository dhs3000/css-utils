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
package de.dennishoersch.web.css.parser;

/**
 * @author hoersch
 */
public class Util {
    static String stripUnnecessary(String css) {
        String s = css;

        // Strip comments
        // First remove the many liners then the single line ones
        s = s.replaceAll("(?s)/\\*.*?\\*/", "");
        s = s.replaceAll("(?)//.*", "");

        // Strip whitespaces
        s = s.replace("\r\n", "").replace("\n", "");
        s = s.replace("\t", " ");
        s = s.replaceAll("\\s+", " ");

        s = s.replace(" {", "{");
        s = s.replace("{ ", "{");

        s = s.replace("} ", "}");
        s = s.replace(" }", "}");

        s = s.replace("; ", ";");
        s = s.replace(" ;", ";");
        s = s.replace(": ", ":");
        s = s.replace(" :", ":");

        // Remove empty rules. (Taken from YUICompressor)
        s = s.replaceAll("[^\\}\\{/;]+\\{\\}", "");

        // Strip multiple semicolons
        s = s.replaceAll(";;+", ";");

        return s.trim();
    }
}
