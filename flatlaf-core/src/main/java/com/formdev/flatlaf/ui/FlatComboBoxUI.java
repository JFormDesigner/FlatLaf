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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JComboBox}.
 *
 * TODO document used UI defaults of superclass
 *
 * @uiDefault Component.focusWidth				int
 * @uiDefault Component.arc						int
 * @uiDefault Component.borderColor				Color
 * @uiDefault Component.disabledBorderColor		Color
 * @uiDefault ComboBox.disabledBackground		Color
 * @uiDefault ComboBox.disabledForeground		Color
 * @uiDefault ComboBox.buttonBackground			Color
 * @uiDefault ComboBox.buttonEditableBackground	Color
 * @uiDefault ComboBox.buttonArrowColor			Color
 * @uiDefault ComboBox.buttonDisabledArrowColor	Color
 *
 * @author Karl Tauber
 */
public class FlatComboBoxUI
	extends BasicComboBoxUI
{
	protected int focusWidth;
	protected int arc;
	protected Color borderColor;
	protected Color disabledBorderColor;

	protected Color disabledBackground;
	protected Color disabledForeground;

	protected Color buttonBackground;
	protected Color buttonEditableBackground;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatComboBoxUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		padding = UIScale.scale( padding );

		focusWidth = UIManager.getInt( "Component.focusWidth" );
		arc = UIManager.getInt( "Component.arc" );
		borderColor = UIManager.getColor( "Component.borderColor" );
		disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );

		disabledBackground = UIManager.getColor( "ComboBox.disabledBackground" );
		disabledForeground = UIManager.getColor( "ComboBox.disabledForeground" );

		buttonBackground = UIManager.getColor( "ComboBox.buttonBackground" );
		buttonEditableBackground = UIManager.getColor( "ComboBox.buttonEditableBackground" );
		buttonArrowColor = UIManager.getColor( "ComboBox.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "ComboBox.buttonDisabledArrowColor" );
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new BasicComboBoxUI.ComboBoxLayoutManager() {
			@Override
			public void layoutContainer( Container parent ) {
				super.layoutContainer( parent );

				if ( editor != null && padding != null ) {
					// fix editor bounds by subtracting padding
					editor.setBounds( FlatUIUtils.subtract( editor.getBounds(), padding ) );
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
		boolean enabled = editor.isEnabled();
		editor.setBackground( enabled ? comboBox.getBackground() : disabledBackground );
		editor.setForeground( (enabled || editor instanceof JTextComponent)
			? comboBox.getForeground()
			: disabledForeground );
		if( editor instanceof JTextComponent )
			((JTextComponent)editor).setDisabledTextColor( disabledForeground );
	}

	@Override
	protected JButton createArrowButton() {
		return new FlatArrowButton();
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
			boolean isLeftToRight = comboBox.getComponentOrientation().isLeftToRight();

			// paint background
			g2.setColor( comboBox.isEnabled() ? c.getBackground() : disabledBackground );
			FlatUIUtils.fillRoundRectangle( g2, 0, 0, width, height, focusWidth, arc );

			// paint arrow button background
			g2.setColor( comboBox.isEnabled()
				? (comboBox.isEditable() ? buttonEditableBackground : buttonBackground)
				: disabledBackground );
			Shape oldClip = g2.getClip();
			if( isLeftToRight )
				g2.clipRect( arrowX, 0, width - arrowX, height );
			else
				g2.clipRect( 0, 0, arrowX + arrowWidth, height );
			FlatUIUtils.fillRoundRectangle( g2, 0, 0, width, height, focusWidth, arc );
			g2.setClip( oldClip );

			if( comboBox.isEditable() ) {
				// paint vertical line between value and arrow button
				g2.setColor( comboBox.isEnabled() ? borderColor : disabledBorderColor );
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
		Component c = renderer.getListCellRendererComponent( listBox, comboBox.getSelectedItem(), -1, false, false );
		c.setFont( comboBox.getFont() );

		boolean enabled = comboBox.isEnabled();
		c.setForeground( enabled ? comboBox.getForeground() : disabledForeground );
		c.setBackground( enabled ? comboBox.getBackground() : disabledBackground );

		boolean shouldValidate = (c instanceof JPanel);
		if( padding != null )
			bounds = FlatUIUtils.subtract( bounds, padding );

		currentValuePane.paintComponent( g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate );
	}

	@Override
	public void paintCurrentValueBackground( Graphics g, Rectangle bounds, boolean hasFocus ) {
		g.setColor( comboBox.isEnabled() ? comboBox.getBackground() : disabledBackground );
		g.fillRect( bounds.x, bounds.y, bounds.width, bounds.height );
	}

	//---- class FlatArrowButton ----------------------------------------------

	private class FlatArrowButton
		extends BasicArrowButton
	{
		FlatArrowButton() {
			super( SOUTH, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE );

			setOpaque( false );
			setBorder( null );
		}

		@Override
		public void paint( Graphics g ) {
			FlatUIUtils.setRenderingHints( (Graphics2D) g );

			int w = scale( 9 );
			int h = scale( 5 );
			int x = Math.round( (getWidth() - w) / 2f );
			int y = Math.round( (getHeight() - h) / 2f );

			Path2D arrow = new Path2D.Float();
			arrow.moveTo( x, y );
			arrow.lineTo( x + w, y );
			arrow.lineTo( x + (w / 2f), y + h );
			arrow.closePath();

			g.setColor( isEnabled() ? buttonArrowColor : buttonDisabledArrowColor );
			((Graphics2D)g).fill( arrow );
		}
	}
}
