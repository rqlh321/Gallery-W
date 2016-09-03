package com.example.sic.media_select__storage;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.sic.media_select__storage.Adapters.GridViewItemsPreviewAdapter;

public class ViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.preview));

        String uri = getIntent().getStringExtra(GridViewItemsPreviewAdapter.URI);
        boolean isVideoFile = getIntent().getBooleanExtra(GridViewItemsPreviewAdapter.IS_VIDEO, false);
        if (isVideoFile) {
            VideoView videoView = (VideoView) findViewById(R.id.video);
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(uri));
            videoView.start();
        } else {
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(uri));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
