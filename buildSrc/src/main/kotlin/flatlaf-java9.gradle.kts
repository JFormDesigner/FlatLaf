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
	java
}

if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
	sourceSets {
		create( "java9" ) {
			java {
				setSrcDirs( listOf( "src/main/java9" ) )
			}
		}
	}

	dependencies {
		add( "java9Implementation", sourceSets.main.get().output )
	}

	tasks {
		named<JavaCompile>( "compileJava9Java" ) {
			sourceCompatibility = "9"
			targetCompatibility = "9"
		}

		jar {
			manifest.attributes( "Multi-Release" to "true" )

			into( "META-INF/versions/9" ) {
				from( sourceSets["java9"].output )
			}
		}
	}
}
