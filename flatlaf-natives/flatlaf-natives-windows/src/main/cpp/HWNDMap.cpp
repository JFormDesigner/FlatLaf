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

// avoid inlining of printf()
#define _NO_CRT_STDIO_INLINE

#include <stdio.h>
#include "HWNDMap.h"

#define DEFAULT_CAPACITY		20
#define INCREASE_CAPACITY		10

/**
 * @author Karl Tauber
 */

class LOCK {
	LPCRITICAL_SECTION lpCriticalSection;

public:
	LOCK( LPCRITICAL_SECTION lpCriticalSection ) {
		this->lpCriticalSection = lpCriticalSection;
		::EnterCriticalSection( lpCriticalSection );
	}
	~LOCK() {
		::LeaveCriticalSection( lpCriticalSection );
	}
};


HWNDMap::HWNDMap() {
	size = 0;
	capacity = 0;
	table = NULL;

	::InitializeCriticalSection( &criticalSection );

//	dump( "<init>" );
}

LPVOID HWNDMap::get( HWND key ) {
	LOCK lock( &criticalSection );

	int index = binarySearch( key );
	return (index >= 0) ? table[index].value : NULL;
}

bool HWNDMap::put( HWND key, LPVOID value ) {
	LOCK lock( &criticalSection );

	int index = binarySearch( key );
//	printf( "put %p %p = %d --\n", key, value, index );
	if( index >= 0 ) {
		// key already in map --> replace
		table[index].value = value;
	} else {
		// insert new key
		if( !ensureCapacity() )
			return false;

		// make room for new entry
		index = -(index + 1);
		for( int i = size - 1; i >= index; i-- )
			table[i + 1] = table[i];
		size++;

		// insert entry
		table[index].key = key;
		table[index].value = value;
	}

//	dump( "put" );
	return true;
}

void HWNDMap::remove( HWND key ) {
	LOCK lock( &criticalSection );

	// search for key
	int index = binarySearch( key );
//	printf( "remove %p = %d --\n", key, index );
	if( index < 0 )
		return;

	// remove entry
	for( int i = index + 1; i < size; i++ )
		table[i - 1] = table[i];
	size--;

//	dump( "remove" );
}

int HWNDMap::binarySearch( HWND key ) {
	if( table == NULL )
		return -1;

	__int64 ikey = reinterpret_cast<__int64>( key );
	int low = 0;
	int high = size - 1;

	while( low <= high ) {
		int mid = (low + high) >> 1;

		__int64 midKey = reinterpret_cast<__int64>( table[mid].key );
		if( midKey < ikey )
			low = mid + 1;
		else if( midKey > ikey )
			high = mid - 1;
		else
			return mid;
	}

	return -(low + 1);
}

bool HWNDMap::ensureCapacity() {
	if( table == NULL ) {
		table = new Entry[DEFAULT_CAPACITY];
		if( table == NULL )
			return false;

		capacity = DEFAULT_CAPACITY;
		return true;
	}

	// check capacity
	int minCapacity = size + 1;
	if( minCapacity <= capacity )
		return true;

	// allocate new table
	int newCapacity = minCapacity + INCREASE_CAPACITY;
	Entry* newTable = new Entry[newCapacity];
	if( newTable == NULL )
		return false;

	// copy old table to new table
	for( int i = 0; i < capacity; i++ )
		newTable[i] = table[i];

	// delete old table
	delete[] table;

	table = newTable;
	capacity = newCapacity;
	return true;
}

/*
void HWNDMap::dump( char* msg ) {
	printf( "---- %s -----------------------\n", msg );
	printf( "size     %d\n", size );
	printf( "capacity %d\n", capacity );
	printf( "table    %p\n", table );

	for( int i = 0; i < capacity; i++ )
		printf( "  %d: %p - %p  %s\n", i, table[i].key, table[i].value, i >= size ? "UNUSED" : "" );
}
*/
