package com.elirex.swipeback;

import android.os.Bundle;
import android.view.MenuItem;

/**
 * @author Sheng-Yuan Wang (2016/1/14).
 */
public class NewActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


}
