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
import java.awt.Paint;
import javax.swing.UIManager;

/**
 * Border for {@link javax.swing.JButton}.
 *
 * @uiDefault Button.startBorderColor			Color
 * @uiDefault Button.endBorderColor				Color
 * @uiDefault Button.disabledBorderColor		Color
 * @uiDefault Button.focusedBorderColor			Color
 * @uiDefault Button.default.startBorderColor	Color
 * @uiDefault Button.default.endBorderColor		Color
 * @uiDefault Button.default.focusedBorderColor	Color
 * @uiDefault Button.default.focusColor			Color
 * @uiDefault Button.arc						int
 *
 * @author Karl Tauber
 */
public class FlatButtonBorder
	extends FlatBorder
{
	protected final Color startBorderColor = UIManager.getColor( "Button.startBorderColor" );
	protected final Color endBorderColor = UIManager.getColor( "Button.endBorderColor" );
	protected final Color disabledBorderColor = UIManager.getColor( "Button.disabledBorderColor" );
	protected final Color focusedBorderColor = UIManager.getColor( "Button.focusedBorderColor" );
	protected final Color defaultStartBorderColor = UIManager.getColor( "Button.default.startBorderColor" );
	protected final Color defaultEndBorderColor = UIManager.getColor( "Button.default.endBorderColor" );
	protected final Color defaultFocusedBorderColor = UIManager.getColor( "Button.default.focusedBorderColor" );
	protected final Color defaultFocusColor = UIManager.getColor( "Button.default.focusColor" );
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
		if( c.isEnabled() ) {
			boolean def = FlatButtonUI.isDefaultButton( c );
			if( c.hasFocus() )
				return def ? defaultFocusedBorderColor : focusedBorderColor;

			Color startColor = def ? defaultStartBorderColor : startBorderColor;
			Color endColor = def ? defaultEndBorderColor : endBorderColor;
			return (startColor.equals( endColor ) )
				? startColor
				: new GradientPaint( 0, getFocusWidth(), startColor,
					0, c.getHeight() - getFocusWidth() - 1f, endColor );
		} else
			return disabledBorderColor;
	}

	@Override
	protected float getArc() {
		return scale( (float) arc );
	}
}
