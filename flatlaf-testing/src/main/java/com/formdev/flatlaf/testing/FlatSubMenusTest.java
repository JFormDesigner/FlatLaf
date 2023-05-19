/*
 * Copyright 2022 FormDev Software GmbH
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

import java.awt.Component;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatSubMenusTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatSubMenusTest" );
			UIManager.put( "FlatLaf.debug.menu.showSafeTriangle", true );
			frame.applyComponentOrientationToFrame = true;
			frame.showFrame( FlatSubMenusTest::new, panel -> ((FlatSubMenusTest)panel).menuBar );
		} );
	}

	FlatSubMenusTest() {
		initComponents();
	}

	private void showPopupMenuButtonActionPerformed(ActionEvent e) {
		Component invoker = (Component) e.getSource();
		PopupMenu popupMenu = new PopupMenu();
		popupMenu.applyComponentOrientation( getComponentOrientation() );
		popupMenu.show( invoker, 0, invoker.getHeight() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JButton showPopupMenuButton = new JButton();
		menuBar = new JMenuBar();
		JMenu menu3 = new JMenu();
		JMenuItem menuItem1 = new JMenuItem();
		JMenuItem menuItem50 = new JMenuItem();
		JMenu menu4 = new JMenu();
		JMenuItem menuItem9 = new JMenuItem();
		JMenuItem menuItem10 = new JMenuItem();
		JMenuItem menuItem11 = new JMenuItem();
		JMenu menu8 = new JMenu();
		JMenu menu21 = new JMenu();
		JMenuItem menuItem81 = new JMenuItem();
		JMenuItem menuItem82 = new JMenuItem();
		JMenuItem menuItem83 = new JMenuItem();
		JMenuItem menuItem41 = new JMenuItem();
		JMenuItem menuItem42 = new JMenuItem();
		JMenuItem menuItem43 = new JMenuItem();
		JMenuItem menuItem44 = new JMenuItem();
		JMenuItem menuItem45 = new JMenuItem();
		JMenuItem menuItem46 = new JMenuItem();
		JMenuItem menuItem12 = new JMenuItem();
		JMenuItem menuItem13 = new JMenuItem();
		JMenuItem menuItem14 = new JMenuItem();
		JMenuItem menuItem2 = new JMenuItem();
		JMenu menu5 = new JMenu();
		JMenuItem menuItem15 = new JMenuItem();
		JMenuItem menuItem16 = new JMenuItem();
		JMenuItem menuItem17 = new JMenuItem();
		JMenuItem menuItem18 = new JMenuItem();
		JMenuItem menuItem19 = new JMenuItem();
		JMenuItem menuItem20 = new JMenuItem();
		JMenuItem menuItem21 = new JMenuItem();
		JMenuItem menuItem22 = new JMenuItem();
		JMenuItem menuItem23 = new JMenuItem();
		JMenuItem menuItem24 = new JMenuItem();
		JMenuItem menuItem25 = new JMenuItem();
		JMenuItem menuItem26 = new JMenuItem();
		JMenuItem menuItem27 = new JMenuItem();
		JMenuItem menuItem28 = new JMenuItem();
		JMenuItem menuItem29 = new JMenuItem();
		JMenu menu6 = new JMenu();
		JMenuItem menuItem30 = new JMenuItem();
		JMenuItem menuItem31 = new JMenuItem();
		JMenuItem menuItem32 = new JMenuItem();
		JMenu menu9 = new JMenu();
		JMenuItem menuItem47 = new JMenuItem();
		JMenuItem menuItem48 = new JMenuItem();
		JMenuItem menuItem49 = new JMenuItem();
		JMenuItem menuItem33 = new JMenuItem();
		JMenuItem menuItem34 = new JMenuItem();
		JMenuItem menuItem3 = new JMenuItem();
		JMenuItem menuItem4 = new JMenuItem();
		JMenuItem menuItem5 = new JMenuItem();
		JMenuItem menuItem6 = new JMenuItem();
		JMenuItem menuItem7 = new JMenuItem();
		JMenuItem menuItem8 = new JMenuItem();
		JMenu menu7 = new JMenu();
		JMenuItem menuItem35 = new JMenuItem();
		JMenuItem menuItem37 = new JMenuItem();
		JMenuItem menuItem36 = new JMenuItem();
		JMenuItem menuItem38 = new JMenuItem();
		JMenuItem menuItem39 = new JMenuItem();
		JMenuItem menuItem40 = new JMenuItem();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]",
			// rows
			"[]"));

		//---- showPopupMenuButton ----
		showPopupMenuButton.setText("show JPopupMenu");
		showPopupMenuButton.addActionListener(e -> showPopupMenuButtonActionPerformed(e));
		add(showPopupMenuButton, "cell 0 0");

		//======== menuBar ========
		{

			//======== menu3 ========
			{
				menu3.setText("main menu");

				//---- menuItem1 ----
				menuItem1.setText("text");
				menu3.add(menuItem1);

				//---- menuItem50 ----
				menuItem50.setText("text");
				menu3.add(menuItem50);

				//======== menu4 ========
				{
					menu4.setText("text");

					//---- menuItem9 ----
					menuItem9.setText("text");
					menu4.add(menuItem9);

					//---- menuItem10 ----
					menuItem10.setText("text text");
					menu4.add(menuItem10);

					//---- menuItem11 ----
					menuItem11.setText("text");
					menu4.add(menuItem11);

					//======== menu8 ========
					{
						menu8.setText("text");

						//======== menu21 ========
						{
							menu21.setText("text");

							//---- menuItem81 ----
							menuItem81.setText("text");
							menu21.add(menuItem81);

							//---- menuItem82 ----
							menuItem82.setText("text");
							menu21.add(menuItem82);

							//---- menuItem83 ----
							menuItem83.setText("text");
							menu21.add(menuItem83);
						}
						menu8.add(menu21);

						//---- menuItem41 ----
						menuItem41.setText("text");
						menu8.add(menuItem41);

						//---- menuItem42 ----
						menuItem42.setText("text");
						menu8.add(menuItem42);

						//---- menuItem43 ----
						menuItem43.setText("text");
						menu8.add(menuItem43);

						//---- menuItem44 ----
						menuItem44.setText("text");
						menu8.add(menuItem44);

						//---- menuItem45 ----
						menuItem45.setText("text");
						menu8.add(menuItem45);

						//---- menuItem46 ----
						menuItem46.setText("text");
						menu8.add(menuItem46);
					}
					menu4.add(menu8);

					//---- menuItem12 ----
					menuItem12.setText("text");
					menu4.add(menuItem12);

					//---- menuItem13 ----
					menuItem13.setText("text");
					menu4.add(menuItem13);

					//---- menuItem14 ----
					menuItem14.setText("text");
					menu4.add(menuItem14);
				}
				menu3.add(menu4);

				//---- menuItem2 ----
				menuItem2.setText("text");
				menu3.add(menuItem2);

				//======== menu5 ========
				{
					menu5.setText("text");

					//---- menuItem15 ----
					menuItem15.setText("text bla bla");
					menu5.add(menuItem15);

					//---- menuItem16 ----
					menuItem16.setText("text");
					menu5.add(menuItem16);

					//---- menuItem17 ----
					menuItem17.setText("text");
					menu5.add(menuItem17);

					//---- menuItem18 ----
					menuItem18.setText("text");
					menu5.add(menuItem18);

					//---- menuItem19 ----
					menuItem19.setText("text");
					menu5.add(menuItem19);

					//---- menuItem20 ----
					menuItem20.setText("text");
					menu5.add(menuItem20);

					//---- menuItem21 ----
					menuItem21.setText("text");
					menu5.add(menuItem21);

					//---- menuItem22 ----
					menuItem22.setText("text");
					menu5.add(menuItem22);

					//---- menuItem23 ----
					menuItem23.setText("text");
					menu5.add(menuItem23);

					//---- menuItem24 ----
					menuItem24.setText("text");
					menu5.add(menuItem24);

					//---- menuItem25 ----
					menuItem25.setText("text");
					menu5.add(menuItem25);

					//---- menuItem26 ----
					menuItem26.setText("text");
					menu5.add(menuItem26);

					//---- menuItem27 ----
					menuItem27.setText("text");
					menu5.add(menuItem27);

					//---- menuItem28 ----
					menuItem28.setText("text");
					menu5.add(menuItem28);

					//---- menuItem29 ----
					menuItem29.setText("text");
					menu5.add(menuItem29);
				}
				menu3.add(menu5);

				//======== menu6 ========
				{
					menu6.setText("text");

					//---- menuItem30 ----
					menuItem30.setText("text o text");
					menu6.add(menuItem30);

					//---- menuItem31 ----
					menuItem31.setText("text");
					menu6.add(menuItem31);

					//---- menuItem32 ----
					menuItem32.setText("text");
					menu6.add(menuItem32);

					//======== menu9 ========
					{
						menu9.setText("text");

						//---- menuItem47 ----
						menuItem47.setText("text");
						menu9.add(menuItem47);

						//---- menuItem48 ----
						menuItem48.setText("text");
						menu9.add(menuItem48);

						//---- menuItem49 ----
						menuItem49.setText("text");
						menu9.add(menuItem49);
					}
					menu6.add(menu9);

					//---- menuItem33 ----
					menuItem33.setText("text");
					menu6.add(menuItem33);

					//---- menuItem34 ----
					menuItem34.setText("text");
					menu6.add(menuItem34);
				}
				menu3.add(menu6);

				//---- menuItem3 ----
				menuItem3.setText("text");
				menu3.add(menuItem3);

				//---- menuItem4 ----
				menuItem4.setText("longer text");
				menu3.add(menuItem4);

				//---- menuItem5 ----
				menuItem5.setText("text");
				menu3.add(menuItem5);

				//---- menuItem6 ----
				menuItem6.setText("text");
				menu3.add(menuItem6);

				//---- menuItem7 ----
				menuItem7.setText("text");
				menu3.add(menuItem7);

				//---- menuItem8 ----
				menuItem8.setText("text");
				menu3.add(menuItem8);

				//======== menu7 ========
				{
					menu7.setText("text");

					//---- menuItem35 ----
					menuItem35.setText("text abc");
					menu7.add(menuItem35);

					//---- menuItem37 ----
					menuItem37.setText("text");
					menu7.add(menuItem37);

					//---- menuItem36 ----
					menuItem36.setText("text");
					menu7.add(menuItem36);

					//---- menuItem38 ----
					menuItem38.setText("text");
					menu7.add(menuItem38);

					//---- menuItem39 ----
					menuItem39.setText("text");
					menu7.add(menuItem39);

					//---- menuItem40 ----
					menuItem40.setText("text");
					menu7.add(menuItem40);
				}
				menu3.add(menu7);
			}
			menuBar.add(menu3);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JMenuBar menuBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class PopupMenu ----------------------------------------------------

	private static class PopupMenu extends JPopupMenu {
		private PopupMenu() {
			initComponents();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			JMenuItem menuItem54 = new JMenuItem();
			JMenuItem menuItem51 = new JMenuItem();
			JMenu menu10 = new JMenu();
			JMenuItem menuItem57 = new JMenuItem();
			JMenuItem menuItem58 = new JMenuItem();
			JMenu menu14 = new JMenu();
			JMenu menu18 = new JMenu();
			JMenu menu19 = new JMenu();
			JMenuItem menuItem76 = new JMenuItem();
			JMenuItem menuItem77 = new JMenuItem();
			JMenuItem menuItem78 = new JMenuItem();
			JMenu menu20 = new JMenu();
			JMenuItem menuItem73 = new JMenuItem();
			JMenuItem menuItem74 = new JMenuItem();
			JMenuItem menuItem75 = new JMenuItem();
			JMenuItem menuItem79 = new JMenuItem();
			JMenuItem menuItem80 = new JMenuItem();
			JMenuItem menuItem70 = new JMenuItem();
			JMenuItem menuItem71 = new JMenuItem();
			JMenuItem menuItem72 = new JMenuItem();
			JMenuItem menuItem59 = new JMenuItem();
			JMenuItem menuItem60 = new JMenuItem();
			JMenuItem menuItem52 = new JMenuItem();
			JMenu menu11 = new JMenu();
			JMenuItem menuItem61 = new JMenuItem();
			JMenuItem menuItem62 = new JMenuItem();
			JMenuItem menuItem63 = new JMenuItem();
			JMenu menu12 = new JMenu();
			JMenuItem menuItem64 = new JMenuItem();
			JMenuItem menuItem65 = new JMenuItem();
			JMenuItem menuItem66 = new JMenuItem();
			JMenuItem menuItem53 = new JMenuItem();
			JMenuItem menuItem55 = new JMenuItem();
			JMenuItem menuItem56 = new JMenuItem();
			JMenu menu13 = new JMenu();
			JMenuItem menuItem67 = new JMenuItem();
			JMenuItem menuItem68 = new JMenuItem();
			JMenuItem menuItem69 = new JMenuItem();

			//======== this ========

			//---- menuItem54 ----
			menuItem54.setText("text");
			add(menuItem54);

			//---- menuItem51 ----
			menuItem51.setText("text text");
			add(menuItem51);

			//======== menu10 ========
			{
				menu10.setText("text");

				//---- menuItem57 ----
				menuItem57.setText("text");
				menu10.add(menuItem57);

				//---- menuItem58 ----
				menuItem58.setText("text");
				menu10.add(menuItem58);

				//======== menu14 ========
				{
					menu14.setText("text");

					//======== menu18 ========
					{
						menu18.setText("text");

						//======== menu19 ========
						{
							menu19.setText("text");

							//---- menuItem76 ----
							menuItem76.setText("text");
							menu19.add(menuItem76);

							//---- menuItem77 ----
							menuItem77.setText("text");
							menu19.add(menuItem77);

							//---- menuItem78 ----
							menuItem78.setText("text");
							menu19.add(menuItem78);
						}
						menu18.add(menu19);

						//======== menu20 ========
						{
							menu20.setText("text");

							//---- menuItem73 ----
							menuItem73.setText("text");
							menu20.add(menuItem73);

							//---- menuItem74 ----
							menuItem74.setText("text");
							menu20.add(menuItem74);

							//---- menuItem75 ----
							menuItem75.setText("text");
							menu20.add(menuItem75);
						}
						menu18.add(menu20);

						//---- menuItem79 ----
						menuItem79.setText("text");
						menu18.add(menuItem79);

						//---- menuItem80 ----
						menuItem80.setText("text");
						menu18.add(menuItem80);
					}
					menu14.add(menu18);

					//---- menuItem70 ----
					menuItem70.setText("text");
					menu14.add(menuItem70);

					//---- menuItem71 ----
					menuItem71.setText("text");
					menu14.add(menuItem71);

					//---- menuItem72 ----
					menuItem72.setText("text");
					menu14.add(menuItem72);
				}
				menu10.add(menu14);

				//---- menuItem59 ----
				menuItem59.setText("text");
				menu10.add(menuItem59);

				//---- menuItem60 ----
				menuItem60.setText("text");
				menu10.add(menuItem60);
			}
			add(menu10);

			//---- menuItem52 ----
			menuItem52.setText("text");
			add(menuItem52);

			//======== menu11 ========
			{
				menu11.setText("text");

				//---- menuItem61 ----
				menuItem61.setText("text");
				menu11.add(menuItem61);

				//---- menuItem62 ----
				menuItem62.setText("text");
				menu11.add(menuItem62);

				//---- menuItem63 ----
				menuItem63.setText("text");
				menu11.add(menuItem63);
			}
			add(menu11);

			//======== menu12 ========
			{
				menu12.setText("text");

				//---- menuItem64 ----
				menuItem64.setText("text");
				menu12.add(menuItem64);

				//---- menuItem65 ----
				menuItem65.setText("text");
				menu12.add(menuItem65);

				//---- menuItem66 ----
				menuItem66.setText("text");
				menu12.add(menuItem66);
			}
			add(menu12);

			//---- menuItem53 ----
			menuItem53.setText("text");
			add(menuItem53);

			//---- menuItem55 ----
			menuItem55.setText("text");
			add(menuItem55);

			//---- menuItem56 ----
			menuItem56.setText("text");
			add(menuItem56);

			//======== menu13 ========
			{
				menu13.setText("text");

				//---- menuItem67 ----
				menuItem67.setText("text");
				menu13.add(menuItem67);

				//---- menuItem68 ----
				menuItem68.setText("text");
				menu13.add(menuItem68);

				//---- menuItem69 ----
				menuItem69.setText("text");
				menu13.add(menuItem69);
			}
			add(menu13);
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
