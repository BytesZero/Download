package com.zsl.download.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.impl.SQLiteHelper;

/**
 * 数据库的操作类
 * Created by zsl on 15/5/20.
 */
public class MyDataBase {
    //数据库的名称
    private final String dbName = "download.db";
    //数据库版本
    private final int dbVersion = 1;

    //上下文
    private Context context;

    // 数据库对象
    private DataBase db;


    public static MyDataBase myDataBase;


    /**
     * 单例模式
     * @param context 上下文
     * @return MyDataBase 对象
     */
    public static MyDataBase init(Context context){
        if (myDataBase==null){
            myDataBase=new MyDataBase(context);
        }

        return myDataBase;
    }

    public MyDataBase(Context context) {
        this.context = context;
        DataBaseConfig dataBaseConfig = new DataBaseConfig(context, dbName, dbVersion, new SQLiteHelper.OnUpdateListener() {
            @Override
            public void onUpdate(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        });
        db = LiteOrm.newInstance(dataBaseConfig);
    }

    /**
     * 获得到数据库对象
     * @return 数据库对象
     */
    public DataBase getDb() {
        if (db==null){
            new MyDataBase(context);
        }
        return db;
    }
}
