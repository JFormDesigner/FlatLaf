/*
 * Copyright 2021 FormDev Software GmbH
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

#include <windows.h>

/**
 * A simple map that uses a sorted array to store key/value pairs.
 * 
 * @author Karl Tauber
 */

struct Entry
{
	HWND key;
	LPVOID value;
};

class HWNDMap
{
private:
	int size; // used entries in table
	int capacity; // total size of table
	Entry* table;

	// used to synchronize to make it thread safe
	CRITICAL_SECTION criticalSection;

public:
	HWNDMap();

	LPVOID get( HWND key );
	void put( HWND key, LPVOID value );
	void remove( HWND key );

private:
	int binarySearch( HWND key );
	void ensureCapacity( int newCapacity );

//	void dump( char* msg );
};
