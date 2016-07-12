package com.stewhouse.nproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gomguk on 16. 7. 12..
 */
public class NSQLiteOpenHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NProjectDB.db";

    private static final String SQL_QUERY_CREATE_TABLE = "CREATE TABLE KEYWORDS ( ID INTEGER PRIMARY KEY AUTO_INCREMENT, KEYWORD VARCHAR(255) NOT NULL, TIMESTAMP TIMESTAMP NOT NULL );";
    private static final String SQL_QUERY_DROP_TABLE = "DROP TABLE IF EXISTS KEYWORDS";

    private static Context mContext = null;

    public NSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over.
        db.execSQL(SQL_QUERY_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void insertKeywordData(String keyword) {
        NSQLiteOpenHelper mDbHelper = new NSQLiteOpenHelper(mContext);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String queryStr = "insert into keywords values ( " + keyword + ", CURRENT_TIMESTAMP );";

        db.execSQL(queryStr);
    }
}
