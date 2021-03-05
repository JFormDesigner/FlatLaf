package com.formdev.flatlaf;

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

	public static void logSevere( Throwable t ) {
		logSevere( null, t );
	}

	public static void logSevere( String message ) {
		logSevere( message, null );
	}

	public static void logSevere( String message, Throwable t ) {
		if( LOG != null ) {
			LOG.log( Level.SEVERE, message, t );
		} else {
			System.err.println( message );
			t.printStackTrace();
		}
	}

	public static void logConfig( String message, Throwable t ) {
		if( LOG != null ) {
			LOG.log( Level.CONFIG, message, t );
		} else {
			if (Boolean.getBoolean( "flatLaf.logConfig" )) {
				System.err.println( message );
				t.printStackTrace();
			}
		}
	}
}
