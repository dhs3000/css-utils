css-utils
=========

- Helper to normalize a (collection of) stylesheet(s)
- Inline (background) images as data-uri direct into the stylesheet

Build
-----
For direct usage create jar with all library dependencies:

    mvn assembly:assembly
    (mv dist/css-utils-*-jar-with-dependencies.jar dist/css-utils.jar)

Usage
-----

    java -jar PATH/TO/css-utils.jar < styles.css > styles-min.css
    OR    
    java -jar PATH/TO/css-utils.jar styles-1.css styles-2.css > styles-min.css


