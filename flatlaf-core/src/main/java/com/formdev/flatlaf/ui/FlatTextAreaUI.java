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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTextArea}.
 *
 * <!-- BasicTextAreaUI -->
 *
 * @uiDefault TextArea.font						Font
 * @uiDefault TextArea.background				Color
 * @uiDefault TextArea.foreground				Color	also used if not editable
 * @uiDefault TextArea.caretForeground			Color
 * @uiDefault TextArea.selectionBackground		Color
 * @uiDefault TextArea.selectionForeground		Color
 * @uiDefault TextArea.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault TextArea.border					Border
 * @uiDefault TextArea.margin					Insets
 * @uiDefault TextArea.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatTextAreaUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault TextArea.disabledBackground		Color	used if not enabled
 * @uiDefault TextArea.inactiveBackground		Color	used if not editable
 *
 * @author Karl Tauber
 */
public class FlatTextAreaUI
	extends BasicTextAreaUI
{
	protected int minimumWidth;
	protected boolean isIntelliJTheme;
	protected Color disabledBackground;
	protected Color inactiveBackground;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextAreaUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		disabledBackground = UIManager.getColor( "TextArea.disabledBackground" );
		inactiveBackground = UIManager.getColor( "TextArea.inactiveBackground" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		disabledBackground = null;
		inactiveBackground = null;
	}

	@Override
	protected void paintBackground( Graphics g ) {
		JTextComponent c = getComponent();

		Color background = c.getBackground();
		g.setColor( !(background instanceof UIResource)
			? background
			: (isIntelliJTheme && (!c.isEnabled() || !c.isEditable())
				? FlatUIUtils.getParentBackground( c )
				: (!c.isEnabled()
					? disabledBackground
					: (!c.isEditable() ? inactiveBackground : background))) );
		g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( super.getPreferredSize( c ), c );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( super.getMinimumSize( c ), c );
	}

	private Dimension applyMinimumWidth( Dimension size, JComponent c ) {
		// do not apply minimum width if JTextArea.columns is set
		if( c instanceof JTextArea && ((JTextArea)c).getColumns() > 0 )
			return size;

		// Assume that text area is in a scroll pane (that displays the border)
		// and subtract 1px border line width.
		// Using "(scale( 1 ) * 2)" instead of "scale( 2 )" to deal with rounding
		// issues. E.g. at scale factor 1.5 the first returns 4, but the second 3.
		size.width = Math.max( size.width, scale( minimumWidth ) - (scale( 1 ) * 2) );
		return size;
	}
}
