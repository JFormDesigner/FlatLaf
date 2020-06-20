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
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JSpinner}.
 *
 * <!-- BasicSpinnerUI -->
 *
 * @uiDefault Spinner.font						Font
 * @uiDefault Spinner.background				Color
 * @uiDefault Spinner.foreground				Color
 * @uiDefault Spinner.border					Border
 * @uiDefault Spinner.disableOnBoundaryValues	boolean	default is false
 * @uiDefault Spinner.editorAlignment			int		0=center, 2=left, 4=right, 10=leading, 11=trailing
 * @uiDefault Spinner.editorBorderPainted		boolean	paint inner editor border; defaults to false
 *
 * <!-- FlatSpinnerUI -->
 *
 * @uiDefault Component.minimumWidth			int
 * @uiDefault Spinner.buttonStyle				String	button (default) or none
 * @uiDefault Component.arrowType				String	triangle (default) or chevron
 * @uiDefault Component.isIntelliJTheme			boolean
 * @uiDefault Component.borderColor				Color
 * @uiDefault Component.disabledBorderColor		Color
 * @uiDefault Spinner.disabledBackground		Color
 * @uiDefault Spinner.disabledForeground		Color
 * @uiDefault Spinner.buttonBackground			Color
 * @uiDefault Spinner.buttonArrowColor			Color
 * @uiDefault Spinner.buttonDisabledArrowColor	Color
 * @uiDefault Spinner.buttonHoverArrowColor		Color
 * @uiDefault Spinner.padding					Insets
 *
 * @author Karl Tauber
 */
