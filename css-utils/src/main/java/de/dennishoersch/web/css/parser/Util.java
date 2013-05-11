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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author hoersch
 */
public class Util {
    static String stripUnnecessary(String css) {
        String s = css;

        // Problem: Some urls might contain '//':
        // url(http://www.somedomain.de/image.png)
        // url('http://www.somedomain.de/image.png')
        // url("http://www.somedomain.de/image.png")

        URLsHolderAndText urlsHolderAndText = URLsHolderAndText.of(s);

        s = urlsHolderAndText.textWithoutURLs();

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

        return urlsHolderAndText.reInsertURLs(s.trim());
    }

    private static class URLsHolderAndText {
        private static final Pattern _URL = Pattern.compile("url\\(([^\\)]*)\\)");
        private final String _text;
        private final Map<String, String> _replacements;

        private URLsHolderAndText(String text, Map<String, String> replacements) {
            _text = text;
            _replacements = replacements;
        }

        public static URLsHolderAndText of(String text) {
            Map<String, String> marker = new HashMap<>();

            String result = text;
            Matcher matcher = _URL.matcher(text);

            for (int i = 0; matcher.find(); i++) {
                String replacement = "__X__URL_" + i + "_URL_X__";
                String original = matcher.group();
                result = StringUtils.replaceOnce(result, original, replacement);
                marker.put(replacement, original);
            }

            return new URLsHolderAndText(result, marker);
        }

        public String textWithoutURLs() {
            return _text;
        }

        public String reInsertURLs(String text) {
            String result = text;
            for (Map.Entry<String, String> entry : _replacements.entrySet()) {
                result = StringUtils.replaceOnce(result, entry.getKey(), entry.getValue());
            }
            return result;
        }

    }
}
