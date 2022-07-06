/*
 * Copyright (c) 1998, 2018, Oracle and/or its affiliates. All rights reserved.
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
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A {@link NavigableSet} implementation based on a {@link TreeMap}.
 * The elements are ordered using their {@linkplain Comparable natural
 * ordering}, or by a {@link Comparator} provided at set creation
 * time, depending on which constructor is used.
 *
 * <p>This implementation provides guaranteed log(n) time cost for the basic
 * operations ({@code add}, {@code remove} and {@code contains}).
 *
 * <p>Note that the ordering maintained by a set (whether or not an explicit
 * comparator is provided) must be <i>consistent with equals</i> if it is to
 * correctly implement the {@code Set} interface.  (See {@code Comparable}
 * or {@code Comparator} for a precise definition of <i>consistent with
 * equals</i>.)  This is so because the {@code Set} interface is defined in
 * terms of the {@code equals} operation, but a {@code TreeSet} instance
 * performs all element comparisons using its {@code compareTo} (or
 * {@code compare}) method, so two elements that are deemed equal by this method
 * are, from the standpoint of the set, equal.  The behavior of a set
 * <i>is</i> well-defined even if its ordering is inconsistent with equals; it
 * just fails to obey the general contract of the {@code Set} interface.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a tree set concurrently, and at least one
 * of the threads modifies the set, it <i>must</i> be synchronized
 * externally.  This is typically accomplished by synchronizing on some
 * object that naturally encapsulates the set.
 * If no such object exists, the set should be "wrapped" using the
 * {@link Collections#synchronizedSortedSet Collections.synchronizedSortedSet}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the set: <pre>
 *   SortedSet s = Collections.synchronizedSortedSet(new TreeSet(...));</pre>
 *
 * <p>The iterators returned by this class's {@code iterator} method are
 * <i>fail-fast</i>: if the set is modified at any time after the iterator is
 * created, in any way except through the iterator's own {@code remove}
 * method, the iterator will throw a {@link ConcurrentModificationException}.
 * Thus, in the face of concurrent modification, the iterator fails quickly
 * and cleanly, rather than risking arbitrary, non-deterministic behavior at
 * an undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:   <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements maintained by this set
 *
 * @author Josh Bloch
 * @see Collection
 * @see Set
 * @see HashSet
 * @see Comparable
 * @see Comparator
 * @see TreeMap
 * @since 1.2
 */

/*
 * TreeSet是有序Set，有序的含义由外部/内部比较器给出
 * TreeSet的内部实现一般是借助TreeMap完成的（只使用Map中的keySet部分）
 */
public class TreeSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable {
    
    // Dummy value to associate with an Object in the backing Map
    private static final Object PRESENT = new Object();
    
    /**
     * The backing map.
     */
    private transient NavigableMap<E, Object> m;
    
    
    
    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Constructs a new, empty tree set, sorted according to the
     * natural ordering of its elements.  All elements inserted into
     * the set must implement the {@link Comparable} interface.
     * Furthermore, all such elements must be <i>mutually
     * comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.  If the user attempts to add an element
     * to the set that violates this constraint (for example, the user
     * attempts to add a string element to a set whose elements are
     * integers), the {@code add} call will throw a
     * {@code ClassCastException}.
     */
    //构造一个新的空树集，按照其元素的自然顺序排序
    public TreeSet() {
        this(new TreeMap<>());
    }
    
    /**
     * Constructs a new, empty tree set, sorted according to the specified
     * comparator.  All elements inserted into the set must be <i>mutually
     * comparable</i> by the specified comparator: {@code comparator.compare(e1,
     * e2)} must not throw a {@code ClassCastException} for any elements
     * {@code e1} and {@code e2} in the set.  If the user attempts to add
     * an element to the set that violates this constraint, the
     * {@code add} call will throw a {@code ClassCastException}.
     *
     * @param comparator the comparator that will be used to order this set.
     *                   If {@code null}, the {@linkplain Comparable natural
     *                   ordering} of the elements will be used.
     */
    public TreeSet(Comparator<? super E> comparator) {
        this(new TreeMap<>(comparator));
    }
    
