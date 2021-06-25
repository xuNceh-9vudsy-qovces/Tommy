package com.hui.tally.frag_chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.hui.tally.R;
import com.hui.tally.adapter.ChartItemAdapter;
import com.hui.tally.db.ChartItemBean;
import com.hui.tally.db.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
abstract public class BaseChartFragment extends Fragment {
    ListView chartLv;
    int year;
    int month;
    List<ChartItemBean>mDatas;  //數據源
    private ChartItemAdapter itemAdapter;
    BarChart barChart;  //代表柱狀圖的控件
    TextView chartTv;  //如果没有收支情況，顯示的TextView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_incom_chart, container, false);
        chartLv = view.findViewById(R.id.frag_chart_lv);
        //獲取Activity傳遞的數據
        Bundle bundle = getArguments();
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        //設置數據源
        mDatas = new ArrayList<>();
        //設置適配器
        itemAdapter = new ChartItemAdapter(getContext(), mDatas);
        chartLv.setAdapter(itemAdapter);
        chartLv.setAdapter(itemAdapter);
        //添加頭佈局
        addLVHeaderView();
        return view;
    }

    protected void addLVHeaderView() {
        //將佈局轉換成View對象
        View headerView = getLayoutInflater().inflate(R.layout.item_chartfrag_top,null);
        //將View添加到ListView的頭佈局上
        chartLv.addHeaderView(headerView);
        //查找頭佈局當中包含的控件
        barChart = headerView.findViewById(R.id.item_chartfrag_chart);
        chartTv = headerView.findViewById(R.id.item_chartfrag_top_tv);
        //設定柱狀圖不顯示描述
        barChart.getDescription().setEnabled(false);
        //設置柱狀圖的内邊距
        barChart.setExtraOffsets(20, 20, 20, 20);
        setAxis(year,month);  //設置座標軸
        //設置坐標軸顯示的數據
        setAxisData(year,month);
    }

    //設置坐標軸顯示的數據
    protected abstract void setAxisData(int year, int month);

    //設置柱狀圖坐標軸的顯示
    protected void setAxis(int year, int month) {
        //設置X軸
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //設置x軸顯示在下方
        xAxis.setDrawGridLines(true);  //設置繪製該軸的網格線
        //設置x軸標籤的個數
        xAxis.setLabelCount(31);
        xAxis.setTextSize(12f);  //x軸標籤的大小
        //設置X軸顯示的值的格式
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int val = (int) value;
                if (val == 0) {
                    return month+"-1";
                }
                if (val==14) {
                    return month+"-15";
                }
                //根據不同的月份，顯示最後一天的位置
                if (month==2) {
                    if (val == 27) {
                        return month+"-28";
                    }
                }else if(month==1||month==3||month==5||month==7||month==8||month==10||month==12) {
                    if (val == 30) {
                        return month+"-31";
                    }
                }else if(month==4||month==6||month==9||month==11) {
                    if (val==29) {
                        return month+"-30";
                    }
                }
                return "";
            }
        });
        xAxis.setYOffset(10);  //設置標籤對x軸的偏移量，垂直方向

        //y軸在子類的設置
        setYAxis(year,month);
    }

    //設置y軸，因為最高的坐標不確定，所以在子類當中設置
    protected abstract void setYAxis(int year, int month);

    public void setDate(int year,int month) {
        this.year = year;
        this.month = month;
        barChart.clear();
        barChart.invalidate();  //重新繪製柱狀圖
        setAxis(year,month);
        setAxisData(year,month);
    }

    public void loadData(int year, int month, int kind) {
        List<ChartItemBean> list = DBManager.getChartListFromAccounttb(year, month, kind);
        mDatas.clear();
        mDatas.addAll(list);
        itemAdapter.notifyDataSetChanged();
    }
}