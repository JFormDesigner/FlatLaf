/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
import javax.swing.table.TableCellRenderer;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.icons.FlatFileViewDirectoryIcon;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JFileChooser}.
 *
 * <!-- BasicFileChooserUI -->
 *
 * @uiDefault FileView.directoryIcon					Icon
 * @uiDefault FileView.fileIcon							Icon
 * @uiDefault FileView.computerIcon						Icon
 * @uiDefault FileView.hardDriveIcon					Icon
 * @uiDefault FileView.floppyDriveIcon					Icon
 *
 * @uiDefault FileChooser.newFolderIcon					Icon
 * @uiDefault FileChooser.upFolderIcon					Icon
 * @uiDefault FileChooser.homeFolderIcon				Icon
 * @uiDefault FileChooser.detailsViewIcon				Icon
 * @uiDefault FileChooser.listViewIcon					Icon
 * @uiDefault FileChooser.viewMenuIcon					Icon
 *
 * @uiDefault FileChooser.usesSingleFilePane			boolean
 * @uiDefault FileChooser.readOnly						boolean	if true, "New Folder" is disabled
 *
 * @uiDefault FileChooser.newFolderErrorText					String
 * @uiDefault FileChooser.newFolderErrorSeparator				String
 * @uiDefault FileChooser.newFolderParentDoesntExistTitleText	String
 * @uiDefault FileChooser.newFolderParentDoesntExistText		String
 * @uiDefault FileChooser.fileDescriptionText					String
 * @uiDefault FileChooser.directoryDescriptionText				String
 * @uiDefault FileChooser.saveButtonText						String
 * @uiDefault FileChooser.openButtonText						String
 * @uiDefault FileChooser.saveDialogTitleText					String
 * @uiDefault FileChooser.openDialogTitleText					String
 * @uiDefault FileChooser.cancelButtonText						String
 * @uiDefault FileChooser.updateButtonText						String
 * @uiDefault FileChooser.helpButtonText						String
 * @uiDefault FileChooser.directoryOpenButtonText				String
 *
 * @uiDefault FileChooser.saveButtonMnemonic					String
 * @uiDefault FileChooser.openButtonMnemonic					String
 * @uiDefault FileChooser.cancelButtonMnemonic					String
 * @uiDefault FileChooser.updateButtonMnemonic					String
 * @uiDefault FileChooser.helpButtonMnemonic					String
 * @uiDefault FileChooser.directoryOpenButtonMnemonic			String
 *
 * @uiDefault FileChooser.saveButtonToolTipText					String
 * @uiDefault FileChooser.openButtonToolTipText					String
 * @uiDefault FileChooser.cancelButtonToolTipText				String
 * @uiDefault FileChooser.updateButtonToolTipText				String
 * @uiDefault FileChooser.helpButtonToolTipText					String
 * @uiDefault FileChooser.directoryOpenButtonToolTipText		String
 *
 * @uiDefault FileChooser.acceptAllFileFilterText				String
 *
 * <!-- MetalFileChooserUI -->
 *
 * @uiDefault FileChooser.lookInLabelMnemonic					String
 * @uiDefault FileChooser.lookInLabelText						String
 * @uiDefault FileChooser.saveInLabelText						String
 * @uiDefault FileChooser.fileNameLabelMnemonic					String
 * @uiDefault FileChooser.fileNameLabelText						String
 * @uiDefault FileChooser.folderNameLabelMnemonic				String
 * @uiDefault FileChooser.folderNameLabelText					String
 * @uiDefault FileChooser.filesOfTypeLabelMnemonic				String
 * @uiDefault FileChooser.filesOfTypeLabelText					String
 *
 * @uiDefault FileChooser.upFolderToolTipText					String
 * @uiDefault FileChooser.upFolderAccessibleName				String
 * @uiDefault FileChooser.homeFolderToolTipText					String
 * @uiDefault FileChooser.homeFolderAccessibleName				String
 * @uiDefault FileChooser.newFolderToolTipText					String
 * @uiDefault FileChooser.newFolderAccessibleName				String
 * @uiDefault FileChooser.listViewButtonToolTipText				String
 * @uiDefault FileChooser.listViewButtonAccessibleName			String
 * @uiDefault FileChooser.detailsViewButtonToolTipText			String
 * @uiDefault FileChooser.detailsViewButtonAccessibleName		String
 *
 * <!-- FilePane -->
 *
 * @uiDefault FileChooser.fileNameHeaderText					String
 * @uiDefault FileChooser.fileSizeHeaderText					String
 * @uiDefault FileChooser.fileTypeHeaderText					String
 * @uiDefault FileChooser.fileDateHeaderText					String
 * @uiDefault FileChooser.fileAttrHeaderText					String
 *
 * @uiDefault FileChooser.viewMenuLabelText						String
 * @uiDefault FileChooser.refreshActionLabelText				String
 * @uiDefault FileChooser.newFolderActionLabelText				String
 * @uiDefault FileChooser.listViewActionLabelText				String
 * @uiDefault FileChooser.detailsViewActionLabelText			String
 *
 * <!-- FlatFileChooserUI -->
 *
 * @uiDefault FileChooser.shortcuts.buttonSize					Dimension	optional; default is 84,64
 * @uiDefault FileChooser.shortcuts.iconSize					Dimension	optional; default is 32,32
 * @uiDefault FileChooser.shortcuts.filesFunction				Function&lt;File[], File[]&gt;
 * @uiDefault FileChooser.shortcuts.displayNameFunction			Function&lt;File, String&gt;
 * @uiDefault FileChooser.shortcuts.iconFunction				Function&lt;File, Icon&gt;
 *
 * @author Karl Tauber
 */
