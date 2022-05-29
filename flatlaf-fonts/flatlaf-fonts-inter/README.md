Inter font
==========

This sub-project contains fonts from the Inter font family and bundles them into
an easy-to-use and redistributable JAR.

**Note:** This font requires **Java 10 or later**. It is displayed too large in
Java 8 and 9.

Font home page: https://rsms.me/inter/

GitHub project: https://github.com/rsms/inter

License:
[SIL OPEN FONT LICENSE Version 1.1](src/main/resources/com/formdev/flatlaf/fonts/inter/LICENSE.txt)


How to install?
---------------

Invoke the `install()` method once in your `main()` method (on AWT thread):

~~~java
FlatInterFont.install();
~~~


How to use?
-----------

~~~java
// basic styles
new Font( FlatInterFont.FAMILY, Font.PLAIN, 12 );
new Font( FlatInterFont.FAMILY, Font.ITALIC, 12 );
new Font( FlatInterFont.FAMILY, Font.BOLD, 12 );
new Font( FlatInterFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );

// light
new Font( FlatInterFont.FAMILY_LIGHT, Font.PLAIN, 12 );
new Font( FlatInterFont.FAMILY_LIGHT, Font.ITALIC, 12 );

// semibold
new Font( FlatInterFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
new Font( FlatInterFont.FAMILY_SEMIBOLD, Font.ITALIC, 12 );
~~~


Download
--------

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-inter
    version:     (see button below)

Otherwise download `flatlaf-fonts-inter-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-inter/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-inter)
