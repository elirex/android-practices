package com.elirex.viewpagersample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/11.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<String> mItems;

    public CustomAdapter(List<String> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemText.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemText;

        public ViewHolder(View view) {
            super(view);
            itemText = (TextView) view.findViewById(R.id.item_text);
        }


    }

}
