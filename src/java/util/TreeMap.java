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
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * A Red-Black tree based {@link NavigableMap} implementation.
 * The map is sorted according to the {@linkplain Comparable natural
 * ordering} of its keys, or by a {@link Comparator} provided at map
 * creation time, depending on which constructor is used.
 *
 * <p>This implementation provides guaranteed log(n) time cost for the
 * {@code containsKey}, {@code get}, {@code put} and {@code remove}
 * operations.  Algorithms are adaptations of those in Cormen, Leiserson, and
 * Rivest's <em>Introduction to Algorithms</em>.
 *
 * <p>Note that the ordering maintained by a tree map, like any sorted map, and
 * whether or not an explicit comparator is provided, must be <em>consistent
 * with {@code equals}</em> if this sorted map is to correctly implement the
 * {@code Map} interface.  (See {@code Comparable} or {@code Comparator} for a
 * precise definition of <em>consistent with equals</em>.)  This is so because
 * the {@code Map} interface is defined in terms of the {@code equals}
 * operation, but a sorted map performs all key comparisons using its {@code
 * compareTo} (or {@code compare}) method, so two keys that are deemed equal by
 * this method are, from the standpoint of the sorted map, equal.  The behavior
 * of a sorted map <em>is</em> well-defined even if its ordering is
 * inconsistent with {@code equals}; it just fails to obey the general contract
 * of the {@code Map} interface.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a map concurrently, and at least one of the
 * threads modifies the map structurally, it <em>must</em> be synchronized
 * externally.  (A structural modification is any operation that adds or
 * deletes one or more mappings; merely changing the value associated
 * with an existing key is not a structural modification.)  This is
 * typically accomplished by synchronizing on some object that naturally
 * encapsulates the map.
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedSortedMap Collections.synchronizedSortedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map: <pre>
 *   SortedMap m = Collections.synchronizedSortedMap(new TreeMap(...));</pre>
 *
 * <p>The iterators returned by the {@code iterator} method of the collections
 * returned by all of this class's "collection view methods" are
 * <em>fail-fast</em>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * {@code remove} method, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <em>the fail-fast behavior of iterators
 * should be used only to detect bugs.</em>
 *
 * <p>All {@code Map.Entry} pairs returned by methods in this class
 * and its views represent snapshots of mappings at the time they were
 * produced. They do <strong>not</strong> support the {@code Entry.setValue}
 * method. (Note however that it is possible to change mappings in the
 * associated map using {@code put}.)
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Josh Bloch and Doug Lea
 * @see Map
 * @see HashMap
 * @see Hashtable
 * @see Comparable
 * @see Comparator
 * @see Collection
 * @since 1.2
 */

/*
 * TreeMap结构：红黑树(没有哈希数组，这一点不同于HashMap)。key不能为null，但value可以为null
 *
 * TreeMap中的key有序，可以升序也可以降序
 *
 * key的排序方式依赖于外部比较器（优先使用）和内部比较器
 *
 * 注：
 * 在无特殊说明的情形下，注释中提到的遍历都是指中序遍历当前的Map
 * 至于中序序列是递增还是递减，则由Map的特性决定（可能是升序Map，也可能是降序Map）
 *
 * 术语约定：
 * TreeMap中包含两种子视图：AscendingSubMap和DescendingSubMap
 * 在TreeMap及其子视图中，当提到靠左、靠前、靠右、靠后的元素时，指的是在正序Map下的排序
 * 而正序Map或逆序Map是由相关的内部比较器和外部比较器决定的
 */
public class TreeMap<K, V> extends AbstractMap<K, V> implements NavigableMap<K, V>, Cloneable, Serializable {
    
    /**
     * The comparator used to maintain order in this tree map, or null if it uses the natural ordering of its keys.
     *
     * @serial
     */
    private final Comparator<? super K> comparator; // TreeMap中的外部比较器，如果不为null，会优先使用
    
    private transient Entry<K, V> root; // 红黑树的根（TreeMap根结点）
    
    // 代表红黑树结点颜色的常量
    private static final boolean RED = false;
    private static final boolean BLACK = true;
    
