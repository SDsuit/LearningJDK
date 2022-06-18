# ArrayLIst的扩容机制

## 主要代码：

```java
 // 如果顺序表已满，则需要扩容
        if((s = size) == (elementData = this.elementData).length){
            // 对当前顺序表扩容
            elementData = grow();
}
```

```java
    // 对当前顺序表扩容，minCapacity是申请的容量
    private Object[] grow(int minCapacity) {
        // 根据申请的容量，返回一个合适的新容量
        int newCapacity = newCapacity(minCapacity);
        return elementData = Arrays.copyOf(elementData, newCapacity);
    }
    
```

```java
private int newCapacity(int minCapacity) {
        int oldCapacity = elementData.length;   // 旧容量
        int newCapacity = oldCapacity + (oldCapacity >> 1); // 预期新容量（增加0.5倍）
        
        // 如果预期新容量小于申请的容量
        if(newCapacity - minCapacity<=0) {
            // 如果数组还未初始化
            if(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
                // 返回一个初始容量
                return Math.max(DEFAULT_CAPACITY, minCapacity);
            }
            
            // 溢出
            if(minCapacity<0) {
                // overflow
                throw new OutOfMemoryError();
            }
            
            return minCapacity;
        }
        
        // 在预期新容量大于申请的容量时，按新容量走
        return (newCapacity - MAX_ARRAY_SIZE<=0) ? newCapacity : hugeCapacity(minCapacity);
    }
```

```java
// 大容量处理
    private static int hugeCapacity(int minCapacity) {
        if(minCapacity<0) {
            // overflow
            throw new OutOfMemoryError();
        }
        
        return (minCapacity>MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }
```

## 解释说明

在添加元素至ArrayLIst中时，判断容量是否已满，若已满则进入grow()方法，minCapacity意为申请的容量大小，oldCapacity为原数组的大小，newCapacoty意为原数组扩容1.5倍后的容量大小即预计新容量，也就是说默认情况下，每次数组扩容后的容量为原本容量的1.5倍。

但是当minCapacity大于newCapacoty时，若数组还未初始化，返回一个初始容量，其余情况数组会扩容为minCapacity，另一种情况是newCapacoty大于minCapacity时，会判断newCapacoty与MAX_ARRAY_SIZE(最大数组容量大小，值为Integer.MAX_VALUE - 8)之间的数值关系，newCapacoty未超出MAX_ARRAY_SIZE时返回newCapacoty，否则进入hugeCapacity()方法。

在hugeCapacity()方法中，如果minCapacity得到了一个负数（也就是说这个值已经超过了Integer最大值），就会抛出一个“内存溢出”的异常，否则会比较minCapacity与MAX_ARRAY_SIZE的值的大小，若minCapacity更大则返回Integer.MAX_VALUE，否则返回MAX_ARRAY_SIZE。



