package com.hui.tally;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hui.tally.adapter.AccountAdapter;
import com.hui.tally.db.AccountBean;
import com.hui.tally.db.DBManager;
import com.hui.tally.utils.BudgetDialog;
import com.hui.tally.utils.MoreDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView todayLv;  //展示今日收支情況的ListView
    ImageView searchIv;
    Button editBtn;
    ImageButton moreBtn;
    //聲明數據源
    List<AccountBean> mDatas;
    private AccountAdapter adapter;
    int year,month,day;
    //頭佈局相關控件
    View headerView;
    TextView topOutTv,topInTv,topbudgetTv,topConTv;
    ImageView topShowIv;
    SharedPreferences preferences;
    private MoreDialog moreDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTime();
        initView();
        preferences = getSharedPreferences("budget", Context.MODE_PRIVATE);
        //添加ListView的頭佈局
        addLVHeaderView();
        mDatas = new ArrayList<>();
        //設置適配器：加載每一行數據到列表當中
        adapter = new AccountAdapter(this, mDatas);
        todayLv.setAdapter(adapter);
    }

    //初始化自帶的View的方法
    private void initView() {
        todayLv = findViewById(R.id.main_lv);
        editBtn = findViewById(R.id.main_btn_edit);
        moreBtn = findViewById(R.id.main_btn_more);
        searchIv = findViewById(R.id.main_iv_search);
        editBtn.setOnClickListener(this);
        moreBtn.setOnClickListener(this);
        searchIv.setOnClickListener(this);
        setLVLongClickListener();
    }

    //設置ListView的長按事件
    private void setLVLongClickListener() {
        todayLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //點擊了頭佈局
                if (position == 0) {
                    return false;
                }
                int pos = position-1;
                AccountBean clickBean = mDatas.get(pos);  //獲取正在被點擊的這條信息

                //彈出提示用户是否刪除的對話框
                showDeleteItemDialog(clickBean);
                return false;
            }
        });
    }

    //彈出是否刪除某一條紀錄的對話框
    private void showDeleteItemDialog(final AccountBean clickBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您確定要刪除這條紀錄嗎？")
                .setNegativeButton("取消",null)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int click_id = clickBean.getId();
                        //執行刪除的操作
                        DBManager.deleteItemFromAccounttbById(click_id);
                        mDatas.remove(clickBean);  //實時刷新，移除集合當中的對象
                        adapter.notifyDataSetChanged();  //提示適配器更新數據
                        setTopTvShow();  //改變頭佈局TextView顯示的内容
                    }
                });
        builder.create().show();  //顯示對話框
    }

    private void addLVHeaderView() {
        //將佈局轉換成View對象
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        //查找頭佈局可用控件
        topOutTv = headerView.findViewById(R.id.item_mainlv_top_tv_out);
        topInTv = headerView.findViewById(R.id.item_mainlv_top_tv_in);
        topbudgetTv = headerView.findViewById(R.id.item_mainlv_top_tv_budget);
        topConTv = headerView.findViewById(R.id.item_mainlv_top_tv_day);
        topShowIv = headerView.findViewById(R.id.item_mainlv_top_iv_hide);

        topbudgetTv.setOnClickListener(this);
        headerView.setOnClickListener(this);
        topShowIv.setOnClickListener(this);
    }

    //獲取今日的具體時間
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    //當activity獲取焦點時，會調用的方法
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();
    }

    //設置頭佈局當中文本内容的顯示
    private void setTopTvShow() {
        //獲取今日支出和收入總金額，顯示在view當中
        float incomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 1);
        float outcomeOneDay = DBManager.getSumMoneyOneDay(year, month, day, 0);
        String infoOneDay = "今日支出 $"+outcomeOneDay+"  收入 $"+incomeOneDay;
        topConTv.setText(infoOneDay);
        //獲取本月收入和支出總金額
        float incomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
        topInTv.setText("$"+incomeOneMonth);
        topOutTv.setText("$"+outcomeOneMonth);
        //設置顯示預算剩餘
        float bmoney = preferences.getFloat("bmoney", 0);  //預算
        if (bmoney == 0) {
            topbudgetTv.setText("$ 0");
        }else {
            float syMoney = bmoney - outcomeOneMonth;
            topbudgetTv.setText("$"+syMoney);
        }
    }

    private void loadDBData() {
        List<AccountBean> list = DBManager.getAccountListOneDayFromAccounttb(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_iv_search:
                Intent it = new Intent(this, SearchActivity.class);  //跳轉界面
                startActivity(it);
                break;
            case R.id.main_btn_edit:
                Intent it1 = new Intent(this, RecordActivity.class);  //跳轉界面
                startActivity(it1);
                break;
            case R.id.main_btn_more:
                MoreDialog moreDialog = new MoreDialog(this);
                moreDialog.show();
                moreDialog.setDialogSize();
                break;
            case R.id.item_mainlv_top_tv_budget:
                showBudgetDialog();
                break;
            case R.id.item_mainlv_top_iv_hide:
                //切換TextView明文和密文
                toggleShow();
                break;
        }
        if (v == headerView) {
            //頭佈局被點擊
            Intent intent = new Intent();
            intent.setClass(this, MonthChartActivity.class);
            startActivity(intent);
        }
    }

    //顯示預算設置對話框
    private void showBudgetDialog() {
        BudgetDialog dialog = new BudgetDialog(this);
        dialog.show();
        dialog.setDialogSize();
        dialog.setOnEnsureListener(new BudgetDialog.OnEnsureListener() {
            @Override
            public void onEnsure(float money) {
                //將預算金額寫入到共享參數當中，進行存儲
                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("bmoney",money);
                editor.commit();
                //計算剩餘金額
                float outcomeOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
                float syMoney = money - outcomeOneMonth;  //預算剩餘 = 預算-支出
                topbudgetTv.setText("$"+syMoney);

                
            }
        });
    }

    boolean isShow = true;
    /*
      點擊頭佈局眼睛時，如果原来是明文，就加密；如果是密文，就顯示出来
    */
    private void toggleShow() {
        //明文 ----> 密文
        if (isShow) {
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            topInTv.setTransformationMethod(passwordMethod);  //設置隱藏
            topOutTv.setTransformationMethod(passwordMethod);  //設置隱藏
            topbudgetTv.setTransformationMethod(passwordMethod);  //設置隱藏
            topShowIv.setImageResource(R.mipmap.ih_hide);
            isShow = false;  //設置標志位為隱藏狀態
        }
        //密文 ----> 明文
        else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            topInTv.setTransformationMethod(hideMethod);  //設置隱藏
            topOutTv.setTransformationMethod(hideMethod);  //設置隱藏
            topbudgetTv.setTransformationMethod(hideMethod);  //設置隱藏
            topShowIv.setImageResource(R.mipmap.ih_show);
            isShow = true;  //設置標志位為隱藏狀態
        }
    }
}