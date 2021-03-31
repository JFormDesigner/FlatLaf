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
import java.awt.Insets;
import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import javax.swing.UIManager;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
import javax.swing.table.TableCellRenderer;
import com.formdev.flatlaf.FlatClientProperties;
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
 * @author Karl Tauber
 */
public class FlatFileChooserUI
	extends MetalFileChooserUI
{
	private final FlatFileView fileView = new FlatFileView();

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

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return UIScale.scale( super.getMinimumSize( c ) );
	}

	@Override
	public FileView getFileView( JFileChooser fc ) {
		return fileView;
	}

	@Override
	public void clearIconCache() {
		fileView.clearIconCache();
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

			// get system icon
			if( f != null ) {
				icon = getFileChooser().getFileSystemView().getSystemIcon( f );

				if( icon != null ) {
					if( icon instanceof ImageIcon )
						icon = new ScaledImageIcon( (ImageIcon) icon );
					cacheIcon( f, icon );
					return icon;
				}
			}

			// get default icon
			icon = super.getIcon( f );

			if( icon instanceof ImageIcon ) {
				icon = new ScaledImageIcon( (ImageIcon) icon );
				cacheIcon( f, icon );
			}

			return icon;
		}
	}
}
