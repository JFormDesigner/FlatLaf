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

package com.formdev.flatlaf.extras;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.ScaledEmptyBorder;
import com.formdev.flatlaf.util.UIScale;

/**
 * A simple UI defaults inspector that shows a window with all UI defaults used
 * in current look and feel.
 * <p>
 * To use it in an application install it with:
 * <pre>
 * FlatUIDefaultsInspector.install( "ctrl shift alt Y" );
 * </pre>
 * This can be done e.g. in the main() method and allows enabling (and disabling)
 * the UI defaults inspector with the given keystroke.
 *
 * @author Karl Tauber
 */
public class FlatUIDefaultsInspector
{
	private static final int KEY_MODIFIERS_MASK = InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.META_DOWN_MASK;

	private static FlatUIDefaultsInspector inspector;

	/**
	 * Installs a key listener into the application that allows enabling and disabling
	 * the UI inspector with the given keystroke (e.g. "ctrl shift alt Y").
	 */
	public static void install( String activationKeys ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( activationKeys );
		Toolkit.getDefaultToolkit().addAWTEventListener( e -> {
			if( e.getID() == KeyEvent.KEY_RELEASED &&
				((KeyEvent)e).getKeyCode() == keyStroke.getKeyCode() &&
				(((KeyEvent)e).getModifiersEx() & KEY_MODIFIERS_MASK) == (keyStroke.getModifiers() & KEY_MODIFIERS_MASK)  )
			{
				show();
			}
		}, AWTEvent.KEY_EVENT_MASK );
	}

	public static void show() {
		if( inspector != null ) {
			inspector.frame.toFront();
			return;
		}

		inspector = new FlatUIDefaultsInspector();
		inspector.frame.setVisible( true );
	}

	public static void hide() {
		if( inspector != null )
			inspector.frame.dispose();
	}

	private FlatUIDefaultsInspector() {
		initComponents();

		panel.setBorder( new ScaledEmptyBorder( 10, 10, 10, 10 ) );

		// initialize table
		Item[] items = getUIDefaultsItems();
		table.setModel( new ItemsTableModel( items ) );
		table.setDefaultRenderer( Item.class, new ValueRenderer() );

		// restore window bounds
		Preferences prefs = getPrefs();
		int x = prefs.getInt( "x", -1 );
		int y = prefs.getInt( "y", -1 );
		int width = prefs.getInt( "width", UIScale.scale( 600 ) );
		int height = prefs.getInt( "height", UIScale.scale( 800 ) );
		frame.setSize( width, height );
		if( x >= 0 && y >= 0 )
			frame.setLocation( x, y );
		else
			frame.setLocationRelativeTo( null );

		// restore column widths
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn( 0 ).setPreferredWidth( prefs.getInt( "column1width", 100 ) );
		columnModel.getColumn( 1 ).setPreferredWidth( prefs.getInt( "column2width", 100 ) );
	}

	private Item[] getUIDefaultsItems() {
		UIDefaults defaults = UIManager.getDefaults();

		ArrayList<Item> items = new ArrayList<>( defaults.size() );
		Enumeration<Object> e = defaults.keys();
		while( e.hasMoreElements() ) {
			Object key = e.nextElement();

			// ignore non-string keys
			if( !(key instanceof String) )
				continue;

			// ignore values of type Class
			Object value = defaults.get( key );
			if( value instanceof Class )
				continue;

			Item item = new Item();
			item.key = String.valueOf( key );
			item.value = value;
			items.add( item );
		}

		items.sort( (item1, item2) -> item1.key.compareToIgnoreCase( item2.key ) );
		return items.toArray( new Item[items.size()] );
	}

	private void saveWindowBounds() {
		Preferences prefs = getPrefs();
		prefs.putInt( "x", frame.getX() );
		prefs.putInt( "y", frame.getY() );
		prefs.putInt( "width", frame.getWidth() );
		prefs.putInt( "height", frame.getHeight() );

		TableColumnModel columnModel = table.getColumnModel();
		prefs.putInt( "column1width", columnModel.getColumn( 0 ).getWidth() );
		prefs.putInt( "column2width", columnModel.getColumn( 1 ).getWidth() );
	}

	private Preferences getPrefs() {
		return Preferences.userRoot().node( "flatlaf-uidefaults-inspector" );
	}

