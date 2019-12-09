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
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Paint;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

/**
 * Border for {@link javax.swing.JButton}.
 *
 * @uiDefault Button.borderColor				Color
 * @uiDefault Button.startBorderColor			Color	optional; if set, a gradient paint is used and Button.borderColor is ignored
 * @uiDefault Button.endBorderColor				Color	optional; if set, a gradient paint is used
 * @uiDefault Button.disabledBorderColor		Color
 * @uiDefault Button.focusedBorderColor			Color
 * @uiDefault Button.hoverBorderColor			Color	optional
 * @uiDefault Button.default.borderColor		Color
 * @uiDefault Button.default.startBorderColor	Color	optional; if set, a gradient paint is used and Button.default.borderColor is ignored
 * @uiDefault Button.default.endBorderColor		Color	optional; if set, a gradient paint is used
 * @uiDefault Button.default.hoverBorderColor	Color	optional
 * @uiDefault Button.default.focusedBorderColor	Color
 * @uiDefault Button.default.focusColor			Color
 * @uiDefault Button.default.borderWidth		int
 * @uiDefault Button.arc						int
 *
 * @author Karl Tauber
 */
public class FlatButtonBorder
	extends FlatBorder
{
	protected final Color borderColor = FlatUIUtils.getUIColor( "Button.startBorderColor", "Button.borderColor" );
	protected final Color endBorderColor = UIManager.getColor( "Button.endBorderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "Button.disabledBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "Button.focusedBorderColor" );
	protected final Color hoverBorderColor = UIManager.getColor( "Button.hoverBorderColor" );
	protected final Color defaultBorderColor = FlatUIUtils.getUIColor( "Button.default.startBorderColor", "Button.default.borderColor" );
	protected final Color defaultEndBorderColor = UIManager.getColor( "Button.default.endBorderColor" );
	protected final Color defaultHoverBorderColor = UIManager.getColor( "Button.default.hoverBorderColor" );
	protected final Color defaultFocusedBorderColor = UIManager.getColor( "Button.default.focusedBorderColor" );
	protected final Color defaultFocusColor = UIManager.getColor( "Button.default.focusColor" );
	protected final int defaultBorderWidth = UIManager.getInt( "Button.default.borderWidth" );
	protected final int arc = UIManager.getInt( "Button.arc" );

	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		if( FlatButtonUI.isContentAreaFilled( c ) && !FlatButtonUI.isHelpButton( c ) )
			super.paintBorder( c, g, x, y, width, height );
	}

	@Override
	protected Color getFocusColor( Component c ) {
		return FlatButtonUI.isDefaultButton( c ) ? defaultFocusColor : super.getFocusColor( c );
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
		insets = super.getBorderInsets( c, insets );

		// use smaller left and right insets for icon-only buttons (so that they are square)
		if( FlatButtonUI.isIconOnlyButton( c ) && ((JButton)c).getMargin() instanceof UIResource )
			insets.left = insets.right = Math.min( insets.top, insets.bottom );

		return insets;
	}

	@Override
	protected float getBorderWidth( Component c ) {
		return FlatButtonUI.isDefaultButton( c ) ? scale( (float) defaultBorderWidth ) : super.getBorderWidth( c );
	}

	@Override
	protected float getArc() {
		return scale( (float) arc );
	}
}
