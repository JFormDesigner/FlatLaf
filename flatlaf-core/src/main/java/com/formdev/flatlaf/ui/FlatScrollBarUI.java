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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Objects;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableField;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableLookupProvider;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JScrollBar}.
 *
 * <!-- BasicScrollBarUI -->
 *
 * @uiDefault ScrollBar.background			Color
 * @uiDefault ScrollBar.foreground			Color	unused
 * @uiDefault ScrollBar.track				Color
 * @uiDefault ScrollBar.thumb				Color
 * @uiDefault ScrollBar.width				int
 * @uiDefault ScrollBar.minimumThumbSize	Dimension
 * @uiDefault ScrollBar.maximumThumbSize	Dimension
 * @uiDefault ScrollBar.allowsAbsolutePositioning	boolean
 *
 * <!-- FlatScrollBarUI -->
 *
 * @uiDefault ScrollBar.minimumButtonSize			Dimension
 * @uiDefault ScrollBar.trackInsets					Insets
 * @uiDefault ScrollBar.thumbInsets					Insets
 * @uiDefault ScrollBar.trackArc					int
 * @uiDefault ScrollBar.thumbArc					int
 * @uiDefault ScrollBar.hoverTrackColor				Color	optional
 * @uiDefault ScrollBar.hoverThumbColor				Color	optional
 * @uiDefault ScrollBar.hoverThumbWithTrack			boolean
 * @uiDefault ScrollBar.pressedTrackColor			Color	optional
 * @uiDefault ScrollBar.pressedThumbColor			Color	optional
 * @uiDefault ScrollBar.pressedThumbWithTrack		boolean
 * @uiDefault Component.arrowType					String	chevron (default) or triangle
 * @uiDefault ScrollBar.showButtons					boolean
 * @uiDefault ScrollBar.buttonArrowColor			Color
 * @uiDefault ScrollBar.buttonDisabledArrowColor	Color
 * @uiDefault ScrollBar.hoverButtonBackground		Color	optional
 * @uiDefault ScrollBar.pressedButtonBackground		Color	optional
 *
 * @author Karl Tauber
 */
@StyleableField( cls=BasicScrollBarUI.class, key="track", fieldName="trackColor" )
@StyleableField( cls=BasicScrollBarUI.class, key="thumb", fieldName="thumbColor" )
@StyleableField( cls=BasicScrollBarUI.class, key="width", fieldName="scrollBarWidth" )
@StyleableField( cls=BasicScrollBarUI.class, key="minimumThumbSize" )
@StyleableField( cls=BasicScrollBarUI.class, key="maximumThumbSize" )

