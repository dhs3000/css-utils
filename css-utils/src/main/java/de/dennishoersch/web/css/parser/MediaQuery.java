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

import java.util.List;

/**
 * @author hoersch
 */
public class MediaQuery {

    private final String _selector;

    private final List<Rule> _rules;

    MediaQuery(String mediaQuery) {
        this(MediaQuery.extractSelector(mediaQuery), MediaQuery.extractRules(mediaQuery));
    }

    private MediaQuery(String selector, String rules) {
        _selector = selector;
        _rules = CssParser.parseAndMerge(rules);
    }

    @Override
    public String toString() {
        String allRules = _rules.toString();
        allRules = allRules.substring(1, allRules.length() - 1);
        return "@media " + _selector + "{" + allRules + "}";
    }

    private static String extractSelector(String mediaQuery) {
        int start = mediaQuery.indexOf("@media") + 6;
        int end = mediaQuery.indexOf("{");
        return mediaQuery.substring(start, end).trim();
    }

    private static String extractRules(String mediaQuery) {
        int start = mediaQuery.indexOf("{");
        int i = start;
        for (int k = 0;; i++) {
            char c = mediaQuery.charAt(i);
            if (c == '{') {
                k++;
            } else if (c == '}') {
                k--;
            }
            if (k == 0) {
                break;
            }
        }

        return mediaQuery.substring(start + 1, i).trim();
    }
}
