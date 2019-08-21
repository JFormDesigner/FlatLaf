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
import java.awt.Paint;
import javax.swing.UIManager;

/**
 * Border for {@link javax.swing.JButton}.
 *
 * @author Karl Tauber
 */
public class FlatButtonBorder
	extends FlatBorder
{
	@Override
	public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
		if( FlatButtonUI.isContentAreaFilled( c ) )
			super.paintBorder( c, g, x, y, width, height );
	}

	@Override
	protected Color getFocusColor( Component c ) {
		return UIManager.getColor( FlatButtonUI.isDefaultButton( c )
			? "Button.default.focusColor"
			: "Component.focusColor" );
	}

	@Override
	protected Paint getBorderColor( Component c ) {
		if( c.isEnabled() ) {
			boolean def = FlatButtonUI.isDefaultButton( c );
			if( c.hasFocus() )
				return UIManager.getColor( def ? "Button.default.focusedBorderColor" : "Button.focusedBorderColor" );

			Color startColor = UIManager.getColor( def ? "Button.default.startBorderColor" : "Button.startBorderColor" );
			Color endColor = UIManager.getColor( def ? "Button.default.endBorderColor" : "Button.endBorderColor" );
			return (startColor.equals( endColor ) )
				? startColor
				: new GradientPaint( 0, getFocusWidth(), startColor,
					0, c.getHeight() - getFocusWidth() - 1f, endColor );
		} else
			return UIManager.getColor( "Button.disabledBorderColor" );
	}

	@Override
	protected float getArc() {
		return FlatUIUtils.getButtonArc();
	}
}
