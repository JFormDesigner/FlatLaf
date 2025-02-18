/*
 * Copyright 2025 FormDev Software GmbH
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

package com.formdev.flatlaf.ui;

import static javax.swing.SwingConstants.*;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import com.formdev.flatlaf.util.UIScale;

/**
 * Rounded border for {@link Window}.
 * Used for popups and for FlatLaf window decorations.
 * <p>
 * Border is painted only if window is not maximized (in both directions) and
 * not in full screen mode. If maximized in one direction (vertically or horizontally),
 * then a square border is painted.
 * <p>
 * Note: The rootpane of the window should have a {@link FlatEmptyBorder} with
 *       same insets as border width used in this class.
 *
 * @author Karl Tauber
 * @since 3.6
 */
public class FlatWindowRoundedBorder
	implements PropertyChangeListener, ComponentListener
{
	protected final JRootPane rootPane;
	protected final int borderCornerRadius;
	protected final float borderWidth;
	protected final Color borderColor;

	protected final Shape cornerShape;

	// edges
	protected final RoundedBorderComponent northComp;
	protected final RoundedBorderComponent southComp;
	protected final RoundedBorderComponent westComp;
	protected final RoundedBorderComponent eastComp;

	// corners
	protected final RoundedBorderComponent northWestComp;
	protected final RoundedBorderComponent northEastComp;
	protected final RoundedBorderComponent southWestComp;
	protected final RoundedBorderComponent southEastComp;

	protected Window window;
	protected boolean windowIsRounded;

	public FlatWindowRoundedBorder( JRootPane rootPane, int borderCornerRadius,
		float borderWidth, Color borderColor )
	{
		this.rootPane = rootPane;
		this.borderCornerRadius = borderCornerRadius;
		this.borderWidth = borderWidth;
		this.borderColor = borderColor;

		// create shape used to paint rounded corners
		cornerShape = createCornerShape();

		// create edges
		northComp = new RoundedBorderComponent( NORTH );
		southComp = new RoundedBorderComponent( SOUTH );
		westComp  = new RoundedBorderComponent( WEST );
		eastComp  = new RoundedBorderComponent( EAST );

		// create corners
		northWestComp = new RoundedBorderComponent( NORTH_WEST );
		northEastComp = new RoundedBorderComponent( NORTH_EAST );
		southWestComp = new RoundedBorderComponent( SOUTH_WEST );
		southEastComp = new RoundedBorderComponent( SOUTH_EAST );

		// insert before layered pane
		int insertIndex = rootPane.getComponentCount() - 1;
		JLayeredPane layeredPane = rootPane.getLayeredPane();
		for( int i = insertIndex; i >= 0; i-- ) {
			if( rootPane.getComponent( insertIndex ) == layeredPane )
				break;
		}

		// add edges
		rootPane.add( northComp, insertIndex++ );
		rootPane.add( southComp, insertIndex++ );
		rootPane.add( westComp,  insertIndex++ );
		rootPane.add( eastComp,  insertIndex++ );

		// add corners
		rootPane.add( northWestComp, insertIndex++ );
		rootPane.add( northEastComp, insertIndex++ );
		rootPane.add( southWestComp, insertIndex++ );
		rootPane.add( southEastComp, insertIndex++ );

		// add listeners
		rootPane.addComponentListener( this );
		rootPane.addPropertyChangeListener( "ancestor", this );

		if( rootPane.isDisplayable() )
			addNotify();
		else
			updateVisibility();
	}

	public void uninstall() {
		removeNotify();

		// remove listeners
		rootPane.removeComponentListener( this );
		rootPane.removePropertyChangeListener( "ancestor", this );

		// remove edges
		rootPane.remove( northComp );
		rootPane.remove( southComp );
		rootPane.remove( westComp );
		rootPane.remove( eastComp );

		// remove corners
		rootPane.remove( northWestComp );
		rootPane.remove( northEastComp );
		rootPane.remove( southWestComp );
		rootPane.remove( southEastComp );
	}

	public void doLayout() {
		if( !northComp.isVisible() )
			return;

		int x = 0;
		int y = 0;
		int width = rootPane.getWidth();
		int height = rootPane.getHeight();
		if( width <= 0 || height <= 0 )
			return;

		// for layout, round-up scaled border width and radius to ensure that components are large enough
		int lineWidth = (int) Math.ceil( UIScale.scale( borderWidth ) );
		int cornerSize = (windowIsRounded && lineWidth > 0)
			? (int) Math.ceil( UIScale.scale( (float) borderCornerRadius ) )
			: 0;
		int cornerSize2x = cornerSize * 2;

		// edges
		northComp.setBounds( x + cornerSize, y, width - cornerSize2x, lineWidth );
		southComp.setBounds( x + cornerSize, y + height - lineWidth, width - cornerSize2x, lineWidth );
		westComp.setBounds( x, y + cornerSize, lineWidth, height - cornerSize2x );
		eastComp.setBounds( x + width - lineWidth, y + cornerSize, lineWidth, height - cornerSize2x );

		// corners
		northWestComp.setBounds( x, y, cornerSize, cornerSize );
		northEastComp.setBounds( x + width - cornerSize, y, cornerSize, cornerSize );
		southWestComp.setBounds( x, y + height - cornerSize, cornerSize, cornerSize );
		southEastComp.setBounds( x + width - cornerSize, y + height - cornerSize, cornerSize, cornerSize );
	}

	protected void addNotify() {
		Container parent = rootPane.getParent();
		window = (parent instanceof Window) ? (Window) parent : null;

		updateVisibility();
		updateWindowShape();
		doLayout();
	}

	protected void removeNotify() {
		if( window != null ) {
			window.setShape( null );
			window = null;
		}

		updateVisibility();
	}

	protected void updateVisibility() {
		boolean visible = needsBorder();
		if( visible == northComp.isVisible() )
			return;

		// edges
		northComp.setVisible( visible );
		southComp.setVisible( visible );
		westComp.setVisible( visible );
		eastComp.setVisible( visible );

		// corners
		northWestComp.setVisible( visible );
		northEastComp.setVisible( visible );
		southWestComp.setVisible( visible );
		southEastComp.setVisible( visible );
	}

	protected boolean needsBorder() {
		if( window == null || FlatUIUtils.isFullScreen( window ) )
			return false;
		if( window instanceof Frame )
			return (((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH;
		return true;
	}

	protected void updateWindowShape() {
		windowIsRounded = false;

		if( window == null )
			return;

		if( !northComp.isVisible() ||
			(window instanceof Frame && (((Frame)window).getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) )
		{
			window.setShape( null );
			return;
		}

		int arc = UIScale.scale( borderCornerRadius * 2 );

		// use a slightly smaller arc for the shape so that at least parts of
		// the antialiased arc outside are shown
		arc -= 2;

		if( arc > 0 ) {
			try {
				window.setShape( new RoundRectangle2D.Float( 0, 0,
					rootPane.getWidth(), rootPane.getHeight(), arc, arc ) );
				windowIsRounded = true;
			} catch( IllegalComponentStateException | UnsupportedOperationException ex ) {
				window.setShape( null );
			}
		} else
			window.setShape( null );
	}

	protected Shape createCornerShape() {
		float lineWidth = UIScale.scale( borderWidth );
		int arc = UIScale.scale( borderCornerRadius * 2 );
		int wh = arc * 3;
		float innerArc = arc - (lineWidth * 2);
		float innerWH = wh - (lineWidth * 2);

		Path2D path = new Path2D.Float( Path2D.WIND_EVEN_ODD );
		path.append( new RoundRectangle2D.Float( 0, 0, wh, wh, arc, arc ), false );
		path.append( new RoundRectangle2D.Float( lineWidth, lineWidth, innerWH, innerWH, innerArc, innerArc ), false );

		Area area = new Area( path );
		int cornerSize = (int) Math.ceil( UIScale.scale( (float) borderCornerRadius ) );
		area.intersect( new Area( new Rectangle2D.Float( 0, 0, cornerSize, cornerSize ) ) );
		return area;
	}

	//---- interface PropertyChangeListener ----

	@Override
	public void propertyChange( PropertyChangeEvent e ) {
		switch( e.getPropertyName() ) {
			case "ancestor":
				if( e.getNewValue() != null )
					addNotify();
				else
					removeNotify();
				break;
		}
	}

	//---- interface ComponentListener ----

	@Override
	public void componentResized( ComponentEvent e ) {
		updateVisibility();
		updateWindowShape();
		doLayout();
	}

	@Override public void componentMoved( ComponentEvent e ) {}
	@Override public void componentShown( ComponentEvent e ) {}
	@Override public void componentHidden( ComponentEvent e ) {}

	//---- class RoundedBorderComponent ---------------------------------------

	protected class RoundedBorderComponent
		extends JComponent
	{
		private final int position;

		protected RoundedBorderComponent( int position ) {
			this.position = position;
		}

		@Override
		public void paint( Graphics g ) {
			Graphics2D g2 = (Graphics2D) g;
			int width = getWidth();
			int height = getHeight();
			float lineWidth = UIScale.scale( borderWidth );

/*debug
			g.setColor( java.awt.Color.green );
			g.drawRect( 0, 0, width - 1, height - 1 );
debug*/

			Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g );

			g.setColor( borderColor );
			switch( position ) {
				case NORTH: g2.fill( new Rectangle2D.Float( 0, 0, width, lineWidth ) ); break;
				case SOUTH: g2.fill( new Rectangle2D.Float( 0, height - lineWidth, width, lineWidth ) ); break;
				case WEST:  g2.fill( new Rectangle2D.Float( 0, 0, lineWidth, height ) ); break;
				case EAST:  g2.fill( new Rectangle2D.Float( width - lineWidth, 0, lineWidth, height ) ); break;

				case NORTH_WEST:
					g2.fill( cornerShape );
					break;

				case NORTH_EAST:
					g2.translate( width, 0 );
					g2.rotate( Math.toRadians( 90 ) );
					g2.fill( cornerShape );
					break;

				case SOUTH_WEST:
					g2.translate( 0, height );
					g2.rotate( Math.toRadians( -90 ) );
					g2.fill( cornerShape );
					break;

				case SOUTH_EAST:
					g2.translate( width, height );
					g2.rotate( Math.toRadians( 180 ) );
					g2.fill( cornerShape );
					break;
			}

			FlatUIUtils.resetRenderingHints( g, oldRenderingHints );
		}
	}
}
