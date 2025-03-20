/*
 * Copyright 2025 FormDev Software GmbH
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
#include "JNIUtils.h"
#include "com_formdev_flatlaf_ui_FlatNativeWindowsLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

#define ID_BUTTON1		101

// declare external fields
extern HINSTANCE _instance;

// declare internal methods
static byte* createInMemoryTemplate( HWND owner, int messageType, LPCWSTR title, LPCWSTR text,
	int defaultButton, int buttonCount, LPCWSTR* buttons );
static INT_PTR CALLBACK messageDialogProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam );
static int textLengthAsDLUs( HDC hdc, LPCWSTR str, int strLen );
static LONG pixel2dluX( LONG px );
static LONG pixel2dluY( LONG px );
static LONG dluX2pixel( LONG dluX );
static LPWORD lpwAlign( LPWORD lpIn );


extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_showMessageBox
	( JNIEnv* env, jclass cls, jlong hwndParent, jstring text, jstring caption, jint type )
{
	// convert Java strings to C strings
	AutoReleaseString ctext( env, text );
	AutoReleaseString ccaption( env, caption );

	return ::MessageBox( reinterpret_cast<HWND>( hwndParent ), ctext, ccaption, type );
}

extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatNativeWindowsLibrary_showMessageDialog
	( JNIEnv* env, jclass cls, jlong hwndParent, jint messageType, jstring title,
		jstring text, jint defaultButton, jobjectArray buttons )
{
	HWND owner = reinterpret_cast<HWND>( hwndParent );

	// convert Java strings to C strings
	AutoReleaseString ctitle( env, title );
	AutoReleaseString ctext( env, text );
	AutoReleaseStringArray cbuttons( env, buttons );

	// get title from parent window if necessary
	WCHAR parentTitle[100];
	if( ctitle == NULL )
		::GetWindowText( owner, parentTitle, 100 );

	byte* templ = createInMemoryTemplate( owner, messageType, (ctitle != NULL) ? ctitle : parentTitle,
		ctext, defaultButton, cbuttons.count, cbuttons );
	if( templ == NULL )
		return -1;

	LRESULT ret = ::DialogBoxIndirect( _instance, (LPDLGTEMPLATE) templ, owner, messageDialogProc );
	delete templ;
	return (ret >= ID_BUTTON1) ? ret - ID_BUTTON1 : -1;
}


// all values in DLUs

#define INSETS_TOP				12
#define INSETS_LEFT				12
#define INSETS_RIGHT			12
#define INSETS_BOTTOM			6

#define ICON_TEXT_GAP			8

#define LABEL_MIN_WIDTH			100
#define LABEL_MAX_WIDTH			250
#define LABEL_HEIGHT			8

#define BUTTON_WIDTH			50
#define BUTTON_HEIGHT			12
#define BUTTON_GAP				5
#define BUTTON_TOP_GAP			14
#define BUTTON_LEFT_RIGHT_GAP	8

// based on https://learn.microsoft.com/en-us/windows/win32/dlgbox/using-dialog-boxes#creating-a-template-in-memory
static byte* createInMemoryTemplate( HWND owner, int messageType, LPCWSTR title, LPCWSTR text,
	int defaultButton, int buttonCount, LPCWSTR* buttons )
{
	// get font info needed for DS_SETFONT
	NONCLIENTMETRICS ncMetrics;
	ncMetrics.cbSize = sizeof( NONCLIENTMETRICS );
	if( !::SystemParametersInfo( SPI_GETNONCLIENTMETRICS, 0, &ncMetrics, 0 ) )
		return NULL;

	// create DC to use message font
	HDC hdcOwner = ::GetDC( owner );
	HDC hdc = ::CreateCompatibleDC( hdcOwner );
	::ReleaseDC( owner, hdcOwner );
	if( hdc == NULL )
		return NULL;

	HFONT hfont = ::CreateFontIndirect( &ncMetrics.lfMessageFont );
	if( hfont == NULL ) {
		::DeleteDC( hdc );
		return NULL;
	}

	if( ::SelectObject( hdc, hfont ) == NULL ) {
		::DeleteDC( hdc );
		::DeleteObject( hfont );
		return NULL;
	}

	//---- calculate layout (in DLUs) ----

	// layout icon
	LPWSTR icon;
	switch( messageType ) {
		case /* JOptionPane.ERROR_MESSAGE */       0: icon = IDI_ERROR; break;
		case /* JOptionPane.INFORMATION_MESSAGE */ 1: icon = IDI_INFORMATION; break;
		case /* JOptionPane.WARNING_MESSAGE */     2: icon = IDI_WARNING; break;
		case /* JOptionPane.QUESTION_MESSAGE */    3: icon = IDI_QUESTION; break;
		default:
		case /* JOptionPane.PLAIN_MESSAGE */      -1: icon = NULL; break;
	}
	int ix = INSETS_LEFT;
	int iy = INSETS_TOP;
	int iw = pixel2dluX( ::GetSystemMetrics( SM_CXICON ) );
	int ih = pixel2dluY( ::GetSystemMetrics( SM_CYICON ) );

	// layout text
	int tx = ix + (icon != NULL ? iw + ICON_TEXT_GAP : 0);
	int ty = iy;
	int tw = 0;
	int th = 0;
	if( text == NULL )
		text = L"";
	LPWSTR wrappedText = new WCHAR[wcslen( text ) + 1];
	wcscpy( wrappedText, text );
	LPWSTR lineStart = wrappedText;
	for( LPWSTR t = wrappedText; ; t++ ) {
		if( *t != '\n' && *t != 0 )
			continue;

		// calculate line width (in pixels) and number of charaters that fit into LABEL_MAX_WIDTH
		int lineLen = t - lineStart;
		int fit = 0;
		SIZE size{ 0 };
		if( !::GetTextExtentExPoint( hdc, lineStart, lineLen, dluX2pixel( LABEL_MAX_WIDTH ), &fit, NULL, &size ) )
			break;

		if( fit < lineLen ) {
			// wrap too long line --> try to wrap at space character
			bool wrapped = false;
			for( LPWSTR t2 = lineStart + fit - 1; t2 > lineStart; t2-- ) {
				if( *t2 == ' ' || *t2 == '\t' ) {
					*t2 = '\n';
					int w = textLengthAsDLUs( hdc, lineStart, t2 - lineStart );
					tw = max( tw, w );
					th += LABEL_HEIGHT;

					// continue wrapping after inserted line break
					t = t2;
					lineStart = t + 1;
					wrapped = true;
					break;
				}
			}
			if( !wrapped ) {
				// not able to wrap at word --> break long word
				int breakIndex = (lineStart + fit) - wrappedText;
				int w = textLengthAsDLUs( hdc, lineStart, breakIndex );
				tw = max( tw, w );
				th += LABEL_HEIGHT;

				// duplicate string
				LPWSTR wrappedText2 = new WCHAR[wcslen( wrappedText ) + 1 + 1];
				// use wcscpy(), instead of wcsncpy(), because this method is inlined and does not require linking to runtime lib
				wcscpy( wrappedText2, wrappedText );
				wrappedText2[breakIndex] = '\n';
				wcscpy( wrappedText2 + breakIndex + 1, wrappedText + breakIndex );

				// delete old text
				delete[] wrappedText;
				wrappedText = wrappedText2;

				// continue wrapping after inserted line break
				t = wrappedText + breakIndex;
				lineStart = t + 1;
			}
		} else {
			// line fits into LABEL_MAX_WIDTH
			int w = pixel2dluX( size.cx );
			tw = max( tw, w );
			th += LABEL_HEIGHT;
			lineStart = t + 1;
		}

		if( *t == 0 )
			break;
	}
	tw = min( max( tw, LABEL_MIN_WIDTH ), LABEL_MAX_WIDTH );
	th = max( th, LABEL_HEIGHT );
	if( icon != NULL && th < ih )
		ty += (ih - th) / 2; // vertically center text

	// layout buttons
	int* bw = new int[buttonCount];
	int buttonTotalWidth = BUTTON_GAP * (buttonCount - 1);
	for( int i = 0; i < buttonCount; i++ ) {
		int w = textLengthAsDLUs( hdc, buttons[i], -1 ) + 16;
		bw[i] = max( BUTTON_WIDTH, w );
		buttonTotalWidth += bw[i];
	}

	// layout dialog
	int dx = 0;
	int dy = 0;
	int dw = max( tx + tw + INSETS_RIGHT, BUTTON_LEFT_RIGHT_GAP + buttonTotalWidth + BUTTON_LEFT_RIGHT_GAP );
	int dh = max( iy + ih, ty + th ) + BUTTON_TOP_GAP + BUTTON_HEIGHT + INSETS_BOTTOM;

	// center dialog in owner
	RECT ownerRect{ 0 };
	if( ::GetClientRect( owner, &ownerRect ) ) {
		dx = (pixel2dluX( ownerRect.right - ownerRect.left ) - dw) / 2;
		dy = (pixel2dluY( ownerRect.bottom - ownerRect.top ) - dh) / 2;
	}

	// layout button area
	int bx = dw - buttonTotalWidth - BUTTON_LEFT_RIGHT_GAP;
	int by = dh - BUTTON_HEIGHT - INSETS_BOTTOM;

	// get font info needed for DS_SETFONT
	int fontPointSize = (ncMetrics.lfMessageFont.lfHeight < 0)
		? -MulDiv( ncMetrics.lfMessageFont.lfHeight, 72, ::GetDeviceCaps( hdc, LOGPIXELSY ) )
		: ncMetrics.lfMessageFont.lfHeight;
	LPCWSTR fontFaceName = ncMetrics.lfMessageFont.lfFaceName;

	// delete DC and font
	::DeleteDC( hdc );
	::DeleteObject( hfont );

	// (approximately) calculate memory size needed for in-memory template
	int templSize = (sizeof(DLGTEMPLATE) + /*menu*/ 2 + /*class*/ 2 + /*title*/ 2)
		+ ((sizeof(DLGITEMTEMPLATE) + /*class*/ 4 + /*title/icon*/ 4 + /*creation data*/ 2) * (/*icon+text*/2 + buttonCount))
		+ (title != NULL ? (wcslen( title ) + 1) * sizeof(wchar_t) : 0)
		+ /*fontPointSize*/ 2 + ((wcslen( fontFaceName ) + 1) * sizeof(wchar_t))
		+ ((wcslen( wrappedText ) + 1) * sizeof(wchar_t));
	for( int i = 0; i < buttonCount; i++ )
		templSize += ((wcslen( buttons[i] ) + 1) * sizeof(wchar_t));

	templSize += (2 * (1 + 1 + buttonCount)); // necessary for DWORD alignment
	templSize += 100; // some reserve

	// allocate memory for in-memory template
	byte* templ = new byte[templSize];
	if( templ == NULL )
		return NULL;


	//---- define dialog box ----

	LPDLGTEMPLATE lpdt = (LPDLGTEMPLATE) templ;
	lpdt->style = WS_POPUP | WS_BORDER | WS_SYSMENU | DS_MODALFRAME | WS_CAPTION | DS_SETFONT;
	lpdt->cdit = /*text*/ 1 + buttonCount; // number of controls
	lpdt->x = dx;
	lpdt->y = dy;
	lpdt->cx = dw;
	lpdt->cy = dh;

	LPWORD lpw = (LPWORD) (lpdt + 1);
	*lpw++ = 0; // no menu
	*lpw++ = 0; // predefined dialog box class (by default)
	if( title != NULL ) {
		wcscpy( (LPWSTR) lpw, title );
		lpw += wcslen( title ) + 1;
	} else
		*lpw++ = 0; // no title

	// for DS_SETFONT
	*lpw++ = fontPointSize;
	wcscpy( (LPWSTR) lpw, fontFaceName );
	lpw += wcslen( fontFaceName ) + 1;

	//---- define icon ----

	if( icon != NULL ) {
		lpdt->cdit++;

		lpw = lpwAlign( lpw );
		LPDLGITEMTEMPLATE lpdit = (LPDLGITEMTEMPLATE) lpw;
		lpdit->x = ix;
		lpdit->y = iy;
		lpdit->cx = iw;
		lpdit->cy = ih;
		lpdit->id = ID_BUTTON1 - 1;
		lpdit->style = WS_CHILD | WS_VISIBLE | SS_ICON;

		lpw = (LPWORD) (lpdit + 1);
		*lpw++ = 0xffff; *lpw++ = 0x0082;		// Static class
		*lpw++ = 0xffff; *lpw++ = (WORD) icon;	// icon
		*lpw++ = 0;								// creation data
	}


	//---- define text ----

	lpw = lpwAlign( lpw );
	LPDLGITEMTEMPLATE lpdit = (LPDLGITEMTEMPLATE) lpw;
	lpdit->x = tx;
	lpdit->y = ty;
	lpdit->cx = tw;
	lpdit->cy = th;
	lpdit->id = ID_BUTTON1 - 2;
	lpdit->style = WS_CHILD | WS_VISIBLE | SS_LEFT | SS_NOPREFIX | SS_EDITCONTROL;

	lpw = (LPWORD) (lpdit + 1);
	*lpw++ = 0xffff; *lpw++ = 0x0082;										// Static class
	wcscpy( (LPWSTR) lpw, wrappedText ); lpw += wcslen( wrappedText ) + 1;	// text
	*lpw++ = 0;																// creation data


	//---- define buttons ----

	defaultButton = min( max( defaultButton, 0 ), buttonCount - 1 );
	int buttonId = ID_BUTTON1;
	for( int i = 0; i < buttonCount; i++ ) {
		lpw = lpwAlign( lpw );
		LPDLGITEMTEMPLATE lpdit = (LPDLGITEMTEMPLATE) lpw;
		lpdit->x = bx;
		lpdit->y = by;
		lpdit->cx = bw[i];
		lpdit->cy = BUTTON_HEIGHT;
		lpdit->id = buttonId++;
		lpdit->style = WS_CHILD | WS_VISIBLE | WS_TABSTOP | (i == 0 ? WS_GROUP : 0)
			| BS_TEXT | (i == defaultButton ? BS_DEFPUSHBUTTON : BS_PUSHBUTTON);

		lpw = (LPWORD) (lpdit + 1);
		*lpw++ = 0xffff; *lpw++ = 0x0080;										// Button class
		wcscpy( (LPWSTR) lpw, buttons[i] ); lpw += wcslen( buttons[i] ) + 1;	// text
		*lpw++ = 0;																// creation data

		bx += bw[i] + BUTTON_GAP;
	}

	delete[] wrappedText;
	delete[] bw;
	
	return templ;
}

