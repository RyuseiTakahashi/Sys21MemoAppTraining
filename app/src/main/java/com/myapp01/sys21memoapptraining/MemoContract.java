package com.myapp01.sys21memoapptraining;

/**
 * Created by RyuseiTakahashi on 2016/07/17.
 */

import android.provider.BaseColumns;
public final class MemoContract {

    public MemoContract() {
    }

    public static abstract class Memos implements BaseColumns {
        public static final String TABLE_NAME = "memos";
        public static final String COL_TITLE = "title";
        public static final String COL_BODY = "body";
        public static final String COL_CREATE = "created";
        public static final String COL_UPDATED = "updated";
    }
}
