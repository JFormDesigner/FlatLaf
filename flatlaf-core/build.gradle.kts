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
	`flatlaf-module-info`
	`flatlaf-java9`
	`flatlaf-publish`
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks {
	jar {
		archiveBaseName.set( "flatlaf" )

		doLast {
			ReorderJarEntries.reorderJarEntries( outputs.files.singleFile );
		}
	}

	javadoc {
		options {
			this as StandardJavadocDocletOptions
			tags = listOf( "uiDefault", "clientProperty" )
			addStringOption( "Xdoclint:all,-missing", "-Xdoclint:all,-missing" )
		}
		isFailOnError = false
	}

	named<Jar>("sourcesJar" ) {
		archiveBaseName.set( "flatlaf" )
	}

	named<Jar>("javadocJar" ) {
		archiveBaseName.set( "flatlaf" )
	}
}

flatlafPublish {
	artifactId = "flatlaf"
	name = "FlatLaf"
	description = "Flat Look and Feel"
}
