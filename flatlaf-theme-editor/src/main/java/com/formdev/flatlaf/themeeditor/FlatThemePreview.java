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
import java.awt.event.HierarchyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.prefs.Preferences;
import javax.swing.*;
import javax.swing.UIDefaults.ActiveValue;
import javax.swing.UIDefaults.LazyValue;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;
import com.formdev.flatlaf.extras.components.*;

/**
 * @author Karl Tauber
 */
class FlatThemePreview
	extends JPanel
	implements DocumentListener
{
	private static final String KEY_SELECTED_TAB = "preview.selectedTab";

	private final FlatSyntaxTextArea textArea;
	private final Timer timer;
	final Preferences state;

	private final FlatThemePreviewAll allTab;
	private final FlatThemePreviewButtons buttonsTab;
	private final FlatThemePreviewSwitches switchesTab;
	private final FlatThemePreviewFonts fontsTab;

	private final Map<LazyValue, Object> lazyValueCache = new WeakHashMap<>();
	private int runWithUIDefaultsGetterLevel;
	private boolean inGetDefaultFont;
	private boolean inGetVariables;

	FlatThemePreview( FlatSyntaxTextArea textArea ) {
		this.textArea = textArea;
		state = Preferences.userRoot().node( FlatThemeFileEditor.PREFS_ROOT_PATH );

		initComponents();

		// add tabs
		allTab = new FlatThemePreviewAll( this );
		buttonsTab = new FlatThemePreviewButtons( this );
		switchesTab = new FlatThemePreviewSwitches( this );
		fontsTab = new FlatThemePreviewFonts();
		tabbedPane.addTab( "All", createPreviewTab( allTab ) );
		tabbedPane.addTab( "Buttons", createPreviewTab( buttonsTab ) );
		tabbedPane.addTab( "Switches", createPreviewTab( switchesTab ) );
		tabbedPane.addTab( "Fonts", createPreviewTab( fontsTab ) );
		selectRecentTab();
		tabbedPane.addChangeListener( e -> selectedTabChanged() );

		// timer used for delayed preview updates
		timer = new Timer( 300, e -> update() );
		timer.setRepeats( false );

		// listen to changes in text area to automatically update preview
		textArea.getDocument().addDocumentListener( this );

		// update when showing preview (e.g. activating tab)
		addHierarchyListener( e -> {
			if( (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing() )
				selectRecentTab();
				updateLater();
		} );
	}

	private JScrollPane createPreviewTab( JComponent c ) {
		JScrollPane scrollPane = new JScrollPane( new PreviewPanel( c ) );
		scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPane.setBorder( BorderFactory.createEmptyBorder() );
		scrollPane.getVerticalScrollBar().setUnitIncrement( 20 );
		scrollPane.getHorizontalScrollBar().setUnitIncrement( 20 );
		return scrollPane;
	}

	private void selectRecentTab() {
		int selectedTab = state.getInt( KEY_SELECTED_TAB, -1 );
		if( selectedTab >= 0 && selectedTab < tabbedPane.getTabCount() ) {
			tabbedPane.setSelectedIndex( selectedTab );

			switch( selectedTab ) {
				case 0: allTab.activated(); break;
				case 1: buttonsTab.activated(); break;
				case 2: switchesTab.activated(); break;
			}
		}
	}

	private void selectedTabChanged() {
		update();
		state.putInt( KEY_SELECTED_TAB, tabbedPane.getSelectedIndex() );
	}

	@Override
	public void insertUpdate( DocumentEvent e ) {
		timer.restart();
	}

	@Override
	public void removeUpdate( DocumentEvent e ) {
		timer.restart();
	}

	@Override
	public void changedUpdate( DocumentEvent e ) {
	}

	void updateLater() {
		EventQueue.invokeLater( this::update );
	}

	private void update() {
		if( !isShowing() )
			return;

		runWithUIDefaultsGetter( this::updateComponentTreeUI );
	}

	private void updateComponentTreeUI() {
		try {
			Component selComp = tabbedPane.getSelectedComponent();
			if( selComp != null ) {
				if( selComp instanceof JScrollPane )
					selComp = ((JScrollPane)selComp).getViewport().getView();
				SwingUtilities.updateComponentTreeUI( selComp );
			}
		} catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	void runWithUIDefaultsGetter( Runnable runnable ) {
		try {
			runWithUIDefaultsGetterLevel++;
			if( runWithUIDefaultsGetterLevel == 1 )
				FlatLaf.runWithUIDefaultsGetter( this::getUIDefaultProperty, runnable );
			else
				runnable.run();
		} finally {
			runWithUIDefaultsGetterLevel--;
		}
	}

	@SuppressWarnings( "unchecked" )
	Object getUIDefaultProperty( Object key ) {
		// avoid StackOverflowError because "defaultFont" value is an active value
		// that itself uses UIManager.getFont( "defaultFont" ) to get base font
		if( inGetDefaultFont )
			return null;

		if( !(key instanceof String) )
			return null;

		// ignore custom UI delegates for preview because those classes
		// are not available in theme editor
		if( ((String)key).endsWith( "UI" ) )
			return null;

		// handle access to variables map, which is used in styles
		if( UIDefaultsLoaderAccessor.KEY_VARIABLES.equals( key ) ) {
			if( inGetVariables )
				return null;

			inGetVariables = true;
			try {
				return new VariablesDelegateMap( (Map<String, String>) UIManager.get( UIDefaultsLoaderAccessor.KEY_VARIABLES ) );
			} finally {
				inGetVariables = false;
			}
		}

		Object value = textArea.propertiesSupport.getParsedProperty( (String) key );

		boolean isDefaultFont = "defaultFont".equals( key );
		inGetDefaultFont = isDefaultFont;
		try {
			if( value instanceof LazyValue ) {
				value = lazyValueCache.computeIfAbsent( (LazyValue) value, k -> {
					return k.createValue( null );
				} );
			} else if( value instanceof ActiveValue )
				value = ((ActiveValue)value).createValue( null );
		} finally {
			inGetDefaultFont = false;
		}

//		System.out.println( key + " = " + value );

		// for "defaultFont" never return a value that is not a font
		// (e.g. a color for "defaultFont = #fff") to avoid StackOverflowError
		// in Active.createValue()
		if( isDefaultFont && !(value instanceof Font) )
			return null;

		// If value is null and is a property that is defined in a core theme,
		// then force the value to null.
		// This is necessary for cases where the current application Laf defines a property
		// but the edited theme does not (or has set the value explicitly to null).
		// E.g. FlatLightLaf defines Button.focusedBackground, but in FlatDarkLaf
		// it is not defined. Without this code, the preview for FlatDarkLaf would use
		// Button.focusedBackground from FlatLightLaf if FlatLightLaf is the current application Laf.
		if( value == null && FlatThemePropertiesBaseManager.getDefindedCoreKeys().contains( key ) && !isDefaultFont )
			return FlatLaf.NULL_VALUE;

		return value;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		tabbedPane = new FlatTabbedPane();
		previewSeparator = new JSeparator();
		previewLabel = new JLabel();

		//======== this ========
		setLayout(new BorderLayout());

		//======== tabbedPane ========
		{
			tabbedPane.setLeadingComponent(previewLabel);
			tabbedPane.setTabAreaAlignment(FlatTabbedPane.TabAreaAlignment.trailing);
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		}
		add(tabbedPane, BorderLayout.CENTER);

		//---- previewSeparator ----
		previewSeparator.setOrientation(SwingConstants.VERTICAL);
		add(previewSeparator, BorderLayout.LINE_START);

		//---- previewLabel ----
		previewLabel.setText(" Preview ");
		previewLabel.putClientProperty(FlatClientProperties.STYLE_CLASS, "h2");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private FlatTabbedPane tabbedPane;
	private JSeparator previewSeparator;
	private JLabel previewLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	//---- class PreviewPanel -------------------------------------------------

	private class PreviewPanel
		extends JPanel
	{
		PreviewPanel( JComponent c ) {
			super( new BorderLayout() );
			add( c );
		}

		@SuppressWarnings( "deprecation" )
		@Override
		public void layout() {
			try {
				runWithUIDefaultsGetter( () -> {
					super.layout();
				} );
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		protected void validateTree() {
			try {
				runWithUIDefaultsGetter( () -> {
					super.validateTree();
				} );
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		public Dimension getPreferredSize() {
			try {
				return super.getPreferredSize();
			} catch( Exception ex ) {
				ex.printStackTrace();
				return new Dimension( 100, 100 );
			}
		}

		@Override
		public Dimension getMinimumSize() {
			try {
				return super.getMinimumSize();
			} catch( Exception ex ) {
				ex.printStackTrace();
				return new Dimension( 100, 100 );
			}
		}

		@Override
		public Dimension getMaximumSize() {
			try {
				return super.getMaximumSize();
			} catch( Exception ex ) {
				ex.printStackTrace();
				return new Dimension( Short.MAX_VALUE, Short.MAX_VALUE );
			}
		}

		@Override
		public void paint( Graphics g ) {
			try {
				runWithUIDefaultsGetter( () -> {
					super.paint( g );
				} );
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		protected void paintComponent( Graphics g ) {
			try {
				runWithUIDefaultsGetter( () -> {
					super.paintComponent( g );
				} );
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		protected void paintChildren( Graphics g ) {
			try {
				runWithUIDefaultsGetter( () -> {
					super.paintChildren( g );
				} );
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	}

	//---- class VariablesDelegateMap -----------------------------------------

	private class VariablesDelegateMap
		extends HashMap<String, String>
	{
		private final Map<String, String> variables;

		public VariablesDelegateMap( Map<String, String> variables ) {
			this.variables = variables;
		}

		@Override
		public String get( Object key ) {
			String value = textArea.propertiesSupport.getProperty( (String) key );
			if( value != null )
				return value;

			if( variables != null ) {
				value = variables.get( key );
				if( value != null )
					return value;
			}

			return super.get( key );
		}
	}
}
