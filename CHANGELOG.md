FlatLaf Change Log
==================

## Unreleased

- Tree: Fixed wide selection if scrolled horizontally.
- ComboBox: Fixed NPE in Oracle SQL Developer settings.
- IntelliJ Themes: Fixed checkbox colors in Material UI Lite dark themes.


## 0.23

- Updated colors in "Flat Light" and "Flat IntelliJ" themes with colors from
  "IntelliJ Light Theme", which provides blue coloring that better matches
  platform colors.
- Tree: Support wide selection (enabled by default).
- Table: Hide grid and changed intercell spacing to zero.
- List, Table and Tree: Added colors for drag-and-drop. Added "enable drag and
  drop" checkbox to Demo on "Data Components" tab.
- List and Tree: Hide cell focus indicator (black rectangle) by default. Can be
  enabled with `List.showCellFocusIndicator=true` /
  `Tree.showCellFocusIndicator=true`, but then the cell focus indicator is shown
  only if more than one item is selected.
- Table: Hide cell focus indicator (black rectangle) by default if none of the
  selected cells is editable. Can be show always with
  `Table.showCellFocusIndicator=true`.
- Support basic color functions in `.properties` files: `rgb(red,green,blue)`,
  `rgba(red,green,blue,alpha)`, `hsl(hue,saturation,lightness)`,
  `hsla(hue,saturation,lightness,alpha)`, `lighten(color,amount[,options])` and
  `darken(color,amount[,options])`.
- Replaced prefix `@@` with `$` in `.properties` files.
- Fixed link color (in HTML text) and separator color in IntelliJ platform
  themes.
- Use logging instead of printing errors to `System.err`.
- Updated IntelliJ Themes in demo to the latest versions.
- IntelliJ Themes: Fixed link and separator colors.


## 0.22

- TextComponent: Support placeholder text that is displayed if text field is
  empty (set client property "JTextField.placeholderText" to a string).
- TextComponent: Scale caret width on HiDPI screens when running on Java 8.
- ProgressBar: If progress text is visible:
  - use smaller font
  - reduced height
  - changed style to rounded rectangle
  - fixed painting issues on low values
- ProgressBar: Support configure of arc with `ProgressBar.arc`.
- ProgressBar: Reduced thickness from 6 to 4.
- TabbedPane: Support background color for selected tabs
  (`TabbedPane.selectedBackground`) and separators between tabs
  (`TabbedPane.showTabSeparators`).
- CheckBox: changed `CheckBox.arc` from radius to diameter to be consistent with
  `Button.arc` and `Component.arc`
- Button: Enabled `Button.defaultButtonFollowsFocus` on Windows, which allows
  pressing focused button with <kbd>Enter</kbd> key (as in Windows LaF).
- Fixed clipped borders at 125%, 150% and 175% scaling when outer focus width is
  zero (default in "Flat Light" and "Flat Dark" themes).
- On Mac show mnemonics only when <kbd>Ctrl</kbd> and <kbd>Alt</kbd> keys are
  pressed. (issue #4)


## 0.21

- ScrollBar: Show decrease/increase arrow buttons if client property
  "JScrollBar.showButtons" is set to `true` on `JScrollPane` or `JScrollBar`.
  (issue #25)
- `FlatLaf.isNativeLookAndFeel()` now returns `false`.
- Button: Optionally support gradient borders, gradient backgrounds and shadows
  for improved compatibility with IntelliJ platform themes (e.g. for Vuesion,
  Spacegray and Material Design Dark themes).
- Button: Fixed help button styling in IntelliJ platform themes.
- ScrollPane: Paint disabled border if view component (e.g. JTextPane) is
  disabled.
- Fixed Swing system colors in dark themes.


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
