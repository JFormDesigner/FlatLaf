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
	`maven-publish`
	id( "com.jfrog.bintray" )
	id( "com.jfrog.artifactory" )
}

dependencies {
	implementation( project( ":flatlaf-core" ) )
}

if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
	sourceSets {
		create( "module-info" ) {
			java {
				// include "src/main/java" here to get compile errors if classes are
				// used from other modules that are not specified in module dependencies
				setSrcDirs( listOf( "src/main/module-info", "src/main/java" ) )
			}
		}
	}
}

java {
	withSourcesJar()
	withJavadocJar()
}

tasks {
	if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
		named<JavaCompile>( "compileModuleInfoJava" ) {
			sourceCompatibility = "9"
			targetCompatibility = "9"

			dependsOn( ":flatlaf-core:jar" )

			options.compilerArgs.add( "--module-path" )
			options.compilerArgs.add( project( ":flatlaf-core" ).tasks["jar"].outputs.files.asPath )
		}
	}

	jar {
		if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
			from( sourceSets["module-info"].output ) {
				include( "module-info.class" )
			}
		}
	}

	javadoc {
		options {
			this as StandardJavadocDocletOptions
			tags = listOf( "uiDefault", "clientProperty" )
		}
		isFailOnError = false
	}
}

publishing {
	publications {
		create<MavenPublication>( "maven" ) {
			artifactId = "flatlaf-intellij-themes"
			groupId = "com.formdev"

			from( components["java"] )

			pom {
				name.set( "FlatLaf IntelliJ Themes Pack" )
				description.set( "Flat Look and Feel IntelliJ Themes Pack" )
				url.set( "https://github.com/JFormDesigner/FlatLaf" )

				licenses {
					license {
						name.set( "The Apache License, Version 2.0" )
						url.set( "https://www.apache.org/licenses/LICENSE-2.0.txt" )
					}
				}

				developers {
					developer {
						name.set( "Karl Tauber" )
						organization.set( "FormDev Software GmbH" )
						organizationUrl.set( "https://www.formdev.com/" )
					}
				}

				scm {
					url.set( "https://github.com/JFormDesigner/FlatLaf" )
				}
			}
		}
	}
}

bintray {
	user = rootProject.extra["bintray.user"] as String?
	key = rootProject.extra["bintray.key"] as String?

	setPublications( "maven" )

	with( pkg ) {
		repo = "flatlaf"
		name = "flatlaf-intellij-themes"
		setLicenses( "Apache-2.0" )
		vcsUrl = "https://github.com/JFormDesigner/FlatLaf"

		with( version ) {
			name = project.version.toString()
		}

		publish = rootProject.extra["bintray.publish"] as Boolean
		dryRun = rootProject.extra["bintray.dryRun"] as Boolean
	}
}

artifactory {
	setContextUrl( "https://oss.jfrog.org" )

	publish( closureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
		repository( delegateClosureOf<groovy.lang.GroovyObject> {
			setProperty( "repoKey", "oss-snapshot-local" )
			setProperty( "username", rootProject.extra["bintray.user"] as String? )
			setProperty( "password", rootProject.extra["bintray.key"] as String? )
		} )

		defaults( delegateClosureOf<groovy.lang.GroovyObject> {
			invokeMethod( "publications", "maven" )
			setProperty( "publishArtifacts", true )
			setProperty( "publishPom", true )
		} )
	} )

	resolve( delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig> {
		setProperty( "repoKey", "jcenter" )
	} )
}
