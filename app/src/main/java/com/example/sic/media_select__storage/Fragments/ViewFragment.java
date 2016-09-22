package com.example.sic.media_select__storage.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.sic.media_select__storage.adapters.GridViewItemsPreviewAdapter;
import com.example.sic.media_select__storage.R;

/**
 * Created by sic on 03.09.2016.
 */
public class ViewFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view, container, false);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.preview);
        setHasOptionsMenu(true);

        String uri = getArguments().getString(GridViewItemsPreviewAdapter.URI);
        boolean isVideoFile = getArguments().getBoolean(GridViewItemsPreviewAdapter.IS_VIDEO, false);
        if (isVideoFile) {
            VideoView videoView = (VideoView) view.findViewById(R.id.video);
            videoView.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(uri));
            videoView.start();
        } else {
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageURI(Uri.parse(uri));
        }
        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);
    }

}
