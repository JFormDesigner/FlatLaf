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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicMenuItemUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import javax.swing.plaf.basic.DefaultMenuLayout;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import com.formdev.flatlaf.ui.FlatStylingSupport.StyleableUI;
import com.formdev.flatlaf.util.LoggingFacade;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JPopupMenu}.
 *
 * <!-- BasicPopupMenuUI -->
 *
 * @uiDefault PopupMenu.font							Font
 * @uiDefault PopupMenu.background						Color
 * @uiDefault PopupMenu.foreground						Color
 * @uiDefault PopupMenu.border							Border
 *
 * <!-- FlatPopupMenuUI -->
 *
 * @uiDefault Component.arrowType					String	chevron (default) or triangle
 * @uiDefault PopupMenu.scrollArrowColor			Color
 * @uiDefault PopupMenu.hoverScrollArrowBackground	Color	optional
 *
 * @author Karl Tauber
 */
public class FlatPopupMenuUI
	extends BasicPopupMenuUI
	implements StyleableUI
{
	/** @since 2.1 */ @Styleable protected String arrowType;
	/** @since 2.1 */ @Styleable protected Color scrollArrowColor;
	/** @since 2.1 */ @Styleable protected Color hoverScrollArrowBackground;

	private PropertyChangeListener propertyChangeListener;
	private Map<String, Object> oldStyleValues;
	private AtomicBoolean borderShared;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatPopupMenuUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		installStyle();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		oldStyleValues = null;
		borderShared = null;
	}

	@Override
	public void installDefaults() {
		super.installDefaults();

		arrowType = UIManager.getString( "Component.arrowType" );
		scrollArrowColor = UIManager.getColor( "PopupMenu.scrollArrowColor" );
		hoverScrollArrowBackground = UIManager.getColor( "PopupMenu.hoverScrollArrowBackground" );

		LayoutManager layout = popupMenu.getLayout();
		if( layout == null || layout instanceof UIResource )
			popupMenu.setLayout( new FlatPopupMenuLayout( popupMenu, BoxLayout.Y_AXIS ) );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		scrollArrowColor = null;
		hoverScrollArrowBackground = null;
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		propertyChangeListener = FlatStylingSupport.createPropertyChangeListener( popupMenu, this::installStyle, null );
		popupMenu.addPropertyChangeListener( propertyChangeListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		popupMenu.removePropertyChangeListener( propertyChangeListener );
		propertyChangeListener = null;
	}

	/** @since 2 */
	protected void installStyle() {
		try {
			applyStyle( FlatStylingSupport.getResolvedStyle( popupMenu, "PopupMenu" ) );
		} catch( RuntimeException ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/** @since 2 */
	protected void applyStyle( Object style ) {
		oldStyleValues = FlatStylingSupport.parseAndApply( oldStyleValues, style, this::applyStyleProperty );
	}

	/** @since 2 */
	protected Object applyStyleProperty( String key, Object value ) {
		if( borderShared == null )
			borderShared = new AtomicBoolean( true );
		return FlatStylingSupport.applyToAnnotatedObjectOrBorder( this, key, value, popupMenu, borderShared );
	}

	/** @since 2 */
	@Override
	public Map<String, Class<?>> getStyleableInfos( JComponent c ) {
		return FlatStylingSupport.getAnnotatedStyleableInfos( this, popupMenu.getBorder() );
	}

	/** @since 2.5 */
	@Override
	public Object getStyleableValue( JComponent c, String key ) {
		return FlatStylingSupport.getAnnotatedStyleableValue( this, popupMenu.getBorder(), key );
	}

	@Override
	public Popup getPopup( JPopupMenu popup, int x, int y ) {
		// do not add scroller to combobox popups or to popups that already have a scroll pane
		if( popup instanceof BasicComboPopup ||
			(popup.getComponentCount() > 0 && popup.getComponent( 0 ) instanceof JScrollPane) )
		  return super.getPopup( popup, x, y );

		// do not add scroller if popup fits into screen
		Dimension prefSize = popup.getPreferredSize();
		int screenHeight = getScreenHeightAt( x, y );
		if( prefSize.height <= screenHeight )
			return super.getPopup( popup, x, y );

		// create scroller
		FlatPopupScroller scroller = new FlatPopupScroller( popup );
		scroller.setPreferredSize( new Dimension( prefSize.width, screenHeight ) );

		// create popup
		PopupFactory popupFactory = PopupFactory.getSharedInstance();
		return popupFactory.getPopup( popup.getInvoker(), scroller, x, y );
	}

	private int getScreenHeightAt( int x, int y ) {
		// find GraphicsConfiguration at popup location (similar to JPopupMenu.getCurrentGraphicsConfiguration())
		GraphicsConfiguration gc = null;
		for( GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices() ) {
			if( device.getType() == GraphicsDevice.TYPE_RASTER_SCREEN ) {
				GraphicsConfiguration dgc = device.getDefaultConfiguration();
				if( dgc.getBounds().contains( x, y ) ) {
					gc = dgc;
					break;
				}
			}
		}
		if( gc == null && popupMenu.getInvoker() != null )
			gc = popupMenu.getInvoker().getGraphicsConfiguration();

		// compute screen height
		// (always subtract screen insets because there is no API to detect whether
		// the popup can overlap the taskbar; see JPopupMenu.canPopupOverlapTaskBar())
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Rectangle screenBounds = (gc != null) ? gc.getBounds() : new Rectangle( toolkit.getScreenSize() );
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets( gc );
		return screenBounds.height - screenInsets.top - screenInsets.bottom;
	}

	//---- class FlatPopupMenuLayout ------------------------------------------

	/**
	 * @since 2.4
	 */
	protected static class FlatPopupMenuLayout
		extends DefaultMenuLayout
	{
		public FlatPopupMenuLayout( Container target, int axis ) {
			super( target, axis );
		}

		@Override
		public Dimension preferredLayoutSize( Container target ) {
			FlatMenuItemRenderer.clearClientProperties( target );

			return super.preferredLayoutSize( target );
		}
	}

	//---- class FlatPopupScroller --------------------------------------------

	private class FlatPopupScroller
		extends JPanel
		implements MouseWheelListener, PopupMenuListener, MenuKeyListener
	{
		private final JPopupMenu popup;

		private final JScrollPane scrollPane;
		private final JButton scrollUpButton;
		private final JButton scrollDownButton;
		private int unitIncrement;

		FlatPopupScroller( JPopupMenu popup ) {
			super( new BorderLayout() );
			this.popup = popup;

			// this panel is required to avoid that JPopupMenu.setLocation() will be invoked
			// while scrolling, because this would call JPopupMenu.showPopup()
			JPanel view = new JPanel( new BorderLayout() );
			view.add( popup, BorderLayout.CENTER );

			scrollPane = new JScrollPane( view, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
			scrollPane.setBorder( null );

			scrollUpButton = new ArrowButton( SwingConstants.NORTH );
			scrollDownButton = new ArrowButton( SwingConstants.SOUTH );

			add( scrollPane, BorderLayout.CENTER );
			add( scrollUpButton, BorderLayout.NORTH );
			add( scrollDownButton, BorderLayout.SOUTH );

			setBackground( popup.getBackground() );
			setBorder( popup.getBorder() );
			popup.setBorder( null );

			popup.addPopupMenuListener( this );
			popup.addMouseWheelListener( this );
			popup.addMenuKeyListener( this );

			updateArrowButtons();

			putClientProperty( FlatClientProperties.POPUP_BORDER_CORNER_RADIUS,
				UIManager.getInt( "PopupMenu.borderCornerRadius" ) );
		}

		void scroll( int unitsToScroll ) {
			if( unitIncrement == 0 )
				unitIncrement = new JMenuItem( "X" ).getPreferredSize().height;

			JViewport viewport = scrollPane.getViewport();
			Point viewPosition = viewport.getViewPosition();
			int newY = viewPosition.y + (unitIncrement * unitsToScroll);
			if( newY < 0 )
				newY = 0;
			else
				newY = Math.min( newY, viewport.getViewSize().height - viewport.getExtentSize().height );
			viewport.setViewPosition( new Point( viewPosition.x, newY ) );

			updateArrowButtons();
		}

		void updateArrowButtons() {
			JViewport viewport = scrollPane.getViewport();
			Point viewPosition = viewport.getViewPosition();

			scrollUpButton.setVisible( viewPosition.y > 0 );
			scrollDownButton.setVisible( viewPosition.y < viewport.getViewSize().height - viewport.getExtentSize().height );
		}

		//---- interface PopupMenuListener ----

		@Override
		public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
			// restore popup border
			popup.setBorder( getBorder() );

			popup.removePopupMenuListener( this );
			popup.removeMouseWheelListener( this );
			popup.removeMenuKeyListener( this );
		}

		@Override public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {}
		@Override public void popupMenuCanceled( PopupMenuEvent e ) {}

		//---- interface MouseWheelListener ----

		/**
		 * Scroll when user rotates mouse wheel.
		 */
		@Override
		public void mouseWheelMoved( MouseWheelEvent e ) {
			// convert mouse location before scrolling
			Point mouseLocation = SwingUtilities.convertPoint( (Component) e.getSource(), e.getPoint(), this );

			// scroll
			scroll( e.getUnitsToScroll() );

			// select menu item at mouse location
			Component c = SwingUtilities.getDeepestComponentAt( this, mouseLocation.x, mouseLocation.y );
			if( c instanceof JMenuItem ) {
				ButtonUI ui = ((JMenuItem)c).getUI();
				if( ui instanceof BasicMenuItemUI )
					MenuSelectionManager.defaultManager().setSelectedPath( ((BasicMenuItemUI)ui).getPath() );
			}

			// this avoids that the popup is closed when running on Java 8
			// https://bugs.openjdk.java.net/browse/JDK-8075063
			e.consume();
		}

		//---- interface MenuKeyListener ----

		/**
		 * Scroll when user presses Up or Down keys.
		 */
		@Override
		public void menuKeyPressed( MenuKeyEvent e ) {
			// use invokeLater() because menu selection is not yet updated because
			// this listener is invoked before another listener that updates the menu selection
			EventQueue.invokeLater( () -> {
				if( !isDisplayable() )
					return;

				MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
				if( path.length == 0 )
					return;

				// scroll selected menu item to visible area
				Component c = path[path.length - 1].getComponent();
				JViewport viewport = scrollPane.getViewport();
				Point pt = SwingUtilities.convertPoint( c, 0, 0, viewport );
				viewport.scrollRectToVisible( new Rectangle( pt, c.getSize() ) );

				// update arrow buttons
				boolean upVisible = scrollUpButton.isVisible();
				updateArrowButtons();
				if( !upVisible && scrollUpButton.isVisible() ) {
					// if "up" button becomes visible, make sure that bottom menu item stays visible
					Point viewPosition = viewport.getViewPosition();
					int newY = viewPosition.y + scrollUpButton.getPreferredSize().height;
					viewport.setViewPosition( new Point( viewPosition.x, newY ) );
				}
			} );
		}

		@Override public void menuKeyTyped( MenuKeyEvent e ) {}
		@Override public void menuKeyReleased( MenuKeyEvent e ) {}

		//---- class ArrowButton ----------------------------------------------

		private class ArrowButton
			extends FlatArrowButton
			implements MouseListener, ActionListener
		{
			private Timer timer;

			ArrowButton( int direction ) {
				super( direction, arrowType, scrollArrowColor, null, null, hoverScrollArrowBackground, null, null );

				addMouseListener( this );
			}

			@Override
			public void paint( Graphics g ) {
				// always fill background to paint over border on HiDPI screens
				g.setColor( popup.getBackground() );
				g.fillRect( 0, 0, getWidth(), getHeight() );

				super.paint( g );
			}

			//---- interface MouseListener ----

			@Override public void mouseClicked( MouseEvent e ) {}
			@Override public void mousePressed( MouseEvent e ) {}
			@Override public void mouseReleased( MouseEvent e ) {}

			@Override
			public void mouseEntered( MouseEvent e ) {
				if( timer == null )
					timer = new Timer( 50, this );
				timer.start();
			}

			@Override
			public void mouseExited( MouseEvent e ) {
				if( timer != null )
					timer.stop();
			}

			//---- interface ActionListener ----

			@Override
			public void actionPerformed( ActionEvent e ) {
				if( timer != null && !isDisplayable() ) {
					timer.stop();
					return;
				}

				scroll( direction == SwingConstants.NORTH ? -1 : 1 );
			}
		}
	}
}
