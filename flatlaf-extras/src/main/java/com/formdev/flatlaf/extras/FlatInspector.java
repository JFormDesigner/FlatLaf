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
import java.awt.GraphicsConfiguration;
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
import java.lang.reflect.Method;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.MigLayoutVisualPadding;
import com.formdev.flatlaf.util.SystemInfo;
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
	private static final Integer HIGHLIGHT_LAYER = JLayeredPane.POPUP_LAYER - 1;

	private static final int KEY_MODIFIERS_MASK =
		InputEvent.CTRL_DOWN_MASK |
		InputEvent.SHIFT_DOWN_MASK |
		InputEvent.ALT_DOWN_MASK |
		InputEvent.META_DOWN_MASK;

	private final JRootPane rootPane;
	private final MouseMotionListener mouseMotionListener;
	private final AWTEventListener keyListener;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport( this );
	private final WindowListener windowListener;
	private Window window;

	private boolean enabled;
	private Object oldGlassPaneFullHeight;
	private Component lastComponent;
	private int lastX;
	private int lastY;
	private int inspectParentLevel;
	private boolean wasModifierKeyPressed;
	private boolean showClassHierarchy;
	private long lastWhen;

	private JComponent highlightFigure;
	private Popup popup;

	/**
	 * Installs a key listener into the application that allows enabling and disabling
	 * the UI inspector with the given keystroke (e.g. "ctrl shift alt X").
	 *
	 * @param activationKeys a keystroke (e.g. "ctrl shift alt X"), or {@code null} to use "ctrl shift alt X"
	 */
	public static void install( String activationKeys ) {
		if( activationKeys == null )
			activationKeys = "ctrl shift alt X";

		KeyStroke keyStroke = KeyStroke.getKeyStroke( activationKeys );
		Toolkit.getDefaultToolkit().addAWTEventListener( e -> {
			if( e.getID() == KeyEvent.KEY_RELEASED &&
				((KeyEvent)e).getKeyCode() == keyStroke.getKeyCode() &&
				(((KeyEvent)e).getModifiersEx() & KEY_MODIFIERS_MASK) == (keyStroke.getModifiers() & KEY_MODIFIERS_MASK)  )
			{
				Window activeWindow = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
				RootPaneContainer rootPaneContainer = null;
				if( activeWindow instanceof RootPaneContainer )
					rootPaneContainer = (RootPaneContainer) activeWindow;
				else {
					// search for root pain container in children
					// (e.g. for Swing embedded into SWT)
					for( Component child : activeWindow.getComponents() ) {
						if( child instanceof RootPaneContainer ) {
							rootPaneContainer = (RootPaneContainer) child;
							break;
						}
					}
				}

				if( rootPaneContainer != null ) {
					JRootPane rootPane = rootPaneContainer.getRootPane();
					FlatInspector inspector = (FlatInspector) rootPane.getClientProperty( FlatInspector.class );
					if( inspector == null ) {
						inspector = new FlatInspector( rootPane );
						rootPane.putClientProperty( FlatInspector.class, inspector );
						inspector.setEnabled( true );
					} else {
						inspector.setEnabled( false );
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

		keyListener = e -> {
			KeyEvent keyEvent = (KeyEvent) e;
			int keyCode = keyEvent.getKeyCode();
			int id = e.getID();

			if( id == KeyEvent.KEY_PRESSED ) {
				// this avoids that the inspection level is changed when UI inspector
				// is enabled with keyboard shortcut (e.g. Ctrl+Shift+Alt+X)
				if( keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_ALT )
					wasModifierKeyPressed = true;
			} else if( id == KeyEvent.KEY_RELEASED && wasModifierKeyPressed ) {
				// ignore duplicate events (for Swing embedded into SWT)
				if( (keyEvent.getWhen() - lastWhen) <= 5 )
					return;
				lastWhen = keyEvent.getWhen();

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
				} else if( keyCode == KeyEvent.VK_ALT && lastComponent != null) {
					showClassHierarchy = !showClassHierarchy;
					showToolTip( lastComponent, lastX, lastY, inspectParentLevel );
				}
			}

			if( keyCode == KeyEvent.VK_ESCAPE ) {
				// consume pressed and released ESC key events to e.g. avoid that dialog is closed
				keyEvent.consume();

				if( id == KeyEvent.KEY_PRESSED ) {
					setEnabled( false );
					FlatInspector inspector = (FlatInspector) rootPane.getClientProperty( FlatInspector.class );
					if( inspector == FlatInspector.this )
						rootPane.putClientProperty( FlatInspector.class, null );
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

		// make sure that glass pane has full height if enabled
		if( enabled ) {
			oldGlassPaneFullHeight = rootPane.getClientProperty( FlatClientProperties.GLASS_PANE_FULL_HEIGHT );
			rootPane.putClientProperty( FlatClientProperties.GLASS_PANE_FULL_HEIGHT, true );
			rootPane.validate();
		} else
			rootPane.putClientProperty( FlatClientProperties.GLASS_PANE_FULL_HEIGHT, oldGlassPaneFullHeight );

		// make sure that glass pane is not opaque, which is not the case in WebLaF
		((JComponent)rootPane.getGlassPane()).setOpaque( false );

		rootPane.getGlassPane().setVisible( enabled );

		// add/remove key listener
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if( enabled )
			toolkit.addAWTEventListener( keyListener, AWTEvent.KEY_EVENT_MASK );
		else
			toolkit.removeAWTEventListener( keyListener );

		// add/remove mouse listener
		if( enabled )
			rootPane.getGlassPane().addMouseMotionListener( mouseMotionListener );
		else
			rootPane.getGlassPane().removeMouseMotionListener( mouseMotionListener );

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
		tip.setTipText( buildToolTipText( c, parentLevel, showClassHierarchy ) );
		tip.putClientProperty( FlatClientProperties.POPUP_FORCE_HEAVY_WEIGHT, true );

		Point pt = new Point( x, y );
		SwingUtilities.convertPointToScreen( pt, rootPane.getGlassPane() );
		int tx = pt.x + UIScale.scale( 8 );
		int ty = pt.y + UIScale.scale( 16 );

		Dimension size = tip.getPreferredSize();

		// position the tip in the visible area
		GraphicsConfiguration gc = rootPane.getGraphicsConfiguration();
		if( gc != null ) {
			Rectangle visibleRect = gc.getBounds();
			if( tx + size.width > visibleRect.x + visibleRect.width )
				tx -= size.width + UIScale.scale( 16 );
			if( ty + size.height > visibleRect.y + visibleRect.height )
				ty -= size.height + UIScale.scale( 32 );
			if( tx < visibleRect.x )
				tx = visibleRect.x;
			if( ty < visibleRect.y )
				ty = visibleRect.y;
		}

		PopupFactory popupFactory = PopupFactory.getSharedInstance();
		popup = popupFactory.getPopup( c, tip, tx, ty );
		popup.show();
	}

	private static String buildToolTipText( Component c, int parentLevel, boolean classHierarchy ) {
		StringBuilder buf = new StringBuilder( 1500 );
		buf.append( "<html><style>" );
		buf.append( "td { padding: 0 10 0 0; }" );
		buf.append( "</style><table>" );

		appendRow( buf, "Class", toString( c.getClass(), classHierarchy ) );
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

		if( c instanceof JComponent ) {
			Object value = ((JComponent)c).getClientProperty( MigLayoutVisualPadding.VISUAL_PADDING_PROPERTY );
			Insets visualPadding = (value instanceof int[])
				? new Insets( ((int[])value)[0], ((int[])value)[1], ((int[])value)[2], ((int[])value)[3] )
				: (value instanceof Insets ? (Insets) value : null);
			if( visualPadding != null )
				appendRow( buf, "Mig visual padding", toString( visualPadding ) );
		}

		Dimension prefSize = c.getPreferredSize();
		Dimension minSize = c.getMinimumSize();
		Dimension maxSize = c.getMaximumSize();
		appendRow( buf, "Pref size", prefSize.width + ", " + prefSize.height );
		appendRow( buf, "Min size", minSize.width + ", " + minSize.height );
		appendRow( buf, "Max size", maxSize.width + ", " + maxSize.height );

		if( c instanceof JComponent )
			appendRow( buf, "Border", toString( ((JComponent)c).getBorder(), classHierarchy ) );

		appendRow( buf, "Background", toString( c.getBackground() ) + (c.isBackgroundSet() ? "" : "  NOT SET") );
		appendRow( buf, "Foreground", toString( c.getForeground() ) + (c.isForegroundSet() ? "" : "  NOT SET") );
		appendRow( buf, "Font", toString( c.getFont() ) + (c.isFontSet() ? "" : "  NOT SET") );

		if( c instanceof JComponent ) {
			try {
				Object ui;
				if( SystemInfo.isJava_9_orLater ) {
					// Java 9+: use public method JComponent.getUI()
					Method m = JComponent.class.getMethod( "getUI" );
					ui = m.invoke( c );
				} else {
					// Java 8: read protected field 'ui'
					Field f = JComponent.class.getDeclaredField( "ui" );
					f.setAccessible( true );
					ui = f.get( c );
				}
				appendRow( buf, "UI", (ui != null ? toString( ui.getClass(), classHierarchy ) : "null") );
			} catch( Exception ex ) {
				// ignore
			}
		}

		if( c instanceof Container ) {
			LayoutManager layout = ((Container)c).getLayout();
			if( layout != null )
				appendRow( buf, "Layout", toString( layout.getClass(), classHierarchy ) );
		}

		appendRow( buf, "Enabled", String.valueOf( c.isEnabled() ) );
		appendRow( buf, "Opaque", String.valueOf( c.isOpaque() )
			+ (c instanceof JComponent && FlatUIUtils.hasOpaqueBeenExplicitlySet( (JComponent) c ) ? " EXPLICIT" : "") );
		if( c instanceof AbstractButton )
			appendRow( buf, "ContentAreaFilled", String.valueOf( ((AbstractButton)c).isContentAreaFilled() ) );
		appendRow( buf, "Focusable", String.valueOf( c.isFocusable() ) );
		appendRow( buf, "Left-to-right", String.valueOf( c.getComponentOrientation().isLeftToRight() ) );
		appendRow( buf, "Parent", (c.getParent() != null ? toString( c.getParent().getClass(), classHierarchy ) : "null") );

		if( c instanceof JComponent ) {
			Object style = ((JComponent)c).getClientProperty( FlatClientProperties.STYLE );
			if( style != null )
				appendRow( buf, "FlatLaf Style", style.toString() );
		}

		// append parent level
		buf.append( "<tr><td colspan=\"2\">" );
		if( parentLevel > 0 )
			buf.append( "<br>Parent level: " + parentLevel );

		// append modifier keys hint
		buf.append( "<br>(" )
			.append( (parentLevel > 0)
				? "press <b>Ctrl/Shift</b> to increase/decrease level"
				: "press <b>Ctrl</b> key to inspect parent" )
			.append( "; &nbsp;" )
			.append( classHierarchy
				? "press <b>Alt</b> key to hide class hierarchy"
				: "press <b>Alt</b> key to show class hierarchy" )
			.append( ')' );

		buf.append( "</td></tr>" );
		buf.append( "</table></html>" );

		return buf.toString();
	}

	private static void appendRow( StringBuilder buf, String key, String value ) {
		buf.append( "<tr><td valign=\"top\">" )
			.append( key )
			.append( ":</td><td>" )
			.append( value )
			.append( "</td></tr>" );
	}

	private static String toString( Class<?> cls, boolean classHierarchy ) {
		StringBuilder buf = new StringBuilder( 100 );
		int level = 0;

		while( cls != null ) {
			if( level > 0 ) {
				if( cls == Object.class )
					break;
				buf.append( "<br>&nbsp;" );
				for( int i = 1; i < level; i++ )
					buf.append( "&nbsp;&nbsp;&nbsp;&nbsp;" );
				buf.append( "\u2570 " );
			}
			level++;

			String name = cls.getName();
			int dot = name.lastIndexOf( '.' );
			String pkg = (dot >= 0) ? name.substring( 0, dot ) : "-";
			String simpleName = (dot >= 0) ? name.substring( dot + 1 ) : name;
			buf.append( simpleName ).append( ' ' ).append( toDimmedText( "(" + pkg + ")" ) );

			if( UIResource.class.isAssignableFrom( cls ) )
				buf.append( " UI" );

			if( !classHierarchy )
				break;

			cls = cls.getSuperclass();
		}

		return buf.toString();
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

	private static String toString( Border b, boolean classHierarchy ) {
		if( b == null )
			return "null";

		String s = toString( b.getClass(), classHierarchy );

		if( b instanceof EmptyBorder ) {
			String borderInsets = " (" + toString( ((EmptyBorder)b).getBorderInsets() ) + ')';
			int brIndex = s.indexOf( "<br>" );
			if( brIndex >= 0 )
				s = s.substring( 0, brIndex ) + borderInsets + s.substring( brIndex );
			else
				s += borderInsets;
		}

		return s;
	}

	private static String toDimmedText( String text ) {
		Color color = UIManager.getColor( "Label.disabledForeground" );
		if( color == null )
			color = UIManager.getColor( "Label.disabledText" );
		if( color == null )
			color = Color.GRAY;
		return String.format( "<span color=\"#%06x\">%s</span>",
			color.getRGB() & 0xffffff, text );
	}
}
