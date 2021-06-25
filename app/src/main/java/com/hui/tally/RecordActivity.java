package com.hui.tally;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.hui.tally.adapter.RecordPagerAdapter;
import com.hui.tally.frag_record.IncomeFragment;
import com.hui.tally.frag_record.BaseRecordFragment;
import com.hui.tally.frag_record.OutcomeFragment;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //1.查找控件
        tabLayout = findViewById(R.id.record_tabs);
        viewPager = findViewById(R.id.record_vp);
        //2.設置ViewPager加載頁面
        initPager();
    }

    private void initPager() {
        //初始化ViewPager頁面的集合
        List<Fragment> fragmentList = new ArrayList<>();
        //創建收入和支出頁面，放置在Fragment當中
        OutcomeFragment outFrag = new OutcomeFragment();  //支出
        IncomeFragment inFrag = new IncomeFragment();  //收入
        fragmentList.add(outFrag);
        fragmentList.add(inFrag);

        //創建適配器
        RecordPagerAdapter pagerAdapter = new RecordPagerAdapter(getSupportFragmentManager(), fragmentList);
        //設置適配器
        viewPager.setAdapter(pagerAdapter);
        //將TableLayout和ViewPager進行關聯
        tabLayout.setupWithViewPager(viewPager);
    }

    //點擊事件
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.record_iv_back:
                finish();
                break;
        }
    }
}