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

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
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
 * @author Karl Tauber
 */
public class FlatFileChooserUI
	extends MetalFileChooserUI
{
	public static ComponentUI createUI( JComponent c ) {
		return new FlatFileChooserUI( (JFileChooser) c );
	}

	public FlatFileChooserUI( JFileChooser filechooser ) {
		super( filechooser );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return UIScale.scale( super.getMinimumSize( c ) );
	}
}
