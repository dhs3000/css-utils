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
package de.dennishoersch.web.css;

import java.io.IOException;

import de.dennishoersch.web.css.parser.Parser;
import de.dennishoersch.web.css.parser.Stylesheet;

/**
 * Normalizes stylesheets such that rules with the same selector are combined into one rule and rules with the same styles are combined to one block.
 * <pre>
 * .rule1 {
 *     background-color: red;
 *     padding: 0;
 * }
 * .rule1 {
 *     color: yellow;
 *     padding: 100px;
 * }
 * </pre>
 * results in
 * <pre>
 * .rule1 {
 *     background-color: red;
 *     color: yellow;
 *     padding: 100px;
 * }
 * </pre>
 * and
 * <pre>
 * .rule1 {
 *     background-color: red;
 * }
 * .rule2 {
 *     background-color: red;
 * }
 * </pre>
 * results in
 * <pre>
 * .rule1, .rule2 {
 *     background-color: red;
 * }
 * </pre>
 * <p>Reads the input to be parsed directly from System.in if no arguments are
 * given, otherwise all arguments have to refer existing files to be read and
 * concatenated.</p>
 * <p>Prints the result to System.out</p>
 *
 * @author hoersch
 */
public class Normalizer {

    public static void main(String[] args) throws IOException {
        String css = CmdLineUtil.readArgumentsOrStdIn(args);

        Stylesheet stylesheet = Parser.parse(css);

        System.out.println(stylesheet);
    }




}
