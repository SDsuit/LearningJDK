# ArrayList与Vector的区别

**1、Vector是线程安全的，ArrayList不是线程安全的。**

**2、ArrayList在底层数组不够用时在原来的基础上扩展0.5倍，Vector是扩展1倍。**

ArrayList和Vector都实现了List接口，他们都是数组实现的。

## **线程安全**

### ArrayList方法源码：

```java
public boolean add(E e) {
        modCount++;
        add(e, elementData, size);
        return true;
    }
```

### Vector方法源码：

```java
public synchronized boolean add(E e) {
        modCount++;
        add(e, elementData, elementCount);
        return true;
    }
```

可见，Vector加入了synchronized方法，只要是关键性的操作，Vector的方法前都加入了synchronized关键字，用于保证线程安全，但在执行带锁的方法时，时间的开销更大，因此在单线程的环境下，Vector效率是比不上ArrayList的。

## **扩容机制**

### ArrayList方法源码：

```java
private int newCapacity(int minCapacity) {
        int oldCapacity = elementData.length; 
        int newCapacity = oldCapacity + (oldCapacity >> 1); 
        
        if(newCapacity - minCapacity<=0) {
            if(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) 			{
                return Math.max(DEFAULT_CAPACITY, minCapacity);
            }
            if(minCapacity<0) {
                throw new OutOfMemoryError();
            }
            
            return minCapacity;
        }
```

### Vector方法源码：

```java
private int newCapacity(int minCapacity) {
        int oldCapacity = elementData.length; 
        int newCapacity = oldCapacity + ((capacityIncrement>0) ? capacityIncrement : oldCapacity); 
        if(newCapacity - minCapacity<=0) {
            if(minCapacity<0) {
                throw new OutOfMemoryError();
            }
            return minCapacity;
        }
        return (newCapacity - MAX_ARRAY_SIZE<=0) ? newCapacity : hugeCapacity(minCapacity);
    }
```

可见，Vector每次扩容为原数组大小的2倍，而ArrayList则扩容为原来的1.5倍.
