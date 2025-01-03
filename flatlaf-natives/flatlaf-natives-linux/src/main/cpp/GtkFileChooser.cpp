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

#include <jawt.h>
#include <linux/jawt_md.h>
#include <gtk/gtk.h>
#include <gdk/gdkx.h>
#include <glib/gi18n.h>
#include "com_formdev_flatlaf_ui_FlatNativeLinuxLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

//---- class AutoReleaseStringUTF8 --------------------------------------------

class AutoReleaseStringUTF8 {
	JNIEnv* env;
	jstring javaString;
	const char* chars;

public:
	AutoReleaseStringUTF8( JNIEnv* _env, jstring _javaString ) {
		env = _env;
		javaString = _javaString;
		chars = (javaString != NULL) ? env->GetStringUTFChars( javaString, NULL ) : NULL;
	}
	~AutoReleaseStringUTF8() {
		if( chars != NULL )
			env->ReleaseStringUTFChars( javaString, chars );
	}
	operator const gchar*() { return chars; }
};

//---- helper -----------------------------------------------------------------

#define isOptionSet( option ) ((optionsSet & com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_ ## option) != 0)
#define isOptionClear( option ) ((optionsClear & com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_ ## option) != 0)
#define isOptionSetOrClear( option ) isOptionSet( option ) || isOptionClear( option )

jobjectArray newJavaStringArray( JNIEnv* env, jsize count ) {
	jclass stringClass = env->FindClass( "java/lang/String" );
	return env->NewObjectArray( count, stringClass, NULL );
}

void initFilters( GtkFileChooser* chooser, JNIEnv* env, jint fileTypeIndex, jobjectArray fileTypes ) {
	jint length = env->GetArrayLength( fileTypes );
	if( length <= 0 )
		return;

	GtkFileFilter* filter = NULL;
	int filterIndex = 0;
	for( int i = 0; i < length; i++ ) {
		jstring jstr = (jstring) env->GetObjectArrayElement( fileTypes, i );
		if( jstr == NULL ) {
			if( filter != NULL ) {
				gtk_file_chooser_add_filter( chooser, filter );
				if( fileTypeIndex == filterIndex )
					gtk_file_chooser_set_filter( chooser, filter );
				filter = NULL;
				filterIndex++;
			}
			continue;
		}

		AutoReleaseStringUTF8 str( env, jstr );
		if( filter == NULL ) {
			filter = gtk_file_filter_new();
			gtk_file_filter_set_name( filter, str );
		} else
			gtk_file_filter_add_pattern( filter, str );
	}
}

static void handle_response( GtkWidget* dialog, gint responseId, gpointer data ) {
	if( responseId == GTK_RESPONSE_ACCEPT )
		*((GSList**)data) = gtk_file_chooser_get_filenames( GTK_FILE_CHOOSER( dialog ) );

	gtk_widget_hide( dialog );
	gtk_widget_destroy( dialog );
	gtk_main_quit();
}

//---- JNI methods ------------------------------------------------------------

extern "C"
JNIEXPORT jobjectArray JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_showFileChooser
	( JNIEnv* env, jclass cls, jboolean open,
		jstring title, jstring okButtonLabel, jstring currentName, jstring currentFolder,
		jint optionsSet, jint optionsClear, jint fileTypeIndex, jobjectArray fileTypes )
{
	// initialize GTK
	if( !gtk_init_check( NULL, NULL ) )
		return NULL;

	// convert Java strings to C strings
	AutoReleaseStringUTF8 ctitle( env, title );
	AutoReleaseStringUTF8 cokButtonLabel( env, okButtonLabel );
	AutoReleaseStringUTF8 ccurrentName( env, currentName );
	AutoReleaseStringUTF8 ccurrentFolder( env, currentFolder );

	// create GTK file chooser dialog
	// https://docs.gtk.org/gtk3/class.FileChooserDialog.html
	bool selectFolder = isOptionSet( FC_select_folder );
	bool multiSelect = isOptionSet( FC_select_multiple );
	GtkWidget* dialog = gtk_file_chooser_dialog_new(
		(ctitle != NULL) ? ctitle
			: (open ? (selectFolder ? (multiSelect ? _("Select Folders") : _("Select Folder"))
				: (multiSelect ? _("Open Files") : _("Open File"))) : _("Save File")),
		NULL, // can not use AWT X11 window as parent because GtkWindow is required
		open ? (selectFolder ? GTK_FILE_CHOOSER_ACTION_SELECT_FOLDER : GTK_FILE_CHOOSER_ACTION_OPEN)
			: GTK_FILE_CHOOSER_ACTION_SAVE,
		_("_Cancel"), GTK_RESPONSE_CANCEL,
		(cokButtonLabel != NULL) ? cokButtonLabel : (open ? _("_Open") : _("_Save")), GTK_RESPONSE_ACCEPT,
		NULL ); // marks end of buttons
	GtkFileChooser* chooser = GTK_FILE_CHOOSER( dialog );

	if( !open && ccurrentName != NULL )
		gtk_file_chooser_set_current_name( chooser, ccurrentName );
	if( ccurrentFolder != NULL )
		gtk_file_chooser_set_current_folder( chooser, ccurrentFolder );

	if( isOptionSetOrClear( FC_select_multiple ) )
		gtk_file_chooser_set_select_multiple( chooser, isOptionSet( FC_select_multiple ) );
	if( isOptionSetOrClear( FC_show_hidden ) )
		gtk_file_chooser_set_show_hidden( chooser, isOptionSet( FC_show_hidden ) );
	if( isOptionSetOrClear( FC_local_only ) )
		gtk_file_chooser_set_local_only( chooser, isOptionSet( FC_local_only ) );
	if( isOptionSetOrClear( FC_do_overwrite_confirmation ) )
		gtk_file_chooser_set_do_overwrite_confirmation( chooser, isOptionSet( FC_do_overwrite_confirmation ) );
	if( isOptionSetOrClear( FC_create_folders ) )
		gtk_file_chooser_set_create_folders( chooser, isOptionSet( FC_create_folders ) );

	initFilters( chooser, env, fileTypeIndex, fileTypes );

	gtk_window_set_modal( GTK_WINDOW( dialog ), true );

	// show dialog
	// (similar to what's done in sun_awt_X11_GtkFileDialogPeer.c)
	GSList* fileList = NULL;
    g_signal_connect( dialog, "response", G_CALLBACK( handle_response ), &fileList );
	gtk_widget_show( dialog );
	gtk_main();

	// canceled?
	if( fileList == NULL )
		return newJavaStringArray( env, 0 );

	// convert GSList to Java string array
	guint count = g_slist_length( fileList );
	jobjectArray array = newJavaStringArray( env, count );
	GSList* it = fileList;
	for( int i = 0; i < count; i++, it = it->next ) {
		gchar* path = (gchar*) it->data;
		jstring jpath = env->NewStringUTF( path );
		g_free( path );

		env->SetObjectArrayElement( array, i, jpath );
		env->DeleteLocalRef( jpath );
	}
	g_slist_free( fileList );
	return array;
}
