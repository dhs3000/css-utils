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

/**
 * Simple css container.
 */
public class CssContainer {
    /** all simple rules. */
    public final Collection<Rule> simpleRules;

    /** all media queries and their rules. */
    public final Collection<MediaQuery> mediaQueries;

    CssContainer(Collection<Rule> simpleRules, Collection<MediaQuery> mediaQueries) {
        this.simpleRules = simpleRules;
        this.mediaQueries = mediaQueries;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Rule rule : simpleRules) {
            result.append(rule.toString());
        }

        for (MediaQuery query : mediaQueries) {
            result.append(query.toString());
        }
        return result.toString();
    }
}