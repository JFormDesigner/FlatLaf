Roboto Mono font
================

This sub-project contains fonts from the Roboto Mono font family and bundles
them into an easy-to-use and redistributable JAR.

Font home page: https://fonts.google.com/specimen/Roboto+Mono

GitHub project: https://github.com/googlefonts/RobotoMono

License:
[Apache License, Version 2.0](src/main/resources/com/formdev/flatlaf/fonts/roboto_mono/LICENSE.txt)


How to install?
---------------

Invoke following once (e.g. in your `main()` method; on AWT thread).

For lazy loading use:

~~~java
FlatRobotoMonoFont.installLazy();
~~~

Or load immediately with:

~~~java
FlatRobotoMonoFont.install();
~~~


How to use?
-----------

Use as application monospaced font (invoke before setting up FlatLaf):

~~~java
FlatLaf.setPreferredMonospacedFontFamily( FlatRobotoMonoFont.FAMILY );
~~~

Create single fonts:

~~~java
// basic styles
new Font( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
new Font( FlatRobotoMonoFont.FAMILY, Font.ITALIC, 12 );
new Font( FlatRobotoMonoFont.FAMILY, Font.BOLD, 12 );
new Font( FlatRobotoMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
~~~

If using lazy loading, invoke one of following before creating the font:

~~~java
FontUtils.loadFontFamily( FlatRobotoMonoFont.FAMILY );
~~~

E.g.:

~~~java
FontUtils.loadFontFamily( FlatRobotoMonoFont.FAMILY );
Font font = new Font( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
~~~

Or use following:

~~~java
Font font = FontUtils.getCompositeFont( FlatRobotoMonoFont.FAMILY, Font.PLAIN, 12 );
~~~


Download
--------

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-roboto-mono
    version:     (see button below)

Otherwise, download `flatlaf-fonts-roboto-mono-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-roboto-mono/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-roboto-mono)
