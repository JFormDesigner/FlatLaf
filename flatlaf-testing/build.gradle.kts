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

version = rootProject.version

plugins {
	`java-library`
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( project( ":flatlaf-swingx" ) )
	implementation( project( ":flatlaf-jide-oss" ) )
	implementation( project( ":flatlaf-demo" ) )

	implementation( "com.miglayout:miglayout-swing:5.2" )
	implementation( "com.jgoodies:jgoodies-forms:1.9.0" )
	implementation( "org.swinglabs.swingx:swingx-all:1.6.5-1" )
	implementation( "org.swinglabs.swingx:swingx-beaninfo:1.6.5-1" )
	implementation( "com.jidesoft:jide-oss:3.6.18" )
	implementation( "org.netbeans.api:org-openide-awt:RELEASE112" )
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}
