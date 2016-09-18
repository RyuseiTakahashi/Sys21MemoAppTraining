package com.myapp01.sys21memoapptraining;

/**
 * Created by RyuseiTakahashi on 2016/07/17.
 */

import android.app.AlertDialog;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.myapp01.sys21memoapptraining.MainActivity.NEW_ITEM_ID;
import static com.myapp01.sys21memoapptraining.MemoContract.Memos.*;
import static com.myapp01.sys21memoapptraining.MemoContentProvider.CONTENT_URI;

public class FormActivity extends AppCompatActivity {

    private long memoId;

    private EditText titleText;
    private EditText bodyText;
    private TextView updatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        titleText   = (EditText) findViewById(R.id.titleText);
        bodyText    = (EditText) findViewById(R.id.bodyText);
        updatedText = (TextView) findViewById(R.id.updatedText);

        //MainActivityから渡ってきたintentを取得
        Intent intent = getIntent();
        //MainActivityから渡ってきたintentIDを取得し、なければNEW_ITEM_IDを取得
        memoId = intent.getLongExtra(MainActivity.EXTRA_MYID, NEW_ITEM_ID);

        if (memoId == NEW_ITEM_ID) {
            // new memo
            //if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New memo");
            //}
            updatedText.setText("Updated: -------");
        } else {
            // 既存 memo
            //if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit memo");
            //}
            //content_uriにmemo_idを付加
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, memoId);
            //取得するカラム
            String[] projection = {COL_TITLE, COL_BODY, COL_UPDATED};
            //content providerへのアクセスはgetContentResolverを使う
            Cursor c = getContentResolver().query(uri, projection, _ID + " = ?", new String[]{Long.toString(memoId)}, null);
            //cursor内のデータの参照先を先頭に移動
            c.moveToFirst();
            //指定した列名が何列目にあるのかを、0から始まるインデックスを取得
            titleText.setText(c.getString(c.getColumnIndex(COL_TITLE)));
            bodyText.setText(c.getString(c.getColumnIndex(COL_BODY)));
            updatedText.setText("Updated: " + c.getString(c.getColumnIndex(COL_UPDATED)));
            c.close();
        }
    }

    /*
    メニューが表示される前に毎回実行。削除メニューの分岐表示
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        //新規memoの場合
        if (memoId == NEW_ITEM_ID) {
            deleteItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }
    /*
    メソッドの中で必要に応じてメニューボタンが押された際に表示されるメニューにメニューアイテム（メニューに表示されるそれぞれの項目）を追加
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_form, menu);
        return true;
    }

    /*
    削除ボタンが押下された時の処理
     */
    private void deleteMemo() {
        new AlertDialog.Builder(this).setTitle("Delete Memo").setMessage("メモを削除しますか？").setNegativeButton("Cancel", null)
           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
               //「android.content.DialogInterface」インターフェースをを実装したクラスのオブジェクト（イベント発生元のダイアログを判別）、クリックが発生したボタンの種類
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   //ベースとなるURIとIDから該当レコードのUriを取得
                   Uri uri = ContentUris.withAppendedId(CONTENT_URI, memoId);
                   getContentResolver().delete(uri, MemoContract.Memos._ID + " = ?", new String[]{Long.toString(memoId)});
                   finish();
               }
           }).show();
    }

    /*
    保存ボタンが押下された時の処理。
     */
    private void saveMemo() {
        String title = titleText.getText().toString().trim();
        String body  = bodyText.getText().toString().trim();
        String enterTitle = "タイトルを入力して下さい。";
        //更新した時の時間のフォーマットを指定
        String updated = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US).format(new Date());
        //エラーチェック
        if (title.isEmpty()) {
            //第三引数は表示時間
            Toast.makeText(FormActivity.this, enterTitle, Toast.LENGTH_LONG).show();
        } else {
            //テーブルに含まれるカラムをキーとし、カラムに対して設定したい値をペアとして保存
            ContentValues values = new ContentValues();
            values.put(COL_TITLE, title);
            values.put(COL_BODY, body);
            values.put(COL_UPDATED, updated);
            if (memoId == NEW_ITEM_ID) {
                // new memo
                getContentResolver().insert(CONTENT_URI, values);
            } else {
                // updated memo
                //ベースとなるURIとIDから該当レコードのUriを取得
                Uri uri = ContentUris.withAppendedId(CONTENT_URI, memoId);
                //content provider へ 更新処理を投げる
                getContentResolver().update(uri, values, _ID + " = ?", new String[]{Long.toString(memoId)});
            }
            finish();
        }
    }
    /*
    メニュー押下時のアクションを設定。
    メニューの時のIDの取得はitem.getItemId()を使用
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteMemo();
                break;
            case R.id.action_save:
                saveMemo();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
