/*
/*
 * Copyright 2021 FormDev Software GmbH
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatTypographyTest
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatTypographyTest" );
			frame.showFrame( FlatTypographyTest::new );
		} );
	}

	public FlatTypographyTest() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel label54 = new JLabel();
		JLabel label68 = new JLabel();
		JLabel label69 = new JLabel();
		JLabel label10 = new JLabel();
		JLabel label11 = new JLabel();
		JLabel label72 = new JLabel();
		JLabel label28 = new JLabel();
		JLabel label29 = new JLabel();
		JLabel label1 = new JLabel();
		JLabel label37 = new JLabel();
		JLabel label46 = new JLabel();
		FlatTypographyTest.LinkLabel linkLabel9 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel1 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel2 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel3 = new FlatTypographyTest.LinkLabel();
		JLabel label2 = new JLabel();
		FlatTypographyTest.LinkLabel linkLabel4 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel10 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel8 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel11 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel5 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel6 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.LinkLabel linkLabel7 = new FlatTypographyTest.LinkLabel();
		FlatTypographyTest.FontPreview fontPreview69 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview93 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview40 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview35 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview85 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview70 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview36 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview51 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview1 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview11 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview19 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview27 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview41 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview86 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview71 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview37 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview47 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview54 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview62 = new FlatTypographyTest.FontPreview();
		JSeparator separator3 = new JSeparator();
		FlatTypographyTest.FontPreview fontPreview2 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview12 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview20 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview28 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview42 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview87 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview72 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview38 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview48 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview55 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview63 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview3 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview13 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview21 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview29 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview43 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview88 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview73 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview79 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview49 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview57 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview64 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview4 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview14 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview22 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview30 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview89 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview74 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview80 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview50 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview58 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview65 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview5 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview15 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview23 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview31 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview44 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview81 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview56 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview66 = new FlatTypographyTest.FontPreview();
		JSeparator separator1 = new JSeparator();
		FlatTypographyTest.FontPreview fontPreview7 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview6 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview16 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview24 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview32 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview45 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview90 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview98 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview75 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview82 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview95 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview52 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview59 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview67 = new FlatTypographyTest.FontPreview();
		JSeparator separator2 = new JSeparator();
		FlatTypographyTest.FontPreview fontPreview8 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview17 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview25 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview39 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview91 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview76 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview83 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview96 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview9 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview18 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview26 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview33 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview46 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview92 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview77 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview84 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview97 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview53 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview60 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview68 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview10 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview34 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview78 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview94 = new FlatTypographyTest.FontPreview();
		FlatTypographyTest.FontPreview fontPreview61 = new FlatTypographyTest.FontPreview();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[left]unrel" +
			"[left]unrel" +
			"[left]unrel" +
			"[left]unrel" +
			"[left]unrel" +
			"[left]unrel" +
			"[left]unrel" +
			"[fill]unrel" +
			"[left]unrel" +
			"[left]unrel" +
			"[left]unrel",
			// rows
			"[top]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[]" +
			"[bottom]" +
			"[]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]"));

		//---- label54 ----
		label54.setText("<html>FlatLaf<br><small>Windows</small></html>");
		label54.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label54, "cell 0 0");

		//---- label68 ----
		label68.setText("<html>JetBrains<br><small>Windows</small></html>");
		label68.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label68, "cell 1 0");

		//---- label69 ----
		label69.setText("<html>JetBrains<br><small>macOS</small></html>");
		label69.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label69, "cell 2 0");

		//---- label10 ----
		label10.setText("macOS");
		label10.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label10, "cell 3 0");

		//---- label11 ----
		label11.setText("Windows 10/11");
		label11.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label11, "cell 4 0");

		//---- label72 ----
		label72.setText("<html>GitHub<br>Primer</html>");
		label72.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label72, "cell 5 0");

		//---- label28 ----
		label28.setText("Material");
		label28.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label28, "cell 6 0");

		//---- label29 ----
		label29.setText("Material 3");
		label29.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label29, "cell 7 0");

		//---- label1 ----
		label1.setText("SAP Fiori");
		label1.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label1, "cell 8 0");

		//---- label37 ----
		label37.setText("Atlassian");
		label37.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label37, "cell 9 0");

		//---- label46 ----
		label46.setText("Iris");
		label46.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(label46, "cell 10 0");

		//---- linkLabel9 ----
		linkLabel9.setLink("https://www.formdev.com/flatlaf/typography/");
		add(linkLabel9, "cell 0 1");

		//---- linkLabel1 ----
		linkLabel1.setLink("https://jetbrains.design/intellij/principles/typography/");
		add(linkLabel1, "cell 1 1");

		//---- linkLabel2 ----
		linkLabel2.setLink("https://developer.apple.com/design/human-interface-guidelines/macos/visual-design/typography/");
		add(linkLabel2, "cell 3 1");

		//---- linkLabel3 ----
		linkLabel3.setLink("https://docs.microsoft.com/en-us/windows/apps/design/style/typography#type-ramp");
		add(linkLabel3, "cell 4 1");

		//---- label2 ----
		label2.setText("/");
		add(label2, "cell 4 1");

		//---- linkLabel4 ----
		linkLabel4.setLink("https://docs.microsoft.com/en-us/windows/apps/design/signature-experiences/typography#type-ramp");
		add(linkLabel4, "cell 4 1");

		//---- linkLabel10 ----
		linkLabel10.setLink("https://primer.style/css/utilities/typography");
		add(linkLabel10, "cell 5 1");

		//---- linkLabel8 ----
		linkLabel8.setLink("https://material.io/design/typography/the-type-system.html#type-scale");
		add(linkLabel8, "cell 6 1");

		//---- linkLabel11 ----
		linkLabel11.setLink("https://m3.material.io/styles/typography/tokens");
		add(linkLabel11, "cell 7 1");

		//---- linkLabel5 ----
		linkLabel5.setLink("https://experience.sap.com/fiori-design-web/typography/#headlines-and-font-styles-for-ui-controls");
		add(linkLabel5, "cell 8 1");

		//---- linkLabel6 ----
		linkLabel6.setLink("https://atlassian.design/foundations/typography");
		add(linkLabel6, "cell 9 1");

		//---- linkLabel7 ----
		linkLabel7.setLink("https://iris.alkamitech.com/foundations/typography.html");
		add(linkLabel7, "cell 10 1");

		//---- fontPreview69 ----
		fontPreview69.setFontType("H1");
		fontPreview69.setFontSize(96);
		fontPreview69.setBaseSize(16);
		fontPreview69.setLight(true);
		add(fontPreview69, "cell 6 2");

		//---- fontPreview93 ----
		fontPreview93.setBaseSize(12);
		fontPreview93.setFontType("H00");
		fontPreview93.setFontSize(36);
		add(fontPreview93, "cell 0 3");

		//---- fontPreview40 ----
		fontPreview40.setFontSize(68);
		fontPreview40.setFontType("Disp");
		fontPreview40.setBaseSize(14);
		fontPreview40.setSemibold(true);
		add(fontPreview40, "cell 4 2 1 2");

		//---- fontPreview35 ----
		fontPreview35.setFontType("Disp L");
		fontPreview35.setFontSize(57);
		fontPreview35.setBaseSize(16);
		add(fontPreview35, "cell 7 2");

		//---- fontPreview85 ----
		fontPreview85.setFontSize(48);
		fontPreview85.setFontType("H00");
		fontPreview85.setBaseSize(16);
		fontPreview85.setSemibold(true);
		add(fontPreview85, "cell 5 3");

		//---- fontPreview70 ----
		fontPreview70.setFontSize(60);
		fontPreview70.setFontType("H2");
		fontPreview70.setBaseSize(16);
		fontPreview70.setLight(true);
		add(fontPreview70, "cell 6 3");

		//---- fontPreview36 ----
		fontPreview36.setFontSize(45);
		fontPreview36.setFontType("Disp M");
		fontPreview36.setBaseSize(16);
		add(fontPreview36, "cell 7 3");

		//---- fontPreview51 ----
		fontPreview51.setFontType("h900");
		fontPreview51.setFontSize(35);
		fontPreview51.setBaseSize(14);
		fontPreview51.setSemibold(true);
		add(fontPreview51, "cell 9 3");

		//---- fontPreview1 ----
		fontPreview1.setBaseSize(12);
		fontPreview1.setFontType("H0");
		fontPreview1.setFontSize(30);
		add(fontPreview1, "cell 0 4");

		//---- fontPreview11 ----
		fontPreview11.setFontType("H0");
		fontPreview11.setFontSize(24);
		fontPreview11.setBold(true);
		fontPreview11.setBaseSize(12);
		fontPreview11.setShowPlain(true);
		add(fontPreview11, "cell 1 4");

		//---- fontPreview19 ----
		fontPreview19.setFontType("H0");
		fontPreview19.setFontSize(25);
		fontPreview19.setBold(true);
		fontPreview19.setBaseSize(13);
		fontPreview19.setShowPlain(true);
		add(fontPreview19, "cell 2 4");

		//---- fontPreview27 ----
		fontPreview27.setFontType("Large Title");
		fontPreview27.setFontSize(26);
		fontPreview27.setBaseSize(13);
		add(fontPreview27, "cell 3 4");

		//---- fontPreview41 ----
		fontPreview41.setFontType("Title L");
		fontPreview41.setFontSize(40);
		fontPreview41.setBaseSize(14);
		fontPreview41.setSemibold(true);
		add(fontPreview41, "cell 4 4");

		//---- fontPreview86 ----
		fontPreview86.setFontSize(40);
		fontPreview86.setFontType("H0");
		fontPreview86.setBaseSize(16);
		fontPreview86.setSemibold(true);
		add(fontPreview86, "cell 5 4");

		//---- fontPreview71 ----
		fontPreview71.setFontType("H3");
		fontPreview71.setFontSize(48);
		fontPreview71.setBaseSize(16);
		add(fontPreview71, "cell 6 4");

		//---- fontPreview37 ----
		fontPreview37.setFontSize(36);
		fontPreview37.setFontType("Display S");
		fontPreview37.setBaseSize(16);
		add(fontPreview37, "cell 7 4");

		//---- fontPreview47 ----
		fontPreview47.setFontType("Header 1");
		fontPreview47.setFontSize(36);
		fontPreview47.setBaseSize(14);
		add(fontPreview47, "cell 8 4");

		//---- fontPreview54 ----
		fontPreview54.setBaseSize(14);
		fontPreview54.setFontType("h800");
		fontPreview54.setFontSize(29);
		fontPreview54.setSemibold(true);
		add(fontPreview54, "cell 9 4");

		//---- fontPreview62 ----
		fontPreview62.setBaseSize(16);
		fontPreview62.setFontSize(44);
		fontPreview62.setFontType("Hero");
		add(fontPreview62, "cell 10 4");
		add(separator3, "cell 0 5 11 1,growx");

		//---- fontPreview2 ----
		fontPreview2.setBaseSize(12);
		fontPreview2.setFontSize(24);
		fontPreview2.setFontType("H1");
		fontPreview2.setSemibold(true);
		fontPreview2.setShowPlain(true);
		add(fontPreview2, "cell 0 6");

		//---- fontPreview12 ----
		fontPreview12.setFontType("H1");
		fontPreview12.setFontSize(21);
		fontPreview12.setBold(true);
		fontPreview12.setBaseSize(12);
		fontPreview12.setShowPlain(true);
		add(fontPreview12, "cell 1 6");

		//---- fontPreview20 ----
		fontPreview20.setFontType("H1");
		fontPreview20.setFontSize(22);
		fontPreview20.setBold(true);
		fontPreview20.setBaseSize(13);
		fontPreview20.setShowPlain(true);
		add(fontPreview20, "cell 2 6");

		//---- fontPreview28 ----
		fontPreview28.setFontSize(22);
		fontPreview28.setFontType("Title 1");
		fontPreview28.setBaseSize(13);
		add(fontPreview28, "cell 3 6");

		//---- fontPreview42 ----
		fontPreview42.setFontType("Title");
		fontPreview42.setFontSize(28);
		fontPreview42.setBaseSize(14);
		fontPreview42.setSemibold(true);
		add(fontPreview42, "cell 4 6");

		//---- fontPreview87 ----
		fontPreview87.setFontType("H1");
		fontPreview87.setFontSize(32);
		fontPreview87.setBaseSize(16);
		fontPreview87.setSemibold(true);
		add(fontPreview87, "cell 5 6");

		//---- fontPreview72 ----
		fontPreview72.setFontType("H4");
		fontPreview72.setFontSize(34);
		fontPreview72.setBaseSize(16);
		add(fontPreview72, "cell 6 6");

		//---- fontPreview38 ----
		fontPreview38.setFontSize(32);
		fontPreview38.setFontType("Headline L");
		fontPreview38.setBaseSize(16);
		add(fontPreview38, "cell 7 6");

		//---- fontPreview48 ----
		fontPreview48.setFontType("Header 2");
		fontPreview48.setFontSize(24);
		fontPreview48.setBaseSize(14);
		add(fontPreview48, "cell 8 6");

		//---- fontPreview55 ----
		fontPreview55.setBaseSize(14);
		fontPreview55.setFontType("h700");
		fontPreview55.setFontSize(24);
		fontPreview55.setSemibold(true);
		add(fontPreview55, "cell 9 6");

		//---- fontPreview63 ----
		fontPreview63.setBaseSize(16);
		fontPreview63.setFontSize(32);
		fontPreview63.setFontType("H1");
		add(fontPreview63, "cell 10 6");

		//---- fontPreview3 ----
		fontPreview3.setBaseSize(12);
		fontPreview3.setFontSize(18);
		fontPreview3.setFontType("H2");
		fontPreview3.setSemibold(true);
		fontPreview3.setShowPlain(true);
		add(fontPreview3, "cell 0 7");

		//---- fontPreview13 ----
		fontPreview13.setFontType("H2");
		fontPreview13.setFontSize(17);
		fontPreview13.setBold(true);
		fontPreview13.setBaseSize(12);
		fontPreview13.setShowPlain(true);
		add(fontPreview13, "cell 1 7");

		//---- fontPreview21 ----
		fontPreview21.setFontType("H2");
		fontPreview21.setFontSize(18);
		fontPreview21.setBold(true);
		fontPreview21.setBaseSize(13);
		fontPreview21.setShowPlain(true);
		add(fontPreview21, "cell 2 7");

		//---- fontPreview29 ----
		fontPreview29.setFontSize(17);
		fontPreview29.setFontType("Title 2");
		fontPreview29.setBaseSize(13);
		add(fontPreview29, "cell 3 7");

		//---- fontPreview43 ----
		fontPreview43.setFontType("Subtitle");
		fontPreview43.setFontSize(20);
		fontPreview43.setBaseSize(14);
		fontPreview43.setSemibold(true);
		add(fontPreview43, "cell 4 7");

		//---- fontPreview88 ----
		fontPreview88.setFontSize(24);
		fontPreview88.setFontType("H2");
		fontPreview88.setBaseSize(16);
		fontPreview88.setSemibold(true);
		add(fontPreview88, "cell 5 7");

		//---- fontPreview73 ----
		fontPreview73.setFontType("H5");
		fontPreview73.setFontSize(24);
		fontPreview73.setBaseSize(16);
		add(fontPreview73, "cell 6 7");

		//---- fontPreview79 ----
		fontPreview79.setFontType("Headline M");
		fontPreview79.setFontSize(28);
		fontPreview79.setBaseSize(16);
		add(fontPreview79, "cell 7 7");

		//---- fontPreview49 ----
		fontPreview49.setFontType("Header 3");
		fontPreview49.setFontSize(20);
		fontPreview49.setBaseSize(14);
		add(fontPreview49, "cell 8 7");

		//---- fontPreview57 ----
		fontPreview57.setBaseSize(14);
		fontPreview57.setFontType("h600");
		fontPreview57.setFontSize(20);
		fontPreview57.setSemibold(true);
		add(fontPreview57, "cell 9 7");

		//---- fontPreview64 ----
		fontPreview64.setBaseSize(16);
		fontPreview64.setFontType("H2");
		fontPreview64.setFontSize(24);
		add(fontPreview64, "cell 10 7");

		//---- fontPreview4 ----
		fontPreview4.setBaseSize(12);
		fontPreview4.setFontSize(15);
		fontPreview4.setFontType("H3");
		fontPreview4.setSemibold(true);
		fontPreview4.setShowPlain(true);
		add(fontPreview4, "cell 0 8");

		//---- fontPreview14 ----
		fontPreview14.setFontType("H3");
		fontPreview14.setFontSize(15);
		fontPreview14.setBold(true);
		fontPreview14.setBaseSize(12);
		fontPreview14.setShowPlain(true);
		add(fontPreview14, "cell 1 8");

		//---- fontPreview22 ----
		fontPreview22.setFontType("H3");
		fontPreview22.setFontSize(16);
		fontPreview22.setBold(true);
		fontPreview22.setBaseSize(13);
		fontPreview22.setShowPlain(true);
		add(fontPreview22, "cell 2 8");

		//---- fontPreview30 ----
		fontPreview30.setFontType("Title 3");
		fontPreview30.setFontSize(15);
		fontPreview30.setBaseSize(13);
		add(fontPreview30, "cell 3 8");

		//---- fontPreview89 ----
		fontPreview89.setFontType("H3");
		fontPreview89.setFontSize(20);
		fontPreview89.setBaseSize(16);
		fontPreview89.setSemibold(true);
		add(fontPreview89, "cell 5 8");

		//---- fontPreview74 ----
		fontPreview74.setFontType("H6");
		fontPreview74.setFontSize(20);
		fontPreview74.setBaseSize(16);
		fontPreview74.setSemibold(true);
		add(fontPreview74, "cell 6 8");

		//---- fontPreview80 ----
		fontPreview80.setFontType("Headline S");
		fontPreview80.setFontSize(24);
		fontPreview80.setBaseSize(16);
		add(fontPreview80, "cell 7 8");

		//---- fontPreview50 ----
		fontPreview50.setFontType("Header 4");
		fontPreview50.setFontSize(18);
		fontPreview50.setBaseSize(14);
		add(fontPreview50, "cell 8 8");

		//---- fontPreview58 ----
		fontPreview58.setBaseSize(14);
		fontPreview58.setFontType("h500");
		fontPreview58.setFontSize(16);
		fontPreview58.setSemibold(true);
		add(fontPreview58, "cell 9 8");

		//---- fontPreview65 ----
		fontPreview65.setBaseSize(16);
		fontPreview65.setFontSize(20);
		fontPreview65.setFontType("H3");
		add(fontPreview65, "cell 10 8");

		//---- fontPreview5 ----
		fontPreview5.setBaseSize(12);
		fontPreview5.setFontSize(14);
		fontPreview5.setFontType("Large");
		add(fontPreview5, "cell 0 9");

		//---- fontPreview15 ----
		fontPreview15.setFontType("H4");
		fontPreview15.setBold(true);
		fontPreview15.setFontSize(12);
		fontPreview15.setBaseSize(12);
		add(fontPreview15, "cell 1 9");

		//---- fontPreview23 ----
		fontPreview23.setFontType("H4");
		fontPreview23.setFontSize(13);
		fontPreview23.setBold(true);
		fontPreview23.setBaseSize(13);
		add(fontPreview23, "cell 2 9");

		//---- fontPreview31 ----
		fontPreview31.setFontType("Headline");
		fontPreview31.setFontSize(13);
		fontPreview31.setBold(true);
		fontPreview31.setBaseSize(13);
		add(fontPreview31, "cell 3 9");

		//---- fontPreview44 ----
		fontPreview44.setFontSize(18);
		fontPreview44.setFontType("Body Large");
		fontPreview44.setBaseSize(14);
		add(fontPreview44, "cell 4 9");

		//---- fontPreview81 ----
		fontPreview81.setFontType("Title L");
		fontPreview81.setFontSize(22);
		fontPreview81.setBaseSize(16);
		fontPreview81.setSemibold(true);
		add(fontPreview81, "cell 7 9");

		//---- fontPreview56 ----
		fontPreview56.setFontType("Large Text / Header 5");
		fontPreview56.setFontSize(16);
		fontPreview56.setBaseSize(14);
		add(fontPreview56, "cell 8 9");

		//---- fontPreview66 ----
		fontPreview66.setBaseSize(16);
		fontPreview66.setFontType("H4");
		fontPreview66.setFontSize(18);
		add(fontPreview66, "cell 10 9");
		add(separator1, "cell 0 10 11 1,growx");

		//---- fontPreview7 ----
		fontPreview7.setFontType("Default");
		fontPreview7.setFontSize(12);
		fontPreview7.setBaseSize(12);
		add(fontPreview7, "cell 0 11");

		//---- fontPreview6 ----
		fontPreview6.setFontSize(12);
		fontPreview6.setBold(true);
		fontPreview6.setFontType("H4");
		fontPreview6.setBaseSize(12);
		add(fontPreview6, "cell 0 11");

		//---- fontPreview16 ----
		fontPreview16.setFontType("Default");
		fontPreview16.setFontSize(12);
		fontPreview16.setBaseSize(12);
		add(fontPreview16, "cell 1 11");

		//---- fontPreview24 ----
		fontPreview24.setFontType("Default");
		fontPreview24.setFontSize(13);
		fontPreview24.setBaseSize(13);
		add(fontPreview24, "cell 2 11");

		//---- fontPreview32 ----
		fontPreview32.setFontType("Body");
		fontPreview32.setFontSize(13);
		fontPreview32.setBaseSize(13);
		add(fontPreview32, "cell 3 11");

		//---- fontPreview45 ----
		fontPreview45.setFontType("Body");
		fontPreview45.setFontSize(14);
		fontPreview45.setBaseSize(14);
		add(fontPreview45, "cell 4 11");

		//---- fontPreview90 ----
		fontPreview90.setFontSize(16);
		fontPreview90.setFontType("Body /");
		fontPreview90.setBaseSize(16);
		add(fontPreview90, "cell 5 11,alignx left,growx 0");

		//---- fontPreview98 ----
		fontPreview98.setFontSize(16);
		fontPreview98.setFontType("H4");
		fontPreview98.setBaseSize(16);
		fontPreview98.setSemibold(true);
		add(fontPreview98, "cell 5 11");

		//---- fontPreview75 ----
		fontPreview75.setFontSize(16);
		fontPreview75.setFontType("Body 1 / Subtitle 1");
		fontPreview75.setBaseSize(16);
		add(fontPreview75, "cell 6 11");

		//---- fontPreview82 ----
		fontPreview82.setFontSize(16);
		fontPreview82.setFontType("Body L /");
		fontPreview82.setBaseSize(16);
		add(fontPreview82, "cell 7 11,alignx left,growx 0");

		//---- fontPreview95 ----
		fontPreview95.setFontSize(16);
		fontPreview95.setFontType("Title M");
		fontPreview95.setBaseSize(16);
		fontPreview95.setSemibold(true);
		add(fontPreview95, "cell 7 11");

		//---- fontPreview52 ----
		fontPreview52.setFontType("Medium Text / Header 6");
		fontPreview52.setFontSize(14);
		fontPreview52.setBaseSize(14);
		add(fontPreview52, "cell 8 11");

		//---- fontPreview59 ----
		fontPreview59.setBaseSize(14);
		fontPreview59.setFontSize(14);
		fontPreview59.setFontType("h400");
		fontPreview59.setSemibold(true);
		add(fontPreview59, "cell 9 11");

		//---- fontPreview67 ----
		fontPreview67.setBaseSize(16);
		fontPreview67.setFontSize(16);
		fontPreview67.setFontType("Body");
		add(fontPreview67, "cell 10 11");
		add(separator2, "cell 0 12 11 1,growx");

		//---- fontPreview8 ----
		fontPreview8.setFontType("Medium");
		fontPreview8.setFontSize(11);
		fontPreview8.setBaseSize(12);
		add(fontPreview8, "cell 0 13");

		//---- fontPreview17 ----
		fontPreview17.setFontType("Medium");
		fontPreview17.setFontSize(12);
		fontPreview17.setBaseSize(12);
		add(fontPreview17, "cell 1 13");

		//---- fontPreview25 ----
		fontPreview25.setFontType("Medium");
		fontPreview25.setFontSize(12);
		fontPreview25.setBaseSize(13);
		add(fontPreview25, "cell 2 13");

		//---- fontPreview39 ----
		fontPreview39.setFontSize(12);
		fontPreview39.setFontType("Callout");
		fontPreview39.setBaseSize(13);
		add(fontPreview39, "cell 3 13");

		//---- fontPreview91 ----
		fontPreview91.setFontType("H5");
		fontPreview91.setFontSize(14);
		fontPreview91.setBaseSize(16);
		fontPreview91.setSemibold(true);
		add(fontPreview91, "cell 5 13");

		//---- fontPreview76 ----
		fontPreview76.setFontType("Body 2 / Subtitle 2");
		fontPreview76.setFontSize(14);
		fontPreview76.setBaseSize(16);
		add(fontPreview76, "cell 6 13");

		//---- fontPreview83 ----
		fontPreview83.setFontType("Body M /");
		fontPreview83.setFontSize(14);
		fontPreview83.setBaseSize(16);
		add(fontPreview83, "cell 7 13,alignx left,growx 0");

		//---- fontPreview96 ----
		fontPreview96.setFontType("Title S / Label L");
		fontPreview96.setFontSize(14);
		fontPreview96.setBaseSize(16);
		fontPreview96.setSemibold(true);
		add(fontPreview96, "cell 7 13");

		//---- fontPreview9 ----
		fontPreview9.setFontType("Small");
		fontPreview9.setFontSize(10);
		fontPreview9.setBaseSize(12);
		add(fontPreview9, "cell 0 14");

		//---- fontPreview18 ----
		fontPreview18.setFontType("Small");
		fontPreview18.setFontSize(12);
		fontPreview18.setBaseSize(12);
		add(fontPreview18, "cell 1 14");

		//---- fontPreview26 ----
		fontPreview26.setFontType("Small");
		fontPreview26.setFontSize(11);
		fontPreview26.setBaseSize(13);
		add(fontPreview26, "cell 2 14");

		//---- fontPreview33 ----
		fontPreview33.setFontType("Subheadline");
		fontPreview33.setFontSize(11);
		fontPreview33.setBaseSize(13);
		add(fontPreview33, "cell 3 14");

		//---- fontPreview46 ----
		fontPreview46.setFontType("Caption");
		fontPreview46.setFontSize(12);
		fontPreview46.setBaseSize(14);
		add(fontPreview46, "cell 4 14");

		//---- fontPreview92 ----
		fontPreview92.setFontSize(12);
		fontPreview92.setFontType("H6");
		fontPreview92.setBaseSize(12);
		fontPreview92.setSemibold(true);
		add(fontPreview92, "cell 5 14");

		//---- fontPreview77 ----
		fontPreview77.setFontSize(12);
		fontPreview77.setFontType("Caption");
		fontPreview77.setBaseSize(16);
		add(fontPreview77, "cell 6 14");

		//---- fontPreview84 ----
		fontPreview84.setFontType("Body S /");
		fontPreview84.setFontSize(12);
		fontPreview84.setBaseSize(16);
		add(fontPreview84, "cell 7 14,alignx left,growx 0");

		//---- fontPreview97 ----
		fontPreview97.setFontType("Label M");
		fontPreview97.setFontSize(12);
		fontPreview97.setBaseSize(16);
		fontPreview97.setSemibold(true);
		add(fontPreview97, "cell 7 14");

		//---- fontPreview53 ----
		fontPreview53.setFontType("Small Text");
		fontPreview53.setFontSize(12);
		fontPreview53.setBaseSize(14);
		add(fontPreview53, "cell 8 14");

		//---- fontPreview60 ----
		fontPreview60.setBaseSize(14);
		fontPreview60.setFontType("h300 / h200");
		fontPreview60.setFontSize(12);
		fontPreview60.setSemibold(true);
		add(fontPreview60, "cell 9 14");

		//---- fontPreview68 ----
		fontPreview68.setBaseSize(16);
		fontPreview68.setFontType("Small");
		fontPreview68.setFontSize(14);
		add(fontPreview68, "cell 10 14");

		//---- fontPreview10 ----
		fontPreview10.setFontType("Mini");
		fontPreview10.setFontSize(9);
		fontPreview10.setBaseSize(12);
		add(fontPreview10, "cell 0 15");

		//---- fontPreview34 ----
		fontPreview34.setFontSize(10);
		fontPreview34.setFontType("Footnote / Caption 1+2");
		fontPreview34.setBaseSize(13);
		add(fontPreview34, "cell 3 15");

		//---- fontPreview78 ----
		fontPreview78.setFontType("Overline");
		fontPreview78.setFontSize(10);
		fontPreview78.setBaseSize(16);
		add(fontPreview78, "cell 6 15");

		//---- fontPreview94 ----
		fontPreview94.setFontSize(11);
		fontPreview94.setFontType("Label S");
		fontPreview94.setBaseSize(16);
		fontPreview94.setSemibold(true);
		add(fontPreview94, "cell 7 15");

		//---- fontPreview61 ----
		fontPreview61.setBaseSize(14);
		fontPreview61.setFontSize(11);
		fontPreview61.setFontType("h100");
		fontPreview61.setSemibold(true);
		add(fontPreview61, "cell 9 15");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class LinkLabel ----------------------------------------------------

	static class LinkLabel
		extends JLabel
	{
		private String linkText;
		private String link;

		public LinkLabel() {
			setLinkText( "Details" );
			setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
			addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked( MouseEvent e ) {
					try {
						Desktop.getDesktop().browse( new URI( link ) );
					} catch( IOException | URISyntaxException ex ) {
						JOptionPane.showMessageDialog( LinkLabel.this,
							"Failed to open '" + link + "' in browser.",
							"Browse", JOptionPane.PLAIN_MESSAGE );
					}
				}
			} );
		}

		public String getLink() {
			return link;
		}

		public void setLink( String link ) {
			this.link = link;
		}

		public String getLinkText() {
			return linkText;
		}

		public void setLinkText( String linkText ) {
			this.linkText = linkText;
			setText( "<html><a href=\"#\">" + linkText + "</a></html>" );
		}
	}

	//---- class FontPreview --------------------------------------------------

	static class FontPreview
		extends JPanel
	{
		private String fontType;
		private int fontSize;
		private int baseSize;
		private boolean light;
		private boolean semibold;
		private boolean bold;
		private boolean showPlain;

		private FontPreview() {
			initComponents();
			preview2Label.setVisible( false );
		}

		public String getFontType() {
			return fontType;
		}

		public void setFontType( String fontType ) {
			this.fontType = fontType;
			previewLabel.setText( fontType );
			preview2Label.setText( previewLabel.getText() );
		}

		public int getFontSize() {
			return fontSize;
		}

		public void setFontSize( int fontSize ) {
			this.fontSize = fontSize;
			updateFont();
			updateDescription();
		}

		public int getBaseSize() {
			return baseSize;
		}

		public void setBaseSize( int baseSize ) {
			this.baseSize = baseSize;
			updateDescription();
		}

		public boolean isLight() {
			return light;
		}

		public void setLight( boolean light ) {
			this.light = light;
			updateFont();
			updateDescription();
		}

		public boolean isSemibold() {
			return semibold;
		}

		public void setSemibold( boolean semibold ) {
			this.semibold = semibold;
			updateFont();
			updateDescription();
		}

		public boolean isBold() {
			return bold;
		}

		public void setBold( boolean bold ) {
			this.bold = bold;
			updateFont();
			updateDescription();
		}

		public boolean isShowPlain() {
			return showPlain;
		}

		public void setShowPlain( boolean showPlain ) {
			this.showPlain = showPlain;
			preview2Label.setVisible( showPlain );
		}

		private void updateFont() {
			previewLabel.setFont( getBaseFont().deriveFont( bold ? Font.BOLD : Font.PLAIN, fontSize ) );
			preview2Label.setFont( getDefaultFont().deriveFont( (float) fontSize ) );
		}

		private void updateDescription() {
			StringBuilder buf = new StringBuilder();
			buf.append( fontSize );
			if( baseSize > 0 && fontSize != baseSize ) {
				buf.append( "  " ).append( fontSize > baseSize ? "+" : "" ).append( fontSize - baseSize );
				buf.append( String.format( "  %.2fx", (float) fontSize / baseSize ) );
			}
			if( light )
				buf.append( "  light" );
			if( semibold )
				buf.append( "  semibold" );
			if( bold )
				buf.append( "  bold" );
			descLabel.setText( buf.toString() );
		}

		private Font getBaseFont() {
			Font font = null;
			if( light )
				font = UIManager.getFont( "light.font" );
			else if( semibold )
				font = UIManager.getFont( "semibold.font" );

			if( font == null )
				font = getDefaultFont();
			return font;
		}

		private Font getDefaultFont() {
			Font font = UIManager.getFont( "defaultFont" );
			if( font == null )
				font = UIManager.getFont( "Label.font" );
			return font;
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			previewLabel = new JLabel();
			preview2Label = new JLabel();
			descLabel = new JLabel();

			//======== this ========
			setLayout(new MigLayout(
				"insets 0,hidemode 3",
				// columns
				"[left]",
				// rows
				"[]0" +
				"[]"));

			//---- previewLabel ----
			previewLabel.setText("preview");
			add(previewLabel, "cell 0 0");
			add(preview2Label, "cell 0 0");

			//---- descLabel ----
			descLabel.setText("description");
			descLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "small");
			descLabel.setEnabled(false);
			add(descLabel, "cell 0 1");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		private JLabel previewLabel;
		private JLabel preview2Label;
		private JLabel descLabel;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
