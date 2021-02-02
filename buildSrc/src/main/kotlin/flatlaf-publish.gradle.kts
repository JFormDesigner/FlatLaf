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


open class PublishExtension {
	var artifactId: String? = null
	var name: String? = null
	var description: String? = null
}

val extension = project.extensions.create<PublishExtension>( "flatlafPublish" )


plugins {
	`maven-publish`
	id( "com.jfrog.bintray" )
	id( "com.jfrog.artifactory" )
}

publishing {
	publications {
		create<MavenPublication>( "maven" ) {
			afterEvaluate {
				artifactId = extension.artifactId
			}
			groupId = "com.formdev"

			from( components["java"] )

			pom {
				afterEvaluate {
					this@pom.name.set( extension.name )
					this@pom.description.set( extension.description )
				}
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
					connection.set( "scm:git:git://github.com/JFormDesigner/FlatLaf.git" )
					url.set( "https://github.com/JFormDesigner/FlatLaf" )
				}

				issueManagement {
					system.set( "GitHub" )
					url.set( "https://github.com/JFormDesigner/FlatLaf/issues" )
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
		afterEvaluate {
			this@with.name = extension.artifactId
		}
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
