package com.formdev.flatlaf.testing.contrib;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * from https://github.com/JFormDesigner/FlatLaf/pull/683#issuecomment-1585667066
 *
 * @author Chrriis
 */
public class SmoothScrollingTest {

	private static class CustomTree extends JTree {

		public CustomTree() {
			super(getDefaultTreeModel());
		}

		protected static TreeModel getDefaultTreeModel() {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("JTree");
			for(int i=0; i<1000; i++) {
				DefaultMutableTreeNode parent;
				parent = new DefaultMutableTreeNode("colors-" + i);
				root.add(parent);
				parent.add(new DefaultMutableTreeNode("blue"));
				parent.add(new DefaultMutableTreeNode("violet"));
				parent.add(new DefaultMutableTreeNode("red"));
				parent.add(new DefaultMutableTreeNode("yellow"));
				parent = new DefaultMutableTreeNode("sports-" + i);
				root.add(parent);
				parent.add(new DefaultMutableTreeNode("basketball"));
				parent.add(new DefaultMutableTreeNode("soccer"));
				parent.add(new DefaultMutableTreeNode("football"));
				parent.add(new DefaultMutableTreeNode("hockey"));
				parent = new DefaultMutableTreeNode("food-" + i);
				root.add(parent);
				parent.add(new DefaultMutableTreeNode("hot dogs"));
				parent.add(new DefaultMutableTreeNode("pizza"));
				parent.add(new DefaultMutableTreeNode("ravioli"));
				parent.add(new DefaultMutableTreeNode("bananas"));
			}
			return new DefaultTreeModel(root);
		}
	}

