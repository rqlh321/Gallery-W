package com.example.sic.media_select__storage.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.sic.media_select__storage.adapters.GridViewItemsPreviewAdapter;
import com.example.sic.media_select__storage.adapters.RecycleViewFolderListAdapter;
import com.example.sic.media_select__storage.DatabaseHelper;
import com.example.sic.media_select__storage.R;

import java.util.ArrayList;

/**
 * Created by sic on 03.09.2016.
 */
public class SelectFileFragment extends Fragment {
    DatabaseHelper dbHelper;
    ArrayList<String> listUri;
    boolean[] selectedItems;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_file, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.select_file);
        setHasOptionsMenu(true);

        dbHelper = new DatabaseHelper(getContext());
        listUri = getFilesUri();
        selectedItems = dbHelper.getSelectedItems(listUri);

        GridView gridView = (GridView) view.findViewById(R.id.list_files);
        GridViewItemsPreviewAdapter adapter = new GridViewItemsPreviewAdapter(getActivity(), listUri, selectedItems);
        gridView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.file_select, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done: {
                dbHelper.selectedItemsUpdate(listUri, selectedItems);
                break;
            }
        }
        getActivity().onBackPressed();
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
        Cursor cursor = getContext().getContentResolver().query(uri,
                projection,
                MediaStore.Images.Media.BUCKET_ID + "=?" + "AND(" + selection + ")",
                new String[]{this.getArguments().getString(RecycleViewFolderListAdapter.FOLDER_ID)},
                sort);

        while (cursor.moveToNext()) {
            list.add(cursor.getString((cursor.getColumnIndex(MediaStore.Images.Media.DATA))));
        }
        cursor.close();
        return list;
    }
}
