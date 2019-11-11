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

package com.formdev.flatlaf.json;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Karl Tauber
 */
public class Json
{
	public static Object parse( Reader reader )
		throws IOException, ParseException
	{
		DefaultHandler handler = new DefaultHandler();
		new JsonParser( handler ).parse( reader );
		return handler.getValue();
	}

	//---- class DefaultHandler -----------------------------------------------

	static class DefaultHandler
		extends JsonHandler<List<Object>, Map<String, Object>>
	{
		private Object value;

		@Override
		public List<Object> startArray() {
			return new ArrayList<>();
		}

		@Override
		public Map<String, Object> startObject() {
			return new LinkedHashMap<>();
		}

		@Override
		public void endNull() {
			value = "null";
		}

		@Override
		public void endBoolean( boolean bool ) {
			value = bool ? "true" : "false";
		}

		@Override
		public void endString( String string ) {
			value = string;
		}

		@Override
		public void endNumber( String string ) {
			value = string;
		}

		@Override
		public void endArray( List<Object> array ) {
			value = array;
		}

		@Override
		public void endObject( Map<String, Object> object ) {
			value = object;
		}

		@Override
		public void endArrayValue( List<Object> array ) {
			array.add( value );
		}

		@Override
		public void endObjectValue( Map<String, Object> object, String name ) {
			object.put( name, value );
		}

		Object getValue() {
			return value;
		}
	}
}
