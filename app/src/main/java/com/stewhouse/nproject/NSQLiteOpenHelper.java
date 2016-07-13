package com.stewhouse.nproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Gomguk on 16. 7. 13..
 */
public class NSQLiteOpenHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NDB.db";

    private static final String QUERY_CREATE_TABLE = "create table keywords ( id integer primary key autoincrement, keyword varchar(255) not null, timestamp timestamp not null )";

    public NSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERY_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is to simply to discard the data and start over.
        db.execSQL(QUERY_CREATE_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Queries.
    public void insertKeyword(SQLiteDatabase db, String keyword) {
        String query = "insert into keywords ( keyword, timestamp ) values ( '" + keyword + "', current_timestamp)";

        // If the keyword data is duplicated, update these timestamp.
        if (isKeywordExists(db, keyword)) {
            updateKeyword(db, keyword);

            return;
        }

        if (getKeywordsCount(db) > NConstants.SEARCH_KEYWORD_LIMIT - 1) {
            deleteRecentKeyword(db);
        }
        db.execSQL(query);
    }

    public ArrayList<String> getKeywords(SQLiteDatabase db) {
        String query = "select * from keywords order by timestamp desc";
        ArrayList<String> row = new ArrayList<>();

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            row.add(cursor.getString(1));
        }

        return row;
    }

    public int getKeywordsCount(SQLiteDatabase db) {
        String query = "select count(*) from keywords";
        int count = -1;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToNext()) {
            count = Integer.parseInt(cursor.getString(0));
        }

        return count;
    }

    public boolean isKeywordExists(SQLiteDatabase db, String keyword) {
        String query = "select count(*) from keywords where keyword = '" + keyword + "'";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToNext()) {
            int count = Integer.parseInt(cursor.getString(0));
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    public void deleteKeyword(SQLiteDatabase db, String keyword) {
        String query = "delete from keywords where keyword = '" + keyword + "'";

        db.execSQL(query);
    }

    public void deleteRecentKeyword(SQLiteDatabase db) {
        String query = "delete from keywords where timestamp in (select timestamp from keywords order by timestamp asc limit 1)";

        db.execSQL(query);
    }

    public void updateKeyword(SQLiteDatabase db, String keyword) {
        String query = "update keywords set timestamp = current_timestamp where keyword = '" + keyword + "'";

        db.execSQL(query);
    }
}