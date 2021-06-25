package com.hui.tally.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hui.tally.R;
import com.hui.tally.adapter.CalendarAdapter;
import com.hui.tally.db.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarDialog extends Dialog implements View.OnClickListener {
    ImageView errorIv;
    GridView gv;
    LinearLayout hsvLayout;

    List<TextView>hsvViewList;
    List<Integer> yearList;

    int selectPos = -1;  //表示正在被點擊的年份的位置
    private CalendarAdapter adapter;
    int selectMonth = -1;

    public interface OnRefreshListener{
        public void onRefresh(int selPos,int year,int month);
    }
    OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public CalendarDialog(@NonNull Context context, int selectPos, int selectMonth) {
        super(context);
        this.selectPos = selectPos;
        this.selectMonth = selectMonth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendar);
        gv = findViewById(R.id.dialog_calendar_gv);
        errorIv = findViewById(R.id.dialog_calendar_iv);
        hsvLayout = findViewById(R.id.dialog_calendar_layout);
        errorIv.setOnClickListener(this);
        //向橫向的ScrollView當中添加View的方法
        addViewToLayout();
        initGridView();
        //設置GridView當中每一個item的點擊事件
        setGVListener();
    }

    private void setGVListener() {
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selPos = position;
                adapter.notifyDataSetInvalidated();
                int month = position + 1;
                int year = adapter.year;
                //獲取到被選中的年份和月份
                onRefreshListener.onRefresh(selectPos,year,month);
                cancel();
            }
        });
    }

    private void initGridView() {
        int selYear = yearList.get(selectPos);
        adapter = new CalendarAdapter(getContext(), selYear);
        if (selectMonth == -1) {
            int month = Calendar.getInstance().get(Calendar.MONTH);
            adapter.selPos = month;
        }else {
            adapter.selPos = selectMonth-1;
        }
        gv.setAdapter(adapter);
    }

    private void addViewToLayout() {
        hsvViewList = new ArrayList<>();  //將添加進入線性佈局當中的TextView進行統一管理的集合
        yearList = DBManager.getYearListFromAccounttb();  //獲取數據庫當中存儲了多少個年份
        //如果數據庫當中没有紀錄，就添加今年的紀錄
        if (yearList.size() == 0) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            yearList.add(year);
        }

        //遍歷年份，有幾年，就向ScrollView當中添加幾個view
        for (int i = 0; i < yearList.size(); i++) {
            int year = yearList.get(i);
            View view = getLayoutInflater().inflate(R.layout.item_dialogcal_hsv, null);
            hsvLayout.addView(view);  //將view添加到佈局當中
            TextView hsvTv = view.findViewById(R.id.item_dialogcal_hsv_tv);
            hsvTv.setText(year+"");
            hsvViewList.add(hsvTv);
        }
        if (selectPos == -1) {
            selectPos = hsvViewList.size()-1;  //設置當前被選中的是最近的年份
        }
        changeTvbg(selectPos);  //將最后一個設置為選中狀態
        setHSVClickListener();  //設置每一個View的監聽事件
    }

    //給横向的ScrollView當中每一個TextView設置點擊事件
    private void setHSVClickListener() {
        for (int i = 0; i < hsvViewList.size(); i++) {
            TextView view = hsvViewList.get(i);
            final int pos = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTvbg(pos);
                    selectPos = pos;
                    //獲取被選中的年份，然后下面的GridView顯示數據源會發生變化
                    int year = yearList.get(selectPos);
                    adapter.setYear(year);
                }
            });
        }
    }

    //傳入被選中的位置，改變此位置上的背景和文字顏色
    private void changeTvbg(int selectPos) {
        for (int i = 0; i < hsvViewList.size(); i++) {
            TextView tv = hsvViewList.get(i);
            tv.setBackgroundResource(R.drawable.dialog_btn_bg);
            tv.setTextColor(Color.BLACK);
        }

        TextView selView = hsvViewList.get(selectPos);
        selView.setBackgroundResource(R.drawable.main_recordbtn_bg);
        selView.setTextColor(Color.WHITE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_calendar_iv:
                cancel();
                break;
        }
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
        wlp.gravity = Gravity.TOP;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
    }
}
