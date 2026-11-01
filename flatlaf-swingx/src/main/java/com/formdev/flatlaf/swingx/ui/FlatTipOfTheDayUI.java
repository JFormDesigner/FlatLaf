/*
 * Copyright 2025 FormDev Software GmbH
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

package com.formdev.flatlaf.swingx.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.plaf.basic.BasicTipOfTheDayUI;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatStylingSupport;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXTipOfTheDay}.
 *
 * @author Karl Tauber
 * @since 3.6
 */
public class FlatTipOfTheDayUI
	extends BasicTipOfTheDayUI
	implements StyleableUI
{
	/** @since 3.8 */ @Styleable protected Insets tipAreaInsets;

	private Border tipAreaBorder;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTipOfTheDayUI( (JXTipOfTheDay) c );
	}

	public FlatTipOfTheDayUI( JXTipOfTheDay tipPane ) {
		super( tipPane );
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		tipAreaInsets = UIManager.getInsets( "TipOfTheDay.tipAreaInsets" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		tipAreaInsets = null;

		oldStyleValues = null;
	}

	@Override
	protected void installComponents() {
		// removing (no longer needed) children when switching from other Laf (e.g. Metal)
		// BasicTipOfTheDayUI adds new components in installComponents(), but never
		// removes them when switching theme, which results in duplicate children
		tipPane.removeAll();

		super.installComponents();

		tipAreaBorder = null;
		updateTipAreaBorder();
	}

	private void updateTipAreaBorder() {
		// replace only border installed by this class
		if( tipArea == null || tipAreaInsets == null ||
			(tipAreaBorder != null && tipArea.getBorder() != tipAreaBorder) )
		  return;

		tipAreaBorder = FlatUIUtils.nonUIResource( new FlatEmptyBorder( tipAreaInsets ) );
		tipArea.setBorder( tipAreaBorder );
	}

	@Override
	protected void uninstallComponents() {
		super.uninstallComponents();

		// BasicTipOfTheDayUI adds new components in installComponents(), but never
		// removes them when switching theme, which results in duplicate children
		tipPane.removeAll();
	}

	@Override
	protected PropertyChangeListener createChangeListener() {
		PropertyChangeListener superListener = super.createChangeListener();
		return FlatStylingSupport.createPropertyChangeListener( tipPane, this::installStyle, e -> {
			superListener.propertyChange( e );

			if( "model".equals( e.getPropertyName() ) )
				showCurrentTip();
		} );
	}

	/** @since 3.8 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( tipPane, "JXTipOfTheDay" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 3.8 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
		updateTipAreaBorder();
	}

	/** @since 3.8 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, tipPane, key, value );
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
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	protected void showCurrentTip() {
		super.showCurrentTip();

		if( currentTipComponent instanceof JScrollPane ) {
			JScrollPane scrollPane = (JScrollPane) currentTipComponent;
			if( scrollPane.getBorder() == null )
				scrollPane.setBorder( BorderFactory.createEmptyBorder() );

			Component view = scrollPane.getViewport().getView();
			if( view instanceof JTextComponent && ((JTextComponent)view).getBorder() == null )
				((JTextComponent)view).setBorder( BorderFactory.createEmptyBorder() );
		}
	}
}
