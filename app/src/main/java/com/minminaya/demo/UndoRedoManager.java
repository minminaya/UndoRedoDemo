package com.minminaya.demo;


public class UndoRedoManager<T> {
    //头结点
    private Node mHead;
    //尾结点
    private Node mTail;
    // 当前的显示的节点
    private Node mCurrentNode;
    private int mCount = 5;

    /**
     * 设置可以缓存的最大数量
     *
     * @param count
     */
    public void setCount(int count) {
        mCount = count;
    }

    public void put(T data) {
        deleteAfterNode(mCurrentNode);
        if (size() >= mCount) {
            insertInTail(data);
            //当前的头部前移
            replaceCurrentHead();
            return;
        }
        //执行插入
        insertInTail(data);
    }

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

    /**
     * 当前的指针头部前移
     */
    private void replaceCurrentHead() {
        mHead = mHead.mNext;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }

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

    /**
     * 在链表表尾插入一个结点
     *
     * @param data
     */
    private void insertInTail(T data) {
        Node newNode = new Node(data);
        // 保存为当前的节点
        this.mCurrentNode = newNode;
        if (mTail == null) {
            // 为null，说明是尾节点
            mHead = newNode;
            mTail = newNode;

            //和头部相连接，形成环形双向链表
            mTail.mNext = mHead;
            mHead.mPrevious = mTail;
        } else {
            newNode.mPrevious = mTail;
            mTail.mNext = newNode;
            mTail = newNode;

            //和头部相连接，形成环形双向链表
            mTail.mNext = mHead;
            mHead.mPrevious = mTail;
        }
    }

    /**
     * 删除链表指定结点之后的元素，具体做法是当前的Node直接连接头节点
     *
     * @param node
     * @return
     */
    private void deleteAfterNode(Node node) {
        if (node == null) {
            return;
        }
        mTail = node;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }

    private T getPreNode() {
        if (isLeftBound()) {
            // 如果是左边界
            return mHead.mData;
        }
        mCurrentNode = mCurrentNode.mPrevious;
        return mCurrentNode.mData;
    }

    private T getNextNode() {
        if (isRightBound()) {
            // 如果是右边界
            return mTail.mData;
        }
        mCurrentNode = mCurrentNode.mNext;
        return mCurrentNode.mData;
    }

    /**
     * 是否是左边界
     *
     * @return false代表是左边界
     */
    private boolean isLeftBound() {
        return mCurrentNode == mHead || mCurrentNode == null;
    }

    /**
     * 是否是右边界
     *
     * @return true代表是右边界
     */
    private boolean isRightBound() {
        return mCurrentNode == mTail || mCurrentNode == null;
    }

    private class Node {
        //业务的数据
        private T mData;
        private Node mPrevious;
        private Node mNext;

        Node(T data) {
            mData = data;
        }
    }
}
