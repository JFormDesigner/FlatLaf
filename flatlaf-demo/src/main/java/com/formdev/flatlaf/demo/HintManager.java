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

package com.formdev.flatlaf.demo;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatPopupMenuBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class HintManager
{
	private static final List<HintPanel> hintPanels = new ArrayList<>();

	static void showHint( Hint hint ) {
		// check whether user already closed the hint
		if( DemoPrefs.getState().getBoolean( hint.prefsKey, false ) ) {
			if( hint.nextHint != null )
				showHint( hint.nextHint );
			return;
		}

		HintPanel hintPanel = new HintPanel( hint );
		hintPanel.showHint();

		hintPanels.add( hintPanel );
	}

	static void hideAllHints() {
		HintPanel[] hintPanels2 = hintPanels.toArray( new HintPanel[hintPanels.size()] );
		for( HintPanel hintPanel : hintPanels2 )
			hintPanel.hideHint();
	}

	//---- class HintPanel ----------------------------------------------------

	static class Hint
	{
		private final String message;
		private final Component owner;
		private final int position;
		private final String prefsKey;
		private final Hint nextHint;

		Hint( String message, Component owner, int position, String prefsKey, Hint nextHint ) {
			this.message = message;
			this.owner = owner;
			this.position = position;
			this.prefsKey = prefsKey;
			this.nextHint = nextHint;
		}
	}

	//---- class HintPanel ----------------------------------------------------

	private static class HintPanel
		extends JPanel
	{
		private final Hint hint;

		private JPanel popup;

		private HintPanel( Hint hint ) {
			this.hint = hint;

			initComponents();

			hintLabel.setText( "<html>" + hint.message + "</html>" );

			// grab all mouse events to avoid that components overlapped
			// by the hint panel receive them
			addMouseListener( new MouseAdapter() {} );
		}

		@Override
		public void updateUI() {
			super.updateUI();

			setBackground( UIManager.getColor( "HintPanel.backgroundColor" ) );
			setBorder( new FlatPopupMenuBorder() );
		}

		void showHint() {
			JRootPane rootPane = SwingUtilities.getRootPane( hint.owner );
			if( rootPane == null )
				return;

			JLayeredPane layeredPane = rootPane.getLayeredPane();

			// create a popup panel that has a drop shadow
			popup = new JPanel( new BorderLayout() ) {
				@Override
				public void updateUI() {
					super.updateUI();

					setBorder( new FlatDropShadowBorder(
						UIManager.getColor( "Popup.dropShadowColor" ),
						UIManager.getInsets( "Popup.dropShadowInsets" ),
						FlatUIUtils.getUIFloat( "Popup.dropShadowOpacity", 0.5f ) ) );

					// use invokeLater because at this time the UI delegates
					// of child components are not yet updated
					EventQueue.invokeLater( () -> {
						validate();
						setSize( getPreferredSize() );
					} );
				}
			};
			popup.setOpaque( false );
			popup.add( this );

			// calculate x/y location for hint popup
			Point pt = SwingUtilities.convertPoint( hint.owner, 0, 0, layeredPane );
			int x = pt.x;
			int y = pt.y;
			Dimension size = popup.getPreferredSize();
			int gap = UIScale.scale( 6 );

			switch( hint.position ) {
				case SwingConstants.LEFT:
					x -= size.width + gap;
					break;

				case SwingConstants.TOP:
					y -= size.height + gap;
					break;

				case SwingConstants.RIGHT:
					x += hint.owner.getWidth() + gap;
					break;

				case SwingConstants.BOTTOM:
					y += hint.owner.getHeight() + gap;
					break;
			}

			// set hint popup size and show it
			popup.setBounds( x, y, size.width, size.height );
			layeredPane.add( popup, JLayeredPane.POPUP_LAYER );
		}

		void hideHint() {
			if( popup != null ) {
				Container parent = popup.getParent();
				if( parent != null ) {
					parent.remove( popup );
					parent.repaint( popup.getX(), popup.getY(), popup.getWidth(), popup.getHeight() );
				}
			}

			hintPanels.remove( this );
		}

		private void gotIt() {
			// hide hint
			hideHint();

			// remember that user closed the hint
			DemoPrefs.getState().putBoolean( hint.prefsKey, true );

			// show next hint (if any)
			if( hint.nextHint != null )
				HintManager.showHint( hint.nextHint );
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			hintLabel = new JLabel();
			gotItButton = new JButton();

			//======== this ========
			setLayout(new MigLayout(
				"insets dialog,hidemode 3",
				// columns
				"[::200,fill]",
				// rows
				"[]para" +
				"[]"));

			//---- hintLabel ----
			hintLabel.setText("hint");
			add(hintLabel, "cell 0 0");

			//---- gotItButton ----
			gotItButton.setText("Got it!");
			gotItButton.setFocusable(false);
			gotItButton.addActionListener(e -> gotIt());
			add(gotItButton, "cell 0 1,alignx right,growx 0");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		private JLabel hintLabel;
		private JButton gotItButton;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
