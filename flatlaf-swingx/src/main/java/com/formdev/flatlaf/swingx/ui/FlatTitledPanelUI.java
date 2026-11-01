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

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.SwingXUtilities;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.PainterUIResource;
import org.jdesktop.swingx.plaf.basic.BasicTitledPanelUI;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXTitledPanel}.
 *
 * @author Karl Tauber
 */
public class FlatTitledPanelUI
	extends BasicTitledPanelUI
	implements StyleableUI
{
	/** @since 3.8 */ @Styleable protected Color titleBackground;
	/** @since 3.8 */ @Styleable protected Color borderColor;
	/** @since 3.8 */ @Styleable protected Insets captionInsets;

	private JXTitledPanel titledPanel;
	private FlatLineBorder installedBorder;

	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTitledPanelUI();
	}

	@Override
	public void installUI( JComponent c ) {
		titledPanel = (JXTitledPanel) c;

		super.installUI( c );

		installStyle();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		titleBackground = null;
		borderColor = null;
		captionInsets = null;

		titledPanel = null;
		installedBorder = null;

		oldStyleValues = null;
	}

	@Override
	protected void installDefaults( JXTitledPanel titledPanel ) {
		super.installDefaults( titledPanel );

		titleBackground = UIManager.getColor( "JXTitledPanel.titleBackground" );
		borderColor = UIManager.getColor( "JXTitledPanel.borderColor" );
		captionInsets = super.getCaptionInsets();

		// replace gradient background
		installTitlePainter();
	}

	@Override
	protected void installComponents( JXTitledPanel titledPanel ) {
		super.installComponents( titledPanel );

		installedBorder = null;
		if( SwingXUtilities.isUIInstallable( titledPanel.getBorder() ) )
			installBorder();
	}

	@Override
	protected void installListeners( JXTitledPanel titledPanel ) {
		super.installListeners( titledPanel );

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener( titledPanel, this::installStyle, null );
		titledPanel.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners( JXTitledPanel titledPanel ) {
		super.uninstallListeners( titledPanel );

		titledPanel.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	/** @since 3.8 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( titledPanel, "JXTitledPanel" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 3.8 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		installTitlePainter();
		if( installedBorder != null && titledPanel.getBorder() == installedBorder )
			installBorder();
		updateCaptionInsets();
	}

	/** @since 3.8 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, titledPanel, key, value );
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

	private void installTitlePainter() {
		installProperty( titledPanel, "titlePainter", new PainterUIResource<>(
			new TitlePainter( titleBackground, borderColor ) ) );
	}

	private void installBorder() {
		installedBorder = new FlatLineBorder( new Insets( 1, 1, 1, 1 ), borderColor );
		titledPanel.setBorder( installedBorder );
	}

	private void updateCaptionInsets() {
		if( topPanel == null || caption == null || !(topPanel.getLayout() instanceof GridBagLayout) )
			return;

		GridBagLayout gbl = (GridBagLayout) topPanel.getLayout();
		GridBagConstraints cons = gbl.getConstraints( caption );
		cons.insets = getCaptionInsets();
		gbl.setConstraints( caption, cons );
	}

	@Override
	protected Insets getCaptionInsets() {
		return UIScale.scale( (captionInsets != null) ? captionInsets : super.getCaptionInsets() );
	}

	//---- class TitlePainter -------------------------------------------------

	private static class TitlePainter
		extends MattePainter
	{
		private final Color borderColor;

		TitlePainter( Color color, Color borderColor ) {
			super( color );
			this.borderColor = borderColor;
		}

		@Override
		protected void doPaint( Graphics2D g, Object component, int width, int height ) {
			super.doPaint( g, component, width, height );

			float h = scale( 1f );
			g.setColor( borderColor );
			g.fill( new Rectangle2D.Float( 0, height - h, width, h ) );
		}
	}
}
