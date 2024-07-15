/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPanel}.
 *
 * <!-- BasicPanelUI -->
 *
 * @uiDefault Panel.font				Font	unused
 * @uiDefault Panel.background			Color	only used if opaque
 * @uiDefault Panel.foreground			Color	unused
 * @uiDefault Panel.border				Border
 *
 * @author Karl Tauber
 */
public class FlatPanelUI
	extends BasicPanelUI
	implements StyleableUI, PropertyChangeListener
{
	// only used via styling (not in UI defaults)
	/** @since 2 */ @Styleable protected int arc = -1;

	private final boolean shared;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return FlatUIUtils.canUseSharedUI( c )
			? FlatUIUtils.createSharedUI( FlatPanelUI.class, () -> new FlatPanelUI( true ) )
			: new FlatPanelUI( false );
	}

	/** @since 2 */
	protected FlatPanelUI( boolean shared ) {
		this.shared = shared;
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		c.addPropertyChangeListener( this );
		if( c.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER ) != null )
			FullWindowContentSupport.registerPlaceholder( c );

		installStyle( (JPanel) c );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		c.removePropertyChangeListener( this );
		if( c.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER ) != null )
			FullWindowContentSupport.unregisterPlaceholder( c );

		oldStyleValues = null;
	}

	@Override
	protected void installDefaults( JPanel p ) {
		super.installDefaults( p );

		if( p.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER ) != null )
			LookAndFeel.installProperty( p, "opaque", false );
	}

	/** @since 2.0.1 */
	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.STYLE:
			case FlatClientProperties.STYLE_CLASS:
				JPanel c = (JPanel) e.getSource();
				if( shared && FlatStylingSupport.hasStyleProperty( c ) ) {
					// unshare component UI if necessary
					// updateUI() invokes installStyle() from installUI()
					c.updateUI();
				} else
					installStyle( c );
				c.revalidate();
				HiDPIUtils.repaint( c );
				break;

			case FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER:
				JPanel p = (JPanel) e.getSource();
				if( e.getOldValue() != null )
					FullWindowContentSupport.unregisterPlaceholder( p );
				if( e.getNewValue() != null )
					FullWindowContentSupport.registerPlaceholder( p );

				// make panel non-opaque for placeholders
				LookAndFeel.installProperty( p, "opaque", e.getNewValue() == null );
				break;
		}
	}

	/** @since 2 */
	protected void installStyle( JPanel c ) {
		try {
			applyStyle( c, FlatStylingSupport.getResolvedStyle( c, "Panel" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( JPanel c, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( c, key, value ) );
	}

	/** @since 2 */
	protected Object applyStyleProperty( JPanel c, String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, c, key, value );
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
	public void update( Graphics g, JComponent c ) {
		fillRoundedBackground( g, c, arc );
		paint( g, c );
	}

	/** @since 3.5 */
	public static void fillRoundedBackground( Graphics g, JComponent c, int arc ) {
		if( arc < 0 ) {
			Border border = c.getBorder();
			arc = ((border instanceof FlatLineBorder)
				? ((FlatLineBorder)border).getArc()
				: 0);
		}

		// fill background
		if( c.isOpaque() ) {
			if( arc > 0 ) {
				// fill background with parent color to avoid garbage in rounded corners
				FlatUIUtils.paintParentBackground( g, c );
			} else {
				g.setColor( c.getBackground() );
				g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
			}
		}

		// fill rounded rectangle if having rounded corners
		if( arc > 0 ) {
			g.setColor( c.getBackground() );
			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
			FlatUIUtils.paintComponentBackground( (Graphics2D) g, 0, 0, c.getWidth(), c.getHeight(),
				0, UIScale.scale( arc ) );
			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Object value = c.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER );
		if( value != null )
			return FullWindowContentSupport.getPlaceholderPreferredSize( c, (String) value );

		return super.getPreferredSize( c );
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		if( c.getClientProperty( FlatClientProperties.FULL_WINDOW_CONTENT_BUTTONS_PLACEHOLDER ) != null )
			FullWindowContentSupport.debugPaint( g, c );
	}
}
