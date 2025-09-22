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
import javax.swing.JRootPane;
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
	 * The resolution of the animation in milliseconds. Default is 16 ms.
	 */
	public static int resolution = 16;

	private static Animator animator;
	private static final Map<JLayeredPane, SnapshotLayer> snapshots = new WeakHashMap<>();
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

		// show snapshot of old UI
		showSnapshot( true );
	}

	private static void showSnapshot( boolean old ) {
		// create snapshots for all shown windows
		Window[] windows = Window.getWindows();
		for( Window window : windows ) {
			if( !(window instanceof RootPaneContainer) || !window.isShowing() )
				continue;

			JLayeredPane layeredPane = ((RootPaneContainer)window).getLayeredPane();

			// create snapshot image
			// (using volatile image to have correct sub-pixel text rendering on Java 9+)
			VolatileImage snapshotImage = layeredPane.createVolatileImage( layeredPane.getWidth(), layeredPane.getHeight() );
			if( snapshotImage == null )
				continue;

			// paint window to snapshot image
			layeredPane.paint( snapshotImage.getGraphics() );

			if( old ) {
				// create snapshot layer, which is added to layered pane and paints
				// snapshot with animated alpha
				SnapshotLayer snapshotLayer = new SnapshotLayer();
				snapshotLayer.setOpaque( true );
				snapshotLayer.setSize( layeredPane.getSize() );
				snapshotLayer.oldSnapshotImage = snapshotImage;

				snapshots.put( layeredPane, snapshotLayer );
			} else {
				SnapshotLayer snapshotLayer = snapshots.get( layeredPane );
				if( snapshotLayer == null ) {
					snapshotImage.flush();
					continue;
				}

				snapshotLayer.newSnapshotImage = snapshotImage;

				// add snapshot layer to layered pane
				layeredPane.add( snapshotLayer, Integer.valueOf( JLayeredPane.DRAG_LAYER + 1 ) );

				// let FlatRootPaneUI know that animated Laf change is in progress
				layeredPane.getRootPane().putClientProperty( "FlatLaf.internal.animatedLafChange", true );
			}
		}
	}

	/**
	 * Starts an animation that shows the snapshot (created by {@link #showSnapshot()})
	 * with a decreasing alpha. At the end, the snapshot is removed and the new UI is shown.
	 * Invoke after updating UI.
	 */
	public static void hideSnapshotWithAnimation() {
		if( !FlatSystemProperties.getBoolean( "flatlaf.animatedLafChange", true ) )
			return;

		if( snapshots.isEmpty() )
			return;

		// show snapshot of new UI
		showSnapshot( false );

		// create animator
		animator = new Animator( duration, fraction -> {
			alpha = 1f - fraction;

			// repaint snapshots
			for( Map.Entry<JLayeredPane, SnapshotLayer> e : snapshots.entrySet() ) {
				if( e.getKey().isShowing() ) {
					SnapshotLayer snapshotLayer = e.getValue();
					snapshotLayer.paintImmediately( 0, 0, snapshotLayer.getWidth(),snapshotLayer.getHeight() );
				}
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
		// remove snapshots
		for( Map.Entry<JLayeredPane, SnapshotLayer> e : snapshots.entrySet() ) {
			JLayeredPane layeredPane = e.getKey();
			SnapshotLayer snapshotLayer = e.getValue();

			layeredPane.remove( snapshotLayer );
			layeredPane.repaint();

			snapshotLayer.flushSnapshotImages();

			// run Runnable that FlatRootPaneUI put into client properties
			JRootPane rootPane = layeredPane.getRootPane();
			rootPane.putClientProperty( "FlatLaf.internal.animatedLafChange", null );
			Runnable r = (Runnable) rootPane.getClientProperty( "FlatLaf.internal.animatedLafChange.runWhenFinished" );
			if( r != null ) {
				rootPane.putClientProperty( "FlatLaf.internal.animatedLafChange.runWhenFinished", null );
				r.run();
			}
		}

		snapshots.clear();
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

	//---- class SnapshotLayer ------------------------------------------------

	private static class SnapshotLayer
		extends JComponent
	{
		VolatileImage oldSnapshotImage;
		VolatileImage newSnapshotImage;

		@Override
		public void paint( Graphics g ) {
			if( oldSnapshotImage.contentsLost() ||
				newSnapshotImage == null || newSnapshotImage.contentsLost() )
			  return;

			// draw new UI snapshot
			g.drawImage( newSnapshotImage, 0, 0, null );

			// draw old UI snapshot
			((Graphics2D)g).setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
			g.drawImage( oldSnapshotImage, 0, 0, null );
		}

		@Override
		public void removeNotify() {
			super.removeNotify();
			flushSnapshotImages();
		}

		void flushSnapshotImages() {
			// release system resources used by volatile image
			oldSnapshotImage.flush();
			if( newSnapshotImage != null )
				newSnapshotImage.flush();
		}
	}
}
