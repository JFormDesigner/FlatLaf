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

import static com.formdev.flatlaf.util.UIScale.scale2;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.util.function.Function;
import javax.swing.JComponent;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Support for MigLayout visual paddings.
 *
 * Visual paddings are used by MigLayout to ignore the usually invisible space
 * around some components (e.g. buttons) that is used to paint a focus border.
 *
 * @author Karl Tauber
 */
public class MigLayoutVisualPadding
{
	/**
	 * Key of visual padding client property.
	 * Value must be either an integer array of size 4, or java.awt.Insets.
	 *
	 * Same as net.miginfocom.layout.PlatformDefaults.VISUAL_PADDING_PROPERTY,
	 * but we don't want to depend on miglayout library.
	 */
	public static String VISUAL_PADDING_PROPERTY = "visualPadding";

	private static final FlatMigInsets ZERO = new FlatMigInsets( 0, 0, 0, 0 );
	private static final boolean migLayoutAvailable;

	static {
		// check whether MigLayout is available
		boolean available = false;
		try {
			Class.forName( "net.miginfocom.swing.MigLayout" );
			available = true;
		} catch( ClassNotFoundException ex ) {
			// ignore
		}
		migLayoutAvailable = available;
	}

	/**
	 * Sets the client property to the given insets.
	 */
	public static void install( JComponent c, Insets insets ) {
		if( !migLayoutAvailable )
			return;

		setVisualPadding( c, insets );
	}

	/**
	 * Convenience method that checks whether component border is a FlatBorder.
	 */
	public static void install( JComponent c ) {
		if( !migLayoutAvailable )
			return;

		install( c, c2 -> {
			FlatBorder border = FlatUIUtils.getOutsideFlatBorder( c2 );
			if( border != null ) {
				int focusWidth = border.getFocusWidth( c2 );
				return new Insets( focusWidth, focusWidth, focusWidth, focusWidth );
			} else
				return null;
		}, "border", FlatClientProperties.STYLE, FlatClientProperties.STYLE_CLASS );
	}

	/**
	 * Invokes the given function to retrieve the actual visual paddings and sets
	 * the client property. Also adds property change listener to component and
	 * re-invokes the function if one of the given properties have changed.
	 */
	public static void install( JComponent c, Function<JComponent, Insets> getPaddingFunction, String... propertyNames ) {
		if( !migLayoutAvailable )
			return;

		// set client property
		setVisualPadding( c, getPaddingFunction.apply( c ) );

		// add listener
		c.addPropertyChangeListener( (FlatMigListener) e -> {
			String propertyName = e.getPropertyName();
			for( String name : propertyNames ) {
				if( name.equals( propertyName ) ) {
					setVisualPadding( c, getPaddingFunction.apply( c ) );
					break;
				}
			}
		} );
	}

	private static void setVisualPadding( JComponent c, Insets visualPadding ) {
		Object oldPadding = c.getClientProperty( VISUAL_PADDING_PROPERTY );
		if( oldPadding == null || oldPadding instanceof FlatMigInsets ) {
			FlatMigInsets flatVisualPadding = (visualPadding != null)
				? new FlatMigInsets( scale2( visualPadding.top ), scale2( visualPadding.left ),
					scale2( visualPadding.bottom ), scale2( visualPadding.right ) )
				: ZERO;

			c.putClientProperty( VISUAL_PADDING_PROPERTY, flatVisualPadding );
		}
	}

	/**
	 * Removes listeners and restores client property.
	 */
	public static void uninstall( JComponent c ) {
		if( !migLayoutAvailable )
			return;

		// remove listener
		for( PropertyChangeListener l : c.getPropertyChangeListeners() ) {
			if( l instanceof FlatMigListener ) {
				c.removePropertyChangeListener( l );
				break;
			}
		}

		// remove client property
		if( c.getClientProperty( VISUAL_PADDING_PROPERTY ) instanceof FlatMigInsets )
			c.putClientProperty( VISUAL_PADDING_PROPERTY, null );
	}

	//---- class FlatMigInsets ------------------------------------------------

	/**
	 * Marker class to identify our visual paddings and leave paddings
	 * set from outside untouched.
	 */
	private static class FlatMigInsets
		extends Insets
	{
		FlatMigInsets( int top, int left, int bottom, int right ) {
			super( top, left, bottom, right );
		}
	}

	//---- class FlatMigListener ----------------------------------------------

	/**
	 * Marker interface needed for listener removal.
	 */
	private interface FlatMigListener
		extends PropertyChangeListener
	{
	}
}
