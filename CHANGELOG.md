FlatLaf Change Log
==================

## Unreleased

- Use new chevron arrows in "Flat Light" and "Flat Dark" themes, but keep
  triangle arrows in "Flat IntelliJ" and "Flat Darcula" themes. (issue #7)
- Use bold font for default buttons in "Flat IntelliJ" and "Flat Darcula"
  themes.
- Hide label and button mnemonics on Mac. (issue #4)
- If a JButton has an icon and no text, then it does not get a minimum width
  (usually 72 pixel) and the left and right insets are same as top/bottom insets
  so that it becomes square (if the icon is square).
- Added Java 9 module descriptor `module-info.class` to `flatlaf.jar` (in
  `META-INF/versions/9`). But FlatLaf remains Java 8 compatible. (issue #1)
- Support specifying custom scale factor in system properties `flatlaf.uiScale`
  or `sun.java2d.uiScale`. E.g. `-Dflatlaf.uiScale=1.5`. (Java 8 only)


## 0.9

- Initial release
