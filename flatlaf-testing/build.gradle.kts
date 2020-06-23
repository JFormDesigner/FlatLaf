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

plugins {
	`java-library`
}

repositories {
	maven {
		// for using MigLayout snapshot
		url = uri( "https://oss.sonatype.org/content/repositories/snapshots/" )
	}
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( project( ":flatlaf-swingx" ) )
	implementation( project( ":flatlaf-jide-oss" ) )
	implementation( project( ":flatlaf-intellij-themes" ) )
	implementation( project( ":flatlaf-demo" ) )

	implementation( "com.miglayout:miglayout-swing:5.3-SNAPSHOT" )
	implementation( "com.jgoodies:jgoodies-forms:1.9.0" )
	implementation( "org.swinglabs.swingx:swingx-all:1.6.5-1" )
	implementation( "org.swinglabs.swingx:swingx-beaninfo:1.6.5-1" )
	implementation( "com.jidesoft:jide-oss:3.6.18" )
	implementation( "com.glazedlists:glazedlists:1.11.0" )
	implementation( "org.netbeans.api:org-openide-awt:RELEASE112" )

//	implementation( "org.pushing-pixels:radiance-substance:3.0.0" )
//	implementation( "com.weblookandfeel:weblaf-ui:1.2.13" )
//	implementation( "com.jgoodies:jgoodies-looks:2.7.0" )
}
