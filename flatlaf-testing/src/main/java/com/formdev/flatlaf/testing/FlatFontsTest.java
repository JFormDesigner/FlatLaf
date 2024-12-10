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

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.fonts.inter.FlatInterFont;
import com.formdev.flatlaf.fonts.jetbrains_mono.FlatJetBrainsMonoFont;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.fonts.roboto_mono.FlatRobotoMonoFont;
import com.formdev.flatlaf.util.FontUtils;
import com.formdev.flatlaf.util.UIScale;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatFontsTest
	extends JPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatInterFont.installLazy();
			FlatJetBrainsMonoFont.installLazy();
			FlatRobotoFont.installLazy();
			FlatRobotoMonoFont.installLazy();

			FlatTestFrame frame = FlatTestFrame.create( args, "FlatFontsTest" );
			frame.showFrame( FlatFontsTest::new );
		} );
	}

	FlatFontsTest() {
		initComponents();

		Font[] allFonts = FontUtils.getAllFonts();

		TreeMap<String, TreeMap<String, Font>> familiesMap = new TreeMap<>();
		for( Font font : allFonts ) {
			TreeMap<String, Font> familyFontsMap = familiesMap.computeIfAbsent( font.getFamily(), key -> new TreeMap<>() );
			Font old = familyFontsMap.put( font.getName(), font );
			if( old != null ) {
				System.err.println( "Duplicate font name '" + font.getName() + "'" );
				System.err.println( "  " + old );
				System.err.println( "  " + font );
			}
		}

		DefaultListModel<FontFamilyInfo> model = new DefaultListModel<>();
		for( Map.Entry<String, TreeMap<String, Font>> e : familiesMap.entrySet() ) {
			FontFamilyInfo info = new FontFamilyInfo();
			info.name = e.getKey();
			info.fonts = e.getValue();
			model.addElement( info );
		}
		familiesList.setModel( model );
		familiesList.setCellRenderer( new FontFamilyRenderer() );
		familiesList.setSelectedIndex( 0 );

		SwingUtilities.invokeLater( () -> {
			SwingUtilities.invokeLater( () -> {
				familiesList.requestFocusInWindow();
			} );
		} );
	}

	private void familyChanged() {
		previewFamilyNameLabel.setText( "" );
		previewPanel.removeAll();

		FontFamilyInfo info = familiesList.getSelectedValue();
		if( info == null )
			return;

		previewFamilyNameLabel.setText( info.name );

		for( Map.Entry<String, Font> e : info.fonts.entrySet() ) {
			JLabel label = new JLabel( e.getKey() );
			label.setFont( e.getValue().deriveFont( (float) UIScale.scale( 36 ) ) );
			label.setToolTipText( e.getValue().toString() );
			previewPanel.add( label, "wrap" );
		}

		previewPanel.revalidate();
		previewPanel.repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel familiesLabel = new JLabel();
		previewFamilyNameLabel = new JLabel();
		JScrollPane familiesScrollPane = new JScrollPane();
		familiesList = new JList<>();
		JSeparator separator1 = new JSeparator();
		JScrollPane scrollPane1 = new JScrollPane();
		previewPanel = new JPanel();

		//======== this ========
		setBorder(null);
		setLayout(new MigLayout(
			"insets dialog,hidemode 3",
			// columns
			"[230:230,fill]para" +
			"[grow,fill]",
			// rows
			"[top]" +
			"[]" +
			"[]" +
			"[800,grow,fill]"));

		//---- familiesLabel ----
		familiesLabel.setText("Families:");
		add(familiesLabel, "cell 0 0");

		//---- previewFamilyNameLabel ----
		previewFamilyNameLabel.setText("name");
		previewFamilyNameLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
		add(previewFamilyNameLabel, "cell 1 1");

		//======== familiesScrollPane ========
		{

			//---- familiesList ----
			familiesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			familiesList.addListSelectionListener(e -> familyChanged());
			familiesScrollPane.setViewportView(familiesList);
		}
		add(familiesScrollPane, "cell 0 1 1 3,growy");
		add(separator1, "cell 1 2");

		//======== scrollPane1 ========
		{
			scrollPane1.setBorder(BorderFactory.createEmptyBorder());

			//======== previewPanel ========
			{
				previewPanel.setLayout(new MigLayout(
					"insets dialog,hidemode 3",
					// columns
					"[fill]",
					// rows
					"[]"));
			}
			scrollPane1.setViewportView(previewPanel);
		}
		add(scrollPane1, "cell 1 3");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JLabel previewFamilyNameLabel;
	private JList<FontFamilyInfo> familiesList;
	private JPanel previewPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class FontFamilyInfo -----------------------------------------------

	private static class FontFamilyInfo
	{
		String name;
		TreeMap<String, Font> fonts;
	}

	//---- class FontFamilyRenderer -------------------------------------------

	private static class FontFamilyRenderer
		extends JPanel
		implements ListCellRenderer<FontFamilyInfo>
	{
		private FontFamilyRenderer() {
			initComponents();
		}

		@Override
		public Component getListCellRendererComponent( JList<? extends FontFamilyInfo> list,
			FontFamilyInfo value, int index, boolean isSelected, boolean cellHasFocus )
		{
			String family = value.name;
			StringBuilder buf = new StringBuilder();
			for( String key : value.fonts.keySet() ) {
				if( key.startsWith( family ) ) {
					key = key.substring( family.length() ).trim();
					if( key.isEmpty() )
						key = "Regular";
				}
				if( buf.length() > 0 )
					buf.append( ", " );
				buf.append( key );
			}

			familyNameLabel.setText( family );
			familyDescriptionLabel.setText( buf.toString() );

			familyNameLabel.setFont( UIManager.getFont( "large.font" ) );
			familyDescriptionLabel.setFont( UIManager.getFont( "small.font" ) );
			familyDescriptionLabel.setEnabled( isSelected );

			setBackground( isSelected ? list.getSelectionBackground() : list.getBackground() );
			Color fg = isSelected ? list.getSelectionForeground() : list.getForeground();
			familyNameLabel.setForeground( fg );
			familyDescriptionLabel.setForeground( fg );
			return this;
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
			familyNameLabel = new JLabel();
			familyDescriptionLabel = new JLabel();

			//======== this ========
			setLayout(new MigLayout(
				"insets 2 6 2 6,hidemode 3",
				// columns
				"[fill]",
				// rows
				"[]0" +
				"[]"));

			//---- familyNameLabel ----
			familyNameLabel.setText("text");
			add(familyNameLabel, "cell 0 0");

			//---- familyDescriptionLabel ----
			familyDescriptionLabel.setText("text");
			add(familyDescriptionLabel, "cell 0 1");
			// JFormDesigner - End of component initialization  //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
		private JLabel familyNameLabel;
		private JLabel familyDescriptionLabel;
		// JFormDesigner - End of variables declaration  //GEN-END:variables
	}
}
