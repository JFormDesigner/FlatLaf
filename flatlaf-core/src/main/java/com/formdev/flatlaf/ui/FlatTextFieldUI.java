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
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.JavaCompatibility;
import com.formdev.flatlaf.util.LoggingFacade;

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
	implements StyleableUI
{
	@Styleable protected int minimumWidth;
	protected boolean isIntelliJTheme;
	private Color background;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color inactiveBackground;
	@Styleable protected Color placeholderForeground;
	@Styleable protected Color focusedBackground;
	/** @since 2 */ @Styleable protected int iconTextGap;

	/** @since 2 */ @Styleable protected Icon leadingIcon;
	/** @since 2 */ @Styleable protected Icon trailingIcon;
	/** @since 2 */ protected JComponent leadingComponent;
	/** @since 2 */ protected JComponent trailingComponent;
	/** @since 2 */ protected JComponent clearButton;

	// only used via styling (not in UI defaults, but has likewise client properties)
	/** @since 2 */ @Styleable protected boolean showClearButton;

	private Color oldDisabledBackground;
	private Color oldInactiveBackground;

	private Insets defaultMargin;

	private FocusListener focusListener;
	private DocumentListener documentListener;
	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatTextFieldUI();
	}

	@Override
	public void installUI( JComponent c ) {
		if( FlatUIUtils.needsLightAWTPeer( c ) )
			FlatUIUtils.runWithLightAWTPeerUIDefaults( () -> installUIImpl( c ) );
		else
			installUIImpl( c );
	}

	private void installUIImpl( JComponent c ) {
		super.installUI( c );

		leadingIcon = clientProperty( c, TEXT_FIELD_LEADING_ICON, null, Icon.class );
		trailingIcon = clientProperty( c, TEXT_FIELD_TRAILING_ICON, null, Icon.class );

		installLeadingComponent();
		installTrailingComponent();
		installClearButton();

		installStyle();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		uninstallLeadingComponent();
		uninstallTrailingComponent();
		uninstallClearButton();

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
		background = UIManager.getColor( prefix + ".background" );
		disabledBackground = UIManager.getColor( prefix + ".disabledBackground" );
		inactiveBackground = UIManager.getColor( prefix + ".inactiveBackground" );
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

		background = null;
		disabledBackground = null;
		inactiveBackground = null;
		placeholderForeground = null;
		focusedBackground = null;

		oldDisabledBackground = null;
		oldInactiveBackground = null;

		oldStyleValues = null;
		borderShared = null;

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

		if( documentListener != null ) {
			getComponent().getDocument().removeDocumentListener( documentListener );
			documentListener = null;
		}
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

		JTextComponent c = getComponent();
		switch( e.getPropertyName() ) {
			case PLACEHOLDER_TEXT:
			case COMPONENT_ROUND_RECT:
			case OUTLINE:
			case TEXT_FIELD_PADDING:
				c.repaint();
				break;

			case MINIMUM_WIDTH:
				c.revalidate();
				break;

			case STYLE:
			case STYLE_CLASS:
				installStyle();
				c.revalidate();
				c.repaint();
				break;

			case TEXT_FIELD_LEADING_ICON:
				leadingIcon = (e.getNewValue() instanceof Icon) ? (Icon) e.getNewValue() : null;
				c.repaint();
				break;

			case TEXT_FIELD_TRAILING_ICON:
				trailingIcon = (e.getNewValue() instanceof Icon) ? (Icon) e.getNewValue() : null;
				c.repaint();
				break;

			case TEXT_FIELD_LEADING_COMPONENT:
				uninstallLeadingComponent();
				installLeadingComponent();
				c.revalidate();
				c.repaint();
				break;

			case TEXT_FIELD_TRAILING_COMPONENT:
				uninstallTrailingComponent();
				installTrailingComponent();
				c.revalidate();
				c.repaint();
				break;

			case TEXT_FIELD_SHOW_CLEAR_BUTTON:
				uninstallClearButton();
				installClearButton();
				c.revalidate();
				c.repaint();
				break;

			case "enabled":
			case "editable":
				updateClearButton();
				break;

			case "document":
				if( documentListener != null ) {
					if( e.getOldValue() instanceof Document )
						((Document)e.getOldValue()).removeDocumentListener( documentListener );
					if( e.getNewValue() instanceof Document )
						((Document)e.getNewValue()).addDocumentListener( documentListener );

					updateClearButton();
				}
				break;
		}
	}

	/** @since 2 */
	protected void installDocumentListener() {
		if( documentListener != null )
			return;

		documentListener = new FlatDocumentListener();
		getComponent().getDocument().addDocumentListener( documentListener );
	}

	/** @since 2 */
	protected void documentChanged( DocumentEvent e ) {
		if( clearButton != null )
			updateClearButton();
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( getComponent(), getStyleType() ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	String getStyleType() {
		return "TextField";
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldDisabledBackground = disabledBackground;
		oldInactiveBackground = inactiveBackground;
		boolean oldShowClearButton = showClearButton;

		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		updateBackground();
		if( showClearButton != oldShowClearButton ) {
			uninstallClearButton();
			installClearButton();
		}
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, getComponent(), borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, getComponent().getBorder() );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, getComponent().getBorder(), key );
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

		// do not update background if it currently has an unknown color (assigned from outside)
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
		// (same behavior as in AquaTextFieldUI)
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
		return applyMinimumWidth( c, applyExtraSize( super.getPreferredSize( c ) ), minimumWidth );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return applyMinimumWidth( c, applyExtraSize( super.getMinimumSize( c ) ), minimumWidth );
	}

	private Dimension applyExtraSize( Dimension size ) {
		// add width of leading and trailing icons
		size.width += getLeadingIconWidth() + getTrailingIconWidth();

		// add width of leading and trailing components
		for( JComponent comp : getLeadingComponents() ) {
			if( comp != null && comp.isVisible() )
				size.width += comp.getPreferredSize().width;
		}
		for( JComponent comp : getTrailingComponents() ) {
			if( comp != null && comp.isVisible() )
				size.width += comp.getPreferredSize().width;
		}

		return size;
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
	 * right margin if the text field has leading or trailing icons or components.
	 * Also, the preferred widths of leading and trailing components are removed.
	 *
	 * @since 2
	 */
	protected Rectangle getIconsRect() {
		Rectangle r = super.getVisibleEditorRect();
		if( r == null )
			return null;

		boolean ltr = isLeftToRight();

		// remove width of leading/trailing components
		JComponent[] leftComponents = ltr ? getLeadingComponents() : getTrailingComponents();
		JComponent[] rightComponents = ltr ? getTrailingComponents() : getLeadingComponents();
		boolean leftVisible = false;
		boolean rightVisible = false;
		for( JComponent leftComponent : leftComponents ) {
			if( leftComponent != null && leftComponent.isVisible() ) {
				int w = leftComponent.getPreferredSize().width;
				r.x += w;
				r.width -= w;
				leftVisible = true;
			}
		}
		for( JComponent rightComponent : rightComponents ) {
			if( rightComponent != null && rightComponent.isVisible() ) {
				r.width -= rightComponent.getPreferredSize().width;
				rightVisible = true;
			}
		}

		// if a leading/trailing icons (or components) are shown, then the left/right margins are reduced
		// to the top margin, which places the icon nicely centered on left/right side
		if( leftVisible || (ltr ? hasLeadingIcon() : hasTrailingIcon()) ) {
			// reduce left margin
			Insets margin = getComponent().getMargin();
			int newLeftMargin = Math.min( margin.left, margin.top );
			if( newLeftMargin < margin.left ) {
				int diff = scale( margin.left - newLeftMargin );
				r.x -= diff;
				r.width += diff;
			}
		}
		if( rightVisible || (ltr ? hasTrailingIcon() : hasLeadingIcon()) ) {
			// reduce right margin
			Insets margin = getComponent().getMargin();
			int newRightMargin = Math.min( margin.right, margin.top );
			if( newRightMargin < margin.left )
				r.width += scale( margin.right - newRightMargin );
		}

		// make sure that width and height are not negative
		r.width = Math.max( r.width, 0 );
		r.height = Math.max( r.height, 0 );

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

	/** @since 1.4 */
	protected Insets getPadding() {
		return scale( clientProperty( getComponent(), TEXT_FIELD_PADDING, null, Insets.class ) );
	}

	/** @since 1.4 */
	protected void scrollCaretToVisible() {
		Caret caret = getComponent().getCaret();
		if( caret instanceof FlatCaret )
			((FlatCaret)caret).scrollCaretToVisible();
	}

	/** @since 2 */
	protected void installLeadingComponent() {
		JTextComponent c = getComponent();
		leadingComponent = clientProperty( c, TEXT_FIELD_LEADING_COMPONENT, null, JComponent.class );
		if( leadingComponent != null ) {
			prepareLeadingOrTrailingComponent( leadingComponent );
			installLayout();
			c.add( leadingComponent );
		}
	}

	/** @since 2 */
	protected void installTrailingComponent() {
		JTextComponent c = getComponent();
		trailingComponent = clientProperty( c, TEXT_FIELD_TRAILING_COMPONENT, null, JComponent.class );
		if( trailingComponent != null ) {
			prepareLeadingOrTrailingComponent( trailingComponent );
			installLayout();
			c.add( trailingComponent );
		}
	}

	/** @since 2 */
	protected void uninstallLeadingComponent() {
		if( leadingComponent != null ) {
			getComponent().remove( leadingComponent );
			leadingComponent = null;
		}
	}

	/** @since 2 */
	protected void uninstallTrailingComponent() {
		if( trailingComponent != null ) {
			getComponent().remove( trailingComponent );
			trailingComponent = null;
		}
	}

	/** @since 2 */
	protected void installClearButton() {
		JTextComponent c = getComponent();
		if( clientPropertyBoolean( c, TEXT_FIELD_SHOW_CLEAR_BUTTON, showClearButton ) ) {
			clearButton = createClearButton();
			updateClearButton();
			installDocumentListener();
			installLayout();
			c.add( clearButton );
		}
	}

	/** @since 2 */
	protected void uninstallClearButton() {
		if( clearButton != null ) {
			getComponent().remove( clearButton );
			clearButton = null;
		}
	}

	/** @since 2 */
	protected JComponent createClearButton() {
		JButton button = new JButton();
		button.setName( "TextField.clearButton" );
		button.putClientProperty( STYLE_CLASS, "clearButton" );
		button.putClientProperty( BUTTON_TYPE, BUTTON_TYPE_TOOLBAR_BUTTON );
		button.setCursor( Cursor.getDefaultCursor() );
		button.addActionListener( e -> clearButtonClicked() );
		return button;
	}

	/** @since 2 */
	@SuppressWarnings( "unchecked" )
	protected void clearButtonClicked() {
		JTextComponent c = getComponent();
		Object callback = c.getClientProperty( TEXT_FIELD_CLEAR_CALLBACK );
		if( callback instanceof Runnable )
			((Runnable)callback).run();
		else if( callback instanceof Consumer )
			((Consumer<JTextComponent>)callback).accept( c );
		else
			c.setText( "" );
	}

	/** @since 2 */
	protected void updateClearButton() {
		if( clearButton == null )
			return;

		JTextComponent c = getComponent();
		boolean visible = c.isEnabled() && c.isEditable() && c.getDocument().getLength() > 0;
		if( visible != clearButton.isVisible() ) {
			clearButton.setVisible( visible );
			c.revalidate();
			c.repaint();
		}
	}

	/**
	 * Returns components placed at the leading side of the text field.
	 * The returned array may contain {@code null}.
	 * The default implementation returns {@link #leadingComponent}.
	 *
	 * @since 2
	 */
	protected JComponent[] getLeadingComponents() {
		return new JComponent[] { leadingComponent };
	}

	/**
	 * Returns components placed at the trailing side of the text field.
	 * The returned array may contain {@code null}.
	 * The default implementation returns {@link #trailingComponent} and {@link #clearButton}.
	 * <p>
	 * <strong>Note</strong>: The components in the array must be in reverse (visual) order.
	 *
	 * @since 2
	 */
	protected JComponent[] getTrailingComponents() {
		return new JComponent[] { trailingComponent, clearButton };
	}

	/** @since 2 */
	protected void prepareLeadingOrTrailingComponent( JComponent c ) {
		c.putClientProperty( STYLE_CLASS, "inTextField" );

		if( c instanceof JButton || c instanceof JToggleButton ) {
			c.putClientProperty( BUTTON_TYPE, BUTTON_TYPE_TOOLBAR_BUTTON );

			if( !c.isCursorSet() )
				c.setCursor( Cursor.getDefaultCursor() );
		} else if( c instanceof JToolBar ) {
			for( Component child : c.getComponents() ) {
				if( child instanceof JComponent )
					((JComponent)child).putClientProperty( STYLE_CLASS, "inTextField" );
			}

			if( !c.isCursorSet() )
				c.setCursor( Cursor.getDefaultCursor() );
		}
	}

	/** @since 2 */
	protected void installLayout() {
		JTextComponent c = getComponent();
		LayoutManager oldLayout = c.getLayout();
		if( !(oldLayout instanceof FlatTextFieldLayout) )
			c.setLayout( new FlatTextFieldLayout( oldLayout ) );
	}

	//---- class FlatTextFieldLayout ------------------------------------------

	private class FlatTextFieldLayout
		implements LayoutManager2, UIResource
	{
		private final LayoutManager delegate;

		FlatTextFieldLayout( LayoutManager delegate ) {
			this.delegate = delegate;
		}

		@Override
		public void addLayoutComponent( String name, Component comp ) {
			if( delegate != null )
				delegate.addLayoutComponent( name, comp );
		}

		@Override
		public void removeLayoutComponent( Component comp ) {
			if( delegate != null )
				delegate.removeLayoutComponent( comp );
		}

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			return (delegate != null) ? delegate.preferredLayoutSize( parent ) : null;
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			return (delegate != null) ? delegate.minimumLayoutSize( parent ) : null;
		}

		@Override
		public void layoutContainer( Container parent ) {
			if( delegate != null )
				delegate.layoutContainer( parent );

			int ow = FlatUIUtils.getBorderFocusAndLineWidth( getComponent() );
			int h = parent.getHeight() - ow - ow;
			boolean ltr = isLeftToRight();
			JComponent[] leftComponents = ltr ? getLeadingComponents() : getTrailingComponents();
			JComponent[] rightComponents = ltr ? getTrailingComponents() : getLeadingComponents();

			// layout left components
			int x = ow;
			for( JComponent leftComponent : leftComponents ) {
				if( leftComponent != null && leftComponent.isVisible() ) {
					int cw = leftComponent.getPreferredSize().width;
					leftComponent.setBounds( x, ow, cw, h );
					x += cw;
				}
			}

			// layout right components
			x = parent.getWidth() - ow;
			for( JComponent rightComponent : rightComponents ) {
				if( rightComponent != null && rightComponent.isVisible() ) {
					int cw = rightComponent.getPreferredSize().width;
					x -= cw;
					rightComponent.setBounds( x, ow, cw, h );
				}
			}
		}

		@Override
		public void addLayoutComponent( Component comp, Object constraints ) {
			if( delegate instanceof LayoutManager2 )
				((LayoutManager2)delegate).addLayoutComponent( comp, constraints );
		}

		@Override
		public Dimension maximumLayoutSize( Container target ) {
			return (delegate instanceof LayoutManager2) ? ((LayoutManager2)delegate).maximumLayoutSize( target ) : null;
		}

		@Override
		public float getLayoutAlignmentX( Container target ) {
			return (delegate instanceof LayoutManager2) ? ((LayoutManager2)delegate).getLayoutAlignmentX( target ) : 0.5f;
		}

		@Override
		public float getLayoutAlignmentY( Container target ) {
			return (delegate instanceof LayoutManager2) ? ((LayoutManager2)delegate).getLayoutAlignmentY( target ) : 0.5f;
		}

		@Override
		public void invalidateLayout( Container target ) {
			if( delegate instanceof LayoutManager2 )
				((LayoutManager2)delegate).invalidateLayout( target );
		}
	}

	//---- class FlatDocumentListener -----------------------------------------

	private class FlatDocumentListener
		implements DocumentListener
	{
		@Override
		public void insertUpdate( DocumentEvent e ) {
			documentChanged( e );
		}

		@Override
		public void removeUpdate( DocumentEvent e ) {
			documentChanged( e );
		}

		@Override
		public void changedUpdate( DocumentEvent e ) {
			documentChanged( e );
		}
	}
}
