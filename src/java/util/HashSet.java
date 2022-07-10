/*
 * Copyright (c) 1997, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import jdk.internal.misc.SharedSecrets;

/**
 * This class implements the {@code Set} interface, backed by a hash table
 * (actually a {@code HashMap} instance).  It makes no guarantees as to the
 * iteration order of the set; in particular, it does not guarantee that the
 * order will remain constant over time.  This class permits the {@code null}
 * element.
 *
 * <p>This class offers constant time performance for the basic operations
 * ({@code add}, {@code remove}, {@code contains} and {@code size}),
 * assuming the hash function disperses the elements properly among the
 * buckets.  Iterating over this set requires time proportional to the sum of
 * the {@code HashSet} instance's size (the number of elements) plus the
 * "capacity" of the backing {@code HashMap} instance (the number of
 * buckets).  Thus, it's very important not to set the initial capacity too
 * high (or the load factor too low) if iteration performance is important.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash set concurrently, and at least one of
 * the threads modifies the set, it <i>must</i> be synchronized externally.
 * This is typically accomplished by synchronizing on some object that
 * naturally encapsulates the set.
 *
 * If no such object exists, the set should be "wrapped" using the
 * {@link Collections#synchronizedSet Collections.synchronizedSet}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the set:<pre>
 *   Set s = Collections.synchronizedSet(new HashSet(...));</pre>
 *
 * <p>The iterators returned by this class's {@code iterator} method are
 * <i>fail-fast</i>: if the set is modified at any time after the iterator is
 * created, in any way except through the iterator's own {@code remove}
 * method, the Iterator throws a {@link ConcurrentModificationException}.
 * Thus, in the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @author Josh Bloch
 * @author Neal Gafter
 * @see Collection
 * @see Set
 * @see TreeSet
 * @see HashMap
 * @since 1.2
 */
// HashSet是无序Set，内部通过HashMap或LinkedHashMap来完成其固有操作
public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
    
    private transient HashMap<E, Object> map;

    
    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Constructs a new, empty set; the backing {@code HashMap} instance has
     * default initial capacity (16) and load factor (0.75).
     */
    //创建一个新的空set
    public HashSet() {
        map = new HashMap<>();
    }
    
    /**
     * Constructs a new, empty set; the backing {@code HashMap} instance has
     * the specified initial capacity and default load factor (0.75).
     *
     * @param initialCapacity the initial capacity of the hash table
     *
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero
     */
    public HashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }
    
    /**
     * Constructs a new, empty set; the backing {@code HashMap} instance has
     * the specified initial capacity and the specified load factor.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor      the load factor of the hash map
     *
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero, or if the load factor is nonpositive
     */
    public HashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }
    
    /**
     * Constructs a new set containing the elements in the specified
     * collection.  The {@code HashMap} is created with default load factor
     * (0.75) and an initial capacity sufficient to contain the elements in
     * the specified collection.
     *
     * @param c the collection whose elements are to be placed into this set
     *
     * @throws NullPointerException if the specified collection is null
     */
    public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c);
    }
    
    
    /**
     * Constructs a new, empty linked hash set.  (This package private
     * constructor is only used by LinkedHashSet.) The backing
     * HashMap instance is a LinkedHashMap with the specified initial
     * capacity and the specified load factor.
     *
     * @param initialCapacity the initial capacity of the hash map
     * @param loadFactor      the load factor of the hash map
     * @param dummy           ignored (distinguishes this
     *                        constructor from other int, float constructor.)
     *
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero, or if the load factor is nonpositive
     */
    // 内部使用了LinkedHashMap
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if
     * this set contains no element {@code e2} such that
     * {@code Objects.equals(e, e2)}.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param e element to be added to this set
     *
     * @return {@code true} if this set did not already contain the specified
     * element
     */
    // 向Set中添加元素
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */
