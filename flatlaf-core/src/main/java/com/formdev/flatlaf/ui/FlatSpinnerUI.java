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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSpinnerUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.HiDPIUtils;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSpinner}.
 *
 * <!-- BasicSpinnerUI -->
 *
 * @uiDefault Spinner.font						Font
 * @uiDefault Spinner.background				Color
 * @uiDefault Spinner.foreground				Color
 * @uiDefault Spinner.border					Border
 * @uiDefault Spinner.disableOnBoundaryValues	boolean	default is false
 * @uiDefault Spinner.editorAlignment			int		0=center, 2=left, 4=right, 10=leading, 11=trailing
 * @uiDefault Spinner.editorBorderPainted		boolean	paint inner editor border; defaults to false
 *
 * <!-- FlatSpinnerUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Spinner.buttonStyle				String	button (default), mac or none
 * @uiDefault Component.arrowType				String	chevron (default) or triangle
 * @uiDefault Spinner.disabledBackground		Color
 * @uiDefault Spinner.disabledForeground		Color
 * @uiDefault Spinner.focusedBackground			Color	optional
 * @uiDefault Spinner.buttonBackground			Color	optional
 * @uiDefault Spinner.buttonSeparatorWidth		int or float	optional; defaults to Component.borderWidth
 * @uiDefault Spinner.buttonSeparatorColor		Color	optional
 * @uiDefault Spinner.buttonDisabledSeparatorColor Color	optional
 * @uiDefault Spinner.buttonArrowColor			Color
 * @uiDefault Spinner.buttonDisabledArrowColor	Color
 * @uiDefault Spinner.buttonHoverArrowColor		Color
 * @uiDefault Spinner.buttonPressedArrowColor	Color
 * @uiDefault Spinner.padding					Insets
 *
 * @author Karl Tauber
 */
