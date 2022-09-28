/*
 * Copyright 2019 FormDev Software GmbH
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

import java.util.Properties

plugins {
	`java-library`
	`flatlaf-toolchain`
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( project( ":flatlaf-swingx" ) )
	implementation( project( ":flatlaf-jide-oss" ) )
	implementation( project( ":flatlaf-intellij-themes" ) )
	implementation( project( ":flatlaf-demo" ) )
//	implementation( project( ":flatlaf-natives-jna" ) )

	implementation( "com.miglayout:miglayout-swing:5.3" )
	implementation( "com.jgoodies:jgoodies-forms:1.9.0" )
	implementation( "org.swinglabs.swingx:swingx-all:1.6.5-1" )
	implementation( "org.swinglabs.swingx:swingx-beaninfo:1.6.5-1" )
	implementation( "com.formdev:jide-oss:3.7.12" )
	implementation( "com.glazedlists:glazedlists:1.11.0" )
	implementation( "org.netbeans.api:org-openide-awt:RELEASE112" )
}

applyLafs()

fun applyLafs() {
	val properties = Properties()
	file( "lafs.properties" ).inputStream().use {
		properties.load( it )
	}

	for( value in properties.values ) {
		value as String
		val parts = value.split( ';' )
		if( parts.size >= 3 )
			dependencies.implementation( parts[2] )
	}
}
