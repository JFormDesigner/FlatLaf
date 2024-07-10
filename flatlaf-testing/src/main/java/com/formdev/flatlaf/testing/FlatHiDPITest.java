/*
 * Copyright 2024 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.testing;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.function.Supplier;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.Graphics2DProxy;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * @author Karl Tauber
 */
public class FlatHiDPITest
{
	private static final double scale = 1.25;

	private final JFrame frame;
	private final JPanel testPanel;

	private final Insets frameInsets;

	public static void main( String[] args ) {
		System.setProperty( FlatSystemProperties.USE_WINDOW_DECORATIONS, "false" );
		System.setProperty( "sun.java2d.uiScale", Double.toString( scale ) );

		System.out.println( "Scale factor: " + scale );
		for( int x = 0; x <= 100; x++ ) {
			int devX = devScaleXY( x, scale );
			int usrX = usrScaleXY( x, scale );
			if( usrX != devX )
				System.out.printf( "%d:  %d != %d\n", x, devX, usrX );

/*
			for( int w = 0; w <= 10; w++ ) {
				int devW = devScaleWH( w, scale );
				int usrW = usrScaleWH( x, w, scale );
				if( usrW != devW )
					System.out.printf( "    %d %d:  %d != %d\n", x, w, devW, usrW );
			}
*/
		}

		SwingUtilities.invokeLater( () -> {
			if( !SystemInfo.isJava_9_orLater ) {
				JOptionPane.showMessageDialog( null, "Use Java 9+" );
				return;
			}

//			HiDPIUtils.installHiDPIRepaintManager();

			FlatLaf.setGlobalExtraDefaults( Collections.singletonMap( "@accentColor", "#f00" ) );
			FlatLightLaf.setup();

			UIManager.put( "Button.pressedBorderColor", Color.blue );
			UIManager.put( "TextField.caretBlinkRate", 0 );
			UIManager.put( "FormattedTextField.caretBlinkRate", 0 );

			new FlatHiDPITest();
		} );
	}

	FlatHiDPITest() {
		frame = new JFrame( "FlatHiDPITest " + scale ) {
			@Override
			public Graphics getGraphics() {
				return TestGraphics2D.install( super.getGraphics(), "JFrame" );
			}
		};
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		// get frame insets
		frame.addNotify();
		frameInsets = frame.getInsets();

		testPanel = new JPanel( null ) {
			@Override
			public Graphics getGraphics() {
				return TestGraphics2D.install( super.getGraphics(), "JPanel" );
			}
		};

		int y = 0;
		addAtProblematicXY( 0, y, 40, 16, 48, "TestComp", TestComp::new );
		y += 20;
		addAtProblematicXY( 0, y, 40, 16, 48, "JButton", () -> new JButton( "B" ) );
		y += 20;
		addAtProblematicXY( 0, y, 40, 16, 48, "JTextField", () -> new JTextField( "Text" ) );
		y += 20;
		addAtProblematicXY( 0, y, 40, 16, 48, "JComboBox", JComboBox<String>::new );
		y += 20;
		addAtProblematicXY( 0, y, 40, 16, 48, "JComboBox editable", () -> {
			JComboBox<String> c = new JComboBox<>();
			c.setEditable( true );
			return c;
		} );
		y += 20;
		addAtProblematicXY( 0, y, 40, 16, 48, "JSpinner", JSpinner::new );
		y += 20;
		addAtProblematicXY( 0, y, 80, 16, 88, "JSlider", JSlider::new );
		y += 20;
		addAtProblematicXY( 0, y, 80, 16, 88, "JScrollBar", () -> new JScrollBar( JScrollBar.HORIZONTAL ) );
		y += 20;
		addAtProblematicXY( 0, y, 16, 40, 20, "JScrollBar", () -> new JScrollBar( JScrollBar.VERTICAL ) );
		y += 60;
		addAtProblematicXY( 0, y, 82, 60, 88, "JScrollPane", () -> new JScrollPane( new JTree() ) );
		y += 80;
		addAtProblematicXY( 0, y, 80, 16, 88, "JProgressBar", () -> {
			JProgressBar c = new JProgressBar();
			c.setValue( 60 );
			c.addMouseListener( new MouseAdapter() {
				@Override
				public void mousePressed( MouseEvent e ) {
					int value = c.getValue();
					c.setValue( (value >= 20) ? value - 20 : 100 );
				}
			} );
			return c;
		} );

		frame.getContentPane().add( testPanel );
		frame.setSize( 400, 400 );
		frame.setVisible( true );
	}

