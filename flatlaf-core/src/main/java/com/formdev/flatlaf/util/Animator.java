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

package com.formdev.flatlaf.util;

import java.util.ArrayList;
import javax.swing.Timer;
import com.formdev.flatlaf.FlatSystemProperties;

/**
 * Simple animator based on ideas and concepts from "Filthy Rich Clients" book
 * and "Timing Framework" library.
 *
 * @author Karl Tauber
 */
public class Animator
{
	private int duration;
	private int resolution = 10;
	private Interpolator interpolator;
	private final ArrayList<TimingTarget> targets = new ArrayList<>();
	private final Runnable endRunnable;

	private boolean running;
	private boolean hasBegun;
	private boolean timeToStop;
	private long startTime;
	private Timer timer;

	/**
	 * Checks whether animations are enabled (the default) or disabled via
	 * system property {@code flatlaf.animation} set to {@code false}.
	 * This allows disabling all animations at command line with {@code -Dflatlaf.animation=false}.
	 */
	public static boolean useAnimation() {
		return FlatSystemProperties.getBoolean( FlatSystemProperties.ANIMATION, true );
	}

	/**
	 * Creates an animation that runs duration milliseconds.
	 * Use {@link #addTarget(TimingTarget)} to receive timing events
	 * and {@link #start()} to start the animation.
	 *
	 * @param duration the duration of the animation in milliseconds
	 */
	public Animator( int duration ) {
		this( duration, null, null );
	}

	/**
	 * Creates an animation that runs duration milliseconds.
	 * Use {@link #start()} to start the animation.
	 *
	 * @param duration the duration of the animation in milliseconds
	 * @param target the target that receives timing events
	 */
	public Animator( int duration, TimingTarget target ) {
		this( duration, target, null );
	}

	/**
	 * Creates an animation that runs duration milliseconds.
	 * Use {@link #start()} to start the animation.
	 *
	 * @param duration the duration of the animation in milliseconds
	 * @param target the target that receives timing events
	 * @param endRunnable a runnable invoked when the animation ends; or {@code null}
	 */
	public Animator( int duration, TimingTarget target, Runnable endRunnable ) {
		setDuration( duration );
		addTarget( target );
		this.endRunnable = endRunnable;
	}

	/**
	 * Returns the duration of the animation in milliseconds.
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets the duration of the animation in milliseconds.
	 *
	 * @throws IllegalStateException if animation is running
	 * @throws IllegalArgumentException if duration is &lt;= zero
	 */
	public void setDuration( int duration ) {
		throwExceptionIfRunning();
		if( duration <= 0 )
			throw new IllegalArgumentException();
		this.duration = duration;
	}

	/**
	 * Returns the resolution of the animation in milliseconds (default is 10).
	 * Resolution is the amount of time between timing events.
	 */
	public int getResolution() {
		return resolution;
	}

	/**
	 * Sets the resolution of the animation in milliseconds.
	 *
	 * @param resolution the resolution of the animation in milliseconds
	 * @throws IllegalStateException if animation is running
	 * @throws IllegalArgumentException if resolution is &lt;= zero
	 */
	public void setResolution( int resolution ) {
		throwExceptionIfRunning();
		if( resolution <= 0 )
			throw new IllegalArgumentException();
		this.resolution = resolution;
	}

	/**
	 * Returns the interpolator for the animation.
	 * Default is {@code null}, which means linear.
	 */
	public Interpolator getInterpolator() {
		return interpolator;
	}

	/**
	 * Sets the interpolator for the animation.
	 *
	 * @throws IllegalStateException if animation is running
	 */
	public void setInterpolator( Interpolator interpolator ) {
		throwExceptionIfRunning();
		this.interpolator = interpolator;
	}

	/**
	 * Adds a target to the animation that receives timing events.
	 *
	 * @param target the target that receives timing events
	 */
	public void addTarget( TimingTarget target ) {
		if( target == null )
			return;

		synchronized( targets ) {
			if( !targets.contains( target ) )
				targets.add( target );
		}
	}

