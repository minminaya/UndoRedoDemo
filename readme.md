## 撤销删除环形管理类

撤销重做的功能用途很广泛，比如平时用到的Ctrl+Z和Ctrl+Y组合，比如图片编辑的左右撤销功能。这种逻辑有很多方法可以实现，比如控制一个指针，比如用命令模式包装控制命令、比如管理两个队列。下面我要做的是一种不需要维护数字指针的实现。

- 1、可以左右撤销数据
- 2、可以控制向左撤销的最大数量
- 3、方便存任意的数据
- 4、希望是一个可以统一使用的工具类

## 一、思路

分析需求，我们需要的是主要是3个功能，添加数据、往左得到左边的数据、往右得到右边的数据，正常情况下，如果不要数字指针，也就说明不会有数字的增和减，那么只能用链表来实现。为了节省内存，可以考虑使用环形链表实现。并且可以用一个当前的节点的全局变量来代替数字的指针。

图绘制中。

![](https://upload-images.jianshu.io/upload_images/3515789-06b0bb5520132fec.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 1、没有元素的情况
- 2、2个元素的情况
- 3、3个元素的情况
- 4、5个元素的情况
- 5、删除一个元素
- 6、删除全部元素

## 二、目录

- 1、定义各个节点
- 2、往链表尾部插入节点
- 3、判断左右边界
- 4、拿到向左向右的数据
- 5、删除节点之后的数据
- 6、将当前的头部节点前移
- 7、遍历链表得到所有节点的数量
- 8、封装put函数
- 9、封装undo redo函数
- 10、删除链表所有数据

## 三、代码实现

针对上面的思路，代码实现如下

##### 1、定义各个节点

```
 	// 头结点
    private Node<T> mHead;
    // 尾结点
    private Node<T> mTail;
    // 当前的显示的节点
    private Node<T> mCurrentNode;
```

##### 2、往链表尾部插入节点

```
 	/**
     * 在链表表尾插入一个结点
     *
     * @param data
     */
    private void insertInTail(T data) {
        Node<T> newNode = new Node<>(data);
        // 保存为当前的节点
        this.mCurrentNode = newNode;
        if (mTail == null) {
            // 为null，说明是尾节点
            mHead = newNode;
            mTail = newNode;

            // 和头部相连接，形成环形双向链表
            mTail.mNext = mHead;
            mHead.mPrevious = mTail;
        } else {
            newNode.mPrevious = mTail;
            mTail.mNext = newNode;
            mTail = newNode;

            // 和头部相连接，形成环形双向链表
            mTail.mNext = mHead;
            mHead.mPrevious = mTail;
        }
    }
```

先把数据存入当前显示的节点，接着判断当前链表中有没有元素。如果没有那么当前的节点既是头部节点又是尾部节点，然后还要将他们头尾相连。如果不为空说明当前链表有数据，那么将新元素接到尾部节点上，并且新的节点变为尾部节点，并且与头部节点相连。

##### 3、判断左右边界

```

    /**
     * 是否是左边界
     *
     * @return false代表是左边界
     */
    public boolean isLeftBound() {
        return mCurrentNode == mHead || mCurrentNode == null;
    }
```

右边界和左边界原理一样。这里只分析左边界，只需要判断当前的节点是否是头结点（链表有数据）或者是否为空（链表无数据时）。

##### 4、拿到向左向右的数据

```
private T getPreNode() {
        if (mHead == null) {
            return null;
        }
        if (isLeftBound()) {
            // 如果是左边界
            return mHead.mData;
        }
        mCurrentNode = mCurrentNode.mPrevious;
        return mCurrentNode.mData;
    }
```

向右的原理一样。这里只分析向左，想判断头部书否为空，如果为空，说明链表无数据，直接返回空。接着判断是否是左边界，如果是左边界，那么返回头部的数据。如果不是上面2种情况那么返回前一个节点的数据。

- 到这里已经实现需求的1、3、4了，还差控制数量的实现，下面的方法是为了控制数量而实现的。
		

##### 5、删除节点之后的数据

假设现在添加了5个数据，然后撤销了2步（当前节点停在第3个元素），如果这个时候需要往链表里添加数据，那么需要将第4、第5个数据删掉。所以就有了```deleteAfterNode（）```方法

```
/**
     * 删除链表指定结点之后的元素，具体做法是当前的Node直接连接头节点
     *
     * @param node
     * @return
     */
    private void deleteAfterNode(Node<T> node) {
        if (node == null) {
            return;
        }
        
        Node<T> cur = node.mNext;
        while (cur != mHead) {
            Node<T> dest = cur;
            cur = cur.mNext;

            dest.mNext = null;
            dest.mPrevious = null;
        }
        
        mTail = node;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }
```

先判断是否是空链表。如果不是则从当前节点的下一节点开始清空节点元素，并且最后把当前的节点变为尾节点并且，直接连接头节点。

##### 6、将当前的头部节点前移

假设当前链表中节点数量到了峰值（比如5个），那么再往里面添加一个数据，就变成了6个，为了稳定在5个，那么要把前面的头部给删掉。
```
 	/**
     * 当前的指针头部前移
     */
    private void replaceCurrentHead() {
        Node<T> node = mHead;
        mHead = mHead.mNext;

        node.mNext = null;
        node.mPrevious = null;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }
```

##### 7、遍历链表得到所有节点的数量

```
 	/**
     * 返回计算后的链表长度
     *
     * @return
     */
    private int size() {
        if (mTail == null) {
            // 如果尾部没有值，那么size为0
            return 0;
        }
        // 尾部有值的情况
        int size = 1;
        // 如果尾部有值，那么开始遍历每一个项
        Node cur = mTail;
        while (cur != mTail.mNext) {
            size++;
            cur = cur.mPrevious;
        }
        return size;
    }
```

##### 8、封装put函数

```
	public void put(T data) {
        deleteAfterNode(mCurrentNode);
        if (size() >= mCount) {
            insertInTail(data);
            // 当前的头部前移
            replaceCurrentHead();
            return;
        }
        // 执行插入
        insertInTail(data);
    }
```

每次添加数据，先清除当前节点后面的数据，如果当前链表的节点数量大于峰值，那么将头部前移。

##### 9、封装undo redo函数

```
	/**
     * 向左撤销
     *
     * @return
     */
    public T undo() {
        return getPreNode();
    }

    /**
     * 向后恢复
     *
     * @return
     */
    public T redo() {
        return getNextNode();
    }
```

##### 10、删除链表所有数据

```
	/**
     * 删除链表所有数据
     */
    public void removeAll() {
        if (mHead == null) {
            return;
        }
        Node cur = mHead;
        while (cur != mHead.mPrevious) {
            Node dest = cur;
            cur = cur.mNext;

            dest.mNext = null;
            dest.mPrevious = null;
        }
        mHead = null;
        mTail = null;
        mCurrentNode = null;
    }
```

## 四、测试

![1.gif](https://upload-images.jianshu.io/upload_images/3515789-f7209039da033845.gif?imageMogr2/auto-orient/strip)


