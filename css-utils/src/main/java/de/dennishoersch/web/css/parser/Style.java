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
 *
 */
public class Style {
    private final String _name;

    private final String _value;

    public Style(String style) {
        int i = style.indexOf(":");
        if (i <= 0) {
            throw new IllegalStateException("Style is incorrect: " + style);
        }
        _name = style.substring(0, i).trim();
        String tmp = style.substring(i + 1).trim();
        if (tmp.endsWith(";")) {
            tmp = tmp.substring(0, tmp.length() - 1);
        }
        _value = tmp;
    }

    public String getName() {
        return _name;
    }

    public String getValue() {
        return _value;
    }

    @Override
    public String toString() {
        return _name + ":" + _value;
    }
}
