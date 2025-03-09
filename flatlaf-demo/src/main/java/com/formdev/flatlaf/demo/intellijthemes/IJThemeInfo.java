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

package com.formdev.flatlaf.demo.intellijthemes;

import java.io.File;

/**
 * @author Karl Tauber
 */
class IJThemeInfo
{
	final String name;
	final String resourceName;
	final boolean discontinued;
	final boolean dark;
	final String license;
	final String licenseFile;
	final String pluginUrl;
	final String sourceCodeUrl;
	final String sourceCodePath;
	final File themeFile;
	final String lafClassName;

	IJThemeInfo( String name, boolean dark, String lafClassName ) {
		this( name, null, false, dark, null, null, null, null, null, null, lafClassName );
	}

	IJThemeInfo( String name, String resourceName, boolean discontinued, boolean dark,
		String license, String licenseFile,
		String pluginUrl, String sourceCodeUrl, String sourceCodePath,
		File themeFile, String lafClassName )
	{
		this.name = name;
		this.resourceName = resourceName;
		this.discontinued = discontinued;
		this.dark = dark;
		this.license = license;
		this.licenseFile = licenseFile;
		this.pluginUrl = pluginUrl;
		this.sourceCodeUrl = sourceCodeUrl;
		this.sourceCodePath = sourceCodePath;
		this.themeFile = themeFile;
		this.lafClassName = lafClassName;
	}
}
