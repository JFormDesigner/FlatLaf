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
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.SwingUtils;
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
 * @uiDefault OptionPane.showIcon					boolean
 * @uiDefault OptionPane.iconMessageGap				int
 * @uiDefault OptionPane.messagePadding				int
 * @uiDefault OptionPane.maxCharactersPerLine		int
 *
 * @author Karl Tauber
 */
public class FlatOptionPaneUI
	extends BasicOptionPaneUI
{
	/** @since 2 */ protected boolean showIcon;
	protected int iconMessageGap;
	protected int messagePadding;
	protected int maxCharactersPerLine;
	private int focusWidth;
	private boolean sameSizeButtons;

	public static ComponentUI createUI( JComponent c ) {
		return new FlatOptionPaneUI();
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		showIcon = UIManager.getBoolean( "OptionPane.showIcon" );
		iconMessageGap = UIManager.getInt( "OptionPane.iconMessageGap" );
		messagePadding = UIManager.getInt( "OptionPane.messagePadding" );
		maxCharactersPerLine = UIManager.getInt( "OptionPane.maxCharactersPerLine" );
		focusWidth = UIManager.getInt( "Component.focusWidth" );
		sameSizeButtons = FlatUIUtils.getUIBoolean( "OptionPane.sameSizeButtons", true );
	}

	@Override
	protected PropertyChangeListener createPropertyChangeListener() {
		PropertyChangeListener superListener = super.createPropertyChangeListener();
		return e -> {
			superListener.propertyChange( e );

			// hide window title bar icon
			// (only if showIcon is false, otherwise the default behavior is used)
			if( !showIcon && "ancestor".equals( e.getPropertyName() ) && e.getNewValue() != null ) {
				JRootPane rootPane = SwingUtilities.getRootPane( optionPane );
				if( rootPane != null &&
					rootPane.getContentPane().getComponentCount() > 0 &&
					rootPane.getContentPane().getComponent( 0 ) == optionPane )
				  rootPane.putClientProperty( FlatClientProperties.TITLE_BAR_SHOW_ICON, false );
			}
		};
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

		// use non-UIResource OptionPane.messageAreaBorder to avoid that it is replaced when switching LaF
		// and make panel non-opaque for OptionPane.background
		updateAreaPanel( messageArea );

		// make known sub-panels non-opaque for OptionPane.background
		updateKnownChildPanels( messageArea );

		// set icon-message gap
		if( iconMessageGap > 0 ) {
			Component iconMessageSeparator = SwingUtils.getComponentByName( messageArea, "OptionPane.separator" );
			if( iconMessageSeparator != null )
				iconMessageSeparator.setPreferredSize( new Dimension( UIScale.scale( iconMessageGap ), 1 ) );
		}

		return messageArea;
	}

	@Override
	protected Container createButtonArea() {
		Container buttonArea = super.createButtonArea();

		// use non-UIResource OptionPane.buttonAreaBorder to avoid that it is replaced when switching LaF
		// and make panel non-opaque for OptionPane.background
		updateAreaPanel( buttonArea );

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
		if( msg != null &&
			!(msg instanceof Component) &&
			!(msg instanceof Object[]) &&
			!(msg instanceof Icon) )
		{
			msg = msg.toString();
			if( BasicHTML.isHTMLString( (String) msg ) )
				maxll = Integer.MAX_VALUE;
		}

		// fix right-to-left alignment if super.addMessageComponents() breaks longer lines
		// into multiple labels and puts them into a box that aligns them to the left
		if( msg instanceof Box ) {
			Box box = (Box) msg;
			if( "OptionPane.verticalBox".equals( box.getName() ) &&
				box.getLayout() instanceof BoxLayout &&
				((BoxLayout)box.getLayout()).getAxis() == BoxLayout.Y_AXIS )
			{
				box.addPropertyChangeListener( "componentOrientation", e -> {
					float alignX = box.getComponentOrientation().isLeftToRight() ? 0 : 1;
					for( Component c : box.getComponents() ) {
						if( c instanceof JLabel && "OptionPane.label".equals( c.getName() ) )
							((JLabel)c).setAlignmentX( alignX );
					}
				} );
			}
		}

		super.addMessageComponents( container, cons, msg, maxll, internallyCreated );
	}

	private void updateAreaPanel( Container area ) {
		if( !(area instanceof JPanel) )
			return;

		// use non-UIResource border to avoid that it is replaced when switching LaF
		// and make panel non-opaque for OptionPane.background
		JPanel panel = (JPanel) area;
		panel.setBorder( FlatUIUtils.nonUIResource( panel.getBorder() ) );
		panel.setOpaque( false );
	}

	private void updateKnownChildPanels( Container c ) {
		for( Component child : c.getComponents() ) {
			if( child instanceof JPanel && child.getName() != null ) {
				switch( child.getName() ) {
					case "OptionPane.realBody":
					case "OptionPane.body":
					case "OptionPane.separator":
					case "OptionPane.break":
						// make known sub-panels non-opaque for OptionPane.background
						((JPanel)child).setOpaque( false );
						break;
				}
			}

			if( child instanceof Container )
				updateKnownChildPanels( (Container) child );
		}
	}

	@Override
	protected boolean getSizeButtonsToSameWidth() {
		return sameSizeButtons;
	}
}
