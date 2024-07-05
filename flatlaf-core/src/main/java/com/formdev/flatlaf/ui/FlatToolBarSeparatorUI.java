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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToolBar.Separator}.
 *
 * <!-- FlatToolBarSeparatorUI -->
 *
 * @uiDefault ToolBar.separatorWidth				int
 * @uiDefault ToolBar.separatorColor				Color
 *
 * @author Karl Tauber
 */
public class FlatToolBarSeparatorUI
	extends BasicToolBarSeparatorUI
	implements StyleableUI, PropertyChangeListener
{
	private static final int LINE_WIDTH = 1;

	@Styleable protected int separatorWidth;
	@Styleable protected Color separatorColor;

	private final boolean shared;
	private boolean defaults_initialized = false;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatToolBarSeparatorUI.class, () -> new FlatToolBarSeparatorUI( true ) )
			: new FlatToolBarSeparatorUI( false );
	}

	/** @since 2 */
	protected FlatToolBarSeparatorUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (JSeparator) c );
	}

	@Override
	protected void installDefaults( JSeparator c ) {
		super.installDefaults( c );

		if( !defaults_initialized ) {
			separatorWidth = UIManager.getInt( "ToolBar.separatorWidth" );
			separatorColor = UIManager.getColor( "ToolBar.separatorColor" );

			defaults_initialized = true;
		}

		// necessary for vertical toolbars if separator size was set using setSeparatorSize()
		// (otherwise there will be a gap on the left side of the vertical toolbar)
		c.setAlignmentX( 0 );
	}

	@Override
	protected void uninstallDefaults( JSeparator s ) {
		super.uninstallDefaults( s );

		defaults_initialized = false;
		oldStyleValues = null;
	}

	@Override
	protected void installListeners( JSeparator s ) {
		super.installListeners( s );

		s.addPropertyChangeListener( this );
	}

	@Override
	protected void uninstallListeners( JSeparator s ) {
		super.uninstallListeners( s );

		s.removePropertyChangeListener( this );
	}

	/** @since 2.0.1 */
	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.STYLE:
			case FlatClientProperties.STYLE_CLASS:
				JSeparator s = (JSeparator) e.getSource();
				if( shared && FlatStylingSupport.hasStyleProperty( s ) ) {
					// unshare component UI if necessary
					// updateUI() invokes installStyle() from installUI()
					s.updateUI();
				} else
					installStyle( s );
				s.revalidate();
				HiDPIUtils.repaint( s );
				break;
		}
	}

	/** @since 2 */
	protected void installStyle( JSeparator s ) {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( s, "ToolBarSeparator" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObject( this, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension size = ((JToolBar.Separator)c).getSeparatorSize();

		if( size != null )
			return scale( size );

		// get separator width
		int separatorWidth = this.separatorWidth;
		FlatToolBarUI toolBarUI = getToolBarUI( c );
		if( toolBarUI != null && toolBarUI.separatorWidth != null )
			separatorWidth = toolBarUI.separatorWidth;

		// make sure that gap on left and right side of line have same size
		int sepWidth = (scale( (separatorWidth - LINE_WIDTH) / 2 ) * 2) + scale( LINE_WIDTH );

		boolean vertical = isVertical( c );
		return new Dimension( vertical ? sepWidth : 0, vertical ? 0 : sepWidth );
	}

	@Override
	public Dimension getMaximumSize( JComponent c ) {
		Dimension size = getPreferredSize( c );
		if( isVertical( c ) )
			return new Dimension( size.width, Short.MAX_VALUE );
		else
			return new Dimension( Short.MAX_VALUE, size.height );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		int width = c.getWidth();
		int height = c.getHeight();
		float lineWidth = scale( 1f );
		float offset = scale( 2f );

		// get separator color
		Color separatorColor = this.separatorColor;
		FlatToolBarUI toolBarUI = getToolBarUI( c );
		if( toolBarUI != null && toolBarUI.separatorColor != null )
			separatorColor = toolBarUI.separatorColor;

		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		g.setColor( separatorColor );

		if( isVertical( c ) )
			((Graphics2D)g).fill( new Rectangle2D.Float( Math.round( (width - lineWidth) / 2f ), offset, lineWidth, height - (offset * 2) ) );
		else
			((Graphics2D)g).fill( new Rectangle2D.Float( offset, Math.round( (height - lineWidth) / 2f ), width - (offset * 2), lineWidth ) );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	private boolean isVertical( JComponent c ) {
		return ((JToolBar.Separator)c).getOrientation() == SwingConstants.VERTICAL;
	}

	private FlatToolBarUI getToolBarUI( JComponent c ) {
		Container parent = c.getParent();
		return (parent instanceof JToolBar && ((JToolBar)parent).getUI() instanceof FlatToolBarUI)
			? (FlatToolBarUI) ((JToolBar)parent).getUI()
			: null;
	}
}
