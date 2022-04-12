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

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.AbstractButton;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.util.UIScale;

/**
 * Border for {@link javax.swing.JButton}.
 *
 * @uiDefault Button.arc						int
 * @uiDefault Button.innerFocusWidth			int or float	optional; defaults to Component.innerFocusWidth
 * @uiDefault Button.borderWidth				int or float	optional; defaults to Component.borderWidth
 *
 * @uiDefault Button.borderColor				Color
 * @uiDefault Button.startBorderColor			Color	optional; if set, a gradient paint is used and Button.borderColor is ignored
 * @uiDefault Button.endBorderColor				Color	optional; if set, a gradient paint is used
 * @uiDefault Button.disabledBorderColor		Color
 * @uiDefault Button.focusedBorderColor			Color
 * @uiDefault Button.hoverBorderColor			Color	optional
 *
 * @uiDefault Button.default.borderWidth		int or float
 * @uiDefault Button.default.borderColor		Color
 * @uiDefault Button.default.startBorderColor	Color	optional; if set, a gradient paint is used and Button.default.borderColor is ignored
 * @uiDefault Button.default.endBorderColor		Color	optional; if set, a gradient paint is used
 * @uiDefault Button.default.focusedBorderColor	Color
 * @uiDefault Button.default.focusColor			Color
 * @uiDefault Button.default.hoverBorderColor	Color	optional
 *
 * @uiDefault Button.toolbar.focusWidth			int or float		optional; default is 1.5
 * @uiDefault Button.toolbar.focusColor			Color	optional; defaults to Component.focusColor
 * @uiDefault Button.toolbar.margin				Insets
 * @uiDefault Button.toolbar.spacingInsets		Insets
 *
 * @author Karl Tauber
 */
