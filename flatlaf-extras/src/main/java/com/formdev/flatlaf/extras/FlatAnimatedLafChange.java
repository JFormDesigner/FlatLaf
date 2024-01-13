/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.extras;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.VolatileImage;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.RootPaneContainer;
import com.formdev.flatlaf.FlatSystemProperties;
import com.formdev.flatlaf.util.Animator;

/**
 * Animated look and feel changing.
 * <p>
 * Invoke {@link #showSnapshot()} before setting look and feel and
 * {@link #hideSnapshotWithAnimation()} after updating UI. E.g.
 * <pre>
 * FlatAnimatedLafChange.showSnapshot();
 * UIManager.setLookAndFeel( lafClassName );
 * FlatLaf.updateUI();
 * FlatAnimatedLafChange.hideSnapshotWithAnimation();
 * </pre>
 *
 * @author Karl Tauber
 */
public class FlatAnimatedLafChange
{
	/**
	 * The duration of the animation in milliseconds. Default is 160 ms.
	 */
	public static int duration = 160;

	/**
	 * The resolution of the animation in milliseconds. Default is 30 ms.
	 */
	public static int resolution = 30;

	private static Animator animator;
	private static final Map<JLayeredPane, JComponent> oldUIsnapshots = new WeakHashMap<>();
	private static final Map<JLayeredPane, JComponent> newUIsnapshots = new WeakHashMap<>();
	private static float alpha;
	private static boolean inShowSnapshot;

	/**
	 * Create a snapshot of the old UI and shows it on top of the UI.
	 * Invoke before setting new look and feel.
	 */
	public static void showSnapshot() {
		if( !FlatSystemProperties.getBoolean( "flatlaf.animatedLafChange", true ) )
			return;

		// stop already running animation
		if( animator != null )
			animator.stop();

		alpha = 1;

		// show snapshot of old UI
		showSnapshot( true, oldUIsnapshots );
	}

	private static void showSnapshot( boolean useAlpha, Map<JLayeredPane, JComponent> map ) {
		inShowSnapshot = true;

		// create snapshots for all shown windows
		Window[] windows = Window.getWindows();
		for( Window window : windows ) {
			if( !(window instanceof RootPaneContainer) || !window.isShowing() )
				continue;

			// create snapshot image
			// (using volatile image to have correct sub-pixel text rendering on Java 9+)
			VolatileImage snapshot = window.createVolatileImage( window.getWidth(), window.getHeight() );
			if( snapshot == null )
				continue;

			// paint window to snapshot image
			JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();
			layeredPane.paint( snapshot.getGraphics() );

			// create snapshot layer, which is added to layered pane and paints
			// snapshot with animated alpha
			JComponent snapshotLayer = new JComponent() {
				@Override
				public void paint( Graphics g ) {
					if( inShowSnapshot || snapshot.contentsLost() )
						return;

					if( useAlpha )
						((Graphics2D)g).setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
					g.drawImage( snapshot, 0, 0, null );
				}

				@Override
				public void removeNotify() {
					super.removeNotify();

					// release system resources used by volatile image
					snapshot.flush();
				}
			};
			if( !useAlpha )
				snapshotLayer.setOpaque( true );
			snapshotLayer.setSize( layeredPane.getSize() );

			// add image layer to layered pane
			layeredPane.add( snapshotLayer, Integer.valueOf( JLayeredPane.DRAG_LAYER + (useAlpha ? 2 : 1) ) );
			map.put( layeredPane, snapshotLayer );
		}

		inShowSnapshot = false;
	}

	/**
	 * Starts an animation that shows the snapshot (created by {@link #showSnapshot()})
	 * with a decreasing alpha. At the end, the snapshot is removed and the new UI is shown.
	 * Invoke after updating UI.
	 */
	public static void hideSnapshotWithAnimation() {
		if( !FlatSystemProperties.getBoolean( "flatlaf.animatedLafChange", true ) )
			return;

		if( oldUIsnapshots.isEmpty() )
			return;

		// show snapshot of new UI
		showSnapshot( false, newUIsnapshots );

		// create animator
		animator = new Animator( duration, fraction -> {
			if( fraction < 0.1 || fraction > 0.9 )
				return; // ignore initial and last events

			alpha = 1f - fraction;

			// repaint snapshots
			for( Map.Entry<JLayeredPane, JComponent> e : oldUIsnapshots.entrySet() ) {
				if( e.getKey().isShowing() )
					e.getValue().repaint();
			}

			Toolkit.getDefaultToolkit().sync();
		}, () -> {
			hideSnapshot();
			animator = null;
		} );

		animator.setResolution( resolution );
		animator.start();
	}

	private static void hideSnapshot() {
		hideSnapshot( oldUIsnapshots );
		hideSnapshot( newUIsnapshots );
	}

	private static void hideSnapshot( Map<JLayeredPane, JComponent> map ) {
		// remove snapshots
		for( Map.Entry<JLayeredPane, JComponent> e : map.entrySet() ) {
			e.getKey().remove( e.getValue() );
			e.getKey().repaint();
		}

		map.clear();
	}

	/**
	 * Stops a running animation (if any) and hides the snapshot.
	 */
	public static void stop() {
		if( animator != null )
			animator.stop();
		else
			hideSnapshot();
	}
}
