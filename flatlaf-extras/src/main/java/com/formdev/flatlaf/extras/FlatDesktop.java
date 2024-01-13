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

package com.formdev.flatlaf.extras;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;
import com.formdev.flatlaf.util.LoggingFacade;
import com.formdev.flatlaf.util.SystemInfo;

/**
 * Supports interaction with desktop.
 * <p>
 * <strong>Note</strong>: If you application requires Java 9 or later,
 * then use class {@link java.awt.Desktop} instead of this class.
 *
 * @author Karl Tauber
 * @since 2
 */
public class FlatDesktop
{
	public enum Action { APP_ABOUT, APP_PREFERENCES, APP_QUIT_HANDLER }

	/**
	 * Checks whether the given action is supported on the current platform.
	 */
	public static boolean isSupported( Action action ) {
		if( SystemInfo.isJava_9_orLater ) {
			try {
				return Desktop.getDesktop().isSupported( Enum.valueOf( Desktop.Action.class, action.name() ) );
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
				return false;
			}
		} else if( SystemInfo.isMacOS )
			return true;
		else
			return false;
	}

	/**
	 * Sets a handler to show a custom About dialog.
	 * <p>
	 * Useful for macOS to enable menu item "MyApp &gt; About".
	 * <p>
	 * Uses:
	 * <ul>
	 * <li>Java 8 on macOS: com.apple.eawt.Application.getApplication().setAboutHandler(com.apple.eawt.AboutHandler)
	 * <li>Java 9+: java.awt.Desktop.getDesktop().setAboutHandler(java.awt.desktop.AboutHandler)
	 * </ul>
	 */
	public static void setAboutHandler( Runnable aboutHandler ) {
		if( !isSupported( Action.APP_ABOUT ) )
			return;

		String handlerClassName;
		if( SystemInfo.isJava_9_orLater )
			handlerClassName = "java.awt.desktop.AboutHandler";
		else if( SystemInfo.isMacOS )
			handlerClassName = "com.apple.eawt.AboutHandler";
		else
			return;

		setHandler( "setAboutHandler", handlerClassName, aboutHandler );
	}

	/**
	 * Sets a handler to show a custom Preferences dialog.
	 * <p>
	 * Useful for macOS to enable menu item "MyApp &gt; Preferences".
	 * <p>
	 * Uses:
	 * <ul>
	 * <li>Java 8 on macOS: com.apple.eawt.Application.getApplication().setPreferencesHandler(com.apple.eawt.PreferencesHandler)
	 * <li>Java 9+: java.awt.Desktop.getDesktop().setPreferencesHandler(java.awt.desktop.PreferencesHandler)
	 * </ul>
	 */
	public static void setPreferencesHandler( Runnable preferencesHandler ) {
		if( !isSupported( Action.APP_PREFERENCES ) )
			return;

		String handlerClassName;
		if( SystemInfo.isJava_9_orLater )
			handlerClassName = "java.awt.desktop.PreferencesHandler";
		else if( SystemInfo.isMacOS )
			handlerClassName = "com.apple.eawt.PreferencesHandler";
		else
			return;

		setHandler( "setPreferencesHandler", handlerClassName, preferencesHandler );
	}

	private static void setHandler( String setHandlerMethodName, String handlerClassName,
		Runnable handler )
	{
		try {
			Object desktopOrApplication = getDesktopOrApplication();
			Class<?> handlerClass = Class.forName( handlerClassName );

			Method m = desktopOrApplication.getClass().getMethod( setHandlerMethodName, handlerClass );
			m.invoke( desktopOrApplication, Proxy.newProxyInstance( FlatDesktop.class.getClassLoader(),
				new Class[] { handlerClass },
				(proxy, method, args) -> {
					// Use invokeLater to release the listener firing for the case
					// that the action listener shows a modal dialog.
					// This (hopefully) prevents application hanging.
					EventQueue.invokeLater( () -> {
						handler.run();
					} );
					return null;
				} ) );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	/**
	 * Sets a handler which is invoked when the application should quit.
	 * The handler must invoke either {@link QuitResponse#performQuit} or
	 * {@link QuitResponse#cancelQuit}.
	 * <p>
	 * Useful for macOS to get notified when user clicks menu item "MyApp &gt; Quit".
	 * <p>
	 * Uses:
	 * <ul>
	 * <li>Java 8 on macOS: com.apple.eawt.Application.getApplication().setQuitHandler(com.apple.eawt.QuitHandler)
	 * <li>Java 9+: java.awt.Desktop.getDesktop().setQuitHandler(java.awt.desktop.QuitHandler)
	 * </ul>
	 */
	public static void setQuitHandler( Consumer<QuitResponse> quitHandler ) {
		if( !isSupported( Action.APP_QUIT_HANDLER ) )
			return;

		String handlerClassName;
		if( SystemInfo.isJava_9_orLater )
			handlerClassName = "java.awt.desktop.QuitHandler";
		else if( SystemInfo.isMacOS )
			handlerClassName = "com.apple.eawt.QuitHandler";
		else
			return;

		try {
			Object desktopOrApplication = getDesktopOrApplication();
			Class<?> handlerClass = Class.forName( handlerClassName );

			Method m = desktopOrApplication.getClass().getMethod( "setQuitHandler", handlerClass );
			m.invoke( desktopOrApplication, Proxy.newProxyInstance( FlatDesktop.class.getClassLoader(),
				new Class[] { handlerClass },
				(proxy, method, args) -> {
					Object response = args[1];
					String responseClass = SystemInfo.isJava_9_orLater
						? "java.awt.desktop.QuitResponse"
						: "com.apple.eawt.QuitResponse";
					quitHandler.accept( new QuitResponse() {
						@Override
						public void performQuit() {
							try {
								Class.forName( responseClass ).getMethod( "performQuit" ).invoke( response );
							} catch( Exception ex ) {
								LoggingFacade.INSTANCE.logSevere( null, ex );
							}
						}

						@Override
						public void cancelQuit() {
							try {
								Class.forName( responseClass ).getMethod( "cancelQuit" ).invoke( response );
							} catch( Exception ex ) {
								LoggingFacade.INSTANCE.logSevere( null, ex );
							}
						}

					} );
					return null;
				} ) );
		} catch( Exception ex ) {
			LoggingFacade.INSTANCE.logSevere( null, ex );
		}
	}

	private static Object getDesktopOrApplication() throws Exception {
		if( SystemInfo.isJava_9_orLater )
			return Desktop.getDesktop();
		else if( SystemInfo.isMacOS ) {
			try {
				Class<?> cls = Class.forName( "com.apple.eawt.Application" );
				return cls.getMethod( "getApplication" ).invoke( null );
			} catch( Exception ex ) {
				LoggingFacade.INSTANCE.logSevere( null, ex );
				throw new UnsupportedOperationException();
			}
		} else
			throw new UnsupportedOperationException();
	}

	//---- interface QuitResponse ---------------------------------------------

	public interface QuitResponse {
		void performQuit();
		void cancelQuit();
	}
}
