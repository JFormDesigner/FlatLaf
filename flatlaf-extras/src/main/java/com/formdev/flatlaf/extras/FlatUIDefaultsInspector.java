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
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.icons.FlatAbstractIcon;
import com.formdev.flatlaf.ui.FlatBorder;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.ui.FlatMarginBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.DerivedColor;
import com.formdev.flatlaf.util.GrayFilter;
import com.formdev.flatlaf.util.HSLColor;
import com.formdev.flatlaf.util.LoggingFacade;
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

	private static JFrame inspectorFrame;

	private final PropertyChangeListener lafListener = this::lafChanged;
	private final PropertyChangeListener lafDefaultsListener = this::lafDefaultsChanged;
	private boolean refreshPending;
	private Properties derivedColorKeys;

	/**
	 * Installs a key listener into the application that allows enabling and disabling
	 * the UI inspector with the given keystroke (e.g. "ctrl shift alt Y").
	 *
	 * @param activationKeys a keystroke (e.g. "ctrl shift alt Y"), or {@code null} to use "ctrl shift alt Y"
	 */
	public static void install( String activationKeys ) {
		if( activationKeys == null )
			activationKeys = "ctrl shift alt Y";

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
		if( inspectorFrame != null ) {
			ensureOnScreen( inspectorFrame );
			inspectorFrame.toFront();
			return;
		}

		inspectorFrame = new FlatUIDefaultsInspector().createFrame();
		inspectorFrame.setVisible( true );
	}

	public static void hide() {
		if( inspectorFrame != null )
			inspectorFrame.dispose();
	}

	/**
	 * Creates a UI defaults inspector panel that can be embedded into any window.
	 */
	public static JComponent createInspectorPanel() {
		return new FlatUIDefaultsInspector().panel;
	}

	private FlatUIDefaultsInspector() {
		initComponents();

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

		// restore column widths
		Preferences prefs = getPrefs();
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn( 0 ).setPreferredWidth( prefs.getInt( "column1width", 100 ) );
		columnModel.getColumn( 1 ).setPreferredWidth( prefs.getInt( "column2width", 100 ) );

		PropertyChangeListener columnWidthListener = e -> {
			if( "width".equals( e.getPropertyName() ) ) {
				prefs.putInt( "column1width", columnModel.getColumn( 0 ).getWidth() );
				prefs.putInt( "column2width", columnModel.getColumn( 1 ).getWidth() );
			}
		};
		columnModel.getColumn( 0 ).addPropertyChangeListener( columnWidthListener );
		columnModel.getColumn( 1 ).addPropertyChangeListener( columnWidthListener );

		// restore filter
		String filter = prefs.get( "filter", "" );
		String valueType = prefs.get( "valueType", null );
		if( filter != null && !filter.isEmpty() )
			filterField.setText( filter );
		if( valueType != null )
			valueTypeField.setSelectedItem( valueType );

		panel.addPropertyChangeListener( "ancestor", e -> {
			if( e.getNewValue() != null ) {
				UIManager.addPropertyChangeListener( lafListener );
				UIManager.getDefaults().addPropertyChangeListener( lafDefaultsListener );
			} else {
				UIManager.removePropertyChangeListener( lafListener );
				UIManager.getDefaults().removePropertyChangeListener( lafDefaultsListener );
			}
		} );

		// register F5 key to refresh
		panel.registerKeyboardAction(
			e -> refresh(),
			KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
	}

	private JFrame createFrame() {
		JFrame frame = new JFrame();
		frame.setTitle( "UI Defaults Inspector" );
		frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		frame.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosed( WindowEvent e ) {
				inspectorFrame = null;
			}
			@Override
			public void windowClosing( WindowEvent e ) {
				saveWindowBounds( frame );
			}
			@Override
			public void windowDeactivated( WindowEvent e ) {
				saveWindowBounds( frame );
			}
		} );

		updateWindowTitle( frame );

		frame.getContentPane().add( panel, BorderLayout.CENTER );

		// restore window bounds
		Preferences prefs = getPrefs();
		int x = prefs.getInt( "x", -1 );
		int y = prefs.getInt( "y", -1 );
		int width = prefs.getInt( "width", UIScale.scale( 600 ) );
		int height = prefs.getInt( "height", UIScale.scale( 800 ) );
		frame.setSize( width, height );
		if( x != -1 && y != -1 ) {
			frame.setLocation( x, y );
			ensureOnScreen( frame );
		} else
			frame.setLocationRelativeTo( null );

		// register ESC key to close frame
		((JComponent)frame.getContentPane()).registerKeyboardAction(
			e -> frame.dispose(),
			KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false ),
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

		return frame;
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

	private static void ensureOnScreen( JFrame frame ) {
		Rectangle frameBounds = frame.getBounds();
		boolean onScreen = false;
		for( GraphicsDevice screen : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices() ) {
			GraphicsConfiguration gc = screen.getDefaultConfiguration();
			Rectangle screenBounds = FlatUIUtils.subtractInsets( gc.getBounds(),
				Toolkit.getDefaultToolkit().getScreenInsets( gc ) );
			if( frameBounds.intersects( screenBounds ) ) {
				onScreen = true;
				break;
			}
		}

		if( !onScreen )
			frame.setLocationRelativeTo( null );
	}

	private void lafChanged( PropertyChangeEvent e ) {
		if( "lookAndFeel".equals( e.getPropertyName() ) )
			refresh();
	}

	private void lafDefaultsChanged( PropertyChangeEvent e ) {
		if( refreshPending )
			return;

		refreshPending = true;
		EventQueue.invokeLater( () -> {
			refresh();
			refreshPending = false;
		} );
	}

	private void refresh() {
		ItemsTableModel model = (ItemsTableModel) table.getModel();
		model.setItems( getUIDefaultsItems() );

		JFrame frame = (JFrame) SwingUtilities.getAncestorOfClass( JFrame.class, panel );
		if( frame != null )
			updateWindowTitle( frame );
	}

	private Item[] getUIDefaultsItems() {
		UIDefaults defaults = UIManager.getDefaults();
		UIDefaults lafDefaults = UIManager.getLookAndFeelDefaults();

		Set<Entry<Object, Object>> defaultsSet = defaults.entrySet();
		ArrayList<Item> items = new ArrayList<>( defaultsSet.size() );
		HashSet<Object> keys = new HashSet<>( defaultsSet.size() );
		Color[] pBaseColor = new Color[1];
		for( Entry<Object,Object> e : defaultsSet ) {
			Object key = e.getKey();

			// ignore non-string keys
			if( !(key instanceof String) )
				continue;

			// ignore internal keys
			if( ((String)key).startsWith( "FlatLaf.internal." ) )
				continue;

			// ignore values of type Class
			Object value = defaults.get( key );
			if( value instanceof Class )
				continue;

			// avoid duplicate keys if UIManager.put(key,value) was used to override a LaF value
			if( !keys.add( key ) )
				continue;

			// resolve derived color
			Object info = null;
			if( value instanceof DerivedColor ) {
				Color resolvedColor = resolveDerivedColor( defaults, (String) key, (DerivedColor) value, pBaseColor );
				if( resolvedColor != value )
					info = new Color[] { resolvedColor, pBaseColor[0] };
			}

			// check whether key was overridden using UIManager.put(key,value)
			Object lafValue = null;
			if( defaults.containsKey( key ) )
				lafValue = lafDefaults.get( key );

			// add item
			items.add( new Item( String.valueOf( key ), value, lafValue, info ) );
		}

		return items.toArray( new Item[items.size()] );
	}

	private Color resolveDerivedColor( UIDefaults defaults, String key, Color color, Color[] pBaseColor ) {
		if( pBaseColor != null )
			pBaseColor[0] = null;

		if( !(color instanceof DerivedColor) )
			return color;

		if( derivedColorKeys == null )
			derivedColorKeys = loadDerivedColorKeys();

		Object baseKey = derivedColorKeys.get( key );
		if( baseKey == null )
			return color;

		// this is for keys that may be defined as derived colors, but do not derive them at runtime
		if( "null".equals( baseKey ) )
			return color;

		Color baseColor = defaults.getColor( baseKey );
		if( baseColor == null )
			return color;

		if( baseColor instanceof DerivedColor )
			baseColor = resolveDerivedColor( defaults, (String) baseKey, baseColor, null );

		if( pBaseColor != null )
			pBaseColor[0] = baseColor;

		Color newColor = FlatUIUtils.deriveColor( color, baseColor );

		// creating a new color instance to drop Color.frgbvalue from newColor
		// and avoid rounding issues/differences
		return new Color( newColor.getRGB(), true );
	}

	private Properties loadDerivedColorKeys() {
		String name = "/com/formdev/flatlaf/extras/resources/DerivedColorKeys.properties";
		Properties properties = new Properties();
		try( InputStream in = getClass().getResourceAsStream( name ) ) {
			if( in != null )
				properties.load( in );
		} catch( IOException ex ) {
			LoggingFacade.INSTANCE.logSevere( "FlatLaf: Failed to load '" + name + "'.", ex );
		}
		return properties;
	}

	private static void updateWindowTitle( JFrame frame ) {
		String title = frame.getTitle();
		String sep = "  -  ";
		int sepIndex = title.indexOf( sep );
		if( sepIndex >= 0 )
			title = title.substring( 0, sepIndex );
		frame.setTitle( title + sep + UIManager.getLookAndFeel().getName() );
	}

	private void saveWindowBounds( JFrame frame ) {
		Preferences prefs = getPrefs();
		prefs.putInt( "x", frame.getX() );
		prefs.putInt( "y", frame.getY() );
		prefs.putInt( "width", frame.getWidth() );
		prefs.putInt( "height", frame.getHeight() );
	}

	private Preferences getPrefs() {
		return Preferences.userRoot().node( "flatlaf-uidefaults-inspector" );
	}

	private void filterChanged() {
		String filter = filterField.getText().trim();
		String valueType = (String) valueTypeField.getSelectedItem();

		// split filter string on space characters
		String[] filters = !filter.isEmpty() ? filter.split( " +" ) : null;
		Pattern[] patterns = (filters != null) ? new Pattern[filters.length] : null;
		if( filters != null ) {
			for( int i = 0; i < filters.length; i++ ) {
				filters[i] = filters[i].toLowerCase( Locale.ENGLISH );

				// simple wildcard matching
				//  - '*' matches any number of characters
				//  - '?' matches a single character
				//  - '^' beginning of line
				//  - '$' end of line
				String f = filters[i];
				boolean matchBeginning = f.startsWith( "^" );
				boolean matchEnd = f.endsWith( "$" );
				if( f.indexOf( '*' ) >= 0 || f.indexOf( '?' ) >= 0 || matchBeginning || matchEnd ) {
					if( matchBeginning )
						f = f.substring( 1 );
					if( matchEnd )
						f = f.substring( 0, f.length() - 1 );

					String regex = ("\\Q" + f + "\\E").replace( "*", "\\E.*\\Q" ).replace( "?", "\\E.\\Q" );
					if( !matchBeginning )
						regex = ".*" + regex;
					if( !matchEnd )
						regex = regex + ".*";
					patterns[i] = Pattern.compile( regex );
				}
			}
		}

		ItemsTableModel model = (ItemsTableModel) table.getModel();
		model.setFilter( item -> {
			if( valueType != null &&
				!valueType.equals( "(any)" ) &&
				!typeOfValue( item.value ).startsWith( valueType ) )
			  return false;

			if( filters == null )
				return true;

			String lkey = item.key.toLowerCase( Locale.ENGLISH );
			String lvalue = item.getValueAsString().toLowerCase( Locale.ENGLISH );
			for( int i = 0; i < filters.length; i++ ) {
				Pattern p = patterns[i];
				if( p != null ) {
					if( p.matcher( lkey ).matches() || p.matcher( lvalue ).matches() )
						return true;
				} else {
					String f = filters[i];
					if( lkey.contains( f ) || lvalue.contains( f ) )
						return true;
				}
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
		if( value instanceof Color ) {
			if( ((Color)value).getAlpha() != 255 )
				return "Color (\u03b1)";
			if( value instanceof DerivedColor )
				return "Color (\u0192)";
			return "Color";
		}
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

	private void tableMousePressed( MouseEvent e ) {
		if( !SwingUtilities.isRightMouseButton( e ) )
			return;

		int row = table.rowAtPoint( e.getPoint() );
		if( row >= 0 && !table.isRowSelected( row ) )
			table.setRowSelectionInterval( row, row );
	}

	private void copyKey() {
		copyToClipboard( 0 );
	}

	private void copyValue() {
		copyToClipboard( 1 );
	}

	private void copyKeyAndValue() {
		copyToClipboard( -1 );
	}

	private void copyToClipboard( int column ) {
		int[] rows = table.getSelectedRows();
		if( rows.length == 0 )
			return;

		StringBuilder buf = new StringBuilder();
		for( int i = 0; i < rows.length; i++ ) {
			if( i > 0 )
				buf.append( '\n' );

			if( column < 0 || column == 0 )
				buf.append( table.getValueAt( rows[i], 0 ) );
			if( column < 0 )
				buf.append( " = " );
			if( column < 0 || column == 1 )
				buf.append( table.getValueAt( rows[i], 1 ) );
		}

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
			new StringSelection( buf.toString() ), null );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel = new JPanel();
		filterPanel = new JPanel();
		filterLabel = new JLabel();
		filterField = new FlatTextField();
		valueTypeLabel = new JLabel();
		valueTypeField = new JComboBox<>();
		scrollPane = new JScrollPane();
		table = new JTable();
		tablePopupMenu = new JPopupMenu();
		copyKeyMenuItem = new JMenuItem();
		copyValueMenuItem = new JMenuItem();
		copyKeyAndValueMenuItem = new JMenuItem();

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

				//---- filterLabel ----
				filterLabel.setText("Filter:");
				filterLabel.setLabelFor(filterField);
				filterLabel.setDisplayedMnemonic('F');
				filterPanel.add(filterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 10), 0, 0));

				//---- filterField ----
				filterField.setPlaceholderText("enter one or more filter strings, separated by space characters");
				filterField.setShowClearButton(true);
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
					"Color (\u03b1)",
					"Color (\u0192)",
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
				table.setComponentPopupMenu(tablePopupMenu);
				table.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						tableMousePressed(e);
					}
				});
				scrollPane.setViewportView(table);
			}
			panel.add(scrollPane, BorderLayout.CENTER);
		}

		//======== tablePopupMenu ========
		{

			//---- copyKeyMenuItem ----
			copyKeyMenuItem.setText("Copy Key");
			copyKeyMenuItem.addActionListener(e -> copyKey());
			tablePopupMenu.add(copyKeyMenuItem);

			//---- copyValueMenuItem ----
			copyValueMenuItem.setText("Copy Value");
			copyValueMenuItem.addActionListener(e -> copyValue());
			tablePopupMenu.add(copyValueMenuItem);

			//---- copyKeyAndValueMenuItem ----
			copyKeyAndValueMenuItem.setText("Copy Key and Value");
			copyKeyAndValueMenuItem.addActionListener(e -> copyKeyAndValue());
			tablePopupMenu.add(copyKeyAndValueMenuItem);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel;
	private JPanel filterPanel;
	private JLabel filterLabel;
	private FlatTextField filterField;
	private JLabel valueTypeLabel;
	private JComboBox<String> valueTypeField;
	private JScrollPane scrollPane;
	private JTable table;
	private JPopupMenu tablePopupMenu;
	private JMenuItem copyKeyMenuItem;
	private JMenuItem copyValueMenuItem;
	private JMenuItem copyKeyAndValueMenuItem;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class Item ---------------------------------------------------------

	private static class Item {
		final String key;
		final Object value;
		final Object lafValue;
		final Object info;

		private String valueStr;

		Item( String key, Object value, Object lafValue, Object info ) {
			this.key = key;
			this.value = value;
			this.lafValue = lafValue;
			this.info = info;
		}

		String getValueAsString() {
			if( valueStr == null )
				valueStr = valueAsString( value, info );
			return valueStr;
		}

		static String valueAsString( Object value, Object info ) {
			if( value instanceof Color ) {
				Color color = (info instanceof Color[]) ? ((Color[])info)[0] : (Color) value;
				HSLColor hslColor = new HSLColor( color );
				int hue = Math.round( hslColor.getHue() );
				int saturation = Math.round( hslColor.getSaturation() );
				int luminance = Math.round( hslColor.getLuminance() );
				if( color.getAlpha() == 255 ) {
					return String.format( "%-9s HSL %3d %3d %3d",
						color2hex( color ), hue, saturation, luminance );
				} else {
					int alpha = Math.round( hslColor.getAlpha() * 100 );
					return String.format( "%-9s HSL %3d %3d %3d %2d",
						color2hex( color ), hue, saturation, luminance, alpha );
				}
			} else if( value instanceof Insets ) {
				Insets insets = (Insets) value;
				return insets.top + "," + insets.left + "," + insets.bottom + "," + insets.right;
			} else if( value instanceof Dimension ) {
				Dimension dim = (Dimension) value;
				return dim.width + "," + dim.height;
			} else if( value instanceof Font ) {
				Font font = (Font) value;
				String s = font.getFamily() + " " + font.getSize();
				if( font.isBold() )
					s += " bold";
				if( font.isItalic() )
					s += " italic";
				return s;
			} else if( value instanceof Icon ) {
				Icon icon = (Icon) value;
				return icon.getIconWidth() + "x" + icon.getIconHeight() + "   " + icon.getClass().getName();
			} else if( value instanceof Border ) {
				Border border = (Border) value;
				if( border instanceof FlatLineBorder ) {
					FlatLineBorder lineBorder = (FlatLineBorder) border;
					return valueAsString( lineBorder.getUnscaledBorderInsets(), null )
						+ "  " + color2hex( lineBorder.getLineColor() )
						+ "  " + lineBorder.getLineThickness()
						+ "    " + border.getClass().getName();
				} else if( border instanceof EmptyBorder ) {
					Insets insets = (border instanceof FlatEmptyBorder)
						? ((FlatEmptyBorder)border).getUnscaledBorderInsets()
						: ((EmptyBorder)border).getBorderInsets();
					return valueAsString( insets, null ) + "    " + border.getClass().getName();
				} else if( border instanceof FlatBorder || border instanceof FlatMarginBorder )
					return border.getClass().getName();
				else
					return String.valueOf( value );
			} else if( value instanceof GrayFilter ) {
				GrayFilter grayFilter = (GrayFilter) value;
				return grayFilter.getBrightness() + "," + grayFilter.getContrast()
					+ " " + grayFilter.getAlpha() + "    " + grayFilter.getClass().getName();
			} else if( value instanceof ActionMap ) {
				ActionMap actionMap = (ActionMap) value;
				return "ActionMap (" + actionMap.size() + ")";
			} else if( value instanceof InputMap ) {
				InputMap inputMap = (InputMap) value;
				return "InputMap (" + inputMap.size() + ")";
			} else if( value instanceof Object[] )
				return Arrays.toString( (Object[]) value );
			else if( value instanceof int[] )
				return Arrays.toString( (int[]) value );
			else
				return String.valueOf( value );
		}

		@SuppressWarnings( "FormatString" ) // Error Prone
		private static String color2hex( Color color ) {
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

		protected String layoutLabel( FontMetrics fm, String text, Rectangle textR ) {
			int width = getWidth();
			int height = getHeight();
			Insets insets = getInsets();

			Rectangle viewR = new Rectangle( insets.left, insets.top,
				width - (insets.left + insets.right),
				height - (insets.top + insets.bottom) );
			Rectangle iconR = new Rectangle();

			return SwingUtilities.layoutCompoundLabel( this, fm, text, null,
				getVerticalAlignment(), getHorizontalAlignment(),
				getVerticalTextPosition(), getHorizontalTextPosition(),
				viewR, iconR, textR, getIconTextGap() );
		}
	}

	//---- class KeyRenderer --------------------------------------------------

	private static class KeyRenderer
		extends Renderer
	{
		private String key;
		private boolean isOverridden;
		private Icon overriddenIcon;

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column )
		{
			key = (String) value;
			init( table, key, isSelected, row );

			Item item = (Item) table.getValueAt( row, 1 );
			isOverridden = (item.lafValue != null);

			// set tooltip
			String toolTipText = key;
			if( isOverridden )
				toolTipText += "    \n\nLaF UI default value was overridden with UIManager.put(key,value).";
			setToolTipText( toolTipText );

			return super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
		}

		@Override
		protected void paintComponent( Graphics g ) {
			g.setColor( getBackground() );
			g.fillRect( 0, 0, getWidth(), getHeight() );

			FontMetrics fm = getFontMetrics( getFont() );
			Rectangle textR = new Rectangle();
			String clippedText = layoutLabel( fm, key, textR );
			int x = textR.x;
			int y = textR.y + fm.getAscent();

			int dot = key.indexOf( '.' );
			if( dot > 0 && !selected ) {
				g.setColor( FlatUIUtils.getUIColor( "Label.disabledForeground",
					FlatUIUtils.getUIColor( "Label.disabledText", Color.gray ) ) );

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

			if( isOverridden ) {
				if( overriddenIcon == null ) {
					overriddenIcon = new FlatAbstractIcon( 16, 16, null ) {
						@Override
						protected void paintIcon( Component c, Graphics2D g2 ) {
							g2.setColor( FlatUIUtils.getUIColor( "Actions.Red", Color.red ) );
							g2.setStroke( new BasicStroke( 2f ) );
							g2.draw( FlatUIUtils.createPath( false, 3,10, 8,5, 13,10 ) );
						}
					};
				}

				overriddenIcon.paintIcon( this, g,
					getWidth() - overriddenIcon.getIconWidth(),
					(getHeight() - overriddenIcon.getIconHeight()) / 2 );
			}

			paintSeparator( g );
		}
	}

	//---- class ValueRenderer ------------------------------------------------

	private static class ValueRenderer
		extends Renderer
	{
		private Item item;

		// used instead of getBackground() because this did not work in some 3rd party Lafs
		private Color valueColor;

		@Override
		public Component getTableCellRendererComponent( JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column )
		{
			item = (Item) value;
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
				Color color = (item.info instanceof Color[]) ? ((Color[])item.info)[0] : (Color) item.value;
				boolean isDark = new HSLColor( color ).getLuminance() < 70 && color.getAlpha() >= 128;
				valueColor = color;
				setForeground( isDark ? Color.white : Color.black );
			} else if( item.value instanceof Icon ) {
				Icon icon = (Icon) item.value;
				setIcon( new SafeIcon( icon ) );
			}

			// set tooltip
			String toolTipText = (item.value instanceof Object[])
				? Arrays.toString( (Object[]) item.value ).replace( ", ", ",\n" )
				: String.valueOf( item.value );
			if( item.lafValue != null ) {
				toolTipText += "    \n\nLaF UI default value was overridden with UIManager.put(key,value):\n    "
					+ Item.valueAsString( item.lafValue, null ) + "\n    " + String.valueOf( item.lafValue );
			}
			setToolTipText( toolTipText );

			return this;
		}

		@Override
		protected void paintComponent( Graphics g ) {
			if( item.value instanceof Color ) {
				int width = getWidth();
				int height = getHeight();
				Color background = valueColor;

				// paint color
				fillRect( g, background, 0, 0, width, height );

				if( item.info instanceof Color[] ) {
					// paint base color
					int width2 = height * 2;
					fillRect( g, ((Color[])item.info)[1], width - width2, 0, width2, height );

					// paint default color
					Color defaultColor = (Color) item.value;
					if( defaultColor != null && !defaultColor.equals( background ) ) {
						int width3 = height / 2;
						fillRect( g, defaultColor, width - width3, 0, width3, height );
					}

					// paint "derived color" indicator
					int width4 = height / 4;
					g.setColor( Color.magenta );
					g.fillRect( width - width4, 0, width4, height );
				}

				// layout text
				FontMetrics fm = getFontMetrics( getFont() );
				String text = getText();
				Rectangle textR = new Rectangle();
				layoutLabel( fm, text, textR );
				int x = textR.x;
				int y = textR.y + fm.getAscent();

				g.setColor( getForeground() );

				// paint hsl horizontally aligned
				int hslIndex = text.indexOf( "HSL" );
				if( hslIndex > 0 ) {
					String hexText = text.substring( 0, hslIndex );
					String hslText = text.substring( hslIndex );
					int hexWidth = Math.max( fm.stringWidth( hexText ), fm.stringWidth( "#12345678  " ) );
					FlatUIUtils.drawString( this, g, hexText, x, y );
					FlatUIUtils.drawString( this, g, hslText, x + hexWidth, y );
				} else
					FlatUIUtils.drawString( this, g, text, x, y );
			} else
				super.paintComponent( g );

			paintSeparator( g );
		}

		private void fillRect( Graphics g, Color color, int x, int y, int width, int height ) {
			// fill white if color is translucent
			if( color.getAlpha() != 255 ) {
				g.setColor( Color.white );
				g.fillRect( x, y, width, height );
			}

			g.setColor( color );
			g.fillRect( x, y, width, height );
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
			int width = getIconWidth();
			int height = getIconHeight();

			try {
				g.setColor( UIManager.getColor( "Panel.background" ) );
				g.fillRect( x, y, width, height );

				icon.paintIcon( c, g, x, y );
			} catch( Exception ex ) {
				g.setColor( Color.red );
				g.drawRect( x, y, width - 1, height - 1 );
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
