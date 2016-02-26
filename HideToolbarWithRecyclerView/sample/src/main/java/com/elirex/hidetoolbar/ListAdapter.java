package com.elirex.hidetoolbar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by nickwang on 2016/2/26.
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {


    private List<String> mItems;

    public ListAdapter(List<String> items) {
       mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.rowitem_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textItem.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textItem;

        public ViewHolder(View view) {
            super(view);
            textItem = (TextView) view.findViewById(R.id.item_text);
        }

    }

}
