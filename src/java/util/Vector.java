/*
 * Copyright (c) 1994, 2018, Oracle and/or its affiliates. All rights reserved.
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
import java.io.StreamCorruptedException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The {@code Vector} class implements a growable array of
 * objects. Like an array, it contains components that can be
 * accessed using an integer index. However, the size of a
 * {@code Vector} can grow or shrink as needed to accommodate
 * adding and removing items after the {@code Vector} has been created.
 *
 * <p>Each vector tries to optimize storage management by maintaining a
 * {@code capacity} and a {@code capacityIncrement}. The
 * {@code capacity} is always at least as large as the vector
 * size; it is usually larger because as components are added to the
 * vector, the vector's storage increases in chunks the size of
 * {@code capacityIncrement}. An application can increase the
 * capacity of a vector before inserting a large number of
 * components; this reduces the amount of incremental reallocation.
 *
 * <p id="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:
 * if the vector is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.  The {@link Enumeration Enumerations} returned by
 * the {@link #elements() elements} method are <em>not</em> fail-fast; if the
 * Vector is structurally modified at any time after the enumeration is
 * created then the results of enumerating are undefined.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>As of the Java 2 platform v1.2, this class was retrofitted to
 * implement the {@link List} interface, making it a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.  Unlike the new collection
 * implementations, {@code Vector} is synchronized.  If a thread-safe
 * implementation is not needed, it is recommended to use {@link
 * ArrayList} in place of {@code Vector}.
 *
 * @param <E> Type of component elements
 *
 * @author Lee Boynton
 * @author Jonathan Payne
 * @see Collection
 * @see LinkedList
 * @since 1.0
 */
