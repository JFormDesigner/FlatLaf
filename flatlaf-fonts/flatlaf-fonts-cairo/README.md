Cairo font
==========

This sub-project contains fonts from the Cairo font family and bundles them into
an easy-to-use and redistributable JAR.

Font home page: https://fonts.google.com/specimen/Cairo

GitHub project: https://github.com/Gue3bara/Cairo

License:
[SIL OPEN FONT LICENSE Version 1.1](src/main/resources/com/formdev/flatlaf/fonts/cairo/LICENSE.txt)


How to install?
---------------

Invoke following once (e.g. in your `main()` method; on AWT thread).

For lazy loading use:

~~~java
FlatCairoFont.installLazy();
~~~

Or load immediately with:

~~~java
FlatCairoFont.install();
// or
FlatCairoFont.installBasic();
FlatCairoFont.installLight();
FlatCairoFont.installSemiBold();
~~~


How to use?
-----------

Use as application font (invoke before setting up FlatLaf):

~~~java
FlatLaf.setPreferredFontFamily( FlatCairoFont.FAMILY );
FlatLaf.setPreferredLightFontFamily( FlatCairoFont.FAMILY_LIGHT );
FlatLaf.setPreferredSemiboldFontFamily( FlatCairoFont.FAMILY_SEMIBOLD );
~~~

Create single fonts:

~~~java
// basic styles
new Font( FlatCairoFont.FAMILY, Font.PLAIN, 12 );
new Font( FlatCairoFont.FAMILY, Font.BOLD, 12 );

// light
new Font( FlatCairoFont.FAMILY_LIGHT, Font.PLAIN, 12 );

// semibold
new Font( FlatCairoFont.FAMILY_SEMIBOLD, Font.PLAIN, 12 );
~~~

If using lazy loading, invoke one of following before creating the font:

~~~java
FontUtils.loadFontFamily( FlatCairoFont.FAMILY );
FontUtils.loadFontFamily( FlatCairoFont.FAMILY_LIGHT );
FontUtils.loadFontFamily( FlatCairoFont.FAMILY_SEMIBOLD );
~~~

E.g.:

~~~java
FontUtils.loadFontFamily( FlatCairoFont.FAMILY );
Font font = new Font( FlatCairoFont.FAMILY, Font.PLAIN, 12 );
~~~

Or use following:

~~~java
Font font = FontUtils.getCompositeFont( FlatCairoFont.FAMILY, Font.PLAIN, 12 );
~~~


Download
--------

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-cairo
    version:     (see button below)

Otherwise, download `flatlaf-fonts-cairo-<version>.jar` from Maven Central.