public class FlatScrollBarUI
	extends BasicScrollBarUI
	implements StyleableUI, StyleableLookupProvider
{
	// overrides BasicScrollBarUI.supportsAbsolutePositioning (which is private)
	@Styleable protected boolean allowsAbsolutePositioning;

	/** @since 2.1 */ @Styleable protected Dimension minimumButtonSize;
	@Styleable protected Insets trackInsets;
	@Styleable protected Insets thumbInsets;
	@Styleable protected int trackArc;
	@Styleable protected int thumbArc;
	@Styleable protected Color hoverTrackColor;
	@Styleable protected Color hoverThumbColor;
	@Styleable protected boolean hoverThumbWithTrack;
	@Styleable protected Color pressedTrackColor;
	@Styleable protected Color pressedThumbColor;
	@Styleable protected boolean pressedThumbWithTrack;

	@Styleable protected boolean showButtons;
	@Styleable protected String arrowType;
	@Styleable protected Color buttonArrowColor;
	@Styleable protected Color buttonDisabledArrowColor;
	@Styleable protected Color hoverButtonBackground;
	@Styleable protected Color pressedButtonBackground;

	private MouseAdapter hoverListener;
	protected boolean hoverTrack;
	protected boolean hoverThumb;

	private Map<String, Object> oldStyleValues;
	private boolean isAWTPeer;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatScrollBarUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		hoverListener = new ScrollBarHoverListener();
		scrollbar.addMouseListener( hoverListener );
		scrollbar.addMouseMotionListener( hoverListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		scrollbar.removeMouseListener( hoverListener );
		scrollbar.removeMouseMotionListener( hoverListener );
		hoverListener = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		allowsAbsolutePositioning = super.getSupportsAbsolutePositioning();

		minimumButtonSize = UIManager.getDimension( "ScrollBar.minimumButtonSize" );
		trackInsets = UIManager.getInsets( "ScrollBar.trackInsets" );
		thumbInsets = UIManager.getInsets( "ScrollBar.thumbInsets" );
		trackArc = UIManager.getInt( "ScrollBar.trackArc" );
		thumbArc = UIManager.getInt( "ScrollBar.thumbArc" );
		hoverTrackColor = UIManager.getColor( "ScrollBar.hoverTrackColor" );
		hoverThumbColor = UIManager.getColor( "ScrollBar.hoverThumbColor" );
		hoverThumbWithTrack = UIManager.getBoolean( "ScrollBar.hoverThumbWithTrack" );
		pressedTrackColor = UIManager.getColor( "ScrollBar.pressedTrackColor" );
		pressedThumbColor = UIManager.getColor( "ScrollBar.pressedThumbColor" );
		pressedThumbWithTrack = UIManager.getBoolean( "ScrollBar.pressedThumbWithTrack" );

		showButtons = UIManager.getBoolean( "ScrollBar.showButtons" );
		arrowType = UIManager.getString( "Component.arrowType" );
		buttonArrowColor = UIManager.getColor( "ScrollBar.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "ScrollBar.buttonDisabledArrowColor" );
		hoverButtonBackground = UIManager.getColor( "ScrollBar.hoverButtonBackground" );
		pressedButtonBackground = UIManager.getColor( "ScrollBar.pressedButtonBackground" );

		// fallback (e.g. when used in NetBeans GUI builder)
		if( trackInsets == null )
			trackInsets = new Insets( 0, 0, 0, 0 );
		if( thumbInsets == null )
			thumbInsets = new Insets( 0, 0, 0, 0 );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		minimumButtonSize = null;
		trackInsets = null;
		thumbInsets = null;
		hoverTrackColor = null;
		hoverThumbColor = null;
		pressedTrackColor = null;
		pressedThumbColor = null;

		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		hoverButtonBackground = null;
		pressedButtonBackground = null;

		oldStyleValues = null;
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			switch( e.getPropertyName() ) {
				case FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS:
					scrollbar.revalidate();
					scrollbar.repaint();
					break;

				case FlatClientProperties.STYLE:
				case FlatClientProperties.STYLE_CLASS:
					installStyle();
					scrollbar.revalidate();
					scrollbar.repaint();
					break;

				case "componentOrientation":
					// this is missing in BasicScrollBarUI.Handler.propertyChange()
					InputMap inputMap = (InputMap) UIManager.get( "ScrollBar.ancestorInputMap" );
					if( !scrollbar.getComponentOrientation().isLeftToRight() ) {
						InputMap rtlInputMap = (InputMap) UIManager.get( "ScrollBar.ancestorInputMap.RightToLeft" );
						if( rtlInputMap != null ) {
							rtlInputMap.setParent( inputMap );
							inputMap = rtlInputMap;
						}
					}
					SwingUtilities.replaceUIInputMap( scrollbar, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap );
					break;

				case "ancestor":
					// check whether scroll bar is used as AWT peer on macOS
					if( SystemInfo.isMacOS ) {
						Container p = scrollbar.getParent();
						for( int i = 0; i < 2 && p != null; i++, p = p.getParent() ) {
							if( FlatUIUtils.isAWTPeer( p ) ) {
								// Used to disable hover, which does not work correctly
								// because scroll bars do not receive mouse exited event.
								// The scroll pane, including its scroll bars, is not part
								// of the component hierarchy and does not receive mouse events
								// directly. Instead LWComponentPeer receives mouse events
								// and delegates them to peers, but entered/exited events
								// are sent only for the whole scroll pane.
								// Exited event is only sent when mouse leaves scroll pane.
								// If mouse enters/exits scroll bar, no entered/exited events are sent.
								isAWTPeer = true;

								// if dark theme is active, reinstall using light theme
								if( FlatLaf.isLafDark() ) {
									FlatUIUtils.runWithLightAWTPeerUIDefaults( () -> {
										JScrollBar scrollbar = this.scrollbar;
										uninstallUI( scrollbar );
										installUI( scrollbar );
									} );
								}
								break;
							}
						}
					}
					break;
			}
		};
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( scrollbar, "ScrollBar" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );

		if( incrButton instanceof FlatScrollBarButton )
			((FlatScrollBarButton)incrButton).updateStyle();
		if( decrButton instanceof FlatScrollBarButton )
			((FlatScrollBarButton)decrButton).updateStyle();
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		return FlatStylingSupport.applyToAnnotatedObjectOrComponent( this, scrollbar, key, value );
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

	/** @since 2.5 */
	@Override
	public MethodHandles.Lookup getLookupForStyling() {
		// MethodHandles.lookup() is caller sensitive and must be invoked in this class,
		// otherwise it is not possible to access protected fields in JRE superclass
		return MethodHandles.lookup();
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( super.getPreferredSize( c ) );
	}

	@Override
	protected JButton createDecreaseButton( int orientation ) {
		return new FlatScrollBarButton( orientation );
	}

	@Override
	protected JButton createIncreaseButton( int orientation ) {
		return new FlatScrollBarButton( orientation );
	}

	protected boolean isShowButtons() {
		// check client property on scroll bar
		Object showButtons = scrollbar.getClientProperty( FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS );
		if( showButtons == null && scrollbar.getParent() instanceof JScrollPane ) {
			JScrollPane scrollPane = (JScrollPane) scrollbar.getParent();
			// check client property on scroll pane
			showButtons = scrollPane.getClientProperty( FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS );
			if( showButtons == null && scrollPane.getUI() instanceof FlatScrollPaneUI ) {
				// check styling property on scroll pane
				showButtons = ((FlatScrollPaneUI)scrollPane.getUI()).showButtons;
			}
		}
		return (showButtons != null) ? Objects.equals( showButtons, true ) : this.showButtons;
	}

	@Override
	public void paint( Graphics g, JComponent c ) {
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
		super.paint( g, c );
		FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
	}

	@Override
	protected void paintTrack( Graphics g, JComponent c, Rectangle trackBounds ) {
		if( trackBounds.isEmpty() || !scrollbar.isEnabled() )
			return;

		g.setColor( getTrackColor( c, hoverTrack, isPressed && hoverTrack && !hoverThumb ) );
		paintTrackOrThumb( g, c, trackBounds, trackInsets, trackArc );
	}

	@Override
	protected void paintThumb( Graphics g, JComponent c, Rectangle thumbBounds ) {
		if( thumbBounds.isEmpty() || !scrollbar.isEnabled() )
			return;

		g.setColor( getThumbColor( c, hoverThumb || (hoverThumbWithTrack && hoverTrack),
			isPressed && (hoverThumb || (pressedThumbWithTrack && hoverTrack)) ) );
		paintTrackOrThumb( g, c, thumbBounds, thumbInsets, thumbArc );
	}

	protected void paintTrackOrThumb( Graphics g, JComponent c, Rectangle bounds, Insets insets, int arc ) {
		// rotate insets for horizontal orientation because they are given for vertical orientation
		if( scrollbar.getOrientation() == JScrollBar.HORIZONTAL )
			insets = new Insets( insets.right, insets.top, insets.left, insets.bottom );

		// subtract insets from bounds
		bounds = FlatUIUtils.subtractInsets( bounds, UIScale.scale( insets ) );

		if( arc <= 0 ) {
			// paint rectangle
			g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
		} else {
			// paint round rectangle
			arc = Math.min( UIScale.scale( arc ), Math.min( bounds.width, bounds.height ) );
			g.fillRoundRect( bounds.x, bounds.y, bounds.width, bounds.height, arc, arc );
		}
	}

	@Override
	protected void paintDecreaseHighlight( Graphics g ) {
		// do not paint
	}

	@Override
	protected void paintIncreaseHighlight( Graphics g ) {
		// do not paint
	}

	protected Color getTrackColor( JComponent c, boolean hover, boolean pressed ) {
		Color trackColor = FlatUIUtils.deriveColor( this.trackColor, c.getBackground() );
		return (pressed && pressedTrackColor != null)
			? FlatUIUtils.deriveColor( pressedTrackColor, trackColor )
			: ((hover && hoverTrackColor != null && !isAWTPeer)
				? FlatUIUtils.deriveColor( hoverTrackColor, trackColor )
				: trackColor);
	}

	protected Color getThumbColor( JComponent c, boolean hover, boolean pressed ) {
		Color trackColor = FlatUIUtils.deriveColor( this.trackColor, c.getBackground() );
		Color thumbColor = FlatUIUtils.deriveColor( this.thumbColor, trackColor );
		return (pressed && pressedThumbColor != null)
			? FlatUIUtils.deriveColor( pressedThumbColor, thumbColor )
			: ((hover && hoverThumbColor != null && !isAWTPeer)
				? FlatUIUtils.deriveColor( hoverThumbColor, thumbColor )
				: thumbColor);
	}

	@Override
	protected Dimension getMinimumThumbSize() {
		return UIScale.scale( FlatUIUtils.addInsets( super.getMinimumThumbSize(), thumbInsets ) );
	}

	@Override
	protected Dimension getMaximumThumbSize() {
		return UIScale.scale( FlatUIUtils.addInsets( super.getMaximumThumbSize(), thumbInsets ) );
	}

	@Override
	public boolean getSupportsAbsolutePositioning() {
		return allowsAbsolutePositioning;
	}

	//---- class ScrollBarHoverListener ---------------------------------------

	// using static field to disabling hover for other scroll bars
	private static boolean isPressed;

	private class ScrollBarHoverListener
		extends MouseAdapter
	{
		@Override
		public void mouseExited( MouseEvent e ) {
			if( !isPressed ) {
				hoverTrack = hoverThumb = false;
				repaint();
			}
		}

		@Override
		public void mouseMoved( MouseEvent e ) {
			if( !isPressed )
				update( e.getX(), e.getY() );
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			if( SwingUtilities.isLeftMouseButton( e ) || isAbsolutePositioning( e ) ) {
				isPressed = true;
				repaint();

				// update hover because BasicScrollBarUI.TrackListener.mousePressed()
				// moves the track on middle-click (if absolute positioning is enabled)
				if( isAbsolutePositioning( e ) )
					update( e.getX(), e.getY() );
			}
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			if( SwingUtilities.isLeftMouseButton( e ) || isAbsolutePositioning( e ) ) {
				isPressed = false;
				repaint();
			}

			update( e.getX(), e.getY() );
		}

		private boolean isAbsolutePositioning( MouseEvent e ) {
			return getSupportsAbsolutePositioning() && SwingUtilities.isMiddleMouseButton( e );
		}

		private void update( int x, int y ) {
			boolean inTrack = getTrackBounds().contains( x, y );
			boolean inThumb = getThumbBounds().contains( x, y );
			if( inTrack != hoverTrack || inThumb != hoverThumb ) {
				hoverTrack = inTrack;
				hoverThumb = inThumb;
				repaint();
			}
		}

		private void repaint() {
			if( scrollbar.isEnabled() )
				scrollbar.repaint();
		}
	}

	//---- class FlatScrollBarButton ------------------------------------------

	protected class FlatScrollBarButton
		extends FlatArrowButton
	{
		protected FlatScrollBarButton( int direction ) {
			this( direction, arrowType, buttonArrowColor, buttonDisabledArrowColor,
				null, hoverButtonBackground, null, pressedButtonBackground );
		}

		protected FlatScrollBarButton( int direction, String type, Color foreground, Color disabledForeground,
			Color hoverForeground, Color hoverBackground, Color pressedForeground, Color pressedBackground )
		{
			super( direction, type, foreground, disabledForeground,
				hoverForeground, hoverBackground, pressedForeground, pressedBackground );

			setFocusable( false );
			setRequestFocusEnabled( false );
		}

		protected void updateStyle() {
			updateStyle( arrowType, buttonArrowColor, buttonDisabledArrowColor,
				null, hoverButtonBackground, null, pressedButtonBackground );
		}

		@Override
		public int getArrowWidth() {
			// scale arrow size depending on scroll bar width
			// (6 is default arrow width; 10 is base scroll bar width)
			int arrowWidth = Math.round( 6 * (scrollBarWidth / 10f) );

			// compute arrow size that leaves equal space on both sides (arrow is centered)
			arrowWidth = scrollBarWidth - (((scrollBarWidth - arrowWidth) / 2) * 2);

			return arrowWidth;
		}

		@Override
		protected Color deriveBackground( Color background ) {
			return FlatUIUtils.deriveColor( background, scrollbar.getBackground() );
		}

		@Override
		public Dimension getPreferredSize() {
			if( isShowButtons() ) {
				int w = UIScale.scale( Math.max( scrollBarWidth, (minimumButtonSize != null) ? minimumButtonSize.width : 0 ) );
				int h = UIScale.scale( Math.max( scrollBarWidth, (minimumButtonSize != null) ? minimumButtonSize.height : 0 ) );
				return new Dimension( w, h );
			} else
				return new Dimension();
		}

		@Override
		public Dimension getMinimumSize() {
			return isShowButtons() ? super.getMinimumSize() : new Dimension();
		}

		@Override
		public Dimension getMaximumSize() {
			return isShowButtons() ? super.getMaximumSize() : new Dimension();
		}
	}
}
