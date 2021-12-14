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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import com.formdev.flatlaf.util.ScaledImageIcon;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF internal frame title bar.
 *
 * @author Karl Tauber
 */
public class FlatInternalFrameTitlePane
	extends BasicInternalFrameTitlePane
{
	private JLabel titleLabel;
	private JPanel buttonPanel;

	public FlatInternalFrameTitlePane( JInternalFrame f ) {
		super( f );
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installBorder( this, "InternalFrameTitlePane.border" );
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new FlatPropertyChangeHandler();
	}

	@Override
	protected LayoutManager createLayout() {
		return new BorderLayout( UIScale.scale( 4 ), 0 );
	}

	@Override
	protected void createButtons() {
		super.createButtons();

		iconButton.setContentAreaFilled( false );
		maxButton.setContentAreaFilled( false );
		closeButton.setContentAreaFilled( false );

		Border emptyBorder = BorderFactory.createEmptyBorder();
		iconButton.setBorder( emptyBorder );
		maxButton.setBorder( emptyBorder );
		closeButton.setBorder( emptyBorder );

		updateButtonsVisibility();
	}

	@Override
	protected void addSubComponents() {
		titleLabel = new JLabel( frame.getTitle() );
		titleLabel.setFont( FlatUIUtils.nonUIResource( getFont() ) );
		titleLabel.setMinimumSize( new Dimension( UIScale.scale( 32 ), 1 ) );
		updateFrameIcon();
		updateColors();

		buttonPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				int height = size.height;
				// use height of invisible buttons to always have same title pane height
				if( !iconButton.isVisible() )
					height = Math.max( height, iconButton.getPreferredSize().height );
				if( !maxButton.isVisible() )
					height = Math.max( height, maxButton.getPreferredSize().height );
				if( !closeButton.isVisible() )
					height = Math.max( height, closeButton.getPreferredSize().height );
				return new Dimension( size.width, height );
			}
		};
		buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.LINE_AXIS ) );
		buttonPanel.setOpaque( false );

		buttonPanel.add( iconButton );
		buttonPanel.add( maxButton );
		buttonPanel.add( closeButton );

		add( titleLabel, BorderLayout.CENTER );
		add( buttonPanel, BorderLayout.LINE_END );
	}

	protected void updateFrameIcon() {
		Icon frameIcon = frame.getFrameIcon();
		if( frameIcon != null && (frameIcon.getIconWidth() == 0 || frameIcon.getIconHeight() == 0) )
			frameIcon = null;
		else if( frameIcon instanceof ImageIcon )
			frameIcon = new ScaledImageIcon( (ImageIcon) frameIcon );
		titleLabel.setIcon( frameIcon );
	}

	protected void updateColors() {
		Color background = FlatUIUtils.nonUIResource( frame.isSelected() ? selectedTitleColor : notSelectedTitleColor );
		Color foreground = FlatUIUtils.nonUIResource( frame.isSelected() ? selectedTextColor : notSelectedTextColor );

		titleLabel.setForeground( foreground );
		iconButton.setBackground( background );
		iconButton.setForeground( foreground );
		maxButton.setBackground( background );
		maxButton.setForeground( foreground );
		closeButton.setBackground( background );
		closeButton.setForeground( foreground );
	}

	protected void updateButtonsVisibility() {
		iconButton.setVisible( frame.isIconifiable() );
		maxButton.setVisible( frame.isMaximizable() );
		closeButton.setVisible( frame.isClosable() );
	}

	Rectangle getFrameIconBounds() {
		Icon icon = titleLabel.getIcon();
		if( icon == null )
			return null;

		int iconWidth = icon.getIconWidth();
		int iconHeight = icon.getIconHeight();
		boolean leftToRight = titleLabel.getComponentOrientation().isLeftToRight();
		int x = titleLabel.getX() + (leftToRight ? 0 : (titleLabel.getWidth() - iconWidth));
		int y = titleLabel.getY() + ((titleLabel.getHeight() - iconHeight) / 2);
		return new Rectangle( x, y, iconWidth, iconHeight );
	}

	/**
	 * Does nothing because FlatLaf internal frames do not have system menus.
	 */
	@Override
	protected void assembleSystemMenu() {
	}

	/**
	 * Does nothing because FlatLaf internal frames do not have system menus.
	 */
	@Override
	protected void showSystemMenu() {
	}

	@Override
	public void paintComponent( Graphics g ) {
		paintTitleBackground( g );
	}

	//---- class FlatPropertyChangeHandler ------------------------------------

	protected class FlatPropertyChangeHandler
		extends PropertyChangeHandler
	{
		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			switch( e.getPropertyName() ) {
				case JInternalFrame.TITLE_PROPERTY:
					titleLabel.setText( frame.getTitle() );
					break;

				case JInternalFrame.FRAME_ICON_PROPERTY:
					updateFrameIcon();
					break;

				case JInternalFrame.IS_SELECTED_PROPERTY:
					updateColors();
					break;

				case "iconable":
				case "maximizable":
				case "closable":
					updateButtonsVisibility();
					enableActions();
					revalidate();
					repaint();

					// do not invoke super.propertyChange() because this adds/removes the buttons
					return;

				case "componentOrientation":
					applyComponentOrientation( frame.getComponentOrientation() );
					break;

				case "opaque":
					// Do not invoke super.propertyChange() here because it always
					// invokes repaint(), which would cause endless repainting.
					// The opaque flag is temporary changed in FlatUIUtils.hasOpaqueBeenExplicitlySet(),
					// invoked from FlatInternalFrameUI.update().
					return;
			}

			super.propertyChange( e );
		}
	}
}
