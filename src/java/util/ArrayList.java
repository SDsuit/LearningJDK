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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import jdk.internal.misc.SharedSecrets;

/**
 * Resizable-array implementation of the {@code List} interface.  Implements
 * all optional list operations, and permits all elements, including
 * {@code null}.  In addition to implementing the {@code List} interface,
 * this class provides methods to manipulate the size of the array that is
 * used internally to store the list.  (This class is roughly equivalent to
 * {@code Vector}, except that it is unsynchronized.)
 *
 * <p>The {@code size}, {@code isEmpty}, {@code get}, {@code set},
 * {@code iterator}, and {@code listIterator} operations run in constant
 * time.  The {@code add} operation runs in <i>amortized constant time</i>,
 * that is, adding n elements requires O(n) time.  All of the other operations
 * run in linear time (roughly speaking).  The constant factor is low compared
 * to that for the {@code LinkedList} implementation.
 *
 * <p>Each {@code ArrayList} instance has a <i>capacity</i>.  The capacity is
 * the size of the array used to store the elements in the list.  It is always
 * at least as large as the list size.  As elements are added to an ArrayList,
 * its capacity grows automatically.  The details of the growth policy are not
 * specified beyond the fact that adding an element has constant amortized
 * time cost.
 *
 * <p>An application can increase the capacity of an {@code ArrayList} instance
 * before adding a large number of elements using the {@code ensureCapacity}
 * operation.  This may reduce the amount of incremental reallocation.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an {@code ArrayList} instance concurrently,
 * and at least one of the threads modifies the list structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new ArrayList(...));</pre>
 *
 * <p id="fail-fast">
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove() remove} or
 * {@link ListIterator#add(Object) add} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/java.base/java/util/package-summary.html#CollectionsFramework">
 * Java Collections Framework</a>.
 *
 * @param <E> the type of elements in this list
 *
 * @author Josh Bloch
 * @author Neal Gafter
 * @see Collection
 * @see List
 * @see LinkedList
 * @see Vector
 * @since 1.2
 */
