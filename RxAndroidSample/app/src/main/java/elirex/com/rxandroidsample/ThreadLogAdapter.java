package elirex.com.rxandroidsample;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * @author Sheng-Yuan Wang (2015/11/16).
 */
public class ThreadLogAdapter extends ArrayAdapter<String> {

    public ThreadLogAdapter(Context context, int resource,
                            int textItemResource, List<String> logs) {
        super(context, resource, textItemResource, logs);
    }

}

