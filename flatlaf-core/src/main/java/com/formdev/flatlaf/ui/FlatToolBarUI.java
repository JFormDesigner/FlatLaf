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
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Insets;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeListener;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.LayoutFocusTraversalPolicy;
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
 * @uiDefault ToolBar.arrowKeysOnlyNavigation			boolean
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
	/** @since 1.4 */ @Styleable protected boolean focusableButtons;
	/** @since 2 */ @Styleable protected boolean arrowKeysOnlyNavigation;

	// for FlatToolBarBorder
	@Styleable protected Insets borderMargins;
	@Styleable protected Color gripColor;

	private FocusTraversalPolicy focusTraversalPolicy;
	private Boolean oldFloatable;
	private Map<String, Object> oldStyleValues;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatToolBarUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installFocusTraversalPolicy();

		installStyle();

		// disable focusable state of buttons (when switching from another Laf)
		//   do this after applying style to avoid disabling (here) and re-enabling
		//   (in applyStyle()), which would transfer focus to next button
		if( !focusableButtons )
			setButtonsFocusable( false );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		// re-enable focusable state of buttons (when switching to another Laf)
		if( !focusableButtons )
			setButtonsFocusable( true );

		uninstallFocusTraversalPolicy();

		oldStyleValues = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		focusableButtons = UIManager.getBoolean( "ToolBar.focusableButtons" );
		arrowKeysOnlyNavigation = UIManager.getBoolean( "ToolBar.arrowKeysOnlyNavigation" );

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

				if( !focusableButtons )
					setButtonFocusable( e.getChild(), false );
			}

			@Override
			public void componentRemoved( ContainerEvent e ) {
				super.componentRemoved( e );

				if( !focusableButtons )
					setButtonFocusable( e.getChild(), true );
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
		boolean oldArrowKeysOnlyNavigation = arrowKeysOnlyNavigation;

		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		if( focusableButtons != oldFocusableButtons )
			setButtonsFocusable( focusableButtons );
		if( arrowKeysOnlyNavigation != oldArrowKeysOnlyNavigation || focusableButtons != oldFocusableButtons ) {
			if( arrowKeysOnlyNavigation )
				installFocusTraversalPolicy();
			else
				uninstallFocusTraversalPolicy();
		}
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
		for( Component c : toolBar.getComponents() )
			setButtonFocusable( c, focusable );
	}

	private void setButtonFocusable( Component c, boolean focusable ) {
		if( c instanceof AbstractButton && focusable != c.isFocusable() )
			c.setFocusable( focusable );
	}

	/** @since 2 */
	protected void installFocusTraversalPolicy() {
		if( !arrowKeysOnlyNavigation || !focusableButtons || toolBar.getFocusTraversalPolicy() != null )
			return;

		focusTraversalPolicy = createFocusTraversalPolicy();
		if( focusTraversalPolicy != null ) {
			toolBar.setFocusTraversalPolicy( focusTraversalPolicy );
			toolBar.setFocusTraversalPolicyProvider( true );
		}
	}

	/** @since 2 */
	protected void uninstallFocusTraversalPolicy() {
		if( focusTraversalPolicy != null && toolBar.getFocusTraversalPolicy() == focusTraversalPolicy ) {
			toolBar.setFocusTraversalPolicy( null );
			toolBar.setFocusTraversalPolicyProvider( false );
		}
		focusTraversalPolicy = null;
	}

	/** @since 2 */
	protected FocusTraversalPolicy createFocusTraversalPolicy() {
		return new FlatToolBarFocusTraversalPolicy();
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
			if( canBeFocusOwner( c ) ) {
				c.requestFocus();
				return;
			}
		}
	}

	private static boolean canBeFocusOwner( Component c ) {
		// see Component.canBeFocusOwner()
		if( c == null || !c.isEnabled() || !c.isVisible() || !c.isDisplayable() || !c.isFocusable() )
			return false;

		// special handling for combo box
		// see LayoutFocusTraversalPolicy.accept()
		if( c instanceof JComboBox ) {
			JComboBox<?> comboBox = (JComboBox<?>) c;
			return comboBox.getUI().isFocusTraversable( comboBox );
		}

		// check whether component has an empty input map to skip components that
		// are focusable, but do nothing when focused (e.g. JLabel)
		// see LayoutFocusTraversalPolicy.accept()
		if( c instanceof JComponent ) {
			InputMap inputMap = ((JComponent)c).getInputMap( JComponent.WHEN_FOCUSED );
			while( inputMap != null && inputMap.size() == 0 )
				inputMap = inputMap.getParent();
			if( inputMap == null )
				return false;
		}

		return true;
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

	//---- class FlatToolBarFocusTraversalPolicy ------------------------------

	/**
	 * Focus traversal policy used for toolbar to modify traversal behaviour:
	 * <ul>
	 * <li>Tab-key moves focus out of toolbar.</li>
	 * <li>If moving focus into the toolbar, focus recently focused toolbar button.</li>
	 * </ul>
	 * If the toolbar contains non-button components (e.g. combobox), then the behavior
	 * is slightly different. Non-button component are always included in Tab-key traversal.
	 *
	 * @since 2
	 */
	protected class FlatToolBarFocusTraversalPolicy
		extends LayoutFocusTraversalPolicy
	{
		@Override
		public Component getComponentAfter( Container aContainer, Component aComponent ) {
			// if currently focused component is not a button,
			// then move focus to next component/button in toolbar
			if( !(aComponent instanceof AbstractButton) )
				return super.getComponentAfter( aContainer, aComponent );

			// if currently focused component is a button,
			// then either move focus to next non-button component in toolbar (and skip buttons)
			// or move it out of toolbar
			Component c = aComponent;
			while( (c = super.getComponentAfter( aContainer, c )) != null ) {
				if( !(c instanceof AbstractButton) )
					return c;
			}

			// move focus out of toolbar
			return null;
		}

		@Override
		public Component getComponentBefore( Container aContainer, Component aComponent ) {
			// if currently focused component is not a button,
			// then move focus to previous component/button in toolbar
			if( !(aComponent instanceof AbstractButton) )
				return super.getComponentBefore( aContainer, aComponent );

			// if currently focused component is a button,
			// then either move focus to previous non-button component in toolbar (and skip buttons)
			// or move it out of toolbar
			Component c = aComponent;
			while( (c = super.getComponentBefore( aContainer, c )) != null ) {
				if( !(c instanceof AbstractButton) )
					return c;
			}

			// move focus out of toolbar
			return null;
		}

		@Override
		public Component getFirstComponent( Container aContainer ) {
			return getRecentComponent( aContainer, true );
		}

		@Override
		public Component getLastComponent( Container aContainer ) {
			return getRecentComponent( aContainer, false );
		}

		private Component getRecentComponent( Container aContainer, boolean first ) {
			// if moving focus into the toolbar, focus recently focused toolbar button
			if( focusedCompIndex >= 0 && focusedCompIndex < toolBar.getComponentCount() )
				return toolBar.getComponent( focusedCompIndex );

			return first
				? super.getFirstComponent( aContainer )
				: super.getLastComponent( aContainer );
		}
	}
}
