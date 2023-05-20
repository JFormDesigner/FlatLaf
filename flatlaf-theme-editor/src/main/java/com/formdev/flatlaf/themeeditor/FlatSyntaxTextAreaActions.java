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

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RecordableTextAction;
import com.formdev.flatlaf.UIDefaultsLoaderAccessor;

/**
 * @author Karl Tauber
 */
class FlatSyntaxTextAreaActions
{
	static final String duplicateLinesUpAction = "FlatLaf.DuplicateLinesUpAction";
	static final String duplicateLinesDownAction = "FlatLaf.DuplicateLinesDownAction";
	static final String incrementNumberAction = "FlatLaf.IncrementNumberAction";
	static final String decrementNumberAction = "FlatLaf.DecrementNumberAction";
	static final String insertColorAction = "FlatLaf.InsertColorAction";
	static final String pickColorAction = "FlatLaf.PickColorAction";

	static int[] findColorAt( RTextArea textArea, int position ) {
		try {
			int start = position;
			int end = position;

			// find first '#' or hex digit
			for( int i = position - 1; i >= 0; i-- ) {
				char ch = textArea.getText( i, 1 ).charAt( 0 );
				if( ch != '#' && !isHexDigit( ch ) )
					break;
				start = i;
			}

			// find last hex digit
			int length = textArea.getDocument().getLength();
			for( int i = position; i < length; i++ ) {
				if( !isHexDigit( textArea.getText( i, 1 ).charAt( 0 ) ) )
					break;
				end = i + 1;
			}

			// check for valid length (#RGB, #RGBA, #RRGGBB or #RRGGBBAA)
			int len = end - start;
			if( len != 4 && len != 5 && len != 7 && len != 9 )
				return null;

			// check whether starts with '#'
			if( textArea.getText( start, 1 ).charAt( 0 ) != '#' )
				return null;

			return new int[] { start, end - start };
		} catch( BadLocationException | IndexOutOfBoundsException | NumberFormatException ex ) {
			ex.printStackTrace();
			return null;
		}
	}

	static boolean isHexDigit( char ch ) {
		return Character.isDigit( ch ) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
	}

	static String colorToString( Color color ) {
		int rgb = color.getRGB();
		int alpha = color.getAlpha();
		String format;
		if( (rgb & 0xf) == ((rgb >> 4) & 0xf) &&
			((rgb >> 8) & 0xf) == ((rgb >> 12) & 0xf) &&
			((rgb >> 16) & 0xf) == ((rgb >> 20) & 0xf) &&
			((rgb >> 24) & 0xf) == ((rgb >> 28) & 0xf) )
		{
			// short format (#RGB or #RGBA)
			format = (alpha != 255) ? "#%03x%01x" : "#%03x";
			rgb = (rgb & 0xf) | ((rgb >> 4) & 0xf0) | ((rgb >> 8) & 0xf00);
			alpha &= 0xf;
		} else {
			// long format (#RRGGBB or #RRGGBBAA)
			format = (alpha != 255) ? "#%06x%02x" : "#%06x";
			rgb &= 0xffffff;
		}

		return String.format( format, rgb, alpha );
	}

	//---- class DuplicateLinesAction -----------------------------------------