public class FlatSpinnerUI
	extends BasicSpinnerUI
{
	private Handler handler;

	protected int minimumWidth;
	protected String buttonStyle;
	protected String arrowType;
	protected boolean isIntelliJTheme;
	protected Color borderColor;
	protected Color disabledBorderColor;
	protected Color disabledBackground;
	protected Color disabledForeground;
	protected Color buttonBackground;
	protected Color buttonArrowColor;
	protected Color buttonDisabledArrowColor;
	protected Color buttonHoverArrowColor;
	protected Insets padding;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatSpinnerUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		LookAndFeel.installProperty( spinner, "opaque", false );

		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
		buttonStyle = UIManager.getString( "Spinner.buttonStyle" );
		arrowType = UIManager.getString( "Component.arrowType" );
		isIntelliJTheme = UIManager.getBoolean( "Component.isIntelliJTheme" );
		borderColor = UIManager.getColor( "Component.borderColor" );
		disabledBorderColor = UIManager.getColor( "Component.disabledBorderColor" );
		disabledBackground = UIManager.getColor( "Spinner.disabledBackground" );
		disabledForeground = UIManager.getColor( "Spinner.disabledForeground" );
		buttonBackground = UIManager.getColor( "Spinner.buttonBackground" );
		buttonArrowColor = UIManager.getColor( "Spinner.buttonArrowColor" );
		buttonDisabledArrowColor = UIManager.getColor( "Spinner.buttonDisabledArrowColor" );
		buttonHoverArrowColor = UIManager.getColor( "Spinner.buttonHoverArrowColor" );
		padding = UIManager.getInsets( "Spinner.padding" );

		// scale
		padding = scale( padding );

		MigLayoutVisualPadding.install( spinner );
	}

	@Override
	protected void uninstallDefaults() {
		super.uninstallDefaults();

		borderColor = null;
		disabledBorderColor = null;
		disabledBackground = null;
		disabledForeground = null;
		buttonBackground = null;
		buttonArrowColor = null;
		buttonDisabledArrowColor = null;
		buttonHoverArrowColor = null;
		padding = null;

		MigLayoutVisualPadding.uninstall( spinner );
	}

	@Override
	protected void installListeners() {
		super.installListeners();

		addEditorFocusListener( spinner.getEditor() );
		spinner.addFocusListener( getHandler() );
		spinner.addPropertyChangeListener( getHandler() );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		removeEditorFocusListener( spinner.getEditor() );
		spinner.removeFocusListener( getHandler() );
		spinner.removePropertyChangeListener( getHandler() );

		handler = null;
	}

	private Handler getHandler() {
		if( handler == null )
			handler = new Handler();
		return handler;
	}

	@Override
	protected JComponent createEditor() {
		JComponent editor = super.createEditor();

		// explicitly make non-opaque
		editor.setOpaque( false );
		JTextField textField = getEditorTextField( editor );
		if( textField != null )
			textField.setOpaque( false );

		updateEditorColors();
		return editor;
	}

	@Override
	protected void replaceEditor( JComponent oldEditor, JComponent newEditor ) {
		super.replaceEditor( oldEditor, newEditor );

		removeEditorFocusListener( oldEditor );
		addEditorFocusListener( newEditor );
		updateEditorColors();
	}

	private void addEditorFocusListener( JComponent editor ) {
		JTextField textField = getEditorTextField( editor );
		if( textField != null )
			textField.addFocusListener( getHandler() );
	}

	private void removeEditorFocusListener( JComponent editor ) {
		JTextField textField = getEditorTextField( editor );
		if( textField != null )
			textField.removeFocusListener( getHandler() );
	}

	private void updateEditorColors() {
		JTextField textField = getEditorTextField( spinner.getEditor() );
		if( textField != null ) {
			// use non-UIResource colors because when SwingUtilities.updateComponentTreeUI()
			// is used, then the text field is updated after the spinner and the
			// colors are again replaced with default colors
			textField.setForeground( FlatUIUtils.nonUIResource( getForeground( true ) ) );
			textField.setDisabledTextColor( FlatUIUtils.nonUIResource( getForeground( false ) ) );
		}
	}

	private static JTextField getEditorTextField( JComponent editor ) {
		return editor instanceof JSpinner.DefaultEditor
			? ((JSpinner.DefaultEditor)editor).getTextField()
			: null;
	}

	protected Color getBackground( boolean enabled ) {
		return enabled
			? spinner.getBackground()
			: (isIntelliJTheme ? FlatUIUtils.getParentBackground( spinner ) : disabledBackground);
	}

	protected Color getForeground( boolean enabled ) {
		return enabled ? spinner.getForeground() : disabledForeground;
	}

	@Override
	protected LayoutManager createLayout() {
		return getHandler();
	}

	@Override
	protected Component createNextButton() {
		return createArrowButton( SwingConstants.NORTH, "Spinner.nextButton" );
	}

	@Override
	protected Component createPreviousButton() {
		return createArrowButton( SwingConstants.SOUTH, "Spinner.previousButton" );
	}

	private Component createArrowButton( int direction, String name ) {
		FlatArrowButton button = new FlatArrowButton( direction, arrowType, buttonArrowColor,
			buttonDisabledArrowColor, buttonHoverArrowColor, null );
		button.setName( name );
		button.setYOffset( (direction == SwingConstants.NORTH) ? 1 : -1 );
		if( direction == SwingConstants.NORTH )
			installNextButtonListeners( button );
		else
			installPreviousButtonListeners( button );
		return button;
	}

	@Override
	public void update( Graphics g, JComponent c ) {
		float focusWidth = FlatUIUtils.getBorderFocusWidth( c );
		float arc = FlatUIUtils.getBorderArc( c );

		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() && (focusWidth > 0 || arc > 0) )
			FlatUIUtils.paintParentBackground( g, c );

		Graphics2D g2 = (Graphics2D) g;
		FlatUIUtils.setRenderingHints( g2 );

		int width = c.getWidth();
		int height = c.getHeight();
		Component nextButton = getHandler().nextButton;
		int arrowX = nextButton.getX();
		int arrowWidth = nextButton.getWidth();
		boolean paintButton = !"none".equals( buttonStyle );
		boolean enabled = spinner.isEnabled();
		boolean isLeftToRight = spinner.getComponentOrientation().isLeftToRight();

		// paint background
		g2.setColor( getBackground( enabled ) );
		FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );

		// paint arrow buttons background
		if( paintButton && enabled ) {
			g2.setColor( buttonBackground );
			Shape oldClip = g2.getClip();
			if( isLeftToRight )
				g2.clipRect( arrowX, 0, width - arrowX, height );
			else
				g2.clipRect( 0, 0, arrowX + arrowWidth, height );
			FlatUIUtils.paintComponentBackground( g2, 0, 0, width, height, focusWidth, arc );
			g2.setClip( oldClip );
		}

		// paint vertical line between value and arrow buttons
		if( paintButton ) {
			g2.setColor( enabled ? borderColor : disabledBorderColor );
			float lw = scale( 1f );
			float lx = isLeftToRight ? arrowX : arrowX + arrowWidth - lw;
			g2.fill( new Rectangle2D.Float( lx, focusWidth, lw, height - 1 - (focusWidth * 2) ) );
		}

		paint( g, c );
	}

	//---- class Handler ------------------------------------------------------

	private class Handler
		implements LayoutManager, FocusListener, PropertyChangeListener
	{
		//---- interface LayoutManager ----

		private Component editor = null;
		private Component nextButton;
		private Component previousButton;

		@Override
		public void addLayoutComponent( String name, Component c ) {
			switch( name ) {
				case "Editor":	editor = c; break;
				case "Next":		nextButton = c; break;
				case "Previous":	previousButton = c; break;
			}
		}

		@Override
		public void removeLayoutComponent( Component c ) {
			if( c == editor )
				editor = null;
			else if( c == nextButton )
				nextButton = null;
			else if( c == previousButton )
				previousButton = null;
		}

		@Override
		public Dimension preferredLayoutSize( Container parent ) {
			Insets insets = parent.getInsets();
			Dimension editorSize = (editor != null) ? editor.getPreferredSize() : new Dimension( 0, 0 );

			// the arrows width is the same as the inner height so that the arrows area is square
			int minimumWidth = FlatUIUtils.minimumWidth( spinner, FlatSpinnerUI.this.minimumWidth );
			int innerHeight = editorSize.height + padding.top + padding.bottom;
			float focusWidth = FlatUIUtils.getBorderFocusWidth( spinner );
			return new Dimension(
				Math.max( insets.left + insets.right + editorSize.width + padding.left + padding.right + innerHeight, scale( minimumWidth ) + Math.round( focusWidth * 2 ) ),
				insets.top + insets.bottom + innerHeight );
		}

		@Override
		public Dimension minimumLayoutSize( Container parent ) {
			return preferredLayoutSize( parent );
		}

		@Override
		public void layoutContainer( Container parent ) {
			Dimension size = parent.getSize();
			Insets insets = parent.getInsets();
			Rectangle r = FlatUIUtils.subtractInsets( new Rectangle( size ), insets );

			if( nextButton == null && previousButton == null ) {
				if( editor != null )
					editor.setBounds( r );
				return;
			}

			Rectangle editorRect = new Rectangle( r );
			Rectangle buttonsRect = new Rectangle( r );

			// make button area square
			int buttonsWidth = r.height;
			buttonsRect.width = buttonsWidth;

			if( parent.getComponentOrientation().isLeftToRight() ) {
				editorRect.width -= buttonsWidth;
				buttonsRect.x += editorRect.width;
			} else {
				editorRect.x += buttonsWidth;
				editorRect.width -= buttonsWidth;
			}

			if( editor != null )
				editor.setBounds( FlatUIUtils.subtractInsets( editorRect, padding ) );

			int nextHeight = (buttonsRect.height / 2) + (buttonsRect.height % 2); // round up
			if( nextButton != null )
				nextButton.setBounds( buttonsRect.x, buttonsRect.y, buttonsRect.width, nextHeight );
			if( previousButton != null ) {
				// for precise layout of arrows, the "previous" button has the same height
				// as the "next" button and may overlap on uneven buttonsRect.height
				int previousY = buttonsRect.y + buttonsRect.height - nextHeight;
				previousButton.setBounds( buttonsRect.x, previousY, buttonsRect.width, nextHeight );
			}
		}

		//---- interface FocusListener ----

		@Override
		public void focusGained( FocusEvent e ) {
			spinner.repaint();

			// if spinner gained focus, transfer it to the editor text field
			if( e.getComponent() == spinner ) {
				JTextField textField = getEditorTextField( spinner.getEditor() );
				if( textField != null )
					textField.requestFocusInWindow();
			}
		}

		@Override
		public void focusLost( FocusEvent e ) {
			spinner.repaint();
		}

		//---- interface PropertyChangeListener ----

		@Override
		public void propertyChange( PropertyChangeEvent e ) {
			switch( e.getPropertyName() ) {
				case "foreground":
				case "enabled":
					updateEditorColors();
					break;

				case FlatClientProperties.COMPONENT_ROUND_RECT:
					spinner.repaint();
					break;

				case FlatClientProperties.MINIMUM_WIDTH:
					spinner.revalidate();
					break;
			}
		}
	}
}
