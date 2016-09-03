package com.example.sic.media_select__storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

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
    SQLiteDatabase sdb = getReadableDatabase();

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

    public void deleteByUri(String uri) {
        sdb.delete(DatabaseHelper.DATABASE_TABLE,
                DatabaseHelper.URI_COLUMN + "=?" + " AND " + DatabaseHelper.STATE_COLUMN + "=?",
                new String[]{uri, "1"});
    }

    public ArrayList<String> getUriList() {
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE,
                new String[]{DatabaseHelper.URI_COLUMN}, DatabaseHelper.STATE_COLUMN + "=?", new String[]{"1"}, null, null, null);
        cursor.getCount();
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.URI_COLUMN));
            list.add(content);
        }
        cursor.close();
        return list;
    }

    public void selectedItemsUpdate(ArrayList<String> listUri, boolean[] selectedItems) {
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i]) {
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.URI_COLUMN, listUri.get(i));
                values.put(DatabaseHelper.STATE_COLUMN, 1);
                sdb.update(DatabaseHelper.DATABASE_TABLE, values, DatabaseHelper.URI_COLUMN + "=?", new String[]{listUri.get(i)});
            }
        }
    }

    public boolean[] getSelectedItems(ArrayList<String> listUri) {
        boolean[] selectedItems = new boolean[listUri.size()];
        Cursor cursor = sdb.query(DatabaseHelper.DATABASE_TABLE,
                new String[]{DatabaseHelper.URI_COLUMN}, DatabaseHelper.STATE_COLUMN + "=?", new String[]{"0"}, null, null, null);
        cursor.getCount();
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.URI_COLUMN));
            if (listUri.indexOf(content) != -1) {
                selectedItems[listUri.indexOf(content)] = true;
            }
        }
        cursor.close();
        return selectedItems;
    }

    public void addToDataBase(String uri) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.URI_COLUMN, uri);
        sdb.insert(DatabaseHelper.DATABASE_TABLE, null, values);
    }

    public void removeFromDataBase(String uri) {
        sdb.delete(DatabaseHelper.DATABASE_TABLE,
                DatabaseHelper.URI_COLUMN + "=?" + " AND " + DatabaseHelper.STATE_COLUMN + "=?",
                new String[]{uri, "0"});
    }
}
