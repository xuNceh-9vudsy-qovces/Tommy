package com.hui.tally.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.hui.tally.R;

public class RemarkDialog extends Dialog implements View.OnClickListener{
    EditText et;
    Button cancelBtn,ensureBtn;
    OnEnsureListener onEnsureListener;

    //設定回調接口的方法
    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public RemarkDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_remark);  //設置對話框顯示佈局
        et = findViewById(R.id.dialog_remark_et);
        cancelBtn = findViewById(R.id.dialog_remark_btn_cancel);
        ensureBtn = findViewById(R.id.dialog_remark_btn_ensure);
        cancelBtn.setOnClickListener(this);
        ensureBtn.setOnClickListener(this);
    }

    public interface OnEnsureListener{
        public void onEnsure();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_remark_btn_cancel:
                cancel();
                break;
            case R.id.dialog_remark_btn_ensure:
                if (onEnsureListener!=null) {
                    onEnsureListener.onEnsure();
                }
                break;
        }
    }

    //獲取輸入數據的方法
    public String getEditText(){
        return et.getText().toString().trim();
    }

    //設置Dialog的尺寸和屏幕尺寸一致
    public void setDialogSize(){
        //獲取當前窗口對象
        Window window = getWindow();
        //獲取窗口對象的参数
        WindowManager.LayoutParams wlp = window.getAttributes();
        //獲取屏幕寬度
        Display d = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int)(d.getWidth());  //對話框窗口為屏幕窗口
        wlp.gravity = Gravity.BOTTOM;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
        handler.sendEmptyMessageDelayed(1,100);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            //自動彈出鍵盤的方法
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };
}