	private void windowClosed() {
		inspector = null;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		frame = new JFrame();
		panel = new JPanel();
		scrollPane = new JScrollPane();
		table = new JTable();

		//======== frame ========
		{
			frame.setTitle("UI Defaults");
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					FlatUIDefaultsInspector.this.windowClosed();
				}
				@Override
				public void windowClosing(WindowEvent e) {
					saveWindowBounds();
				}
				@Override
				public void windowDeactivated(WindowEvent e) {
					saveWindowBounds();
				}
			});
			Container frameContentPane = frame.getContentPane();
			frameContentPane.setLayout(new BorderLayout());

			//======== panel ========
			{
				panel.setLayout(new BorderLayout());

				//======== scrollPane ========
				{
					scrollPane.setViewportView(table);
				}
				panel.add(scrollPane, BorderLayout.CENTER);
			}
			frameContentPane.add(panel, BorderLayout.CENTER);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JFrame frame;
	private JPanel panel;
	private JScrollPane scrollPane;
	private JTable table;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Item ---------------------------------------------------------

	private static class Item {
		String key;
		Object value;
	}

	//---- class ItemsTableModel ----------------------------------------------

	private static class ItemsTableModel
		extends AbstractTableModel
	{
		private final Item[] items;

		ItemsTableModel( Item[] items ) {
			this.items = items;
		}

		@Override
		public int getRowCount() {
			return items.length;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName( int columnIndex ) {
			switch( columnIndex ) {
				case 0: return "Name";
				case 1: return "Value";
			}
			return super.getColumnName( columnIndex );
		}

		@Override
		public Class<?> getColumnClass( int columnIndex ) {
			switch( columnIndex ) {
				case 0: return String.class;
				case 1: return Item.class;
			}
			return super.getColumnClass( columnIndex );
		}

		@Override
		public Object getValueAt( int rowIndex, int columnIndex ) {
			Item item = items[rowIndex];
			switch( columnIndex ) {
				case 0: return item.key;
				case 1: return item;
			}
			return null;
		}
	}

	//---- class ValueRenderer ------------------------------------------------

	private static class ValueRenderer
		extends DefaultTableCellRenderer
	{
		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column )
		{
			Item item = (Item) value;

			// reset background, foreground and icon
			if( !(item.value instanceof Color) ) {
				setBackground( null );
				setForeground( null );
			}
			if( !(item.value instanceof Icon) )
				setIcon( null );

			// value to string
			if( item.value instanceof Color ) {
				Color color = (Color) item.value;
				HSLColor hslColor = new HSLColor( color );
				if( color.getAlpha() == 255 ) {
					value = String.format( "#%06x    rgb(%d, %d, %d)    hsl(%d, %d, %d)",
						color.getRGB() & 0xffffff,
						color.getRed(), color.getGreen(), color.getBlue(),
						(int) hslColor.getHue(), (int) hslColor.getSaturation(),
						(int) hslColor.getLuminance() );
				} else {
					value = String.format( "#%06x%02x   rgba(%d, %d, %d, %d)    hsla(%d, %d, %d, %d)",
						color.getRGB() & 0xffffff, color.getAlpha(),
						color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
						(int) hslColor.getHue(), (int) hslColor.getSaturation(),
						(int) hslColor.getLuminance(), (int) (hslColor.getAlpha() * 100) );
				}
			} else if( item.value instanceof Insets ) {
				Insets insets = (Insets) item.value;
				value = insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right;
			} else if( item.value instanceof Dimension ) {
				Dimension dim = (Dimension) item.value;
				value = dim.width + "," + dim.height;
			} else if( item.value instanceof Font ) {
				Font font = (Font) item.value;
				value = font.getFamily() + " " + font.getSize();
				if( font.isBold() )
					value += " bold";
				if( font.isItalic() )
					value += " italic";
			} else if( item.value instanceof Icon ) {
				Icon icon = (Icon) item.value;
				value = icon.getIconWidth() + "x" + icon.getIconHeight() + "   " + icon.getClass().getName();
				setIcon( new SafeIcon( icon ) );
			} else if( item.value instanceof ActionMap ) {
				ActionMap actionMap = (ActionMap) item.value;
				value = "ActionMap (" + actionMap.size() + ")";
			} else if( item.value instanceof InputMap ) {
				InputMap inputMap = (InputMap) item.value;
				value = "InputMap (" + inputMap.size() + ")";
			} else
				value = item.value;

			super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

			if( item.value instanceof Color ) {
				Color color = (Color) item.value;
				boolean isDark = new HSLColor( color ).getLuminance() < 70;
				setBackground( color );
				setForeground( isDark ? Color.white : Color.black );
			}

			setToolTipText( String.valueOf( item.value ) );
			return this;
		}
	}

	//---- class SafeIcon -----------------------------------------------------

	private static class SafeIcon
		implements Icon
	{
		private final Icon icon;

		SafeIcon( Icon icon ) {
			this.icon = icon;
		}

		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ) {
			try {
				icon.paintIcon( c, g, x, y );
			} catch( Exception ex ) {
				g.setColor( Color.red );
				g.drawRect( x, y, getIconWidth() - 1, getIconHeight() - 1 );
			}
		}

		@Override
		public int getIconWidth() {
			return icon.getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return icon.getIconHeight();
		}
	}
}
