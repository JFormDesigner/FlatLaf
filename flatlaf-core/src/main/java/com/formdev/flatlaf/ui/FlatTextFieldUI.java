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

import static com.formdev.flatlaf.FlatClientProperties.*;
import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
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
 * @uiDefault TextField.iconTextGap				int		optional, default is 4
 * @uiDefault TextComponent.selectAllOnFocusPolicy	String	never, once (default) or always
 * @uiDefault TextComponent.selectAllOnMouseClick	boolean
 *
 * @author Karl Tauber
 */
public class FlatTextFieldUI
	extends BasicTextFieldUI
{
	protected int minimumWidth;
	protected boolean isIntelliJTheme;
	protected Color placeholderForeground;
	protected Color focusedBackground;
	protected int iconTextGap;

	protected Icon leadingIcon;
	protected Icon trailingIcon;

	private Insets defaultMargin;

	private FocusListener focusListener;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextFieldUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		leadingIcon = clientProperty( c, TEXT_FIELD_LEADING_ICON, null, Icon.class );
		trailingIcon = clientProperty( c, TEXT_FIELD_TRAILING_ICON, null, Icon.class );
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		leadingIcon = null;
		trailingIcon = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		String prefix = getPropertyPrefix();
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		placeholderForeground = UIManager.getColor( prefix + ".placeholderForeground" );
		focusedBackground = UIManager.getColor( prefix + ".focusedBackground" );
		iconTextGap = FlatUIUtils.getUIInt( prefix + ".iconTextGap", 4 );

		defaultMargin = UIManager.getInsets( prefix + ".margin" );

		LookAndFeel.installProperty( getComponent(), "opaque", false );

		MigLayoutVisualPadding.install( getComponent() );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		placeholderForeground = null;
		focusedBackground = null;

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
		super.propertyChange( e );

		JTextComponent c = getComponent();
		switch( e.getPropertyName() ) {
			case PLACEHOLDER_TEXT:
			case COMPONENT_ROUND_RECT:
			case TEXT_FIELD_PADDING:
				c.repaint();
				break;

			case MINIMUM_WIDTH:
				c.revalidate();
				break;

			case TEXT_FIELD_LEADING_ICON:
				leadingIcon = (e.getNewValue() instanceof Icon) ? (Icon) e.getNewValue() : null;
				c.repaint();
				break;

			case TEXT_FIELD_TRAILING_ICON:
				trailingIcon = (e.getNewValue() instanceof Icon) ? (Icon) e.getNewValue() : null;
				c.repaint();
				break;
		}
	}

	@Override
	protected void paintSafely( Graphics g ) {
		paintBackground( g, getComponent(), isIntelliJTheme, focusedBackground );
		paintPlaceholder( g );

		if( hasLeadingIcon() || hasTrailingIcon() )
			paintIcons( g, new Rectangle( getIconsRect() ) );

/*debug
		Rectangle r = getVisibleEditorRect();
		g.setColor( Color.red );
		g.drawRect( r.x, r.y, r.width - 1, r.height - 1 );
debug*/

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

		// focused
		if( focusedBackground != null && FlatUIUtils.isPermanentFocusOwner( c ) )
			return focusedBackground;

		// for compatibility with IntelliJ themes
		if( isIntelliJTheme && (!c.isEnabled() || !c.isEditable()) )
			return FlatUIUtils.getParentBackground( c );

		return background;
	}

	protected void paintPlaceholder( Graphics g ) {
		JTextComponent c = getComponent();

		// check whether text component is empty
		if( c.getDocument().getLength() > 0 )
			return;

		// check for JComboBox
		Container parent = c.getParent();
		JComponent jc = (parent instanceof JComboBox) ? (JComboBox<?>) parent : c;

		// get placeholder text
		String placeholder = clientProperty( jc, PLACEHOLDER_TEXT, null, String.class );
		if( placeholder == null )
			return;

		// compute placeholder location
		Rectangle r = getVisibleEditorRect();
		FontMetrics fm = c.getFontMetrics( c.getFont() );
		String clippedPlaceholder = JavaCompatibility.getClippedString( c, fm, placeholder, r.width );
		int x = r.x + (isLeftToRight() ? 0 : r.width - fm.stringWidth( clippedPlaceholder ));
		int y = r.y + fm.getAscent() + ((r.height - fm.getHeight()) / 2);

		// paint placeholder
		g.setColor( placeholderForeground );
		FlatUIUtils.drawString( c, g, clippedPlaceholder, x, y );
	}

	/**
	 * Paints the leading and trailing icons in the given rectangle.
	 * The rectangle is updated by this method so that subclasses can use it
	 * without painting over leading or trailing icons.
	 *
	 * @since 2
	 */
	protected void paintIcons( Graphics g, Rectangle r ) {
		boolean ltr = isLeftToRight();
		Icon leftIcon = ltr ? leadingIcon : trailingIcon;
		Icon rightIcon = ltr ? trailingIcon : leadingIcon;

		// paint left icon
		if( leftIcon != null ) {
			int x = r.x;
			int y = r.y + Math.round( (r.height - leftIcon.getIconHeight()) / 2f );
			leftIcon.paintIcon( getComponent(), g, x, y );

			// update rectangle so that subclasses can use it
			int w = leftIcon.getIconWidth() + scale( iconTextGap );
			r.x += w;
			r.width -= w;
		}

		// paint right icon
		if( rightIcon != null ) {
			int iconWidth = rightIcon.getIconWidth();
			int x = r.x + r.width - iconWidth;
			int y = r.y + Math.round( (r.height - rightIcon.getIconHeight()) / 2f );
			rightIcon.paintIcon( getComponent(), g, x, y );

			// update rectangle so that subclasses can use it
			r.width -= iconWidth + scale( iconTextGap );
		}
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return applyMinimumWidth( c, super.getPreferredSize( c ), minimumWidth );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( c, super.getMinimumSize( c ), minimumWidth );
	}

	private Dimension applyMinimumWidth( JComponent c, Dimension size, int minimumWidth ) {
		// do not apply minimum width if JTextField.columns is set
		if( c instanceof JTextField && ((JTextField)c).getColumns() > 0 )
			return size;

		// do not apply minimum width if JTextComponent.margin is set
		if( !hasDefaultMargins( c, defaultMargin ) )
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

	static boolean hasDefaultMargins( JComponent c, Insets defaultMargin ) {
		Insets margin = ((JTextComponent)c).getMargin();
		return margin instanceof UIResource && Objects.equals( margin, defaultMargin );
	}

	/**
	 * Returns the rectangle used for the root view of the text.
	 * This method is used to place the text.
	 */
	@Override
	protected Rectangle getVisibleEditorRect() {
		Rectangle r = getIconsRect();
		if( r == null )
			return null;

		// remove space needed for leading and trailing icons
		int leading = getLeadingIconWidth();
		int trailing = getTrailingIconWidth();
		if( leading != 0 || trailing != 0 ) {
			boolean ltr = isLeftToRight();
			int left = ltr ? leading : trailing;
			int right = ltr ? trailing : leading;
			r.x += left;
			r.width -= left + right;
		}

		// remove padding
		Insets padding = getPadding();
		if( padding != null )
			r = FlatUIUtils.subtractInsets( r, padding );

		// make sure that width and height are not negative
		r.width = Math.max( r.width, 0 );
		r.height = Math.max( r.height, 0 );

		return r;
	}

	/**
	 * Returns the rectangle used to paint leading and trailing icons.
	 * It invokes {@code super.getVisibleEditorRect()} and reduces left and/or
	 * right margin if the text field has leading or trailing icons.
	 *
	 * @since 2
	 */
	protected Rectangle getIconsRect() {
		Rectangle r = super.getVisibleEditorRect();
		if( r == null )
			return null;

		// if a leading/trailing icon is shown, then the left/right margin is reduced
		// to the top margin, which places the icon nicely centered on left/right side
		boolean ltr = isLeftToRight();
		if( ltr ? hasLeadingIcon() : hasTrailingIcon() ) {
			// reduce left margin
			Insets margin = getComponent().getMargin();
			int newLeftMargin = Math.min( margin.left, margin.top );
			if( newLeftMargin < margin.left ) {
				int diff = scale( margin.left - newLeftMargin );
				r.x -= diff;
				r.width += diff;
			}
		}
		if( ltr ? hasTrailingIcon() : hasLeadingIcon() ) {
			// reduce right margin
			Insets margin = getComponent().getMargin();
			int newRightMargin = Math.min( margin.right, margin.top );
			if( newRightMargin < margin.left )
				r.width += scale( margin.right - newRightMargin );
		}

		return r;
	}

	/** @since 2 */
	protected boolean hasLeadingIcon() {
		return leadingIcon != null;
	}

	/** @since 2 */
	protected boolean hasTrailingIcon() {
		return trailingIcon != null;
	}

	/** @since 2 */
	protected int getLeadingIconWidth() {
		return (leadingIcon != null) ? leadingIcon.getIconWidth() + scale( iconTextGap ) : 0;
	}

	/** @since 2 */
	protected int getTrailingIconWidth() {
		return (trailingIcon != null) ? trailingIcon.getIconWidth() + scale( iconTextGap ) : 0;
	}

	boolean isLeftToRight() {
		return getComponent().getComponentOrientation().isLeftToRight();
	}

	/**
	 * @since 1.4
	 */
	protected Insets getPadding() {
		return scale( clientProperty( getComponent(), TEXT_FIELD_PADDING, null, Insets.class ) );
	}

	/**
	 * @since 1.4
	 */
	protected void scrollCaretToVisible() {
		Caret caret = getComponent().getCaret();
		if( caret instanceof FlatCaret )
			((FlatCaret)caret).scrollCaretToVisible();
	}
}
