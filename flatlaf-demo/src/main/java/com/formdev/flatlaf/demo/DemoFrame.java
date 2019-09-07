/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.demo;

import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
class DemoFrame
	extends JFrame
{
	DemoFrame() {
		int tabIndex = FlatLafDemo.prefs.getInt( FlatLafDemo.KEY_TAB, 0 );

		initComponents();
		controlBar.initialize( this, tabbedPane );

		if( tabIndex >= 0 && tabIndex < tabbedPane.getTabCount() && tabIndex != tabbedPane.getSelectedIndex() )
			tabbedPane.setSelectedIndex( tabIndex );
	}

	private void selectedTabChanged() {
		FlatLafDemo.prefs.putInt( FlatLafDemo.KEY_TAB, tabbedPane.getSelectedIndex() );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JPanel contentPanel = new JPanel();
		tabbedPane = new JTabbedPane();
		BasicComponentsPanel basicComponentsPanel = new BasicComponentsPanel();
		MoreComponentsPanel moreComponentsPanel = new MoreComponentsPanel();
		DataComponentsPanel dataComponentsPanel = new DataComponentsPanel();
		TabsPanel tabsPanel = new TabsPanel();
		OptionPanePanel optionPanePanel = new OptionPanePanel();
		controlBar = new ControlBar();

		//======== this ========
		setTitle("FlatLaf Demo");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== contentPanel ========
		{
			contentPanel.setLayout(new MigLayout(
				"insets dialog,hidemode 3",
				// columns
				"[grow,fill]",
				// rows
				"[grow,fill]" +
				"[]"));

			//======== tabbedPane ========
			{
				tabbedPane.addChangeListener(e -> selectedTabChanged());
				tabbedPane.addTab("Basic Components", basicComponentsPanel);
				tabbedPane.addTab("More Components", moreComponentsPanel);
				tabbedPane.addTab("Data Components", dataComponentsPanel);
				tabbedPane.addTab("Tabs", tabsPanel);
				tabbedPane.addTab("Option Pane", optionPanePanel);
			}
			contentPanel.add(tabbedPane, "cell 0 0");
			contentPanel.add(controlBar, "cell 0 1");
		}
		contentPane.add(contentPanel, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JTabbedPane tabbedPane;
	private ControlBar controlBar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
