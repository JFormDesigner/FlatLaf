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

rootProject.name = "FlatLaf"

include( "flatlaf-core" )
include( "flatlaf-extras" )
include( "flatlaf-swingx" )
include( "flatlaf-jide-oss" )
include( "flatlaf-intellij-themes" )
include( "flatlaf-demo" )
include( "flatlaf-testing" )
include( "flatlaf-theme-editor" )

includeProject( "flatlaf-fonts-inter",          "flatlaf-fonts/flatlaf-fonts-inter" )
includeProject( "flatlaf-fonts-jetbrains-mono", "flatlaf-fonts/flatlaf-fonts-jetbrains-mono" )
includeProject( "flatlaf-fonts-roboto",         "flatlaf-fonts/flatlaf-fonts-roboto" )
includeProject( "flatlaf-fonts-roboto-mono",    "flatlaf-fonts/flatlaf-fonts-roboto-mono" )

includeProject( "flatlaf-natives-windows", "flatlaf-natives/flatlaf-natives-windows" )
includeProject( "flatlaf-natives-macos",   "flatlaf-natives/flatlaf-natives-macos" )
includeProject( "flatlaf-natives-linux",   "flatlaf-natives/flatlaf-natives-linux" )
includeProject( "flatlaf-natives-jna",     "flatlaf-natives/flatlaf-natives-jna" )

includeProject( "flatlaf-testing-modular-app", "flatlaf-testing/flatlaf-testing-modular-app" )

fun includeProject( projectPath: String, projectDir: String ) {
	include( projectPath )
	project( ":$projectPath" ).projectDir = file( projectDir )
}


// for using newer Java version via toolchain
plugins {
	id( "org.gradle.toolchains.foojay-resolver-convention" ) version( "0.5.0" )
}
