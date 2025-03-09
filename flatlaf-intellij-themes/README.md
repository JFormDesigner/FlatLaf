FlatLaf IntelliJ Themes Pack
============================

This addon for FlatLaf bundles many popular open-source 3rd party themes from
JetBrains Plugins Repository into a JAR and provides Java classes to use them.

Use [FlatLaf Demo](https://github.com/JFormDesigner/FlatLaf#demo) to try them
out.


Download
--------

FlatLaf IntelliJ Themes Pack binaries are available on **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-intellij-themes
    version:     (see button below)

Otherwise, download `flatlaf-intellij-themes-<version>.jar` here:

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-intellij-themes/badge.svg?style=flat-square&color=007ec6)](https://maven-badges.herokuapp.com/maven-central/com.formdev/flatlaf-intellij-themes)


How to use?
-----------

Choose a theme (see list below) and invoke its `setup` method. E.g.:

~~~java
FlatArcOrangeIJTheme.setup();
~~~


Themes
------

This addon contains following themes.

Name | Class
-----|------
[Arc](https://gitlab.com/zlamalp/arc-theme-idea) | `com.formdev.flatlaf.intellijthemes.FlatArcIJTheme`
[Arc - Orange](https://gitlab.com/zlamalp/arc-theme-idea) | `com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme`
[Arc Dark](https://gitlab.com/zlamalp/arc-theme-idea) | `com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme`
[Arc Dark - Orange](https://gitlab.com/zlamalp/arc-theme-idea) | `com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme`
[Carbon](https://github.com/luisfer0793/theme-carbon) | `com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme`
[Cobalt 2](https://github.com/ngehlert/cobalt2) | `com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme`
[Cyan light](https://github.com/OlyaB/CyanTheme) | `com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme`
[Dark Flat](https://github.com/nerzhulart/DarkFlatTheme) | `com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme`
[Dark purple](https://github.com/OlyaB/DarkPurpleTheme) | `com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme`
[Dracula](https://github.com/dracula/jetbrains) | `com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme`
[Gradianto Dark Fuchsia](https://github.com/thvardhan/Gradianto) | `com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme`
[Gradianto Deep Ocean](https://github.com/thvardhan/Gradianto) | `com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme`
[Gradianto Midnight Blue](https://github.com/thvardhan/Gradianto) | `com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme`
[Gradianto Nature Green](https://github.com/thvardhan/Gradianto) | `com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme`
[Gray](https://github.com/OlyaB/GreyTheme) | `com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme`
[Gruvbox Dark Hard](https://github.com/Vincent-P/gruvbox-intellij-theme) | `com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme`
[Hiberbee Dark](https://github.com/Hiberbee/themes) | `com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme`
[High Contrast](https://github.com/JetBrains/intellij-community) | `com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme`
[Light Flat](https://github.com/nerzhulart/LightFlatTheme) | `com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme`
[Material Design Dark](https://github.com/xinkunZ/NotReallyMDTheme) | `com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme`
[Monocai](https://github.com/TheEggi/intellij-monocai-theme) | `com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme`
[Monokai Pro](https://github.com/subtheme-dev/monokai-pro) | `com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme`
[Nord](https://github.com/nordtheme/jetbrains) | `com.formdev.flatlaf.intellijthemes.FlatNordIJTheme`
[One Dark](https://github.com/one-dark/jetbrains-one-dark-theme) | `com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme`
[Solarized Dark](https://github.com/4lex4/intellij-platform-solarized) | `com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme`
[Solarized Light](https://github.com/4lex4/intellij-platform-solarized) | `com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme`
[Spacegray](https://github.com/mturlo/intellij-spacegray) | `com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme`
[Vuesion](https://github.com/vuesion/intellij-theme) | `com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme`
[Xcode-Dark](https://github.com/antelle/intellij-xcode-dark-theme) | `com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme`

Material Theme UI Lite:

Name | Class
-----|------
[Arc Dark (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTArcDarkIJTheme`
[Atom One Dark (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTAtomOneDarkIJTheme`
[Atom One Light (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTAtomOneLightIJTheme`
[Dracula (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTDraculaIJTheme`
[GitHub (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubIJTheme`
[GitHub Dark (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme`
[Light Owl (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTLightOwlIJTheme`
[Material Darker (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme`
[Material Deep Ocean (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDeepOceanIJTheme`
[Material Lighter (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialLighterIJTheme`
[Material Oceanic (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialOceanicIJTheme`
[Material Palenight (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialPalenightIJTheme`
[Monokai Pro (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMonokaiProIJTheme`
[Moonlight (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMoonlightIJTheme`
[Night Owl (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTNightOwlIJTheme`
[Solarized Dark (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTSolarizedDarkIJTheme`
[Solarized Light (Material)](https://github.com/AtomMaterialUI/material-theme-ui-lite) | `com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTSolarizedLightIJTheme`
