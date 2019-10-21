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

package com.formdev.flatlaf.ui;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JComboBox}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Component.focusWidth				int
 * @uiDefault Component.arc						int
 * @uiDefault Component.arrowType				String	triangle (default) or chevron
 * @uiDefault Component.borderColor				Color
 * @uiDefault Component.disabledBorderColor		Color
 * @uiDefault ComboBox.disabledBackground		Color
 * @uiDefault ComboBox.disabledForeground		Color
 * @uiDefault ComboBox.buttonBackground			Color
 * @uiDefault ComboBox.buttonEditableBackground	Color
 * @uiDefault ComboBox.buttonArrowColor			Color
 * @uiDefault ComboBox.buttonDisabledArrowColor	Color
 * @uiDefault ComboBox.buttonHoverArrowColor	Color
 *
 * @author Karl Tauber
 */
public class FlatComboBoxUI
	extends BasicComboBoxUI
{
	protected int focusWidth;
	protected int arc;
	protected String arrowType;
	protected Color borderColor;
	protected Color disabledBorderColor;

	protected Color disabledBackground;
	protected Color disabledForeground;

	protected Color buttonBackground;
	protected Color buttonEditableBackground;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;
	protected Color buttonHoverArrowColor;

	private MouseListener hoverListener;
	private boolean hover;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatComboBoxUI();
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		hoverListener = new FlatUIUtils.HoverListener( null, h -> {
			if( !comboBox.isEditable() ) {
				hover = h;
				if( arrowButton != null )
					arrowButton.repaint();
			}
		} );
		comboBox.addMouseListener( hoverListener );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		comboBox.removeMouseListener( hoverListener );
		hoverListener = null;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		focusWidth = UIManager.getInt( "Component.focusWidth" );
		arc = UIManager.getInt( "Component.arc" );
		arrowType = UIManager.getString( "Component.arrowType" );
		borderColor = UIManager.getColor( "Component.borderColor" );
		disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );

		disabledBackground = UIManager.getColor( "ComboBox.disabledBackground" );
		disabledForeground = UIManager.getColor( "ComboBox.disabledForeground" );

		buttonBackground = UIManager.getColor( "ComboBox.buttonBackground" );
		buttonEditableBackground = UIManager.getColor( "ComboBox.buttonEditableBackground" );
		buttonArrowColor = UIManager.getColor( "ComboBox.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "ComboBox.buttonDisabledArrowColor" );
		buttonHoverArrowColor = UIManager.getColor( "ComboBox.buttonHoverArrowColor" );

		// scale
		padding = UIScale.scale( padding );

		MigLayoutVisualPadding.install( comboBox, focusWidth );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		borderColor = null;
		disabledBorderColor = null;

		disabledBackground = null;
		disabledForeground = null;

		buttonBackground = null;
		buttonEditableBackground = null;
		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		buttonHoverArrowColor = null;

		MigLayoutVisualPadding.uninstall( comboBox );
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new BasicComboBoxUI.ComboBoxLayoutManager() {
			@Override
			public void layoutContainer( Container parent ) {
				super.layoutContainer( parent );

				if ( editor != null && padding != null ) {
					// fix editor bounds by subtracting padding
					editor.setBounds( FlatUIUtils.subtractInsets( editor.getBounds(), padding ) );
				}
			}
		};
	}

	@Override
	protected FocusListener createFocusListener() {
		// repaint combobox to update focus border
		return new BasicComboBoxUI.FocusHandler() {
			@Override
			public void focusGained( FocusEvent e ) {
				super.focusGained( e );
				if( comboBox != null && comboBox.isEditable() )
					comboBox.repaint();
			}

			@Override
			public void focusLost( FocusEvent e ) {
				super.focusLost( e );
				if( comboBox != null && comboBox.isEditable() )
					comboBox.repaint();
			}
		};
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		return new BasicComboBoxUI.PropertyChangeHandler() {
			@Override
			public void propertyChange( PropertyChangeEvent e ) {
				super.propertyChange( e );

				Object source = e.getSource();
				String propertyName = e.getPropertyName();

				if( editor != null &&
					((source == comboBox && (propertyName == "background" || propertyName == "foreground")) ||
					 (source == editor && propertyName == "enabled")) )
				{
					// fix editor component colors
					updateEditorColors();
				}
			}
		};
	}

	@Override
	protected ComboPopup createPopup() {
		return new FlatComboPopup( comboBox );
	}

	@Override
	protected void configureEditor() {
		super.configureEditor();

		// assign a non-javax.swing.plaf.UIResource border to the text field,
		// otherwise it is replaced with default text field border when switching LaF
		// because javax.swing.plaf.basic.BasicComboBoxEditor.BorderlessTextField.setBorder()
		// uses "border instanceof javax.swing.plaf.basic.BasicComboBoxEditor.UIResource"
		// instead of "border instanceof javax.swing.plaf.UIResource"
		if( editor instanceof JTextComponent )
			((JTextComponent)editor).setBorder( BorderFactory.createEmptyBorder() );

		updateEditorColors();
	}

	private void updateEditorColors() {
		// use non-UIResource colors because when SwingUtilities.updateComponentTreeUI()
		// is used, then the editor is updated after the combobox and the
		// colors are again replaced with default colors
		boolean enabled = editor.isEnabled();
		editor.setBackground( FlatUIUtils.nonUIResource( enabled ? comboBox.getBackground() : disabledBackground ) );
		editor.setForeground( FlatUIUtils.nonUIResource( (enabled || editor instanceof JTextComponent)
			? comboBox.getForeground()
			: disabledForeground ) );
		if( editor instanceof JTextComponent )
			((JTextComponent)editor).setDisabledTextColor( FlatUIUtils.nonUIResource( disabledForeground ) );
	}

	@Override
	protected JButton createArrowButton() {
		return new FlatArrowButton( SwingConstants.SOUTH, arrowType, buttonArrowColor,
			buttonDisabledArrowColor, buttonHoverArrowColor, null )
		{
			@Override
			protected boolean isHover() {
				return super.isHover() || (!comboBox.isEditable() ? hover : false);
			}
		};
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		if( c.isOpaque() ) {
			FlatUIUtils.paintParentBackground( g, c );

			Graphics2D g2 = (Graphics2D) g;
			FlatUIUtils.setRenderingHints( g2 );

			int width = c.getWidth();
			int height = c.getHeight();
			float focusWidth = (c.getBorder() instanceof FlatBorder) ? scale( (float) this.focusWidth ) : 0;
			float arc = (c.getBorder() instanceof FlatRoundBorder) ? scale( (float) this.arc ) : 0;
			int arrowX = arrowButton.getX();
			int arrowWidth = arrowButton.getWidth();
			boolean enabled = comboBox.isEnabled();
			boolean isLeftToRight = comboBox.getComponentOrientation().isLeftToRight();

			// paint background
			g2.setColor( enabled ? c.getBackground() : disabledBackground );
			FlatUIUtils.fillRoundRectangle( g2, 0, 0, width, height, focusWidth, arc );

			// paint arrow button background
			if( enabled ) {
				g2.setColor( comboBox.isEditable() ? buttonEditableBackground : buttonBackground );
				Shape oldClip = g2.getClip();
				if( isLeftToRight )
					g2.clipRect( arrowX, 0, width - arrowX, height );
				else
					g2.clipRect( 0, 0, arrowX + arrowWidth, height );
				FlatUIUtils.fillRoundRectangle( g2, 0, 0, width, height, focusWidth, arc );
				g2.setClip( oldClip );
			}

			if( comboBox.isEditable() ) {
				// paint vertical line between value and arrow button
				g2.setColor( enabled ? borderColor : disabledBorderColor );
				float lw = scale( 1f );
				float lx = isLeftToRight ? arrowX : arrowX + arrowWidth - lw;
				g2.fill( new Rectangle2D.Float( lx, focusWidth, lw, height - (focusWidth * 2) ) );
			}
		}

		paint( g, c );
	}

	@Override
	@SuppressWarnings( "unchecked" )
	public void paintCurrentValue( Graphics g, Rectangle bounds, boolean hasFocus ) {
		ListCellRenderer<Object> renderer = comboBox.getRenderer();
		CellPaddingBorder.uninstall( renderer );
		Component c = renderer.getListCellRendererComponent( listBox, comboBox.getSelectedItem(), -1, false, false );
		c.setFont( comboBox.getFont() );
		CellPaddingBorder.uninstall( c );

		boolean enabled = comboBox.isEnabled();
		c.setForeground( enabled ? comboBox.getForeground() : disabledForeground );
		c.setBackground( enabled ? comboBox.getBackground() : disabledBackground );

		boolean shouldValidate = (c instanceof JPanel);
		if( padding != null )
			bounds = FlatUIUtils.subtractInsets( bounds, padding );

		// increase the size of the rendering area to make sure that the text
		// is vertically aligned with other component types (e.g. JTextField)
		Insets rendererInsets = getRendererComponentInsets( c );
		if( rendererInsets != null )
			bounds = FlatUIUtils.addInsets( bounds, rendererInsets );

		currentValuePane.paintComponent( g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate );
	}

	@Override
	public void paintCurrentValueBackground( Graphics g, Rectangle bounds, boolean hasFocus ) {
		g.setColor( comboBox.isEnabled() ? comboBox.getBackground() : disabledBackground );
		g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
	}

	@Override
	protected Dimension getSizeForComponent( Component comp ) {
		Dimension size = super.getSizeForComponent( comp );

		// remove the renderer border top/bottom insets from the size to make sure that
		// the combobox gets the same height as other component types (e.g. JTextField)
		Insets rendererInsets = getRendererComponentInsets( comp );
		if( rendererInsets != null )
			size = new Dimension( size.width, size.height - rendererInsets.top - rendererInsets.bottom );

		return size;
	}

	private Insets getRendererComponentInsets( Component rendererComponent ) {
		if( rendererComponent instanceof JComponent ) {
			Border rendererBorder = ((JComponent)rendererComponent).getBorder();
			if( rendererBorder != null )
				return rendererBorder.getBorderInsets( rendererComponent );
		}

		return null;
	}

	//---- class FlatComboPopup -----------------------------------------------

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	private class FlatComboPopup
		extends BasicComboPopup
	{
		private CellPaddingBorder paddingBorder;

		FlatComboPopup( JComboBox combo ) {
			super( combo );
		}

		@Override
		protected Rectangle computePopupBounds( int px, int py, int pw, int ph ) {
			// get maximum display size of all items, ignoring prototype value
			Object prototype = comboBox.getPrototypeDisplayValue();
			if( prototype != null )
				comboBox.setPrototypeDisplayValue( null );
			Dimension displaySize = getDisplaySize();
			if( prototype != null )
				comboBox.setPrototypeDisplayValue( prototype );

			// make popup wider if necessary
			pw = Math.max( pw, displaySize.width );

			return super.computePopupBounds( px, py, pw, ph );
		}

		@Override
		protected void configurePopup() {
			super.configurePopup();

			Border border = UIManager.getBorder( "PopupMenu.border" );
			if( border != null )
				setBorder( border );
		}

		@Override
		protected void configureList() {
			super.configureList();

			list.setCellRenderer( new PopupListCellRenderer() );
		}

		@Override
		protected PropertyChangeListener createPropertyChangeListener() {
			return new BasicComboPopup.PropertyChangeHandler() {
				@Override
				public void propertyChange( PropertyChangeEvent e ) {
					super.propertyChange( e );

					if( e.getPropertyName() == "renderer" )
						list.setCellRenderer( new PopupListCellRenderer() );
				}
			};
		}

		//---- class PopupListCellRenderer -----

		private class PopupListCellRenderer
			implements ListCellRenderer
		{
			@Override
			public Component getListCellRendererComponent( JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus )
			{
				ListCellRenderer renderer = comboBox.getRenderer();
				CellPaddingBorder.uninstall( renderer );

				Component c = renderer.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );

				if( c instanceof JComponent ) {
					if( paddingBorder == null )
						paddingBorder = new CellPaddingBorder( padding );
					paddingBorder.install( (JComponent) c );
				}

				return c;
			}
		}
	}

	//---- class CellPaddingBorder --------------------------------------------

	/**
	 * Cell padding border used only in popup list.
	 *
	 * The insets are the union of the cell padding and the renderer border insets,
	 * which vertically aligns text in popup list with text in combobox.
	 *
	 * The renderer border is painted on the outside of this border.
	 */
	private static class CellPaddingBorder
		extends AbstractBorder
	{
		private final Insets padding;
		private Border rendererBorder;

		CellPaddingBorder( Insets padding ) {
			this.padding = padding;
		}

		void install( JComponent rendererComponent ) {
			Border oldBorder = rendererComponent.getBorder();
			if( !(oldBorder instanceof CellPaddingBorder) ) {
				rendererBorder = oldBorder;
				rendererComponent.setBorder( this );
			}
		}

		static void uninstall( Object o ) {
			if( !(o instanceof JComponent) )
				return;

			JComponent rendererComponent = (JComponent) o;
			Border border = rendererComponent.getBorder();
			if( border instanceof CellPaddingBorder ) {
				CellPaddingBorder paddingBorder = (CellPaddingBorder) border;
				rendererComponent.setBorder( paddingBorder.rendererBorder );
				paddingBorder.rendererBorder = null;
			}
		}

		@Override
		public Insets getBorderInsets( Component c, Insets insets ) {
			if( rendererBorder != null ) {
				Insets insideInsets = rendererBorder.getBorderInsets( c );
				insets.top = Math.max( padding.top, insideInsets.top );
				insets.left = Math.max( padding.left, insideInsets.left );
				insets.bottom = Math.max( padding.bottom, insideInsets.bottom );
				insets.right = Math.max( padding.right, insideInsets.right );
			} else {
				insets.top = padding.top;
				insets.left = padding.left;
				insets.bottom = padding.bottom;
				insets.right = padding.right;
			}
			return insets;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			if( rendererBorder != null )
				rendererBorder.paintBorder( c, g, x, y, width, height );
		}
	}
}
