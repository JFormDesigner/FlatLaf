FlatLaf Change Log
==================

## 2.2-SNAPSHOT

#### Fixed bugs

- Native window decorations (Windows 10/11 only): Fixed wrong window title
  character encoding used in Windows taskbar. (issue #502)
- Button: Fixed icon layout and preferred width of default buttons that use bold
  font. (issue #506)
- FileChooser: Enabled full row selection for details view to fix alternate row
  coloring. (issue #512)
- SplitPane: Fixed `StackOverflowError` caused by layout loop that may occur
  under special circumstances. (issue #513)
- ToolBar: Fixed endless loop in focus navigation that may occur under special
  circumstances. (issue #505)
- IntelliJ Themes: `Component.accentColor` UI property now has useful theme
  specific values. (issue #507)


## 2.1

#### New features and improvements

- Menus: Improved usability of submenus. (PR #490; issue #247)
- Menus: Scroll large menus using mouse wheel or up/down arrows. (issue #225)
- Linux: Support using custom window decorations. Enable with
  `JFrame.setDefaultLookAndFeelDecorated(true)` and
  `JDialog.setDefaultLookAndFeelDecorated(true)` before creating a window.
  (issue #482)
- ScrollBar: Added UI value `ScrollBar.minimumButtonSize` to specify minimum
  scroll arrow button size (if shown). (issue #493)

#### Fixed bugs

- PasswordField: Fixed reveal button appearance in IntelliJ themes. (issue #494)
- ScrollBar: Center and scale arrows in scroll up/down buttons (if shown).
  (issue #493)
- TextArea, TextPane and EditorPane: No longer select all text when component is
  focused for the first time. (issue #498; regression in FlatLaf 2.0)
- TabbedPane: Disable all items in "Show Hidden Tabs" popup menu if tabbed pane
  is disabled.

#### Incompatibilities

- Method `FlatUIUtils.paintArrow()` (and class `FlatArrowButton`) now paints
  arrows one pixel smaller than before. To fix this, increase parameter
  `arrowSize` by one.


## 2.0.2

- Native window decorations (Windows 10/11 only): Fixed rendering artifacts on
  HiDPI screens when dragging window partly offscreen and back into screen
  bounds. (issue #477)
- Repaint component when setting client property `JComponent.outline` (issue
  #480).
- macOS: Fixed NPE when using some icons in main menu items. (issue #483)


## 2.0.1

- Fixed memory leak in Panel, Separator and ToolBarSeparator. (issue #471;
  regression in FlatLaf 2.0)
- ToolTip: Fixed wrong tooltip location if component overrides
  `JComponent.getToolTipLocation()` and wants place tooltip under mouse
  location. (issue #468)
- Extras: Added copy constructor to `FlatSVGIcon`. (issue #465)
- Moved `module-info.class` from `META-INF\versions\9\` to root folder of JARs.
  (issue #466)


## 2.0

- Added system property `flatlaf.nativeLibraryPath` to load native libraries
  from a directory. (PR #453)
- Fixed "endless recursion in font" exception in
  `FlatLaf$ActiveFont.createValue()` if `UIManager.getFont()` is invoked from
  multiple threads. (issue #456)
- PasswordField: Preserve reveal button state when switching theme. (PR #442;
  issue #173)
- PasswordField: Reveal button did not show password if
  `JPasswordField.setEchoChar()` was invoked from application. (PR #442; issue
  #173)
- Slider: Fixed/improved focused indicator color when changing accent color. (PR
  #375)
- TextField:
  - Improved hover/pressed/selected colors of leading/trailing buttons (e.g.
    "reveal" button in password field). (issue #452)
  - Clear button no longer paints over round border. (issue #451)
- Extras: Fixed concurrent loading of SVG icons on multiple threads. (issue
  #459)
- Use FlatLaf native window decorations by default when running in
  [JetBrains Runtime](https://github.com/JetBrains/JetBrainsRuntime/wiki)
  (instead of using JetBrains custom decorations). System variable
  `flatlaf.useJetBrainsCustomDecorations` is now `false` by default (was `true`
  in FlatLaf 1.x). (issue #454)
- Native window decorations:
  - Fixed blurry iconify/maximize/close button hover rectangles at 125%, 150% or
    175% scaling. (issue #431)
  - Updated maximize and restore icons for Windows 11 style. (requires Java
    8u321, 11.0.14, 17.0.2 or 18+)
  - Updated hover and pressed colors of iconify/maximize/close buttons for
    Windows 11 style.


## 2.0-rc1

#### New features and improvements

- Styling:
  - Styling individual components using string in CSS syntax or `java.util.Map`.
    (PR #341)\
    E.g.: `mySlider.putClientProperty( "FlatLaf.style", "trackWidth: 2" );`
  - Style classes allow defining style rules at a single place (in UI defaults)
    and use them in any component. (PR #388)\
    E.g.: `mySlider.putClientProperty( "FlatLaf.styleClass", "myclass" );`
- Typography defines several font styles for headers and various text sizes,
  which makes it easy to use consistent font styles across the application. (PR
  #396)
- Native window decorations (Windows 10/11 only):
  - Unified backgrounds for window title bar is now enabled by default (window
    title bar has now same background color as window content). Bottom separator
    for menu bars is no longer painted (if unified background is enabled).
  - Show Windows 11 snap layouts menu when hovering the mouse over the maximize
    button. (issues #397 and #407)
  - Possibility to hide window title bar icon (for single window set client
    property `JRootPane.titleBarShowIcon` to `false`; for all windows set UI
    value `TitlePane.showIcon` to `false`).
  - OptionPane: Hide window title bar icon by default. Can be be made visibly by
    setting UI default `OptionPane.showIcon` to `true`. (issue #416)
  - No longer show the Java "duke/cup" icon if no window icon image is set.
    (issue #416)
- TextField, FormattedTextField and PasswordField:
  - Support leading and trailing icons (set client property
    `JTextField.leadingIcon` or `JTextField.trailingIcon` to a
    `javax.swing.Icon`). (PR #378; issue #368)
  - Support leading and trailing components (set client property
    `JTextField.leadingComponent` or `JTextField.trailingComponent` to a
    `java.awt.Component`). (PR #386)
  - Support "clear" (or "cancel") button to empty text field. Only shown if text
    field is not empty, editable and enabled. (set client property
    `JTextField.showClearButton` to `true`). (PR #442)
- PasswordField: Support reveal (or "eye") button to show password. (see UI
  value `PasswordField.showRevealButton`) (PR #442; issue #173)
- TextComponents: Double/triple-click-and-drag now extends selection by whole
  words/lines.
- Theming improvements: Reworks core themes to make it easier to create new
  themes (e.g. reduced explicit colors by using color functions). **Note**:
  There are minor incompatible changes in FlatLaf properties files. (PR #390)
- ToolBar:
  - Toolbars are no longer floatable by default (dots on left side of toolbar
    that allows dragging toolbar). Use `UIManager.put( "ToolBar.floatable", true
    )` if you want the old behavior.
  - Skip components with empty input map (e.g. `JLabel`) when using arrow keys
    to navigate in focusable buttons (if UI value `ToolBar.focusableButtons` is
    `true`).
  - Support arrow-keys-only navigation within focusable buttons of toolbar (if
    UI value `ToolBar.focusableButtons` is `true`):
    - arrow keys move focus within toolbar
    - tab-key moves focus out of toolbar
    - if moving focus into the toolbar, focus recently focused toolbar button
- ComboBox, Spinner, TextField and subclasses: Support specifying width of
  border (see UI value `Component.borderWidth`).
- CheckBox and RadioButton:
  - Made selected icon better recognizable in **FlatLaf Light** (use blue
    border), **Dark** and **Darcula** (use lighter border) themes. **IntelliJ**
    theme is not changed.
  - Support specifying width of icon border (see UI value
    `CheckBox.icon.borderWidth`).
  - Reworked icon UI defaults and added missing ones. **Note**: There are minor
    incompatible changes in FlatLaf properties files.
- Slider: Support specifying width of thumb border (see UI value
  `Slider.thumbBorderWidth`).
- TabbedPane: Optionally paint selected tab as card. (PR #343)
- MenuItem:
  - Paint the selected icon when the item is selected. (PR #415)
  - Vertically align text if icons have different widths. (issue #437)
- Panel: Support painting background with rounded corners. (issue #367)
- Added more color functions to class `ColorFunctions` for easy use in
  applications: `lighten()`, `darken()`, `saturate()`, `desaturate()`, `spin()`,
  `tint()`, `shade()` and `luma()`.
- Support defining fonts in FlatLaf properties files. (issue #384)
- Added method `FlatLaf.registerCustomDefaultsSource(URL packageUrl)` for JPMS.
  (issue #325)
- Extras:
  - Added class `FlatDesktop` for easy integration into macOS screen menu
    (About, Preferences and Quit) when using Java 8.
  - `FlatSVGIcon`: Support loading SVG from `URL` (for JPMS), `URI`, `File` or
    `InputStream`. (issues #419 and #325)
  - `FlatSVGUtils`: Support loading SVG from `URL` (for JPMS). (issue #325)
- SwingX:
  - New "column control" icon for `JXTable` that scales and uses antialiasing.
    (issue #434)

#### Fixed bugs

- Native window decorations: Fixed `UnsatisfiedLinkError` on Windows 11 for ARM
  processors. (issue #443)
- MenuBar: Do not fill background if non-opaque and having custom background
  color. (issue #409)
- InternalFrame: Fill background to avoid that parent may shine through internal
  frame if it contains non-opaque components. (better fix for issue #274)
- SwingX: Fixed `NullPointerException` in `FlatCaret` when using
  `org.jdesktop.swingx.prompt.PromptSupport.setPrompt()` on a text field and
  then switching theme.


## 1.6.5

#### Fixed bugs

- Linux: Fixed font problems when running on Oracle Java (OpenJDK is not
  affected):
  - oversized text if system font is "Inter" (issue #427)
  - missing text if system font is "Cantarell" (on Fedora)
- MenuItem: Changed accelerator delimiter from `-` to `+`. (Windows and Linux).
- ComboBox: Fixed occasional `StackOverflowError` when modifying combo box not
  on AWT thread. (issue #432)
- macOS: Fixed `NullPointerException` when using AWT component
  `java.awt.Choice`. (issue #439)
- Native window decorations: Do not exit application with `UnsatisfiedLinkError`
  in case that FlatLaf DLL cannot be executed because of restrictions on
  temporary directory. Instead, continue with default window decorations. (issue
  #436)


## 1.6.4

#### Fixed bugs

- ComboBox: Fixed regression in FlatLaf 1.6.3 that makes selected item invisible
  in popup list if `DefaultListCellRenderer` is used as renderer. If using
  default renderer, it works. (issue #426)


## 1.6.3

#### Fixed bugs

- ComboBox (not editable): Fixed regression in FlatLaf 1.6.2 that may display
  text in non-editable combo boxes in bold. (issue #423)
- Tree: Fixed editing cell issue with custom cell renderer and cell editor that
  use same component for rendering and editing. (issue #385)


## 1.6.2

#### Fixed bugs

- ComboBox (not editable): Fixed background painted outside of border if round
  edges are enabled (client property `JComponent.roundRect` is `true`). (similar
  to issue #382; regression since fixing #330 in FlatLaf 1.4)
- ComboBox: Fixed `NullPointerException`, which may occur under special
  circumstances. (issue #408)
- Table: Do not select text in cell editor when it gets focus (when
  `JTable.surrendersFocusOnKeystroke` is `true`) and
  `TextComponent.selectAllOnFocusPolicy` is `once` (the default) or `always`.
  (issue #395)
- Linux: Fixed NPE when using `java.awt.TrayIcon`. (issue #405)
- FileChooser: Workaround for crash on Windows with Java 17 32-bit (disabled
  Windows icons). Java 17 64-bit is not affected. (issue #403)
- Native window decorations: Fixed layout loop, which may occur under special
  circumstances and slows down the application. (issue #420)


## 1.6.1

#### Fixed bugs

- Native window decorations: Catch `UnsatisfiedLinkError` when trying to load
  `jawt.dll` to avoid an application crash (Java 8 on Windows 10 only).


## 1.6

#### New features and improvements

- InternalFrame: Double-click on icon in internal frame title bar now closes the
  internal frame. (issue #374)
- IntelliJ Themes: Removed deprecated `install()` methods.

#### Fixed bugs

- Menus: Fixed missing modifiers flags in `ActionEvent` (e.g. `Ctrl` key
  pressed) when running in Java 9+ on Linux, macOS. Occurs also on Windows in
  large popup menus that do not fit into the window. (issue #371; regression
  since FlatLaf 1.3)
- OptionPane: Fixed `OptionPane.sameSizeButtons`, which did not work as expected
  when setting to `false`.
- OptionPane: Fixed rendering of longer HTML text if it is passed as
  `StringBuilder`, `StringBuffer`, or any other object that returns HTML text in
  method `toString()`. (similar to issue #12)
- ComboBox: Fixed popup border painting on HiDPI screens (e.g. at 150% scaling).
- ComboBox: Fixed popup location if shown above of combo box (Java 8 only).
- ComboBox (editable): Fixed wrong border of internal text field under special
  circumstances.
- Spinner: Fixed painting of border corners on left side. (issue #382;
  regression since fixing #330 in FlatLaf 1.4)
- TableHeader: Do not show resize cursor for last column if resizing last column
  is not possible because auto resize mode of table is not off. (issue #332)
- TableHeader: Fixed missing trailing vertical separator line if used in upper
  left corner of scroll pane. (issue #332)
- TextField, FormattedTextField, PasswordField and ComboBox: Fixed alignment of
  placeholder text in right-to-left component orientation.
- Slider: Fixed calculation of baseline, which was wrong under some
  circumstances.


## 1.5

#### New features and improvements

- SwingX: Added search and clear icons to `JXSearchField`. (issue #359)

#### Fixed bugs

- Button and TextComponent: Do not apply minimum width/height if margins are
  set. (issue #364)
- ComboBox and Spinner: Limit arrow button width if component has large
  preferred height. (issue #361)
- FileChooser: Fixed missing (localized) texts when FlatLaf is loaded in special
  classloader (e.g. plugin system in Apache NetBeans).
- InternalFrame: Limit internal frame bounds to parent bounds on resize. Also
  honor maximum size of internal frame. (issue #362)
- Popup: Fixed incorrectly placed drop shadow for medium-weight popups in
  maximized windows. (issue #358)
- Native window decorations (Windows 10 only):
  - Fixed occasional application crash in `flatlaf-windows.dll`. (issue #357)
  - When window is initially shown, fill background with window background color
    (instead of white), which avoids flickering in dark themes. (issue 339)
  - When resizing a window at the right/bottom edge, then first fill the new
    space with the window background color (instead of black) before the layout
    is updated.
  - When resizing a window at the left/top edge, then first fill the new space
    with the window background color (instead of garbage) before the layout is
    updated.


## 1.4

#### New features and improvements

- TextField, FormattedTextField and PasswordField: Support adding extra padding
  (set client property `JTextField.padding` to an `Insets`).
- PasswordField: UI delegate `FlatPasswordFieldUI` now extends `FlatTextFieldUI`
  (instead of `BasicPasswordFieldUI`) to avoid duplicate code and for easier
  extensibility.
- Table and PopupFactory: Use `StackWalker` in Java 9+ for better performance.
  (issue #334)
- ToolBar: Paint focus indicator for focused button in toolbar. (issue #346)
- ToolBar: Support focusable buttons in toolbar (set UI value
  `ToolBar.focusableButtons` to `true`). (issue #346)

#### Fixed bugs

- ComboBox (editable) and Spinner: Increased size of internal text field to the
  component border so that it behaves like plain text field (mouse click to left
  of text now positions caret to first character instead of opening ComboBox
  popup; mouse cursor is now of type "text" within the whole component, except
  for arrow buttons). (issue #330)
- ComboBox (not editable): Increased size of internal renderer pane to the
  component border so that it can paint within the whole component. Also
  increase combo box size if a custom renderer uses a border with insets that
  are larger than the default combo box padding (`2,6,2,6`).
- Fixed component heights at `1.25x`, `1.75x` and `2.25x` scaling factors (Java
  8 only) so that Button, ComboBox, Spinner and TextField components (including
  subclasses) have same heights. This increases heights of Button and TextField
  components by:
  - `2px` at `1.75x` in **Light** and **Dark** themes
  - `2px` at `1.25x` and `2.25x` in **IntelliJ** and **Darcula** themes
- OptionPane: Do not make child components, which are derived from `JPanel`,
  non-opaque. (issue #349)
- OptionPane: Align wrapped lines to the right if component orientation is
  right-to-left. (issue #350)
- PasswordField: Caps lock icon no longer painted over long text. (issue #172)
- PasswordField: Paint caps lock icon on left side in right-to-left component
  orientation.
- Window decorations: Window title bar width is no longer considered when
  calculating preferred/minimum width of window. (issue #351)


## 1.3

#### New features and improvements

- TextComponents, ComboBox and Spinner: Support different background color when
  component is focused (use UI values `TextField.focusedBackground`,
  `PasswordField.focusedBackground`, `FormattedTextField.focusedBackground`,
  `TextArea.focusedBackground`, `TextPane.focusedBackground`,
  `EditorPane.focusedBackground`, `ComboBox.focusedBackground`,
  `ComboBox.buttonFocusedBackground`, `ComboBox.popupBackground` and
  `Spinner.focusedBackground`). (issue #335)

#### Fixed bugs

- Fixed white lines at bottom and right side of window (in dark themes on HiDPI
  screens with scaling enabled).
- ScrollBar: Fixed left/top arrow icon location (if visible). (issue #329)
- Spinner: Fixed up/down arrow icon location.
- ToolTip: Fixed positioning of huge tooltips. (issue #333)


## 1.2

#### New features and improvements

- Renamed `Flat*Laf.install()` methods to `Flat*Laf.setup()` to avoid confusion
  with `UIManager.installLookAndFeel(LookAndFeelInfo info)`. The old
  `Flat*Laf.install()` methods are still there, but marked as deprecated. They
  will be removed in a future version.
- Button and ToggleButton: Support borderless button style (set client property
  `JButton.buttonType` to `borderless`). (PR #276)
- ComboBox: Support using as cell renderer (e.g. in `JTable`).
- DesktopPane: Improved layout of iconified internal frames in dock:
  - Always placed at bottom-left in desktop pane.
  - Newly iconified frames are added to the right side of the dock.
  - If frame is deiconified, dock is compacted (icons move to the left).
  - If dock is wider than desktop width, additional rows are used.
  - If desktop pane is resized, layout of dock is updated.
- TableHeader: Moved table header column border painting from
  `FlatTableHeaderUI` to new border `FlatTableHeaderBorder` to improve
  compatibility with custom table header implementations. (issue #228)
- Linux: Enable text anti-aliasing if no Gnome or KDE Desktop properties are
  available. (issue #218)
- IntelliJ Themes: Added "Material Theme UI Lite / GitHub Dark" theme.
- JIDE Common Layer: Improved support for `JideTabbedPane`. (PR #306)
- Extras: `FlatSVGIcon` improvements:
  - Each icon can now have its own color filter. (PR #303)
  - Use mapper function in color filter to dynamically map colors. (PR #303)
  - Color filter supports light and dark themes.
  - Getters for icon name, classloader, etc.
- Extras: UI Inspector: Show class hierarchies when pressing <kbd>Alt</kbd> key
  and prettified class names (dimmed package name).
- Extras: `FlatSVGUtils.createWindowIconImages()` now returns a single
  multi-resolution image that creates requested image sizes on demand from SVG
  (only on Windows with Java 9+).

#### Fixed bugs

- CheckBox and RadioButton: Do not fill background if used as cell renderer,
  except if cell is selected or has different background color. (issue #311)
- DesktopPane:
  - Fixed missing preview of iconified internal frames in dock when using a
    custom desktop manager. (PR #294)
  - Fixed incomplete preview of iconified internal frames in dock when switching
    LaF.
  - On HiDPI screens, use high-resolution images for preview of iconified
    internal frames in dock.
- PopupFactory: Fixed occasional `NullPointerException` in
  `FlatPopupFactory.fixToolTipLocation()`. (issue #305)
- Tree: Fill cell background if
  `DefaultTreeCellRenderer.setBackgroundNonSelectionColor(Color)` was used.
  (issue #322)
- IntelliJ Themes: Fixed background colors of DesktopPane and DesktopIcon in all
  themes.
- Native window decorations:
  - Fixed slow application startup under particular conditions. (e.g. incomplete
    custom JRE) (issue #319)
  - Fixed occasional double window title bar when creating many frames or
    dialogs. (issue #315)
  - Fixed broken maximizing window (under special conditions) when restoring
    frame state at startup.
  - Title icon: For multi-resolution images now use `getResolutionVariant(width,
    height)` (instead of `getResolutionVariants()`) to allow creation of
    requested size on demand. This also avoids creation of all resolution
    variants.
  - Double-click at upper-left corner of maximized frame did not close window.
    (issue #326)
- Linux: Fixed/improved detection of user font settings. (issue #309)


## 1.1.2

#### New features and improvements

- Native window decorations: Added API to check whether current platform
  supports window decorations (`FlatLaf.supportsNativeWindowDecorations()`) and
  to toggle window decorations of all windows
  (`FlatLaf.setUseNativeWindowDecorations(boolean)`).
- Native window decorations: Support changing title bar background and
  foreground colors per window. (set client properties
  `JRootPane.titleBarBackground` and `JRootPane.titleBarForeground` on root pane
  to a `java.awt.Color`).

#### Fixed bugs

- Native window decorations: Fixed loading of native library when using Java
  Platform Module System (JPMS) for application. (issue #289)
- Native window decorations: Removed superfluous pixel-line at top of screen
  when window is maximized. (issue #296)
- Window decorations: Fixed random window title bar background in cases were
  background is not filled by custom window or root pane components and unified
  background is enabled.
- IntelliJ Themes: Fixed window title bar background if unified background is
  enabled.
- IntelliJ Themes: Fixed system colors.
- Button and ToggleButton: Do not paint background of disabled (and unselected)
  toolBar buttons. (issue #292; regression since fixing #112)
- ComboBox and Spinner: Fixed too wide arrow button if component is higher than
  preferred. (issue #302)
- SplitPane: `JSplitPane.setContinuousLayout(false)` did not work. (issue #301)
- TabbedPane: Fixed NPE when creating/modifying in another thread. (issue #299)
- Fixed crash when running in Webswing. (issue #290)


## 1.1.1

#### New features and improvements

- Native window decorations: Support disabling native window decorations per
  window. (set client property `JRootPane.useWindowDecorations` on root pane to
  `false`).
- Support running on WinPE. (issue #279)

#### Fixed bugs

- Native window decorations: Fixed missing animations when minimizing,
  maximizing or restoring a window using window title bar buttons. (issue #282)
- Native window decorations: Fixed broken maximizing window when restoring frame
  state at startup. (issue #283)
- Native window decorations: Fixed double window title bar when first disposing
  a window with `frame.dispose()` and then showing it again with
  `frame.setVisible(true)`. (issue #277)
- Custom window decorations: Fixed NPE in `FlatTitlePane.findHorizontalGlue()`.
  (issue #275)
- Custom window decorations: Fixed right aligned progress bar in embedded menu
  bar was overlapping window title. (issue #272)
- Fixed missing focus indicators in heavy-weight popups. (issue #273)
- InternalFrame: Fixed translucent internal frame menu bar background if
  `TitlePane.unifiedBackground` is `true`. (issue #274)
- Extras: UI Inspector: Fixed `InaccessibleObjectException` when running in Java 16.


## 1.1

#### New features and improvements

- Windows 10 only:
  - Native window decorations for Windows 10 enables dark frame/dialog title bar
    and embedded menu bar with all JREs, while still having native Windows 10
    border drop shadows, resize behavior, window snapping and system window
    menu. (PR #267)
  - Custom window decorations: Support right aligned components in `JFrame`
    title bar with embedded menu bar (using `Box.createHorizontalGlue()`). (PR
    #268)
  - Custom window decorations: Improved centering of window title with embedded
    menu bar. (PR #268; issue #252)
  - Custom window decorations: Support unified backgrounds for window title bar,
    menu bar and main content. If enabled with `UIManager.put(
    "TitlePane.unifiedBackground", true );` then window title bar and menu bar
    use same background color as main content. (PR #268; issue #254)
- JIDE Common Layer: Support `JideButton`, `JideLabel`, `JideSplitButton`,
  `JideToggleButton` and `JideToggleSplitButton`.
- JIDE Common Layer: The library on Maven Central no longer depends on
  `com.jidesoft:jide-oss:3.6.18` to avoid problems when another JIDE library
  should be used. (issue #270)
- SwingX: The library on Maven Central no longer depends on
  `org.swinglabs.swingx:swingx-all:1.6.5-1` to avoid problems when another
  SwingX library should be used.
- Support running in [JetBrains Projector](https://jetbrains.com/projector/).

#### Fixed bugs

- IntelliJ Themes: Fixed text color of CheckBoxMenuItem and RadioButtonMenuItem
  in all "Arc" themes. (issue #259)


## 1.0

#### New features and improvements

- Extras: UI Inspector: Tooltip is no longer limited to window bounds.

#### Fixed bugs

- TabbedPane: Custom `TabbedPane.selectedForeground` color did not work when
  `TabbedPane.foreground` has also custom color. (issue #257)
- FileChooser: Fixed display of date in details view if current user is selected
  in "Look in" combobox. (Windows 10 only; issue #249)
- Table: Fixed wrong grid line thickness in dragged column on HiDPI screens on
  Java 9+. (issue #236)
- PopupFactory: Fixed `NullPointerException` when `PopupFactory.getPopup()` is
  invoked with parameter `owner` set to `null`.


## 1.0-rc3

#### New features and improvements

- Extras:
  - UI Inspector: Use HTML in tooltip. Display color value in same format as
    used in FlatLaf properties files. Added color preview.

#### Fixed bugs

- Label and ToolTip: Fixed font sizes for `<code>`, `<kbd>`, `<big>`, `<small>`
  and `<samp>` tags in HTML text.
- Fixed color of `<address>` tag in HTML text.
- IntelliJ Themes: Fixed table header background when dragging column in "Dark
  Flat" and "Light Flat" themes.
- CheckBox: Fixed background of check boxes in JIDE `CheckBoxTree`. (regression
  in 1.0-rc2)


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
  cell renderer in Table, Tree or List.
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
