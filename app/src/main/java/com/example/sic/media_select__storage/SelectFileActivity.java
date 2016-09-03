package com.example.sic.media_select__storage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.example.sic.media_select__storage.Adapters.GridViewItemsPreviewAdapter;
import com.example.sic.media_select__storage.Adapters.RecycleViewFolderListAdapter;

import java.util.ArrayList;

public class SelectFileActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ArrayList<String> listUri;
    boolean[] selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.select_file));

        dbHelper = new DatabaseHelper(this);
        listUri = getFilesUri();
        selectedItems = dbHelper.getSelectedItems(listUri);

        GridView gridView = (GridView) findViewById(R.id.list_files);
        GridViewItemsPreviewAdapter adapter = new GridViewItemsPreviewAdapter(this, listUri, selectedItems);
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
                dbHelper.selectedItemsUpdate(listUri, selectedItems);
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
                new String[]{getIntent().getExtras().getString(RecycleViewFolderListAdapter.FOLDER_ID)},
                sort);

        while (cursor.moveToNext()) {
            list.add(cursor.getString((cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
        }
        cursor.close();
        return list;
    }
}