	/**
	 * Removes a target from the animation.
	 *
	 * @param target the target that should be removed
	 */
	public void removeTarget( TimingTarget target ) {
		synchronized( targets ) {
			targets.remove( target );
		}
	}

	/**
	 * Starts the animation.
	 *
	 * @throws IllegalStateException if animation is running
	 */
	public void start() {
		throwExceptionIfRunning();

		running = true;
		hasBegun = false;
		timeToStop = false;
		startTime = System.nanoTime() / 1000000;

		if( timer == null ) {
			timer = new Timer( resolution, e -> {
				if( !hasBegun ) {
					begin();
					hasBegun = true;
				}

				timingEvent( getTimingFraction() );
			} );
		} else
			timer.setDelay( resolution );
		timer.setInitialDelay( 0 );
		timer.start();
	}

	/**
	 * Stops the animation before it normally ends.
	 * Invokes {@link TimingTarget#end()} on timing targets.
	 */
	public void stop() {
		stop( false );
	}

	/**
	 * Cancels the animation before it normally ends.
	 * Does not invoke {@link TimingTarget#end()} on timing targets.
	 */
	public void cancel() {
		stop( true );
	}

	private void stop( boolean cancel ) {
		if( !running )
			return;

		if( timer != null )
			timer.stop();

		if( !cancel )
			end();

		running = false;
		timeToStop = false;
	}

	/**
	 * Restarts the animation.
	 * Invokes {@link #cancel()} and {@link #start()}.
	 */
	public void restart() {
		cancel();
		start();
	}

	/**
	 * Returns whether this animation is running.
	 */
	public boolean isRunning() {
		return running;
	}

	private float getTimingFraction() {
		long currentTime = System.nanoTime() / 1000000;
		long elapsedTime = currentTime - startTime;
		timeToStop = (elapsedTime >= duration);

		float fraction = clampFraction( (float) elapsedTime / duration );
		if( interpolator != null )
			fraction = clampFraction( interpolator.interpolate( fraction ) );
		return fraction;
	}

	private float clampFraction( float fraction ) {
		if( fraction < 0 )
			return 0;
		if( fraction > 1 )
			return 1;
		return fraction;
	}

	private void timingEvent( float fraction ) {
		synchronized( targets ) {
			for( TimingTarget target : targets )
				target.timingEvent( fraction );
		}

		if( timeToStop )
			stop();
	}

	private void begin() {
		synchronized( targets ) {
			for( TimingTarget target : targets )
				target.begin();
		}
	}

	private void end() {
		synchronized( targets ) {
			for( TimingTarget target : targets )
				target.end();
		}

		if( endRunnable != null )
			endRunnable.run();
	}

	private void throwExceptionIfRunning() {
		if( isRunning() )
			throw new IllegalStateException();
	}

	//---- interface TimingTarget ---------------------------------------------

	/**
	 * Animation callbacks.
	 */
	@FunctionalInterface
	public interface TimingTarget {
		/**
		 * Invoked multiple times while animation is running.
		 *
		 * @param fraction the percent (0 to 1) elapsed of the current animation cycle
		 */
		void timingEvent( float fraction );

		/**
		 * Invoked when the animation begins.
		 */
		default void begin() {}

		/**
		 * Invoked when the animation ends.
		 */
		default void end() {}
	}

	//---- interface Interpolator ---------------------------------------------

	/**
	 * Interpolator used by animation to change timing fraction. E.g. for easing.
	 */
	@FunctionalInterface
	public interface Interpolator {
		/**
		 * Interpolate the given fraction and returns a new fraction.
		 * Both fractions are in range [0, 1].
		 *
		 * @param fraction the percent (0 to 1) elapsed of the current animation cycle
		 * @return new fraction in range [0, 1]
		 */
		float interpolate( float fraction );
	}
}