// 顺序表：线性表的顺序存储结构，内部使用数组实现，非线程安全
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
    
    /**
     * Default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 10;
    
    /**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    /**
     * Shared empty array instance used for empty instances.
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};
    
    /**
     * Shared empty array instance used for default sized empty instances. We
     * distinguish this from EMPTY_ELEMENTDATA to know how much to inflate when
     * first element is added.
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    
    /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
     //元素数组的容量
    transient Object[] elementData; // non-private to simplify nested class access
    
    /**
     * The size of the ArrayList (the number of elements it contains).
     *
     * @serial
     */
    //包含的元素数量
    private int size;



    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }
    
    /**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the list
     *
     * @throws IllegalArgumentException if the specified initial capacity
     *                                  is negative
     */
    public ArrayList(int initialCapacity) {
        if(initialCapacity>0) {
            this.elementData = new Object[initialCapacity];
        } else if(initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
    }
    
    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.
     *
     * @param c the collection whose elements are to be placed into this list
     *
     * @throws NullPointerException if the specified collection is null
     */
    public ArrayList(Collection<? extends E> c) {
        //toArray()将集合c以数组(Object[])形式返回
        elementData = c.toArray();
        //将size赋值为elementData数组的长度值，再判断是否非0
        if((size = elementData.length) != 0) {
            /*
             * defend against c.toArray (incorrectly) not returning Object[]
             * (see e.g. https://bugs.openjdk.java.net/browse/JDK-6260652)
             */
            if(elementData.getClass() != Object[].class) {
                elementData = Arrays.copyOf(elementData, size, Object[].class);
            }
        } else {
            // replace with empty array.
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     *
     * @return {@code true} (as specified by {@link Collection#add})
     */
    // 将元素e追加到当前顺序表中
    public boolean add(E e) {
        modCount++;
        add(e, elementData, size);
        return true;
    }
/**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 将元素element添加到顺序表index处，此位置后续的元素向右移动1位（索引加1）
    public void add(int index, E element) {
        rangeCheckForAdd(index);
        
        modCount++;
        
        final int s;
        Object[] elementData;
        
        // 如果顺序表已满，则需要扩容
        if((s = size) == (elementData = this.elementData).length) {
            // 对当前顺序表扩容
            elementData = grow();
        }
        
        // 移动元素
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        
        // 插入元素
        elementData[index] = element;
        
        size = s + 1;
    }
    
    
    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the
     * specified collection's Iterator.  The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     *
     * @return {@code true} if this list changed as a result of the call
     *
     * @throws NullPointerException if the specified collection is null
     */
    // 将指定容器中的元素追加到当前顺序表中
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        
        modCount++;
        
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        
        Object[] elementData;
        final int s;
        //判断集合剩余的空间大小是否能存储指定容器中的元素
        if(numNew>(elementData = this.elementData).length - (s = size)) {
            elementData = grow(s + numNew);
        }
        
        System.arraycopy(a, 0, elementData, s, numNew);
        
        size = s + numNew;
        
        return true;
    }
    
    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c     collection containing elements to be added to this list
     *
     * @return {@code true} if this list changed as a result of the call
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException      if the specified collection is null
     */
    // 将指定容器中的元素添加到当前顺序表的index处
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);
        
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        Object[] elementData;
        final int s;
        if(numNew>(elementData = this.elementData).length - (s = size)) {
            elementData = grow(s + numNew);
        }
        
        int numMoved = s - index;
        if(numMoved>0) {
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        }
        System.arraycopy(a, 0, elementData, index, numNew);
        size = s + numNew;
        return true;
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    /*▼ 取值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     *
     * @return the element at the specified position in this list
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 获取指定索引处的元素
    public E get(int index) {
        //检查传入的index是否合法（要求满足0<=index<length）
        Objects.checkIndex(index, size);
        return elementData(index);
    }
    
    /*▲ 取值 ████████████████████████████████████████████████████████████████████████████████┛ */
    
     /*▼ 移除 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     *
     * @return the element that was removed from the list
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 移除索引index处的元素，返回被移除的元素
    public E remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] es = elementData;
        
        @SuppressWarnings("unchecked")
        E oldValue = (E) es[index];
        
        // 移除es[index]
        fastRemove(es, index);
        
        return oldValue;
    }
    
    /**
     * Removes the first occurrence of the specified element from this list,
     * if it is present.  If the list does not contain the element, it is
     * unchanged.  More formally, removes the element with the lowest index
     * {@code i} such that
     * {@code Objects.equals(o, get(i))}
     * (if such an element exists).  Returns {@code true} if this list
     * contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     *
     * @param o element to be removed from this list, if present
     *
     * @return {@code true} if this list contained the specified element
     */
    // 移除指定的元素，返回值指示是否移除成功
    public boolean remove(Object o) {
        final Object[] es = elementData;
        final int size = this.size;
        int i = 0;
found:
        {
            if(o == null) {
                for(; i<size; i++) {
                    if(es[i] == null) {
                        break found;
                    }
                }
            } else {
                for(; i<size; i++) {
                    if(o.equals(es[i])) {
                        break found;
                    }
                }
            }
            return false;
        }
        
        // 移除es[index]
        fastRemove(es, i);
        
        return true;
    }
    
    
    /**
     * @throws NullPointerException {@inheritDoc}
     */
    // 移除满足条件的元素，移除条件由filter决定，返回值指示是否移除成功
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return removeIf(filter, 0, size);
    }
    
    
    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     *
     * @return {@code true} if this list changed as a result of the call
     *
     * @throws ClassCastException   if the class of an element of this list
     *                              is incompatible with the specified collection
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *                              specified collection does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>),
     *                              or if the specified collection is null
     * @see Collection#contains(Object)
     */
    // (匹配则移除)移除当前顺序表中所有与给定容器中的元素匹配的元素
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false, 0, size);
    }
    
    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     *
     * @return {@code true} if this list changed as a result of the call
     *
     * @throws ClassCastException   if the class of an element of this list
     *                              is incompatible with the specified collection
     *                              (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this list contains a null element and the
     *                              specified collection does not permit null elements
     *                              (<a href="Collection.html#optional-restrictions">optional</a>),
     *                              or if the specified collection is null
     * @see Collection#contains(Object)
     */
    // (不匹配则移除)移除当前顺序表中所有与给定容器中的元素不匹配的元素
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true, 0, size);
    }
    
    
    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.
     * Shifts any succeeding elements to the left (reduces their index).
     * This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     *
     * @throws IndexOutOfBoundsException if {@code fromIndex} or
     *                                   {@code toIndex} is out of range
     *                                   ({@code fromIndex < 0 ||
     *                                   toIndex > size() ||
     *                                   toIndex < fromIndex})
     */
    // 移除当前顺序表[fromIndex,toIndex]之间的元素
    protected void removeRange(int fromIndex, int toIndex) {
        if(fromIndex>toIndex) {
            throw new IndexOutOfBoundsException(outOfBoundsMsg(fromIndex, toIndex));
        }
        
        modCount++;
        
        // 移除lo~hi之间的元素
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }
    
    
    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    // 清空当前顺序表中的元素
    public void clear() {
        modCount++;
        final Object[] es = elementData;
        for(int to = size, i = size = 0; i<to; i++) {
            es[i] = null;
        }
    }
    
    /*▲ 移除 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 替换 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Replaces the element at the specified position in this list with
     * the specified element.
     *
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     *
     * @return the element previously at the specified position
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 将index处的元素更新为element，并返回旧元素
    public E set(int index, E element) {
        Objects.checkIndex(index, size);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }
    
    
    // 更新当前顺序表中所有元素，更新策略由operator决定
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        replaceAllRange(operator, 0, size);
        modCount++;
    }
    
    /*▲ 替换 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 包含查询 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns {@code true} if this list contains the specified element.
     * More formally, returns {@code true} if and only if this list contains
     * at least one element {@code e} such that
     * {@code Objects.equals(o, e)}.
     *
     * @param o element whose presence in this list is to be tested
     *
     * @return {@code true} if this list contains the specified element
     */
    // 判断当前顺序表中是否包含指定的元素
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }
    
    /*▲ 包含查询 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 定位 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the lowest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     */
    // 返回指定元素的正序索引(正序查找首个匹配的元素)
    public int indexOf(Object o) {
        // 在[0, size)之间正序搜索元素o，返回首个匹配的索引
        return indexOfRange(o, 0, size);
    }
    
    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * More formally, returns the highest index {@code i} such that
     * {@code Objects.equals(o, get(i))},
     * or -1 if there is no such index.
     */
    // 返回指定元素的逆序索引(逆序查找首个匹配的元素)
    public int lastIndexOf(Object o) {
        // 在[0, size)之间逆序搜索元素o，返回首个匹配的索引
        return lastIndexOfRange(o, 0, size);
    }
    
    /*▲ 定位 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
