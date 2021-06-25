package com.hui.tally.frag_chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.hui.tally.R;
import com.hui.tally.db.BarChartItemBean;
import com.hui.tally.db.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OutcomChartFragment extends BaseChartFragment {
    int kind = 0;

    @Override
    public void onResume() {
        super.onResume();
        loadData(year,month,kind);
    }

    @Override
    protected void setAxisData(int year, int month) {
        List<IBarDataSet> sets = new ArrayList<>();
        //獲取這個月每天的支出總金額
        List<BarChartItemBean> list = DBManager.getSumMoneyOneDayInMonth(year, month, kind);
        if (list.size() == 0) {
            barChart.setVisibility(View.GONE);
            chartTv.setVisibility(View.VISIBLE);
        }else {
            barChart.setVisibility(View.VISIBLE);
            chartTv.setVisibility(View.GONE);
            //設置有多少根柱子
            List<BarEntry> barEntries1 = new ArrayList<>();
            for (int i = 0; i < 31; i++) {
                //初始化每一根柱子，添加到柱狀圖當中
                BarEntry entry = new BarEntry(i, 0.0f);
                barEntries1.add(entry);
            }
            for (int i = 0; i < list.size(); i++) {
                BarChartItemBean itemBean = list.get(i);
                int day = itemBean.getDay();  //獲取日期
                //根據天數，獲取x軸的位置
                int xIndex = day - 1;
                BarEntry barEntry = barEntries1.get(xIndex);
                barEntry.setY(itemBean.getSummoney());
            }
            BarDataSet barDataSet1 = new BarDataSet(barEntries1, "");
            barDataSet1.setValueTextColor(Color.BLACK);  //值的顏色
            barDataSet1.setValueTextSize(8f);  //值的大小
            barDataSet1.setColor(Color.RED);  //柱子的顏色

            //設置柱子上數據顯示的格式
            barDataSet1.setValueFormatter(new IValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    //此處的value默認保存一位小數
                    if (value==0) {
                        return "";
                    }
                    return value + "";
                }
            });
            sets.add(barDataSet1);

            BarData barData = new BarData(sets);
            barData.setBarWidth(0.2f);  //設置柱子的寬度
            barChart.setData(barData);
        }
    }

    @Override
    protected void setYAxis(int year, int month) {
        //獲取本月收入最高的一天為多少，將它設定為y軸的最大值
        float maxMoney = DBManager.getMaxMoneyOneDayInMonth(year, month, kind);
        float max = (float) Math.ceil(maxMoney);  //將最大金額向上取整
        //設置y軸
        YAxis yAxis_right = barChart.getAxisRight();
        yAxis_right.setAxisMaximum(max);  //設置y軸的最大值
        yAxis_right.setAxisMinimum(0f);  //設置y軸的最小值
        yAxis_right.setEnabled(false);  //不顯示右邊的y軸

        YAxis yAxis_left = barChart.getAxisLeft();
        yAxis_left.setAxisMaximum(max);
        yAxis_left.setAxisMinimum(0f);
        yAxis_left.setEnabled(false);

        //設置不顯示圖例
        Legend legend = barChart.getLegend();
        legend.setEnabled(false);
    }

    @Override
    public void setDate(int year, int month) {
        super.setDate(year, month);
        loadData(year,month,kind);
    }
}