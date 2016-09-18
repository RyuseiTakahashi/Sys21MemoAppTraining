package com.myapp01.sys21memoapptraining;

/**
 * Created by RyuseiTakahashi on 2016/07/17.
 * cursorloaderからこのクラスを呼び出す。目的はDBにアクセスするためのパイプ
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import static com.myapp01.sys21memoapptraining.MemoContract.Memos.TABLE_NAME;

public class MemoContentProvider extends ContentProvider {

    //ContentProviderに接続先
    public static final String AUTHORITY    = "com.myapp01.sys21memoapptraining.MemoContentProvider";
    //文字型のAUTHORITYをURI型に変換し、テーブルへの接続を指定
    public static final Uri    CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    // UriMatcher
    //テーブル自体にアクセスするときのuri
    private static final int MEMOS     = 1;
    //テーブルのどこの行にアクセスするときのuri
    private static final int MEMO_ITEM = 2;
    private static final UriMatcher uriMatcher;

    //クラスが呼ばれた時に1回だけ実行したい？？
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME, MEMOS);
        uriMatcher.addURI(AUTHORITY, TABLE_NAME + "/#", MEMO_ITEM);
    }

    private MemoOpenHelper memoOpenHelper;

    /*
    public MemoContentProvider() {
    }
    */

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != MEMO_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        //データベースをオープン状態にしDBの削除処理を行う
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        //削除条件を指定しおそらく削除された件数を返す。
        int deletedCount = db.delete(TABLE_NAME, selection, selectionArgs);
        //登録・更新・削除でデータが変更されたタイミングで自動で決まった処理を実行
        getContext().getContentResolver().notifyChange(uri, null);
        return deletedCount;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != MEMOS) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        long newId = db.insert(
                TABLE_NAME,
                null,
                values
        );
        Uri newUri = ContentUris.withAppendedId(
                MemoContentProvider.CONTENT_URI,
                newId
        );
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        //？？？
        memoOpenHelper = new MemoOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder
    ) {
        //不正なURIが入ってこないかの判定
        switch (uriMatcher.match(uri)) {
            case MEMOS:
            case MEMO_ITEM:
                break;
            default:
                throw new IllegalArgumentException("有効ではありません URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getReadableDatabase();
        Cursor c = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) != MEMO_ITEM) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase db = memoOpenHelper.getWritableDatabase();
        int updatedCount = db.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return updatedCount;
    }
}
