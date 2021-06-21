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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStyleSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStyleSupport.UnknownStyleException;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.JavaCompatibility;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JTextField}.
 *
 * <!-- BasicTextFieldUI -->
 *
 * @uiDefault TextField.font					Font
 * @uiDefault TextField.background				Color
 * @uiDefault TextField.foreground				Color	also used if not editable
 * @uiDefault TextField.caretForeground			Color
 * @uiDefault TextField.selectionBackground		Color
 * @uiDefault TextField.selectionForeground		Color
 * @uiDefault TextField.disabledBackground		Color	used if not enabled
 * @uiDefault TextField.inactiveBackground		Color	used if not editable
 * @uiDefault TextField.inactiveForeground		Color	used if not enabled (yes, this is confusing; this should be named disabledForeground)
 * @uiDefault TextField.border					Border
 * @uiDefault TextField.margin					Insets
 * @uiDefault TextField.caretBlinkRate			int		default is 500 milliseconds
 *
 * <!-- FlatTextFieldUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault TextField.placeholderForeground	Color
 * @uiDefault TextField.focusedBackground		Color	optional
 * @uiDefault TextComponent.selectAllOnFocusPolicy	String	never, once (default) or always
 * @uiDefault TextComponent.selectAllOnMouseClick	boolean
 *
 * @author Karl Tauber
 */
