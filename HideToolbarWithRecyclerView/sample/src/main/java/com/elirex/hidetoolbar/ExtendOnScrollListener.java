package com.elirex.hidetoolbar;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import java.lang.ref.WeakReference;

/**
 * Created by nickwang on 2016/2/26.
 */
public class ExtendOnScrollListener extends RecyclerView.OnScrollListener {

    private WeakReference<View> mView;
    private OnScrollListener mOnScrollListener;
    private OnScrollHideHelper mScrollHideHelper;
    private boolean mIsVisible;


    public ExtendOnScrollListener(View view, OnScrollListener onScrollListener) {
        mView = new WeakReference<View>(view);
        mOnScrollListener = onScrollListener;
        ViewConfiguration viewConfig = ViewConfiguration.get(mView.get().getContext());
        int threshold = viewConfig.getScaledMinimumFlingVelocity();
        mScrollHideHelper = new OnScrollHideHelper(threshold);

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        final View view = mView.get();
        if(view == null) return;

        boolean isShouldBeOutside = mScrollHideHelper.isViewShouldBeOutside(dy);

        if(!mIsVisible && !isShouldBeOutside) {
            Log.d("onScrolled", "show");
            show(view);
            mIsVisible = true;
        } else if(mIsVisible && isShouldBeOutside) {
            Log.d("onScrolled", "hide");
            hide(view, -view.getHeight());
            mIsVisible = false;
        }

        if(mOnScrollListener != null) {
            mOnScrollListener.onScrolled(recyclerView, dx, dy);
        }

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    private void show(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",
                view.getTranslationY(), 0.f);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void hide(View view, float distance) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY",
                view.getTranslationY(), distance);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /* Class and Interface */
    public abstract static class OnScrollListener {
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {}
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {}
    }


    public static class OnScrollHideHelper {

        public static final int UNKNOWN = 0;
        public static final int UP = 1;
        public static final int DOWN = 2;

        private int mScrollDistance;
        private int mOldDirection;
        private int mNowDragDirection;

        private int mHideShowThreshold;

        public OnScrollHideHelper(int threshold) {
            mOldDirection = UNKNOWN;
            mNowDragDirection = UNKNOWN;
            mScrollDistance = 0;
            mHideShowThreshold = threshold;
        }

        public boolean isViewShouldBeOutside(int dy) {
            mNowDragDirection = dy > UNKNOWN ? DOWN : UP;

            if(mNowDragDirection != mOldDirection) {
                mOldDirection = mNowDragDirection;
                mScrollDistance = 0;
            }

            mScrollDistance += dy;
            if(mNowDragDirection == UP && Math.abs(mScrollDistance) > mHideShowThreshold) {
                return false;
            } else if(mNowDragDirection == DOWN && mScrollDistance > mHideShowThreshold) {
                return true;
            } else {
                return false;
            }
        }

    }



}
