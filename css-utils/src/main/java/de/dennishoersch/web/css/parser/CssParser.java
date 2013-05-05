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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The css to be parsed must not contain @imports and has to be strict, which
 * means styles in a rule must be separated by semicolon.
 *
 * @author hoersch
 */
public class CssParser {
    private static final Splitter _SELECTOR_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    /**
     *
     * @param css (must not contain any @import tags)
     * @return the normal rules and the media queries (including their rules)
     */
    public static CssContainer parse(String css) {
        if (css == null) {
            throw new NullPointerException();
        }
        String css_ = Util.stripUnnecessary(css);
        if (css_.contains("@import")) {
            throw new IllegalArgumentException("Stylesheet must not contain any @import tag, but is '" + css + "'!");
        }

        RulesAndMediaQueries tmp = extractMediaQueries(css_);

        List<Rule> rules = parseAndMerge(tmp.rules);

        List<MediaQuery> mediaQueries = Lists.newArrayList();
        for (String mediaQuery : tmp.mediaQueries) {
            MediaQuery query = new MediaQuery(mediaQuery);
            mediaQueries.add(query);
        }
        return new CssContainer(rules, mediaQueries);
    }

    /**
     * @param css (must not contain any @import tags)
     * @return pares and merged stylesheet rules
     */
    static List<Rule> parseAndMerge(String css) {
        if (css.isEmpty()) {
            return Collections.emptyList();
        }
        List<Rule> rules = parseInternal(css);
        rules = merge(rules);
        rules = mergeByContent(rules);
        return rules;
    }

    private static RulesAndMediaQueries extractMediaQueries(String css) {
        String css_ = css;
        List<String> mediaQueries = Lists.newArrayList();
        while (true) {
            MediaQuerySection mediaQuery = extractMediaQuery(css_);
            if (mediaQuery == null) {
                break;
            }
            mediaQueries.add(mediaQuery.query);
            css_ = css_.substring(0, mediaQuery.start) + css_.substring(mediaQuery.end + 1);
        }
        return new RulesAndMediaQueries(css_, mediaQueries);
    }

    private static MediaQuerySection extractMediaQuery(String css) {
        int media = css.indexOf("@media");
        if (media < 0) {
            return null;
        }
        int i = css.substring(media).indexOf("{") + media;
        for (int k = 0;; i++) {
            char c = css.charAt(i);
            if (c == '{') {
                k++;
            } else if (c == '}') {
                k--;
            }
            if (k == 0) {
                break;
            }
        }

        String mediaQuery = css.substring(media, i + 1);
        return new MediaQuerySection(mediaQuery, media, i);
    }

    private static List<Rule> parseInternal(String css) {
        List<Rule> rules = Lists.newArrayList();
        String[] rulesStrings = css.split("}");
        for (String rule : rulesStrings) {
            String[] parts = rule.split("\\{");
            if (parts.length != 2) {
                throw new IllegalStateException("Rule is incorrect: " + rule + "}");
            }
            String selector = parts[0];
            String styles = parts[1];
            for (String selector_ : _SELECTOR_SPLITTER.split(selector)) {
                rules.add(new Rule(selector_, styles));
            }
        }
        return rules;
    }

    private static List<Rule> merge(List<Rule> rules) {
        List<Rule> result = Lists.newArrayList();

        Map<String, StringBuilder> mergedRules = Maps.newLinkedHashMap();

        for (Rule rule : rules) {
            mergeRules(rule, mergedRules);
        }
        for (Map.Entry<String, StringBuilder> entry : mergedRules.entrySet()) {
            result.add(new Rule(entry.getKey(), entry.getValue().toString()));
        }
        return result;
    }

    private static void mergeRules(Rule rule, Map<String, StringBuilder> mergedRules) {
        StringBuilder styles = mergedRules.get(rule.getSelector());
        if (styles == null) {
            styles = new StringBuilder();
            mergedRules.put(rule.getSelector(), styles);
        }
        // erstmal nur alles anhängen
        // Überschriebene werden durch Rule automatisch beachtet
        for (Style style : rule.getStyles()) {
            styles.append(style).append(";");
        }
    }

    private static List<Rule> mergeByContent(List<Rule> rules) {
        List<Rule> result = Lists.newArrayList();

        Map<String, StringBuilder> mergedRules = Maps.newLinkedHashMap();

        for (Rule rule : rules) {
            String allStyles = rule.allStyles();
            StringBuilder selectors = mergedRules.get(allStyles);
            if (selectors == null) {
                selectors = new StringBuilder(rule.getSelector());
                mergedRules.put(allStyles, selectors);
            } else {
                selectors.append(",").append(rule.getSelector());
            }

        }
        for (Map.Entry<String, StringBuilder> entry : mergedRules.entrySet()) {
            result.add(new Rule(entry.getValue().toString(), entry.getKey()));
        }
        return result;
    }

    private static class RulesAndMediaQueries {
        final String rules;
        final List<String> mediaQueries;

        RulesAndMediaQueries(String rules, List<String> mediaQueries) {
            this.rules = rules;
            this.mediaQueries = mediaQueries;
        }
    }

    private static class MediaQuerySection {
        final String query;
        final int start;
        final int end;

        MediaQuerySection(String query, int start, int end) {
            this.query = query;
            this.start = start;
            this.end = end;
        }
    }
}
