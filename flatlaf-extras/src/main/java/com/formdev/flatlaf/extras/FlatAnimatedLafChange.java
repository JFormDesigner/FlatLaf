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
import java.awt.Image;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
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
	 * The resolution of the animation in milliseconds. Default is 40 ms.
	 */
	public static int resolution = 40;

	private static Animator animator;
	private static final Map<JLayeredPane, JComponent> map = new HashMap<>();
	private static float alpha;

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

		// create snapshots for all shown windows
		Window[] windows = Window.getWindows();
		for( Window window : windows ) {
			if( !(window instanceof RootPaneContainer) || !window.isShowing() )
				continue;

			JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();

			// create snapshot image of layered pane
			Image snapshot = window.createImage( window.getWidth(), window.getHeight() );
			layeredPane.paint( snapshot.getGraphics() );

			// create snapshot layer, which is added to layered pane and paints
			// snapshot with animated alpha
			JComponent snapshotLayer = new JComponent() {
				@Override
				public void paint( Graphics g ) {
					((Graphics2D)g).setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
					g.drawImage( snapshot, 0, 0, null );
				}
			};
			snapshotLayer.setSize( layeredPane.getSize() );

			// add image layer to layered pane
			layeredPane.add( snapshotLayer, JLayeredPane.DRAG_LAYER );
			map.put( layeredPane, snapshotLayer );
		}
	}

	/**
	 * Starts an animation that shows the snapshot (created by {@link #showSnapshot()}
	 * with an decreasing alpha. At the end, the snapshot is removed and the new UI is shown.
	 * Invoke after updating UI.
	 */
	public static void hideSnapshotWithAnimation() {
		if( !FlatSystemProperties.getBoolean( "flatlaf.animatedLafChange", true ) )
			return;

		if( map.isEmpty() )
			return;

		// create animator
		animator = new Animator( duration, fraction -> {
			if( fraction < 0.1 || fraction > 0.9 )
				return; // ignore initial and last events

			alpha = 1f - fraction;

			// repaint snapshots
			for( Map.Entry<JLayeredPane, JComponent> e : map.entrySet() ) {
				if( e.getKey().isShowing() )
					e.getValue().repaint();
			}
		}, () -> {
			hideSnapshot();
			animator = null;
		} );

		animator.setResolution( resolution );
		animator.start();
	}

	private static void hideSnapshot() {
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
