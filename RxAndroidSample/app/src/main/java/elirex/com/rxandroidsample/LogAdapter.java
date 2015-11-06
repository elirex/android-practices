package elirex.com.rxandroidsample;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * @author Sheng-Yuan Wang (2015/11/6).
 */
public class LogAdapter extends ArrayAdapter<String> {

    public LogAdapter(Context context, List<String> logs) {
        super(context, R.layout.row_item_log, R.id.item_log, logs);
    }

}
