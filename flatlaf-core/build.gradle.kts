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
	`maven-publish`
	id( "com.jfrog.bintray" )
	id( "com.jfrog.artifactory" )
}

if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
	sourceSets {
		create( "java9" ) {
			java {
				setSrcDirs( listOf( "src/main/java9" ) )
			}
		}
		create( "module-info" ) {
			java {
				// include "src/main/java" here to get compile errors if classes are
				// used from other modules that are not specified in module dependencies
				setSrcDirs( listOf( "src/main/module-info", "src/main/java" ) )
			}
		}
	}
}

tasks {
	assemble {
		dependsOn(
			"sourcesJar",
			"javadocJar"
		)
	}

	if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
		named<JavaCompile>( "compileModuleInfoJava" ) {
			sourceCompatibility = "9"
			targetCompatibility = "9"
		}
	}

	jar {
		archiveBaseName.set( "flatlaf" )

		if( JavaVersion.current() >= JavaVersion.VERSION_1_9 ) {
			manifest.attributes( "Multi-Release" to "true" )

			into( "META-INF/versions/9" ) {
				from( sourceSets["java9"].output )
			}

			from( sourceSets["module-info"].output ) {
				include( "module-info.class" )
			}
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

	register( "sourcesJar", Jar::class ) {
		archiveBaseName.set( "flatlaf" )
		archiveClassifier.set( "sources" )

		from( sourceSets.main.get().allJava )
	}

	register( "javadocJar", Jar::class ) {
		archiveBaseName.set( "flatlaf" )
		archiveClassifier.set( "javadoc" )

		from( javadoc )
	}
}

publishing {
	publications {
		create<MavenPublication>( "maven" ) {
			artifactId = "flatlaf"
			groupId = "com.formdev"

			from( components["java"] )

			artifact( tasks["sourcesJar"] )
			artifact( tasks["javadocJar"] )

			pom {
				name.set( "FlatLaf" )
				description.set( "Flat Look and Feel" )
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
		name = "flatlaf"
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
