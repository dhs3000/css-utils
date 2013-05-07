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

import com.google.common.collect.ImmutableList;

/**
 * @author hoersch
 */
public class Stylesheet {

    private final List<Rule> _rules;

    Stylesheet(List<Rule> rules) {
        _rules = rules == null ? ImmutableList.<Rule> of() : ImmutableList.<Rule> copyOf(rules);
    }

    /**
     * @return the rules
     */
    public List<Rule> getRules() {
        return _rules;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Rule rule : _rules) {
            result.append(rule.toString())
//            .append("\n")
            ;
        }
        return result.toString();
    }

}