	private static class CustomTable extends JTable {
		public CustomTable() {
			super(createDefaultTableModel());
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			setCellSelectionEnabled(true);
		}
		private static TableModel createDefaultTableModel() {
			int columnChunkCount = 90;
			Object[][] data = new Object[1000][3 * columnChunkCount];
			String[] prefixes = {"", "  ", "    ", "     "};
			String[] titles = new String[columnChunkCount * 3];
			for(int j=0; j<columnChunkCount; j++) {
				titles[j * 3] = "Column" + j * 3 + 1;
				titles[j * 3 + 1] = "Column" + j * 3 + 2;
				titles[j * 3 + 2] = "Column" + j * 3 + 3;
				for(int i=0; i<data.length; i++) {
					data[i][j * 3] = prefixes[i % prefixes.length] + "Cell " + (i + 1) + "/" + (j + 1);
					data[i][j * 3 + 1] = "Cell " + (i + 1) + "/" + (j + 2);
					data[i][j * 3 + 2] = Boolean.valueOf(i%5 == 0);
				}
			}
			AbstractTableModel tableModel = new AbstractTableModel() {
				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					return data[rowIndex][columnIndex];
				}
				@Override
				public int getRowCount() {
					return data.length;
				}
				@Override
				public int getColumnCount() {
					return titles.length;
				}
				@Override
				public String getColumnName(int column) {
					return titles[column];
				}
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnIndex % 3 == 2 ? Boolean.class : String.class;
				}
			};
			return tableModel;
		}
	}

	private static class ScrollableCustomPane extends JPanel implements Scrollable {
		public ScrollableCustomPane() {
			super(new GridLayout(100, 0));
			for(int i=0; i<10000; i++) {
				add(new JButton("Button " + (i + 1)));
			}
		}
		@Override
		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}
		@Override
		public boolean getScrollableTracksViewportWidth() {
			return false;
		}
		@Override
		public boolean getScrollableTracksViewportHeight() {
			return false;
		}
		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
			Dimension referenceSize = getComponent(0).getSize();
			switch(orientation) {
				case SwingConstants.VERTICAL: return referenceSize.height;
				case SwingConstants.HORIZONTAL: return referenceSize.width;
			}
			return 20;
		}
		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
			Dimension referenceSize = getComponent(0).getSize();
			switch(orientation) {
				case SwingConstants.VERTICAL: return referenceSize.height * 10;
				case SwingConstants.HORIZONTAL: return referenceSize.width * 5;
			}
			return 100;
		}
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(new FlatLightLaf());
//		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		UIManager.getDefaults().put("ScrollBar.showButtons", true);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JCheckBox smoothCheckBox = new JCheckBox("Smooth", true);
		JComboBox<Integer> scrollModeComboBox = new JComboBox<>(new Integer[] {JViewport.BLIT_SCROLL_MODE, JViewport.BACKINGSTORE_SCROLL_MODE, JViewport.SIMPLE_SCROLL_MODE});
		JCheckBox blitBlockCheckBox = new JCheckBox("Prevent Blit");
		@SuppressWarnings( "rawtypes" )
		ListCellRenderer defaultComboRenderer = scrollModeComboBox.getRenderer();
		scrollModeComboBox.setRenderer(new ListCellRenderer<Integer>() {
			@SuppressWarnings( "unchecked" )
			@Override
			public Component getListCellRendererComponent(JList<? extends Integer> list, Integer value, int index, boolean isSelected, boolean cellHasFocus) {
				String sValue = null;
				switch(value) {
					case JViewport.BLIT_SCROLL_MODE: sValue = "Blit"; break;
					case JViewport.BACKINGSTORE_SCROLL_MODE: sValue = "Backing Store"; break;
					case JViewport.SIMPLE_SCROLL_MODE: sValue = "Simple"; break;
				}
				return defaultComboRenderer.getListCellRendererComponent(list, sValue, index, isSelected, cellHasFocus);
			}
		});
		JPanel northBar = new JPanel();
		JButton lightPopupButton = new JButton("Light Popup");
		lightPopupButton.addActionListener(e -> {
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.setLightWeightPopupEnabled(true);
			popupMenu.putClientProperty(FlatClientProperties.POPUP_BORDER_CORNER_RADIUS, 0);
			JTable table = new CustomTable();
			table.setColumnSelectionInterval(1, 1);
			table.setRowSelectionInterval(28, 28);
			JScrollPane tableScrollPane = new JScrollPane(table);
			tableScrollPane.setPreferredSize(new Dimension(400, 600));
			tableScrollPane.putClientProperty(FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, !smoothCheckBox.isSelected()? Boolean.FALSE: null);
			tableScrollPane.getViewport().setScrollMode((Integer)scrollModeComboBox.getSelectedItem());
			JRootPane popupRootPane = new JRootPane();
			popupRootPane.getContentPane().add(tableScrollPane);
			popupMenu.add(popupRootPane);
			popupMenu.show(lightPopupButton, 0, lightPopupButton.getHeight());
		});
		northBar.add(lightPopupButton);
		JButton heavyPopupButton = new JButton("Heavy Popup");
		heavyPopupButton.addActionListener(e -> {
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.setLightWeightPopupEnabled(false);
			JTable table = new CustomTable();
			table.setColumnSelectionInterval(1, 1);
			table.setRowSelectionInterval(28, 28);
			JScrollPane tableScrollPane = new JScrollPane(table);
			tableScrollPane.setPreferredSize(new Dimension(400, 600));
			tableScrollPane.putClientProperty(FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, !smoothCheckBox.isSelected()? Boolean.FALSE: null);
			tableScrollPane.getViewport().setScrollMode((Integer)scrollModeComboBox.getSelectedItem());
			JRootPane popupRootPane = new JRootPane();
			popupRootPane.getContentPane().add(tableScrollPane);
			popupMenu.add(popupRootPane);
			popupMenu.show(heavyPopupButton, 0, heavyPopupButton.getHeight());
		});
		northBar.add(heavyPopupButton);
		contentPane.add(northBar, BorderLayout.NORTH);
		JPanel centerPane = new JPanel(new GridLayout(1, 0));
		JTree tree = new CustomTree();
		JScrollPane treeScrollPane = new JScrollPane(tree);
		centerPane.add(treeScrollPane);
		for(int i=tree.getRowCount()-1; i>=0; i--) {
			tree.expandRow(i);
		}
		tree.setSelectionRow(28);
		JTable table = new CustomTable();
		table.setColumnSelectionInterval(1, 1);
		table.setRowSelectionInterval(28, 28);
		JScrollPane tableScrollPane = new JScrollPane(table);
		centerPane.add(tableScrollPane);
		ScrollableCustomPane scrollableCustomPane = new ScrollableCustomPane();
		JScrollPane customPaneScrollPane = new JScrollPane(scrollableCustomPane);
		centerPane.add(customPaneScrollPane);
		contentPane.add(centerPane, BorderLayout.CENTER);
		JPanel southBar = new JPanel(new FlowLayout());
		smoothCheckBox.addItemListener(e -> {
			boolean isSmooth = e.getStateChange() == ItemEvent.SELECTED;
			treeScrollPane.putClientProperty(FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, !isSmooth? Boolean.FALSE: null);
			tableScrollPane.putClientProperty(FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, !isSmooth? Boolean.FALSE: null);
			customPaneScrollPane.putClientProperty(FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, !isSmooth? Boolean.FALSE: null);
		});
		southBar.add(smoothCheckBox);
		southBar.add(Box.createHorizontalStrut(30));
		JButton scrollButton = new JButton("Scroll rect");
		scrollButton.addActionListener(e -> {
			treeScrollPane.getViewport().setViewPosition(new Point(9, Integer.MAX_VALUE / 2));
			tableScrollPane.getViewport().setViewPosition(new Point(9, Integer.MAX_VALUE / 2));
			customPaneScrollPane.getViewport().setViewPosition(new Point(9, Integer.MAX_VALUE / 2));
		});
		southBar.add(scrollButton);
		southBar.add(Box.createHorizontalStrut(30));
		scrollModeComboBox.addItemListener(e -> {
			SwingUtilities.invokeLater(() -> {
				int scrollMode = (Integer)scrollModeComboBox.getSelectedItem();
				treeScrollPane.getViewport().setScrollMode(scrollMode);
				tableScrollPane.getViewport().setScrollMode(scrollMode);
				customPaneScrollPane.getViewport().setScrollMode(scrollMode);
			});
		});
		southBar.add(scrollModeComboBox);
		southBar.add(Box.createHorizontalStrut(30));
		JButton xButton1 = new JButton("Blit Blocker");
		xButton1.setBounds(20, 400, xButton1.getPreferredSize().width, xButton1.getPreferredSize().height);
		JButton xButton2 = new JButton("Blit Blocker");
		xButton2.setBounds(600, 400, xButton2.getPreferredSize().width, xButton2.getPreferredSize().height);
		JButton xButton3 = new JButton("Blit Blocker");
		xButton3.setBounds(800, 400, xButton3.getPreferredSize().width, xButton3.getPreferredSize().height);
		blitBlockCheckBox.addItemListener(e -> {
			boolean isBlockingBlit = e.getStateChange() == ItemEvent.SELECTED;
			JLayeredPane layeredPane = frame.getLayeredPane();
			if(isBlockingBlit) {
				layeredPane.add(xButton1);
				layeredPane.add(xButton2);
				layeredPane.add(xButton3);
			} else {
				layeredPane.remove(xButton1);
				layeredPane.remove(xButton2);
				layeredPane.remove(xButton3);
			}
			layeredPane.revalidate();
			layeredPane.repaint();
		});
		southBar.add(blitBlockCheckBox);
		contentPane.add(southBar, BorderLayout.SOUTH);
		frame.getContentPane().add(contentPane);
		frame.setSize(1200, 800);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

}
