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
	`flatlaf-toolchain`
	`flatlaf-publish`
}

dependencies {
	implementation( project( ":flatlaf-core" ) )

	// use compileOnly() because there are various JIDE libraries available on Maven Central
	compileOnly( libs.jide.oss )
}

java {
	withSourcesJar()
	withJavadocJar()
}

flatlafPublish {
	artifactId = "flatlaf-jide-oss"
	name = "FlatLaf addon for JIDE Common Layer"
	description = "Flat Look and Feel addon for JIDE Common Layer"
}
