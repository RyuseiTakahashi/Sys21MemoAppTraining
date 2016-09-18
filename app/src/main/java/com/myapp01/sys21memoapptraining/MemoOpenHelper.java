package com.myapp01.sys21memoapptraining;

/**
 * Created by RyuseiTakahashi on 2016/07/17.
 * DB定義
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "myapp.db";
    public static final int DB_VERSION = 1;

    public static final String CREATE_TABLE =
            "create table memos (" +
                    "_id integer primary key autoincrement, " +
                    "title text, " +
                    "body text, " +
                    "created datetime default current_timestamp, " +
                    "updated datetime default current_timestamp)";

    //テーブル作成時にテストデータをinsert
    public static final String INIT_TABLE =
            "insert into memos (title, body) values " +
                    "('t1', 'b1'), " +
                    "('t2', 'b2'), " +
                    "('t3', 'b3')";

    public static final String DROP_TABLE =
            "drop table if exists " + MemoContract.Memos.TABLE_NAME;


    //context〜アプリ全体の状態を持っていて、何から起動されたかどういう状態か、何にアクセスしようとしているか、といった情報を受け渡すための情報
    //context、DB名、QLiteDatabase.CursorFactroy型、DB Ver
    public MemoOpenHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
