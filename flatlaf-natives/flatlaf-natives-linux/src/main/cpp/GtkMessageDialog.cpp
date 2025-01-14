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
#include "JNIUtils.h"
#include "com_formdev_flatlaf_ui_FlatNativeLinuxLibrary.h"

/**
 * @author Karl Tauber
 * @since 3.6
 */

extern "C"
JNIEXPORT jint JNICALL Java_com_formdev_flatlaf_ui_FlatNativeLinuxLibrary_showMessageDialog
	( JNIEnv* env, jclass cls, jlong hwndParent, jint messageType, jstring primaryText, jstring secondaryText,
		jint defaultButton, jobjectArray buttons )
{
	GtkWindow* window = (GtkWindow*) hwndParent;

	// convert message type
	GtkMessageType gmessageType;
	switch( messageType ) {
		case /* JOptionPane.ERROR_MESSAGE */       0: gmessageType = GTK_MESSAGE_ERROR; break;
		case /* JOptionPane.INFORMATION_MESSAGE */ 1: gmessageType = GTK_MESSAGE_INFO; break;
		case /* JOptionPane.WARNING_MESSAGE */     2: gmessageType = GTK_MESSAGE_WARNING; break;
		case /* JOptionPane.QUESTION_MESSAGE */    3: gmessageType = GTK_MESSAGE_QUESTION; break;
		default:
		case /* JOptionPane.PLAIN_MESSAGE */      -1: gmessageType = GTK_MESSAGE_OTHER; break;
	}

	// convert Java strings to C strings
	AutoReleaseStringUTF8 cprimaryText( env, primaryText );
	AutoReleaseStringUTF8 csecondaryText( env, secondaryText );

	// create GTK file chooser dialog
	// https://docs.gtk.org/gtk3/class.MessageDialog.html
	jint buttonCount = env->GetArrayLength( buttons );
	GtkWidget* dialog = gtk_message_dialog_new( window, GTK_DIALOG_MODAL, gmessageType,
		(buttonCount > 0) ? GTK_BUTTONS_NONE : GTK_BUTTONS_OK,
		"%s", (const gchar*) cprimaryText );
	if( csecondaryText != NULL )
		gtk_message_dialog_format_secondary_text( GTK_MESSAGE_DIALOG( dialog ), "%s", (const gchar*) csecondaryText );

	// add buttons
	for( int i = 0; i < buttonCount; i++ ) {
		AutoReleaseStringUTF8 str( env, (jstring) env->GetObjectArrayElement( buttons, i ) );
		gtk_dialog_add_button( GTK_DIALOG( dialog ), str, i );
	}

	// set default button
	gtk_dialog_set_default_response( GTK_DIALOG( dialog ), MIN( MAX( defaultButton, 0 ), buttonCount - 1 ) );

	// show message dialog
	gint responseID = gtk_dialog_run( GTK_DIALOG( dialog ) );
	gtk_widget_destroy( dialog );

	// return -1 if closed with ESC key
	return (responseID >= 0) ? responseID : -1;
}
