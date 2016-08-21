package com.example.sic.media_select__storage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> listUri = new ArrayList<>();
    BaseAdapter adapter;
    SQLiteDatabase sdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        sdb = dbHelper.getReadableDatabase();

        Button addButton = (Button) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectFolderActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkReadExternalPermission()) {
                        startActivity(intent);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
                    }
                } else {
                    startActivity(intent);
                }
            }
        });

        GridView gridView = (GridView) findViewById(R.id.gallery_list);
        adapter = new BaseAdapter() {
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Random random = new Random();

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
                    view = inflater.inflate(R.layout.main_grid_view_item, viewGroup, false);
                }
                final boolean isVideoFile = isVideoFile(listUri.get(i));
                ImageView playButton = (ImageView) view.findViewById(R.id.play_button);
                if (isVideoFile) {
                    playButton.setVisibility(View.VISIBLE);
                } else {
                    playButton.setVisibility(View.GONE);
                }
                ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
                Glide.with(MainActivity.this)
                        .load(listUri.get(i))
                        .fitCenter()
                        .into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                        intent.putExtra(SelectFileActivity.URI, listUri.get(i));
                        intent.putExtra(SelectFileActivity.IS_VIDEO, isVideoFile);
                        startActivity(intent);
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        sdb.delete(DatabaseHelper.DATABASE_TABLE,
                                DatabaseHelper.URI_COLUMN + "=?" + " AND " + DatabaseHelper.STATE_COLUMN + "=?",
                                new String[]{listUri.get(i), "1"});
                        listUri.remove(i);
                        adapter.notifyDataSetChanged();
                        return false;
                    }
                });
                FrameLayout layout = (FrameLayout) view.findViewById(R.id.item_layout);
                layout.setRotation(random.nextFloat() * 1000);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    Intent intent = new Intent(MainActivity.this, SelectFolderActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    ArrayList<String> getFromDb() {
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

    private boolean checkReadExternalPermission() {
        int res = this.checkCallingOrSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listUri = getFromDb();
        adapter.notifyDataSetChanged();
    }
}
