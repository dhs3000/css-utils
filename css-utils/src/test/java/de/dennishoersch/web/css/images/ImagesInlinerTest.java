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
package de.dennishoersch.web.css.images;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

/**
 * @author hoersch
 *
 */
public class ImagesInlinerTest {
    //@formatter:off
    private String stylesheetJPG =
                                "li { \n" +
                                "   background: url(src/test/resources/test-image-red.jpg) no-repeat left center;\n" +
                                "   padding: 5px 0 5px 25px;\n"+
                                "}";

    private String stylesheetPNG =
                                "li { \n" +
                                "   background: url(src/test/resources/test-image-green.png) no-repeat left center;\n" +
                                "   padding: 5px 0 5px 25px;\n"+
                                "}";

    private String stylesheetWithURLImage =
                                "li { \n" +
                                "   background: url(http://static.kyoma.de/website_images/logo.png) no-repeat left center;\n" +
                                "   padding: 5px 0 5px 25px;\n"+
                                "}";


    private String styleWithNoImage =
                                "li {" +
                                "    font-family: 'Signika';\n"+
                                "    font-style: normal;\n"+
                                "    font-weight: 400;\n"+
                                "    src: local('Signika'), local('Signika-Regular'), url(http://www.kyoma.de/static/fonts/google-webfonts-Signika-v3.woff) format('woff');\n"+
                                "}";

    //@formatter:on

    @Test
    public void test_no_inline_font() throws IOException {

        String result = process(styleWithNoImage);

        assertThat(result, containsString("http://www.kyoma.de/static/fonts/google-webfonts-Signika-v3.woff"));
    }

    @Test
    public void test_inline_png_web_image() throws IOException {

        String result = process(stylesheetWithURLImage);

        assertThat(result, not(containsString("logo.png")));
        assertThat(result, containsString("data:image/png;base64,"));
    }

    @Test
    public void test_inline_jpg_image() throws IOException {

        String result = process(stylesheetJPG);

        assertThat(result, not(containsString("test-image-red.jpg")));
        assertThat(result, containsString("data:image/jpeg;base64,"));
    }

    @Test
    public void test_inline_png_image() throws IOException {

        String result = process(stylesheetPNG);

        assertThat(result, not(containsString("test-image-green.png")));
        assertThat(result, containsString("data:image/png;base64,"));
    }

    private String process(String stylesheet) throws IOException {
        String result = ImagesInliner.inline(stylesheet);
        return result;
    }

}
