/*
 * Copyright 2024 FormDev Software GmbH
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

package com.formdev.flatlaf.ui;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Provides Java version compatibility methods.
 * <p>
 * WARNING: This is private API and may change.
 *
 * @author Karl Tauber
 * @since 3.3
 */
public class JavaCompatibility2
{
	private static boolean getUIMethodInitialized;
	private static MethodHandle getUIMethod;

	/**
	 * Java 8: getUI() method on various components (e.g. JButton, JList, etc)
	 * <br>
	 * Java 9: javax.swing.JComponent.getUI()
	 */
	public static ComponentUI getUI( JComponent c ) {
		try {
			// Java 9+
			if( SystemInfo.isJava_9_orLater ) {
				if( !getUIMethodInitialized ) {
					getUIMethodInitialized = true;

					try {
						MethodType mt = MethodType.methodType( ComponentUI.class, new Class[0] );
						getUIMethod = MethodHandles.publicLookup().findVirtual( JComponent.class, "getUI", mt );
					} catch( Exception ex ) {
						// ignore
						LoggingFacade.INSTANCE.logSevere( null, ex );
					}
				}

				if( getUIMethod != null )
					return (ComponentUI) getUIMethod.invoke( c );
			}

			// components often used (e.g. as view in scroll panes)
			if( c instanceof JPanel )
				return ((JPanel)c).getUI();
			if( c instanceof JList )
				return ((JList<?>)c).getUI();
			if( c instanceof JTable )
				return ((JTable)c).getUI();
			if( c instanceof JTree )
				return ((JTree)c).getUI();
			if( c instanceof JTextComponent )
				return ((JTextComponent)c).getUI();

			// Java 8 and fallback
			Method m = c.getClass().getMethod( "getUI" );
			return (ComponentUI) m.invoke( c );
		} catch( Throwable ex ) {
			// ignore
			return null;
		}
	}

	/**
	 * Java 8 - 11 on Windows: sun.awt.shell.ShellFolder.get( "fileChooserShortcutPanelFolders" )
	 * <br>
	 * Java 12: javax.swing.filechooser.FileSystemView.getChooserShortcutPanelFiles()
	 *
	 * @since 3.4
	 */
	public static File[] getChooserShortcutPanelFiles( FileSystemView fsv ) {
		try {
			if( SystemInfo.isJava_12_orLater ) {
				Method m = fsv.getClass().getMethod( "getChooserShortcutPanelFiles" );
				File[] files = (File[]) m.invoke( fsv );

				// on macOS and Linux, files consists only of the user home directory
				if( files.length == 1 && files[0].equals( new File( System.getProperty( "user.home" ) ) ) )
					files = new File[0];

				return files;
			} else if( SystemInfo.isWindows ) {
				Class<?> cls = Class.forName( "sun.awt.shell.ShellFolder" );
				Method m = cls.getMethod( "get", String.class );
				return (File[]) m.invoke( null, "fileChooserShortcutPanelFolders" );
			}
		} catch( IllegalAccessException ex ) {
			// do not log because access may be denied via VM option '--illegal-access=deny'
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}

		// fallback
		return new File[0];
	}

	/**
	 * Java 8: sun.awt.shell.ShellFolder.get( "fileChooserComboBoxFolders" )
	 * <br>
	 * Java 9: javax.swing.filechooser.FileSystemView.getChooserComboBoxFiles()
	 *
	 * @since 3.4
	 */
	public static File[] getChooserComboBoxFiles( FileSystemView fsv ) {
		try {
			if( SystemInfo.isJava_9_orLater ) {
				Method m = fsv.getClass().getMethod( "getChooserComboBoxFiles" );
				return (File[]) m.invoke( fsv );
			} else {
				Class<?> cls = Class.forName( "sun.awt.shell.ShellFolder" );
				Method m = cls.getMethod( "get", String.class );
				return (File[]) m.invoke( null, "fileChooserComboBoxFolders" );
			}
		} catch( IllegalAccessException ex ) {
			// do not log because access may be denied via VM option '--illegal-access=deny'
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}

		// fallback
		return new File[0];
	}
}
