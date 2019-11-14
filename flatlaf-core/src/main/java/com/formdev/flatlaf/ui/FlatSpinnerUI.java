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
 * @uiDefault Component.focusWidth				int
 * @uiDefault Component.arc						int
 * @uiDefault Component.minimumWidth			int
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

	protected int focusWidth;
	protected int arc;
	protected int minimumWidth;
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

		focusWidth = UIManager.getInt( "Component.focusWidth" );
		arc = UIManager.getInt( "Component.arc" );
		minimumWidth = UIManager.getInt( "Component.minimumWidth" );
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

		MigLayoutVisualPadding.install( spinner, focusWidth );
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
		spinner.addPropertyChangeListener( getHandler() );
	}

	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();

		removeEditorFocusListener( spinner.getEditor() );
		spinner.removePropertyChangeListener( getHandler() );

		handler = null;
	}

	public Handler getHandler() {
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
			textField.setForeground( FlatUIUtils.nonUIResource( spinner.getForeground() ) );
			textField.setDisabledTextColor( FlatUIUtils.nonUIResource( disabledForeground ) );
		}
	}

	private JTextField getEditorTextField( JComponent editor ) {
		return editor instanceof JSpinner.DefaultEditor
			? ((JSpinner.DefaultEditor)editor).getTextField()
			: null;
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
		// fill background if opaque to avoid garbage if user sets opaque to true
		if( c.isOpaque() && (focusWidth > 0 || arc != 0) )
			FlatUIUtils.paintParentBackground( g, c );

		Graphics2D g2 = (Graphics2D) g;
		FlatUIUtils.setRenderingHints( g2 );

		int width = c.getWidth();
		int height = c.getHeight();
		float focusWidth = (c.getBorder() instanceof FlatBorder) ? scale( (float) this.focusWidth ) : 0;
		float arc = (c.getBorder() instanceof FlatRoundBorder) ? scale( (float) this.arc ) : 0;
		Component nextButton = getHandler().nextButton;
		int arrowX = nextButton.getX();
		int arrowWidth = nextButton.getWidth();
		boolean enabled = spinner.isEnabled();
		boolean isLeftToRight = spinner.getComponentOrientation().isLeftToRight();

		// paint background
		g2.setColor( enabled
			? c.getBackground()
			: (isIntelliJTheme ? FlatUIUtils.getParentBackground( c ) : disabledBackground) );
		FlatUIUtils.fillRoundRectangle( g2, 0, 0, width, height, focusWidth, arc );

		// paint arrow buttons background
		if( enabled ) {
			g2.setColor( buttonBackground );
			Shape oldClip = g2.getClip();
			if( isLeftToRight )
				g2.clipRect( arrowX, 0, width - arrowX, height );
			else
				g2.clipRect( 0, 0, arrowX + arrowWidth, height );
			FlatUIUtils.fillRoundRectangle( g2, 0, 0, width, height, focusWidth, arc );
			g2.setClip( oldClip );
		}

		// paint vertical line between value and arrow buttons
		g2.setColor( enabled ? borderColor : disabledBorderColor );
		float lw = scale( 1f );
		float lx = isLeftToRight ? arrowX : arrowX + arrowWidth - lw;
		g2.fill( new Rectangle2D.Float( lx, focusWidth, lw, height - (focusWidth * 2) ) );

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
			int innerHeight = editorSize.height + padding.top + padding.bottom;
			return new Dimension(
				Math.max( insets.left + insets.right + editorSize.width + padding.left + padding.right + innerHeight, scale( minimumWidth + (focusWidth * 2) ) ),
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

			int nextHeight = Math.round( buttonsRect.height / 2f );
			if( nextButton != null )
				nextButton.setBounds( buttonsRect.x, buttonsRect.y, buttonsRect.width, nextHeight );
			if( previousButton != null ) {
				previousButton.setBounds( buttonsRect.x, buttonsRect.y + nextHeight,
					buttonsRect.width, buttonsRect.height - nextHeight );
			}
		}

		//---- interface FocusListener ----

		@Override
		public void focusGained( FocusEvent e ) {
			spinner.repaint();
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
			}
		}
	}
}