public class FlatFileChooserUI
	extends MetalFileChooserUI
{
	private final FlatFileView fileView = new FlatFileView();
	private FlatShortcutsPanel shortcutsPanel;
	private JScrollPane shortcutsScrollPane;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatFileChooserUI( (JFileChooser) c );
	}

	public FlatFileChooserUI( JFileChooser filechooser ) {
		super( filechooser );
	}

	@Override
	public void installComponents( JFileChooser fc ) {
		super.installComponents( fc );

		patchUI( fc );

		if( !UIManager.getBoolean( "FileChooser.noPlacesBar" ) ) { // same as in Windows L&F
			FlatShortcutsPanel panel = createShortcutsPanel( fc );
			if( panel.getComponentCount() > 0 ) {
				shortcutsPanel = panel;
				shortcutsScrollPane = new JScrollPane( shortcutsPanel,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
				shortcutsScrollPane.setBorder( BorderFactory.createEmptyBorder() );
				fc.add( shortcutsScrollPane, BorderLayout.LINE_START );
				fc.addPropertyChangeListener( shortcutsPanel );
			}
		}
	}

	@Override
	public void uninstallComponents( JFileChooser fc ) {
		super.uninstallComponents( fc );

		if( shortcutsPanel != null ) {
			fc.removePropertyChangeListener( shortcutsPanel );
			shortcutsPanel = null;
			shortcutsScrollPane = null;
		}
	}

	private void patchUI( JFileChooser fc ) {
		// turn top-right buttons into toolbar buttons
		Component topPanel = fc.getComponent( 0 );
		if( (topPanel instanceof JPanel) &&
			(((JPanel)topPanel).getLayout() instanceof BorderLayout) )
		{
			Component topButtonPanel = ((JPanel)topPanel).getComponent( 0 );
			if( (topButtonPanel instanceof JPanel) &&
				(((JPanel)topButtonPanel).getLayout() instanceof BoxLayout) )
			{
				Insets margin = UIManager.getInsets( "Button.margin" );
				Component[] comps = ((JPanel)topButtonPanel).getComponents();
				for( int i = comps.length - 1; i >= 0; i-- ) {
					Component c = comps[i];
					if( c instanceof JButton || c instanceof JToggleButton ) {
						AbstractButton b = (AbstractButton)c;
						b.putClientProperty( FlatClientProperties.BUTTON_TYPE,
							FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON );
						b.setMargin( margin );
						b.setFocusable( false );
					} else if( c instanceof Box.Filler )
						((JPanel)topButtonPanel).remove( i );
				}
			}
		}

		// increase maximum row count of directory combo box popup list
		try {
			Component directoryComboBox =  ((JPanel)topPanel).getComponent( 2 );
			if( directoryComboBox instanceof JComboBox ) {
				int maximumRowCount = UIManager.getInt( "ComboBox.maximumRowCount" );
				if( maximumRowCount > 0 )
					((JComboBox<?>)directoryComboBox).setMaximumRowCount( maximumRowCount );
			}
		} catch( ArrayIndexOutOfBoundsException ex ) {
			// ignore
		}

		// put north, center and south components into a new panel so that
		// the shortcuts panel (at west) gets full height
		LayoutManager layout = fc.getLayout();
		if( layout instanceof BorderLayout ) {
			BorderLayout borderLayout = (BorderLayout) layout;
			borderLayout.setHgap( 8 );

			Component north = borderLayout.getLayoutComponent( BorderLayout.NORTH );
			Component lineEnd = borderLayout.getLayoutComponent( BorderLayout.LINE_END );
			Component center = borderLayout.getLayoutComponent( BorderLayout.CENTER );
			Component south = borderLayout.getLayoutComponent( BorderLayout.SOUTH );
			if( north != null && lineEnd != null && center != null && south != null ) {
				JPanel p = new JPanel( new BorderLayout( 0, 11 ) );
				p.add( north, BorderLayout.NORTH );
				p.add( lineEnd, BorderLayout.LINE_END );
				p.add( center, BorderLayout.CENTER );
				p.add( south, BorderLayout.SOUTH );
				fc.add( p, BorderLayout.CENTER );
			}
		}
	}

	@Override
	protected JPanel createDetailsView( JFileChooser fc ) {
		JPanel p = super.createDetailsView( fc );

		if( !SystemInfo.isWindows )
			return p;

		// find scroll pane
		JScrollPane scrollPane = null;
		for( Component c : p.getComponents() ) {
			if( c instanceof JScrollPane ) {
				scrollPane = (JScrollPane) c;
				break;
			}
		}
		if( scrollPane == null )
			return p;

		// get scroll view, which should be a table
		Component view = scrollPane.getViewport().getView();
		if( !(view instanceof JTable) )
			return p;

		JTable table = (JTable) view;

		// on Windows 10, the date may contain left-to-right (0x200e) and right-to-left (0x200f)
		// mark characters (see https://en.wikipedia.org/wiki/Left-to-right_mark)
		// when the "current user" item is selected in the "look in" combobox
		// --> remove them
		TableCellRenderer defaultRenderer = table.getDefaultRenderer( Object.class );
		table.setDefaultRenderer( Object.class, new TableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column )
			{
				// remove left-to-right and right-to-left mark characters
				if( value instanceof String && ((String)value).startsWith( "\u200e" ) ) {
					String str = (String) value;
					char[] buf = new char[str.length()];
					int j = 0;
					for( int i = 0; i < buf.length; i++ ) {
						char ch = str.charAt( i );
						if( ch != '\u200e' && ch != '\u200f' )
							buf[j++] = ch;
					}
					value = new String( buf, 0, j );
				}

				return defaultRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
			}

		} );

		return p;
	}

	/** @since 2.3 */
	protected FlatShortcutsPanel createShortcutsPanel( JFileChooser fc ) {
		return new FlatShortcutsPanel( fc );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension prefSize = super.getPreferredSize( c );
		Dimension minSize = getMinimumSize( c );
		int shortcutsPanelWidth = (shortcutsScrollPane != null) ? shortcutsScrollPane.getPreferredSize().width : 0;
		return new Dimension(
			Math.max( prefSize.width, minSize.width + shortcutsPanelWidth ),
			Math.max( prefSize.height, minSize.height ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return UIScale.scale( super.getMinimumSize( c ) );
	}

	@Override
	public FileView getFileView( JFileChooser fc ) {
		return doNotUseSystemIcons() ? super.getFileView( fc ) : fileView;
	}

	@Override
	public void clearIconCache() {
		if( doNotUseSystemIcons() )
			super.clearIconCache();
		else
			fileView.clearIconCache();
	}

	private static boolean doNotUseSystemIcons() {
		// Java 17 32bit craches on Windows when using system icons
		// fixed in Java 18+, fix backported in Java 17.0.3+ (see https://bugs.openjdk.java.net/browse/JDK-8277299)
		return SystemInfo.isWindows &&
			SystemInfo.isX86 &&
			(SystemInfo.isJava_17_orLater && SystemInfo.javaVersion < SystemInfo.toVersion( 17, 0, 3, 0 ));
	}

	//---- class FlatFileView -------------------------------------------------

	private class FlatFileView
		extends BasicFileView
	{
		@Override
		public Icon getIcon( File f ) {
			// get cached icon
			Icon icon = getCachedIcon( f );
			if( icon != null )
				return icon;

			// new proxy icon
			//
			// Note: Since this is a super light weight icon object, we do not add it
			//       to the icon cache here. This keeps cache small in case of large directories
			//       with thousands of files when icons of all files are only needed to compute
			//       the layout of list/table, but never painted because located outside of visible area.
			//       When an icon needs to be painted, the proxy adds it to the icon cache
			//       and loads the real icon.
			return new FlatFileViewIcon( f );
		}

		//---- class FlatFileViewIcon -----------------------------------------

		/**
		 * A proxy icon that has a fixed (scaled) width/height (16x16) and
		 * gets/loads the real (system) icon only for painting.
		 * Avoids unnecessary getting/loading system icons.
		 */
		private class FlatFileViewIcon
			implements Icon
		{
			private final File f;
			private Icon realIcon;

			FlatFileViewIcon( File f ) {
				this.f = f;
			}

			@Override
			public int getIconWidth() {
				return UIScale.scale( 16 );
			}

			@Override
			public int getIconHeight() {
				return UIScale.scale( 16 );
			}

			@Override
			public void paintIcon( Component c, Graphics g, int x, int y ) {
				// get icon on demand
				if( realIcon == null ) {
					// get system icon
					try {
						if( f != null )
							realIcon = getFileChooser().getFileSystemView().getSystemIcon( f );
					} catch( NullPointerException ex ) {
						// Java 21 may throw a NPE for exe files that use default Windows exe icon
					}

					// get default icon
					if( realIcon == null )
						realIcon = FlatFileView.super.getIcon( f );

					if( realIcon instanceof ImageIcon )
						realIcon = new ScaledImageIcon( (ImageIcon) realIcon );

					cacheIcon( f, this );
				}

				realIcon.paintIcon( c, g, x, y );
			}
		}
	}

	//---- class FlatShortcutsPanel -------------------------------------------

	/** @since 2.3 */
	public static class FlatShortcutsPanel
		extends JToolBar
		implements PropertyChangeListener, Scrollable
	{
		private final JFileChooser fc;

		private final Dimension buttonSize;
		private final Dimension iconSize;
		private final Function<File[], File[]> filesFunction;
		private final Function<File, String> displayNameFunction;
		private final Function<File, Icon> iconFunction;

		protected final File[] files;
		protected final JToggleButton[] buttons;
		protected final ButtonGroup buttonGroup = new ButtonGroup();

		@SuppressWarnings( "unchecked" )
		public FlatShortcutsPanel( JFileChooser fc ) {
			super( JToolBar.VERTICAL );
			this.fc = fc;
			setFloatable( false );
			putClientProperty( FlatClientProperties.STYLE, "hoverButtonGroupBackground: null" );

			buttonSize = UIScale.scale( getUIDimension( "FileChooser.shortcuts.buttonSize", 84, 64 ) );
			iconSize = getUIDimension( "FileChooser.shortcuts.iconSize", 32, 32 );

			filesFunction = (Function<File[], File[]>) UIManager.get( "FileChooser.shortcuts.filesFunction" );
			displayNameFunction = (Function<File, String>) UIManager.get( "FileChooser.shortcuts.displayNameFunction" );
			iconFunction = (Function<File, Icon>) UIManager.get( "FileChooser.shortcuts.iconFunction" );

			FileSystemView fsv = fc.getFileSystemView();
			File[] files = JavaCompatibility2.getChooserShortcutPanelFiles( fsv );
			if( filesFunction != null )
				files = filesFunction.apply( files );

			// create toolbar buttons
			ArrayList<File> filesList = new ArrayList<>();
			ArrayList<JToggleButton> buttonsList = new ArrayList<>();
			for( File file : files ) {
				if( file == null )
					continue;

				// wrap drive path
				if( fsv.isFileSystemRoot( file ) )
					file = fsv.createFileObject( file.getAbsolutePath() );

				String name = getDisplayName( fsv, file );
				Icon icon = getIcon( fsv, file );
				if( name == null )
					continue;

				// remove path from name
				int lastSepIndex = name.lastIndexOf( File.separatorChar );
				if( lastSepIndex >= 0 && lastSepIndex < name.length() - 1 )
					name = name.substring( lastSepIndex + 1 );

				// scale icon (if necessary)
				if( icon instanceof ImageIcon )
					icon = new ScaledImageIcon( (ImageIcon) icon, iconSize.width, iconSize.height );
				else if( icon != null )
					icon = new ShortcutIcon( icon, iconSize.width, iconSize.height );

				// create button
				JToggleButton button = createButton( name, icon, file.toString() );
				File f = file;
				button.addActionListener( e -> {
					fc.setCurrentDirectory( f );
				} );

				add( button );
				buttonGroup.add( button );

				filesList.add( file );
				buttonsList.add( button );
			}

			this.files = filesList.toArray( new File[filesList.size()] );
			this.buttons = buttonsList.toArray( new JToggleButton[buttonsList.size()] );

			directoryChanged( fc.getCurrentDirectory() );
		}

		private Dimension getUIDimension( String key, int defaultWidth, int defaultHeight ) {
			Dimension size = UIManager.getDimension( key );
			if( size == null )
				size = new Dimension( defaultWidth, defaultHeight );
			return size;
		}

		/** @since 3.5 */
		protected JToggleButton createButton( String name, Icon icon, String toolTip ) {
			JToggleButton button = new JToggleButton( name, icon );
			button.setToolTipText( toolTip );
			button.setVerticalTextPosition( SwingConstants.BOTTOM );
			button.setHorizontalTextPosition( SwingConstants.CENTER );
			button.setAlignmentX( Component.CENTER_ALIGNMENT );
			button.setIconTextGap( 0 );
			button.setPreferredSize( buttonSize );
			button.setMaximumSize( buttonSize );
			return button;
		}

		protected String getDisplayName( FileSystemView fsv, File file ) {
			if( displayNameFunction != null ) {
				String name = displayNameFunction.apply( file );
				if( name != null )
					return name;
			}

			return fsv.getSystemDisplayName( file );
		}

		protected Icon getIcon( FileSystemView fsv, File file ) {
			if( iconFunction != null ) {
				Icon icon = iconFunction.apply( file );
				if( icon != null )
					return icon;
			}

			if( doNotUseSystemIcons() )
				return new FlatFileViewDirectoryIcon();

			try {
				// Java 17+ supports getting larger system icons
				try {
					if( SystemInfo.isJava_17_orLater ) {
						Method m = fsv.getClass().getMethod( "getSystemIcon", File.class, int.class, int.class );
						return (Icon) m.invoke( fsv, file, iconSize.width, iconSize.height );
					} else if( iconSize.width > 16 || iconSize.height > 16 ) {
						Class<?> cls = Class.forName( "sun.awt.shell.ShellFolder" );
						if( cls.isInstance( file ) ) {
							Method m = file.getClass().getMethod( "getIcon", boolean.class );
							m.setAccessible( true );
							Image image = (Image) m.invoke( file, true );
							if( image != null )
								return new ImageIcon( image );
						}
					}
				} catch( Exception ex ) {
					// do not log InaccessibleObjectException because access
					// may be denied via VM option '--illegal-access=deny' (default in Java 16)
					// (not catching InaccessibleObjectException here because it is new in Java 9, but FlatLaf also runs on Java 8)
					if( !"java.lang.reflect.InaccessibleObjectException".equals( ex.getClass().getName() ) )
						LoggingFacade.INSTANCE.logSevere( null, ex );
				}

				// get system icon in default size 16x16
				return fsv.getSystemIcon( file );
			} catch( NullPointerException ex ) {
				// Java 21 may throw a NPE for exe files that use default Windows exe icon
				return new FlatFileViewDirectoryIcon();
			}
		}

		protected void directoryChanged( File file ) {
			if( file != null ) {
				String absolutePath = file.getAbsolutePath();
				for( int i = 0; i < files.length; i++ ) {
					// also compare path because otherwise selecting "Documents"
					// in "Look in" combobox would not select "Documents" shortcut item
					if( files[i].equals( file ) || files[i].getAbsolutePath().equals( absolutePath ) ) {
						buttons[i].setSelected( true );
						return;
					}
				}
			}

			buttonGroup.clearSelection();
		}

		//---- interface PropertyChangeListener ----

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			switch( e.getPropertyName() ) {
				case JFileChooser.DIRECTORY_CHANGED_PROPERTY:
					directoryChanged( fc.getCurrentDirectory() );
					break;
			}
		}

		//---- interface Scrollable ----

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			if( getComponentCount() > 0 ) {
				Insets insets = getInsets();
				int height = (getComponent( 0 ).getPreferredSize().height * 5) + insets.top + insets.bottom;
				return new Dimension( getPreferredSize().width, height );
			}
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
			if( orientation == SwingConstants.VERTICAL && getComponentCount() > 0 )
				return getComponent( 0 ).getPreferredSize().height;

			return getScrollableBlockIncrement( visibleRect, orientation, direction ) / 10;
		}

		@Override
		public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
			return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
	}

	//---- class ShortcutIcon -------------------------------------------------

	private static class ShortcutIcon
		implements Icon
	{
		private final Icon icon;
		private final int iconWidth;
		private final int iconHeight;

		ShortcutIcon( Icon icon, int iconWidth, int iconHeight ) {
			this.icon = icon;
			this.iconWidth = iconWidth;
			this.iconHeight = iconHeight;
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				// set rendering hint for the case that the icon is a bitmap (not used for vector icons)
				g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );

				double scale = (double) getIconWidth() / (double) icon.getIconWidth();
				g2.translate( x, y );
				g2.scale( scale, scale );

				icon.paintIcon( c, g2, 0, 0 );
			} finally {
				g2.dispose();
			}
		}

		@Override
		public int getIconWidth() {
			return UIScale.scale( iconWidth );
		}

		@Override
		public int getIconHeight() {
			return UIScale.scale( iconHeight );
		}
	}
}
