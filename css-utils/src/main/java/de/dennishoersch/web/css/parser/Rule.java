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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * @author hoersch
 */
public class Rule {
    private static final Splitter _STYLE_SPLITTER = Splitter.on(";").omitEmptyStrings().trimResults();

    private static final Joiner _STYLE_JOINER = Joiner.on(";").skipNulls();

    private final String _selector;

    private final Collection<Style> _styles;

    /**
     * Styles in einer Regel, die einen vorherigen Wert überschreiben, ersetzen
     * den anderen. Außer wenn einer der Style-Werte ein Vendor-Prefix enthält.
     *
     * @param selector
     * @param styles
     */
    Rule(String selector, String styles) {
        _selector = selector;

        Multimap<String, Style> reduced = LinkedHashMultimap.create();
        for (String style_ : _STYLE_SPLITTER.split(styles)) {
            Style style = new Style(style_);
            reduced.put(style.getName(), style);
        }

        // Wenn keiner der Werte zu einem Style ein Vendor-Prefix enthält, dann
        // kann der letzte alle anderen überschreiben
        _styles = Lists.newArrayList();
        for (Map.Entry<String, Collection<Style>> entry : reduced.asMap().entrySet()) {
            Collection<Style> values = entry.getValue();
            if (Iterables.any(values, HasVendorPrefixValue.INSTANCE)) {
                _styles.addAll(values);
            } else {
                _styles.add(Iterables.getLast(values));
            }
        }
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

    @Override
    public String toString() {
        String s = allStyles();
        return _selector + "{" + s + "}";
    }

    String allStyles() {
        return _STYLE_JOINER.join(_styles) + ";";
    }

    String getSelector() {
        return _selector;
    }

    Collection<Style> getStyles() {
        return _styles;
    }
}
