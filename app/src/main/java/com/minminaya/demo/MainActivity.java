package com.minminaya.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.minminaya.demo.bean.UndoRedoBean;
import com.minminaya.demo.syntest.UndoRedoLinkedList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private EditText mInputEt;
    private Button mLeftBtn;
    private Button mPutBtn;
    private Button mRightBtn;
    private Button mDeleteBtn;
    private TextView mStateTv;
    private TextView mShowData;
    private UndoRedoLinkedList<UndoRedoBean> mUndoRedoLinkedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mLeftBtn = findViewById(R.id.btn_left);
        mPutBtn = findViewById(R.id.btn_put);
        mRightBtn = findViewById(R.id.btn_right);
        mInputEt = findViewById(R.id.et_input);
        mStateTv = findViewById(R.id.tv_state);
        mDeleteBtn = findViewById(R.id.btn_delete_all);
        mShowData = findViewById(R.id.btn_show_data);
        mLeftBtn.setOnClickListener(this);
        mPutBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mDeleteBtn.setOnClickListener(this);
        mShowData.setOnClickListener(this);
        mUndoRedoLinkedList = new UndoRedoLinkedList<>(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left: {
//                UndoRedoBean undoRedoBean = mUndoRedoLinkedList.undo();
//                if (undoRedoBean != null) {
//                    String str = undoRedoBean.getData();
//                    mStateTv.setText(str);
//                }


                //多线程测试
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mUndoRedoLinkedList.undo();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mUndoRedoLinkedList.undo();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mUndoRedoLinkedList.undo();
                        }
                    }
                }).start();


                break;
            }
            case R.id.btn_right: {
//                UndoRedoBean undoRedoBean = mUndoRedoLinkedList.redo();
//                if (undoRedoBean != null) {
//                    String str = undoRedoBean.getData();
//                    mStateTv.setText(str);
//                }

                //多线程测试
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mUndoRedoLinkedList.redo();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mUndoRedoLinkedList.redo();
                        }
                    }
                }).start();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            mUndoRedoLinkedList.redo();
                        }
                    }
                }).start();

                break;
            }
            case R.id.btn_put: {
                //多线程测试
                final String str = mInputEt.getText().toString();
                mStateTv.setText(str);
                final UndoRedoBean undoRedoBean = new UndoRedoBean();
                undoRedoBean.setData(str);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            undoRedoBean.setData(str);
                            mUndoRedoLinkedList.put(undoRedoBean);
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            undoRedoBean.setData(str);
                            mUndoRedoLinkedList.put(undoRedoBean);
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            undoRedoBean.setData(str);
                            mUndoRedoLinkedList.put(undoRedoBean);
                        }
                    }
                }).start();

//                String str = mInputEt.getText().toString();
//                mStateTv.setText(str);
//                UndoRedoBean undoRedoBean = new UndoRedoBean();
//                undoRedoBean.setData(str);
//                mUndoRedoLinkedList.put(undoRedoBean);
                break;
            }
            case R.id.btn_delete_all: {
                mUndoRedoLinkedList.removeAll();
                break;
            }
            case R.id.btn_show_data: {
                mUndoRedoLinkedList.showData();
                break;
            }
        }
    }
}
