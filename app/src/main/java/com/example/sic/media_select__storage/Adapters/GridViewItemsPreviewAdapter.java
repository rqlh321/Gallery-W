package com.example.sic.media_select__storage.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.example.sic.media_select__storage.DatabaseHelper;
import com.example.sic.media_select__storage.R;
import com.example.sic.media_select__storage.ViewActivity;

import java.util.ArrayList;

/**
 * Created by sic on 03.09.2016.
 */
public class GridViewItemsPreviewAdapter extends BaseAdapter {
    public static final String URI = "uri";
    public static final String IS_VIDEO = "isVideo";
    LayoutInflater inflater;
    ArrayList<String> listUri;
    boolean[] selectedItems;
    Context context;
    DatabaseHelper dbHelper;

    public GridViewItemsPreviewAdapter(Context context, ArrayList<String> listUri, boolean[] selectedItems) {
        this.listUri = listUri;
        this.selectedItems = selectedItems;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbHelper = new DatabaseHelper(context);
    }

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

        final ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.grid_view_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.item_image);
            viewHolder.playButton = (ImageView) view.findViewById(R.id.play_button);
            viewHolder.selector = (Switch) view.findViewById(R.id.selector);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final boolean isVideoFile = RecycleViewMainListAdapter.isVideoFile(listUri.get(i));
        if (isVideoFile) {
            viewHolder.playButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.playButton.setVisibility(View.GONE);
        }
        Glide.with(context)
                .load(listUri.get(i))
                .fitCenter()
                .into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewActivity.class);
                intent.putExtra(URI, listUri.get(i));
                intent.putExtra(IS_VIDEO, isVideoFile);
                context.startActivity(intent);
            }
        });
        viewHolder.selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItems[i] = !selectedItems[i];
                viewHolder.selector.setChecked(selectedItems[i]);
                if (selectedItems[i]) {
                    dbHelper.addToDataBase(listUri.get(i));
                } else {
                    dbHelper.removeFromDataBase(listUri.get(i));
                }
            }
        });
        viewHolder.selector.setChecked(selectedItems[i]);
        return view;
    }

    class ViewHolder {
        Switch selector;
        ImageView playButton;
        ImageView imageView;
    }

}
