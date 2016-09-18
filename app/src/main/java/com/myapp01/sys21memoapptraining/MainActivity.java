package com.myapp01.sys21memoapptraining;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //ListViewにデータを渡すためのadapter。DBのカーソルを渡すことで、直接DBのデータを表示できる。
    private SimpleCursorAdapter adapter;

    public final static String EXTRA_MYID  = "com.myapp01.sys21memoapptraining.MYID";
    public final static Long   NEW_ITEM_ID = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ListViewに出力させるカラムを指定
        String[] from = {
            MemoContract.Memos.COL_TITLE,
            MemoContract.Memos.COL_UPDATED
        };

        //レイアウトに配置してあるTextViewに値を格納
        int[] to = {
            android.R.id.text1,
            android.R.id.text2
        };

        //context, 行レイアウト,SQLiteのカーソル,表示させるカラム,バインド先のID,アダプタの動作を決めるFLG
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, from, to, 0);

        ListView myListView = (ListView) findViewById(R.id.myListView);

        //ListViewに表示させる値を引数に指定。
        myListView.setAdapter(adapter);

        /*
        ListViewをクリックした時の処理
        無名クラスを引数に指定しクリック処理を記述
         */
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //イベントが発生したListView、選択されたリスト、選択されたリスト場所、選択されたリストID
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //どこのActivityからどこのActivityへ遷移するか指定。
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                //遷移先にクリックしたIDを格納
                intent.putExtra(EXTRA_MYID, id);
                startActivity(intent);
            }
        });
        //Loaderを初期化
        //ID, 起動オプション, ローダーの実装場所
        getLoaderManager().initLoader(0, null, this);
    }

    //オプションメニューを作成
    //オプションメニューが1度だけ呼ばれるときに1度だけ実行
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu生成
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //メニュー項目押下時の処理
    //新規ボタン（item）参照が引数で渡ってくる
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //新規登録の場合
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra(EXTRA_MYID, NEW_ITEM_ID);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Loaderを初期化した時に実行される
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //カラム指定
        String[] projection = {
                MemoContract.Memos._ID,
                MemoContract.Memos.COL_TITLE,
                MemoContract.Memos.COL_UPDATED
        };
        //context, content_uri, フィールド, group by ,having, order by
        return new CursorLoader(
                this,
                MemoContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                MemoContract.Memos.COL_UPDATED + " DESC"
        );
    }

    /*
    content providerからデータが帰ってきた時に行う処理
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //帰ってきたデータでアダプタを更新
        adapter.swapCursor(data);
    }
    /*
    loaderがリセットされた時に行う処理。
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}