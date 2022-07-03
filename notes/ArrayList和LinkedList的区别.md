# ArrayList和LinkedList的区别

ArrayList 与 LinkedList 都是 List 接口的实现类，数据的存储都是有序的并且都允许存放重复元素。

### 底层实现：

ArrayList是由动态数组实现的；LinkedList是由双链表实现的。

### 扩容机制：

ArrayList每次扩容会扩大50%；而LinkedList由于底层实现是双链表，所以并没有所谓的扩容机制。

### 查找性能差异：

ArrayList底层是有序数组，可以通过数组下标查找到相应的元素，**速度较快**；而LinkedLIst底层是双链表，其中的元素通过指针互相连接，并不连续，如果要查找某个元素，则需要对整个链表进行遍历，**速度较慢**。

##### LinkedList查找元素源代码：

```java
public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

Node<E> node(int index) {
        if(index<(size >> 1)) {
            Node<E> x = first;
            for(int i = 0; i<index; i++) {
                x = x.next;
            }
            return x;
        } else {
            Node<E> x = last;
            for(int i = size - 1; i>index; i--) {
                x = x.prev;
            }
            return x;
        }
    }
```

##### ArrayList查找元素源代码：

```java
public E get(int index) {
        //检查传入的index是否合法（要求满足0<=index<length）
        Objects.checkIndex(index, size);
        return elementData(index);
    }
    
    E elementData(int index) {
        return (E) elementData[index];
    }
```



### 添加或删除性能差异：

ArrayList是一段连续的内存空间，其中存储的数据元素，如果要添加元素或者是删除元素的话，之前或者之后的元素都要发生位置的移动，**速度较慢**；LinkedList底层是双链表，在添加或者删除元素的时候，只需要操作该位置前后节点的指针即可，**速度较快**。

##### LinkedList删除元素源代码：

```java
public boolean remove(Object o) {
        if(o == null) {
            for(Node<E> x = first; x != null; x = x.next) {
                if(x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for(Node<E> x = first; x != null; x = x.next) {
                if(o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
```

##### ArrayList删除元素源代码：

```java
public E remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] es = elementData;
        
        @SuppressWarnings("unchecked")
        E oldValue = (E) es[index];
        
        // 移除es[index]
        fastRemove(es, index);
        
        return oldValue;
    }
```

### 安全性：

两者在单线程情况下均为安全，多线程情况下不是线程安全的。