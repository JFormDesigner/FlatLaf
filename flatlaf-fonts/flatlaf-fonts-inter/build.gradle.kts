/*
 * Copyright 2022 FormDev Software GmbH
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

// Version format: <font-version>-<build-number>
//     For maven compatibility, <font-version> should be in format <major>.<minor>[.<micro>].
//     <build-number> is usually '1' and should be incremented only if a new release is
//     necessary, but the <font-version> has not changed.
version = "3.19-1"

plugins {
	`java-library`
	`flatlaf-module-info`
	`flatlaf-publish`
}

dependencies {
	testImplementation( "org.junit.jupiter:junit-jupiter-api:5.7.2" )
	testImplementation( "org.junit.jupiter:junit-jupiter-params" )
	testRuntimeOnly( "org.junit.jupiter:junit-jupiter-engine" )
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks {
	named<Jar>( "sourcesJar" ) {
		exclude( "**/*.ttf", "**/*.otf" )
	}

	test {
		useJUnitPlatform()
		testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
	}
}

flatlafPublish {
	artifactId = "flatlaf-fonts-inter"
	name = "FlatLaf Inter Fonts Pack"
}