	private void addAtProblematicXY( int x, int y, int w, int h, int offset, String text, Supplier<Component> generator ) {
		// plain component
		addAtProblematicXY( x, y, w, h, generator.get() );

		// component in (opaque) panel which has same bounds as component
		addAtProblematicXY( x + offset, y, w, h, wrapInPanel( generator.get(), false ) );

		// component in (opaque) panel which is 1px larger than component
		addAtProblematicXY( x + (offset * 2), y, w + 1, h + 1, wrapInPanel( generator.get(), true ) );

		JLabel l = new JLabel( text );
		testPanel.add( l );
		l.setLocation( x + (offset * 3) + 20, y );
		l.setSize( l.getPreferredSize() );
	}

	private void addAtProblematicXY( int x, int y, int w, int h, Component c ) {
		int px = nextProblematicXY( x + frameInsets.left ) - frameInsets.left;
		int py = nextProblematicXY( y + frameInsets.top ) - frameInsets.top;
		testPanel.add( c );
		c.setBounds( px, py, w, h );
	}

	private Component wrapInPanel( Component c, boolean emptyBorder ) {
		JPanel p = new JPanel( new BorderLayout() ) {
			@Override
			public Graphics getGraphics() {
				return TestGraphics2D.install( super.getGraphics(), "wrapping JPanel" );
			}
		};
		if( emptyBorder )
			p.setBorder( new EmptyBorder( 0, 0, 1, 1 ) );
		p.add( c, BorderLayout.CENTER );
		return p;
	}

	private static int nextProblematicXY( int xy ) {
		for( int i = xy; i < xy + 20; i++ ) {
			if( devScaleXY( i, scale ) != usrScaleXY( i, scale ) )
				return i;
		}
		throw new IllegalArgumentException();
	}

	private static int devScaleXY( int xy, double scale ) {
		return (int) (xy * scale);
	}

	private static int usrScaleXY( int xy, double scale ) {
		// see sun.java2d.pipe.Region.clipRound(double);
		return (int) Math.ceil( (xy * scale) - 0.5 );
	}

	@SuppressWarnings( "unused" )
	private static int devScaleWH( int wh, double scale ) {
		return (int) Math.round( wh * scale );
	}

	@SuppressWarnings( "unused" )
	private static int usrScaleWH( int xy, int wh, double scale ) {
		int usrXY = usrScaleXY( xy, scale );
		return ((int) Math.ceil( ((xy + wh) * scale) - 0.5 )) - usrXY;
	}

	//---- class TestComp -----------------------------------------------------

