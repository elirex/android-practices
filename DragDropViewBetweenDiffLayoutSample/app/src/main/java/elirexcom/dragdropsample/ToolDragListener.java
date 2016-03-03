package elirexcom.dragdropsample;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by nickwang on 2016/3/3.
 */
public class ToolDragListener implements View.OnDragListener {

    private Drawable mNormalShape, mTargetShape;
    private View mViewGroup;

    public ToolDragListener(View viewGroup) {
        mViewGroup = viewGroup;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNormalShape = App.getContext().getDrawable(R.drawable.normal_shape);
            mTargetShape = App.getContext().getDrawable(R.drawable.target_shape);
        } else {
            mNormalShape = ContextCompat.getDrawable(App.getContext(), R.drawable.normal_shape);
            mTargetShape = ContextCompat.getDrawable(App.getContext(), R.drawable.target_shape);
        }
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
// Handles each of the expected events
        switch (event.getAction()) {

            //signal for the start of a drag and drop operation.
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                break;

            //the drag point has entered the bounding box of the View
            case DragEvent.ACTION_DRAG_ENTERED:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackground(mTargetShape);    //change the shape of the view
                } else {
                    v.setBackgroundDrawable(mTargetShape);
                }
                break;

            //the user has moved the drag shadow outside the bounding box of the View
            case DragEvent.ACTION_DRAG_EXITED:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackground(mNormalShape);	//change the shape of the view back to normal
                } else {
                    v.setBackgroundDrawable(mNormalShape);
                }
                break;

            //drag shadow has been released,the drag point is within the bounding box of the View
            case DragEvent.ACTION_DROP:
                // if the view is the bottomlinear, we accept the drag item
                if(v == mViewGroup) {
                    View view = (View) event.getLocalState();
                    ViewGroup viewgroup = (ViewGroup) view.getParent();
                    viewgroup.removeView(view);

                    //change the text
                    // TextView text = (TextView) v.findViewById(R.id.text);
                    // text.setText("The item is dropped");

                    // LinearLayout containView = (LinearLayout) v;
                    // containView.addView(view);
                    String item = ((TextView) v).getText().toString();
                    Context context = App.getContext();
                    Toast.makeText(context, item,
                            Toast.LENGTH_LONG).show();
                    view.setVisibility(View.VISIBLE);
                } else {
                    View view = (View) event.getLocalState();
                    view.setVisibility(View.VISIBLE);
                    Context context = App.getContext();
                    Toast.makeText(context, "You can't drop the image here",
                            Toast.LENGTH_LONG).show();
                    break;
                }
                break;

            //the drag and drop operation has concluded.
            case DragEvent.ACTION_DRAG_ENDED:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.setBackground(mNormalShape);    //go back to normal shape
                } else {
                    v.setBackgroundDrawable(mNormalShape);
                }

            default:
                break;
        }
        return true;
    }


}
