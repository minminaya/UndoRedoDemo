package com.minminaya.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private EditText mInputEt;
    private Button mLeftBtn;
    private Button mPutBtn;
    private Button mRightBtn;
    private TextView mStateTv;
    private UndoRedoLinkList<String> mUndoRedoLinkList;

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
        mLeftBtn.setOnClickListener(this);
        mPutBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);
        mUndoRedoLinkList = new UndoRedoLinkList<>(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left: {
                String str = mUndoRedoLinkList.undo();
                mStateTv.setText(str);
                break;
            }
            case R.id.btn_right: {
                String str = mUndoRedoLinkList.redo();
                mStateTv.setText(str);
                break;
            }
            case R.id.btn_put: {
                final String str = mInputEt.getText().toString();
                mStateTv.setText(str);
                mUndoRedoLinkList.put(str);
                Log.d(TAG, "btn_put: ");
                break;
            }
        }
    }
}
