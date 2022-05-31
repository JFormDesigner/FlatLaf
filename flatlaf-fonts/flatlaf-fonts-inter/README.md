Inter font
==========

This sub-project contains fonts from the Inter font family and bundles them into
an easy-to-use and redistributable JAR.

**Note**: This font does not work correctly in older Java 8 versions (before
8u212) and in Java 9 because it is displayed way too large.

Font home page: https://rsms.me/inter/

GitHub project: https://github.com/rsms/inter

License:
[SIL OPEN FONT LICENSE Version 1.1](src/main/resources/com/formdev/flatlaf/fonts/inter/LICENSE.txt)


How to install?
---------------

Invoke the `install()` method once (e.g. in your `main()` method; on AWT
thread):

~~~java
FlatInterFont.install();
~~~


How to use?
-----------

Use as default font:

~~~java
FlatLaf.setPreferredFontFamily( FlatInterFont.FAMILY );
FlatLaf.setPreferredLightFontFamily( FlatInterFont.FAMILY_LIGHT );
FlatLaf.setPreferredSemiboldFontFamily( FlatInterFont.FAMILY_SEMIBOLD );
~~~

Create fonts:

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

Not yet available.

<!--

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-inter
    version:     (see button below)

Otherwise download `flatlaf-fonts-inter-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-inter/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-inter)

-->
