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

package com.formdev.flatlaf.extras;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Field;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

/**
 * A simple UI inspector that shows information about UI component at mouse location
 * in a tooltip.
 * <p>
 * To use it in an application install it with:
 * <pre>
 * FlatInspector.install( "ctrl shift alt X" );
 * </pre>
 * This can be done e.g. in the main() method and allows enabling (and disabling)
 * the UI inspector for the active window with the given keystroke.
 * <p>
 * When the UI inspector is active some additional keys are available:
 * <ul>
 *   <li>press {@code Esc} key to disable UI inspector</li>
 *   <li>press {@code Ctrl} key to increase inspection level, which shows
 *       information about parent of UI component at mouse location</li>
 *   <li>press {@code Shift} key to decrease inspection level</li>
 * </ul>
 *
 * @author Karl Tauber
 */
public class FlatInspector
{
	private static final Integer HIGHLIGHT_LAYER = 401;

	private static final int KEY_MODIFIERS_MASK = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK;

	private final JRootPane rootPane;
	private final MouseMotionListener mouseMotionListener;
	private final AWTEventListener keyListener;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( this );
	private final WindowListener windowListener;
	private Window window;

	private boolean enabled;
	private Component lastComponent;
	private int lastX;
	private int lastY;
	private int inspectParentLevel;
	private boolean wasCtrlOrShiftKeyPressed;

	private JComponent highlightFigure;
	private Popup popup;

