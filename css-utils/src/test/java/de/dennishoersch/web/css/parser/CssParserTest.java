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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.google.common.io.Files;

/**
 *
 * @author hoersch
 */
public class CssParserTest {

    @Test
    public void testCompressRulesHISinOneCss() throws Exception {

        StringWriter merged = new StringWriter();

        String css = getFileContent("simpleTest.css");

        CssContainer rulesAndMediaQueries = CssParser.parse(css);

        for (Rule rule : rulesAndMediaQueries.simpleRules) {
            IOUtils.write(rule.toString() + "\n", merged);
        }
        for (MediaQuery query : rulesAndMediaQueries.mediaQueries) {
            IOUtils.write(query.toString() + "\n", merged);
        }

        merged.flush();
        merged.close();

        System.out.println();
        System.out.println(merged);
        System.out.println();

        // The overriding removes the 'yellow' definition
        assertThat(merged.toString(), not(containsString("yellow")));

        // Vendor prefixes in values do not override
        assertThat(merged.toString(), containsString("-moz"));
        assertThat(merged.toString(), containsString("-webkit"));

        // was ist besser oder effizienter?

        // .paddingAll, .paddingWithMarginTop {padding: 20px;}
        // .paddingWithMarginTop {margin-top: 10px;}

        // vs:
        // .paddingAll{padding:20px;}
        // .paddingWithMarginTop{padding:20px;margin-top:10px;}
    }

    private String getFileContent(String filename) throws IOException, URISyntaxException {
        return Files.toString(getFile(filename), Charset.defaultCharset());
    }

    private File getFile(String filename) throws URISyntaxException {
        return new File(getClass().getClassLoader().getResource(getPathToScript() + "/" + filename).toURI());
    }

    private String getPathToScript() {
        String packageName = getClass().getPackage().getName();
        return packageName.replace('.', '/');
    }

}
