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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import com.formdev.flatlaf.util.UIScale;

/**
 * Provides the Flat LaF UI delegate for {@link javax.swing.JOptionPane}.
 *
 * <!-- BasicOptionPaneUI -->
 *
 * @uiDefault OptionPane.font						Font	unused
 * @uiDefault OptionPane.background					Color
 * @uiDefault OptionPane.foreground					Color	unused
 * @uiDefault OptionPane.border						Border
 * @uiDefault OptionPane.messageAreaBorder			Border
 * @uiDefault OptionPane.buttonAreaBorder			Border
 * @uiDefault OptionPane.messageForeground			Color	optional; defaults to Label.foreground
 * @uiDefault OptionPane.messageFont				Font	optional; defaults to Label.font
 * @uiDefault OptionPane.buttonFont					Font	optional; defaults to Button.font
 *
 * @uiDefault OptionPane.minimumSize				Dimension
 * @uiDefault OptionPane.buttonPadding				int
 * @uiDefault OptionPane.buttonMinimumWidth			int		-1=disabled
 * @uiDefault OptionPane.sameSizeButtons			boolean	if true, gives all buttons same size
 * @uiDefault OptionPane.setButtonMargin			boolean	if true, invokes button.setMargin(2,4,2,4)
 * @uiDefault OptionPane.buttonOrientation			int		0=center, 2=left, 4=right
 * @uiDefault OptionPane.isYesLast					boolean	reverse button order if true
 *
 * @uiDefault OptionPane.errorIcon					Icon
 * @uiDefault OptionPane.informationIcon			Icon
 * @uiDefault OptionPane.questionIcon				Icon
 * @uiDefault OptionPane.warningIcon				Icon
 *
 * @uiDefault OptionPane.okButtonText				String
 * @uiDefault OptionPane.okButtonMnemonic			String
 * @uiDefault OptionPane.okIcon						Icon
 * @uiDefault OptionPane.cancelButtonText			String
 * @uiDefault OptionPane.cancelButtonMnemonic		String
 * @uiDefault OptionPane.cancelIcon					Icon
 * @uiDefault OptionPane.yesButtonText				String
 * @uiDefault OptionPane.yesButtonMnemonic			String
 * @uiDefault OptionPane.yesIcon					Icon
 * @uiDefault OptionPane.noButtonText				String
 * @uiDefault OptionPane.noButtonMnemonic			String
 * @uiDefault OptionPane.noIcon						Icon
 *
 * <!-- FlatOptionPaneUI -->
 *
 * @uiDefault OptionPane.iconMessageGap				int
 * @uiDefault OptionPane.messagePadding				int
 * @uiDefault OptionPane.maxCharactersPerLine		int
 *
 * @author Karl Tauber
 */
public class FlatOptionPaneUI
	extends BasicOptionPaneUI
{
	protected int iconMessageGap;
	protected int messagePadding;
	protected int maxCharactersPerLine;
	private int focusWidth;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatOptionPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		iconMessageGap = UIManager.getInt( "OptionPane.iconMessageGap" );
		messagePadding = UIManager.getInt( "OptionPane.messagePadding" );
		maxCharactersPerLine = UIManager.getInt( "OptionPane.maxCharactersPerLine" );
		focusWidth = UIManager.getInt( "Component.focusWidth" );
	}

	@Override
	protected void installComponents() {
		super.installComponents();

		updateChildPanels( optionPane );
	}

	@Override
	public Dimension getMinimumOptionPaneSize() {
		return UIScale.scale( super.getMinimumOptionPaneSize() );
	}

	@Override
	protected int getMaxCharactersPerLineCount() {
		int max = super.getMaxCharactersPerLineCount();
		return (maxCharactersPerLine > 0 && max == Integer.MAX_VALUE) ? maxCharactersPerLine : max;
	}

	@Override
	protected Container createMessageArea() {
		Container messageArea = super.createMessageArea();

		// set icon-message gap
		if( iconMessageGap > 0 ) {
			Component iconMessageSeparator = findByName( messageArea, "OptionPane.separator" );
			if( iconMessageSeparator != null )
				iconMessageSeparator.setPreferredSize( new Dimension( UIScale.scale( iconMessageGap ), 1 ) );
		}

		return messageArea;
	}

	@Override
	protected Container createButtonArea() {
		Container buttonArea = super.createButtonArea();

		// scale button padding and subtract focusWidth
		if( buttonArea.getLayout() instanceof ButtonAreaLayout ) {
			ButtonAreaLayout layout = (ButtonAreaLayout) buttonArea.getLayout();
			layout.setPadding( UIScale.scale( layout.getPadding() - (focusWidth * 2) ) );
		}

		return buttonArea;
	}

	@Override
	protected void addMessageComponents( Container container, GridBagConstraints cons, Object msg, int maxll,
		boolean internallyCreated )
	{
		// set message padding
		if( messagePadding > 0 )
			cons.insets.bottom = UIScale.scale( messagePadding );

		// disable line wrapping for HTML
		if( msg instanceof String && BasicHTML.isHTMLString( (String) msg ) )
			maxll = Integer.MAX_VALUE;

		super.addMessageComponents( container, cons, msg, maxll, internallyCreated );
	}

	private void updateChildPanels( Container c ) {
		for( Component child : c.getComponents() ) {
			if( child instanceof JPanel ) {
				JPanel panel = (JPanel)child;

				// make sub-panel non-opaque for OptionPane.background
				panel.setOpaque( false );

				// use non-UIResource borders to avoid that they are replaced when switching LaF
				Border border = panel.getBorder();
				if( border instanceof UIResource )
					panel.setBorder( new NonUIResourceBorder( border ) );
			}

			if( child instanceof Container ) {
				updateChildPanels( (Container) child );
			}
		}
	}

	private Component findByName( Container c, String name ) {
		for( Component child : c.getComponents() ) {
			if( name.equals( child.getName() ) )
				return child;

			if( child instanceof Container ) {
				Component c2 = findByName( (Container) child, name );
				if( c2 != null )
					return c2;
			}
		}
		return null;
	}

	//---- class NonUIResourceBorder ------------------------------------------

	private static class NonUIResourceBorder
		implements Border
	{
		private final Border delegate;

		NonUIResourceBorder( Border delegate ) {
			this.delegate = delegate;
		}

		@Override
		public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
			delegate.paintBorder( c, g, x, y, width, height );
		}

		@Override
		public Insets getBorderInsets( Component c ) {
			return delegate.getBorderInsets( c );
		}

		@Override
		public boolean isBorderOpaque() {
			return delegate.isBorderOpaque();
		}
	}
}