	private static class TestComp
		extends JComponent
		implements FocusListener
	{
		// used to avoid repainting when window is deactivated and activated (for easier debugging)
		private boolean permanentFocused;

		TestComp() {
			setOpaque( true );
			setFocusable( true );

			addFocusListener( this );
			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked( MouseEvent e ) {
					requestFocusInWindow();
				}
			} );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			g.setColor( isFocusOwner() ? Color.green : Color.red );
			g.fillRect( 0, 0, getWidth(), getHeight() );
		}

		@Override
		public void focusGained( FocusEvent e ) {
			if( permanentFocused )
				return;

			if( !e.isTemporary() ) {
				repaint();
				permanentFocused = true;
			}
		}

		@Override
		public void focusLost( FocusEvent e ) {
			if( !e.isTemporary() ) {
				repaint();
				permanentFocused = false;
			}
		}

		@Override
		public Graphics getGraphics() {
			return TestGraphics2D.install( super.getGraphics(), "TestComp" );
		}
	}

	//---- TestGraphics2D -----------------------------------------------------

	private static class TestGraphics2D
		extends Graphics2DProxy
	{
		private final Graphics2D delegate;
		private final String id;

		static Graphics install( Graphics g, String id ) {
			return wasInvokedFrom_safelyGetGraphics()
				? new TestGraphics2D( (Graphics2D) g, id )
				: g;
		}

		private static boolean wasInvokedFrom_safelyGetGraphics() {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			StackTraceElement stackTraceElement = stackTrace[4];
			return "javax.swing.JComponent".equals( stackTraceElement.getClassName() ) &&
					"safelyGetGraphics".equals( stackTraceElement.getMethodName() );
		}

		private TestGraphics2D( Graphics2D delegate, String id ) {
			super( delegate );
			this.delegate = delegate;
			this.id = id;

			System.out.println();
			System.out.println( "----------------------------------------	" );
			System.out.println( id + ": construct" );
			printClipRects();
		}

		private void printClipRects() {
			try {
				Class<?> sunGraphics2DClass = Class.forName( "sun.java2d.SunGraphics2D" );
				if( !sunGraphics2DClass.isInstance( delegate ) ) {
					System.out.println( "    not a SunGraphics2D: " + delegate.getClass().getName() );
					return;
				}

				Rectangle devClip = region2rect( getFieldValue( sunGraphics2DClass, delegate, "devClip" ) );
				Shape usrClip = (Shape) getFieldValue( sunGraphics2DClass, delegate, "usrClip" );
				Rectangle clipRegion = region2rect( getFieldValue( sunGraphics2DClass, delegate, "clipRegion" ) );

				printField( devClip, "devClip" );
				printField( usrClip, "usrClip" );
				printField( clipRegion, "clipRegion" );

				if( (usrClip instanceof Rectangle && !devClip.contains( (Rectangle) usrClip )) ||
					(usrClip instanceof Rectangle2D && !devClip.contains( (Rectangle2D) usrClip )) )
				{
					System.out.flush();
					System.err.println( "WARNING: devClip smaller than usrClip" );
					System.err.flush();
				}
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}

		private void printField( Object value, String name ) throws Exception {
			System.out.printf( "    %-16s", name );

			if( value instanceof Rectangle ) {
				Rectangle r = (Rectangle) value;
				System.out.printf( "xy %3d %3d -> %3d %3d   wh %3d %3d\n",
					r.x, r.y, r.x + r.width, r.y + r.height, r.width, r.height );
			} else if( value instanceof Rectangle2D ) {
				Rectangle2D r = (Rectangle2D) value;
				System.out.printf( "xy %.2f %.2f -> %.2f %.2f   wh %.2f %.2f\n",
					r.getX(), r.getY(), r.getX() + r.getWidth(), r.getY() + r.getHeight(), r.getWidth(), r.getHeight() );
			} else
				System.out.println( value );
		}

		private static Rectangle region2rect( Object region ) throws Exception {
			Class<?> regionClass = Class.forName( "sun.java2d.pipe.Region" );
			int loX = (int) getMethodValue( regionClass, region, "getLoX" );
			int loY = (int) getMethodValue( regionClass, region, "getLoY" );
			int hiX = (int) getMethodValue( regionClass, region, "getHiX" );
			int hiY = (int) getMethodValue( regionClass, region, "getHiY" );
			return new Rectangle( loX, loY, hiX - loX, hiY - loY );
		}

		private static Object getFieldValue( Class<?> cls, Object object, String name ) throws Exception {
			Field f = cls.getDeclaredField( name );
			f.setAccessible( true );
			return f.get( object );
		}

		private static Object getMethodValue( Class<?> cls, Object object, String name ) throws Exception {
			Method m = cls.getDeclaredMethod( name );
			m.setAccessible( true );
			return m.invoke( object );
		}

		@Override
		public void clipRect( int x, int y, int width, int height ) {
			System.out.printf( "\n%s: clipRect( %d, %d, %d, %d )\n", id, x, y, width, height );
			super.clipRect( x, y, width, height );
			printClipRects();
		}

		@Override
		public void setClip( int x, int y, int width, int height ) {
			System.out.printf( "\n%s: setClip( %d, %d, %d, %d )\n", id, x, y, width, height );
			super.setClip( x, y, width, height );
			printClipRects();
		}

		@Override
		public void setClip( Shape clip ) {
			System.out.printf( "\n%s: setClip( %s )\n", id, clip );
			super.setClip( clip );
			printClipRects();
		}

		@Override
		public void clip( Shape s ) {
			System.out.printf( "\n%s: clip( %s )\n", id, s );
			super.clip( s );
			printClipRects();
		}
	}
}
