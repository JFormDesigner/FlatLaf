Roboto font
===========

This sub-project contains fonts from the Roboto font family and bundles them
into an easy-to-use and redistributable JAR.

Font home page: https://fonts.google.com/specimen/Roboto

GitHub project: https://github.com/googlefonts/roboto

License:
[Apache License, Version 2.0](src/main/resources/com/formdev/flatlaf/fonts/roboto/LICENSE.txt)


How to install?
---------------

Invoke the `install()` method once (e.g. in your `main()` method; on AWT
thread):

~~~java
FlatRobotoFont.install();
~~~


How to use?
-----------

Use as default font:

~~~java
FlatLaf.setPreferredFontFamily( FlatRobotoFont.FAMILY );
FlatLaf.setPreferredLightFontFamily( FlatRobotoFont.FAMILY_LIGHT );
FlatLaf.setPreferredSemiboldFontFamily( FlatRobotoFont.FAMILY_SEMIBOLD );
~~~

Create fonts:

~~~java
// basic styles
new Font( FlatRobotoFont.FAMILY, Font.PLAIN, 12 );
new Font( FlatRobotoFont.FAMILY, Font.ITALIC, 12 );
new Font( FlatRobotoFont.FAMILY, Font.BOLD, 12 );
new Font( FlatRobotoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );

// light
new Font( FlatRobotoFont.FAMILY_LIGHT, Font.PLAIN, 12 );
new Font( FlatRobotoFont.FAMILY_LIGHT, Font.ITALIC, 12 );

// semibold
new Font( FlatRobotoFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
new Font( FlatRobotoFont.FAMILY_SEMIBOLD, Font.ITALIC, 12 );
~~~


Download
--------

Not yet available.

<!--

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-roboto
    version:     (see button below)

Otherwise download `flatlaf-fonts-roboto-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-roboto/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-roboto)

-->
