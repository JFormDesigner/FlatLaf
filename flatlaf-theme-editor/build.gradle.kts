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

plugins {
	`java-library`
	`flatlaf-toolchain`
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
	implementation( project( ":flatlaf-extras" ) )
	implementation( project( ":flatlaf-fonts-inter" ) )
	implementation( project( ":flatlaf-fonts-jetbrains-mono" ) )
	implementation( project( ":flatlaf-fonts-roboto" ) )
	implementation( project( ":flatlaf-fonts-roboto-mono" ) )

	implementation( libs.miglayout.swing )
	implementation( libs.fifesoft.rsyntaxtextarea )
	implementation( libs.fifesoft.autocomplete )
}

tasks {
	jar {
		dependsOn( ":flatlaf-core:jar" )
		dependsOn( ":flatlaf-extras:jar" )
		dependsOn( ":flatlaf-fonts-inter:jar" )
		dependsOn( ":flatlaf-fonts-jetbrains-mono:jar" )
		dependsOn( ":flatlaf-fonts-roboto:jar" )
		dependsOn( ":flatlaf-fonts-roboto-mono:jar" )

		manifest {
			attributes( "Main-Class" to "com.formdev.flatlaf.themeeditor.FlatLafThemeEditor" )

			if( JavaVersion.current() >= JavaVersion.VERSION_1_9 )
				attributes( "Multi-Release" to "true" )

			// allow loading FlatLaf native library in Java 24+ (see https://openjdk.org/jeps/472)
			attributes( "Enable-Native-Access" to "ALL-UNNAMED" )
		}

		exclude( "module-info.class" )
		exclude( "META-INF/versions/*/module-info.class" )

		// include all dependencies in jar
		from( {
			configurations.runtimeClasspath.get()
				.filter { it.name.endsWith( "jar" ) }
				.map { zipTree( it ).matching {
					exclude( "META-INF/LICENSE" )
				} }
		} )
	}
}
