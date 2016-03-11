package com.elirex.viewpagersample;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/11.
 */
public class CustomViewPager extends ViewPager {

    private boolean mSliding = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSliding(boolean b) {
        mSliding = b;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        if(mSliding) {
            return super.canScrollHorizontally(direction);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mSliding) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mSliding) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }
}