	static class DuplicateLinesAction
		extends RecordableTextAction
	{
		private final boolean up;

		public DuplicateLinesAction( String name, boolean up ) {
			super( name );
			this.up = up;
		}

		@Override
		public void actionPerformedImpl( ActionEvent e, RTextArea textArea ) {
			try {
				int selStart = textArea.getSelectionStart();
				int selEnd = textArea.getSelectionEnd();

				int selStartLine = textArea.getLineOfOffset( selStart );
				int selEndLine = textArea.getLineOfOffset( selEnd );

				// check whether selection end is at the beginning of the next line
				if( selEnd > selStart && textArea.getLineOfOffset( selEnd - 1 ) < selEndLine )
					selEndLine--;

				int linesStart = textArea.getLineStartOffset( selStartLine );
				int linesEnd = textArea.getLineEndOffset( selEndLine );

				String linesText = textArea.getText( linesStart, linesEnd - linesStart );
				if( !linesText.endsWith( "\n" ) )
					linesText += "\n";

				textArea.replaceRange( linesText, linesStart, linesStart );

				if( up )
					textArea.select( linesStart, linesStart + linesText.length() - 1 );
				else {
					int newSelStart = linesStart + linesText.length();
					int newSelEnd = newSelStart + linesText.length();
					if( linesText.endsWith( "\n" ) )
						newSelEnd--;
					textArea.select( newSelStart, newSelEnd );
				}
			} catch( BadLocationException ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		public String getMacroID() {
			return getName();
		}
	}

	//---- class IncrementNumberAction ----------------------------------------

	static class IncrementNumberAction
		extends RecordableTextAction
	{
		private final boolean increment;

		IncrementNumberAction( String name, boolean increment ) {
			super( name );
			this.increment = increment;
		}

		@Override
		public void actionPerformedImpl( ActionEvent e, RTextArea textArea ) {
			if( !incrementRGBColor( textArea ) )
				incrementNumber( textArea );
		}

		private void incrementNumber( RTextArea textArea ) {
			try {
				int caretPosition = textArea.getCaretPosition();
				int start = caretPosition;
				int end = caretPosition;

				// find first digit
				for( int i = caretPosition - 1; i >= 0; i-- ) {
					if( !Character.isDigit( textArea.getText( i, 1 ).charAt( 0 ) ) )
						break;
					start = i;
				}

				// find last digit
				int length = textArea.getDocument().getLength();
				for( int i = caretPosition; i < length; i++ ) {
					if( !Character.isDigit( textArea.getText( i, 1 ).charAt( 0 ) ) )
						break;
					end = i + 1;
				}

				if( start == end )
					return;

				// parse number
				String str = textArea.getText( start, end - start );
				long number = Long.parseLong( str );

				// increment/decrement number
				if( increment )
					number++;
				else
					number--;

				if( number < 0 )
					return;

				// update editor
				textArea.replaceRange( Long.toString( number ), start, end );
			} catch( BadLocationException | IndexOutOfBoundsException | NumberFormatException ex ) {
				ex.printStackTrace();
			}
		}

		private boolean incrementRGBColor( RTextArea textArea ) {
			try {
				int caretPosition = textArea.getCaretPosition();
				int[] result = findColorAt( textArea, caretPosition );
				if( result == null )
					return false;

				int start = result[0];
				int len = result[1];

				// find start of color part that should be changed (red, green, blue or alpha)
				int start2;
				int hexDigitCount = (len == 4 || len == 5) ? 1 : 2;
				if( hexDigitCount == 1 ) {
					// #RGB or #RGBA
					start2 = caretPosition - 1;
				} else {
					// #RRGGBB or #RRGGBBAA
					int offset = caretPosition - (start + 1);
					offset += (offset % 2);
					start2 = start + 1 + offset - 2;
				}
				start2 = Math.max( start2, start + 1 );

				// parse number
				String str = textArea.getText( start2, hexDigitCount );
				int number = Integer.parseInt( str, 16 );

				// increment/decrement number
				if( increment )
					number++;
				else
					number--;

				// wrap numbers if less than zero or too large
				int maxNumber = (hexDigitCount == 1) ? 15 : 255;
				if( number < 0 )
					number = maxNumber;
				else if( number > maxNumber )
					number = 0;

				// update editor
				String newStr = String.format( hexDigitCount == 1 ? "%1x" : "%02x", number );
				textArea.replaceRange( newStr, start2, start2 + hexDigitCount );
				return true;
			} catch( BadLocationException | IndexOutOfBoundsException | NumberFormatException ex ) {
				ex.printStackTrace();
				return false;
			}
		}

		@Override
		public String getMacroID() {
			return getName();
		}
	}

	//---- class InsertColorAction --------------------------------------------

	static class InsertColorAction
		extends RecordableTextAction
	{
		private static final String KEY_SELECTED_TAB = "colorchooser.selectedTab";

		private static Point lastLocation;

		InsertColorAction( String name ) {
			super( name );
		}

		@Override
		public void actionPerformedImpl( ActionEvent e, RTextArea textArea ) {
			try {
				// find current color at caret
				Color currentColor = Color.white;
				int caretPosition = textArea.getCaretPosition();
				int start;
				int len = 0;
				String oldStr;
				int[] result = findColorAt( textArea, caretPosition );
				if( result != null ) {
					start = result[0];
					len = result[1];

					oldStr = textArea.getText( start, len );
					int rgb = UIDefaultsLoaderAccessor.parseColorRGBA( oldStr );
					currentColor = new Color( rgb, true );
				} else {
					start = caretPosition;
					oldStr = "";
				}

				// create color chooser
				JColorChooser chooser = new JColorChooser( currentColor );
				Component tabbedPane = chooser.getComponent( 0 );
				Preferences state = Preferences.userRoot().node( FlatThemeFileEditor.PREFS_ROOT_PATH );
				int selectedTab = state.getInt( KEY_SELECTED_TAB, -1 );
				if( tabbedPane instanceof JTabbedPane && selectedTab >= 0 && selectedTab < ((JTabbedPane)tabbedPane).getTabCount() )
					((JTabbedPane)tabbedPane).setSelectedIndex( selectedTab );

				// update editor immediately for live preview
				AtomicInteger length = new AtomicInteger( len );
				AtomicBoolean changed = new AtomicBoolean();
				chooser.getSelectionModel().addChangeListener( e2 -> {
					String str = colorToString( chooser.getColor() );
					((FlatSyntaxTextArea)textArea).runWithoutUndo( () -> {
						textArea.replaceRange( str, start, start + length.get() );
					} );
					length.set( str.length() );
					changed.set( true );
				} );
				Runnable restore = () -> {
					if( changed.get() ) {
						((FlatSyntaxTextArea)textArea).runWithoutUndo( () -> {
							textArea.replaceRange( oldStr, start, start + length.get() );
						} );
						length.set( oldStr.length() );
					}
				};

				// show color chooser dialog
				Window window = SwingUtilities.windowForComponent( textArea );
				JDialog dialog = JColorChooser.createDialog( window, "Insert Color", true, chooser,
					// okListener
					e2 -> {
						// restore original string
						restore.run();

						// update editor
						String newStr = colorToString( chooser.getColor() );
						try {
							if( !newStr.equals( textArea.getText( start, length.get() ) ) )
								textArea.replaceRange( newStr, start, start + length.get() );
						} catch( BadLocationException ex ) {
							ex.printStackTrace();
						}

						// remember selected tab
						if( tabbedPane instanceof JTabbedPane )
							state.putInt( KEY_SELECTED_TAB, ((JTabbedPane)tabbedPane).getSelectedIndex() );
					},
					// cancelListener
					e2 -> {
						// restore original string
						restore.run();
					} );
				if( lastLocation != null )
					dialog.setLocation( lastLocation );
				dialog.setVisible( true );

				lastLocation = dialog.getLocation();
			} catch( BadLocationException | IndexOutOfBoundsException | IllegalArgumentException ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		public String getMacroID() {
			return getName();
		}
	}

	//---- class PickColorAction ----------------------------------------------

	static class PickColorAction
		extends RecordableTextAction
	{
		PickColorAction( String name ) {
			super( name );
		}

		@Override
		public void actionPerformedImpl( ActionEvent e, RTextArea textArea ) {
			try {
				// find current color at caret
				int caretPosition = textArea.getCaretPosition();
				int start;
				int len = 0;
				String oldStr;
				int[] result = findColorAt( textArea, caretPosition );
				if( result != null ) {
					start = result[0];
					len = result[1];

					oldStr = textArea.getText( start, len );
				} else {
					start = caretPosition;
					oldStr = "";
				}

				AtomicInteger length = new AtomicInteger( len );
				AtomicBoolean changed = new AtomicBoolean();

				// show pipette color picker
				Window window = SwingUtilities.windowForComponent( textArea );
				FlatColorPipette.pick( window, true,
					color -> {
						// update editor immediately for live preview
						String str = colorToString( color );
						((FlatSyntaxTextArea)textArea).runWithoutUndo( () -> {
							textArea.replaceRange( str, start, start + length.get() );
						} );
						length.set( str.length() );
						changed.set( true );
					},
					color -> {
						// restore original string
						((FlatSyntaxTextArea)textArea).runWithoutUndo( () -> {
							textArea.replaceRange( oldStr, start, start + length.get() );
						} );
						length.set( oldStr.length() );

						// update editor
						if( color != null ) {
							String newStr = colorToString( color );
							try {
								if( !newStr.equals( textArea.getText( start, length.get() ) ) )
									textArea.replaceRange( newStr, start, start + length.get() );
							} catch( BadLocationException ex ) {
								ex.printStackTrace();
							}
						}
					} );
			} catch( BadLocationException | IndexOutOfBoundsException | NumberFormatException | UnsupportedOperationException | AWTException ex ) {
				ex.printStackTrace();
			}
		}

		@Override
		public String getMacroID() {
			return getName();
		}
	}
}
