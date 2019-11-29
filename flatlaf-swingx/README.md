FlatLaf addon for SwingX
========================

This addon for FlatLaf adds support for **some** widely used SwingX components.

Many SwingX components that do not use UI delegates (e.g. `JXButton`, `JXLabel`,
`JXList`, etc) work with FlatLaf without adaptation.

Following SwingX components, which use UI delegates, are currently supported by
this addon:

- `JXBusyLabel`
- `JXDatePicker`
- `JXHeader`
- `JXHyperlink`
- `JXMonthView`
- `JXTaskPaneContainer`
- `JXTaskPane`
- `JXTitledPanel`

![Flat Light SwingX Demo](../images/FlatLightSwingXTest.png)

![Flat Dark SwingX Demo](../images/FlatDarkSwingXTest.png)


Download
--------

FlatLaf for SwingX binaries are available on **JCenter** and **Maven Central**.

If you use Maven or Gradle, add a dependency with following coordinates to your
build script:

    groupId:     com.formdev
    artifactId:  flatlaf-swingx
    version:     0.20

Otherwise download `flatlaf-swingx-<version>.jar` here:

[![Download](https://api.bintray.com/packages/jformdesigner/flatlaf/flatlaf-swingx/images/download.svg)](https://bintray.com/jformdesigner/flatlaf/flatlaf-swingx/_latestVersion)

You also need `flatlaf-<version>.jar`, which you can download here:

[![Download](https://api.bintray.com/packages/jformdesigner/flatlaf/flatlaf/images/download.svg)](https://bintray.com/jformdesigner/flatlaf/flatlaf/_latestVersion)
