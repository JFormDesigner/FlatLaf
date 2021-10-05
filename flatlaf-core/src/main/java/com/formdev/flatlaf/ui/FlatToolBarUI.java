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
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JToolBar}.
 *
 * <!-- BasicToolBarUI -->
 *
 * @uiDefault ToolBar.font								Font
 * @uiDefault ToolBar.background						Color
 * @uiDefault ToolBar.foreground						Color
 * @uiDefault ToolBar.border							Border
 * @uiDefault ToolBar.dockingBackground					Color
 * @uiDefault ToolBar.dockingForeground					Color
 * @uiDefault ToolBar.floatingBackground				Color
 * @uiDefault ToolBar.floatingForeground				Color
 * @uiDefault ToolBar.isRollover						boolean
 *
 * <!-- FlatToolBarUI -->
 *
 * @uiDefault ToolBar.focusableButtons					boolean
 * @uiDefault ToolBar.floatable							boolean
 *
 * <!-- FlatToolBarBorder -->
 *
 * @uiDefault ToolBar.borderMargins				Insets
 * @uiDefault ToolBar.gripColor					Color
 *
 * @author Karl Tauber
 */
public class FlatToolBarUI
	extends BasicToolBarUI
	implements StyleableUI
{
	/** @since 1.4 */
	@Styleable protected boolean focusableButtons;

	// for FlatToolBarBorder
	@Styleable protected Insets borderMargins;
	@Styleable protected Color gripColor;

	private Boolean oldFloatable;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatToolBarUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		// disable focusable state of buttons (when switching from another Laf)
		if( !focusableButtons )
			setButtonsFocusable( false );

		installStyle();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		// re-enable focusable state of buttons (when switching to another Laf)
		if( !focusableButtons )
			setButtonsFocusable( true );

		oldStyleValues = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		focusableButtons = UIManager.getBoolean( "ToolBar.focusableButtons" );

		// floatable
		if( !UIManager.getBoolean( "ToolBar.floatable" ) ) {
			oldFloatable = toolBar.isFloatable();
			toolBar.setFloatable( false );
		} else
			oldFloatable = null;
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		if( oldFloatable != null ) {
			toolBar.setFloatable( oldFloatable );
			oldFloatable = null;
		}
	}

	@Override
	protected ContainerListener createToolBarContListener() {
		return new ToolBarContListener() {
			@Override
			public void componentAdded( ContainerEvent e ) {
				super.componentAdded( e );

				if( !focusableButtons ) {
					Component c = e.getChild();
					if( c instanceof AbstractButton )
						c.setFocusable( false );
				}
			}

			@Override
			public void componentRemoved( ContainerEvent e ) {
				super.componentRemoved( e );

				if( !focusableButtons ) {
					Component c = e.getChild();
					if( c instanceof AbstractButton )
						c.setFocusable( true );
				}
			}
		};
	}

	@Override
	protected PropertyChangeListener createPropertyListener() {
		return FlatStylingSupport.createPropertyChangeListener( toolBar, this::installStyle, super.createPropertyListener() );
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( toolBar, "ToolBar" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		boolean oldFocusableButtons = focusableButtons;

		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		if( focusableButtons != oldFocusableButtons )
			setButtonsFocusable( focusableButtons );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, toolBar, key, value );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this );
	}

	/** @since 1.4 */
	protected void setButtonsFocusable( boolean focusable ) {
		for( Component c : toolBar.getComponents() ) {
			if( c instanceof AbstractButton )
				c.setFocusable( focusable );
		}
	}

	/**
	 * Does the same as super.navigateFocusedComp() with the exception that components
	 * with empty input map (e.g. JLabel) are skipped.
	 */
	@Override
	protected void navigateFocusedComp( int direction ) {
		int count = toolBar.getComponentCount();

		if( focusedCompIndex < 0 || focusedCompIndex >= count )
			return;

		int add;
		switch( direction ) {
			case EAST: case SOUTH: add = 1; break;
			case WEST: case NORTH: add = -1; break;
			default: return;
		}

		for( int i = focusedCompIndex + add; i != focusedCompIndex; i += add ) {
			if( i < 0 )
				i = count - 1;
			else if( i >= count )
				i = 0;

			Component c = toolBar.getComponentAtIndex( i );

			// see Component.canBeFocusOwner()
			if( c == null || !c.isEnabled() || !c.isVisible() || !c.isDisplayable() || !c.isFocusable() )
				continue; // skip

			// check whether component has a empty input map to skip components that
			// are focusable, but do nothing when focused (e.g. JLabel)
			if( c instanceof JComponent ) {
				// see LayoutFocusTraversalPolicy.accept()
				InputMap inputMap = ((JComponent)c).getInputMap( JComponent.WHEN_FOCUSED );
				while( inputMap != null && inputMap.size() == 0 )
					inputMap = inputMap.getParent();
				if( inputMap == null )
					continue; // skip
			}

			c.requestFocus();
			return;
		}
	}

	// disable rollover border
	@Override protected void setBorderToRollover( Component c ) {}
	@Override protected void setBorderToNonRollover( Component c ) {}
	@Override protected void setBorderToNormal( Component c ) {}
	@Override protected void installRolloverBorders( JComponent c ) {}
	@Override protected void installNonRolloverBorders( JComponent c ) {}
	@Override protected void installNormalBorders( JComponent c ) {}
	@Override protected Border createRolloverBorder() { return null; }
	@Override protected Border createNonRolloverBorder() { return null; }

	@Override
	public void setOrientation( int orientation ) {
		if( orientation != toolBar.getOrientation() ) {
			// swap margins if orientation changes when floating
			Insets margin = toolBar.getMargin();
			Insets newMargin = new Insets( margin.left, margin.top, margin.right, margin.bottom );
			if( !newMargin.equals( margin ) )
				toolBar.setMargin( newMargin );
		}

		super.setOrientation( orientation );
	}
}