    /**
     * Constructs a new tree set containing the elements in the specified
     * collection, sorted according to the <i>natural ordering</i> of its
     * elements.  All elements inserted into the set must implement the
     * {@link Comparable} interface.  Furthermore, all such elements must be
     * <i>mutually comparable</i>: {@code e1.compareTo(e2)} must not throw a
     * {@code ClassCastException} for any elements {@code e1} and
     * {@code e2} in the set.
     *
     * @param c collection whose elements will comprise the new set
     *
     * @throws ClassCastException   if the elements in {@code c} are
     *                              not {@link Comparable}, or are not mutually comparable
     * @throws NullPointerException if the specified collection is null
     */
    public TreeSet(Collection<? extends E> c) {
        this();
        addAll(c);
    }
    
    /**
     * Constructs a new tree set containing the same elements and
     * using the same ordering as the specified sorted set.
     *
     * @param s sorted set whose elements will comprise the new set
     *
     * @throws NullPointerException if the specified sorted set is null
     */
    public TreeSet(SortedSet<E> s) {
        this(s.comparator());
        addAll(s);
    }
    
    /**
     * Constructs a set backed by the specified navigable map.
     */
    TreeSet(NavigableMap<E, Object> m) {
        this.m = m;
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */

    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if
     * the set contains no element {@code e2} such that
     * {@code Objects.equals(e, e2)}.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param e element to be added to this set
     *
     * @return {@code true} if this set did not already contain the specified
     * element
     *
     * @throws ClassCastException   if the specified object cannot be compared
     *                              with the elements currently in this set
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     */
    // 向Set中添加元素
    public boolean add(E e) {
        return m.put(e, PRESENT) == null;
    }
    
    /**
     * Adds all of the elements in the specified collection to this set.
     *
     * @param c collection containing elements to be added to this set
     *
     * @return {@code true} if this set changed as a result of the call
     *
     * @throws ClassCastException   if the elements provided cannot be compared
     *                              with the elements currently in the set
     * @throws NullPointerException if the specified collection is null or
     *                              if any element is null and this set uses natural ordering, or
     *                              its comparator does not permit null elements
     */
    // 将指定容器中的元素添加到当前Set中
    public boolean addAll(Collection<? extends E> c) {
        // Use linear-time version if applicable
        if(m.size() == 0 && c.size()>0 && c instanceof SortedSet && m instanceof TreeMap) {
            SortedSet<? extends E> set = (SortedSet<? extends E>) c;
            
            TreeMap<E, Object> map = (TreeMap<E, Object>) m;
            
            Comparator<?> cc = set.comparator();
            Comparator<? super E> mc = map.comparator();
            
            if(cc == mc || (cc != null && cc.equals(mc))) {
                map.addAllForTreeSet(set, PRESENT);
                return true;
            }
        }
        
        return super.addAll(c);
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */

    /*▼ 取值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    // 返回遍历当前集合时的首个元素
    public E first() {
        return m.firstKey();
    }
    
    /**
     * @throws NoSuchElementException {@inheritDoc}
     */
    // 返回遍历当前集合时的最后一个元素
    public E last() {
        return m.lastKey();
    }
    
    /**
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     * @since 1.6
     */
    // 〖前驱〗获取遍历当前Set时形参e的前驱
    public E lower(E e) {
        return m.lowerKey(e);
    }
    
    /**
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     * @since 1.6
     */
    // 〖后继〗获取遍历当前Set时形参e的后继
    public E higher(E e) {
        return m.higherKey(e);
    }
    
    /**
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     * @since 1.6
     */
    // 【前驱】获取遍历当前Set时形参e的前驱（包括e本身）
    public E floor(E e) {
        return m.floorKey(e);
    }
    
    /**
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException if the specified element is null
     *                              and this set uses natural ordering, or its comparator
     *                              does not permit null elements
     * @since 1.6
     */
    // 【后继】获取遍历当前Set时形参e的后继（包括e本身）
    public E ceiling(E e) {
        return m.ceilingKey(e);
    }
    
    /*▲ 取值 ████████████████████████████████████████████████████████████████████████████████┛ */