    /**
     * Fields initialized to contain an instance of the entry set view
     * the first time this view is requested.  Views are stateless, so
     * there's no reason to create more than one.
     */
    private transient EntrySet entrySet;                // 当前Map中key-value对的集合
    private transient KeySet<K> navigableKeySet;        // 当前Map中的key的集合
    private transient NavigableMap<K, V> descendingMap; // 【逆序】Map（实质是对当前Map实例的一个【逆序】包装）
    
    /**
     * The number of entries in the tree
     */
    private transient int size = 0;     // TreeMap中的元素数量
    
    /**
     * The number of structural modifications to the tree.
     */
    private transient int modCount = 0; // 记录TreeMap结构的修改次数
    
    /**
     * Dummy value serving as unmatchable fence key for unbounded SubMapIterators
     */
    private static final Object UNBOUNDED = new Object();

    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Constructs a new, empty tree map, using the natural ordering of its
     * keys.  All keys inserted into the map must implement the {@link
     * Comparable} interface.  Furthermore, all such keys must be
     * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw
     * a {@code ClassCastException} for any keys {@code k1} and
     * {@code k2} in the map.  If the user attempts to put a key into the
     * map that violates this constraint (for example, the user attempts to
     * put a string key into a map whose keys are integers), the
     * {@code put(Object key, Object value)} call will throw a
     * {@code ClassCastException}.
     */
    public TreeMap() {
        comparator = null;
    }
    
    /**
     * Constructs a new, empty tree map, ordered according to the given
     * comparator.  All keys inserted into the map must be <em>mutually
     * comparable</em> by the given comparator: {@code comparator.compare(k1,
     * k2)} must not throw a {@code ClassCastException} for any keys
     * {@code k1} and {@code k2} in the map.  If the user attempts to put
     * a key into the map that violates this constraint, the {@code put(Object
     * key, Object value)} call will throw a
     * {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this map.
     *                   If {@code null}, the {@linkplain Comparable natural
     *                   ordering} of the keys will be used.
     */
    public TreeMap(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }
    
    /**
     * Constructs a new tree map containing the same mappings as the given
     * map, ordered according to the <em>natural ordering</em> of its keys.
     * All keys inserted into the new map must implement the {@link
     * Comparable} interface.  Furthermore, all such keys must be
     * <em>mutually comparable</em>: {@code k1.compareTo(k2)} must not throw
     * a {@code ClassCastException} for any keys {@code k1} and
     * {@code k2} in the map.  This method runs in n*log(n) time.
     *
     * @param m the map whose mappings are to be placed in this map
     *
     * @throws ClassCastException   if the keys in m are not {@link Comparable},
     *                              or are not mutually comparable
     * @throws NullPointerException if the specified map is null
     */
    public TreeMap(Map<? extends K, ? extends V> m) {
        comparator = null;
        putAll(m);
    }
    
