FlatLaf Extras
==============

This sub-project provides some additional components and classes:

- [FlatSVGIcon](https://www.javadoc.io/doc/com.formdev/flatlaf-extras/latest/com/formdev/flatlaf/extras/FlatSVGIcon.html):
  An icon that displays SVG using
  [svgSalamander](https://github.com/JFormDesigner/svgSalamander).\
  ![FlatSVGIcon.png](../images/extras-FlatSVGIcon.png)
- [FlatTriStateCheckBox](https://www.javadoc.io/doc/com.formdev/flatlaf-extras/latest/com/formdev/flatlaf/extras/components/FlatTriStateCheckBox.html):
  A tri-state check box.\
  ![TriStateCheckBox.png](../images/extras-TriStateCheckBox.png)
- [FlatAnimatedLafChange](https://www.javadoc.io/doc/com.formdev/flatlaf-extras/latest/com/formdev/flatlaf/extras/FlatAnimatedLafChange.html):
  Animated Laf (theme) changing.
- [FlatInspector](#ui-inspector): A simple UI inspector that shows information
  about UI component at mouse location in a tooltip.
- [FlatUIDefaultsInspector](#ui-defaults-inspector): A simple UI defaults
  inspector that shows a window with all UI defaults used in current theme (look
  and feel).


Download
--------

FlatLaf Extras binaries are available on **JCenter** and **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-extras
    version:     (see button below)

Otherwise download `flatlaf-extras-<version>.jar` here:

[![Download](https://api.bintray.com/packages/jformdesigner/flatlaf/flatlaf-extras/images/download.svg)](https://bintray.com/jformdesigner/flatlaf/flatlaf-extras/_latestVersion)

You also need `flatlaf-<version>.jar` and `svgSalamander-<version>.jar`, which
you can download here:

[![Download](https://api.bintray.com/packages/jformdesigner/flatlaf/flatlaf/images/download.svg)](https://bintray.com/jformdesigner/flatlaf/flatlaf/_latestVersion)
[![Download](https://api.bintray.com/packages/jformdesigner/svgSalamander/svgSalamander/images/download.svg)](https://bintray.com/jformdesigner/svgSalamander/svgSalamander/_latestVersion)


Tools
-----

### UI Inspector

A simple UI inspector that shows information about UI component at mouse
location in a tooltip, which may be useful while developing an application.
Should not be installed in released applications.

Once installed with following code (e.g. in method `main`), it can be activated
for the active window with the given keystroke:

~~~java
FlatInspector.install( "ctrl shift alt X" );
~~~

![UI inspector](../images/extras-FlatInspector.png)

When the UI inspector is active some additional keys are available:

- press <kbd>Esc</kbd> key to disable UI inspector
- press <kbd>Ctrl</kbd> key to increase inspection level, which shows
  information about parent of UI component at mouse location
- press <kbd>Shift</kbd> key to decrease inspection level


### UI Defaults Inspector

A simple UI defaults inspector that shows a window with all UI defaults used in
current theme (look and feel), which may be useful while developing an
application. Should be not installed in released applications.

Once installed with following code (e.g. in method `main`), it can be activated
with the given keystroke:

~~~java
FlatUIDefaultsInspector.install( "ctrl shift alt Y" );
~~~

![UI Defaults Inspector](../images/extras-FlatUIDefaultsInspector.png)