public class FlatButtonBorder
	extends FlatBorder
{
	@Styleable protected int arc = UIManager.getInt( "Button.arc" );

	protected Color endBorderColor = UIManager.getColor( "Button.endBorderColor" );
	@Styleable protected Color hoverBorderColor = UIManager.getColor( "Button.hoverBorderColor" );

	@Styleable(dot=true) protected float defaultBorderWidth = FlatUIUtils.getUIFloat( "Button.default.borderWidth", 1 );
	@Styleable(dot=true) protected Color defaultBorderColor = FlatUIUtils.getUIColor( "Button.default.startBorderColor", "Button.default.borderColor" );
	protected Color defaultEndBorderColor = UIManager.getColor( "Button.default.endBorderColor" );
	@Styleable(dot=true) protected Color defaultFocusedBorderColor = UIManager.getColor( "Button.default.focusedBorderColor" );
	@Styleable(dot=true) protected Color defaultFocusColor = UIManager.getColor( "Button.default.focusColor" );
	@Styleable(dot=true) protected Color defaultHoverBorderColor = UIManager.getColor( "Button.default.hoverBorderColor" );

	/** @since 1.4 */ @Styleable(dot=true) protected float toolbarFocusWidth = FlatUIUtils.getUIFloat( "Button.toolbar.focusWidth", 1.5f );
	/** @since 1.4 */ @Styleable(dot=true) protected Color toolbarFocusColor = UIManager.getColor( "Button.toolbar.focusColor" );
	@Styleable(dot=true) protected Insets toolbarMargin = UIManager.getInsets( "Button.toolbar.margin" );
	@Styleable(dot=true) protected Insets toolbarSpacingInsets = UIManager.getInsets( "Button.toolbar.spacingInsets" );

	public FlatButtonBorder() {
		innerFocusWidth = FlatUIUtils.getUIFloat( "Button.innerFocusWidth", innerFocusWidth );
		borderWidth = FlatUIUtils.getUIFloat( "Button.borderWidth", borderWidth );

		borderColor = FlatUIUtils.getUIColor( "Button.startBorderColor", "Button.borderColor" );
		disabledBorderColor = UIManager.getColor( "Button.disabledBorderColor" );
		focusedBorderColor = UIManager.getColor( "Button.focusedBorderColor" );
	}

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		if( FlatButtonUI.isContentAreaFilled( c ) &&
			!FlatButtonUI.isToolBarButton( c ) &&
			(!FlatButtonUI.isBorderlessButton( c ) || FlatUIUtils.isPermanentFocusOwner( c )) &&
			!FlatButtonUI.isHelpButton( c ) &&
			!FlatToggleButtonUI.isTabButton( c ) )
		  super.paintBorder( c, g, x, y, width, height );
		else if( FlatButtonUI.isToolBarButton( c ) && isFocused( c ) )
			paintToolBarFocus( c, g, x, y, width, height );
	}

	/** @since 1.4 */
	protected void paintToolBarFocus( Component c, Graphics g, int x, int y, int width, int height ) {
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			float focusWidth = UIScale.scale( toolbarFocusWidth );
			float arc = UIScale.scale( (float) getArc( c ) );
			Color outlineColor = getOutlineColor( c );

			Insets spacing = UIScale.scale( toolbarSpacingInsets );
			x += spacing.left;
			y += spacing.top;
			width -= spacing.left + spacing.right;
			height -= spacing.top + spacing.bottom;

			Color color = (outlineColor != null) ? outlineColor : getFocusColor( c );
			// not using focus border painting of paintOutlinedComponent() here
			// because its round edges look too "thick"
			FlatUIUtils.paintOutlinedComponent( g2, x, y, width, height, 0, 0, 0, focusWidth, arc, null, color, null );
		} finally {
			g2.dispose();
		}
	}

	@Override
	protected Color getFocusColor( Component c ) {
		return (toolbarFocusColor != null && FlatButtonUI.isToolBarButton( c ))
			? toolbarFocusColor
			: (FlatButtonUI.isDefaultButton( c ) ? defaultFocusColor : super.getFocusColor( c ));
	}

	@Override
	protected boolean isFocused( Component c ) {
		return FlatButtonUI.isFocusPainted( c ) && super.isFocused( c );
	}

	@Override
	protected Paint getBorderColor( Component c ) {
		boolean def = FlatButtonUI.isDefaultButton( c );
		Paint color = FlatButtonUI.buttonStateColor( c,
			def ? defaultBorderColor : borderColor,
			disabledBorderColor,
			def ? defaultFocusedBorderColor : focusedBorderColor,
			def ? defaultHoverBorderColor : hoverBorderColor,
			null );

		// change to gradient paint if start/end colors are specified
		Color startBg = def ? defaultBorderColor : borderColor;
		Color endBg = def ? defaultEndBorderColor : endBorderColor;
		if( color == startBg && endBg != null && !startBg.equals( endBg ) )
			color = new GradientPaint( 0, 0, startBg, 0, c.getHeight(), endBg );

		return color;
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		if( FlatButtonUI.isToolBarButton( c ) ) {
			// In toolbars, use button margin only if explicitly set.
			// Otherwise, use toolbar margin specified in UI defaults.
			Insets margin = (c instanceof AbstractButton)
				? ((AbstractButton)c).getMargin()
				: null;

			FlatUIUtils.setInsets( insets, UIScale.scale( FlatUIUtils.addInsets( toolbarSpacingInsets,
				(margin != null && !(margin instanceof UIResource)) ? margin : toolbarMargin ) ) );
		} else {
			insets = super.getBorderInsets( c, insets );

			// use smaller left and right insets for icon-only or single-character buttons (so that they are square)
			if( FlatButtonUI.isIconOnlyOrSingleCharacterButton( c ) && ((AbstractButton)c).getMargin() instanceof UIResource )
				insets.left = insets.right = Math.min( insets.top, insets.bottom );
		}

		return insets;
	}

	@Override
	protected int getFocusWidth( Component c ) {
		return FlatToggleButtonUI.isTabButton( c ) ? 0 : super.getFocusWidth( c );
	}

	@Override
	protected float getBorderWidth( Component c ) {
		return FlatButtonUI.isDefaultButton( c ) ? defaultBorderWidth : borderWidth;
	}

	@Override
	protected int getArc( Component c ) {
		if( isCellEditor( c ) )
			return 0;

		switch( FlatButtonUI.getButtonType( c ) ) {
			case FlatButtonUI.TYPE_SQUARE: return 0;
			case FlatButtonUI.TYPE_ROUND_RECT: return Short.MAX_VALUE;
			default: return arc;
		}
	}
}
