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

package com.formdev.flatlaf.themeeditor;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatThemePreviewFonts
	extends JPanel
{
	private static final DecimalFormat SCALE_FORMAT = new DecimalFormat( "0.##x" );

	public FlatThemePreviewFonts() {
		initComponents();

		scaleValueLabel.setText( SCALE_FORMAT.format( UIScale.getUserScaleFactor() ) );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel headingsLabel = new JLabel();
		FlatThemePreviewFonts.FontPreview h00Preview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview h0Preview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview h1Preview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview h2Preview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview h3Preview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview h4Preview = new FlatThemePreviewFonts.FontPreview();
		JLabel textLabel = new JLabel();
		FlatThemePreviewFonts.FontPreview largePreview = new FlatThemePreviewFonts.FontPreview();
		defaultPreview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview mediumPreview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview smallPreview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview miniPreview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview lightPreview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview semiboldPreview = new FlatThemePreviewFonts.FontPreview();
		FlatThemePreviewFonts.FontPreview monospacedPreview = new FlatThemePreviewFonts.FontPreview();
		JLabel scaleLabel = new JLabel();
		scaleValueLabel = new JLabel();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[left]unrel",
			// rows
			"[]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]para" +
			"[]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]" +
			"[bottom]para" +
			"[]" +
			"[]para" +
			"[]para" +
			"[]"));

		//---- headingsLabel ----
		headingsLabel.setText("Headings");
		headingsLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(headingsLabel, "cell 0 0");

		//---- h00Preview ----
		h00Preview.setFontType("H00");
		h00Preview.setFontStyle("h00");
		add(h00Preview, "cell 0 1,gapx 12");

		//---- h0Preview ----
		h0Preview.setFontType("H0");
		h0Preview.setFontStyle("h0");
		add(h0Preview, "cell 0 2,gapx 12");

		//---- h1Preview ----
		h1Preview.setFontType("H1");
		h1Preview.setFontStyle("h1");
		h1Preview.setFontStyleRegular("h1.regular");
		add(h1Preview, "cell 0 3,gapx 12");

		//---- h2Preview ----
		h2Preview.setFontType("H2");
		h2Preview.setFontStyle("h2");
		h2Preview.setFontStyleRegular("h2.regular");
		add(h2Preview, "cell 0 4,gapx 12");

		//---- h3Preview ----
		h3Preview.setFontType("H3");
		h3Preview.setFontStyle("h3");
		h3Preview.setFontStyleRegular("h3.regular");
		add(h3Preview, "cell 0 5,gapx 12");

		//---- h4Preview ----
		h4Preview.setFontType("H4");
		h4Preview.setFontStyle("h4");
		add(h4Preview, "cell 0 6,gapx 12");

		//---- textLabel ----
		textLabel.setText("Text");
		textLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");
		add(textLabel, "cell 0 7");

		//---- largePreview ----
		largePreview.setFontType("Large");
		largePreview.setFontStyle("large");
		add(largePreview, "cell 0 8,gapx 12");

		//---- defaultPreview ----
		defaultPreview.setFontType("Default");
		add(defaultPreview, "cell 0 9,gapx 12");

		//---- mediumPreview ----
		mediumPreview.setFontType("Medium");
		mediumPreview.setFontStyle("medium");
		add(mediumPreview, "cell 0 10,gapx 12");

		//---- smallPreview ----
		smallPreview.setFontType("Small");
		smallPreview.setFontStyle("small");
		add(smallPreview, "cell 0 11,gapx 12");

		//---- miniPreview ----
		miniPreview.setFontType("Mini");
		miniPreview.setFontStyle("mini");
		add(miniPreview, "cell 0 12,gapx 12");

		//---- lightPreview ----
		lightPreview.setFontType("Light");
		lightPreview.setFontStyle("light");
		add(lightPreview, "cell 0 13,gapx 12");

		//---- semiboldPreview ----
		semiboldPreview.setFontType("Semibold");
		semiboldPreview.setFontStyle("semibold");
		add(semiboldPreview, "cell 0 14,gapx 12");

		//---- monospacedPreview ----
		monospacedPreview.setFontType("Monospaced");
		monospacedPreview.setFontStyle("monospaced");
		add(monospacedPreview, "cell 0 15,gapx 12");

		//---- scaleLabel ----
		scaleLabel.setText("Fonts in preview are scaled by:");
		add(scaleLabel, "cell 0 16,gapx 12");

		//---- scaleValueLabel ----
		scaleValueLabel.setText("1x");
		scaleValueLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");
		add(scaleValueLabel, "cell 0 16");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private FlatThemePreviewFonts.FontPreview defaultPreview;
	private JLabel scaleValueLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class FontPreview --------------------------------------------------

	static class FontPreview
		extends JPanel
	{
		private String fontType;
		private String fontStyle;
		private String fontStyleRegular;

		private FontPreview() {
			initComponents();

			updateDescription( previewLabel.getFont() );
			previewLabel.addPropertyChangeListener( "font", e -> {
				updateDescription( previewLabel.getFont() );
			} );

			preview2Label.setVisible( false );
		}

		public String getFontType() {
			return fontType;
		}

		public void setFontType( String fontType ) {
			this.fontType = fontType;
			previewLabel.setText( fontType );
			preview2Label.setText( " / " + fontType );
		}

		public String getFontStyle() {
			return fontStyle;
		}

		public void setFontStyle( String fontStyle ) {
			this.fontStyle = fontStyle;
			previewLabel.putClientProperty( FlatClientProperties.STYLE_CLASS, fontStyle );
		}

		public String getFontStyleRegular() {
			return fontStyleRegular;
		}

		public void setFontStyleRegular( String fontStyleRegular ) {
			this.fontStyleRegular = fontStyleRegular;
			preview2Label.putClientProperty( FlatClientProperties.STYLE_CLASS, fontStyleRegular );
			preview2Label.setVisible( fontStyleRegular != null );
		}

		private void updateDescription( Font font ) {
			int baseSize = getDefaultFont().getSize();
			int fontSize = font.getSize();

			descLabel.setText( String.format( "%s  %d %s%s  (%+d  %s)",
				font.getFamily(),
				fontSize,
				(font.getStyle() & Font.BOLD) != 0 ? " bold" : "",
				(font.getStyle() & Font.ITALIC) != 0 ? " italic" : "",
				fontSize - baseSize,
				SCALE_FORMAT.format( (float) fontSize / baseSize ) ) );
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
				"[90,left]" +
				"[fill]",
				// rows
				"[]0"));

			//---- previewLabel ----
			previewLabel.setText("preview");
			add(previewLabel, "cell 0 0");

			//---- preview2Label ----
			preview2Label.setText("preview");
			add(preview2Label, "cell 0 0,gapx 0");

			//---- descLabel ----
			descLabel.setText("description");
			descLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "medium");
			add(descLabel, "cell 1 0");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		private JLabel previewLabel;
		private JLabel preview2Label;
		private JLabel descLabel;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
