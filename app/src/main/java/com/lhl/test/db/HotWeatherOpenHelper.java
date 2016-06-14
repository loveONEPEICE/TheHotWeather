package com.lhl.test.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/6/13 0013.
 */
public class HotWeatherOpenHelper extends SQLiteOpenHelper {
    //  创建Province表语句
    public static final String CREATE_PROVINCE = "create table Province " +
            "(_id integer primary key autoincrement" +
            ",province_name text" +
            ",province_code text)";
//    创建City表语句
    public static final String CREATE_CITY =  "create table City"+
        "(_id integer primary key autoincrement"+
        ",city_name text,city_code text"+
        ",province_id integer)";
//    创建County表语句
    public static final String CREATE_COUNTY = "create table County "+
        "(_id integer primary key autoincrement"+
        ",county_name text,county_code text"+
        ",city_id integer)";
    public HotWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建三张表，分别用于存放省、市、县的各种数据信息
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
