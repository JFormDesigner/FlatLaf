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

package com.formdev.flatlaf.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicDesktopIconUI;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JInternalFrame.JDesktopIcon}.
 *
 * <!-- BasicDesktopIconUI -->
 *
 * @uiDefault DesktopIcon.border					Border
 *
 * <!-- FlatDesktopIconUI -->
 *
 * @uiDefault DesktopIcon.background				Color
 * @uiDefault DesktopIcon.foreground				Color
 * @uiDefault DesktopIcon.iconSize					Dimension
 * @uiDefault DesktopIcon.closeSize					Dimension
 * @uiDefault DesktopIcon.closeIcon					Icon
 *
 * @author Karl Tauber
 */
public class FlatDesktopIconUI
	extends BasicDesktopIconUI
{
	private Dimension iconSize;
	private Dimension closeSize;

	private JLabel dockIcon;
	private JButton closeButton;
	private JToolTip titleTip;
	private ActionListener closeListener;
	private MouseInputListener mouseInputListener;
	private PropertyChangeListener ancestorListener;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatDesktopIconUI();
	}

	@Override
	public void installUI( JComponent c ) {
		super.installUI( c );

		// update dock icon preview if already iconified
		if( c.isDisplayable() )
			updateDockIconPreviewLater();
	}

	@Override
	public void uninstallUI( JComponent c ) {
		super.uninstallUI( c );

		dockIcon = null;
		closeButton = null;
	}

	@Override
	protected void installComponents() {
		dockIcon = new JLabel();
		dockIcon.setHorizontalAlignment( SwingConstants.CENTER );

		closeButton = new JButton();
		closeButton.setIcon( UIManager.getIcon( "DesktopIcon.closeIcon" ) );
		closeButton.setFocusable( false );
		closeButton.setBorder( BorderFactory.createEmptyBorder() );
		closeButton.setOpaque( true );
		closeButton.setBackground( FlatUIUtils.nonUIResource( desktopIcon.getBackground() ) );
		closeButton.setForeground( FlatUIUtils.nonUIResource( desktopIcon.getForeground() ) );
		closeButton.setVisible( false );

		desktopIcon.setLayout( new FlatDesktopIconLayout() );
		desktopIcon.add( closeButton );
		desktopIcon.add( dockIcon );
	}

	@Override
	protected void uninstallComponents() {
		hideTitleTip();

		desktopIcon.remove( dockIcon );
		desktopIcon.remove( closeButton );
		desktopIcon.setLayout( null );
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installColors( desktopIcon, "DesktopIcon.background", "DesktopIcon.foreground" );

		iconSize = UIManager.getDimension( "DesktopIcon.iconSize" );
		closeSize = UIManager.getDimension( "DesktopIcon.closeSize" );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		closeListener = e -> {
			if( frame.isClosable() )
				frame.doDefaultCloseAction();
		};
		closeButton.addActionListener( closeListener );
		closeButton.addMouseListener( mouseInputListener );

		ancestorListener = e -> {
			if( e.getNewValue() != null ) {
				// update dock icon preview if desktopIcon is added to desktop (internal frame was iconified)
				updateDockIconPreviewLater();
			} else {
				// remove preview icon to release memory
				dockIcon.setIcon( null );
			}
		};
		desktopIcon.addPropertyChangeListener( "ancestor", ancestorListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		closeButton.removeActionListener( closeListener );
		closeButton.removeMouseListener( mouseInputListener );
		closeListener = null;
		mouseInputListener = null;

		desktopIcon.removePropertyChangeListener( "ancestor", ancestorListener );
		ancestorListener = null;
	}

	@Override
	protected MouseInputListener createMouseInputListener() {
		mouseInputListener = new MouseInputAdapter() {
			@Override
			public void mouseReleased( MouseEvent e ) {
				if( frame.isIcon() && desktopIcon.contains( e.getX(), e.getY() ) ) {
					hideTitleTip();
					closeButton.setVisible( false );

					try {
						frame.setIcon( false );
					} catch( PropertyVetoException ex ) {
						// ignore
					}
				}
			}

			@Override
			public void mouseEntered( MouseEvent e ) {
				showTitleTip();
				if( frame.isClosable() )
					closeButton.setVisible( true );
			}

			@Override
			public void mouseExited( MouseEvent e ) {
				hideTitleTip();
				closeButton.setVisible( false );
			}
		};
		return mouseInputListener;
	}

	private void showTitleTip() {
		JRootPane rootPane = SwingUtilities.getRootPane( desktopIcon );
		if( rootPane == null )
			return;

		if( titleTip == null ) {
			titleTip = new JToolTip();
			rootPane.getLayeredPane().add( titleTip, JLayeredPane.POPUP_LAYER );
		}
		titleTip.setTipText( frame.getTitle() );
		titleTip.setSize( titleTip.getPreferredSize() );

		int tx = (desktopIcon.getWidth() - titleTip.getWidth()) / 2;
		int ty = -(titleTip.getHeight() + UIScale.scale( 4 ));
		Point pt = SwingUtilities.convertPoint( desktopIcon, tx, ty, titleTip.getParent() );
		if( pt.x + titleTip.getWidth() > rootPane.getWidth() )
			pt.x = rootPane.getWidth() - titleTip.getWidth();
		if( pt.x < 0 )
			pt.x = 0;
		titleTip.setLocation( pt );
		titleTip.repaint();
	}

	private void hideTitleTip() {
		if( titleTip == null )
			return;

		titleTip.setVisible( false );
		titleTip.getParent().remove( titleTip );
		titleTip = null;
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		return UIScale.scale( iconSize );
	}

	@Override
	public Dimension getMinimumSize( JComponent c ) {
		return getPreferredSize( c );
	}

	@Override
	public Dimension getMaximumSize( JComponent c ) {
		return getPreferredSize( c );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() ) {
			// fill background with color derived from desktop pane
			Color background = c.getBackground();
			JDesktopPane desktopPane = desktopIcon.getDesktopPane();
			g.setColor( (desktopPane != null)
				? FlatUIUtils.deriveColor( background, desktopPane.getBackground() )
				: background );
			g.fillRect( 0, 0, c.getWidth(), c.getHeight() );
		}

		paint( g, c );
	}

	private void updateDockIconPreviewLater() {
		// use invoke later to make sure that components are updated when switching LaF
		EventQueue.invokeLater( () -> {
			if( dockIcon != null )
				updateDockIconPreview();
		} );
	}

	protected void updateDockIconPreview() {
		// make sure that frame is not selected
		if( frame.isSelected() ) {
			try {
				frame.setSelected( false );
			} catch( PropertyVetoException ex ) {
				// ignore
			}
		}

		// layout internal frame title pane, which was recreated when switching Laf
		// (directly invoke doLayout() because frame.validate() does not work here
		// because frame is not displayable)
		if( !frame.isValid() )
			frame.doLayout();
		for( Component c : frame.getComponents() ) {
			if( !c.isValid() )
				c.doLayout();
		}

		// paint internal frame to buffered image
		int frameWidth = Math.max( frame.getWidth(), 1 );
		int frameHeight = Math.max( frame.getHeight(), 1 );
		BufferedImage frameImage = new BufferedImage( frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = frameImage.createGraphics();
		try {
			frame.paint( g );
		} finally {
			g.dispose();
		}

		// compute preview size (keep ratio; also works with non-square preview)
		Insets insets = desktopIcon.getInsets();
		int previewWidth = UIScale.scale( iconSize.width ) - insets.left - insets.right;
		int previewHeight = UIScale.scale( iconSize.height ) - insets.top - insets.bottom;
		float frameRatio = ((float) frameHeight / (float) frameWidth);
		if( ((float) previewWidth / (float) frameWidth) > ((float) previewHeight / (float) frameHeight) )
			previewWidth = Math.round( previewHeight / frameRatio );
		else
			previewHeight = Math.round( previewWidth * frameRatio );

		// scale preview
		Image previewImage = frameImage.getScaledInstance( previewWidth, previewHeight, Image.SCALE_SMOOTH );
		if( MultiResolutionImageSupport.isAvailable() ) {
			// On HiDPI screens, create preview images for 1x, 2x and current scale factor.
			// The icon then chooses the best resolution for painting, which is usually
			// the one for the current scale factor. But if changing scale factor or
			// moving window to another screen with different scale factor, then another
			// resolution may be used because the preview icon is not updated.
			Image previewImage2x = frameImage.getScaledInstance( previewWidth * 2, previewHeight * 2, Image.SCALE_SMOOTH );
			double scaleFactor = UIScale.getSystemScaleFactor( desktopIcon.getGraphicsConfiguration() );
			if( scaleFactor != 1 && scaleFactor != 2 ) {
				Image previewImageCurrent = frameImage.getScaledInstance(
					(int) Math.round( previewWidth * scaleFactor ),
					(int) Math.round( previewHeight * scaleFactor ),
					Image.SCALE_SMOOTH );

				// the images must be ordered by resolution
				previewImage = (scaleFactor < 2)
					? MultiResolutionImageSupport.create( 0, previewImage, previewImageCurrent, previewImage2x )
					: MultiResolutionImageSupport.create( 0, previewImage, previewImage2x, previewImageCurrent );
			} else
				previewImage = MultiResolutionImageSupport.create( 0, previewImage, previewImage2x );
		}
		dockIcon.setIcon( new ImageIcon( previewImage ) );
	}

	//---- class DockIcon -----------------------------------------------------

	private class FlatDesktopIconLayout
		implements LayoutManager
	{
		@Override public void addLayoutComponent( String name, Component comp ) {}
		@Override public void removeLayoutComponent( Component comp ) {}

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			return dockIcon.getPreferredSize();
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			return dockIcon.getMinimumSize();
		}

		@Override
		public void layoutContainer( Container parent ) {
			Insets insets = parent.getInsets();

			// dock icon
			dockIcon.setBounds( insets.left, insets.top,
				parent.getWidth() - insets.left - insets.right,
				parent.getHeight() - insets.top - insets.bottom );

			// close button in upper right corner
			Dimension cSize = UIScale.scale( closeSize );
			closeButton.setBounds( parent.getWidth() - cSize.width, 0, cSize.width, cSize.height );
		}
	}
}
