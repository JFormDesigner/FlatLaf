JetBrains Mono font
===================

This sub-project contains fonts from the JetBrains Mono font family and bundles
them into an easy-to-use and redistributable JAR.

Font home page: https://www.jetbrains.com/mono

GitHub project: https://github.com/JetBrains/JetBrainsMono

License:
[SIL OPEN FONT LICENSE Version 1.1](src/main/resources/com/formdev/flatlaf/fonts/jetbrains_mono/OFL.txt)


How to install?
---------------

Invoke following once (e.g. in your `main()` method; on AWT thread).

For lazy loading use:

~~~java
FlatJetBrainsMonoFont.installLazy();
~~~

Or load immediately with:

~~~java
FlatJetBrainsMonoFont.install();
~~~


How to use?
-----------

Use as application monospaced font (invoke before setting up FlatLaf):

~~~java
FlatLaf.setPreferredMonospacedFontFamily( FlatJetBrainsMonoFont.FAMILY );
~~~

Create single fonts:

~~~java
// basic styles
new Font( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
new Font( FlatJetBrainsMonoFont.FAMILY, Font.ITALIC, 12 );
new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD, 12 );
new Font( FlatJetBrainsMonoFont.FAMILY, Font.BOLD | Font.ITALIC, 12 );
~~~

If using lazy loading, invoke one of following before creating the font:

~~~java
FontUtils.loadFontFamily( FlatJetBrainsMonoFont.FAMILY );
~~~

E.g.:

~~~java
FontUtils.loadFontFamily( FlatJetBrainsMonoFont.FAMILY );
Font font = new Font( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
~~~

Or use following:

~~~java
Font font = FontUtils.getCompositeFont( FlatJetBrainsMonoFont.FAMILY, Font.PLAIN, 12 );
~~~


Download
--------

FlatLaf Fonts binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-fonts-jetbrains-mono
    version:     (see button below)

Otherwise, download `flatlaf-fonts-jetbrains-mono-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-jetbrains-mono/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-fonts-jetbrains-mono)
