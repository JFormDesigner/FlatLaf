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

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import com.formdev.flatlaf.util.UIScale;

/**
 * Border for {@link javax.swing.JPopupMenu}.
 *
 * @uiDefault PopupMenu.borderInsets			Insets
 * @uiDefault PopupMenu.borderColor				Color
 *
 * @author Karl Tauber
 */
public class FlatPopupMenuBorder
	extends FlatLineBorder
{
	public FlatPopupMenuBorder() {
		super( UIManager.getInsets( "PopupMenu.borderInsets" ),
			UIManager.getColor( "PopupMenu.borderColor" ) );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		if( c instanceof Container &&
			((Container)c).getComponentCount() > 0 &&
			((Container)c).getComponent( 0 ) instanceof JScrollPane )
		{
			// e.g. for combobox popups
			insets.left = insets.top = insets.right = insets.bottom = UIScale.scale( 1 );
			return insets;
		}

		return super.getBorderInsets( c, insets );
	}
}
