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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.ui.FlatUIUtils;
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

	private final String title;
	private final PropertyChangeListener lafListener = this::lafChanged;

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

		title = frame.getTitle();
		updateWindowTitle();

		panel.setBorder( new ScaledEmptyBorder( 10, 10, 10, 10 ) );
		filterPanel.setBorder( new ScaledEmptyBorder( 0, 0, 10, 0 ) );

		// initialize filter
		filterField.getDocument().addDocumentListener( new DocumentListener() {
			@Override
			public void removeUpdate( DocumentEvent e ) {
				filterChanged();
			}
			@Override
			public void insertUpdate( DocumentEvent e ) {
				filterChanged();
			}
			@Override
			public void changedUpdate( DocumentEvent e ) {
				filterChanged();
			}
		} );
		delegateKey( KeyEvent.VK_UP, "unitScrollUp" );
		delegateKey( KeyEvent.VK_DOWN, "unitScrollDown" );
		delegateKey( KeyEvent.VK_PAGE_UP, "scrollUp" );
		delegateKey( KeyEvent.VK_PAGE_DOWN, "scrollDown" );

		// initialize table
		table.setModel( new ItemsTableModel( getUIDefaultsItems() ) );
		table.setDefaultRenderer( String.class, new KeyRenderer() );
		table.setDefaultRenderer( Item.class, new ValueRenderer() );
		table.getRowSorter().setSortKeys( Collections.singletonList(
			new RowSorter.SortKey( 0, SortOrder.ASCENDING ) ) );

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

		// restore filter
		String filter = prefs.get( "filter", "" );
		String valueType = prefs.get( "valueType", null );
		if( filter != null && !filter.isEmpty() )
			filterField.setText( filter );
		if( valueType != null )
			valueTypeField.setSelectedItem( valueType );

		UIManager.addPropertyChangeListener( lafListener );

		// register F5 key to refresh
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> refresh(),
			KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		// register ESC key to close frame
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> frame.dispose(),
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private void delegateKey( int keyCode, String actionKey ) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke( keyCode, 0 );
		String actionMapKey = "delegate-" + actionKey;

		filterField.getInputMap().put( keyStroke, actionMapKey );
		filterField.getActionMap().put( actionMapKey, new AbstractAction() {
			@Override
			public void actionPerformed( ActionEvent e ) {
				Action action = scrollPane.getActionMap().get( actionKey );
				if( action != null ) {
					action.actionPerformed( new ActionEvent( scrollPane,
						e.getID(), actionKey, e.getWhen(), e.getModifiers() ) );
				}
			}
		} );
	}

	void lafChanged( PropertyChangeEvent e ) {
		if( "lookAndFeel".equals( e.getPropertyName() ) )
			refresh();
	}

	void refresh() {
		ItemsTableModel model = (ItemsTableModel) table.getModel();
		model.setItems( getUIDefaultsItems() );

		updateWindowTitle();
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

			items.add( new Item( String.valueOf( key ), value ) );
		}

		return items.toArray( new Item[items.size()] );
	}

	private void updateWindowTitle() {
		frame.setTitle( title + "  -  " + UIManager.getLookAndFeel().getName() );
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
		UIManager.removePropertyChangeListener( lafListener );

		inspector = null;
	}

	private void filterChanged() {
		String filter = filterField.getText().trim();
		String valueType = (String) valueTypeField.getSelectedItem();

		// split filter string on space characters
		String[] filters = filter.split( " +" );
		for( int i = 0; i < filters.length; i++ )
			filters[i] = filters[i].toLowerCase( Locale.ENGLISH );

		ItemsTableModel model = (ItemsTableModel) table.getModel();
		model.setFilter( item -> {
			if( valueType != null &&
				!valueType.equals( "(any)" ) &&
				!valueType.equals( typeOfValue( item.value ) ) )
			  return false;

			String lkey = item.key.toLowerCase( Locale.ENGLISH );
			String lvalue = item.getValueAsString().toLowerCase( Locale.ENGLISH );
			for( String f : filters ) {
				if( lkey.contains( f ) || lvalue.contains( f ) )
					return true;
			}
			return false;
		} );

		Preferences prefs = getPrefs();
		prefs.put( "filter", filter );
		prefs.put( "valueType", valueType );
	}

	private String typeOfValue( Object value ) {
		if( value instanceof Boolean )
			return "Boolean";
		if( value instanceof Border )
			return "Border";
		if( value instanceof Color )
			return "Color";
		if( value instanceof Dimension )
			return "Dimension";
		if( value instanceof Float )
			return "Float";
		if( value instanceof Font )
			return "Font";
		if( value instanceof Icon )
			return "Icon";
		if( value instanceof Insets )
			return "Insets";
		if( value instanceof Integer )
			return "Integer";
		if( value instanceof String )
			return "String";
		return "(other)";
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		frame = new JFrame();
		panel = new JPanel();
		filterPanel = new JPanel();
		flterLabel = new JLabel();
		filterField = new JTextField();
		valueTypeLabel = new JLabel();
		valueTypeField = new JComboBox<>();
		scrollPane = new JScrollPane();
		table = new JTable();

		//======== frame ========
		{
			frame.setTitle("UI Defaults Inspector");
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

				//======== filterPanel ========
				{
					filterPanel.setLayout(new GridBagLayout());
					((GridBagLayout)filterPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
					((GridBagLayout)filterPanel.getLayout()).rowHeights = new int[] {0, 0};
					((GridBagLayout)filterPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0E-4};
					((GridBagLayout)filterPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

					//---- flterLabel ----
					flterLabel.setText("Filter:");
					flterLabel.setLabelFor(filterField);
					flterLabel.setDisplayedMnemonic('F');
					filterPanel.add(flterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 10), 0, 0));
					filterPanel.add(filterField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 10), 0, 0));

					//---- valueTypeLabel ----
					valueTypeLabel.setText("Value Type:");
					valueTypeLabel.setLabelFor(valueTypeField);
					valueTypeLabel.setDisplayedMnemonic('T');
					filterPanel.add(valueTypeLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 10), 0, 0));

					//---- valueTypeField ----
					valueTypeField.setModel(new DefaultComboBoxModel<>(new String[] {
						"(any)",
						"Boolean",
						"Border",
						"Color",
						"Dimension",
						"Float",
						"Font",
						"Icon",
						"Insets",
						"Integer",
						"String",
						"(other)"
					}));
					valueTypeField.addActionListener(e -> filterChanged());
					filterPanel.add(valueTypeField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				}
				panel.add(filterPanel, BorderLayout.NORTH);

				//======== scrollPane ========
				{

					//---- table ----
					table.setAutoCreateRowSorter(true);
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
	private JPanel filterPanel;
	private JLabel flterLabel;
	private JTextField filterField;
	private JLabel valueTypeLabel;
	private JComboBox<String> valueTypeField;
	private JScrollPane scrollPane;
	private JTable table;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Item ---------------------------------------------------------

	private static class Item {
		final String key;
		final Object value;

		private String valueStr;

		Item( String key, Object value ) {
			this.key = key;
			this.value = value;
		}

		String getValueAsString() {
			if( valueStr != null )
				return valueStr;

			if( value instanceof Color ) {
				Color color = (Color) value;
				HSLColor hslColor = new HSLColor( color );
				if( color.getAlpha() == 255 ) {
					valueStr = String.format( "%s    rgb(%d, %d, %d)    hsl(%d, %d, %d)",
						color2hex( color ),
						color.getRed(), color.getGreen(), color.getBlue(),
						(int) hslColor.getHue(), (int) hslColor.getSaturation(),
						(int) hslColor.getLuminance() );
				} else {
					valueStr = String.format( "%s   rgba(%d, %d, %d, %d)    hsla(%d, %d, %d, %d)",
						color2hex( color ),
						color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha(),
						(int) hslColor.getHue(), (int) hslColor.getSaturation(),
						(int) hslColor.getLuminance(), (int) (hslColor.getAlpha() * 100) );
				}
			} else if( value instanceof Insets ) {
				Insets insets = (Insets) value;
				valueStr = insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right;
			} else if( value instanceof Dimension ) {
				Dimension dim = (Dimension) value;
				valueStr = dim.width + "," + dim.height;
			} else if( value instanceof Font ) {
				Font font = (Font) value;
				valueStr = font.getFamily() + " " + font.getSize();
				if( font.isBold() )
					valueStr += " bold";
				if( font.isItalic() )
					valueStr += " italic";
			} else if( value instanceof Icon ) {
				Icon icon = (Icon) value;
				valueStr = icon.getIconWidth() + "x" + icon.getIconHeight() + "   " + icon.getClass().getName();
			} else if( value instanceof ActionMap ) {
				ActionMap actionMap = (ActionMap) value;
				valueStr = "ActionMap (" + actionMap.size() + ")";
			} else if( value instanceof InputMap ) {
				InputMap inputMap = (InputMap) value;
				valueStr = "InputMap (" + inputMap.size() + ")";
			} else
				valueStr = String.valueOf( value );

			return valueStr;
		}

		private String color2hex( Color color ) {
			int rgb = color.getRGB();
			boolean hasAlpha = color.getAlpha() != 255;

			boolean useShortFormat =
				(rgb & 0xf0000000) == (rgb & 0xf000000) << 4 &&
				(rgb & 0xf00000) == (rgb & 0xf0000) << 4 &&
				(rgb & 0xf000) == (rgb & 0xf00) << 4 &&
				(rgb & 0xf0) == (rgb & 0xf) << 4;

			if( useShortFormat ) {
				int srgb = ((rgb & 0xf0000) >> 8) | ((rgb & 0xf00) >> 4) | (rgb & 0xf);
				return String.format( hasAlpha ? "#%03X%X" : "#%03X", srgb, (rgb >> 24) & 0xf );
			} else
				return String.format( hasAlpha ? "#%06X%02X" : "#%06X", rgb & 0xffffff, (rgb >> 24) & 0xff );
		}

		// used for sorting by value
		@Override
		public String toString() {
			return getValueAsString();
		}
	}

	//---- class ItemsTableModel ----------------------------------------------

	private static class ItemsTableModel
		extends AbstractTableModel
	{
		private Item[] allItems;
		private Item[] items;
		private Predicate<Item> filter;

		ItemsTableModel( Item[] items ) {
			this.allItems = this.items = items;
		}

		void setItems( Item[] items ) {
			this.allItems = this.items = items;
			setFilter( filter );
		}

		void setFilter( Predicate<Item> filter ) {
			this.filter = filter;

			if( filter != null ) {
				ArrayList<Item> list = new ArrayList<>( allItems.length );
				for( Item item : allItems ) {
					if( filter.test( item ) )
						list.add( item );
				}
				items = list.toArray( new Item[list.size()] );
			} else
				items = allItems;

			fireTableDataChanged();
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

	//---- class Renderer -----------------------------------------------------

	private static class Renderer
		extends DefaultTableCellRenderer
	{
		protected boolean selected;
		protected boolean first;

		protected void init( JTable table, String key, boolean selected, int row ) {
			this.selected = selected;

			first = false;
			if( row > 0 ) {
				String previousKey = (String) table.getValueAt( row - 1, 0 );
				int dot = key.indexOf( '.' );
				if( dot > 0 ) {
					String prefix = key.substring( 0, dot + 1 );
					first = !previousKey.startsWith( prefix );
				} else
					first = previousKey.indexOf( '.' ) > 0;
			}
		}

		protected void paintSeparator( Graphics g ) {
			if( first && !selected ) {
				g.setColor( FlatLaf.isLafDark() ? Color.gray : Color.lightGray );
				g.fillRect( 0, 0, getWidth() - 1, 1 );
			}
		}
	}

	//---- class KeyRenderer --------------------------------------------------

	private static class KeyRenderer
		extends Renderer
	{
		private String key;

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column )
		{
			key = (String) value;
			init( table, key, isSelected, row );
			setToolTipText( key );
			return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			int width = getWidth();
			int height = getHeight();
			Insets insets = getInsets();
			FontMetrics fm = getFontMetrics( getFont() );

			g.setColor( getBackground() );
			g.fillRect( 0, 0, width, height );

			Rectangle viewR = new Rectangle( insets.left, insets.top,
				width - (insets.left + insets.right),
				height - (insets.top + insets.bottom) );
			Rectangle iconR = new Rectangle();
			Rectangle textR = new Rectangle();

			String clippedText = SwingUtilities.layoutCompoundLabel( this, fm, key, null,
				getVerticalAlignment(), getHorizontalAlignment(),
				getVerticalTextPosition(), getHorizontalTextPosition(),
				viewR, iconR, textR, getIconTextGap() );
			int x = textR.x;
			int y = textR.y + fm.getAscent();

			int dot = key.indexOf( '.' );
			if( dot > 0 && !selected ) {
				g.setColor( UIManager.getColor( "Label.disabledForeground" ) );

				if( dot >= clippedText.length() )
					FlatUIUtils.drawString( this, g, clippedText, x, y );
				else {
					String prefix = clippedText.substring( 0, dot + 1 );
					String subkey = clippedText.substring( dot + 1 );

					FlatUIUtils.drawString( this, g, prefix, x, y );

					g.setColor( getForeground() );
					FlatUIUtils.drawString( this, g, subkey, x + fm.stringWidth( prefix ), y );
				}
			} else {
				g.setColor( getForeground() );
				FlatUIUtils.drawString( this, g, clippedText, x, y );
			}

			paintSeparator( g );
		}
	}

	//---- class ValueRenderer ------------------------------------------------

	private static class ValueRenderer
		extends Renderer
	{
		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column )
		{
			Item item = (Item) value;
			init( table, item.key, isSelected, row );

			// reset background, foreground and icon
			if( !(item.value instanceof Color) ) {
				setBackground( null );
				setForeground( null );
			}
			if( !(item.value instanceof Icon) )
				setIcon( null );

			// value to string
			value = item.getValueAsString();

			super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

			if( item.value instanceof Color ) {
				Color color = (Color) item.value;
				boolean isDark = new HSLColor( color ).getLuminance() < 70;
				setBackground( color );
				setForeground( isDark ? Color.white : Color.black );
			} else if( item.value instanceof Icon ) {
				Icon icon = (Icon) item.value;
				setIcon( new SafeIcon( icon ) );
			}

			setToolTipText( String.valueOf( item.value ) );
			return this;
		}

		@Override
		protected void paintComponent( Graphics g ) {
			super.paintComponent( g );
			paintSeparator( g );
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
