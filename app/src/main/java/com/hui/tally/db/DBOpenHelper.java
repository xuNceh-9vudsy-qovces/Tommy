package com.hui.tally.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.hui.tally.R;

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(@Nullable Context context) {
        super(context,"tally.db" , null, 1);
    }

    //創建數據庫的方法，只有項目第一次運行時會被調用
    @Override
    public void onCreate(SQLiteDatabase db) {
    //創建表示類型的表
        String sql = "create table typetb(id integer primary key autoincrement, typename varchar(10), imageId integer, sImageId integer, kind integer)";
        db.execSQL(sql);
        insertType(db);
        //創建記帳表
        sql = "create table accounttb(id integer primary key autoincrement,typename varchar(10),sImageId integer,remark varchar(80),money float," +
                "time varchar(60),year integer,month integer,day integer,kind integer)";
        db.execSQL(sql);
    }

    private void insertType(SQLiteDatabase db) {
    //向typetb表當中插入元素
        String sql = "insert into typetb (typename, imageId, sImageId, kind) values (?,?,?,?)";
        db.execSQL(sql,new Object[]{"其他", R.mipmap.ic_more,R.mipmap.ic_more_fs,0});
        db.execSQL(sql,new Object[]{"餐飲", R.mipmap.ic_food,R.mipmap.ic_food_fs,0});
        db.execSQL(sql,new Object[]{"交通", R.mipmap.ic_traffic,R.mipmap.ic_traffic_fs,0});
        db.execSQL(sql,new Object[]{"購物", R.mipmap.ic_shopping,R.mipmap.ic_shopping_fs,0});
        db.execSQL(sql,new Object[]{"服飾", R.mipmap.ic_clothes,R.mipmap.ic_clothes_fs,0});
        db.execSQL(sql,new Object[]{"日用品", R.mipmap.ic_necessary,R.mipmap.ic_necessary_fs,0});
        db.execSQL(sql,new Object[]{"娛樂", R.mipmap.ic_entertainment,R.mipmap.ic_entertainment_fs,0});
        db.execSQL(sql,new Object[]{"零食", R.mipmap.ic_snacks,R.mipmap.ic_snacks_fs,0});
        db.execSQL(sql,new Object[]{"飲料", R.mipmap.ic_drink,R.mipmap.ic_drink_fs,0});
        db.execSQL(sql,new Object[]{"學習", R.mipmap.ic_learn,R.mipmap.ic_learn_fs,0});
        db.execSQL(sql,new Object[]{"醫療", R.mipmap.ic_medical,R.mipmap.ic_medical_fs,0});
        db.execSQL(sql,new Object[]{"住宅", R.mipmap.ic_house,R.mipmap.ic_house_fs,0});
        db.execSQL(sql,new Object[]{"水電費", R.mipmap.ic_hydropower,R.mipmap.ic_hydropower_fs,0});
        db.execSQL(sql,new Object[]{"通訊", R.mipmap.ic_communication,R.mipmap.ic_communication_fs,0});
        db.execSQL(sql,new Object[]{"人情往来", R.mipmap.ic_help,R.mipmap.ic_help_fs,0});

        db.execSQL(sql,new Object[]{"其他", R.mipmap.in_more,R.mipmap.in_more_fs,1});
        db.execSQL(sql,new Object[]{"薪資", R.mipmap.in_salary,R.mipmap.in_salary_fs,1});
        db.execSQL(sql,new Object[]{"獎金", R.mipmap.in_bonus,R.mipmap.in_bonus_fs,1});
        db.execSQL(sql,new Object[]{"借入", R.mipmap.in_borrow,R.mipmap.in_borrow_fs,1});
        db.execSQL(sql,new Object[]{"收債", R.mipmap.in_debtcollection,R.mipmap.in_debtcollection_fs,1});
        db.execSQL(sql,new Object[]{"利息收入", R.mipmap.in_interestincome,R.mipmap.in_interestincome_fs,1});
        db.execSQL(sql,new Object[]{"投資回報", R.mipmap.in_investment,R.mipmap.in_investment_fs,1});
        db.execSQL(sql,new Object[]{"二手交易", R.mipmap.in_secondhand,R.mipmap.in_secondhand_fs,1});
        db.execSQL(sql,new Object[]{"意外所得", R.mipmap.in_windfall,R.mipmap.in_windfall_fs,1});
    }

    //數據庫版本在更新時發生改變，會調用此方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
