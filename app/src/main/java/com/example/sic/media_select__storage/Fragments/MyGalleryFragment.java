package com.example.sic.media_select__storage.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sic.media_select__storage.adapters.RecycleViewMainListAdapter;
import com.example.sic.media_select__storage.DatabaseHelper;
import com.example.sic.media_select__storage.MainActivity;
import com.example.sic.media_select__storage.R;

/**
 * Created by sic on 03.09.2016.
 */
public class MyGalleryFragment extends Fragment {
    RecycleViewMainListAdapter adapter;
    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_galery, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        dbHelper = new DatabaseHelper(getContext());

        Button addButton = (Button) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkReadExternalPermission()) {
                        startSelectFolderFragment();
                    } else {
                        getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivity.READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
                    }
                } else {
                    startSelectFolderFragment();
                }
            }
        });

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.gallery_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecycleViewMainListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshList(dbHelper.getUriList());
    }

    private boolean checkReadExternalPermission() {
        int res = getContext().checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void startSelectFolderFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                .replace(R.id.container, new SelectFolderFragment())
                .addToBackStack(null)
                .commit();
    }

}
