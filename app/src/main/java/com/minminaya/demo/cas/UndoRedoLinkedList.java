package com.minminaya.demo.cas;

import android.util.Log;

import java.io.Serializable;

/**
 * 撤销删除环型双向链表，既是管理类也是数据结构(CAS实现)
 * <p></p>
 * T为要存储的数据
 *
 * @time Created by 2018/8/30 13:56
 */
@Deprecated
public class UndoRedoLinkedList<T extends UndoRedoLinkedList.Entry> implements Serializable {
    private final static String TAG = UndoRedoLinkedList.class.getSimpleName();
    private static final long serialVersionUID = -2767605621001330L;


    //头结点
    private UndoRedoLinkedList<T> mHead;
    //尾结点
    private UndoRedoLinkedList<T> mTail;
    // 当前的显示的节点
    private UndoRedoLinkedList<T> mCurrentNode;
    private int mCount = 30;

    //业务的数据
    private T mData;
    private UndoRedoLinkedList<T> mPrevious;
    private UndoRedoLinkedList<T> mNext;

    private int mIndex = 0;

    /**
     * @param data 管理类只需要传入null
     */
    public UndoRedoLinkedList(T data) {
        mData = data;
    }


    private boolean compareAndSet(UndoRedoLinkedList<T> expect, UndoRedoLinkedList<T> newNode) {
        synchronized (this) {
            if (mCurrentNode == expect) {
                mCurrentNode = newNode;
                return true;
            }
        }
        return false;
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
        synchronized (UndoRedoLinkedList.this) {
            deleteAfterNode(mCurrentNode);
            //判断有没有已存在node下，则将当前数据移动到尾部
//        moveToTail(data);
            if (size() >= mCount) {
                insertInTail(data);
                //当前的头部前移
                replaceCurrentHead();
                return;
            }
            //执行插入
            insertInTail(data);

            Log.d(TAG, "当前线程：" + Thread.currentThread().getName() + "，index:" + (mIndex++));
        }
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
    public synchronized void removeAll() {
        if (mHead == null) {
            return;
        }
        UndoRedoLinkedList<T> cur = mHead;
        while (cur != mHead.mPrevious) {
            UndoRedoLinkedList<T> dest = cur;
            cur = cur.mNext;

            dest.mData.onDestroy();

            dest.mNext = null;
            dest.mPrevious = null;
        }
        mHead = null;
        mTail = null;
        mCurrentNode = null;
    }

//    /**
//     * 移动到尾部
//     *
//     * @param data
//     */
//    @Deprecated
//    private void moveToTail(T data) {
//        UndoRedoLinkedList<T> node = new UndoRedoLinkedList<>(data);
//        if (checkEqualNode(node)) {
//            //将移动点的节点前后两个节点互相连接
//            UndoRedoLinkedList<T> preNode = node.mPrevious;
//            UndoRedoLinkedList<T> nextNode = node.mNext;
//            preNode.mNext = nextNode;
//            nextNode.mPrevious = preNode;
//
//            //当前移动节点移动到尾部
//            mTail.mNext = node;
//            node.mPrevious = mTail;
//            mTail = node;
//
//            //头尾相连接
//            mTail.mNext = mHead;
//            mHead.mPrevious = mTail;
//        }
//    }

//    /**
//     * 遍历链表，判断有没有相同的节点存在
//     *
//     * @param newNode
//     * @return false 说明不存在相同的节点，true说明存在相同的节点
//     */
//    private boolean checkEqualNode(UndoRedoLinkedList newNode) {
//        if (mHead == null) {
//            return false;
//        }
//        UndoRedoLinkedList<T> node = mHead;
//        for (; ; ) {
//            if (newNode == node) {
//                //找到同节点则返回true
//                return true;
//            }
//            node = node.mNext;
//            if (node == mHead) {
//                //遍历完没有找到则返回false
//                return false;
//            }
//        }
//    }

    /**
     * 当前的指针头部前移
     */
    private void replaceCurrentHead() {
        UndoRedoLinkedList<T> node = mHead;

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
        UndoRedoLinkedList<T> cur = mTail;
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
        UndoRedoLinkedList<T> newNode = new UndoRedoLinkedList<>(data);
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
    private void deleteAfterNode(UndoRedoLinkedList<T> node) {
        if (node == null) {
            return;
        }

        UndoRedoLinkedList<T> cur = node.mNext;
        while (cur != mHead) {
            UndoRedoLinkedList<T> dest = cur;
            cur = cur.mNext;

            dest.mData.onDestroy();

            dest.mNext = null;
            dest.mPrevious = null;
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
            // 如果是左边界，同步无影响
            return mHead.mData;
        }

        //CAS操作
        UndoRedoLinkedList<T> expect;
        UndoRedoLinkedList<T> newNode;
        do {
            expect = mCurrentNode;
            newNode = mCurrentNode.mPrevious;
        } while (!compareAndSet(expect, newNode));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "当前线程：" + Thread.currentThread().getName() + "，向左的index:" + (mIndex--));
        return newNode.mData;
    }

    private T getNextNode() {
        if (mTail == null) {
            return null;
        }
        if (isRightBound()) {
            // 如果是右边界，同步无影响
            return mTail.mData;
        }

        //CAS操作
        UndoRedoLinkedList<T> expect;
        UndoRedoLinkedList<T> newNode;
        do {
            expect = mCurrentNode;
            newNode = mCurrentNode.mNext;
        } while (!compareAndSet(expect, newNode));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "当前线程：" + Thread.currentThread().getName() + "，向右的index:" + (mIndex++));

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

    public void showData() {
        //测试有没有对当前的T中的数据进行删除
//        mCurrentNode.mData.onDestroy();
        UndoRedoLinkedList<T> node = mHead;
        for (; ; ) {
            if (node == null) {
                return;
            }
//            Log.d(TAG, "当前线程：" + Thread.currentThread().getName());
//            Log.d(TAG, "数据data：" + ((UndoRedoBean) node.mData).getData() + "，index：" + ((UndoRedoBean) node.mData).getIndex());
            node = node.mNext;
            if (node == mHead) {
                return;
            }
        }
    }

    public interface Entry {
        void onDestroy();
    }
}
