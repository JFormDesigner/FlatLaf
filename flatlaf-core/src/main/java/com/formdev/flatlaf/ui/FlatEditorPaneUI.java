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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.HiDPIUtils;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JEditorPane}.
 *
 * <!-- BasicEditorPaneUI -->
 *
 * @uiDefault EditorPane.font					Font
 * @uiDefault EditorPane.background				Color	also used if not editable
 * @uiDefault EditorPane.foreground				Color
 * @uiDefault EditorPane.caretForeground		Color
 * @uiDefault EditorPane.selectionBackground	Color
 * @uiDefault EditorPane.selectionForeground	Color
 * @uiDefault EditorPane.disabledBackground		Color	used if not enabled
 * @uiDefault EditorPane.inactiveBackground		Color	used if not editable
 * @uiDefault EditorPane.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault EditorPane.border					Border
 * @uiDefault EditorPane.margin					Insets
 * @uiDefault EditorPane.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatEditorPaneUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Component.isIntelliJTheme			boolean
 *
 * @author Karl Tauber
 */
public class FlatEditorPaneUI
	extends BasicEditorPaneUI
{
	protected int minimumWidth;
	protected boolean isIntelliJTheme;

	private Object oldHonorDisplayProperties;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatEditorPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );

		// use component font and foreground for HTML text
		oldHonorDisplayProperties = getComponent().getClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES );
		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, true );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		getComponent().putClientProperty( JEditorPane.HONOR_DISPLAY_PROPERTIES, oldHonorDisplayProperties );
	}

	@Override
	protected void propertyChange( PropertyChangeEvent e ) {
		super.propertyChange( e );
		propertyChange( getComponent(), e );
	}

	static void propertyChange( JTextComponent c, PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.MINIMUM_WIDTH:
				c.revalidate();
				break;
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( c, super.getPreferredSize( c ), minimumWidth );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( c, super.getMinimumSize( c ), minimumWidth );
	}

	static Dimension applyMinimumWidth( JComponent c, Dimension size, int minimumWidth ) {
		// Assume that text area is in a scroll pane (that displays the border)
		// and subtract 1px border line width.
		// Using "(scale( 1 ) * 2)" instead of "scale( 2 )" to deal with rounding
		// issues. E.g. at scale factor 1.5 the first returns 4, but the second 3.
		minimumWidth = FlatUIUtils.minimumWidth( c, minimumWidth );
		size.width = Math.max( size.width, scale( minimumWidth ) - (scale( 1 ) * 2) );
		return size;
	}

	@Override
	protected void paintSafely( Graphics g ) {
		super.paintSafely( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ) );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		JTextComponent c = getComponent();

		// for compatibility with IntelliJ themes
		if( isIntelliJTheme && (!c.isEnabled() || !c.isEditable()) && (c.getBackground() instanceof UIResource) ) {
			FlatUIUtils.paintParentBackground( g, c );
			return;
		}

		super.paintBackground( g );
	}
}
