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

package com.formdev.flatlaf.swingx.ui;

import static com.formdev.flatlaf.util.UIScale.scale;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.text.ParseException;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.calendar.DatePickerFormatter.DatePickerFormatterUIResource;
import org.jdesktop.swingx.plaf.basic.BasicDatePickerUI;
import com.formdev.flatlaf.ui.FlatArrowButton;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.ui.MigLayoutVisualPadding;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link org.jdesktop.swingx.JXDatePicker}.
 *
 * @author Karl Tauber
 */
public class FlatDatePickerUI
	extends BasicDatePickerUI
{
	protected Insets padding;

	protected String arrowType;
	protected Color borderColor;
	protected Color disabledBorderColor;

	protected Color disabledBackground;

	protected Color buttonBackground;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;
	protected Color buttonHoverArrowColor;
	protected Color buttonPressedArrowColor;

	private JButton popupButton;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatDatePickerUI();
	}

	@Override
	public void installUI( JComponent c ) {
		// must get UI defaults here because installDefaults() is invoked after
		// installComponents(), which uses these values to create popup button

		padding = UIManager.getInsets( "ComboBox.padding" );

		arrowType = UIManager.getString( "Component.arrowType" );
		borderColor = UIManager.getColor( "Component.borderColor" );
		disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );

		disabledBackground = UIManager.getColor( "ComboBox.disabledBackground" );

		buttonBackground = UIManager.getColor( "ComboBox.buttonBackground" );
		buttonArrowColor = UIManager.getColor( "ComboBox.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "ComboBox.buttonDisabledArrowColor" );
		buttonHoverArrowColor = UIManager.getColor( "ComboBox.buttonHoverArrowColor" );
		buttonPressedArrowColor = UIManager.getColor( "ComboBox.buttonPressedArrowColor" );

		super.installUI( c );

		LookAndFeel.installProperty( datePicker, "opaque", false );

		// hack JXDatePicker.TodayPanel colors
		// (there is no need to uninstall these changes because only UIResources are used,
		// which are automatically replaced when switching LaF)
		JPanel linkPanel = datePicker.getLinkPanel();
		if( linkPanel instanceof JXPanel && linkPanel.getClass().getName().equals( "org.jdesktop.swingx.JXDatePicker$TodayPanel" ) ) {
			((JXPanel)linkPanel).setBackgroundPainter( null );
			linkPanel.setBackground( UIManager.getColor( "JXMonthView.background" ) );

			if( linkPanel.getComponentCount() >= 1 && linkPanel.getComponent( 0 ) instanceof JXHyperlink ) {
				JXHyperlink todayLink = (JXHyperlink) linkPanel.getComponent( 0 );
				todayLink.setUnclickedColor( UIManager.getColor( "Hyperlink.linkColor" ) );
				todayLink.setClickedColor( UIManager.getColor( "Hyperlink.visitedColor" ) );
			}
		}
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installColors( datePicker, "ComboBox.background", "ComboBox.foreground" );

		LookAndFeel.installBorder( datePicker, "JXDatePicker.border" );
		LookAndFeel.installProperty( datePicker, "opaque", Boolean.TRUE );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		borderColor = null;
		disabledBorderColor = null;

		disabledBackground = null;

		buttonBackground = null;
		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		buttonHoverArrowColor = null;
		buttonPressedArrowColor = null;

		if( datePicker.getBorder() instanceof UIResource )
			datePicker.setBorder( null );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		MigLayoutVisualPadding.install( datePicker );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		MigLayoutVisualPadding.uninstall( datePicker );
	}

	@Override
	protected JFormattedTextField createEditor() {
		JFormattedTextField editor = new DefaultEditor( new DatePickerFormatterUIResource( datePicker.getLocale() ) );
		editor.setName( "dateField" );
		editor.setBorder( BorderFactory.createEmptyBorder() );
		editor.setOpaque( false );
		editor.addFocusListener( new FlatUIUtils.RepaintFocusListener( datePicker, null ) );
		return editor;
	}

	@Override
	protected JButton createPopupButton() {
		popupButton = new FlatArrowButton( SwingConstants.SOUTH, arrowType, buttonArrowColor,
			buttonDisabledArrowColor, buttonHoverArrowColor, null, buttonPressedArrowColor, null );
		popupButton.setName( "popupButton" );
		return popupButton;
	}

	@Override
	protected LayoutManager createLayoutManager() {
		return new LayoutManager() {
			@Override
			public void addLayoutComponent( String name, Component comp ) {}
			@Override
			public void removeLayoutComponent( Component comp ) {}

			@Override
			public Dimension preferredLayoutSize( Container parent ) {
				return parent.getPreferredSize();
			}
			@Override
			public Dimension minimumLayoutSize( Container parent ) {
				return parent.getMinimumSize();
			}
			@Override
			public void layoutContainer( Container parent ) {
				Insets insets = datePicker.getInsets();
				int x = insets.left;
				int y = insets.top;
				int width = datePicker.getWidth() - insets.left - insets.right;
				int height = datePicker.getHeight() - insets.top - insets.bottom;

				int popupButtonWidth = popupButton != null ? height : 0;
				boolean ltr = datePicker.getComponentOrientation().isLeftToRight();

				Rectangle r = new Rectangle( x + (ltr ? 0 : popupButtonWidth), y, width - popupButtonWidth, height );
				r = FlatUIUtils.subtractInsets( r, UIScale.scale( padding ) );
				datePicker.getEditor().setBounds( r );

				if( popupButton != null )
					popupButton.setBounds( x + (ltr ? width - popupButtonWidth : 0), y, popupButtonWidth, height );
			}
		};
	}

	@Override
	public Dimension getPreferredSize( JComponent c ) {
		Dimension dim = datePicker.getEditor().getPreferredSize();
		dim = FlatUIUtils.addInsets( dim, UIScale.scale( padding ) );
		if( popupButton != null )
			dim.width += dim.height;
		return FlatUIUtils.addInsets( dim, datePicker.getInsets() );
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() )
			FlatUIUtils.paintParentBackground( g, c );

		Graphics2D g2 = (Graphics2D) g;
		Object[] oldRenderingHints = FlatUIUtils.setRenderingHints( g2 );

		int width = c.getWidth();
		int height = c.getHeight();
		float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
		float arc = FlatUIUtils.getBorderArc( c );
		int arrowX = popupButton.getX();
		int arrowWidth = popupButton.getWidth();
		boolean enabled = c.isEnabled();
		boolean isLeftToRight = c.getComponentOrientation().isLeftToRight();

		// paint background
		g2.setColor( enabled ? c.getBackground() : disabledBackground );
		FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );

		// paint arrow button background
		if( enabled ) {
			g2.setColor( buttonBackground );
			Shape oldClip = g2.getClip();
			if( isLeftToRight )
				g2.clipRect( arrowX, 0, width - arrowX, height );
			else
				g2.clipRect( 0, 0, arrowX + arrowWidth, height );
			FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );
			g2.setClip( oldClip );
		}

		// paint vertical line between value and arrow button
		g2.setColor( enabled ? borderColor : disabledBorderColor );
		float lw = scale( 1f );
		float lx = isLeftToRight ? arrowX : arrowX + arrowWidth - lw;
		g2.fill( new Rectangle2D.Float( lx, focusWidth, lw, height - 1 - (focusWidth * 2) ) );

		paint( g, c );

		FlatUIUtils.resetRenderingHints( g2, oldRenderingHints );
	}

	//---- class DefaultEditor ------------------------------------------------

	private class DefaultEditor
		extends JFormattedTextField
		implements UIResource
	{
		DefaultEditor( AbstractFormatter formatter ) {
			super( formatter );

			// disable Component.minimumWidth
			setColumns( 1 );
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension prefSize = super.getPreferredSize();

			// create date 2000-11-30 that is used for minimum width calculation
			Calendar calendar = Calendar.getInstance( datePicker.getLocale() );
			calendar.set( 2000, 11, 30 );

			try {
				AbstractFormatter formatter = getFormatter();
				String str = formatter.valueToString( calendar.getTime() );

				Insets insets = getInsets();
				FontMetrics metrics = getFontMetrics( getFont() );
				int minWidth = metrics.stringWidth( str ) + insets.left + insets.right + 2;
				return new Dimension( Math.max( minWidth, prefSize.width ), prefSize.height );
			} catch( ParseException ex ) {
				// ignore
				return prefSize;
			}
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}
	}
}
