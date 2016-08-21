package com.example.sic.media_select__storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATABASE_TABLE = "gallery";
    public static final String URI_COLUMN = "uri";
    public static final String STATE_COLUMN = "state";
    private static final String DATABASE_NAME = "localTaxi.db";
    private static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + "(" + BaseColumns._ID + " integer primary key autoincrement, "
            + STATE_COLUMN + " integer default 0, "
            + URI_COLUMN + " text not null);";

    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
