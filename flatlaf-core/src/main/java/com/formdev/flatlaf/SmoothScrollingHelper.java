/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package com.formdev.flatlaf;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.util.Animator;

/**
 * @author Christopher Deckers
 */
public class SmoothScrollingHelper implements AWTEventListener
{

	private static SmoothScrollingHelper instance;
	
	static synchronized boolean install() {
		if( instance != null )
			return false;
		instance = new SmoothScrollingHelper();
		long eventMask = AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK;
		Toolkit.getDefaultToolkit().addAWTEventListener(instance, eventMask);
		return true;
	}

	static synchronized void uninstall() {
		if( instance == null )
			return;
		Toolkit.getDefaultToolkit().removeAWTEventListener(instance);
		instance = null;
	}
	
	@Override
	public void eventDispatched( AWTEvent event ) {
		if( event instanceof ComponentEvent && ((ComponentEvent)event).getComponent() instanceof JScrollBar ) {
			// Do not disconnect blit scroll mode when e.g. dragging the scroll bar.
			return;
		}
		if( Animator.useAnimation() && FlatSystemProperties.getBoolean( FlatSystemProperties.SMOOTH_SCROLLING, true ) ) {
			boolean isHoldingScrollModeBlocked = false;
			switch(event.getID()) {
			    case MouseEvent.MOUSE_MOVED:
			    case MouseEvent.MOUSE_ENTERED:
			    case MouseEvent.MOUSE_EXITED:
			        if(isBlitScrollModeBlocked) {
			            int modifiersEx = ((MouseEvent)event).getModifiersEx();
			            if((modifiersEx & (MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK)) == 0) {
			                // If the scroll mode was blocked for dragging, let's release if we receive a non-drag-related event (i.e. drag is done).
			                for( JViewport viewport: viewportSet ) {
			                    setBlitScrollModeBlocked( viewport, false );
			                    setInSmoothScrolling( viewport, false );
			                }
			                isBlitScrollModeBlocked = false;
			            }
			        }
			        break;
				case MouseEvent.MOUSE_PRESSED:
				case MouseEvent.MOUSE_DRAGGED:
					isHoldingScrollModeBlocked = true;
					// Fall through
				case MouseEvent.MOUSE_RELEASED:
				case MouseEvent.MOUSE_CLICKED:
				case MouseEvent.MOUSE_WHEEL:
				case KeyEvent.KEY_PRESSED:
				case KeyEvent.KEY_RELEASED:
				case KeyEvent.KEY_TYPED:
					boolean isBlitScrollModeBlocked_ = isBlitScrollModeBlocked;
					if(!isBlitScrollModeBlocked_) {
						Component c = ((ComponentEvent)event).getComponent();
						for( JViewport viewport: viewportSet ) {
							Container scrollPane = viewport.getParent();
							if(scrollPane == c || scrollPane.isAncestorOf( c )) {
								setInSmoothScrolling( viewport, true );
							}
							setBlitScrollModeBlocked( viewport, true );
						}
						isBlitScrollModeBlocked = true;
					}
					if( !isHoldingScrollModeBlocked && ( !isBlitScrollModeBlocked_ || event.getID() == MouseEvent.MOUSE_RELEASED )) {
						SwingUtilities.invokeLater( () -> {
							for( JViewport viewport: viewportSet ) {
								setBlitScrollModeBlocked( viewport, false );
								setInSmoothScrolling( viewport, false );
							}
							isBlitScrollModeBlocked = false;
						} );
					}
					break;
			}
		}
	}

	private boolean isBlitScrollModeBlocked;
	
