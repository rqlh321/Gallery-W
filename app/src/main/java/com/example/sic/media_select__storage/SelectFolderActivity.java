package com.example.sic.media_select__storage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.sic.media_select__storage.Adapters.RecycleViewFolderListAdapter;

import java.util.ArrayList;

public class SelectFolderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.select_folder));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.gallery_folders);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RecycleViewFolderListAdapter adapter = new RecycleViewFolderListAdapter(this);
        ArrayList<Album> list = getGalleryAlbums();
        adapter.refreshList(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    ArrayList<Album> getGalleryAlbums() {
        ArrayList<Album> list = new ArrayList<>();
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projection = new String[]{MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        String sort = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        Cursor cursor = getContentResolver().query(queryUri, projection, selection, null, sort);

        ArrayList<String> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            Album album = new Album(cursor.getString((cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID))),
                    cursor.getString((cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME))),
                    cursor.getString((cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)))
            );
            if (!ids.contains(album.getId())) {
                list.add(album);
                ids.add(album.getId());
            }
        }
        cursor.close();
        return list;
    }

    public class Album {
        private String id;
        private String name;
        private String coverUri;

        Album(String id, String name, String coverUri) {
            this.id = id;
            this.name = name;
            this.coverUri = coverUri;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCoverUri() {
            return coverUri;
        }
    }
}
