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

open class JniHeadersExtension {
	var headers: List<String> = emptyList()
}

val extension = project.extensions.create<JniHeadersExtension>( "flatlafJniHeaders" )


tasks {
	register<Copy>( "jni-headers" ) {
		// depend on :flatlaf-core:compileJava because it generates the JNI headers
		dependsOn( ":flatlaf-core:compileJava" )

		from( project( ":flatlaf-core" ).layout.buildDirectory.dir( "generated/jni-headers" ) )
		into( "src/main/headers" )
		include( extension.headers )
		filter<LfFilterReader>()
	}
}


/**
 * Converts line endings to LF (by removing all CR characters).
 */
class LfFilterReader( reader: java.io.Reader ) : java.io.FilterReader( reader ) {
	override fun read(): Int {
		var c: Int
		do {
			c = super.read()
		} while( c == '\r'.code )
		return c
	}

	override fun read( cbuf: CharArray, off: Int, len: Int ): Int {
		while( true ) {
			val n = super.read( cbuf, off, len )
			if( n <= 0 )
				return n

			var j = off
			for( i in off until off + n ) {
				if( cbuf[i] != '\r' )
					cbuf[j++] = cbuf[i]
			}
			if( j > off )
				return j - off
		}
	}
}
