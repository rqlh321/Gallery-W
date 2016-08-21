package com.example.sic.media_select__storage;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SelectFolderActivity extends AppCompatActivity {
    static final public String FOLDER_ID = "folderId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.select_folder));

        final ArrayList<Album> list = new ArrayList<>();
        getGalleryAlbums(list);

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.folder_preview_item, list) {
            LayoutInflater inflater = (LayoutInflater) SelectFolderActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = inflater.inflate(R.layout.folder_preview_item, parent, false);
                }
                TextView text = (TextView) view.findViewById(R.id.folder_text);
                text.setText(list.get(position).getName());
                ImageView imageView = (ImageView) view.findViewById(R.id.folder_preview_image);
                Glide.with(SelectFolderActivity.this)
                        .load(list.get(position).getCoverUri())
                        .fitCenter()
                        .into(imageView);
                return view;
            }
        };
        ListView galleryFolders = (ListView) findViewById(R.id.gallery_folders);
        galleryFolders.setAdapter(adapter);
        galleryFolders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SelectFolderActivity.this, SelectFileActivity.class);
                intent.putExtra(FOLDER_ID, list.get(i).getId());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    void getGalleryAlbums(ArrayList<Album> list) {
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
    }

    class Album {
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
