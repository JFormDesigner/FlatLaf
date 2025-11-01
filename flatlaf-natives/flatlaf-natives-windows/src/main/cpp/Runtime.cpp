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

#include <windows.h>
#include <stdio.h>
#include <stdarg.h>

/**
 * Methods that replace C-runtime methods and allow linking/running without C-runtime.
 * 
 * WARNING: Constructors/destructors of static objects are not invoked!
 *
 * https://documentation.help/Far-Manager/msdnmag-issues-01-01-hood-default.aspx.html
 * www.catch22.net/tuts/win32/reducing-executable-size#the-c-runtime-and-default-libraries
 * https://www.mvps.org/user32/nocrt.html
 * 
 * see also LIBCTINY on "Downloads" page here: http://www.wheaty.net/
 * or https://github.com/leepa/libctiny
 * 
 * @author Karl Tauber
 */

HINSTANCE _instance;

extern "C"
BOOL WINAPI _DllMainCRTStartup( HINSTANCE instance, DWORD reason, LPVOID reserved ) {
	_instance = instance;
	return TRUE;
}

void* __cdecl operator new( size_t cb ) {
//	printf( "new %d\n", cb );
	return ::HeapAlloc( ::GetProcessHeap(), HEAP_ZERO_MEMORY, cb );
}

void* __cdecl operator new[]( size_t cb ) {
//	printf( "new[] %d\n", cb );
	return ::HeapAlloc( ::GetProcessHeap(), HEAP_ZERO_MEMORY, cb );
}

void __cdecl operator delete( void* pv, size_t cb ) {
//	printf( "delete %p %d\n", pv, cb );
	if( pv != NULL )
		::HeapFree( ::GetProcessHeap(), 0, pv );
}

void __cdecl operator delete[]( void* pv ) {
//	printf( "delete[] %p\n", pv );
	if( pv != NULL )
		::HeapFree( ::GetProcessHeap(), 0, pv );
}

/*
extern "C"
int __cdecl printf( const char* format, ... ) {
	char szBuff[1024];
	int retValue;
	DWORD cbWritten;
	va_list argptr;

	va_start( argptr, format );
	retValue = wvsprintfA( szBuff, format, argptr );
	va_end( argptr );

	WriteFile( GetStdHandle( STD_OUTPUT_HANDLE ), szBuff, retValue, &cbWritten, NULL );

	return retValue;
}
*/

size_t rt_wcslen( const wchar_t* str ) {
	const wchar_t* s = str;
	while( *s != '\0' )
		s++;
	return (size_t) (s - str);
}

void rt_wcscpy( wchar_t* dest, const wchar_t* src ) {
	while( *src != '\0' )
		*dest++ = *src++;
	*dest = '\0';
}
