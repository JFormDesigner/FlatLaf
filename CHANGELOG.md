FlatLaf Change Log
==================

## 0.20

- Support using IntelliJ platform themes (.theme.json files).
- Support `JFileChooser`. (issue #5)
- Look and feel identifier returned by `FlatLaf.getID()` now always starts with
  "FlatLaf". Use `UIManager.getLookAndFeel().getID().startsWith( "FlatLaf" )` to
  check whether the current look and feel is FlatLaf.
- Fixed selection background of checkbox in table cell.
- Fixed color of links in HTML text.
- Fixed jittery submenu rendering on Mac. (issue #10)
- Fixed "cannot find symbol" error in NetBeans editor, when source/binary format
  is set to JDK 9 (or later) in NetBeans project. (issue #13)
- Button: Make button square if button text is "..." or a single character.
- ComboBox: Fixed issues with NetBeans `org.openide.awt.ColorComboBox`
  component.
- Hex color values in `.properties` files now must start with a `#` character.
- SwingX: Support `JXTitledPanel`. (issue #22)
- SwingX: Fixed too wide border when using date picker as table cell editor.
  (issue #24)
- JIDE Common Layer: Fixed `JidePopup` border.


## 0.18

- TextField and TextArea: Do not apply minimum width if `columns` property is
  greater than zero.
- TabbedPane: In scroll-tab-layout, the separator line now spans the whole width
  and is no longer interrupted by the scroll buttons.
- TabbedPane: Content pane is no longer opaque. Use antialiasing for painting
  separator and content border.
- ToolTip: Use anti-aliasing to render multi-line tooltips.
- JIDE Common Layer: Support `JideTabbedPane`.


## 0.17

- CheckBox: Support painting a third state (set client property
  "JButton.selectedState" to "indeterminate").
- `TriStateCheckBox` component added (see [FlatLaf Extras](flatlaf-extras)).
- Made `JComboBox`, `JProgressBar`, `JSpinner` and `JXDatePicker` non-opaque.
  `JPasswordField`, `JScrollPane` and `JTextField` are non-opaque if they have
  an outside focus border (e.g. IntelliJ and Darcula themes). (issues #20 and
  #17)
- Button: Hover and pressed background colors are now derived from actual button
  background color. (issue #21)
- Table: Fixed missing upper right corner (e.g. in SwingX JXTable with column
  control visible).


## 0.16

- Made some fixes for right-to-left support in ComboBox, Slider and ToolTip.
  (issue #18)
- Fixed Java 9 module descriptor (broken since 0.14).
- Made `JButton`, `JCheckBox`, `JRadioButton`, `JToggleButton` and `JSlider`
  non-opaque. (issue #20)


## 0.15

- ToolTip: Improved styling of dark tooltips (darker background, no border).
- ToolTip: Fixed colors in tooltips of disabled components. (issue #15)
- ComboBox: Fixed NPE in combobox with custom renderer after switching to
  FlatLaf. (issue #16; regression in 0.14)


## 0.14

- ComboBox: Use small border if used as table editor.
- ToolBar: Disable focusability of buttons in toolbar.
- OptionPane: Fixed rendering of longer HTML text. (issue #12)
- EditorPane and TextPane: Fixed font and text color when using HTML content.
  (issue #9)
- ComboBox: Fixed `StackOverflowError` when switching LaF. (issue #14)
- SwingX: Support `JXBusyLabel`, `JXDatePicker`, `JXHeader`, `JXHyperlink`,
  `JXMonthView`, `JXTaskPaneContainer` and `JXTaskPane`. (issue #8)


## 0.13

- Added developer information to Maven POM for Maven Central publishing.


## 0.12

- Support Linux. (issue #2)
- Added `Flat*Laf.install()` methods.
- macOS: Use native screen menu bar if system property
  `apple.laf.useScreenMenuBar` is `true`.
- Windows: Update fonts (and scaling) when user changes Windows text size
  (Settings > Ease of Access > Display > Make text bigger).


## 0.11

- Changed Maven groupId to `com.formdev` and artifactId to `flatlaf`.


## 0.10

- Use new chevron arrows in "Flat Light" and "Flat Dark" themes, but keep
  triangle arrows in "Flat IntelliJ" and "Flat Darcula" themes. (issue #7)
- Use bold font for default buttons in "Flat IntelliJ" and "Flat Darcula"
  themes.
- Hide label, button and tab mnemonics by default and show them only when
  <kbd>Alt</kbd> is pressed. (issue #4)
- If a JButton has an icon and no text, then it does not get a minimum width
  (usually 72 pixel) and the left and right insets are same as top/bottom insets
  so that it becomes square (if the icon is square).
- Changed styling of default button in "Flat Light" theme (wide blue border
  instead of blue background).
- Added Java 9 module descriptor `module-info.class` to `flatlaf.jar` (in
  `META-INF/versions/9`). But FlatLaf remains Java 8 compatible. (issue #1)
- Support specifying custom scale factor in system properties `flatlaf.uiScale`
  or `sun.java2d.uiScale`. E.g. `-Dflatlaf.uiScale=1.5`. (Java 8 only)


## 0.9

- Initial release
