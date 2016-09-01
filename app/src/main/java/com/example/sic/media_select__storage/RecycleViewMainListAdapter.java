package com.example.sic.media_select__storage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class RecycleViewMainListAdapter extends RecyclerView.Adapter<RecycleViewMainListAdapter.ViewHolder> {
    Random random = new Random();
    ArrayList<String> list = new ArrayList<>();
    Context context;

    public RecycleViewMainListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_grid_view_item, parent, false);
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

        Glide.with(context)
                .load(list.get(position))
                .fitCenter()
                .into(holder.itemImage);
        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra(SelectFileActivity.URI, list.get(position));
                intent.putExtra(SelectFileActivity.IS_VIDEO, isVideoFile);
                context.startActivity(intent);
            }
        });
        holder.itemImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(context);
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

    public boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.indexOf("video") == 0;
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