static BOOL CALLBACK focusDefaultButtonProc( HWND hwnd, LPARAM lParam ) {
	if( ::GetWindowLong( hwnd, GWL_ID ) >= ID_BUTTON1 ) {
		LONG style = ::GetWindowLong( hwnd, GWL_STYLE );
		if( (style & BS_DEFPUSHBUTTON) != 0 ) {
			::SetFocus( hwnd );
			return FALSE;
		}
	}
	return TRUE;
}

static INT_PTR CALLBACK messageDialogProc( HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam ) {
	switch( uMsg ) {
		case WM_INITDIALOG:
			::EnumChildWindows( hwnd, focusDefaultButtonProc, 0 );
			break;

		case WM_COMMAND:
			::EndDialog( hwnd, wParam );
			return TRUE;
	}
	return FALSE;
}

static int textLengthAsDLUs( HDC hdc, LPCWSTR str, int strLen ) {
	SIZE size{ 0 };
	::GetTextExtentPoint32( hdc, str, (strLen >= 0) ? strLen : wcslen( str ), &size );
	return pixel2dluX( size.cx );
}

static LONG pixel2dluX( LONG px ) {
	return MulDiv( px, 4, LOWORD( ::GetDialogBaseUnits() ) );
}

static LONG pixel2dluY( LONG py ) {
	return MulDiv( py, 8, HIWORD( ::GetDialogBaseUnits() ) );
}

static LONG dluX2pixel( LONG dluX ) {
	return MulDiv( dluX, LOWORD( ::GetDialogBaseUnits() ), 4 );
}

static LPWORD lpwAlign( LPWORD lpIn ) {
	ULONG_PTR ul = (ULONG_PTR) lpIn;
	ul += 3;
	ul >>= 2;
	ul <<= 2;
	return (LPWORD) ul;
}
