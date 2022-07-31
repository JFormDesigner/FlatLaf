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
import java.awt.Paint;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicBorders;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableBorder;
import com.formdev.flatlaf.util.DerivedColor;

/**
 * Border for various components (e.g. {@link javax.swing.JTextField}).
 * <p>
 * There is empty space around the component border, if Component.focusWidth is greater than zero,
 * which is used to paint outer focus border.
 * <p>
 * Because there is empty space (if outer focus border is not painted),
 * UI delegates that use this border (or subclasses) must invoke
 * {@link FlatUIUtils#paintParentBackground} to fill the empty space correctly.
 *
 * @uiDefault Component.focusWidth						int
 * @uiDefault Component.innerFocusWidth					int or float
 * @uiDefault Component.innerOutlineWidth				int or float
 * @uiDefault Component.borderWidth						int or float
 *
 * @uiDefault Component.focusColor						Color
 * @uiDefault Component.borderColor						Color
 * @uiDefault Component.disabledBorderColor				Color
 * @uiDefault Component.focusedBorderColor				Color
 *
 * @uiDefault Component.error.borderColor				Color
 * @uiDefault Component.error.focusedBorderColor		Color
 * @uiDefault Component.warning.borderColor				Color
 * @uiDefault Component.warning.focusedBorderColor		Color
 * @uiDefault Component.custom.borderColor				Color
 *
 * @author Karl Tauber
 */
public class FlatBorder
	extends BasicBorders.MarginBorder
	implements StyleableBorder
{
	@Styleable protected int focusWidth = UIManager.getInt( "Component.focusWidth" );
	@Styleable protected float innerFocusWidth = FlatUIUtils.getUIFloat( "Component.innerFocusWidth", 0 );
	@Styleable protected float innerOutlineWidth = FlatUIUtils.getUIFloat( "Component.innerOutlineWidth", 0 );
	/** @since 2 */ @Styleable protected float borderWidth = FlatUIUtils.getUIFloat( "Component.borderWidth", 1 );

	@Styleable protected Color focusColor = UIManager.getColor( "Component.focusColor" );
	@Styleable protected Color borderColor = UIManager.getColor( "Component.borderColor" );
	@Styleable protected Color disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );
	@Styleable protected Color focusedBorderColor = UIManager.getColor( "Component.focusedBorderColor" );

	@Styleable(dot=true) protected Color errorBorderColor = UIManager.getColor( "Component.error.borderColor" );
	@Styleable(dot=true) protected Color errorFocusedBorderColor = UIManager.getColor( "Component.error.focusedBorderColor" );
	@Styleable(dot=true) protected Color warningBorderColor = UIManager.getColor( "Component.warning.borderColor" );
	@Styleable(dot=true) protected Color warningFocusedBorderColor = UIManager.getColor( "Component.warning.focusedBorderColor" );
	@Styleable(dot=true) protected Color customBorderColor = UIManager.getColor( "Component.custom.borderColor" );

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected String outline;
	/** @since 2 */ @Styleable protected Color outlineColor;
	/** @since 2 */ @Styleable protected Color outlineFocusedColor;

	/** @since 2 */
	@Override
	public Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos() {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			float focusWidth = scale( (float) getFocusWidth( c ) );
			float focusInnerWidth = 0;
			float borderWidth = scale( getBorderWidth( c ) );
			float arc = scale( (float) getArc( c ) );
			Color outlineColor = getOutlineColor( c );
			Color focusColor = null;

			// paint outer border
			if( outlineColor != null || isFocused( c ) ) {
				float innerWidth = !isCellEditor( c ) && !(c instanceof JScrollPane)
					? (outlineColor != null ? innerOutlineWidth : getInnerFocusWidth( c ))
					: 0;

				if( focusWidth > 0 || innerWidth > 0 ) {
					focusColor = (outlineColor != null) ? outlineColor : getFocusColor( c );
					focusInnerWidth = borderWidth + scale( innerWidth );
				}
			}

			// paint border
			Paint borderColor = (outlineColor != null) ? outlineColor : getBorderColor( c );
			FlatUIUtils.paintOutlinedComponent( g2, x, y, width, height,
				focusWidth, 1, focusInnerWidth, borderWidth, arc,
				focusColor, borderColor, null );
		} finally {
			g2.dispose();
		}
	}

	/**
	 * Returns the outline color of the component border specified in client property
	 * {@link FlatClientProperties#OUTLINE}.
	 */
	protected Color getOutlineColor( Component c ) {
		if( !(c instanceof JComponent) )
			return null;

		Object outline = ((JComponent)c).getClientProperty( FlatClientProperties.OUTLINE );
		if( outline == null )
			outline = this.outline;
		if( outline == null ) {
			if( outlineColor != null && outlineFocusedColor != null )
				outline = new Color[] { outlineFocusedColor, outlineColor };
			else if( outlineColor != null )
				outline = outlineColor;
			else if( outlineFocusedColor != null )
				outline = outlineFocusedColor;
		}

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

		return c.isEnabled();
	}

	protected boolean isFocused( Component c ) {
		if( c instanceof JScrollPane )
			return FlatScrollPaneUI.isPermanentFocusOwner( (JScrollPane) c );
		else if( c instanceof JComboBox )
			return FlatComboBoxUI.isPermanentFocusOwner( (JComboBox<?>) c );
		else if( c instanceof JSpinner )
			return FlatSpinnerUI.isPermanentFocusOwner( (JSpinner) c );
		else
			return FlatUIUtils.isPermanentFocusOwner( c );
	}

	protected boolean isCellEditor( Component c ) {
		return FlatUIUtils.isCellEditor( c );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		float focusWidth = scale( (float) getFocusWidth( c ) );
		int ow = Math.round( focusWidth + scale( (float) getLineWidth( c ) ) );

		insets = super.getBorderInsets( c, insets );

		insets.top = scale( insets.top ) + ow;
		insets.left = scale( insets.left ) + ow;
		insets.bottom = scale( insets.bottom ) + ow;
		insets.right = scale( insets.right ) + ow;

		if( isCellEditor( c ) ) {
			// remove top and bottom insets if used as cell editor
			insets.top = insets.bottom = 0;

			// remove right/left insets to avoid that text is truncated (e.g. in file chooser)
			if( c.getComponentOrientation().isLeftToRight() )
				insets.right = 0;
			else
				insets.left = 0;
		}

		return insets;
	}

	/**
	 * Returns the (unscaled) thickness of the outer focus border.
	 */
	protected int getFocusWidth( Component c ) {
		if( isCellEditor( c ) )
			return 0;

		return focusWidth;
	}

	/**
	 * Returns the (unscaled) thickness of the inner focus border.
	 */
	protected float getInnerFocusWidth( Component c ) {
		return innerFocusWidth;
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
	protected float getBorderWidth( Component c ) {
		return borderWidth;
	}

	/**
	 * Returns the (unscaled) arc diameter of the border.
	 */
	protected int getArc( Component c ) {
		return 0;
	}
}
