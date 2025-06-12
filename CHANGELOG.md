FlatLaf Change Log
==================

## 3.7-SNAPSHOT

- Extras: Support JSVG 2.0.0. Minimum JSVG version is now 1.6.0. (issue #997)
- JideSplitButton: Fixed updating popup when switching theme. (issue #1000)
- IntelliJ Themes: Fixed logging false errors when loading 3rd party
  `.theme.json` files. (issue #990)
- Linux: Popups appeared in wrong position on multi-screen setup if primary
  display is located below or right to secondary display. (see
  [NetBeans issue #8532](https://github.com/apache/netbeans/issues/8532))


## 3.6

#### New features and improvements

- macOS: Re-enabled rounded popup border (see PR #772) on macOS 14.4+ (was
  disabled in 3.5.x).
- Increased contrast of text for better readability: (PR #972; issue #762)
  - In **FlatLaf Dark**, **FlatLaf Darcula** and many dark IntelliJ themes, made
    all text colors brighter.
  - In **FlatLaf Light**, **FlatLaf IntelliJ** and many light IntelliJ themes,
    made disabled text colors slightly darker.
  - In **FlatLaf macOS Light**, made disabled text colors darker.
  - In **FlatLaf macOS Dark**, made text colors of "default" button and selected
    ToggleButton lighter.
- CheckBox: Support styling indeterminate state of
  [tri-state check boxes](https://www.javadoc.io/doc/com.formdev/flatlaf-extras/latest/com/formdev/flatlaf/extras/components/FlatTriStateCheckBox.html).
  (PR #936; issue #919)
- List: Support for alternate row highlighting. (PR #939)
- Tree: Support for alternate row highlighting. (PR #903)
- Tree: Support wide cell renderer. (issue #922)
- ScrollBar: Use rounded thumb also on Windows (as on macOS and Linux) and made
  thumb slightly darker/lighter. (issue #918)
- Extras: `FlatSVGIcon` color filters now can access painting component to
  implement component state based color mappings. (issue #906)
- Linux:
  - Rounded iconify/maximize/close buttons if using FlatLaf window decorations.
    (PR #971)
  - Added `libflatlaf-linux-arm64.so` for Linux on ARM64. (issue #899)
  - Use X11 window manager events to resize window, if FlatLaf window
    decorations are enabled. This gives FlatLaf windows a more "native" feeling.
    (issue #866)
- IntelliJ Themes:
  - Updated to latest versions and fixed various issues.
  - Support customizing through properties files. (issue #824)
- SwingX: Support `JXTipOfTheDay` component. (issue #980)
- Support key prefixes for Linux desktop environments (e.g. `[gnome]`, `[kde]`
  or `[xfce]`) in properties files. (issue #974)
- Support custom key prefixes (e.g. `[win10]` or `[test]`) in properties files.
  (issue #649)
- Support multi-prefixed keys (e.g. `[dark][gnome]TitlePane.buttonBackground`).
  The value is only used if all prefixes match current platform/theme.
- Support new component border color to indicate success state (set client
  property `JComponent.outline` to `success`). (PR #982, issue #945)
- Fonts: Updated **Inter** to
  [v4.1](https://github.com/rsms/inter/releases/tag/v4.1).

#### Fixed bugs

- Button: Fixed background and foreground colors for `borderless` and
  `toolBarButton` style default buttons (`JButton.isDefaultButton()` is `true`).
  (issue #947)
- FileChooser: Improved performance when navigating to large directories with
  thousands of files. (issue #953)
- PopupFactory: Fixed NPE on Windows 10 when `owner` is `null`. (issue #952)
- Popup: On Windows 10, drop shadow of heavy-weight popup was not updated if
  popup moved/resized. (issue #942)
- FlatLaf window decorations:
  - Minimize and maximize icons were not shown for custom scale factors less
    than 100% (e.g. `-Dflatlaf.uiScale=75%`). (issue #951)
  - Linux: Fixed occasional maximizing of window when single-clicking the
    window's title bar. (issue #637)
- Styling: MigLayout visual padding was not updated after applying style to
  Button, ComboBox, Spinner, TextField (and subclasses) and ToggleButton. (issue
  #965)
- Linux: Popups (menus and combobox lists) were not hidden when window is moved,
  resized, maximized, restored, iconified or switched to another window. (issue
  #962)
- Fixed loading FlatLaf UI delegate classes when using FlatLaf in special
  application where multiple class loaders are involved. E.g. in Eclipse plugin
  or in LibreOffice extension. (issues #955 and #851)
- HTML: Fixed rendering of `<hr noshade>` in dark themes. (issue #932)
- TextComponents: `selectAllOnFocusPolicy` related changes:
  - No longer select all text if selection (or caret position) was changed by
    application and `selectAllOnFocusPolicy` is `once` (the default). (issue
    #983)
  - FormattedTextField and Spinner: `selectAllOnFocusPolicy = once` behaves now
    as `always` (was `never` before), which means that all text is selected when
    component gains focus. This is because of special behavior of
    `JFormattedTextField` that did not allow implementation of `once`.
  - Client property `JTextField.selectAllOnFocusPolicy` now also works on
    (editable) `JComboBox` and on `JSpinner`.
  - Added client property `JTextField.selectAllOnMouseClick` to override UI
    property `TextComponent.selectAllOnMouseClick`. (issue #961)
  - For `selectAllOnMouseClick = true`, clicking with the mouse into the text
    field, to focus it, now always selects all text, even if
    `selectAllOnFocusPolicy` is `once`.

#### Incompatibilities

- IntelliJ Themes:
  - Theme prefix in `IntelliJTheme$ThemeLaf.properties` changed from
    `[theme-name]` to `{theme-name}`.
  - Renamed classes in package
    `com.formdev.flatlaf.intellijthemes.materialthemeuilite` from `Flat<theme>`
    to `FlatMT<theme>`.
  - Removed `Gruvbox Dark Medium` and `Gruvbox Dark Soft` themes.
- Prefixed keys in properties files (e.g. `[dark]Button.background` or
  `[win]Button.arc`) are now handled earlier than before. In previous versions,
  prefixed keys always had higher priority than unprefixed keys and did always
  overwrite unprefixed keys. Now prefixed keys are handled in same order as
  unprefixed keys, which means that if a key is prefixed and unprefixed (e.g.
  `[win]Button.arc` and `Button.arc`), the one which is last specified in
  properties file is used.\
  Following worked in previous versions, but now `Button.arc` is always `6`:
  ~~~properties
  [win]Button.arc = 12
  Button.arc = 6
  ~~~
  This works in new (and old) versions:
  ~~~properties
  Button.arc = 6
  [win]Button.arc = 12
  ~~~


## 3.5.4

#### Fixed bugs

- HTML: Fixed NPE when using HTML text on a component with `null` font. (issue
  #930; PR #931; regression in 3.5)
- Linux: Fixed NPE when using FlatLaf window decorations and switching theme.
  (issue #933; regression in 3.5.3)


## 3.5.3

#### Fixed bugs

- HTML: Fixed wrong rendering if HTML text contains `<style>` tag with
  attributes (e.g. `<style type='text/css'>`). (issue #905; regression in 3.5.1)
- FlatLaf window decorations:
  - Windows: Fixed possible deadlock with TabbedPane in window title area in
    "full window content" mode. (issue #909)
  - Windows: Fixed wrong layout in maximized frame after changing screen scale
    factor. (issue #904)
  - Linux: Fixed continuous cursor toggling between resize and standard cursor
    when resizing window. (issue #907)
  - Fixed sometimes broken window moving with SplitPane in window title area in
    "full window content" mode. (issue #926)
- Popup: On Windows 10, fixed misplaced popup drop shadow. (issue #911;
  regression in 3.5)
- Popup: Fixed NPE if `GraphicsConfiguration` is `null` on Windows. (issue #921)
- Theme Editor: Fixed using color picker on secondary screen.
- Fixed detection of Windows 11 if custom exe launcher does not specify Windows
  10+ compatibility in application manifest. (issue #916)
- Linux: Fixed slightly different font size (or letter width) used to paint HTML
  text when default font family is _Cantarell_ (e.g. on Fedora). (issue #912)

#### Other Changes

- Class `FlatPropertiesLaf` now supports FlatLaf macOS themes as base themes.


## 3.5.2

#### Fixed bugs

- Windows: Fixed repaint issues (ghosting) on some systems (probably depending
  on graphics card/driver). This is done by setting Java system property
  `sun.java2d.d3d.onscreen` to `false` (but only if `sun.java2d.d3d.onscreen`,
  `sun.java2d.d3d` and `sun.java2d.noddraw` are not yet set), which disables
  usage of Windows Direct3D (DirectX) onscreen surfaces. Component rendering
  still uses Direct3D. (issue #887)
- FlatLaf window decorations:
  - Iconify/maximize/close buttons did not fill whole title bar height, if some
    custom component in menu bar increases title bar height. (issue #897)
  - Windows: Fixed possible application freeze when using custom component that
    overrides `Component.contains(int x, int y)` and invokes
    `SwingUtilities.convertPoint()` (or similar) from the overridden method.
    (issue #878)
- TextComponents: Fixed too fast scrolling in multi-line text components when
  using touchpads (e.g. on macOS). (issue #892)
- ToolBar: Fixed endless loop if button in Toolbar has focus and is made
  invisible. (issue #884)

#### Other Changes

- FlatLaf window decorations: Added client property `JRootPane.titleBarHeight`
  to allow specifying a (larger) preferred height for the title bar. (issue
  #897)
- Added system property `flatlaf.useRoundedPopupBorder` to allow disabling
  native rounded popup borders on Windows 11 and macOS. On macOS 14.4+, where
  rounded popup borders are disabled since FlatLaf 3.5 because of occasional
  problems, you can use this to enable rounded popup borders (at your risk).


## 3.5.1

#### Fixed bugs

- HTML: Fixed occasional cutoff wrapped text when using multi-line text in HTML
  tags `<h1>`...`<h6>`, `<code>`, `<kbd>`, `<big>`, `<small>` or `<samp>`.
  (issue #873; regression in 3.5)
- Popup: Fixed `UnsupportedOperationException: PERPIXEL_TRANSLUCENT translucency
  is not supported` exception on Haiku OS when showing popup (partly) outside of
  window. (issue #869)
- HiDPI: Fixed occasional wrong repaint areas when using
  `HiDPIUtils.installHiDPIRepaintManager()`. (see PR #864)
- Added system property `flatlaf.useSubMenuSafeTriangle` to allow disabling
  submenu safe triangle (PR #490) for
  [SWTSwing](https://github.com/Chrriis/SWTSwing). (issue #870)


## 3.5

#### New features and improvements

- Table: Support rounded selection. (PR #856)
- Button and ToggleButton: Added border colors for pressed and selected states.
  (issue #848)
- Label: Support painting background with rounded corners. (issue #842)
- Popup: Fixed flicker of popups (e.g. tooltips) while they are moving (e.g.
  following mouse pointer). (issues #832 and #672)
- FileChooser: Wrap shortcuts in scroll pane. (issue #828)
- Theme Editor: On macOS, use larger window title bar. (PR #779)

#### Fixed bugs

- macOS: Disabled rounded popup border (see PR #772) on macOS 14.4+ because it
  may freeze the application and crash the macOS WindowServer process (reports
  vary from Finder restarts to OS restarts). This is a temporary change until a
  solution is found. See NetBeans issues
  [apache/netbeans#7560](https://github.com/apache/netbeans/issues/7560#issuecomment-2226439215)
  and
  [apache/netbeans#6647](https://github.com/apache/netbeans/issues/6647#issuecomment-2070124442).
- FlatLaf window decorations: Window top border on Windows 10 in "full window
  content" mode was not fully repainted when activating or deactivating window.
  (issue #809)
- Button and ToggleButton: UI properties `[Toggle]Button.selectedForeground` and
  `[Toggle]Button.pressedForeground` did not work for HTML text. (issue #848)
- HTML: Fixed font sizes for HTML tags `<h1>`...`<h6>`, `<code>`, `<kbd>`,
  `<big>`, `<small>` and `<samp>` in HTML text for components Button, CheckBox,
  RadioButton, MenuItem (and subclasses), JideLabel, JideButton, JXBusyLabel and
  JXHyperlink. Also fixed for Label and ToolTip if using Java 11+.
- ScrollPane: Fixed/improved border painting at 125% - 175% scaling to avoid
  different border thicknesses. (issue #743)
- Table: Fixed painting of alternating rows below table if auto-resize mode is
  `JTable.AUTO_RESIZE_OFF` and table width is smaller than scroll pane (was not
  updated when table width changed and was painted on wrong side in
  right-to-left component orientation).
- Theme Editor: Fixed occasional empty window on startup on macOS.
- FlatLaf window decorations: Fixed black line sometimes painted on top of
  (native) window border on Windows 11. (issue #852)
- HiDPI: Fixed incomplete component paintings at 125% or 175% scaling on Windows
  where sometimes a 1px wide area at the right or bottom component edge is not
  repainted. E.g. ScrollPane focus indicator border. (issues #860 and #582)

#### Incompatibilities

- ProgressBar: Log warning (including stack trace) when uninstalling
  indeterminate progress bar UI or using `JProgressBar.setIndeterminate(false)`
  not on AWT thread, because this may throw NPE in `FlatProgressBarUI.paint()`.
  (issues #841 and #830)
- Panel: Rounded background of panel with rounded corners is now painted even if
  panel is not opaque. (issue #840)


## 3.4.1

#### Fixed bugs

- SplitPane: Update divider when client property `JSplitPane.expandableSide`
  changed.
- TabbedPane: Fixed swapped back and forward scroll buttons when using
  `TabbedPane.scrollButtonsPlacement = trailing` (regression in FlatLaf 3.3).
- Fixed missing window top border on Windows 10 in "full window content" mode.
  (issue #809)
- Extras:
  - `FlatSVGIcon` color filters now support linear gradients. (PR #817)
  - `FlatSVGIcon`: Use log level `CONFIG` instead of `SEVERE` and allow
    disabling logging. (issue #823)
  - Added support for `JSplitPane.expandableSide` client property to
    `FlatSplitPane`.
- Native libraries: Added API version check to test whether native library
  matches the JAR (bad builds could e.g. ship a newer JAR with an older
  incompatible native library) and to test whether native methods can be invoked
  (some security software allows loading native library but blocks method
  invocation).
- macOS: Fixed crash when running in WebSwing. (issue #826; regression in 3.4)

#### Incompatibilities

- File names of custom properties files for nested Laf classes now must include
  name of enclosing class name. E.g. nested Laf class `IntelliJTheme.ThemeLaf`
  used `ThemeLaf.properties` in previous versions, but now needs to be named
  `IntelliJTheme$ThemeLaf.properties`.


## 3.4

#### New features and improvements

- FlatLaf window decorations (Windows 10/11 and Linux): Support "full window
  content" mode, which allows you to extend the content into the window title
  bar. (PR #801)
- macOS: Support larger window title bar close/minimize/zoom buttons spacing in
  [full window content](https://www.formdev.com/flatlaf/macos/#full_window_content)
  mode and introduced "buttons placeholder". (PR #779)
- Native libraries:
  - System property `flatlaf.nativeLibraryPath` now supports loading native
    libraries named the same as on Maven central.
  - Published `flatlaf-<version>-no-natives.jar` to Maven Central. This JAR is
    equal to `flatlaf-<version>.jar`, except that it does not contain the
    FlatLaf native libraries. The Maven "classifier" to use this JAR is
    `no-natives`. You need to distribute the FlatLaf native libraries with your
    application.
    See https://www.formdev.com/flatlaf/native-libraries/ for more details.
  - Improved log messages for loading fails.
- Fonts: Updated **Inter** to
  [v4.0](https://github.com/rsms/inter/releases/tag/v4.0).
- Table: Select all text in cell editor when starting editing using `F2` key on
  Windows or Linux. (issue #652)

#### Fixed bugs

- macOS: Setting window background (of undecorated window) to translucent color
  (alpha < 255) did not show the window translucent. (issue #705)
- JIDE CommandMenuBar: Fixed `ClassCastException` when JIDE command bar displays
  `JideMenu` in popup. (PR #794)


## 3.3

#### New features and improvements

- macOS (10.14+): Popups (`JPopupMenu`, `JComboBox`, `JToolTip`, etc.) now use
  native macOS rounded borders. (PR #772; issue #715)
- Native libraries: Added `libflatlaf-macos-arm64.dylib` and
  `libflatlaf-macos-x86_64.dylib`. See also
  https://www.formdev.com/flatlaf/native-libraries/.
- ScrollPane: Support rounded border. (PR #713)
- SplitPane: Support divider hover and pressed background colors. (PR #788)
- TabbedPane: Support vertical tabs. (PR #758, issue #633)
- TabbedPane: Paint rounded tab area background for rounded cards. (issue #717)
- ToolBar: Added styling properties `separatorWidth` and `separatorColor`.

#### Fixed bugs

- Button and ToggleButton: Selected buttons did not use explicitly set
  foreground color. (issue #756)
- FileChooser: Catch NPE in Java 21 when getting icon for `.exe` files that use
  default Windows exe icon. (see
  [JDK-8320692](https://bugs.openjdk.org/browse/JDK-8320692))
- OptionPane: Fixed styling custom panel background in `JOptionPane`. (issue
  #761)
- ScrollPane: Styling ScrollPane border properties did not work if view
  component is a Table.
- Table:
  - Switching theme looses table grid and intercell spacing. (issues #733 and
    #750)
  - Fixed background of `boolean` columns when using alternating row colors.
    (issue #780)
  - Fixed border arc of components in complex table cell editors. (issue #786)
- TableHeader:
  - No longer temporary replace header cell renderer while painting. This avoids
    a `StackOverflowError` in case that custom renderer does this too. (see
    [NetBeans issue #6835](https://github.com/apache/netbeans/issues/6835)) This
    also improves compatibility with custom table header implementations.
  - Header cell renderer background/foreground colors were not restored after
    hover if renderer uses `null` for background/foreground. (PR #790)
- TabbedPane:
  - Avoid unnecessary repainting whole tabbed pane content area when layouting
    leading/trailing components.
  - Avoid unnecessary repainting of selected tab on temporary changes.
  - Fixed "endless" layouting and repainting when using nested tabbed panes (top
    and bottom tab placement) and RSyntaxTextArea (with enabled line-wrapping)
    as tab content. (see
    [jadx issue #2030](https://github.com/skylot/jadx/issues/2030))
- Fixed broken rendering after resizing window to minimum size and then
  increasing size again. (issue #767)

#### Incompatibilities

- Removed support for JetBrains custom decorations, which required
  [JetBrains Runtime](https://github.com/JetBrains/JetBrainsRuntime/wiki) (JBR)
  8 or 11. It did not work for JBR 17. System property
  `flatlaf.useJetBrainsCustomDecorations` is now ignored. **Note**: FlatLaf
  window decorations continue to work with JBR.


## 3.2.5

#### Fixed bugs

- Popup: Fixed NPE if popup invoker is `null` on Windows 10. (issue #753;
  regression in 3.2.1 in fix for #626)


## 3.2.4

#### Fixed bugs

- Popup: Fixed NPE if popup invoker is `null` on Linux with Wayland and Java 21.
  (issue #752; regression in 3.2.3)


## 3.2.3

#### Fixed bugs

- Popup: Popups that request focus were not shown on Linux with Wayland and Java 21.
  (issue #752)


## 3.2.2

#### Fixed bugs

- Button: Fixed painting icon and text at wrong location when using HTML text,
  left/right vertical alignment and running in Java 19+. (issue #746)
- CheckBox and RadioButton: Fixed cut off right side when border is removed and
  horizontal alignment is set to `right`. (issue #734)
- TabbedPane: Fixed NPE when using focusable component as tab component and
  switching theme. (issue #745)


## 3.2.1

#### Fixed bugs

- Fixed memory leak in
  `MultiResolutionImageSupport.create(int,Dimension[],Function<Dimension,Image>)`,
  which caches images created by the producer function. Used by
  `FlatSVGIcon.getImage()` and `FlatSVGUtils.createWindowIconImages()`. If you
  use one of these methods, it is **strongly recommended** to upgrade to this
  version, because if the returned image is larger and painted very often it may
  result in an out-of-memory situation. (issue #726)
- FileChooser: Fixed occasional NPE in `FlatShortcutsPanel` on Windows. (issue
  #718)
- TextField: Fixed placeholder text painting, which did not respect horizontal
  alignment property of `JTextField`. (issue #721)
- Popup: Fixed drop shadow if popup overlaps a heavyweight component. (Windows
  10 only; issue #626)


## 3.2

#### New features and improvements

- TabbedPane: Support rounded underline selection and rounded card tabs. (PR
  #703)
- FlatLaf window decorations:
  - Support for Windows on ARM 64-bit. (issue #443, PR #707)
  - Support toolbox-style "small" window title bar. (issue #659, PR #702)
- Extras: Class `FlatSVGIcon` now uses [JSVG](https://github.com/weisJ/jsvg)
  library (instead of svgSalamander) for rendering. JSVG provides improved SVG
  rendering and uses less memory compared to svgSalamander. (PR #684)
- ComboBox: Improved location of selected item in popup if list is large and
  scrollable.
- FileChooser: Show localized text for all locales supported by Java's Metal
  look and feel. (issue #680)
- Added system property `flatlaf.useNativeLibrary` to allow disabling loading of
  FlatLaf native library. (issue #674)
- IntelliJ Themes:
  - Reduced memory footprint by releasing Json data and ignoring IntelliJ UI
    properties that are not used in FlatLaf.
  - Updated "Hiberbee Dark" and "Gradianto" themes.

#### Fixed bugs

- Styling: Fixed scaling of some styling properties (`rowHeight` for Table and
  Tree; `iconTextGap` for Button, CheckBox and RadioButton). (issue #682)
- Fixed `IllegalComponentStateException` when invoker is not showing in
  `SubMenuUsabilityHelper`. (issue #692)
- macOS themes: Changing `@accentColor` variable in FlatLaf properties files did
  not change all accent related colors for all components.
- IntelliJ Themes:
  - "Light Owl" theme: Fixed wrong (unreadable) text color in selected menu
    items, selected text in text components, and selection in ComboBox popup
    list. (issue #687)
  - "Gradianto Midnight Blue" theme: Fixed color of ScrollBar track, which was
    not visible. (issue #686)
  - "Monocai" theme: Fixed unreadable text color of default buttons. (issue
    #693)
  - "Vuesion" theme: Fixed foreground colors of disabled text.
  - "Material UI Lite" themes: Fixed non-editable ComboBox button background.
  - CheckBox and RadioButton: Fixed unselected icon colors for themes "Atom One
    Light", "Cyan Light", "GitHub", "Light Owl", "Material Lighter" and
    "Solarized Light".
  - TabbedPane: Fixed focused tab background color for themes "Arc *", "Material
    Design Dark", "Monocai", "One Dark", "Spacegray" and "Xcode-Dark". (issue
    #697)
  - TextComponents, ComboBox and Spinner: Fixed background colors of enabled
    text components, to distinguish from disabled, for themes "Carbon", "Cobalt
    2", "Gradianto *", "Gruvbox *", "Monocai", "Spacegray", "Vuesion",
    "Xcode-Dark", "GitHub", and "Light Owl". (issue #528)
  - Fixed wrong disabled text colors in "Dark Flat", "Hiberbee Dark", "Light
    Flat", "Nord", "Solarized Dark" and "Solarized Light" themes.
  - Fixed colors for selection background/foreground, Separator, Slider track
    and ProgressBar background in various themes.
- Native Windows libraries: Fixed crash when running in Java 8 and newer Java
  version is installed in `PATH` environment variable and using class
  `SystemInfo` before AWT initialization. (issue #673)
- ComboBox: Fixed search in item list for text with spaces. (issue #691)
- FormattedTextField: On Linux, fixed `IllegalArgumentException: Invalid
  location` if `JFormattedTextField.setDocument()` is invoked in a focus gained
  listener on that formatted text field. (issue #698)
- PopupMenu: Make sure that popup menu does not overlap any operating system
  task bar. (issue #701)
- FileChooser: Use system icons on Windows with Java 17.0.3 (and later) 32-bit.
  Only Java 17 - 17.0.2 32-bit do not use system icons because of a bug in Java
  32-bit that crashes the application. (PR #709)
- FileChooser: Fixed crash on Windows with Java 17 to 17.0.2 32-bit. Java 17
  64-bit is not affected. (regression since FlatLaf 2.3; PR #522, see also issue
  #403)

#### Incompatibilities

- Extras: Class `FlatSVGIcon` now uses [JSVG](https://github.com/weisJ/jsvg)
  library for SVG rendering. You need to replace svgSalamander with JSVG in your
  build scripts and distribute `jsvg.jar` with your application. Also replace
  `com.kitfox.svg` with `com.github.weisj.jsvg` in `module-info.java` files.
- IntelliJ Themes: Removed all "Contrast" themes from "Material UI Lite".


## 3.1.1

- IntelliJ Themes:
  - Fixed too large menu item paddings and too large table/tree row heights (all
    "Material Theme UI Lite" themes; issue #667; regression in FlatLaf 3.1).
  - Fixed too large tree row height in "Carbon", "Dark Purple", "Gray",
    "Material Design Dark", "Monokai Pro", "One Dark" and "Spacegray" themes.
- Native libraries: Fixed `IllegalArgumentException: URI scheme is not "file"`
  when using FlatLaf in WebStart. (issue #668; regression in FlatLaf 3.1)


## 3.1

#### New features and improvements

- Windows 11: Popups (`JPopupMenu`, `JComboBox`, `JToolTip`, etc.) now use
  native Windows 11 rounded borders and drop shadows. (PR #643)
- Fonts:
  - Added **Roboto Mono** (https://fonts.google.com/specimen/Roboto+Mono). (PR
    #639, issue #638)
  - Updated **JetBrains Mono** to
    [v2.304](https://github.com/JetBrains/JetBrainsMono/releases/tag/v2.304).
- Theme Editor: Support macOS light and dark themes.
- TabbedPane: Support hover and focused tab foreground colors. (issue #627)
- TabbedPane: `tabbedPane.getBackgroundAt(tabIndex)` now has higher priority
  than `TabbedPane.focusColor` and `TabbedPane.selectedBackground`. If
  `tabbedPane.setBackgroundAt(tabIndex)` is used to set a color for a single
  tab, then this color is now used even if the tab is focused or selected.
- TableHeader: Support column hover and pressed background and foreground
  colors. (issue #636)
- Native libraries: Made it easier to distribute FlatLaf native libraries
  (Windows `.dll` and Linux `.so`) to avoid problems on operating systems with
  enabled execution restrictions.
  See https://www.formdev.com/flatlaf/native-libraries/ for more details. (issue #624)
  - Published native libraries to Maven Central for easy using them as
    dependencies in Gradle and Maven.
  - If available, native libraries are now loaded from same location as
    `flatlaf.jar`, otherwise they are extract from `flatlaf.jar` to temporary
    folder and loaded from there.
  - Windows DLLs are now digitally signed with FormDev Software GmbH
    certificate.

#### Fixed bugs

- FlatLaf window decorations:
  - Fixed inconsistent size of glass pane depending on whether FlatLaf window
    decorations are used (e.g. Windows 10/11) or not (e.g. macOS). Now the glass
    pane no longer overlaps the FlatLaf window title bar. (issue #630)
  - Linux: Fixed broken window resizing on multi-screen setups. (issue #632)
  - Linux: Fixed behavior of maximize/restore button when tiling window to left
    or right half of screen. (issue #647)
- IntelliJ Themes:
  - Fixed default button hover background in "Solarized Light" theme. (issue
    #628)
  - Avoid that accent color affect some colors in some IntelliJ themes. (issue
    #625)
  - Updated "Hiberbee Dark" and "Material Theme UI Lite" themes.
- Styling: Fixed resolving of UI variables in styles that use other variables.
- MenuItem: Fixed horizontal alignment of icons. (issue #631)
- Table: Fixed potential performance issue with paint cell focus indicator
  border. (issue #654)
- Tree: Fixed missing custom closed/opened/leaf icons of a custom
  `DefaultTreeCellRenderer`. (issue #653; regression since implementing PR #609
  in FlatLaf 3.0)
- Tree: Fixed truncated node text and too small painted non-wide node background
  if custom cell renderer sets icon, but not disabled icon, and tree is
  disabled. (issue #640)
- Fixed `HiDPIUtils.paintAtScale1x()`, which painted at wrong location if
  graphics is rotated, is scaled and `x` or `y` parameters are not zero. (issue
  #646)


## 3.0

#### New features and improvements

- **macOS light and dark themes**: The two new themes `FlatMacLightLaf` and
  `FlatMacDarkLaf` use macOS colors and look similar to native macOS controls.
  (PRs #533, #612 and #607)
- **Fonts**: Packaged some fonts into JARs and provide an easy way to use them
  with FlatLaf. (PRs #545, #614 and #615) At the moment there are three fonts:
  - **Inter** (https://rsms.me/inter/) - a typeface carefully crafted & designed
    for computer screens
  - **Roboto** (https://fonts.google.com/specimen/Roboto) - default font on
    Android and recommended for Material Design
  - **JetBrains Mono** (https://www.jetbrains.com/mono) - a monospaced typeface
- **Rounded selection**: Optionally use rounded selection in:
  - Menus (PR #536)
  - ComboBox (PR #548)
  - List (PR #547)
  - Tree (PR #546)
- Tree: Hide default closed/opened/leaf icons by default. Set UI value
  `Tree.showDefaultIcons` to `true` to show them.
- ToolBar: Hover effect for button groups. (PR #534)
- Icons: New modern **rounded outlined icons** for `JFileChooser`,
  `JOptionPane`, `JPasswordField` and `JTree`. (PR #577)

#### Fixed bugs

- FileChooser: Fixed layout of (optional) accessory component and fixed too
  large right margin. (issue #604; regression since implementing PR #522 in
  FlatLaf 2.3)
- Tree:
  - Fixed missing tree lines (if enabled) for wide-selected rows. (issue #598)
  - Fixed scaling of tree lines and fixed alignment to expand/collapse arrows.
  - Removed support for dashed tree lines. `Tree.lineTypeDashed` is now ignored.
- SwingX: Fonts in `JXHeader`, `JXMonthView`, `JXTaskPane` and `JXTitledPanel`
  were not updated when changing default font.


## 2.6

#### New features and improvements

- If value of system property `flatlaf.nativeLibraryPath` is `system`, then
  `System.loadLibrary(String)` is used to load the native library.
- TabbedPane: Switch and close tabs on left mouse click only. (PR #595)

#### Fixed bugs

- ComboBox and Spinner: Fixed missing arrow buttons if preferred height is zero.
  Minimum width of arrow buttons is 3/4 of default width.
- MenuBar: Fixed NPE in `FlatMenuItemRenderer.getTopLevelFont()` if menu item
  does not have a parent. (issue #600; regression since implementing #589 in
  FlatLaf 2.5)
- ScrollBar: Show "pressed" feedback on track/thumb only for left mouse button.
  If absolute positioning is enabled (the default), then also for middle mouse
  button.
- Arrow buttons in ComboBox, Spinner, ScrollBar and TabbedPane: Show "pressed"
  feedback only for left mouse button.
- ScaledImageIcon: Do not throw exceptions if image was has invalid size (e.g.
  not found). Instead, paint a red rectangle (similar to `FlatSVGIcon`).
- Fixed NPE in `FlatUIUtils.isCellEditor()`. (issue #601)


## 2.5

#### New features and improvements

- Linux: Use X11 window manager events to move window and to show window menu
  (right-click on window title bar), if custom window decorations are enabled.
  This gives FlatLaf windows a more "native" feeling. (issue #482)
- MenuBar: Support different menu selection style UI defaults for `MenuBar` and
  `MenuItem`. (issue #587)
- MenuBar: Top level menus now use `MenuBar.font` instead of `Menu.font`. (issue
  #589)
- PasswordField: Reveal button is now hidden (and turned off) if password field
  is disabled. (issue #501)
- TabbedPane: New option to disable tab run rotation in wrap layout. Set UI
  value `TabbedPane.rotateTabRuns` to `false`. (issue #574)
- Window decorations:
  - Added client property to mark components in embedded menu bar as "caption"
    (allow moving window). (issue #569)
  - Option to show window icon only in frames, but not in dialogs. Set UI value
    `TitlePane.showIconInDialogs` to `false`. (issue #589)
  - Added UI value `TitlePane.font` to customize window title font. (issue #589)
- Added system property `flatlaf.updateUIOnSystemFontChange` to allow disabling
  automatic UI update when system font changes. (issue #580)

#### Fixed bugs

- Fixed missing UI value `MenuItem.acceleratorDelimiter` on macOS. (was `null`,
  is now an empty string)
- Fixed possible exception in `FlatUIUtils.resetRenderingHints()`. (issue #575)
- Fixed AWT components on macOS, which use Swing components internally. (issue
  #583)
- SwingX: Fixed missing highlighting of "today" in `JXMonthView` and
  `JXDatePicker`.


## 2.4

#### New features and improvements

- Native window decorations (Windows 10/11 only):
  - There is now a small area at top of the embedded menu bar to resize the
    window.
  - Improved window title bar layout for small window widths:
    - Width of iconify/maximize/close buttons is reduced (if necessary) to give
      more space to embedded menu bar and title.
    - Window title now has a minimum width to always allow moving window
      (click-and-drag on window title). Instead, embedded menu bar is made
      smaller.
    - Option to show window icon beside window title, if menu bar is embedded or
      title is centered. Set UI value `TitlePane.showIconBesideTitle` to `true`.
  - No longer reduce height of window title bar if it has an embedded menu bar
    and is maximized.

#### Fixed bugs

- ComboBox: Fixed vertical alignment of text in popup list with text in combo
  box in IntelliJ/Darcula themes.
- Menus: Fixed application freeze under very special conditions (invoking
  `FlatLaf.initialize()` twice in NetBeans GUI builder) and using menu that has
  submenus. See
  [NetBeans issue #4231](https://github.com/apache/netbeans/issues/4231#issuecomment-1179611682)
  for details.
- MenuItem: Fixed sometimes wrapped HTML text on HiDPI screens on Windows.
- TableHeader: Fixed exception when changing table structure (e.g. removing
  column) from a table header popup menu action. (issue #532)
- `HiDPIUtils.paintAtScale1x()` now supports rotated graphics. (issue #557)
- Typography: No longer use `Consolas` or `Courier New` as monospaced font on
  Windows because they have bad vertically placement.
- Native window decorations (Windows 10/11 only):
  - Do not center window title if embedded menu bar is empty or has no menus at
    left side, but some components at right side. (issue #558)
  - Do not use window decorations if system property `sun.java2d.opengl` is
    `true` on Windows 10. (issue #540)
  - Fixed missing top window border in dark themes if window drop shadows are
    disabled in system settings. (issue #554; Windows 10 only)
  - Right-to-left component orientation of title bar was lost when switching
    theme.


## 2.3

#### New features and improvements

- FileChooser: Added (optional) shortcuts panel. On Windows it contains "Recent
  Items", "Desktop", "Documents", "This PC" and "Network". On macOS and Linux it
  is empty/hidden. (issue #100)
- Button and ToggleButton: Added missing foreground colors for hover, pressed,
  focused and selected states. (issue #535)
- Table: Optionally paint alternating rows below table if table is smaller than
  scroll pane. Set UI value `Table.paintOutsideAlternateRows` to `true`.
  Requires that `Table.alternateRowColor` is set to a color. (issue #504)
- ToggleButton: Made the underline placement of tab-style toggle buttons
  configurable. (PR #530; issue #529)
- Added spanish translation. (PR #525)

#### Fixed bugs

- IntelliJ Themes: Fixed `TitledBorder` text color in "Monokai Pro" theme.
  (issue #524)


## 2.2

#### New features and improvements

- SplitPane: Allow limiting one-touch expanding to a single side (set client
  property `JSplitPane.expandableSide` to `"left"` or `"right"`). (issue #355)
- TabbedPane: Selected tab underline color now changes depending on whether the
  focus is within the tab content. (issue #398)
- IntelliJ Themes:
  - Added "Monokai Pro" and "Xcode-Dark" themes.
  - TabbedPane now use different background color for selected tabs in all "Arc"
    themes, in "Hiberbee Dark" and in all "Material UI Lite" themes.

#### Fixed bugs

- Native window decorations (Windows 10/11 only): Fixed wrong window title
  character encoding used in Windows taskbar. (issue #502)
- Button: Fixed icon layout and preferred width of default buttons that use bold
  font. (issue #506)
- FileChooser: Enabled full row selection for details view to fix alternate row
  coloring. (issue #512)
- SplitPane: Fixed `StackOverflowError` caused by layout loop that may occur
  under special circumstances. (issue #513)
- Table: Slightly changed grid colors to make grid better recognizable. (issue
  #514)
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
  - OptionPane: Hide window title bar icon by default. Can be made visibly by
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
    (instead of white), which avoids flickering in dark themes. (issue #339)
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
