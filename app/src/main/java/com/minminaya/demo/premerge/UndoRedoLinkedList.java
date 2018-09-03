package com.minminaya.demo.premerge;

/**
 * 撤销删除环型双向链表实现
 * <p></p>
 * T为要存储的数据
 *
 * @time Created by 2018/8/29 18:47
 */
public class UndoRedoLinkedList<T extends UndoRedoLinkedList.Entry> {
    // 头结点
    private Node<T> mHead;
    // 尾结点
    private Node<T> mTail;
    // 当前的显示的节点
    private volatile Node<T> mCurrentNode;
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
        synchronized (UndoRedoLinkedList.this) {
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
    }

    /**
     * 向左撤销
     *
     * @return
     */
    public synchronized T undo() {
        return getPreNode();
    }

    /**
     * 向后恢复
     *
     * @return
     */
    public synchronized T redo() {
        return getNextNode();
    }

    /**
     * 删除链表所有数据
     */
    public synchronized void removeAll() {
        if (mHead == null) {
            return;
        }
        Node<T> cur = mHead;
        while (cur != mHead.mPrevious) {
            Node<T> dest = cur;
            cur = cur.mNext;

            dest.mData.onDestroy();
            dest.mNext = null;
            dest.mPrevious = null;
        }
        mHead = null;
        mTail = null;
        mCurrentNode = null;
    }

    /**
     * 当前的指针头部前移
     */
    private void replaceCurrentHead() {
        Node<T> node = mHead;
        mHead = mHead.mNext;

        //头部置空
        node.mData.onDestroy();
        node.mNext = null;
        node.mPrevious = null;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }

    /**
     * 返回计算后的链表长度
     *
     * @return
     */
    private synchronized int size() {
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

            dest.mData.onDestroy();
            dest.mNext = null;
            dest.mPrevious = null;
        }

        mTail = node;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }

    private synchronized T getPreNode() {
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

    private synchronized T getNextNode() {
        if (mTail == null) {
            return null;
        }
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
    public synchronized boolean isLeftBound() {
        return mCurrentNode == mHead || mCurrentNode == null;
    }

    /**
     * 是否是右边界
     *
     * @return true代表是右边界
     */
    public synchronized boolean isRightBound() {
        return mCurrentNode == mTail || mCurrentNode == null;
    }

    private static class Node<T> {
        // 业务的数据
        private T mData;
        private volatile Node<T> mPrevious;
        private volatile Node<T> mNext;

        Node(T data) {
            mData = data;
        }
    }

    public interface Entry {
        void onDestroy();
    }
}
