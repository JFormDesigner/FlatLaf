FlatLaf Change Log
==================

## 1.0-rc3-SNAPSHOT

#### Fixed bugs

- Label and ToolTip: Fixed font sizes for `<code>`, `<kbd>`, `<big>`, `<small>`
  and `<samp>` tags in HTML text.


## 1.0-rc2

#### New features and improvements

- Button:
  - In "Flat Light" theme, use a slightly thinner border for focused buttons
    (because they already have light blue background).
  - In "Flat Dark" theme, use slightly wider border for focused buttons.
- CheckBox and RadioButton: In "Flat Dark" theme, use blueish background for
  focused components.
- Tree: Support disabling wide selection per component. (set client property
  `JTree.wideSelection` to `false`). (PR #245)
- Tree: Support disabling selection painting per component. Then the tree cell
  renderer is responsible for selection painting. (set client property
  `JTree.paintSelection` to `false`).
- JIDE Common Layer: Support `JidePopupMenu`.

#### Fixed bugs

- Button: Fixed behavior of <kbd>Enter</kbd> key on focused button on Windows
  and Linux, which now clicks the focused button (instead of the default
  button).
  - On Windows, this is a regression in 1.0-rc1.
  - On macOS, the <kbd>Enter</kbd> key always clicks the default button, which
    is the platform behavior.
  - On all platforms, the default button can be always clicked with
    <kbd>Ctrl+Enter</kbd> keys, even if another button is focused.
- CheckBox and RadioButton: Fill component background as soon as background
  color is different to default background color, even if component is not
  opaque (which is the default). This paints selection if using the component as
  cell renderer a Table, Tree or List.
- TextComponents: Border of focused non-editable text components had wrong
  color.
- Custom window decorations: Fixed top window border in dark themes when running
  in JetBrains Runtime.


## 1.0-rc1

#### New features and improvements

- Button: Disabled `Button.defaultButtonFollowsFocus` on Windows (as on other
  platforms). If you like to keep the old behavior in your application, use:
  `if(SystemInfo.isWindows)
  UIManager.put("Button.defaultButtonFollowsFocus",true);`.
- ComboBox, Spinner and SplitPaneDivider: Added pressed feedback to arrow
  buttons.
- Slider: Support per component custom thumb and track colors via
  `JSlider.setForeground(Color)` and `JSlider.setBackground(Color)`.
- Slider: Improved thumb hover and pressed colors.
- TextComponent: Clip placeholder text if it does not fit into visible area. (PR
  #229)
- macOS: Improved font rendering on macOS when using JetBrains Runtime. (PRs
  #237, #239 and #241)
- Extras: UI defaults inspector:
  - Support embedding UI defaults inspector panel into any window. See
    `FlatUIDefaultsInspector.createInspectorPanel()`.
  - Copy selected keys and values into clipboard via context menu.
  - Support wildcard matching in filter (`*` matches any number of characters,
    `?` matches a single character, `^` beginning of line, `$` end of line).
- IntelliJ Themes:
  - Added hover and pressed feedback to Button, CheckBox, RadioButton and
    ToggleButton. (issue #176)
  - Added "Material Theme UI Lite / Moonlight" theme.
  - Updated "Dracula", "Gradianto" and "Material Theme UI Lite" themes.

#### Fixed bugs

- Button and ToggleButton: Threat Unicode surrogate character pair as single
  character and make button square. (issue #234)
- Button and ToggleButton: ToolBar buttons now respect explicitly set background
  color. If no background color is set, then the button background is not
  painted anymore. (issue #191)
- ToggleButton: Tab style buttons (client property `JButton.buttonType` is
  `tab`) now respect explicitly set background color.
- TabbedPane: Fixed `IndexOutOfBoundsException` when using tooltip text on close
  buttons and closing last/rightmost tab. (issue #235)
- TabbedPane: Fixed scrolling tabs with touchpads and high-resolution mouse
  wheels.
- Extras: Added missing export of package
  `com.formdev.flatlaf.extras.components` to Java 9 module descriptor.
- JIDE Common Layer:
  - Invoke `LookAndFeelFactory.installJideExtension()` when using FlatLaf UI
    delegates. (issue #230)
  - RangeSlider: Fixed slider focused colors in IntelliJ themes.
- IntelliJ Themes:
  - Fixed menu item check colors.
  - Fixed `MenuItem.underlineSelectionColor`.
  - Fixed List, Tree and Table `selectionInactiveForeground` in light Arc
    themes.
  - Fixed List and Table background colors in Material UI Lite themes.
  - Fixed menu accelerator colors in Monocai theme. (issue #243)


## 0.46

#### New features and improvements

- Slider and JIDE RangeSlider: Clicking on track now immediately moves the thumb
  to mouse location and starts dragging the thumb. Use `UIManager.put(
  "Slider.scrollOnTrackClick", true )` to enable old behavior that scrolls the
  thumb when clicking on track.
- Slider: Snap to ticks is now done while dragging the thumb. Use
  `UIManager.put( "Slider.snapToTicksOnReleased", true )` to enable old behavior
  that snaps to ticks on mouse released.
- Extras: Added standard component extension classes that provides easy access
  to FlatLaf specific client properties (see package
  `com.formdev.flatlaf.extras.components`).
- Extras: Renamed tri-state check box class from
  `com.formdev.flatlaf.extras.TriStateCheckBox` to
  `com.formdev.flatlaf.extras.components.FlatTriStateCheckBox`. Also
  changed/improved API and added javadoc.
- Extras: Renamed SVG utility class from `com.formdev.flatlaf.extras.SVGUtils`
  to `com.formdev.flatlaf.extras.FlatSVGUtils`.
- IntelliJ Themes: Added flag whether a theme is dark to
  `FlatAllIJThemes.INFOS`. (issue #221)
- JIDE Common Layer: Support `TristateCheckBox`.


#### Fixed bugs

- Slider: Fixed painting of colored track if `JSlider.inverted` is `true`.
- Table and TableHeader: Fixed missing right vertical grid line if using table
  as row header in scroll pane. (issues #152 and #46)
- TableHeader: Fixed position of column separators in right-to-left component
  orientation.
- ToolTip: Fixed drop shadow for wide tooltips on Windows and Java 9+. (issue
  #224)
- SwingX: Fixed striping background highlighting color (e.g. alternating table
  rows) in dark themes.
- Fixed: If text antialiasing is disabled (in OS system settings or via
  `-Dawt.useSystemAAFontSettings=off`), then some components still did use
  antialiasing to render text (not-editable ComboBox, ProgressBar, Slider,
  TabbedPane and multiline ToolTip). (issue #227)


## 0.45

#### New features and improvements

- Slider: New design, added hover and pressed feedback and improved customizing.
  (PR #214)
- JIDE Common Layer: Support `RangeSlider`. (PR #209)
- IntelliJ Themes:
  - Added "Gradianto Nature Green" theme.
  - Updated "Arc Dark", "Cyan", "Dark purple", "Gradianto", "Gray", "Gruvbox"
    and "One Dark" themes.
- TabbedPane: Support hiding tab area if it contains only one tab. (set client
  property `JTabbedPane.hideTabAreaWithOneTab` to `true`)
- MenuBar: Support different underline menu selection style UI defaults for
  `MenuBar` and `MenuItem`. (PR #217; issue #216)


#### Fixed bugs

- Table: Do not paint last vertical grid line if auto-resize mode is not off.
  (issue #46)
- Table: Fixed unstable grid line thickness when scaled on HiDPI screens. (issue
  #152)
- TabbedPane: No longer add (internal) tab close button component as child to
  `JTabbedPane`. (issue #219)
- Custom window decorations: Title bar was not hidden if window is in
  full-screen mode. (issue #212)


## 0.44

#### New features and improvements

- TabbedPane: In scroll tab layout, added "Show Hidden Tabs" button to trailing
  side of tab area. If pressed, it shows a popup menu that contains (partly)
  hidden tabs and selecting one activates that tab. (PR #190; issue #40)
- TabbedPane: Support forward/backward scroll arrow buttons on both sides of tab
  area. Backward button on left side, forward button on right side. Not
  applicable scroll buttons are hidden. (PR #211; issue #40)
- TabbedPane: Support specifying default tab layout policy for all tabbed panes
  in the application via UI value `TabbedPane.tabLayoutPolicy`. E.g. invoke
  `UIManager.put( "TabbedPane.tabLayoutPolicy", "scroll" );` to use scroll
  layout.
- TabbedPane: Support tab scrolling with mouse wheel (in scroll tab layout). (PR
  #187; issue #40)
- TabbedPane: Repeat scrolling as long as scroll arrow buttons are pressed. (PR
  #187; issue #40)
- TabbedPane: Support adding custom components to left and right sides of tab
  area. (set client property `JTabbedPane.leadingComponent` or
  `JTabbedPane.trailingComponent` to a `java.awt.Component`) (PR #192; issue
  #40)
- TabbedPane: Support closable tabs. (PR #193; issues #31 and #40)
- TabbedPane: Support minimum or maximum tab widths. (set client property
  `JTabbedPane.minimumTabWidth` or `JTabbedPane.maximumTabWidth` to an integer)
  (PR #199)
- TabbedPane: Support alignment of tab area. (set client property
  `JTabbedPane.tabAreaAlignment` to `"leading"`, `"trailing"`, `"center"` or
  `"fill"`) (PR #199)
- TabbedPane: Support horizontal alignment of tab title and icon. (set client
  property `JTabbedPane.tabAlignment` to `SwingConstants.LEADING`,
  `SwingConstants.TRAILING` or `SwingConstants.CENTER`)
- TabbedPane: Support equal and compact tab width modes. (set client property
  `JTabbedPane.tabWidthMode` to `"preferred"`, `"equal"` or `"compact"`) (PR
  #199)
- TabbedPane: Support left, right, top and bottom tab icon placement. (set
  client property `JTabbedPane.tabIconPlacement` to `SwingConstants.LEADING`,
  `SwingConstants.TRAILING`, `SwingConstants.TOP` or `SwingConstants.BOTTOM`)
  (PR #199)
- Support painting separator line between window title and content (use UI value
  `TitlePane.borderColor`). (issue #184)
- Extras: `FlatSVGIcon` now allows specifying icon width and height in
  constructors. (issue #196)
- SplitPane: Hide not applicable expand/collapse buttons. Added tooltips to
  expand/collapse buttons. (issue #198)
- SplitPane: Added grip to divider. Can be disabled with `UIManager.put(
  "SplitPaneDivider.style", "plain" )`. (issue #179)


#### Fixed bugs

- Custom window decorations: Not visible menu bar is now ignored in layout.
- Popups using `JToolTip` components did not respect their location. (issue
  #188; regression in 0.42 in fix for #164)
- IntelliJ Themes: Added suffix "(Material)" to names of all Material UI Lite
  themes to avoid duplicate theme names. (issue #201)
- Extras: `FlatSVGIcon` icons were not painted in disabled labels and disabled
  tabs. (issue #205)


## 0.43

#### New features and improvements

- TabbedPane: Made tabs separator color lighter in dark themes so that it is
  easier to recognize the tabbed pane.
- TabbedPane: Added top and bottom tab insets to avoid that large tab icons are
  painted over active tab underline.
- TabbedPane: Support hiding separator between tabs and content area (set client
  property `JTabbedPane.showContentSeparator` to `false`).
- CheckBoxMenuItem and RadioButtonMenuItem: Improved checkmark background colors
  of selected menu items that have also an icon. This makes it is easier to
  recognize selected menu items.
- Windows: Made scaling compatible with Windows OS scaling, which distinguish
  between "screen scaling" and "text scaling". (issue #175)

#### Fixed bugs

- ComboBox: If using own `JTextField` as editor, default text field border is
  now removed to avoid duplicate border.
- ComboBox: Limit popup width to screen width for very long items. (issue #182)
- FileChooser: Fixed localizing special Windows folders (e.g. "Documents") and
  enabled hiding known file extensions (if enabled in Windows Explorer). (issue
  #178)
- Spinner: Fixed `NullPointerException` in case that arrow buttons were removed
  to create button-less spinner. (issue #181)


## 0.42

#### New features and improvements

- Demo: Improved "SplitPane & Tabs" and "Data Components" tabs.
- Demo: Menu items "File > Open" and "File > Save As" now show file choosers.
- InternalFrame: Support draggable border for resizing frame inside of the
  visible frame border. (issue #121)
- `FlatUIDefaultsInspector` added (see [FlatLaf Extras](flatlaf-extras)). A
  simple UI defaults inspector that shows a window with all UI defaults used in
  current theme (look and feel).
- Made disabled text color slightly lighter in dark themes for better
  readability. (issue #174)
- PasswordField: Support disabling Caps Lock warning icon. (issue #172)

#### Fixed bugs

- TextComponents: Fixed text color of disabled text components in dark themes.
- Custom window decorations: Fixed wrong window placement when moving window to
  another screen with different scaling factor. (issue #166)
- Custom window decorations: Fixed wrong window bounds when resizing window to
  another screen with different scaling factor. (issue #166)
- Fixed occasional wrong positioning of heavy weight popups when using multiple
  screens with different scaling factors. (issue #166)
- ToolTip: Avoid that tooltip hides owner component. (issue #164)


## 0.41

#### New features and improvements

- Added API to register packages or folders where FlatLaf searches for
  application specific properties files with custom UI defaults (see
  `FlatLaf.registerCustomDefaultsSource(...)` methods).
- Demo: Show hint popups to guide users to some features of the FlatLaf Demo
  application.
- Extras: `FlatSVGIcon` now allows specifying `ClassLoader` that is used to load
  SVG file. (issue #163)
- Smoother transition from old to new theme, independent of UI complexity, when
  using animated theme change (see [FlatLaf Extras](flatlaf-extras)).

#### Fixed bugs

- Button: "selected" state was not shown. (issue #161)
- TextArea: Update background color property if enabled or editable state
  changes in the same way as Swing does it for all other text components. (issue
  #147)
- Demo: Fixed restoring last used theme on startup. (regression in 0.39)
- Custom window decorations: Fixed iconify, maximize and close icon colors if
  window is inactive.
- Custom window decorations: Fixed title pane background color in IntelliJ
  themes if window is inactive.
- Fixed sub-pixel text rendering in animated theme change (see
  [FlatLaf Extras](flatlaf-extras)).

#### Other Changes

- Extras: Updated dependency
  [svgSalamander](https://github.com/JFormDesigner/svgSalamander) to version
  1.1.2.3.


## 0.40

#### New features

- Table: Detect whether component is used in cell editor and automatically
  disable round border style and reduce cell editor outer border width (used for
  focus indicator) to zero. (issue #148)
- ComboBox, Spinner and TextField: Support disabling round border style per
  component, if globally enabled (set client property `JComponent.roundRect` to
  `false`). (issue #148)

#### Fixed bugs

- Custom window decorations: Embedded menu bar did not always respond to mouse
  events after adding menus and when running in JetBrains Runtime. (issue #151)
- IntelliJ Themes: Fixed NPE in Solarized themes on scroll bar hover.


## 0.39

#### New features

- Animated theme change (see [FlatLaf Extras](flatlaf-extras)). Used in Demo.
- Demo: Added combo box above themes list to show only light or dark themes.
- IntelliJ Themes:
  - Added "Arc Dark", "Arc Dark - Orange", "Carbon" and "Cobalt 2" themes.
  - Replaced "Solarized" themes with much better ones from 4lex4.
  - Updated "Arc", "One Dark" and "Vuesion" themes.
- ScrollPane: Enable/disable smooth scrolling per component if client property
  "JScrollPane.smoothScrolling" is set to a `Boolean` on `JScrollPane`.
- ScrollBar: Increased minimum thumb size on macOS and Linux from 8 to 18
  pixels. On Windows, it is now 10 pixels. (issue #131)
- Button: Support specifying button border width.
- ComboBox: Changed maximum row count of popup list to 15 (was 20). Set UI value
  `ComboBox.maximumRowCount` to any integer to use a different value.

#### Fixed bugs

- Custom window decorations: Fixed maximized window bounds when programmatically
  maximizing window. E.g. restoring window state at startup. (issue #129)
- InternalFrame: Title pane height was too small when iconify, maximize and
  close buttons are hidden. (issue #132)
- ToolTip: Do not show empty tooltip component if tooltip text is an empty
  string. (issue #134)
- ToolTip: Fixed truncated text in HTML formatted tooltip on HiDPI displays.
  (issue #142)
- ComboBox: Fixed width of popup, which was too small if popup is wider than
  combo box and vertical scroll bar is visible. (issue #137)
- MenuItem on macOS: Removed plus characters from accelerator text and made
  modifier key order conform with macOS standard. (issue #141)
- FileChooser: Fixed too small text field when renaming a file/directory in Flat
  IntelliJ/Darcula themes. (issue #143)
- IntelliJ Themes: Fixed text colors in ProgressBar. (issue #138)


## 0.38

- Hide focus indicator when window is inactive.
- Custom window decorations: Improved/fixed window border color in dark themes.
- Custom window decorations: Hide window border if window is maximized.
- Custom window decorations: Center title if menu bar is embedded.
- Custom window decorations: Cursor of components (e.g. TextField) was not
  changed. (issue #125)
- CheckBox: Fixed colors in light IntelliJ themes. (issue #126; regression in
  0.37)
- InternalFrame: Use default icon in internal frames. (issue #122)


## 0.37

- Custom window decorations (Windows 10 only; PR #108; issues #47 and #82)
  support:
  - dark window title panes
  - embedding menu bar into window title pane
  - native Windows 10 borders and behavior when running in
    [JetBrains Runtime 11](https://confluence.jetbrains.com/display/JBR/JetBrains+Runtime)
    or later (the JRE that IntelliJ IDEA uses)
- CheckBox and RadioButton: Support changing selected icon style from outline to
  filled (as in FlatLaf IntelliJ theme) with `UIManager.put(
  "CheckBox.icon.style", "filled" );`.
- Button and ToggleButton: Support disabled background color (use UI values
  `Button.disabledBackground` and `ToggleButton.disabledBackground`). (issue
  #112)
- Button and ToggleButton: Support making buttons square (set client property
  `JButton.squareSize` to `true`). (issue #118)
- ScrollBar: Support pressed track, thumb and button colors (use UI values
  `ScrollBar.pressedTrackColor`, `ScrollBar.pressedThumbColor` and
  `ScrollBar.pressedButtonBackground`). (issue #115)
- ComboBox: Support changing arrow button style (set UI value
  `ComboBox.buttonStyle` to `auto` (default), `button` or `none`). (issue #114)
- Spinner: Support changing arrows button style (set UI value
  `Spinner.buttonStyle` to `button` (default) or `none`).
- TableHeader: Support top/bottom/left positioned sort arrow when using
  [Glazed Lists](https://github.com/glazedlists/glazedlists). (issue #113)
- Button, CheckBox, RadioButton and ToggleButton: Do not paint focus indicator
  if `AbstractButton.isFocusPainted()` returns `false`.
- ComboBox: Increase maximum row count of popup list to 20 (was 8). Set UI value
  `ComboBox.maximumRowCount` to any integer to use a different value.
- Fixed/improved vertical position of text when scaled on HiDPI screens on
  Windows.
- IntelliJ Themes: Updated Gradianto Themes.
- IntelliJ Themes: Fixed menu bar and menu item margins in all Material UI Lite
  themes.


## 0.36

- ScrollBar: Made styling more flexible by supporting insets and arc for track
  and thumb. (issue #103)
- ScrollBar: Use round thumb on macOS and Linux to make it look similar to
  native platform scroll bars. (issue #103)
- ComboBox: Minimum width is now 72 pixels (was ~50 for non-editable and ~130
  for editable comboboxes).
- ComboBox: Support custom borders in combobox editors. (issue #102)
- Button: Support non-square icon-only buttons. (issue #110)
- Ubuntu Linux: Fixed poorly rendered font. (issue #105)
- macOS Catalina: Use Helvetica Neue font.
- `FlatInspector` added (see [FlatLaf Extras](flatlaf-extras)).


## 0.35

- Added drop shadows to popup menus, combobox popups, tooltips and internal
  frames. (issue #94)
- Support different component border colors to indicate errors, warnings or
  custom state (set client property `JComponent.outline` to `error`, `warning`
  or any `java.awt.Color`).
- Button and ToggleButton: Support round button style (set client property
  `JButton.buttonType` to `roundRect`).
- ComboBox, Spinner and TextField: Support round border style (set client
  property `JComponent.roundRect` to `true`).
- Paint nicely rounded buttons, comboboxes, spinners and text fields when
  setting `Button.arc`, `Component.arc` or `TextComponent.arc` to a large value
  (e.g. 1000).
- Added Java 9 module descriptor to `flatlaf-extras-<version>.jar` and
  `flatlaf-swingx-<version>.jar`.
- CheckBox and RadioButton: Flag `opaque` is no longer ignored when checkbox or
  radio button is used as table cell renderer. (issue #77)
- FileChooser: Use system icons. (issue #100)
- FileChooser: Fixed missing labels in file chooser when running on Java 9 or
  later. (issue #98)
- PasswordField: Do not apply minimum width if `columns` property is greater
  than zero.


## 0.34

- Menus: New menu item renderer brings stable left margins, right aligned
  accelerators and larger gap between text and accelerator. This makes menus
  look more modern and more similar to native platform menus.
- New underline menu selection style that displays selected menu items similar
  to tabs (to enable use `UIManager.put( "MenuItem.selectionType", "underline"
  );`).
- Menus: Fixed text color of selected menu items that use HTML. (issue #87)
- Menus: On Windows, pressing <kbd>F10</kbd> now activates the menu bar without
  showing a menu popup (as usual on Windows platform). On other platforms the
  first menu popup is shown.
- Menus: On Windows, releasing <kbd>Alt</kbd> key now activates the menu bar (as
  usual on Windows platform). (issue #43)
- Menus: Fixed inconsistent left padding in menu items. (issue #3)
- Menus: Fixed: Setting `iconTextGap` property on a menu item did increase left
  and right margins. (issue #54)
- Hide mnemonics if window is deactivated (e.g. <kbd>Alt+Tab</kbd> to another
  window). (issue #43)
- macOS: Enabled drop shadows for popup menus and combobox popups. (issue #94)
- macOS: Fixed NPE if using `JMenuBar` in `JInternalFrame` and macOS screen menu
  bar is enabled (with `-Dapple.laf.useScreenMenuBar=true`). (issue #90)


## 0.33

- Improved creation of disabled grayscale icons used in disabled buttons, labels
  and tabs. They now have more contrast and are lighter in light themes and
  darker in dark themes. (issue #70)
- IntelliJ Themes: Fixed ComboBox size and Spinner border in all Material UI
  Lite themes and limit tree row height in all Material UI Lite themes and some
  other themes.
- IntelliJ Themes: Material UI Lite themes did not work when using
  [IntelliJ Themes Pack](flatlaf-intellij-themes) addon. (PR #88, issue #89)
- IntelliJ Themes: Added Java 9 module descriptor to
  `flatlaf-intellij-themes-<version>.jar`.


## 0.32

- New [IntelliJ Themes Pack](flatlaf-intellij-themes) addon bundles many popular
  open-source 3rd party themes from JetBrains Plugins Repository into a JAR and
  provides Java classes to use them.
- IntelliJ Themes: Fixed button and toggle button colors. (issue #86)
- Updated IntelliJ Themes in demo to the latest versions.
- ToggleButton: Compute selected background color based on current component
  background. (issue #32)


## 0.31

- Focus indication border (or background) no longer hidden when temporary
  loosing focus (e.g. showing a popup menu).
- List, Table and Tree: Item selection color of focused components no longer
  change from blue to gray when temporary loosing focus (e.g. showing a popup
  menu).


## 0.30

- Windows: Fixed rendering of Unicode characters. Previously not all Unicode
  characters were rendered on Windows. (issue #81)


## 0.29

- Linux: Fixed scaling if `GDK_SCALE` environment variable is set or if running
  on JetBrains Runtime. (issue #69)
- Tree: Fixed repainting wide selection on focus gained/lost.
- ComboBox: No longer ignore `JComboBox.prototypeDisplayValue` when computing
  popup width. (issue #80)
- Support changing default font used for all components with automatic scaling
  UI if using larger font. Use `UIManager.put( "defaultFont", myFont );`
- No longer use system property `sun.java2d.uiScale`. (Java 8 only)
- Support specifying custom scale factor in system property `flatlaf.uiScale`
  also for Java 9 and later.
- Demo: Support using own FlatLaf themes (`.properties` files) that are located
  in working directory of Demo application. Shown in the "Themes" list under
  category "Current Directory".


## 0.28

- PasswordField: Warn about enabled Caps Lock.
- TabbedPane: Support <kbd>Ctrl+TAB</kbd> / <kbd>Ctrl+Shift+TAB</kbd> to switch
  to next / previous tab.
- TextField, FormattedTextField and PasswordField: Support round borders (see UI
  default value `TextComponent.arc`). (issue #65)
- IntelliJ Themes: Added Gradianto themes to demo.
- Button, CheckBox and RadioButton: Fixed NPE when button has children. (PR #68)
- ScrollBar: Improved colors.
- Reviewed (and tested) all key bindings on Windows and macOS. Linux key
  bindings are equal to Windows key bindings. macOS key bindings are slightly
  different for platform specific behavior.
- UI default values are no longer based on Metal/Aqua UI defaults.


## 0.27

- Support `JInternalFrame` and `JDesktopPane`. (issues #39 and #11)
- Table: Support positioning the column sort arrow in header right, left, top or
  bottom. (issue #34)
- ProgressBar: Fixed visual artifacts in indeterminate mode, on HiDPI screens at
  125%, 150% and 175% scaling, when the progress moves around.
- TabbedPane: New option to allow tab separators to take full height (to enable
  use `UIManager.put( "TabbedPane.tabSeparatorsFullHeight", true );`). (issue
  #59, PR #62)
- CheckBox and RadioButton: Do not fill background if `contentAreaFilled` is
  `false`. (issue #58, PR #63)
- ToggleButton: Make toggle button square if it has an icon but no text or text
  is "..." or a single character.
- ToolBar: No longer use special rollover border for buttons in toolbar. (issue
  #36)
- ToolBar: Added empty space around buttons in toolbar and toolbar itself (see
  UI default values `Button.toolbar.spacingInsets` and `ToolBar.borderMargins`).
  (issue #56)
- Fixed "illegal reflective access operation" warning on macOS when using Java
  12 or later. (issue #60, PR #61)


## 0.26

- Menus:
  - Changed menu bar and popup menu background colors (made brighter in light
    themes and darker in dark themes).
  - Highlight items in menu bar on mouse hover. (issue #49)
  - Popup menus now have empty space at the top and bottom.
  - Menu items now have larger left and right margins.
  - Made `JMenu`, `JMenuItem`, `JCheckBoxMenuItem` and `JRadioButtonMenuItem`
    non-opaque.
- TextField, FormattedTextField and PasswordField: Select all text when a text
  field gains focus for the first time and selection was not set explicitly.
  This can be configured to newer or always select all text on focus gain (see
  UI default value `TextComponent.selectAllOnFocusPolicy`).
- ProgressBar: Made progress bar paint smooth in indeterminate mode.


## 0.25.1

Re-release of 0.25 because of problems with Maven Central.


## 0.25

- Hide menu mnemonics by default and show them only when <kbd>Alt</kbd> key is
  pressed. (issue #43)
- Menu: Fixed vertical alignment of sub-menus. (issue #42)
- TabbedPane: In scroll-tab-layout, the cropped line is now hidden. (issue #40)
- Tree: UI default value `Tree.textBackground` now has a valid color and is no
  longer `null`.
- Tree on macOS: Fixed <kbd>Left</kbd> and <kbd>Right</kbd> keys to collapse or
  expand nodes.
- ComboBox on macOS: Fixed keyboard navigation and show/hide popup.
- Button and ToggleButton: Support per component minimum height (set client
  property `JComponent.minimumHeight` to an integer). (issue #44)
- Button and ToggleButton: Do not apply minimum width if button border was
  changed (is no longer an instance of `FlatButtonBorder`).
- ToggleButton: Renamed toggle button type "underline" to "tab" (value of client
  property `JButton.buttonType` is now `tab`).
- ToggleButton: Support per component styling for tab-style toggle buttons with
  client properties `JToggleButton.tab.underlineHeight` (integer),
  `JToggleButton.tab.underlineColor` (Color) and
  `JToggleButton.tab.selectedBackground` (Color). (issue #45)
- ToggleButton: No longer use focus width for tab-style toggle buttons to
  compute component size, which reduces/fixes component size in "Flat IntelliJ"
  and "Flat Darcula" themes.
- TabbedPane: Support per component tab height (set client property
  `JTabbedPane.tabHeight` to an integer).
- ProgressBar: Support square painting (set client property
  `JProgressBar.square` to `true`) and larger height even if no string is
  painted (set client property `JProgressBar.largeHeight` to `true`).


## 0.24

- Support smooth scrolling with touchpads and high precision mouse wheels.
  (issue #27)
- Changed `.properties` file loading order: Now all core `.properties` files are
  loaded before loading addon `.properties` files. This makes it easier to
  overwrite core values in addons. Also, addon loading order can be specified.
- TableHeader: Paint column borders if renderer has changed, but delegates to
  the system default renderer (e.g. done in NetBeans).
- Label and ToolTip: Fixed font sizes for HTML headings.
- Button and ToggleButton: Support square button style (set client property
  `JButton.buttonType` to `square`).
- ToggleButton: Support underline toggle button style (set client property
  `JButton.buttonType` to `underline`).
- Button and TextComponent: Support per component minimum width (set client
  property `JComponent.minimumWidth` to an integer).
- ScrollPane with Table: The border of buttons that are added to one of the four
  scroll pane corners are now removed if the center component is a table. Also,
  these corner buttons are made not focusable.
- Table: Replaced `Table.showGrid` with `Table.showHorizontalLines` and
  `Table.showVerticalLines`. (issue #38)
- ProgressBar: Now uses blueish color for the progress part in "Flat Dark"
  theme. In the "Flat Darcula" theme, it remains light gray.
- Improved Swing system colors `controlHighlight`, `controlLtHighlight`,
  `controlShadow` and `controlDkShadow`.


## 0.23.1

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
