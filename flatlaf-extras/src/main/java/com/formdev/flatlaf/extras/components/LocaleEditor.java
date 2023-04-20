package com.formdev.flatlaf.extras.components;

import java.util.Calendar;
import java.util.Locale;

/**
 * Property editor for locales.
 * 
 * @author SoftwareOrgMX
 */
public class LocaleEditor extends java.beans.PropertyEditorSupport {
	private Locale[] locales;
	private String[] localeStrings;
	private Locale locale;
	private int length;

	/**
	 * Default LocaleEditor constructor.
	 */
	public LocaleEditor() {
		locale = Locale.getDefault();
		locales = Calendar.getAvailableLocales();
		length = locales.length;
		localeStrings = new String[length];
	}

	/**
	 * Returns the locale strings.
	 * 
	 * @return the locale strings
	 */
	public String[] getTags() {
		for (int i = 0; i < length; i++)
			localeStrings[i] = locales[i].getDisplayName();

		return localeStrings;
	}

	/**
	 * Sets the locale strings as text and invokes setValue( locale ).
	 * 
	 * @param text
	 *            the locale string text
	 * 
	 * @throws IllegalArgumentException
	 *             not used
	 */
	public void setAsText(String text) throws IllegalArgumentException {
		for (int i = 0; i < length; i++)
			if (text.equals(locales[i].getDisplayName())) {
				locale = locales[i];
				setValue(locale);

				break;
			}
	}

	/**
	 * Returns the locale string as text.
	 * 
	 * @return the locale string
	 */
	public String getAsText() {
		return locale.getDisplayName();
	}
}