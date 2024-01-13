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

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.beans.*;
import java.net.URL;
import javax.swing.*;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.util.MultiResolutionImageSupport;
import com.formdev.flatlaf.util.SystemInfo;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatDisabledIconsTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatDisabledIconsTest" );
			frame.showFrame( FlatDisabledIconsTest::new );
		} );
	}

	FlatDisabledIconsTest() {
		initComponents();

		RGBImageFilter plasticLafFilter = new PlasticRGBGrayFilter();
		RGBImageFilter intellijTextFilter = new IntelliJGrayFilter( 20, 0, 100 );
		RGBImageFilter netbeansFilter = new NetBeansDisabledButtonFilter();

		for( Component c : enabledToolBar.getComponents() ) {
			AbstractButton b = (AbstractButton) c;
			Icon icon = b.getIcon();

			JToggleButton cb = new JToggleButton( icon );
			cb.setEnabled( false );
			currentLafToolBar.add( cb );
			basicLafToolBar.add( new FilterButton( null, icon ) );
			metalLafToolBar.add( new FilterButton( null, icon ) );
			plasticToolBar.add( new FilterButton( plasticLafFilter, icon ) );
			intellijTextToolBar.add( new FilterButton( intellijTextFilter, icon ) );
			intellijLightToolBar.add( new FilterButton( null, icon ) );
			intellijDarkToolBar.add( new FilterButton( null, icon ) );
			netbeansToolBar.add( new FilterButton( netbeansFilter, icon ) );
		}

		Icon zipIcon = zipButton.getIcon();
		JToggleButton cb = new JToggleButton( zipIcon );
		cb.setEnabled( false );
		zipToolBar.add( cb );
		zipToolBar.add( new FilterButton( null, zipIcon ) );
		zipToolBar.add( new FilterButton( null, zipIcon ) );
		zipToolBar.add( new FilterButton( plasticLafFilter, zipIcon ) );
		zipToolBar.add( new FilterButton( intellijTextFilter, zipIcon ) );
		zipToolBar.add( new FilterButton( null, zipIcon ) );
		zipToolBar.add( new FilterButton( null, zipIcon ) );
		zipToolBar.add( new FilterButton( netbeansFilter, zipIcon ) );

		basicLafReset();
		metalLafReset();

		intelliJTextFilterController.defaultBrightness = 20;
		intelliJTextFilterController.defaultContrast = 0;
		intelliJTextFilterController.reset();

		// values from intellijlaf.properties
		intelliJLightFilterController.defaultBrightness = 33;
		intelliJLightFilterController.defaultContrast = -35;
		intelliJLightFilterController.reset();

		// values from darcula.properties
		intelliJDarkFilterController.defaultBrightness = -70;
		intelliJDarkFilterController.defaultContrast = -70;
		intelliJDarkFilterController.reset();

		toolBars = new JToolBar[] {
			enabledToolBar,
			currentLafToolBar,
			basicLafToolBar,
			metalLafToolBar,
			plasticToolBar,
			intellijTextToolBar,
			intellijLightToolBar,
			intellijDarkToolBar,
			netbeansToolBar
		};
	}

	private void selectedChanged() {
		boolean armed = selectedCheckBox.isSelected();
		for( Component c : getComponents() ) {
			if( c instanceof JToolBar ) {
				for( Component c2 : ((JToolBar)c).getComponents() ) {
					if( c2 instanceof JToggleButton )
						((JToggleButton)c2).getModel().setSelected( armed );
				}
			}
		}
	}

	private final JToolBar[] toolBars;
	private Icon[] oldIcons;
	private static final String[] COLOR_NAMES = {
		"Actions.Red",
		"Actions.Yellow",
		"Actions.Green",
		"Actions.Blue",
		"Actions.Grey",
		"Actions.GreyInline",

		"Objects.Grey",
		"Objects.Blue",
		"Objects.Green",
		"Objects.Yellow",
		"Objects.YellowDark",
		"Objects.Purple",
		"Objects.Pink",
		"Objects.Red",
		"Objects.RedStatus",
		"Objects.GreenAndroid",
		"Objects.BlackText",

		"", // black
	};

	private void paletteIconsChanged() {
		if( paletteIconsCheckBox.isSelected() ) {
			oldIcons = new Icon[COLOR_NAMES.length];
			for( int i = 0; i < COLOR_NAMES.length; i++ )
				oldIcons[i] = ((JToggleButton)enabledToolBar.getComponent( i )).getIcon();

			for( int i = 0; i < COLOR_NAMES.length; i++ ) {
				ColorIcon icon = new ColorIcon( UIManager.getColor( COLOR_NAMES[i] ) );
				for( JToolBar toolBar : toolBars )
					((JToggleButton)toolBar.getComponent( i )).setIcon( icon );
			}
		} else if( oldIcons != null ){
			for( int i = 0; i < COLOR_NAMES.length; i++ ) {
				for( JToolBar toolBar : toolBars )
					((JToggleButton)toolBar.getComponent( i )).setIcon( oldIcons[i] );
			}
		}
	}

	private void basicLafChanged() {
		boolean brighter = basicLafBrighterCheckBox.isSelected();
		int percent = basicLafPercentSlider.getValue();

		basicLafPercentValue.setText( String.valueOf( percent ) );

		RGBImageFilter filter = new GrayFilter( brighter, percent );
		updateFilter( basicLafToolBar, 2, filter );
	}

	private void basicLafReset() {
		basicLafBrighterCheckBox.setSelected( true );
		basicLafPercentSlider.setValue( 50 );
		basicLafChanged();
	}

	private void metalLafChanged() {
		int min = metalLafMinSlider.getValue();
		int max = metalLafMaxSlider.getValue();

		metalLafMinValue.setText( String.valueOf( min ) );
		metalLafMaxValue.setText( String.valueOf( max ) );

		RGBImageFilter filter = new MetalDisabledButtonImageFilter( min, max );
		updateFilter( metalLafToolBar, 3, filter );
	}

	private void metalLafReset() {
		metalLafMinSlider.setValue( 180 );
		metalLafMaxSlider.setValue( 215 );
		metalLafChanged();
	}

	private void intelliJTextFilterChanged(PropertyChangeEvent e) {
		updateFilter( intellijTextToolBar, 5, (RGBImageFilter) e.getNewValue() );
	}

	private void intelliJLightFilterChanged(PropertyChangeEvent e) {
		updateFilter( intellijLightToolBar, 6, (RGBImageFilter) e.getNewValue() );
	}

	private void intelliJDarkFilterChanged(PropertyChangeEvent e) {
		updateFilter( intellijDarkToolBar, 7, (RGBImageFilter) e.getNewValue() );
	}

	private void updateFilter( JToolBar toolBar, int zipIndex, RGBImageFilter filter ) {
		for( Component c : toolBar.getComponents() )
			((FilterButton)c).setFilter( filter );

		((FilterButton)zipToolBar.getComponent( zipIndex )).setFilter( filter );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel enabledLabel = new JLabel();
		enabledToolBar = new JToolBar();
		JToggleButton button1 = new JToggleButton();
		JToggleButton button2 = new JToggleButton();
		JToggleButton button3 = new JToggleButton();
		JToggleButton button4 = new JToggleButton();
		JToggleButton button5 = new JToggleButton();
		JToggleButton button6 = new JToggleButton();
		JToggleButton button7 = new JToggleButton();
		JToggleButton button8 = new JToggleButton();
		JToggleButton button9 = new JToggleButton();
		JToggleButton button10 = new JToggleButton();
		JToggleButton button11 = new JToggleButton();
		JToggleButton button12 = new JToggleButton();
		JToggleButton button13 = new JToggleButton();
		JToggleButton button14 = new JToggleButton();
		JToggleButton button15 = new JToggleButton();
		JToggleButton button16 = new JToggleButton();
		JToggleButton button17 = new JToggleButton();
		JToggleButton button18 = new JToggleButton();
		JLabel currentLabel = new JLabel();
		currentLafToolBar = new JToolBar();
		JLabel basicLafLabel = new JLabel();
		basicLafToolBar = new JToolBar();
		JPanel panel2 = new JPanel();
		basicLafBrighterCheckBox = new JCheckBox();
		JLabel basicLafPercentLabel = new JLabel();
		basicLafPercentSlider = new JSlider();
		basicLafPercentValue = new JLabel();
		JButton basicLafResetButton = new JButton();
		JLabel metalLafLabel = new JLabel();
		metalLafToolBar = new JToolBar();
		JPanel panel4 = new JPanel();
		JLabel metalLafMinLabel = new JLabel();
		metalLafMinSlider = new JSlider();
		metalLafMinValue = new JLabel();
		JButton metalLafResetButton = new JButton();
		JLabel metalLafMaxLabel = new JLabel();
		metalLafMaxSlider = new JSlider();
		metalLafMaxValue = new JLabel();
		JLabel plasticLabel = new JLabel();
		plasticToolBar = new JToolBar();
		JLabel intellijTextLabel = new JLabel();
		intellijTextToolBar = new JToolBar();
		intelliJTextFilterController = new FlatDisabledIconsTest.IntelliJFilterController();
		JLabel intellijLightLabel = new JLabel();
		intellijLightToolBar = new JToolBar();
		intelliJLightFilterController = new FlatDisabledIconsTest.IntelliJFilterController();
		JLabel intellijDarkLabel = new JLabel();
		intellijDarkToolBar = new JToolBar();
		intelliJDarkFilterController = new FlatDisabledIconsTest.IntelliJFilterController();
		JLabel netbeansLabel = new JLabel();
		netbeansToolBar = new JToolBar();
		zipToolBar = new JToolBar();
		zipButton = new JToggleButton();
		selectedCheckBox = new JCheckBox();
		paletteIconsCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[fill]" +
			"[fill]" +
			"[left]" +
			"[fill]",
			// rows
			"[]" +
			"[]" +
			"[center]" +
			"[center]" +
			"[center]" +
			"[center]" +
			"[center]" +
			"[center]" +
			"[center]para" +
			"[center]" +
			"[]"));

		//---- enabledLabel ----
		enabledLabel.setText("enabled");
		add(enabledLabel, "cell 0 0");

		//======== enabledToolBar ========
		{

			//---- button1 ----
			button1.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/netbeans-cut24.gif")));
			enabledToolBar.add(button1);

			//---- button2 ----
			button2.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/netbeans-copy24.gif")));
			enabledToolBar.add(button2);

			//---- button3 ----
			button3.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/netbeans-paste24.gif")));
			enabledToolBar.add(button3);

			//---- button4 ----
			button4.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/netbeans-undo24.gif")));
			enabledToolBar.add(button4);

			//---- button5 ----
			button5.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/netbeans-redo24.gif")));
			enabledToolBar.add(button5);

			//---- button6 ----
			button6.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/netbeans-find24.gif")));
			enabledToolBar.add(button6);
			enabledToolBar.add(button7);
			enabledToolBar.add(button8);
			enabledToolBar.add(button9);
			enabledToolBar.add(button10);
			enabledToolBar.add(button11);
			enabledToolBar.add(button12);
			enabledToolBar.add(button13);
			enabledToolBar.add(button14);
			enabledToolBar.add(button15);
			enabledToolBar.add(button16);
			enabledToolBar.add(button17);
			enabledToolBar.add(button18);
		}
		add(enabledToolBar, "cell 1 0");

		//---- currentLabel ----
		currentLabel.setText("current LaF");
		add(currentLabel, "cell 0 1");
		add(currentLafToolBar, "cell 1 1");

		//---- basicLafLabel ----
		basicLafLabel.setText("Basic LaF");
		add(basicLafLabel, "cell 0 2");
		add(basicLafToolBar, "cell 1 2");

		//======== panel2 ========
		{
			panel2.setLayout(new MigLayout(
				"insets 0,hidemode 3,gap 0 0",
				// columns
				"[60,fill]" +
				"[fill]" +
				"[25,right]rel" +
				"[fill]",
				// rows
				"[center]" +
				"[center]"));

			//---- basicLafBrighterCheckBox ----
			basicLafBrighterCheckBox.setText("brighter");
			basicLafBrighterCheckBox.addActionListener(e -> basicLafChanged());
			panel2.add(basicLafBrighterCheckBox, "cell 0 0 2 1,alignx left,growx 0");

			//---- basicLafPercentLabel ----
			basicLafPercentLabel.setText("Percent");
			panel2.add(basicLafPercentLabel, "cell 0 1");

			//---- basicLafPercentSlider ----
			basicLafPercentSlider.setToolTipText("Percent");
			basicLafPercentSlider.setValue(0);
			basicLafPercentSlider.addChangeListener(e -> basicLafChanged());
			panel2.add(basicLafPercentSlider, "cell 1 1");

			//---- basicLafPercentValue ----
			basicLafPercentValue.setText("000");
			panel2.add(basicLafPercentValue, "cell 2 1");

			//---- basicLafResetButton ----
			basicLafResetButton.setText("Reset");
			basicLafResetButton.addActionListener(e -> basicLafReset());
			panel2.add(basicLafResetButton, "cell 3 0 1 2");
		}
		add(panel2, "cell 2 2");

		//---- metalLafLabel ----
		metalLafLabel.setText("Metal LaF");
		add(metalLafLabel, "cell 0 3");
		add(metalLafToolBar, "cell 1 3");

		//======== panel4 ========
		{
			panel4.setLayout(new MigLayout(
				"insets 0,hidemode 3,gap 0 0",
				// columns
				"[60,fill]" +
				"[fill]" +
				"[25,right]rel" +
				"[fill]",
				// rows
				"[center]" +
				"[center]"));

			//---- metalLafMinLabel ----
			metalLafMinLabel.setText("Min");
			panel4.add(metalLafMinLabel, "cell 0 0");

			//---- metalLafMinSlider ----
			metalLafMinSlider.setMaximum(255);
			metalLafMinSlider.addChangeListener(e -> metalLafChanged());
			panel4.add(metalLafMinSlider, "cell 1 0");

			//---- metalLafMinValue ----
			metalLafMinValue.setText("000");
			panel4.add(metalLafMinValue, "cell 2 0");

			//---- metalLafResetButton ----
			metalLafResetButton.setText("Reset");
			metalLafResetButton.addActionListener(e -> metalLafReset());
			panel4.add(metalLafResetButton, "cell 3 0 1 2");

			//---- metalLafMaxLabel ----
			metalLafMaxLabel.setText("Max");
			panel4.add(metalLafMaxLabel, "cell 0 1");

			//---- metalLafMaxSlider ----
			metalLafMaxSlider.setMaximum(255);
			metalLafMaxSlider.addChangeListener(e -> metalLafChanged());
			panel4.add(metalLafMaxSlider, "cell 1 1");

			//---- metalLafMaxValue ----
			metalLafMaxValue.setText("000");
			panel4.add(metalLafMaxValue, "cell 2 1");
		}
		add(panel4, "cell 2 3");

		//---- plasticLabel ----
		plasticLabel.setText("Plastic LaF");
		add(plasticLabel, "cell 0 4");
		add(plasticToolBar, "cell 1 4");

		//---- intellijTextLabel ----
		intellijTextLabel.setText("IntelliJ text");
		add(intellijTextLabel, "cell 0 5");
		add(intellijTextToolBar, "cell 1 5");

		//---- intelliJTextFilterController ----
		intelliJTextFilterController.addPropertyChangeListener("filter", e -> intelliJTextFilterChanged(e));
		add(intelliJTextFilterController, "cell 2 5");

		//---- intellijLightLabel ----
		intellijLightLabel.setText("IntelliJ light");
		add(intellijLightLabel, "cell 0 6");
		add(intellijLightToolBar, "cell 1 6");

		//---- intelliJLightFilterController ----
		intelliJLightFilterController.addPropertyChangeListener("filter", e -> intelliJLightFilterChanged(e));
		add(intelliJLightFilterController, "cell 2 6");

		//---- intellijDarkLabel ----
		intellijDarkLabel.setText("IntelliJ dark");
		add(intellijDarkLabel, "cell 0 7");
		add(intellijDarkToolBar, "cell 1 7");

		//---- intelliJDarkFilterController ----
		intelliJDarkFilterController.addPropertyChangeListener("filter", e -> intelliJDarkFilterChanged(e));
		add(intelliJDarkFilterController, "cell 2 7");

		//---- netbeansLabel ----
		netbeansLabel.setText("NetBeans");
		add(netbeansLabel, "cell 0 8");
		add(netbeansToolBar, "cell 1 8");

		//======== zipToolBar ========
		{

			//---- zipButton ----
			zipButton.setIcon(new ImageIcon(getClass().getResource("/com/formdev/flatlaf/testing/disabled_icons_test/zip128.png")));
			zipToolBar.add(zipButton);
		}
		add(zipToolBar, "cell 1 9 3 1");

		//---- selectedCheckBox ----
		selectedCheckBox.setText("selected");
		selectedCheckBox.addActionListener(e -> selectedChanged());
		add(selectedCheckBox, "cell 0 10");

		//---- paletteIconsCheckBox ----
		paletteIconsCheckBox.setText("palette icons");
		paletteIconsCheckBox.addActionListener(e -> paletteIconsChanged());
		add(paletteIconsCheckBox, "cell 1 10,alignx left,growx 0");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents

		button7.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-cut.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-cut_dark.png" ) );
		button8.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste_dark.png" ) );
		button9.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-show.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-show_dark.png" ) );
		button10.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess_dark.png" ) );
		button11.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showWriteAccess.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showWriteAccess_dark.png" ) );
		button12.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-search.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-search_dark.png" ) );

		button13.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-cut@2x.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-cut_dark@2x.png" ) );
		button14.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste@2x.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-menu-paste_dark@2x.png" ) );
		button15.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-show@2x.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-show_dark@2x.png" ) );
		button16.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess@2x.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showReadAccess_dark@2x.png" ) );
		button17.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showWriteAccess@2x.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-showWriteAccess_dark@2x.png" ) );
		button18.setIcon( new LightOrDarkIcon(
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-search@2x.png",
			"/com/formdev/flatlaf/testing/disabled_icons_test/intellij-search_dark@2x.png" ) );
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JToolBar enabledToolBar;
	private JToolBar currentLafToolBar;
	private JToolBar basicLafToolBar;
	private JCheckBox basicLafBrighterCheckBox;
	private JSlider basicLafPercentSlider;
	private JLabel basicLafPercentValue;
	private JToolBar metalLafToolBar;
	private JSlider metalLafMinSlider;
	private JLabel metalLafMinValue;
	private JSlider metalLafMaxSlider;
	private JLabel metalLafMaxValue;
	private JToolBar plasticToolBar;
	private JToolBar intellijTextToolBar;
	private FlatDisabledIconsTest.IntelliJFilterController intelliJTextFilterController;
	private JToolBar intellijLightToolBar;
	private FlatDisabledIconsTest.IntelliJFilterController intelliJLightFilterController;
	private JToolBar intellijDarkToolBar;
	private FlatDisabledIconsTest.IntelliJFilterController intelliJDarkFilterController;
	private JToolBar netbeansToolBar;
	private JToolBar zipToolBar;
	private JToggleButton zipButton;
	private JCheckBox selectedCheckBox;
	private JCheckBox paletteIconsCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class LightOrDarkIcon ----------------------------------------------

	private static class LightOrDarkIcon
		extends ImageIcon
	{
		private final ImageIcon lightIcon;
		private final ImageIcon darkIcon;

		LightOrDarkIcon( String lightIconName, String darkIconName ) {
			this.lightIcon = loadIcon( lightIconName );
			this.darkIcon = loadIcon( darkIconName );
		}

		private static ImageIcon loadIcon( String iconName ) {
			ImageIcon icon = new ImageIcon( LightOrDarkIcon.class.getResource( iconName ) );

			if( SystemInfo.isMacOS || !MultiResolutionImageSupport.isAvailable() || !iconName.endsWith( ".png" ) )
				return icon;

			String iconName2x = iconName.replace( ".png", "@2x.png" );
			URL url2x = LightOrDarkIcon.class.getResource( iconName2x );
			if( url2x == null )
				return icon;

			ImageIcon icon2x = new ImageIcon( url2x );
			return new ImageIcon( MultiResolutionImageSupport.create( 0, icon.getImage(), icon2x.getImage() ) );
		}

		private ImageIcon getCurrentIcon() {
			return FlatLaf.isLafDark() ? darkIcon : lightIcon;
		}

		@Override
		public int getIconWidth() {
			return getCurrentIcon().getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return getCurrentIcon().getIconHeight();
		}

		@Override
		public synchronized void paintIcon( Component c, Graphics g, int x, int y ) {
			getCurrentIcon().paintIcon( c, g, x, y );
		}

		@Override
		public Image getImage() {
			return getCurrentIcon().getImage();
		}
	}

	//---- class ColorIcon ----------------------------------------------------

	private static class ColorIcon
		extends ImageIcon
	{
		ColorIcon( Color color ) {
			super( createColorImage( color ) );
		}

		private static Image createColorImage( Color color ) {
			if( color == null )
				color = Color.black;

			BufferedImage image = new BufferedImage( UIScale.scale( 16 ), UIScale.scale( 16 ), BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = image.createGraphics();
			try {
				g.setColor( color );
				g.fillRect( UIScale.scale( 1 ), UIScale.scale( 2 ), UIScale.scale( 14 ), UIScale.scale( 12 ) );
			} finally {
				g.dispose();
			}
			return image;
		}
	}

	//---- class IntelliJFilterController  ------------------------------------

	private static class IntelliJFilterController
		extends JPanel
	{
		int defaultBrightness;
		int defaultContrast;

		private IntelliJFilterController() {
			initComponents();
		}

		private void changed() {
			int brightness = intellijBrightnessSlider.getValue();
			int contrast = intellijContrastSlider.getValue();

			intellijBrightnessValue.setText( String.valueOf( brightness ) );
			intellijContrastValue.setText( String.valueOf( contrast ) );

			RGBImageFilter filter = new IntelliJGrayFilter( brightness, contrast, 100 );
			firePropertyChange( "filter", null, filter );
		}

		private void reset() {
			intellijBrightnessSlider.setValue( defaultBrightness );
			intellijContrastSlider.setValue( defaultContrast );
			changed();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			JLabel intellijBrightnessLabel = new JLabel();
			intellijBrightnessSlider = new JSlider();
			intellijBrightnessValue = new JLabel();
			JLabel intellijContrastLabel = new JLabel();
			intellijContrastSlider = new JSlider();
			intellijContrastValue = new JLabel();
			JButton intellijLightResetButton = new JButton();

			//======== this ========
			setLayout(new MigLayout(
				"insets 0,hidemode 3,gap 0 0",
				// columns
				"[60,fill]" +
				"[fill]" +
				"[25,right]rel" +
				"[fill]",
				// rows
				"[center]" +
				"[center]"));

			//---- intellijBrightnessLabel ----
			intellijBrightnessLabel.setText("Brightness");
			add(intellijBrightnessLabel, "cell 0 0");

			//---- intellijBrightnessSlider ----
			intellijBrightnessSlider.setMinimum(-100);
			intellijBrightnessSlider.addChangeListener(e -> changed());
			add(intellijBrightnessSlider, "cell 1 0");

			//---- intellijBrightnessValue ----
			intellijBrightnessValue.setText("000");
			add(intellijBrightnessValue, "cell 2 0");

			//---- intellijContrastLabel ----
			intellijContrastLabel.setText("Contrast");
			add(intellijContrastLabel, "cell 0 1");

			//---- intellijContrastSlider ----
			intellijContrastSlider.setMinimum(-100);
			intellijContrastSlider.addChangeListener(e -> changed());
			add(intellijContrastSlider, "cell 1 1");

			//---- intellijContrastValue ----
			intellijContrastValue.setText("-000");
			add(intellijContrastValue, "cell 2 1");

			//---- intellijLightResetButton ----
			intellijLightResetButton.setText("Reset");
			intellijLightResetButton.addActionListener(e -> reset());
			add(intellijLightResetButton, "cell 3 0 1 2");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		private JSlider intellijBrightnessSlider;
		private JLabel intellijBrightnessValue;
		private JSlider intellijContrastSlider;
		private JLabel intellijContrastValue;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}

	//---- class FilterButton -------------------------------------------------

	private static class FilterButton
		extends JToggleButton
	{
		private RGBImageFilter filter;

		FilterButton( RGBImageFilter filter, Icon icon ) {
			this.filter = filter;

			setEnabled( false );
			setIcon( icon );
		}

		@Override
		public void setIcon( Icon defaultIcon ) {
			super.setIcon( defaultIcon );

			if( filter != null )
				updateDisabledIcon();
		}

		void setFilter( RGBImageFilter filter ) {
			this.filter = filter;
			updateDisabledIcon();
		}

		@Override
		public void updateUI() {
			super.updateUI();
			updateDisabledIcon();
		}

		private void updateDisabledIcon() {
			setDisabledIcon( createDisabledIcon( getIcon() ) );
		}

		protected Icon createDisabledIcon( Icon icon ) {
			if( !(icon instanceof ImageIcon) )
				return null;

			Image image = ((ImageIcon) icon).getImage();
			ImageProducer producer = new FilteredImageSource( image.getSource(), filter );
			Image disabledImage = Toolkit.getDefaultToolkit().createImage( producer );
			return new ImageIcon( disabledImage );
		}
	}

	//---- class PlasticRGBGrayFilter -----------------------------------------

	// from    https://github.com/openjdk/jdk/blob/6bab0f539fba8fb441697846347597b4a0ade428/src/java.desktop/share/classes/javax/swing/plaf/metal/MetalUtils.java#L415-L434
	// license https://github.com/openjdk/jdk/blob/master/LICENSE
	private static class MetalDisabledButtonImageFilter
		extends RGBImageFilter
	{
		private final float min;
		private final float factor;

		MetalDisabledButtonImageFilter(int min, int max) {
			canFilterIndexColorModel = true;
			this.min = min;
			this.factor = (max - min) / 255f;
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// Coefficients are from the sRGB color space:
			int gray = Math.min(255, (int)(((0.2125f * ((rgb >> 16) & 0xFF)) +
				(0.7154f * ((rgb >> 8) & 0xFF)) +
				(0.0721f * (rgb & 0xFF)) + .5f) * factor + min));

			return (rgb & 0xff000000) | (gray << 16) | (gray << 8) |
				(gray << 0);
		}
	}

	//---- class PlasticRGBGrayFilter -----------------------------------------

	// from    https://github.com/JFormDesigner/swing-jgoodies-looks/blob/master/src/main/java/com/jgoodies/looks/common/RGBGrayFilter.java
	// license https://github.com/JFormDesigner/swing-jgoodies-looks/blob/master/LICENSE.txt
	private static final class PlasticRGBGrayFilter
		extends RGBImageFilter
	{
		private PlasticRGBGrayFilter() {
			canFilterIndexColorModel = true;
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// Find the average of red, green, and blue.
			float avg = (((rgb >> 16) & 0xff) / 255f +
				((rgb >>  8) & 0xff) / 255f +
				(rgb        & 0xff) / 255f) / 3;
			// Pull out the alpha channel.
			float alpha = (((rgb >> 24) & 0xff) / 255f);

			// Calculate the average.
			// Sun's formula: Math.min(1.0f, (1f - avg) / (100.0f / 35.0f) + avg);
			// The following formula uses fewer operations and hence is faster.
			avg = Math.min(1.0f, 0.35f + 0.65f * avg);
			// Convert back into RGB.
			return (int) (alpha * 255f) << 24 |
				(int) (avg   * 255f) << 16 |
				(int) (avg   * 255f) << 8  |
				(int) (avg   * 255f);
		}
	}

	//---- class IntelliJGrayFilter -------------------------------------------

	// from    https://github.com/JetBrains/intellij-community/blob/3840eab54746f5c4f301bb3ac78f00a980b5fd6e/platform/util/ui/src/com/intellij/util/ui/UIUtil.java#L253-L347
	// license https://github.com/JetBrains/intellij-community/blob/master/LICENSE.txt
	private static class IntelliJGrayFilter
		extends RGBImageFilter
	{
		private float brightness;
		private float contrast;
		private int alpha;

		private int origContrast;
		private int origBrightness;

		/**
		 * @param brightness in range [-100..100] where 0 has no effect
		 * @param contrast in range [-100..100] where 0 has no effect
		 * @param alpha in range [0..100] where 0 is transparent, 100 has no effect
		 */
		public IntelliJGrayFilter(int brightness, int contrast, int alpha) {
			setBrightness(brightness);
			setContrast(contrast);
			setAlpha(alpha);
		}

		private void setBrightness(int brightness) {
			origBrightness = Math.max(-100, Math.min(100, brightness));
			this.brightness = (float)(Math.pow(origBrightness, 3) / (100f * 100f)); // cubic in [0..100]
		}

		private void setContrast(int contrast) {
			origContrast = Math.max(-100, Math.min(100, contrast));
			this.contrast = origContrast / 100f;
		}

		private void setAlpha(int alpha) {
			this.alpha = Math.max(0, Math.min(100, alpha));
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// Use NTSC conversion formula.
			int gray = (int)(0.30 * (rgb >> 16 & 0xff) +
				0.59 * (rgb >> 8 & 0xff) +
				0.11 * (rgb & 0xff));

			if (brightness >= 0) {
				gray = (int)((gray + brightness * 255) / (1 + brightness));
			}
			else {
				gray = (int)(gray / (1 - brightness));
			}

			if (contrast >= 0) {
				if (gray >= 127) {
					gray = (int)(gray + (255 - gray) * contrast);
				}
				else {
					gray = (int)(gray - gray * contrast);
				}
			}
			else {
				gray = (int)(127 + (gray - 127) * (contrast + 1));
			}

			int a = ((rgb >> 24) & 0xff) * alpha / 100;

			return (a << 24) | (gray << 16) | (gray << 8) | gray;
		}
	}

	//---- NetBeansDisabledButtonFilter ---------------------------------------

	// from    https://github.com/apache/netbeans/blob/166e2bb491c29f6778223c6e9e16f70664252bce/platform/openide.util.ui/src/org/openide/util/ImageUtilities.java#L1202-L1221
	// license https://github.com/apache/netbeans/blob/master/LICENSE
	private static class NetBeansDisabledButtonFilter
		extends RGBImageFilter
	{
		NetBeansDisabledButtonFilter() {
			canFilterIndexColorModel = true;
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			// Reduce the color bandwidth in quarter (>> 2) and Shift 0x88.
			return (rgb & 0xff000000) + 0x888888 + ((((rgb >> 16) & 0xff) >> 2) << 16) + ((((rgb >> 8) & 0xff) >> 2) << 8) + ((rgb & 0xff) >> 2);
		}
	}
}
