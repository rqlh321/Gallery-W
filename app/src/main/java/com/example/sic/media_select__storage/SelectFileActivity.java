package com.example.sic.media_select__storage;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;

import java.net.URLConnection;
import java.util.ArrayList;

public class SelectFileActivity extends AppCompatActivity {
    public static final String URI = "uri";
    public static final String IS_VIDEO = "isVideo";

    SQLiteDatabase sdb;
    ArrayList<String> listUri;
    boolean[] selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.select_file));


        DatabaseHelper dbHelper = new DatabaseHelper(this);
        sdb = dbHelper.getReadableDatabase();
        listUri = getFilesUri();
        selectedItems = getSelectedItems(sdb, listUri);

        GridView gridView = (GridView) findViewById(R.id.list_files);
        BaseAdapter adapter = new BaseAdapter() {
            LayoutInflater inflater = (LayoutInflater) SelectFileActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            @Override
            public int getCount() {
                return listUri.size();
            }

            @Override
            public Object getItem(int i) {
                return listUri.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = inflater.inflate(R.layout.grid_view_item, viewGroup, false);
                }
                final boolean isVideoFile = isVideoFile(listUri.get(i));
                ImageView playButton = (ImageView) view.findViewById(R.id.play_button);
                if (isVideoFile) {
                    playButton.setVisibility(View.VISIBLE);
                } else {
                    playButton.setVisibility(View.GONE);
                }
                ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
                Glide.with(SelectFileActivity.this)
                        .load(listUri.get(i))
                        .fitCenter()
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SelectFileActivity.this, ViewActivity.class);
                        intent.putExtra(URI, listUri.get(i));
                        intent.putExtra(IS_VIDEO, isVideoFile);
                        startActivity(intent);
                    }
                });
                final Switch selector = (Switch) view.findViewById(R.id.selector);
                selector.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedItems[i] = !selectedItems[i];
                        selector.setChecked(selectedItems[i]);
                        if (selectedItems[i]) {
                            //add to db
                            ContentValues values = new ContentValues();
                            values.put(DatabaseHelper.URI_COLUMN, listUri.get(i));
                            sdb.insert(DatabaseHelper.DATABASE_TABLE, null, values);
                        } else {
                            //remove from db
                            sdb.delete(DatabaseHelper.DATABASE_TABLE,
                                    DatabaseHelper.URI_COLUMN + "=?" + " AND " + DatabaseHelper.STATE_COLUMN + "=?",
                                    new String[]{listUri.get(i), "0"});
                        }
                    }
                });
                selector.setChecked(selectedItems[i]);
                return view;
            }

            public boolean isVideoFile(String path) {
                String mimeType = URLConnection.guessContentTypeFromName(path);
                return mimeType != null && mimeType.indexOf("video") == 0;
            }
        };
        gridView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.file_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done: {
                for (int i = 0; i < selectedItems.length; i++) {
                    if (selectedItems[i]) {
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.URI_COLUMN, listUri.get(i));
                        values.put(DatabaseHelper.STATE_COLUMN, 1);
                        sdb.update(DatabaseHelper.DATABASE_TABLE, values, DatabaseHelper.URI_COLUMN + "=?", new String[]{listUri.get(i)});
                    }
                }
                finish();
                break;
            }
            default: {
                onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    boolean[] getSelectedItems(SQLiteDatabase sdb, ArrayList<String> listUri) {
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

    private ArrayList<String> getFilesUri() {
        ArrayList<String> list = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        String sort = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        Cursor cursor = getContentResolver().query(uri,
                projection,
                MediaStore.Images.Media.BUCKET_ID + "=?" + "AND(" + selection + ")",
                new String[]{getIntent().getExtras().getString(SelectFolderActivity.FOLDER_ID)},
                sort);

        while (cursor.moveToNext()) {
            list.add(cursor.getString((cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
        }
        cursor.close();
        return list;
    }
}
