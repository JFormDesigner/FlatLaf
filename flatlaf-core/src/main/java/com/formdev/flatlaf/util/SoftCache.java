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

package com.formdev.flatlaf.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * A simple cache (map) that uses soft references for the values.
 *
 * @author Karl Tauber
 * @since 2
 */
public class SoftCache<K,V>
	implements Map<K, V>
{
	private final Map<K, CacheReference<K,V>> map;
	private final ReferenceQueue<V> queue = new ReferenceQueue<>();

	public SoftCache() {
		map = new HashMap<>();
	}

	public SoftCache( int initialCapacity ) {
		map = new HashMap<>( initialCapacity );
	}

	@Override
	public int size() {
		expungeStaleEntries();
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		expungeStaleEntries();
		return map.isEmpty();
	}

	@Override
	public boolean containsKey( Object key ) {
		expungeStaleEntries();
		return map.containsKey( key );
	}

	/**
	 * Not supported. Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public boolean containsValue( Object value ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V get( Object key ) {
		expungeStaleEntries();
		return getRef( map.get( key ) );
	}

	@Override
	public V put( K key, V value ) {
		expungeStaleEntries();
		return getRef( map.put( key, new CacheReference<>( key, value, queue ) ) );
	}

	@Override
	public V remove( Object key ) {
		expungeStaleEntries();
		return getRef( map.remove( key ) );
	}

	private V getRef( CacheReference<K,V> ref ) {
		return (ref != null) ? ref.get() : null;
	}

	@Override
	public void putAll( Map<? extends K, ? extends V> m ) {
		expungeStaleEntries();
		for( Entry<? extends K, ? extends V> e : m.entrySet() )
			put( e.getKey(), e.getValue() );
	}

	@Override
	public void clear() {
		map.clear();
		expungeStaleEntries();
	}

	@Override
	public Set<K> keySet() {
		expungeStaleEntries();
		return map.keySet();
	}

	/**
	 * Not supported. Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported. Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported. Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public void forEach( BiConsumer<? super K, ? super V> action ) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not supported. Throws {@link UnsupportedOperationException}.
	 */
	@Override
	public void replaceAll( BiFunction<? super K, ? super V, ? extends V> function ) {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings( "unchecked" )
	private void expungeStaleEntries() {
		Reference<? extends V> reference;
		while( (reference = queue.poll()) != null )
			map.remove( ((CacheReference<K,V>)reference).key );
	}

	//---- class CacheReference ----

	private static class CacheReference<K,V>
		extends SoftReference<V>
	{
		// needed to remove reference from map in expungeStaleEntries()
		final K key;

		CacheReference( K key, V value, ReferenceQueue<? super V> queue ) {
			super( value, queue );
			this.key = key;
		}
	}
}
