CSS utilities
=============
- Helper to normalize a (collection of) stylesheet(s)
    - Combines rules with the same selector into one rule and rules with the same styles to one block
    - Properties with the same name override in order they appear in the stylesheet; Except if the values contain vendor-prefixes
- Inline (background) images as data-uri direct into the stylesheet
    - Reduces request because all background images are already inlined in the stylesheet

####Normalize
The stylesheet

     .rule1 {
         background-color: red;
         padding: 0;
     }
     .rule1 {
         color: yellow;
         padding: 100px;
     }

results in

    .rule1 {
        background-color: red;
        color: yellow;
        padding: 100px;
    }

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


