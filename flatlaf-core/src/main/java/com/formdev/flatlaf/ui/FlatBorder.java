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

import static com.formdev.flatlaf.util.UIScale.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;

/**
 * Border for various components (e.g. {@link javax.swing.JTextField}).
 *
 * There is empty space around the component border, if Component.focusWidth is greater than zero,
 * which is used to paint focus border.
 *
 * Because there is empty space (if focus border is not painted),
 * UI delegates that use this border (or subclasses) must invoke
 * {@link FlatUIUtils#paintParentBackground} to paint the empty space correctly.
 *
 * @uiDefault Component.focusWidth			int
 * @uiDefault Component.innerFocusWidth		int
 * @uiDefault Component.focusColor			Color
 * @uiDefault Component.borderColor			Color
 * @uiDefault Component.disabledBorderColor	Color
 * @uiDefault Component.focusedBorderColor	Color
 *
 * @author Karl Tauber
 */
public class FlatBorder
	extends BasicBorders.MarginBorder
{
	protected final int focusWidth = UIManager.getInt( "Component.focusWidth" );
	protected final int innerFocusWidth = UIManager.getInt( "Component.innerFocusWidth" );
	protected final Color focusColor = UIManager.getColor( "Component.focusColor" );
	protected final Color borderColor = UIManager.getColor( "Component.borderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "Component.focusedBorderColor" );

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			boolean isCellEditor = isTableCellEditor( c );
			float focusWidth = isCellEditor ? 0 : getFocusWidth();
			float borderWidth = getBorderWidth( c );
			float arc = isCellEditor ? 0 : getArc();

			if( isFocused( c ) ) {
				g2.setColor( getFocusColor( c ) );
				FlatUIUtils.paintOutlineBorder( g2, x, y, width, height, focusWidth,
					getLineWidth() + scale( (float) innerFocusWidth ), arc );
			}

			g2.setColor( getBorderColor( c ) );
			FlatUIUtils.drawRoundRectangle( g2, x, y, width, height, focusWidth, borderWidth, arc );
		} finally {
			g2.dispose();
		}
	}

	protected Color getFocusColor( Component c ) {
		return focusColor;
	}

	protected Color getBorderColor( Component c ) {
		boolean enabled = c.isEnabled() && (!(c instanceof JTextComponent) || ((JTextComponent)c).isEditable());
		return enabled
			? (isFocused( c ) ? focusedBorderColor : borderColor)
			: disabledBorderColor;
	}

	protected boolean isFocused( Component c ) {
		if( c instanceof JScrollPane ) {
			JViewport viewport = ((JScrollPane)c).getViewport();
			Component view = (viewport != null) ? viewport.getView() : null;
			if( view != null ) {
				if( view.hasFocus() )
					return true;

				if( (view instanceof JTable && ((JTable)view).isEditing()) ||
					(view instanceof JTree && ((JTree)view).isEditing()) )
				{
					Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
					if( focusOwner != null )
						return SwingUtilities.isDescendingFrom( focusOwner, view );
				}
			}
			return false;
		} else if( c instanceof JComboBox && ((JComboBox<?>)c).isEditable() ) {
			Component editorComponent = ((JComboBox<?>)c).getEditor().getEditorComponent();
			return (editorComponent != null) ? editorComponent.hasFocus() : false;
		} else if( c instanceof JSpinner ) {
			JComponent editor = ((JSpinner)c).getEditor();
			if( editor instanceof JSpinner.DefaultEditor ) {
				JTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
				if( textField != null )
					return textField.hasFocus();
			}
			return false;
		} else
			return c.hasFocus();
	}

	protected boolean isTableCellEditor( Component c ) {
		return FlatUIUtils.isTableCellEditor( c );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		boolean isCellEditor = isTableCellEditor( c );
		float ow = (isCellEditor ? 0 : getFocusWidth()) + getLineWidth();

		insets = super.getBorderInsets( c, insets );
		insets.top = Math.round( scale( (float) insets.top ) + ow );
		insets.left = Math.round( scale( (float) insets.left ) + ow );
		insets.bottom = Math.round( scale( (float) insets.bottom ) + ow );
		insets.right = Math.round( scale( (float) insets.right ) + ow );
		return insets;
	}

	protected float getFocusWidth() {
		return scale( (float) focusWidth );
	}

	protected float getLineWidth() {
		return scale( 1f );
	}

	protected float getBorderWidth( Component c ) {
		return getLineWidth();
	}

	protected float getArc() {
		return 0;
	}
}
