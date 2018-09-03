package com.minminaya.demo.bean;


import com.minminaya.demo.syntest.UndoRedoLinkedList;

public class UndoRedoBean implements UndoRedoLinkedList.Entry {

    private String mData = null;
    private int mIndex = 1;

    public void setData(String data) {
        mData = data;
    }

    public String getData() {
        return mData;
    }

    public int getIndex() {
        return mIndex;
    }

    @Override
    public void onDestroy() {
        mIndex = 0;
    }
}
