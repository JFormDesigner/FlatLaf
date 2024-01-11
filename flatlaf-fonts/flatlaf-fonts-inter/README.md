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

Invoke following once (e.g. in your `main()` method; on AWT thread).

For lazy loading use:

~~~java
FlatInterFont.installLazy();
~~~

Or load immediately with:

~~~java
FlatInterFont.install();
// or
FlatInterFont.installBasic();
FlatInterFont.installLight();
FlatInterFont.installSemiBold();
~~~


How to use?
-----------

Use as application font (invoke before setting up FlatLaf):

~~~java
FlatLaf.setPreferredFontFamily( FlatInterFont.FAMILY );
FlatLaf.setPreferredLightFontFamily( FlatInterFont.FAMILY_LIGHT );
FlatLaf.setPreferredSemiboldFontFamily( FlatInterFont.FAMILY_SEMIBOLD );
~~~

Create single fonts:

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

If using lazy loading, invoke one of following before creating the font:

~~~java
FontUtils.loadFontFamily( FlatInterFont.FAMILY );
FontUtils.loadFontFamily( FlatInterFont.FAMILY_LIGHT );
FontUtils.loadFontFamily( FlatInterFont.FAMILY_SEMIBOLD );
~~~

E.g.:

~~~java
FontUtils.loadFontFamily( FlatInterFont.FAMILY );
Font font = new Font( FlatInterFont.FAMILY, Font.PLAIN, 12 );
~~~

Or use following:

~~~java
Font font = FontUtils.getCompositeFont( FlatInterFont.FAMILY, Font.PLAIN, 12 );
~~~


Download
--------

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-inter
    version:     (see button below)

Otherwise, download `flatlaf-fonts-inter-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-inter/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-inter)
