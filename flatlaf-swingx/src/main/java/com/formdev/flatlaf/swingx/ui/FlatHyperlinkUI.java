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

package com.formdev.flatlaf.swingx.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import org.jdesktop.swingx.plaf.basic.BasicHyperlinkUI;
import com.formdev.flatlaf.ui.FlatButtonUI;
import com.formdev.flatlaf.ui.FlatHTML;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXHyperlink}.
 *
 * @uiDefault Hyperlink.disabledText				Color
 *
 * @author Karl Tauber
 */
public class FlatHyperlinkUI
	extends BasicHyperlinkUI
	implements StyleableUI
{
	/** @since 3.8 */ @Styleable protected Color disabledText;

	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatHyperlinkUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle( (AbstractButton) c );
	}

	@Override
	protected void installDefaults( AbstractButton b ) {
		super.installDefaults( b );

		disabledText = UIManager.getColor( "Hyperlink.disabledText" );
	}

	@Override
	protected void uninstallDefaults( AbstractButton b ) {
		super.uninstallDefaults( b );

		disabledText = null;

		oldStyleValues = null;
	}

	@Override
	protected void installListeners( AbstractButton b ) {
		super.installListeners( b );

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener( b, () -> installStyle( b ), null );
		b.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners( AbstractButton b ) {
		super.uninstallListeners( b );

		b.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	@Override
	protected BasicButtonListener createButtonListener( AbstractButton b ) {
		return new FlatHyperlinkListener( b );
	}

	/** @since 3.8 */
	protected void installStyle( AbstractButton b ) {
		try {
			applyStyle( b, FlatStylingSupport.getResolvedStyle( b, "JXHyperlink" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 3.8 */
	protected void applyStyle( AbstractButton b, Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style,
			(key, value) -> applyStyleProperty( b, key, value ) );
	}

	/** @since 3.8 */
	protected Object applyStyleProperty( AbstractButton b, String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, b, key, value );
	}

	/** @since 3.8 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 3.8 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, key );
	}

	@Override
	protected void paintText( Graphics g, AbstractButton b, Rectangle textRect, String text ) {
		FlatButtonUI.paintText( g, b, textRect, text, b.isEnabled() ? b.getForeground() : disabledText );

		if( b.getModel().isRollover() )
			paintUnderline( g, textRect );
	}

	private void paintUnderline( Graphics g, Rectangle rect ) {
		int descent = g.getFontMetrics().getDescent();

		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

		((Graphics2D)g).fill( new Rectangle2D.Float(
			rect.x, (rect.y + rect.height) - descent + UIScale.scale( 1f ),
			rect.width, UIScale.scale( 1f ) ) );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	//---- class FlatHyperlinkListener ----------------------------------------

	/** @since 3.5 */
	protected static class FlatHyperlinkListener
		extends BasicHyperlinkListener
	{
		protected FlatHyperlinkListener( AbstractButton b ) {
			super( b );
		}

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			super.propertyChange( e );
			FlatHTML.propertyChange( e );
		}
	}
}
