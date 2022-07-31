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
import java.awt.Component;
import java.awt.Insets;
import javax.swing.plaf.BorderUIResource;

/**
 * Empty border for various components.
 *
 * The insets are scaled.
 *
 * @author Karl Tauber
 */
public class FlatEmptyBorder
	extends BorderUIResource.EmptyBorderUIResource
{
	public FlatEmptyBorder() {
		super( 0, 0, 0, 0 );
	}

	public FlatEmptyBorder( int top, int left, int bottom, int right ) {
		super( top, left, bottom, right );
	}

	public FlatEmptyBorder( Insets insets ) {
		super( insets );
	}

	@Override
	public Insets getBorderInsets() {
		return new Insets( scale( top ), scale( left ), scale( bottom ), scale( right ) );
	}

	@Override
	public Insets getBorderInsets( Component c, Insets insets ) {
		return scaleInsets( c, insets, top, left, bottom, right );
	}

	protected static Insets scaleInsets( Component c, Insets insets,
		int top, int left, int bottom, int right )
	{
		boolean leftToRight = left == right || c == null || c.getComponentOrientation().isLeftToRight();
		insets.left = scale( leftToRight ? left : right );
		insets.top = scale( top );
		insets.right = scale( leftToRight ? right : left );
		insets.bottom = scale( bottom );
		return insets;
	}

	public Insets getUnscaledBorderInsets() {
		return super.getBorderInsets();
	}

	public Object applyStyleProperty( Insets insets ) {
		Insets oldInsets = getUnscaledBorderInsets();
		top = insets.top;
		left = insets.left;
		bottom = insets.bottom;
		right = insets.right;
		return oldInsets;
	}

	/** @since 2.5 */
	public Insets getStyleableValue() {
		return new Insets( top, left, bottom, right );
	}
}
