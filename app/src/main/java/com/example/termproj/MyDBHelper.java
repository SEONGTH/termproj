package com.example.termproj;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context){
        super(context, "myDiary.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "create table myDairy(diaryDate char(10) primary key, content varchar(500));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db , int oldVersion , int newVersion) {

    }
}