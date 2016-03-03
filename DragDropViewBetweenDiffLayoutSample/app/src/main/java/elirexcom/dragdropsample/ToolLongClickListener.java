package elirexcom.dragdropsample;

import android.content.ClipData;
import android.content.ClipDescription;
import android.view.View;

/**
 * Created by nickwang on 2016/3/3.
 */
public class ToolLongClickListener implements View.OnLongClickListener {


    @Override
    public boolean onLongClick(View v) {
        ClipData.Item item = new ClipData.Item(v.getTag().toString());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_INTENT};
        ClipData data = new ClipData(v.getTag().toString(), mimeTypes, item);
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

        v.startDrag(data, shadowBuilder, v, 0);
        return true;
    }


}
