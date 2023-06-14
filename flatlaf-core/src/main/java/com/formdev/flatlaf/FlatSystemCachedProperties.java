package com.formdev.flatlaf;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Caches system properties
 * @author Ryan Cuprak
 */
public class FlatSystemCachedProperties
{

	/**
	 * True if the cache is to be used
	 */
	private static boolean useCache = false;

	/**
	 * Cache Properties
	 */
	private final static Map<String,String> cachedSystemProperties = new HashMap<>();

	/**
	 * Environment properties
	 */
	private final static Map<String,String> envSystemProperties = new HashMap<>();

	/**
	 * Enables or disables using a cache
	 * @param useCache - true if to use cache
	 */
	public static void setUseCache(boolean useCache) {
		FlatSystemCachedProperties.useCache = useCache;
		Properties properties = System.getProperties();
		for(String str : properties.stringPropertyNames()) {
			cachedSystemProperties.put(str,System.getProperty(str));
		}
		Map<String,String> props = System.getenv();
		for(String str : props.keySet()) {
			envSystemProperties.put( str , System.getenv(str) );
		}
	}

	/**
	 * Returns the property
	 * @param property - property
	 * @return property value
	 */
	public static String getProperty(String property, String defaultValue) {
		if(useCache) {
			return cachedSystemProperties.get( property ) == null ? defaultValue : cachedSystemProperties.get( property );
		}
		return System.getProperty( property , defaultValue );
	}

	public static String getProperty(String property) {
		if(useCache) {
			return cachedSystemProperties.get( property );
		}
		return System.getProperty( property );
	}

	/**
	 * Returns an environment property
	 * @param property - property
	 * @return String
	 */
	public static String getenv(String property) {
		if(useCache) {
			return envSystemProperties.get( property );
		}
		return System.getenv(property);
	}

	/**
	 * Sets properties
	 * @param property - property to be set
	 * @param value - value of the property
	 */
	public static void setProperty(String property, String value) {
		if(useCache) {
			cachedSystemProperties.put( property,value );
		} else {
			System.setProperty( property , value );
		}
	}

	/**
	 * Clears the property
	 * @param property - property
	 */
	public static void clearProperty ( String property ) {
		if(useCache) {
			cachedSystemProperties.remove( property );
		} else {
			System.clearProperty( property );
		}
	}
}