	public static synchronized void setBlitScrollModeBlocked( JViewport viewport, boolean isBlocked ) {
		if( instance == null )
			return;
		String clientPropertyName = "_flatlaf.originalScrollMode";
		if( isBlocked ) {
			int scrollMode = viewport.getScrollMode();
			if( scrollMode == JViewport.BLIT_SCROLL_MODE ) {
				viewport.putClientProperty( clientPropertyName , scrollMode );
				viewport.setScrollMode( JViewport.SIMPLE_SCROLL_MODE );
			}
		} else {
			Integer scrollMode = (Integer)viewport.getClientProperty( clientPropertyName );
			if( scrollMode != null ) {
				viewport.setScrollMode( scrollMode );
				viewport.putClientProperty( clientPropertyName , null );
			}
		}
	}
	
	public static void setScrollBarValueWithOptionalRepaint(JViewport viewport, JScrollBar scrollbar, int value) {
		Container viewportParent = null;
		Rectangle dirtyRegion = null;
		viewportParent = viewport == null? null: viewport.getParent();
		dirtyRegion = viewportParent instanceof JComponent? RepaintManager.currentManager( viewport ).getDirtyRegion((JComponent)viewportParent ): null;
		int scrollMode = viewport.getScrollMode();
		if(scrollMode == JViewport.BLIT_SCROLL_MODE) {
			viewport.setScrollMode( JViewport.SIMPLE_SCROLL_MODE );
		}
		scrollbar.setValue( value );
		if(scrollMode == JViewport.BLIT_SCROLL_MODE) {
			viewport.setScrollMode( JViewport.BLIT_SCROLL_MODE );
		}
		if(dirtyRegion != null && dirtyRegion.width == 0 && dirtyRegion.height == 0) {
			// There was no dirty region. Let's restore that state for blit scroll mode to work.
			RepaintManager.currentManager( viewport ).markCompletelyClean( (JComponent)viewportParent );
		}
	}
	
	public static synchronized boolean isInBlockedBlitScrollMode( JViewport viewport ) {
		if( instance == null )
			return false;
		String clientPropertyName = "_flatlaf.originalScrollMode";
		return viewport.getClientProperty( clientPropertyName ) != null && viewport.getScrollMode() == JViewport.SIMPLE_SCROLL_MODE;
	}
	
	public static synchronized void allowBlitScrollModeTemporarily( JViewport viewport, boolean isAllowed ) {
		if( instance == null )
			return;
		// When mouse is dragged, blit scroll mode is deactivated so that drag timers that provoke more scrolling do not repaint at wrong position.
		// The FlatLaf animator re-activates the blit scroll mode just for its timed operation.
		String clientPropertyName = "_flatlaf.originalScrollMode";
		if(viewport.getClientProperty( clientPropertyName ) != null) {
			if(isAllowed) {
				viewport.setScrollMode( JViewport.BLIT_SCROLL_MODE );
			} else {
				viewport.setScrollMode( JViewport.SIMPLE_SCROLL_MODE );
			}
		}
	}
	
	public static synchronized boolean isInSmoothScrolling( JViewport viewport ) {
		if( instance == null )
			return false;
		String clientPropertyName = "_flatlaf.inSmoothScrolling";
		return viewport != null && viewport.getClientProperty( clientPropertyName ) != null;
	}
	
	private static synchronized void setInSmoothScrolling( JViewport viewport, boolean isInSmoothScrolling ) {
		if( instance == null )
			return;
		String clientPropertyName = "_flatlaf.inSmoothScrolling";
		viewport.putClientProperty( clientPropertyName , isInSmoothScrolling? Boolean.TRUE: null);
	}
	
	private static Set<JViewport> viewportSet = Collections.newSetFromMap(new IdentityHashMap<JViewport, Boolean>());
	
	public static synchronized void registerViewport( JViewport viewport ) {
		if( instance == null )
			return;
		if(viewport.getParent() instanceof JScrollPane) {
			viewportSet.add( viewport );
		}
	}
	
	public static synchronized void unregisterViewport( JViewport viewport ) {
		if( instance == null )
			return;
		if( instance.isBlitScrollModeBlocked ) {
			instance.setBlitScrollModeBlocked( viewport, false );
		}
		viewportSet.remove( viewport );
	}
	
}
