package com.elirex.swipeback;

import android.app.ActionBar;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

/**
 * @author Sheng-Yuan Wang (2016/1/14).
 */
public class SwipeBackActivity extends AppCompatActivity implements
        SwipeBackLayout.SwipeBackListener {

    private SwipeBackLayout mSwipeBackLayout;
    private ImageView mIvShadow;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        mSwipeBackLayout.addView(view);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        mSwipeBackLayout = new SwipeBackLayout(this);
        mSwipeBackLayout.setOnSwipeBackListener(this);
        mIvShadow = new ImageView(this);
        mIvShadow.setBackgroundColor(getResources().getColor(R.color.black_p50));
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(mIvShadow, params);
        container.addView(mSwipeBackLayout);
        return container;
    }

    public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
        mSwipeBackLayout.setDragEdge(dragEdge);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    @Override
    public void onViewPositionChanged(float factionAnchor, float fractionScreen) {
        mIvShadow.setAlpha(1 - fractionScreen);
    }
}