	/**
	 * Installs a key listener into the application that allows enabling and disabling
	 * the UI inspector with the given keystroke (e.g. "ctrl shift alt X").
	 *
	 * @param activationKeys a keystroke (e.g. "ctrl shift alt X")
	 */
	public static void install( String activationKeys ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( activationKeys );
		Toolkit.getDefaultToolkit().addAWTEventListener( e -> {
			if( e.getID() == KeyEvent.KEY_RELEASED &&
				((KeyEvent)e).getKeyCode() == keyStroke.getKeyCode() &&
				(((KeyEvent)e).getModifiersEx() & KEY_MODIFIERS_MASK) == (keyStroke.getModifiers() & KEY_MODIFIERS_MASK)  )
			{
				Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
				if( activeWindow instanceof RootPaneContainer ) {
					JRootPane rootPane = ((RootPaneContainer)activeWindow).getRootPane();
					FlatInspector inspector = (FlatInspector) rootPane.getClientProperty( FlatInspector.class );
					if( inspector == null ) {
						inspector = new FlatInspector( rootPane );
						rootPane.putClientProperty( FlatInspector.class, inspector );
						inspector.setEnabled( true );
					} else {
						inspector.uninstall();
						rootPane.putClientProperty( FlatInspector.class, null );
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK );
	}

	public FlatInspector( JRootPane rootPane ) {
		this.rootPane = rootPane;

		mouseMotionListener = new MouseMotionAdapter() {
			@Override
			public void mouseMoved( MouseEvent e ) {
				lastX = e.getX();
				lastY = e.getY();
				inspect( lastX, lastY );
			}
		};

		rootPane.getGlassPane().addMouseMotionListener( mouseMotionListener );

		keyListener = e -> {
			KeyEvent keyEvent = (KeyEvent) e;
			int keyCode = keyEvent.getKeyCode();
			int id = e.getID();

			if( id == KeyEvent.KEY_PRESSED ) {
				// this avoids that the inspection level is changed when UI inspector
				// is enabled with keyboard shortcut (e.g. Ctrl+Shift+Alt+X)
				if( keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_SHIFT )
					wasCtrlOrShiftKeyPressed = true;
			} else if( id == KeyEvent.KEY_RELEASED && wasCtrlOrShiftKeyPressed ) {
				if( keyCode == KeyEvent.VK_CONTROL ) {
					inspectParentLevel++;
					int parentLevel = inspect( lastX, lastY );

					// limit level
					if( inspectParentLevel > parentLevel )
						inspectParentLevel = parentLevel;
				} else if( keyCode == KeyEvent.VK_SHIFT && inspectParentLevel > 0 ) {
					inspectParentLevel--;
					int parentLevel = inspect( lastX, lastY );

					// decrease level
					if( inspectParentLevel > parentLevel ) {
						inspectParentLevel = Math.max( parentLevel - 1, 0 );
						inspect( lastX, lastY );
					}
				}
			}

			if( keyCode == KeyEvent.VK_ESCAPE ) {
				// consume pressed and released ESC key events to e.g. avoid that dialog is closed
				keyEvent.consume();

				if( id == KeyEvent.KEY_PRESSED ) {
					FlatInspector inspector = (FlatInspector) rootPane.getClientProperty( FlatInspector.class );
					if( inspector == FlatInspector.this ) {
						uninstall();
						rootPane.putClientProperty( FlatInspector.class, null );
					} else
						setEnabled( false );
				}
			}
		};

		windowListener = new WindowAdapter() {
			@Override
			public void windowActivated( WindowEvent e ) {
				update();
			}

			@Override
			public void windowDeactivated( WindowEvent e ) {
				hidePopup();
			}
		};
	}

	private void uninstall() {
		setEnabled( false );
		rootPane.getGlassPane().setVisible( false );
		rootPane.getGlassPane().removeMouseMotionListener( mouseMotionListener );
	}


	public void addPropertyChangeListener( PropertyChangeListener l ) {
		propertyChangeSupport.addPropertyChangeListener( l );
	}

	public void removePropertyChangeListener( PropertyChangeListener l ) {
		propertyChangeSupport.removePropertyChangeListener( l );
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled( boolean enabled ) {
		if( this.enabled == enabled )
			return;

		this.enabled = enabled;

		// make sure that glass pane is not opaque, which is not the case in WebLaF
		((JComponent)rootPane.getGlassPane()).setOpaque( false );

		rootPane.getGlassPane().setVisible( enabled );

		// add/remove key listener
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if( enabled )
			toolkit.addAWTEventListener( keyListener, AWTEvent.KEY_EVENT_MASK );
		else
			toolkit.removeAWTEventListener( keyListener );

		// add/remove window listener
		if( enabled ) {
			window = SwingUtilities.windowForComponent( rootPane );
			if( window != null )
				window.addWindowListener( windowListener );
		} else {
			if( window != null ) {
				window.removeWindowListener( windowListener );
				window = null;
			}
		}

		// show/hide popup
		if( enabled ) {
			Point pt = new Point( MouseInfo.getPointerInfo().getLocation() );
			SwingUtilities.convertPointFromScreen( pt, rootPane );

			lastX = pt.x;
			lastY = pt.y;
			inspect( lastX, lastY );
		} else {
			lastComponent = null;
			inspectParentLevel = 0;

			if( highlightFigure != null )
				highlightFigure.getParent().remove( highlightFigure );
			highlightFigure = null;

			hidePopup();
		}

		propertyChangeSupport.firePropertyChange( "enabled", !enabled, enabled );
	}

	private void hidePopup() {
		if( popup != null ) {
			popup.hide();
			popup = null;
		}
	}

	public void update() {
		if( !rootPane.getGlassPane().isVisible() )
			return;

		EventQueue.invokeLater( () -> {
			setEnabled( false );
			setEnabled( true );
			inspect( lastX, lastY );
		} );
	}

	private int inspect( int x, int y ) {
		Point pt = SwingUtilities.convertPoint( rootPane.getGlassPane(), x, y, rootPane );
		Component c = getDeepestComponentAt( rootPane, pt.x, pt.y );
		int parentLevel = 0;
		for( int i = 0; i < inspectParentLevel && c != null; i++ ) {
			Container parent = c.getParent();
			if( parent == null )
				break;

			c = parent;
			parentLevel++;
		}

		if( c == lastComponent )
			return parentLevel;

		lastComponent = c;

		highlight( c );
		showToolTip( c, x, y, parentLevel );

		return parentLevel;
	}

	private Component getDeepestComponentAt( Component parent, int x, int y ) {
		if( !parent.contains( x, y ) )
			return null;

		if( parent instanceof Container ) {
			for( Component child : ((Container)parent).getComponents() ) {
				if( child == null || !child.isVisible() )
					continue;

				int cx = x - child.getX();
				int cy = y - child.getY();
				Component c = (child instanceof Container)
					? getDeepestComponentAt( child, cx, cy )
					: child.getComponentAt( cx, cy );
				if( c == null || !c.isVisible() )
					continue;

				// ignore highlight figure and tooltip
				if( c == highlightFigure )
					continue;

				// ignore glass pane
				if( c.getParent() instanceof JRootPane && c == ((JRootPane)c.getParent()).getGlassPane() )
					continue;

				if( "com.formdev.flatlaf.ui.FlatWindowResizer".equals( c.getClass().getName() ) )
					continue;

				return c;
			}
		}

		return parent;
	}

	private void highlight( Component c ) {
		if( highlightFigure == null ) {
			highlightFigure = createHighlightFigure();
			rootPane.getLayeredPane().add( highlightFigure, HIGHLIGHT_LAYER );
		}

		highlightFigure.setVisible( c != null );

		if( c != null ) {
			Insets insets = rootPane.getInsets();
			highlightFigure.setBounds( new Rectangle(
				SwingUtilities.convertPoint( c, -insets.left, -insets.top, rootPane ),
				c.getSize() ) );
		}
	}

	private JComponent createHighlightFigure() {
		JComponent c = new JComponent() {
			@Override
			protected void paintComponent( Graphics g ) {
				g.setColor( getBackground() );
				g.fillRect( 0, 0, getWidth(), getHeight() );
			}

			@Override
			protected void paintBorder( Graphics g ) {
				Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );
				super.paintBorder( g );
				FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
			}
		};
		c.setBackground( new Color( 255, 0, 0, 32 ) );
		c.setBorder( new LineBorder( Color.red ) );
		return c;
	}

	private void showToolTip( Component c, int x, int y, int parentLevel ) {
		hidePopup();

		if( c == null || (window != null && !window.isActive()) )
			return;

		JToolTip tip = new JToolTip();
		tip.setTipText( buildToolTipText( c, parentLevel ) );
		tip.putClientProperty( FlatClientProperties.POPUP_FORCE_HEAVY_WEIGHT, true );

		Point pt = new Point( x, y );
		SwingUtilities.convertPointToScreen( pt, rootPane.getGlassPane() );
		int tx = pt.x + UIScale.scale( 8 );
		int ty = pt.y + UIScale.scale( 16 );

		Dimension size = tip.getPreferredSize();

		// position the tip in the visible area
		Rectangle visibleRect = rootPane.getGraphicsConfiguration().getBounds();
		if( tx + size.width > visibleRect.x + visibleRect.width )
			tx -= size.width + UIScale.scale( 16 );
		if( ty + size.height > visibleRect.y + visibleRect.height )
			ty -= size.height + UIScale.scale( 32 );
		if( tx < visibleRect.x )
			tx = visibleRect.x;
		if( ty < visibleRect.y )
			ty = visibleRect.y;

		PopupFactory popupFactory = PopupFactory.getSharedInstance();
		popup = popupFactory.getPopup( c, tip, tx, ty );
		popup.show();
	}

	private static String buildToolTipText( Component c, int parentLevel ) {
		StringBuilder buf = new StringBuilder( 1500 );
		buf.append( "<html><style>" );
		buf.append( "td { padding: 0 10 0 0; }" );
		buf.append( "</style><table>" );

		String name = c.getClass().getName();
		name = name.substring( name.lastIndexOf( '.' ) + 1 );
		Package pkg = c.getClass().getPackage();
		appendRow( buf, "Class", name + " (" + (pkg != null ? pkg.getName() : "-") + ")" );
		appendRow( buf, "Size", c.getWidth() + ", " + c.getHeight() + "&nbsp;&nbsp; @ " + c.getX() + ", " + c.getY() );

		if( c instanceof Container )
			appendRow( buf, "Insets", toString( ((Container)c).getInsets() ) );

		Insets margin = null;
		if( c instanceof AbstractButton )
			margin = ((AbstractButton) c).getMargin();
		else if( c instanceof JTextComponent )
			margin = ((JTextComponent) c).getMargin();
		else if( c instanceof JMenuBar )
			margin = ((JMenuBar) c).getMargin();
		else if( c instanceof JToolBar )
			margin = ((JToolBar) c).getMargin();

		if( margin != null )
			appendRow( buf, "Margin", toString( margin ) );

		Dimension prefSize = c.getPreferredSize();
		Dimension minSize = c.getMinimumSize();
		Dimension maxSize = c.getMaximumSize();
		appendRow( buf, "Pref size", prefSize.width + ", " + prefSize.height );
		appendRow( buf, "Min size", minSize.width + ", " + minSize.height );
		appendRow( buf, "Max size", maxSize.width + ", " + maxSize.height );

		if( c instanceof JComponent )
			appendRow( buf, "Border", toString( ((JComponent)c).getBorder() ) );

		appendRow( buf, "Background", toString( c.getBackground() ) );
		appendRow( buf, "Foreground", toString( c.getForeground() ) );
		appendRow( buf, "Font", toString( c.getFont() ) );

		if( c instanceof JComponent ) {
			try {
				Field f = JComponent.class.getDeclaredField( "ui" );
				f.setAccessible( true );
				Object ui = f.get( c );
				appendRow( buf, "UI", (ui != null ? ui.getClass().getName() : "null") );
			} catch( Exception ex ) {
				// ignore
			}
		}

		if( c instanceof Container ) {
			LayoutManager layout = ((Container)c).getLayout();
			if( layout != null )
				appendRow( buf, "Layout", layout.getClass().getName() );
		}

		appendRow( buf, "Enabled", String.valueOf( c.isEnabled() ) );
		appendRow( buf, "Opaque", String.valueOf( c.isOpaque() )
			+ (c instanceof JComponent && FlatUIUtils.hasOpaqueBeenExplicitlySet( (JComponent) c ) ? " EXPLICIT" : "") );
		if( c instanceof AbstractButton )
			appendRow( buf, "ContentAreaFilled", String.valueOf( ((AbstractButton)c).isContentAreaFilled() ) );
		appendRow( buf, "Focusable", String.valueOf( c.isFocusable() ) );
		appendRow( buf, "Left-to-right", String.valueOf( c.getComponentOrientation().isLeftToRight() ) );
		appendRow( buf, "Parent", (c.getParent() != null ? c.getParent().getClass().getName() : "null") );

		buf.append( "<tr><td colspan=\"2\">" );
		if( parentLevel > 0 )
			buf.append( "<br>Parent level: " + parentLevel );

		if( parentLevel > 0 )
			buf.append( "<br>(press Ctrl/Shift to increase/decrease level)" );
		else
			buf.append( "<br>(press Ctrl key to inspect parent)" );

		buf.append( "</td></tr>" );
		buf.append( "</table></html>" );

		return buf.toString();
	}

	private static void appendRow( StringBuilder buf, String key, String value ) {
		buf.append( "<tr><td>" )
			.append( key )
			.append( ":</td><td>" )
			.append( value )
			.append( "</td></tr>" );
	}

	private static String toString( Insets insets ) {
		if( insets == null )
			return "null";

		return insets.top + ", " + insets.left + ", " + insets.bottom + ", " + insets.right
			+ (insets instanceof UIResource ? " UI" : "");
	}

	private static String toString( Color c ) {
		if( c == null )
			return "null";

		StringBuilder buf = new StringBuilder( 150 );

		buf.append( "<tt>" ); // <tt> is similar to <code>, but uses same font size as body
		buf.append( (c.getAlpha() != 255)
			? String.format( "#%06x%02x", c.getRGB() & 0xffffff, (c.getRGB() >> 24) & 0xff )
			: String.format( "#%06x", c.getRGB() & 0xffffff ) );
		buf.append( "</tt>" );

		if( c instanceof UIResource )
			buf.append( " UI" );

		// color preview
		buf.append( "&nbsp; &nbsp;" )
			.append( "<span style=\"background: " )
			.append( String.format( "#%06x", c.getRGB() & 0xffffff ) ) // Java CSS does not support alpha; see CSS.hexToColor()
			.append( ";\">" )
			.append( "&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;" )
			.append( "</span>" );

		if( c.getAlpha() != 255 )
			buf.append( " " ).append( Math.round( c.getAlpha() / 2.55f ) ).append( '%' );

		return buf.toString();
	}

	private static String toString( Font f ) {
		if( f == null )
			return "null";

		return f.getFamily() + " " + f.getSize() + " " + f.getStyle()
			+ (f instanceof UIResource ? " UI" : "");
	}

	private static String toString( Border b ) {
		if( b == null )
			return "null";

		String s = b.getClass().getName();

		if( b instanceof EmptyBorder )
			s += '(' + toString( ((EmptyBorder)b).getBorderInsets() ) + ')';

		if( b instanceof UIResource )
			s += " UI";

		return s;
	}
}
