package elirexcom.dragdropsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by nickwang on 2016/3/3.
 */
public class ToolListAdapter extends RecyclerView.Adapter<ToolListAdapter.ViewHolder> {

    private List<Integer> mItems;

    public ToolListAdapter(List<Integer> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.row_item_tool_icon, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.icon.setImageResource(mItems.get(position));
        holder.icon.setTag(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView icon;

        public ViewHolder(View view) {
            super(view);
            icon = (ImageView) view.findViewById(R.id.tool_icon);
            icon.setOnLongClickListener(new ToolLongClickListener());
        }

    }

}
