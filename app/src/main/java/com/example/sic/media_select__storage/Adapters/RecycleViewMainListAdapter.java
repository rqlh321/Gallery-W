package com.example.sic.media_select__storage.adapters;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sic.media_select__storage.DatabaseHelper;
import com.example.sic.media_select__storage.fragments.ViewFragment;
import com.example.sic.media_select__storage.R;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class RecycleViewMainListAdapter extends RecyclerView.Adapter<RecycleViewMainListAdapter.ViewHolder> {
    Random random = new Random();
    ArrayList<String> list = new ArrayList<>();
    FragmentActivity fragmentActivity;

    public RecycleViewMainListAdapter(FragmentActivity fragmentActivity) {
        this.fragmentActivity = fragmentActivity;
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("video") == 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_main_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final boolean isVideoFile = isVideoFile(list.get(position));
        if (isVideoFile) {
            holder.playImage.setVisibility(View.VISIBLE);
        } else {
            holder.playImage.setVisibility(View.GONE);
        }

        Glide.with(fragmentActivity)
                .load(list.get(position))
                .override(480,256)
                .animate(R.anim.image_open)
                .into(holder.itemImage);
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewFragment viewFragment = new ViewFragment();
                FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                Bundle bundle = new Bundle();
                bundle.putString(GridViewItemsPreviewAdapter.URI, list.get(position));
                bundle.putBoolean(GridViewItemsPreviewAdapter.IS_VIDEO, isVideoFile);
                viewFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.container, viewFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        holder.itemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(fragmentActivity);
                dbHelper.deleteByUri(list.get(position));
                list.remove(position);
                notifyDataSetChanged();
                return false;
            }
        });
        holder.layout.setRotation(random.nextFloat() * 1000);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshList(ArrayList<String> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView itemImage;
        public final ImageView playImage;
        public final FrameLayout layout;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            itemImage = (ImageView) view.findViewById(R.id.item_image);
            playImage = (ImageView) view.findViewById(R.id.play_button);
            layout = (FrameLayout) view.findViewById(R.id.item_layout);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}