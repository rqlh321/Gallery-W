package com.example.sic.media_select__storage.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.sic.media_select__storage.Adapters.RecycleViewFolderListAdapter;
import com.example.sic.media_select__storage.R;

import java.util.ArrayList;

/**
 * Created by sic on 03.09.2016.
 */
public class SelectFolderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_folder, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.select_folder);
        setHasOptionsMenu(true);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_folders);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        RecycleViewFolderListAdapter adapter = new RecycleViewFolderListAdapter(getActivity());
        ArrayList<Album> list = getGalleryAlbums();
        adapter.refreshList(list);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
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

        Cursor cursor = getContext().getContentResolver().query(queryUri, projection, selection, null, sort);

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