public class FlatSpinnerUI
	extends BasicSpinnerUI
	implements StyleableUI
{
	private Handler handler;

	@Styleable protected int minimumWidth;
	@Styleable protected String buttonStyle;
	@Styleable protected String arrowType;
	@Styleable protected Color disabledBackground;
	@Styleable protected Color disabledForeground;
	@Styleable protected Color focusedBackground;
	@Styleable protected Color buttonBackground;
	/** @since 2 */ @Styleable protected float buttonSeparatorWidth;
	/** @since 2 */ @Styleable protected Color buttonSeparatorColor;
	/** @since 2 */ @Styleable protected Color buttonDisabledSeparatorColor;
	@Styleable protected Color buttonArrowColor;
	@Styleable protected Color buttonDisabledArrowColor;
	@Styleable protected Color buttonHoverArrowColor;
	@Styleable protected Color buttonPressedArrowColor;
	@Styleable protected Insets padding;

	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSpinnerUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( spinner, "opaque", false );

		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		buttonStyle = UIManager.getString( "Spinner.buttonStyle" );
		arrowType = UIManager.getString( "Component.arrowType" );
		disabledBackground = UIManager.getColor( "Spinner.disabledBackground" );
		disabledForeground = UIManager.getColor( "Spinner.disabledForeground" );
		focusedBackground = UIManager.getColor( "Spinner.focusedBackground" );
		buttonBackground = UIManager.getColor( "Spinner.buttonBackground" );
		buttonSeparatorWidth = FlatUIUtils.getUIFloat( "Spinner.buttonSeparatorWidth", FlatUIUtils.getUIFloat( "Component.borderWidth", 1 ) );
		buttonSeparatorColor = UIManager.getColor( "Spinner.buttonSeparatorColor" );
		buttonDisabledSeparatorColor = UIManager.getColor( "Spinner.buttonDisabledSeparatorColor" );
		buttonArrowColor = UIManager.getColor( "Spinner.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "Spinner.buttonDisabledArrowColor" );
		buttonHoverArrowColor = UIManager.getColor( "Spinner.buttonHoverArrowColor" );
		buttonPressedArrowColor = UIManager.getColor( "Spinner.buttonPressedArrowColor" );
		padding = UIManager.getInsets( "Spinner.padding" );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		disabledBackground = null;
		disabledForeground = null;
		focusedBackground = null;
		buttonBackground = null;
		buttonSeparatorColor = null;
		buttonDisabledSeparatorColor = null;
		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		buttonHoverArrowColor = null;
		buttonPressedArrowColor = null;
		padding = null;

		oldStyleValues = null;
		borderShared = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		addEditorFocusListener( spinner.getEditor() );
		spinner.addFocusListener( getHandler() );
		spinner.addPropertyChangeListener( getHandler() );

		MigLayoutVisualPadding.install( spinner );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		removeEditorFocusListener( spinner.getEditor() );
		spinner.removeFocusListener( getHandler() );
		spinner.removePropertyChangeListener( getHandler() );

		handler = null;

		MigLayoutVisualPadding.uninstall( spinner );
	}

	private Handler getHandler() {
		if( handler == null )
			handler = new Handler();
		return handler;
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( spinner, "Spinner" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
		updateEditorPadding();
		updateArrowButtonsStyle();
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, spinner, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, spinner.getBorder() );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, spinner.getBorder(), key );
	}

	@Override
	protected JComponent createEditor() {
		JComponent editor = super.createEditor();
		configureEditor( editor );
		return editor;
	}

	@Override
	protected void replaceEditor( JComponent oldEditor, JComponent newEditor ) {
		super.replaceEditor( oldEditor, newEditor );

		configureEditor( newEditor );

		removeEditorFocusListener( oldEditor );
		addEditorFocusListener( newEditor );
	}

	/** @since 1.6 */
	protected void configureEditor( JComponent editor ) {
		// explicitly make non-opaque
		editor.setOpaque( false );
		JTextField textField = getEditorTextField( editor );
		if( textField != null )
			textField.setOpaque( false );

		updateEditorPadding();
		updateEditorColors();
	}

	private void addEditorFocusListener( JComponent editor ) {
		JTextField textField = getEditorTextField( editor );
		if( textField != null )
			textField.addFocusListener( getHandler() );
	}

	private void removeEditorFocusListener( JComponent editor ) {
		JTextField textField = getEditorTextField( editor );
		if( textField != null )
			textField.removeFocusListener( getHandler() );
	}

	private void updateEditorPadding() {
		JTextField textField = getEditorTextField( spinner.getEditor() );
		if( textField != null )
			textField.putClientProperty( FlatClientProperties.TEXT_FIELD_PADDING, padding );
	}

	private void updateEditorColors() {
		JTextField textField = getEditorTextField( spinner.getEditor() );
		if( textField != null ) {
			// use non-UIResource colors because when SwingUtilities.updateComponentTreeUI()
			// is used, then the text field is updated after the spinner and the
			// colors are again replaced with default colors
			textField.setForeground( FlatUIUtils.nonUIResource( getForeground( true ) ) );
			textField.setDisabledTextColor( FlatUIUtils.nonUIResource( getForeground( false ) ) );
		}
	}

	private static JTextField getEditorTextField( JComponent editor ) {
		return editor instanceof JSpinner.DefaultEditor
			? ((JSpinner.DefaultEditor)editor).getTextField()
			: null;
	}

	/** @since 1.3 */
	public static boolean isPermanentFocusOwner( JSpinner spinner ) {
		if( FlatUIUtils.isPermanentFocusOwner( spinner ) )
			return true;

		JTextField textField = getEditorTextField( spinner.getEditor() );
		return textField != null && FlatUIUtils.isPermanentFocusOwner( textField );
	}

	protected Color getBackground( boolean enabled ) {
		if( enabled ) {
			Color background = spinner.getBackground();

			// always use explicitly set color
			if( !(background instanceof UIResource) )
				return background;

			// focused
			if( focusedBackground != null && isPermanentFocusOwner( spinner ) )
				return focusedBackground;

			return background;
		} else
			return disabledBackground;
	}

	protected Color getForeground( boolean enabled ) {
		return enabled ? spinner.getForeground() : disabledForeground;
	}

	@Override
	protected LayoutManager createLayout() {
		return getHandler();
	}

	@Override
	protected Component createNextButton() {
		return createArrowButton( SwingConstants.NORTH, "Spinner.nextButton" );
	}

	@Override
	protected Component createPreviousButton() {
		return createArrowButton( SwingConstants.SOUTH, "Spinner.previousButton" );
	}

	private Component createArrowButton( int direction, String name ) {
		FlatArrowButton button = new FlatArrowButton( direction, arrowType, buttonArrowColor,
			buttonDisabledArrowColor, buttonHoverArrowColor, null, buttonPressedArrowColor, null )
		{
			@Override
			public int getArrowWidth() {
				return isMacStyle() ? 7 : super.getArrowWidth();
			}
			@Override
			public float getArrowThickness() {
				return isMacStyle() ? 1.5f : super.getArrowThickness();
			}
			@Override
			public float getYOffset() {
				return isMacStyle() ? 0 : super.getYOffset();
			}
			@Override
			public boolean isRoundBorderAutoXOffset() {
				return isMacStyle() ? false : super.isRoundBorderAutoXOffset();
			}
		};
		button.setName( name );
		button.setYOffset( (direction == SwingConstants.NORTH) ? 1.25f : -1.25f );
		if( direction == SwingConstants.NORTH )
			installNextButtonListeners( button );
		else
			installPreviousButtonListeners( button );
		return button;
	}

	private void updateArrowButtonsStyle() {
		for( Component c : spinner.getComponents() ) {
			if( c instanceof FlatArrowButton ) {
				((FlatArrowButton)c).updateStyle( arrowType, buttonArrowColor,
					buttonDisabledArrowColor, buttonHoverArrowColor, null, buttonPressedArrowColor, null );
			}
		}
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
		float arc = FlatUIUtils.getBorderArc( c );

		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() && (focusWidth > 0 || arc > 0) )
			FlatUIUtils.paintParentBackground( g, c );

		Graphics2D g2 = (Graphics2D) g;
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );

		int width = c.getWidth();
		int height = c.getHeight();
		boolean enabled = spinner.isEnabled();
		boolean ltr = spinner.getComponentOrientation().isLeftToRight();
		boolean isMacStyle = isMacStyle();
		int macStyleButtonsWidth = isMacStyle ? getMacStyleButtonsWidth() : 0;

		// paint background
		g2.setColor( getBackground( enabled ) );
		FlatUIUtils.paintComponentBackground( g2, ltr ? 0 : macStyleButtonsWidth, 0, width - macStyleButtonsWidth, height, focusWidth, arc );

		// paint button background and separator
		boolean paintButton = !"none".equals( buttonStyle );
		Handler handler = getHandler();
		if( paintButton && (handler.nextButton != null || handler.previousButton != null) ) {
			Component button = (handler.nextButton != null) ? handler.nextButton : handler.previousButton;
			int arrowX = button.getX();
			int arrowWidth = button.getWidth();
			Color separatorColor = enabled ? buttonSeparatorColor : buttonDisabledSeparatorColor;

			if( isMacStyle ) {
				Insets insets = spinner.getInsets();
				int lineWidth = Math.round( FlatUIUtils.getBorderLineWidth( spinner ) );
				int bx = arrowX;
				int by = insets.top - lineWidth;
				int bw = arrowWidth;
				int bh = height - insets.top - insets.bottom + (lineWidth * 2);
				float lw = scale( buttonSeparatorWidth );

				// buttons border
				FlatUIUtils.paintOutlinedComponent( g2, bx, by, bw, bh,
					0, 0, 0, lw, scale( 12 ),
					null, separatorColor, buttonBackground );

				// separator between buttons
				if( separatorColor != null ) {
					int thickness = scale( 1 );
					g2.setColor( separatorColor );
					g2.fill( new Rectangle2D.Float( bx + lw, by + ((bh - thickness) / 2f),
						bw - (lw * 2), thickness ) );
				}
			} else {
				// paint arrow buttons background
				if( enabled && buttonBackground != null ) {
					g2.setColor( buttonBackground );
					Shape oldClip = g2.getClip();
					if( ltr )
						g2.clipRect( arrowX, 0, width - arrowX, height );
					else
						g2.clipRect( 0, 0, arrowX + arrowWidth, height );
					FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );
					g2.setClip( oldClip );
				}

				// paint vertical line between value and arrow buttons
				if( separatorColor != null && buttonSeparatorWidth > 0 ) {
					g2.setColor( separatorColor );
					float lw = scale( buttonSeparatorWidth );
					float lx = ltr ? arrowX : arrowX + arrowWidth - lw;
					g2.fill( new Rectangle2D.Float( lx, focusWidth, lw, height - 1 - (focusWidth * 2) ) );
				}
			}
		}

		paint( g, c );

		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	boolean isMacStyle() {
		return "mac".equals( buttonStyle );
	}

	int getMacStyleButtonsWidth() {
		return (handler.nextButton != null || handler.previousButton != null)
			? scale( MAC_STEPPER_GAP ) + scale( MAC_STEPPER_WIDTH )
			: 0;
	}

	private static final int MAC_STEPPER_WIDTH = 15;
	private static final int MAC_STEPPER_GAP = 3;

	//---- class Handler ------------------------------------------------------

	private class Handler
		implements LayoutManager, FocusListener, PropertyChangeListener
	{
		//---- interface LayoutManager ----

		private Component editor = null;
		private Component nextButton;
		private Component previousButton;

		@Override
		public void addLayoutComponent( String name, Component c ) {
			switch( name ) {
				case "Editor":	editor = c; break;
				case "Next":		nextButton = c; break;
				case "Previous":	previousButton = c; break;
			}
		}

		@Override
		public void removeLayoutComponent( Component c ) {
			if( c == editor )
				editor = null;
			else if( c == nextButton )
				nextButton = null;
			else if( c == previousButton )
				previousButton = null;
		}

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			Insets insets = parent.getInsets();
			Insets padding = scale( FlatSpinnerUI.this.padding );
			Dimension editorSize = (editor != null) ? editor.getPreferredSize() : new Dimension( 0, 0 );

			// the arrow buttons width is the same as the inner height so that the arrow buttons area is square
			int minimumWidth = FlatUIUtils.minimumWidth( spinner, FlatSpinnerUI.this.minimumWidth );
			int innerHeight = editorSize.height + padding.top + padding.bottom;
			float focusWidth = FlatUIUtils.getBorderFocusWidth( spinner );
			return new Dimension(
				Math.max( insets.left + insets.right + editorSize.width + padding.left + padding.right + innerHeight, scale( minimumWidth ) + Math.round( focusWidth * 2 ) ),
				insets.top + insets.bottom + innerHeight );
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			return preferredLayoutSize( parent );
		}

		@Override
		public void layoutContainer( Container parent ) {
			Dimension size = parent.getSize();
			Insets insets = parent.getInsets();
			Rectangle r = FlatUIUtils.subtractInsets( new Rectangle( size ), insets );

			// editor gets all space if there are no buttons
			if( nextButton == null && previousButton == null ) {
				if( editor != null )
					editor.setBounds( r );
				return;
			}

			Rectangle editorRect = new Rectangle( r );
			Rectangle buttonsRect = new Rectangle( r );

			// limit buttons width to height of a raw spinner (without insets)
			FontMetrics fm = spinner.getFontMetrics( spinner.getFont() );
			int maxButtonWidth = fm.getHeight() + scale( padding.top ) + scale( padding.bottom );
			int minButtonWidth = (maxButtonWidth * 3) / 4;

			// make button area square (except if width is limited)
			boolean isMacStyle = isMacStyle();
			int buttonsGap = isMacStyle ? scale( MAC_STEPPER_GAP ) : 0;
			int prefButtonWidth = isMacStyle ? scale( MAC_STEPPER_WIDTH ) : buttonsRect.height;
			int buttonsWidth = Math.min( Math.max( prefButtonWidth, minButtonWidth ), maxButtonWidth );

			// update editor and buttons bounds
			buttonsRect.width = buttonsWidth;
			editorRect.width -= buttonsWidth + buttonsGap;
			boolean ltr = parent.getComponentOrientation().isLeftToRight();
			if( ltr )
				buttonsRect.x += editorRect.width + buttonsGap;
			else
				editorRect.x += buttonsWidth + buttonsGap;

			// in mac button style increase buttons height and move to the right
			// for exact alignment with border
			if( isMacStyle ) {
				int lineWidth = Math.round( FlatUIUtils.getBorderLineWidth( spinner ) );
				if( lineWidth > 0 ) {
					buttonsRect.x += ltr ? lineWidth : -lineWidth;
					buttonsRect.y -= lineWidth;
					buttonsRect.height += lineWidth * 2;
				}
			}

			// set editor bounds
			if( editor != null )
				editor.setBounds( editorRect );

			// set buttons bounds
			int nextHeight = (buttonsRect.height / 2) + (buttonsRect.height % 2); // round up
			if( nextButton != null )
				nextButton.setBounds( buttonsRect.x, buttonsRect.y, buttonsRect.width, nextHeight );
			if( previousButton != null ) {
				// for precise layout of arrows, the "previous" button has the same height
				// as the "next" button and may overlap on uneven buttonsRect.height
				int previousY = buttonsRect.y + buttonsRect.height - nextHeight;
				previousButton.setBounds( buttonsRect.x, previousY, buttonsRect.width, nextHeight );
			}
		}

		//---- interface FocusListener ----

		@Override
		public void focusGained( FocusEvent e ) {
			// necessary to update focus border
			HiDPIUtils.repaint( spinner );

			// if spinner gained focus, transfer it to the editor text field
			if( e.getComponent() == spinner ) {
				JTextField textField = getEditorTextField( spinner.getEditor() );
				if( textField != null )
					textField.requestFocusInWindow();
			}
		}

		@Override
		public void focusLost( FocusEvent e ) {
			// necessary to update focus border
			HiDPIUtils.repaint( spinner );
		}

		//---- interface PropertyChangeListener ----

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			switch( e.getPropertyName() ) {
				case "foreground":
				case "enabled":
					updateEditorColors();
					break;

				case FlatClientProperties.COMPONENT_ROUND_RECT:
				case FlatClientProperties.OUTLINE:
					HiDPIUtils.repaint( spinner );
					break;

				case FlatClientProperties.MINIMUM_WIDTH:
					spinner.revalidate();
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					spinner.revalidate();
					HiDPIUtils.repaint( spinner );
					break;
			}
		}
	}
}
