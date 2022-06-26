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

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.function.Consumer;

/**
 * Doubly-linked list implementation of the {@code List} and {@code Deque}
 * interfaces.  Implements all optional list operations, and permits all
 * elements (including {@code null}).
 *
 * <p>All of the operations perform as could be expected for a doubly-linked
 * list.  Operations that index into the list will traverse the list from
 * the beginning or the end, whichever is closer to the specified index.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a linked list concurrently, and at least
 * one of the threads modifies the list structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more elements; merely setting the value of
 * an element is not a structural modification.)  This is typically
 * accomplished by synchronizing on some object that naturally
 * encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList Collections.synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new LinkedList(...));</pre>
 *
 * <p>The iterators returned by this class's {@code iterator} and
 * {@code listIterator} methods are <i>fail-fast</i>: if the list is
 * structurally modified at any time after the iterator is created, in
 * any way except through the Iterator's own {@code remove} or
 * {@code add} methods, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than
 * risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.
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
 * @param <E> the type of elements held in this collection
 * 
 * @author Josh Bloch
 * @see List
 * @see ArrayList
 * @since 1.2
 */
// 双向链表：线性表的链式存储结构，内部使用指针来连接各个结点
public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable, Serializable {
    
    /**
     * Pointer to first node.
     */
    // 链表的表头
    transient Node<E> first;
    
    /**
     * Pointer to last node.
     */
    // 链表的表尾
    transient Node<E> last;
    
    // 元素数量
    transient int size = 0;
    
    
    
    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Constructs an empty list.
     */
    public LinkedList() {
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
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 存值 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Appends the specified element to the end of this list.
     *
     * <p>This method is equivalent to {@link #addLast}.
     *
     * @param e element to be appended to this list
     *
     * @return {@code true} (as specified by {@link Collection#add})
     */
    // 将元素e追加到当前双向链表中
    public boolean add(E e) {
        linkLast(e);
        return true;
    }
    
    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    // 将元素element添加到双向链表index处
    public void add(int index, E element) {
        checkPositionIndex(index);
        
        if(index == size) {
            // 将指定的元素添加到链表结尾
            linkLast(element);
        } else {
            // 获取index处的结点
            Node<E> node = node(index);
            
            // 将元素element插入为node的前驱
            linkBefore(element, node);
        }
    }
    
    
    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's iterator.  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in
     * progress.  (Note that this will occur if the specified collection is
     * this list, and it's nonempty.)
     *
     * @param c collection containing elements to be added to this list
     *
     * @return {@code true} if this list changed as a result of the call
     *
     * @throws NullPointerException if the specified collection is null
     */
    // 将指定容器中的元素追加到当前双向链表中
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }
    
    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element
     *              from the specified collection
     * @param c     collection containing elements to be added to this list
     *
     * @return {@code true} if this list changed as a result of the call
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException      if the specified collection is null
     */
    // 将指定容器中的元素添加到当前双向链表的index处
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);
        
        Object[] a = c.toArray();
        int numNew = a.length;
        if(numNew == 0) {
            return false;
        }
        
        Node<E> pred, succ;
        if(index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }
        
        for(Object o : a) {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);
            if(pred == null) {
                first = newNode;
            } else {
                pred.next = newNode;
            }
            pred = newNode;
        }
        
        if(succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }
        
        size += numNew;
        modCount++;
        return true;
    }
    
    /*▲ 存值 ████████████████████████████████████████████████████████████████████████████████┛ */
