/*
 * Copyright 2020 FormDev Software GmbH
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

package com.formdev.flatlaf.util;

import com.formdev.flatlaf.FlatLaf;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingFacade
{
	private static final Logger LOG = createLogger();

	private static Logger createLogger() {
		try {
			return Logger.getLogger( FlatLaf.class.getName() );
		} catch( Throwable e ) {
			// Module java.logging is not present
			return null;
		}
	}

	public static void logSevere( String message, Throwable t ) {
		if( LOG != null ) {
			LOG.log( Level.SEVERE, message, t );
		} else {
			if( message != null ) {
				System.err.println( message );
			}
			if( t != null ) {
				t.printStackTrace();
			}
		}
	}

	public static void logConfig( String message, Throwable t ) {
		if( LOG != null ) {
			LOG.log( Level.CONFIG, message, t );
		} else {
			if (Boolean.getBoolean( "flatLaf.logConfig" )) {
				if( message != null ) {
					System.err.println( message );
				}
				if( t != null ) {
					t.printStackTrace();
				}
			}
		}
	}
}
