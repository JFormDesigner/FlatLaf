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

import java.util.Properties

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
	implementation( project( ":flatlaf-swingx" ) )
	implementation( project( ":flatlaf-jide-oss" ) )
	implementation( project( ":flatlaf-intellij-themes" ) )
	implementation( project( ":flatlaf-demo" ) )
//	implementation( project( ":flatlaf-natives-jna" ) )

	implementation( libs.miglayout.swing )
	implementation( libs.jgoodies.forms )
	implementation( libs.swingx.all )
	implementation( libs.swingx.beaninfo )
	implementation( libs.jide.oss )
	implementation( libs.glazedlists )
	implementation( libs.netbeans.api.awt )

	components.all<TargetJvmVersion8Rule>()
}

applyLafs()

fun applyLafs() {
	val properties = Properties()
	file( "lafs.properties" ).inputStream().use {
		properties.load( it )
	}

	for( value in properties.values ) {
		value as String
		val parts = value.split( ';' )
		if( parts.size >= 3 )
			dependencies.implementation( parts[2] )
	}
}

// rule that overrides 'org.gradle.jvm.version' with '8'
// (required for Radiance, which requires Java 9, but FlatLaf build uses Java 8)
abstract class TargetJvmVersion8Rule : ComponentMetadataRule {
	override fun execute( context: ComponentMetadataContext ) {
		context.details.allVariants {
			attributes.attribute( TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8 )
		}
	}
}