// 向量表：线性表的顺序存储结构，内部使用数组实现，可以看做是ArrayList的线程安全版本
public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    
    /**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    
    /**
     * The array buffer into which the components of the vector are
     * stored. The capacity of the vector is the length of this array buffer,
     * and is at least large enough to contain all the vector's elements.
     *
     * <p>Any array elements following the last element in the Vector are null.
     *
     * @serial
     */
    protected Object[] elementData; // 存储当前向量表的元素
    
    /**
     * The number of valid components in this {@code Vector} object.
     * Components {@code elementData[0]} through
     * {@code elementData[elementCount-1]} are the actual items.
     *
     * @serial
     */
    protected int elementCount; // 元素数量
    
    /**
     * The amount by which the capacity of the vector is automatically
     * incremented when its size becomes greater than its capacity.  If
     * the capacity increment is less than or equal to zero, the capacity
     * of the vector is doubled each time it needs to grow.
     *
     * @serial
     */
    protected int capacityIncrement;    // 扩容增量
    
    
    
    
    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Constructs an empty vector so that its internal data array
     * has size {@code 10} and its standard capacity increment is
     * zero.
     */
    //默认容量大小为10，增量大小为0
    public Vector() {
        this(10);
    }
    
    /**
     * Constructs an empty vector with the specified initial capacity and
     * with its capacity increment equal to zero.
     *
     * @param initialCapacity the initial capacity of the vector
     *
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }
    
    /**
     * Constructs an empty vector with the specified initial capacity and
     * capacity increment.
     *
     * @param initialCapacity   the initial capacity of the vector
     * @param capacityIncrement the amount by which the capacity is
     *                          increased when the vector overflows
     *
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if(initialCapacity<0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }
    
    /**
     * Constructs a vector containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this
     *          vector
     *
     * @throws NullPointerException if the specified collection is null
     * @since 1.2
     */
    //用一个集合创建Vector
    public Vector(Collection<? extends E> c) {
        elementData = c.toArray();
        elementCount = elementData.length;
        // defend against c.toArray (incorrectly) not returning Object[]
        // (see e.g. https://bugs.openjdk.java.net/browse/JDK-6260652)
        if(elementData.getClass() != Object[].class) {
            elementData = Arrays.copyOf(elementData, elementCount, Object[].class);
        }
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Appends the specified element to the end of this Vector.
     *
     * @param e element to be appended to this Vector
     *
     * @return {@code true} (as specified by {@link Collection#add})
     *
     * @since 1.2
     */
    // 将元素e追加到当前向量表中，返回值指示是否添加成功，添加synchronized，线程安全
    public synchronized boolean add(E e) {
        modCount++;
        add(e, elementData, elementCount);
        return true;
    }
    
    /**
     * Adds the specified component to the end of this vector,
     * increasing its size by one. The capacity of this vector is
     * increased if its size becomes greater than its capacity.
     *
     * <p>This method is identical in functionality to the
     * {@link #add(Object) add(E)}
     * method (which is part of the {@link List} interface).
     *
     * @param obj the component to be added
     */
    // 将元素e追加到当前向量表中
    public synchronized void addElement(E e) {
        modCount++;
        add(e, elementData, elementCount);
    }
    
    /**
     * Inserts the specified element at the specified position in this Vector.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     *
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *                                        ({@code index < 0 || index > size()})
     * @since 1.2
     */
    // 将元素element添加到向量表index处
    public void add(int index, E element) {
        insertElementAt(element, index);
    }
    
    /**
     * Inserts the specified object as a component in this vector at the
     * specified {@code index}. Each component in this vector with
     * an index greater or equal to the specified {@code index} is
     * shifted upward to have an index one greater than the value it had
     * previously.
     *
     * <p>The index must be a value greater than or equal to {@code 0}
     * and less than or equal to the current size of the vector. (If the
     * index is equal to the current size of the vector, the new element
     * is appended to the Vector.)
     *
     * <p>This method is identical in functionality to the
     * {@link #add(int, Object) add(int, E)}
     * method (which is part of the {@link List} interface).  Note that the
     * {@code add} method reverses the order of the parameters, to more closely
     * match array usage.
     *
     * @param obj   the component to insert
     * @param index where to insert the new component
     *
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *                                        ({@code index < 0 || index > size()})
     */
    // 将元素element添加到向量表index处
    public synchronized void insertElementAt(E element, int index) {
        if(index>elementCount) {
            throw new ArrayIndexOutOfBoundsException(index + " > " + elementCount);
        }
        
        modCount++;
        
        final int s = elementCount;
        
        Object[] elementData = this.elementData;
        
        // 如果向量表已满，则需要扩容
        if(s == elementData.length) {
            // 对当前向量表扩容
            elementData = grow();
        }
        
        // 移动元素
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        
        // 插入元素
        elementData[index] = element;
        
        elementCount = s + 1;
    }
    
    /**
     * Appends all of the elements in the specified Collection to the end of
     * this Vector, in the order that they are returned by the specified
     * Collection's Iterator.  The behavior of this operation is undefined if
     * the specified Collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified Collection is this Vector, and this Vector is nonempty.)
     *
     * @param c elements to be inserted into this Vector
     *
     * @return {@code true} if this Vector changed as a result of the call
     *
     * @throws NullPointerException if the specified collection is null
     * @since 1.2
     */
    // 将指定容器中的元素追加到当前向量表中
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        modCount++;
        
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        //使用同步代码块而不是同步方法，给核心代码加锁
        synchronized(this) {
            Object[] elementData = this.elementData;
            final int s = elementCount;
            if(numNew>elementData.length - s) {
                elementData = grow(s + numNew);
            }
            System.arraycopy(a, 0, elementData, s, numNew);
            elementCount = s + numNew;
            return true;
        }
    }
    
    /**
     * Inserts all of the elements in the specified Collection into this
     * Vector at the specified position.  Shifts the element currently at
     * that position (if any) and any subsequent elements to the right
     * (increases their indices).  The new elements will appear in the Vector
     * in the order that they are returned by the specified Collection's
     * iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c     elements to be inserted into this Vector
     *
     * @return {@code true} if this Vector changed as a result of the call
     *
     * @throws ArrayIndexOutOfBoundsException if the index is out of range
     *                                        ({@code index < 0 || index > size()})
     * @throws NullPointerException           if the specified collection is null
     * @since 1.2
     */
    // 将指定容器中的元素添加到当前向量表的index处
    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        if(index<0 || index>elementCount) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        Object[] elementData = this.elementData;
        final int s = elementCount;
        if(numNew>elementData.length - s) {
            elementData = grow(s + numNew);
        }
        
        int numMoved = s - index;
        if(numMoved>0) {
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        }
        System.arraycopy(a, 0, elementData, index, numNew);
        elementCount = s + numNew;
        return true;
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    