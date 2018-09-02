package com.minminaya.demo.two;

/**
 * 撤销删除环型双向链表
 * <p></p>
 * T为要存储的数据
 *
 * @author LiGuangMin
 * @email lgm@meitu.com
 * @time Created by 2018/8/30 13:56
 */
public class UndoRedoLinkList<T> {
    //头结点
    private UndoRedoLinkList<T> mHead;
    //尾结点
    private UndoRedoLinkList<T> mTail;
    // 当前的显示的节点
    private UndoRedoLinkList<T> mCurrentNode;
    private int mCount = 5;

    //业务的数据
    private T mData;
    private UndoRedoLinkList<T> mPrevious;
    private UndoRedoLinkList<T> mNext;

    /**
     * @param data 管理类只需要传入null
     */
    public UndoRedoLinkList(T data) {
        mData = data;
    }

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
     * 删除链表所有数据
     */
    public void removeAll() {
        if (mHead == null) {
            return;
        }
        UndoRedoLinkList<T> cur = mHead;
        while (cur != mHead.mPrevious) {
            UndoRedoLinkList<T> dest = cur;
            cur = cur.mNext;

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
        UndoRedoLinkList<T> cur = mTail;
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
        UndoRedoLinkList<T> newNode = new UndoRedoLinkList<>(data);
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
    private void deleteAfterNode(UndoRedoLinkList<T> node) {
        if (node == null) {
            return;
        }
        mTail = node;

        mTail.mNext = mHead;
        mHead.mPrevious = mTail;
    }

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

    private T getNextNode() {
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
    public boolean isLeftBound() {
        return mCurrentNode == mHead || mCurrentNode == null;
    }

    /**
     * 是否是右边界
     *
     * @return true代表是右边界
     */
    public boolean isRightBound() {
        return mCurrentNode == mTail || mCurrentNode == null;
    }
}
