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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Reorders entries in a JAR file so that .properties files are placed before .class files,
 * which is necessary to workaround an issue in NetBeans 11.3 (and older).
 * See issues #13 and #93.
 *
 * @author Karl Tauber
 */
public class ReorderJarEntries
{
	public static void reorderJarEntries( File jarFile )
		throws IOException
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream( (int) jarFile.length() + 1000 );

		try( ZipOutputStream zipOutStream = new ZipOutputStream( outStream ) ) {
			// 1st pass: copy .properties files
			copyFiles( zipOutStream, jarFile, name -> name.endsWith( ".properties" ) );

			// 2nd pass: copy other files
			copyFiles( zipOutStream, jarFile, name -> !name.endsWith( ".properties" ) );
		}

		// replace JAR
		Files.write( jarFile.toPath(), outStream.toByteArray(),
			StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING );
	}

	private static void copyFiles( ZipOutputStream dest, File jarFile, Predicate<String> filter )
		throws IOException
	{
		try( ZipInputStream zipInputStream = new ZipInputStream( new FileInputStream( jarFile ) ) ) {
			ZipEntry entry;
			while( (entry = zipInputStream.getNextEntry()) != null ) {
				if( filter.test( entry.getName() ) ) {
					dest.putNextEntry( entry );
					copyFile( zipInputStream, dest );
				}
			}
		}
	}

	private static void copyFile( InputStream src, OutputStream dest )
		throws IOException
	{
		byte[] buf = new byte[8*1024];
		int len;
		while( (len = src.read( buf )) > 0 )
			dest.write( buf, 0, len );
		dest.flush();
	}
}
