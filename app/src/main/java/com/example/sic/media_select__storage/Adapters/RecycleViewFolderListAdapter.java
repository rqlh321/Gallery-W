package com.example.sic.media_select__storage.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sic.media_select__storage.R;
import com.example.sic.media_select__storage.SelectFileActivity;
import com.example.sic.media_select__storage.SelectFolderActivity;

import java.util.ArrayList;

public class RecycleViewFolderListAdapter extends RecyclerView.Adapter<RecycleViewFolderListAdapter.ViewHolder> {
    static final public String FOLDER_ID = "folderId";
    ArrayList<SelectFolderActivity.Album> list = new ArrayList<>();
    Context context;

    public RecycleViewFolderListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_preview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.folderText.setText(list.get(position).getName());
        Glide.with(context)
                .load(list.get(position).getCoverUri())
                .fitCenter()
                .into(holder.folderCover);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SelectFileActivity.class);
                intent.putExtra(FOLDER_ID, list.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refreshList(ArrayList<SelectFolderActivity.Album> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        TextView folderText;
        ImageView folderCover;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            folderText = (TextView) view.findViewById(R.id.folder_text);
            folderCover = (ImageView) view.findViewById(R.id.folder_preview_image);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}