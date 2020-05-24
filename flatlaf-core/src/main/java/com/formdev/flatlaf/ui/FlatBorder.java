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
import java.awt.Paint;
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
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.DerivedColor;

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
 * @uiDefault Component.innerFocusWidth		int or float
 * @uiDefault Component.focusColor			Color
 * @uiDefault Component.borderColor			Color
 * @uiDefault Component.disabledBorderColor	Color
 * @uiDefault Component.focusedBorderColor	Color
 *
 * @uiDefault Component.error.borderColor			Color
 * @uiDefault Component.error.focusedBorderColor	Color
 * @uiDefault Component.warning.borderColor			Color
 * @uiDefault Component.warning.focusedBorderColor	Color
 * @uiDefault Component.custom.borderColor			Color
 *
 * @author Karl Tauber
 */
public class FlatBorder
	extends BasicBorders.MarginBorder
{
	protected final int focusWidth = UIManager.getInt( "Component.focusWidth" );
	protected final float innerFocusWidth = FlatUIUtils.getUIFloat( "Component.innerFocusWidth", 0 );
	protected final float innerOutlineWidth = FlatUIUtils.getUIFloat( "Component.innerOutlineWidth", 0 );
	protected final Color focusColor = UIManager.getColor( "Component.focusColor" );
	protected final Color borderColor = UIManager.getColor( "Component.borderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "Component.focusedBorderColor" );

	protected final Color errorBorderColor = UIManager.getColor( "Component.error.borderColor" );
	protected final Color errorFocusedBorderColor = UIManager.getColor( "Component.error.focusedBorderColor" );
	protected final Color warningBorderColor = UIManager.getColor( "Component.warning.borderColor" );
	protected final Color warningFocusedBorderColor = UIManager.getColor( "Component.warning.focusedBorderColor" );
	protected final Color customBorderColor = UIManager.getColor( "Component.custom.borderColor" );

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			boolean isCellEditor = isTableCellEditor( c );
			float focusWidth = isCellEditor ? 0 : scale( (float) getFocusWidth( c ) );
			float borderWidth = scale( (float) getBorderWidth( c ) );
			float arc = isCellEditor ? 0 : scale( (float) getArc( c ) );
			Color outlineColor = getOutlineColor( c );

			if( outlineColor != null || isFocused( c ) ) {
				float innerFocusWidth = !(c instanceof JScrollPane)
					? (outlineColor != null ? innerOutlineWidth : this.innerFocusWidth)
					: 0;

				g2.setColor( (outlineColor != null) ? outlineColor : getFocusColor( c ) );
				FlatUIUtils.paintComponentOuterBorder( g2, x, y, width, height, focusWidth,
					scale( (float) getLineWidth( c ) ) + scale( innerFocusWidth ), arc );
			}

			g2.setPaint( (outlineColor != null) ? outlineColor : getBorderColor( c ) );
			FlatUIUtils.paintComponentBorder( g2, x, y, width, height, focusWidth, borderWidth, arc );
		} finally {
			g2.dispose();
		}
	}

	protected Color getOutlineColor( Component c ) {
		if( !(c instanceof JComponent) )
			return null;

		Object outline = ((JComponent)c).getClientProperty( FlatClientProperties.OUTLINE );
		if( outline instanceof String ) {
			switch( (String) outline ) {
				case FlatClientProperties.OUTLINE_ERROR:
					return isFocused( c ) ? errorFocusedBorderColor : errorBorderColor;

				case FlatClientProperties.OUTLINE_WARNING:
					return isFocused( c ) ? warningFocusedBorderColor : warningBorderColor;
			}
		} else if( outline instanceof Color ) {
			Color color = (Color) outline;
			// use color functions to compute color for unfocused state
			if( !isFocused( c ) && customBorderColor instanceof DerivedColor )
				color = ((DerivedColor)customBorderColor).derive( color );
			return color;
		} else if( outline instanceof Color[] && ((Color[])outline).length >= 2 )
			return ((Color[])outline)[isFocused( c ) ? 0 : 1];

		return null;
	}

	protected Color getFocusColor( Component c ) {
		return focusColor;
	}

	protected Paint getBorderColor( Component c ) {
		return isEnabled( c )
			? (isFocused( c ) ? focusedBorderColor : borderColor)
			: disabledBorderColor;
	}

	protected boolean isEnabled( Component c ) {
		if( c instanceof JScrollPane ) {
			// check whether view component is disabled
			JViewport viewport = ((JScrollPane)c).getViewport();
			Component view = (viewport != null) ? viewport.getView() : null;
			if( view != null && !isEnabled( view ) )
				return false;
		}

		return c.isEnabled() && (!(c instanceof JTextComponent) || ((JTextComponent)c).isEditable());
	}

	protected boolean isFocused( Component c ) {
		if( c instanceof JScrollPane ) {
			JViewport viewport = ((JScrollPane)c).getViewport();
			Component view = (viewport != null) ? viewport.getView() : null;
			if( view != null ) {
				if( FlatUIUtils.isPermanentFocusOwner( view ) )
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
			return (editorComponent != null) ? FlatUIUtils.isPermanentFocusOwner( editorComponent ) : false;
		} else if( c instanceof JSpinner ) {
			if( FlatUIUtils.isPermanentFocusOwner( c ) )
				return true;

			JComponent editor = ((JSpinner)c).getEditor();
			if( editor instanceof JSpinner.DefaultEditor ) {
				JTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();
				if( textField != null )
					return FlatUIUtils.isPermanentFocusOwner( textField );
			}
			return false;
		} else
			return FlatUIUtils.isPermanentFocusOwner( c );
	}

	protected boolean isTableCellEditor( Component c ) {
		return FlatUIUtils.isTableCellEditor( c );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		boolean isCellEditor = isTableCellEditor( c );
		float focusWidth = isCellEditor ? 0 : scale( (float) getFocusWidth( c ) );
		float ow = focusWidth + scale( (float) getLineWidth( c ) );

		insets = super.getBorderInsets( c, insets );
		insets.top = Math.round( scale( (float) insets.top ) + ow );
		insets.left = Math.round( scale( (float) insets.left ) + ow );
		insets.bottom = Math.round( scale( (float) insets.bottom ) + ow );
		insets.right = Math.round( scale( (float) insets.right ) + ow );
		return insets;
	}

	/**
	 * Returns the (unscaled) thickness of the outer focus border.
	 */
	protected int getFocusWidth( Component c ) {
		return focusWidth;
	}

	/**
	 * Returns the (unscaled) line thickness used to compute the border insets.
	 * This may be different to {@link #getBorderWidth}.
	 */
	protected int getLineWidth( Component c ) {
		return 1;
	}

	/**
	 * Returns the (unscaled) line thickness used to paint the border.
	 * This may be different to {@link #getLineWidth}.
	 */
	protected int getBorderWidth( Component c ) {
		return getLineWidth( c );
	}

	/**
	 * Returns the (unscaled) arc diameter of the border.
	 */
	protected int getArc( Component c ) {
		return 0;
	}
}