public class FlatTextFieldUI
	extends BasicTextFieldUI
{
	@Styleable protected int minimumWidth;
	protected boolean isIntelliJTheme;
	private Color background;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color inactiveBackground;
	@Styleable protected Color placeholderForeground;
	@Styleable protected Color focusedBackground;

	private Color oldDisabledBackground;
	private Color oldInactiveBackground;

	private FocusListener focusListener;
	private Map<String, Object> oldStyleValues;
	private boolean borderShared = true;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextFieldUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		applyStyle( FlatStyleSupport.getStyle( c ) );
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		background = UIManager.getColor( prefix + ".background" );
		disabledBackground = UIManager.getColor( prefix + ".disabledBackground" );
		inactiveBackground = UIManager.getColor( prefix + ".inactiveBackground" );
		placeholderForeground = UIManager.getColor( prefix + ".placeholderForeground" );
		focusedBackground = UIManager.getColor( prefix + ".focusedBackground" );

		LookAndFeel.installProperty( getComponent(), "opaque", false );

		MigLayoutVisualPadding.install( getComponent() );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		background = null;
		disabledBackground = null;
		inactiveBackground = null;
		placeholderForeground = null;
		focusedBackground = null;

		oldDisabledBackground = null;
		oldInactiveBackground = null;

		oldStyleValues = null;

		MigLayoutVisualPadding.uninstall( getComponent() );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		// necessary to update focus border and background
		focusListener = new FlatUIUtils.RepaintFocusListener( getComponent(), null );
		getComponent().addFocusListener( focusListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		getComponent().removeFocusListener( focusListener );
		focusListener = null;
	}

	@Override
	protected Caret createCaret() {
		return new FlatCaret( UIManager.getString( "TextComponent.selectAllOnFocusPolicy"),
			UIManager.getBoolean( "TextComponent.selectAllOnMouseClick" ) );
	}

	@Override
	protected void propertyChange( PropertyChangeEvent e ) {
		String propertyName = e.getPropertyName();
		if( "editable".equals( propertyName ) || "enabled".equals( propertyName ) )
			updateBackground();
		else
			super.propertyChange( e );
		propertyChange( getComponent(), e, this::applyStyle );
	}

	static void propertyChange( JTextComponent c, PropertyChangeEvent e, Consumer<Object> applyStyle ) {
		switch( e.getPropertyName() ) {
			case FlatClientProperties.PLACEHOLDER_TEXT:
			case FlatClientProperties.COMPONENT_ROUND_RECT:
				c.repaint();
				break;

			case FlatClientProperties.MINIMUM_WIDTH:
				c.revalidate();
				break;

			case FlatClientProperties.STYLE:
				applyStyle.accept( e.getNewValue() );
				c.revalidate();
				c.repaint();
				break;
		}
	}

	/**
	 * @since TODO
	 */
	protected void applyStyle( Object style ) {
		oldDisabledBackground = disabledBackground;
		oldInactiveBackground = inactiveBackground;

		oldStyleValues = FlatStyleSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		updateBackground();
	}

	/**
	 * @since TODO
	 */
	protected Object applyStyleProperty( String key, Object value ) {
		try {
			return FlatStyleSupport.applyToAnnotatedObject( this, key, value );
		} catch( UnknownStyleException ex ) {
			Border border = getComponent().getBorder();
			if( border instanceof FlatBorder ) {
				if( borderShared ) {
					border = FlatStyleSupport.cloneBorder( border );
					getComponent().setBorder( border );
					borderShared = false;
				}

				try {
					return ((FlatBorder)border).applyStyleProperty( key, value );
				} catch( UnknownStyleException ex2 ) {
					// ignore
				}
			}
			throw ex;
		}
	}

	private void updateBackground() {
		updateBackground( getComponent(), background,
			disabledBackground, inactiveBackground,
			oldDisabledBackground, oldInactiveBackground );
	}

	// same functionality as BasicTextUI.updateBackground()
	static void updateBackground( JTextComponent c, Color background,
		Color disabledBackground, Color inactiveBackground,
		Color oldDisabledBackground, Color oldInactiveBackground )
	{
		Color oldBackground = c.getBackground();
		if( !(oldBackground instanceof UIResource) )
			return;

		// do not update background if it currently has a unknown color (assigned from outside)
		if( oldBackground != background &&
			oldBackground != disabledBackground &&
			oldBackground != inactiveBackground &&
			oldBackground != oldDisabledBackground &&
			oldBackground != oldInactiveBackground )
		  return;

		Color newBackground = !c.isEnabled()
			? disabledBackground
			: (!c.isEditable()
				? inactiveBackground
				: background);

		if( newBackground != oldBackground )
			c.setBackground( newBackground );
	}

	@Override
	protected void paintSafely( Graphics g ) {
		paintBackground( g, getComponent(), isIntelliJTheme, focusedBackground );
		paintPlaceholder( g, getComponent(), placeholderForeground );

		super.paintSafely( HiDPIUtils.createGraphicsTextYCorrection( (Graphics2D) g ) );
	}

	@Override
	protected void paintBackground( Graphics g ) {
		// background is painted elsewhere
	}

	static void paintBackground( Graphics g, JTextComponent c, boolean isIntelliJTheme, Color focusedBackground ) {
		// do not paint background if:
		//   - not opaque and
		//   - border is not a flat border and
		//   - opaque was explicitly set (to false)
		// (same behaviour as in AquaTextFieldUI)
		if( !c.isOpaque() && FlatUIUtils.getOutsideFlatBorder( c ) == null && FlatUIUtils.hasOpaqueBeenExplicitlySet( c ) )
			return;

		float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
		float arc = FlatUIUtils.getBorderArc( c );

		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() && (focusWidth > 0 || arc > 0) )
			FlatUIUtils.paintParentBackground( g, c );

		// paint background
		Graphics2D g2 = (Graphics2D) g.create();
		try {
			FlatUIUtils.setRenderingHints( g2 );

			g2.setColor( getBackground( c, isIntelliJTheme, focusedBackground ) );
			FlatUIUtils.paintComponentBackground( g2, 0, 0, c.getWidth(), c.getHeight(), focusWidth, arc );
		} finally {
			g2.dispose();
		}
	}

	static Color getBackground( JTextComponent c, boolean isIntelliJTheme, Color focusedBackground ) {
		Color background = c.getBackground();

		// always use explicitly set color
		if( !(background instanceof UIResource) )
			return background;

		// for compatibility with IntelliJ themes
		if( isIntelliJTheme && (!c.isEnabled() || !c.isEditable()) )
			return FlatUIUtils.getParentBackground( c );

		// focused and editable
		if( focusedBackground != null && c.isEditable() && FlatUIUtils.isPermanentFocusOwner( c ) )
			return focusedBackground;

		return background;
	}

	static void paintPlaceholder( Graphics g, JTextComponent c, Color placeholderForeground ) {
		// check whether text component is empty
		if( c.getDocument().getLength() > 0 )
			return;

		// check for JComboBox
		Container parent = c.getParent();
		JComponent jc = (parent instanceof JComboBox) ? (JComboBox<?>) parent : c;

		// get placeholder text
		Object placeholder = jc.getClientProperty( FlatClientProperties.PLACEHOLDER_TEXT );
		if( !(placeholder instanceof String) )
			return;

		// compute placeholder location
		Insets insets = c.getInsets();
		FontMetrics fm = c.getFontMetrics( c.getFont() );
		int x = insets.left;
		int y = insets.top + fm.getAscent() + ((c.getHeight() - insets.top - insets.bottom - fm.getHeight()) / 2);

		// paint placeholder
		g.setColor( placeholderForeground );
		String clippedPlaceholder = JavaCompatibility.getClippedString( jc, fm,
				(String) placeholder, c.getWidth() - insets.left - insets.right );
		FlatUIUtils.drawString( c, g, clippedPlaceholder, x, y );
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( c, super.getPreferredSize( c ), minimumWidth );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( c, super.getMinimumSize( c ), minimumWidth );
	}

	static Dimension applyMinimumWidth( JComponent c, Dimension size, int minimumWidth ) {
		// do not apply minimum width if JTextField.columns is set
		if( c instanceof JTextField && ((JTextField)c).getColumns() > 0 )
			return size;

		// do not apply minimum width if used in combobox or spinner
		Container parent = c.getParent();
		if( parent instanceof JComboBox ||
			parent instanceof JSpinner ||
			(parent != null && parent.getParent() instanceof JSpinner) )
		  return size;

		minimumWidth = FlatUIUtils.minimumWidth( c, minimumWidth );
		float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
		size.width = Math.max( size.width, scale( minimumWidth ) + Math.round( focusWidth * 2 ) );
		return size;
	}
}
