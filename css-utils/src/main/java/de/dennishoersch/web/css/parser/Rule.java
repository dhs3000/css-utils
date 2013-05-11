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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

/**
 * @author hoersch
 */
public class Rule {

    private static final Joiner _STYLE_JOINER = Joiner.on(";").skipNulls();
    private static final Joiner _RULE_JOINER = Joiner.on("").skipNulls();

    private final String _selector;
    private final List<Style> _styles;
    private final List<Rule> _subRules;

    public Rule(String selector, List<Style> styles, List<Rule> subRules) {
        _selector = selector;
        _styles = styles == null ? ImmutableList.<Style> of() : ImmutableList.<Style> copyOf(styles);
        _subRules = subRules == null ? ImmutableList.<Rule> of() : ImmutableList.<Rule> copyOf(subRules);
    }

    /**
     * @return the selector
     */
    public String getSelector() {
        return _selector;
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

    @Override
    public String toString() {
        String content = getContent();
        return _selector + "{" + content + "}";
    }

    String getContent() {
        String content = "";
        if (!_styles.isEmpty()) {
            content += _STYLE_JOINER.join(_styles) + ";";
        }
        if (!_subRules.isEmpty()) {
            content += _RULE_JOINER.join(_subRules);
        }
        return content;
    }
}
