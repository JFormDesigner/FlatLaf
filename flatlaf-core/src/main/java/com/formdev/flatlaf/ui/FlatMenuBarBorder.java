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
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Map;
import javax.swing.JMenuBar;
import javax.swing.UIManager;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableBorder;

/**
 * Border for {@link javax.swing.JMenuBar}.
 *
 * @uiDefault MenuBar.borderColor			Color
 *
 * @author Karl Tauber
 */
public class FlatMenuBarBorder
	extends FlatMarginBorder
	implements StyleableBorder
{
	@Styleable protected Color borderColor = UIManager.getColor( "MenuBar.borderColor" );

	/** @since 2 */
	@Override
	public Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
	}

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
		if( !showBottomSeparator( c ) )
			return;

		float lineHeight = scale( (float) 1 );
		FlatUIUtils.paintFilledRectangle( g, borderColor, x, y + height - lineHeight, width, lineHeight );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		// BasicBorders.MarginBorder does not handle JMenuBar margin
		Insets margin = (c instanceof JMenuBar) ? ((JMenuBar)c).getMargin() : new Insets( 0, 0, 0, 0 );

		insets.top = scale( margin.top );
		insets.left = scale( margin.left );
		insets.bottom = scale( margin.bottom + 1 );
		insets.right = scale( margin.right );
		return insets;
	}

	/** @since 2 */
	protected boolean showBottomSeparator( Component c ) {
		return !FlatMenuBarUI.useUnifiedBackground( c );
	}
}