    /**
     * Constructs a new tree map containing the same mappings and
     * using the same ordering as the specified sorted map.  This
     * method runs in linear time.
     *
     * @param m the sorted map whose mappings are to be placed in this map,
     *          and whose comparator is to be used to sort this map
     *
     * @throws NullPointerException if the specified map is null
     */
    public TreeMap(SortedMap<K, ? extends V> m) {
        comparator = m.comparator();
        try {
            buildFromSorted(m.size(), m.entrySet().iterator(), null, null);
        } catch(java.io.IOException | ClassNotFoundException cannotHappen) {
        }
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */

    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     *
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key}.)
     *
     * @throws ClassCastException   if the specified key cannot be compared
     *                              with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *                              and this map uses natural ordering, or its comparator
     *                              does not permit null keys
     */
    /*
     * 向当前Map中存储一个key-value对，返回值代表该位置存储之前的值
     *
     * 如果外部比较器comparator有效，则允许key为null
     * 否则，key不能为null，且需要实现内部比较器Comparable接口
     */
    public V put(K key, V value) {
        Entry<K, V> t = root;
        
        // 如果根结点为null，说明是首个结点
        if(t == null) {
            // 这里使用compare起到了校验作用
            compare(key, key); // type (and possibly null) check
            
            // 创建一个红黑树结点
            root = new Entry<>(key, value, null);
            size = 1;
            modCount++;
            return null;
        }
        
        int cmp;
        Entry<K, V> parent;
        
        // split comparator and comparable paths
        Comparator<? super K> cpr = comparator;
        
        /* 查找同位元素，如果找到，直接覆盖 */
        
        // 如果存在外部比较器
        if(cpr != null) {
            do {
                parent = t;
                cmp = cpr.compare(key, t.key);
                if(cmp<0) {
                    t = t.left;
                } else if(cmp>0) {
                    t = t.right;
                } else {
                    return t.setValue(value);
                }
            } while(t != null);
            
            // 如果不存在外部比较器，则要求key实现内部比较器Comparable接口
        } else {
            if(key == null) {
                throw new NullPointerException();
            }
            
            @SuppressWarnings("unchecked")
            Comparable<? super K> k = (Comparable<? super K>) key;
            do {
                parent = t;
                cmp = k.compareTo(t.key);
                if(cmp<0) {
                    t = t.left;
                } else if(cmp>0) {
                    t = t.right;
                } else {
                    return t.setValue(value);
                }
            } while(t != null);
        }
        
        /* 至此，说明没找到同位元素，需要新建一个元素插入到红黑树中 */
        
        Entry<K, V> e = new Entry<>(key, value, parent);
        if(cmp<0) {
            parent.left = e;
        } else {
            parent.right = e;
        }
        
        // 将元素e插入到红黑树后，可能会破坏其平衡性，所以这里需要做出调整，保持红黑树的平衡
        fixAfterInsertion(e);
        
        size++;
        
        modCount++;
        
        return null;
    }
    
    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings replace any mappings that this map had for any
     * of the keys currently in the specified map.
     *
     * @param map mappings to be stored in this map
     *
     * @throws ClassCastException   if the class of a key or value in
     *                              the specified map prevents it from being stored in this map
     * @throws NullPointerException if the specified map is null or
     *                              the specified map contains a null key and this map does not
     *                              permit null keys
     */
    // 将指定Map中的元素存入到当前Map（允许覆盖）
    public void putAll(Map<? extends K, ? extends V> map) {
        int mapSize = map.size();
        
        if(size == 0 && mapSize != 0 && map instanceof SortedMap) {
            Comparator<?> c = ((SortedMap<?, ?>) map).comparator();
            if(c == comparator || (c != null && c.equals(comparator))) {
                ++modCount;
                try {
                    buildFromSorted(mapSize, map.entrySet().iterator(), null, null);
                } catch(IOException | ClassNotFoundException cannotHappen) {
                }
                return;
            }
        }
        
        super.putAll(map);
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */

    /*▼ 取值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code key} compares
     * equal to {@code k} according to the map's ordering, then this
     * method returns {@code v}; otherwise it returns {@code null}.
     * (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <em>necessarily</em>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @throws ClassCastException   if the specified key cannot be compared
     *                              with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *                              and this map uses natural ordering, or its comparator
     *                              does not permit null keys
     */
    // 查找key对应的元素的值，如果不存在该元素，则返回null值
    public V get(Object key) {
        // 查找key对应的元素
        Entry<K, V> p = getEntry(key);
        return (p == null ? null : p.value);
    }
    
    /*▲ 取值 ████████████████████████████████████████████████████████████████████████████████┛ */

    
    /*▼ 移除 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Removes the mapping for this key from this TreeMap if present.
     *
     * @param key key for which mapping should be removed
     *
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key}.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key}.)
     *
     * @throws ClassCastException   if the specified key cannot be compared
     *                              with the keys currently in the map
     * @throws NullPointerException if the specified key is null
     *                              and this map uses natural ordering, or its comparator
     *                              does not permit null keys
     */
    // 查找key对应的元素，并移除该元素
    public V remove(Object key) {
        Entry<K, V> p = getEntry(key);
        if(p == null) {
            return null;
        }
        
        V oldValue = p.value;
        
        // 将元素从红黑树中移除
        deleteEntry(p);
        
        return oldValue;
    }
    
    
    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    // 清空当前Map
    public void clear() {
        modCount++;
        size = 0;
        root = null;
    }
    
    /*▲ 移除 ████████████████████████████████████████████████████████████████████████████████┛ */
