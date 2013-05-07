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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;


/**
 * 
 * The css to be parsed must not contain @imports and has to be strict, which
 * means styles in a rule must be separated by semicolon.
 * 
 *      * Styles in einer Regel, die einen vorherigen Wert überschreiben, ersetzen
     * den anderen. Außer wenn einer der Style-Werte ein Vendor-Prefix enthält.
     * 
     * 
 * @author hoersch
 *
 */
public class Parser {
    private static final Splitter _STYLE_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();
    private static final Splitter _SELECTOR_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    public static Stylesheet parse(String stylesheet) {
        if (stylesheet == null) {
            throw new NullPointerException("stylesheet");
        }
        String stylesheet_ = Util.stripUnnecessary(stylesheet);
        if (stylesheet_.contains("@import")) {
            throw new IllegalArgumentException("Stylesheet must not contain any @import tag, but is '" + stylesheet + "'!");
        }

        // @-regeln nach vorne?
        // order inerhalb aber beibehalten

        List<Rule> rules = parseIntern(stylesheet_);

//        for (Rule r : rules) {
//            System.out.println();
//            System.out.println(r);
//        }

        return new Stylesheet(rules);
    }

    private static List<Rule> parseIntern(String stylesheet) {
        List<Rule> result = new ArrayList<>();
        ParseResult parseResult = null;

        for (int i = 0;; i = parseResult.getNext()) {
            parseResult = parseNext(stylesheet, i);
            if (parseResult == null) {
                break;
            }
            result.addAll(parseResult.getRules());
        }

        result = merge(result);
        result = mergeByContent(result);

        return result;
    }

    public static ParseResult parseNext(String stylesheet, int i) {
        int next = -1;
        if (i < stylesheet.length()) {
            next = stylesheet.indexOf('{', i);
            if (next < 0) {
                System.err.println("keine klammer auf mehr");
                return null;
            }

            String selector = stylesheet.substring(i, next);
            int k = next;
            for (int count = 0; k < stylesheet.length(); k++) {
                char c = stylesheet.charAt(k);
                if (c == '{') {
                    count++;
                } else if (c == '}') {
                    count--;
                }
                if (count == 0) {
                    break;
                }
            }

            String ruleset = stylesheet.substring(next + 1, k);

            RuleParseResult ruleParseResult = parseRule(ruleset);

            List<Rule> rules = new ArrayList<>();
            for (String selector_ : _SELECTOR_SPLITTER.split(selector)) {
                rules.add(new Rule(selector_, ruleParseResult.getStyles(), ruleParseResult.getSubRules()));
            }
            return new ParseResult(rules, k + 1);
        }
        return null;
    }

    private static RuleParseResult parseRule(String ruleset) {
        List<Rule> subRules = null;
        List<Style> styles = null;
        if (ruleset.contains("{")) {
            subRules = parseIntern(ruleset);
        } else {
            styles = parseStyles(ruleset);
        }
        return new RuleParseResult(styles, subRules);
    }

    private static class RuleParseResult {
        private final List<Style> _styles;
        private final List<Rule> _subRules;

        RuleParseResult(List<Style> styles, List<Rule> subRules) {
            _styles = styles;
            _subRules = subRules;
        }

        /**
         * @return the styles
         */
        public List<Style> getStyles() {
            return _styles;
        }

        /**
         * @return the subRules
         */
        public List<Rule> getSubRules() {
            return _subRules;
        }
    }

    private static List<Style> parseStyles(String styles) {
        Multimap<String, Style> reduced = LinkedHashMultimap.create();
        for (String style_ : _STYLE_SPLITTER.split(styles)) {
            Style style = new Style(style_);
            reduced.put(style.getName(), style);
        }

        // Wenn keiner der Werte zu einem Style ein Vendor-Prefix enthält, dann
        // kann der letzte alle anderen überschreiben
        List<Style> result = Lists.newArrayList();
        for (Map.Entry<String, Collection<Style>> entry : reduced.asMap().entrySet()) {
            Collection<Style> values = entry.getValue();
            if (Iterables.any(values, HasVendorPrefixValue.INSTANCE)) {
                result.addAll(values);
            } else {
                result.add(Iterables.getLast(values));
            }
        }
        return result;
    }

    private enum HasVendorPrefixValue implements Predicate<Style> {
        INSTANCE;
        //@formatter:off
        private static final List<String> _VENDOR_PREFIXES = new ImmutableList.Builder<String>()
                        .add("-moz")
                        .add("-webkit")
                        .add("-ms")
                        .add("-o")
                        .add("-wap")
                        .add("-xv")
                        .build();
        //@formatter:on
        @Override
        public boolean apply(Style input) {
            return Iterables.any(_VENDOR_PREFIXES, startsWith(input.getValue()));
        }

        private static Predicate<String> startsWith(final String string) {
            return new Predicate<String>() {

                @Override
                public boolean apply(String input) {
                    return string.startsWith(input);
                }
            };
        }
    }

    private static class ParseResult {

        private final List<Rule> _rules;
        private final int _next;

        /**
         * @param rules
         * @param next
         */
        public ParseResult(List<Rule> rules, int next) {
            this._rules = rules;
            this._next = next;
        }

        /**
         * @return the rules
         */
        public List<Rule> getRules() {
            return _rules;
        }

        /**
         * @return the next
         */
        public int getNext() {
            return _next;
        }

    }

    private static List<Rule> merge(List<Rule> rules) {
        List<Rule> result = Lists.newArrayList();

        Map<String, StringBuilder> mergedRules = Maps.newLinkedHashMap();

        for (Rule rule : rules) {
            mergeRules(rule, mergedRules);
        }

        for (Map.Entry<String, StringBuilder> entry : mergedRules.entrySet()) {
            String ruleset = entry.getValue().toString();
            RuleParseResult ruleParseResult = parseRule(ruleset);
            result.add(new Rule(entry.getKey(), ruleParseResult.getStyles(), ruleParseResult.getSubRules()));
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
        styles.append(rule.getContent());
    }

    private static List<Rule> mergeByContent(List<Rule> rules) {
        List<Rule> result = Lists.newArrayList();

        Map<String, StringBuilder> mergedRules = Maps.newLinkedHashMap();

        for (Rule rule : rules) {
            String content = rule.getContent();
            StringBuilder selectors = mergedRules.get(content);
            if (selectors == null) {
                selectors = new StringBuilder(rule.getSelector());
                mergedRules.put(content, selectors);
            } else {
                selectors.append(",").append(rule.getSelector());
            }

        }
        for (Map.Entry<String, StringBuilder> entry : mergedRules.entrySet()) {
            String ruleset = entry.getKey();
            RuleParseResult ruleParseResult = parseRule(ruleset);
            result.add(new Rule(entry.getValue().toString(), ruleParseResult.getStyles(), ruleParseResult.getSubRules()));
        }
        return result;
    }
}
