package com.elirex.swipeback;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import java.security.MessageDigest;

/**
 * @author Sheng-Yuan Wang (2016/1/14).
 */
public class SwipeBackLayout extends ViewGroup {

    private static final String LOG_TAG = SwipeBackLayout.class.getSimpleName();

    private static final double AUTO_FINISHED_SPEED_LIMIT = 200.0;
    private static final float BACK_FACTOR = 0.5f;

    public enum DragEdge { LEFT, TOP, RIGHT, BOTTOM }

    private DragEdge mDragEdge = DragEdge.TOP;
    private final ViewDragHelper mViewDragHelper;
    private View mTargeView;
    private View mScrollChild;
    private int mVerticalDragRange = 0;
    private int mHorizontalDragRange = 0;
    private int mDraggingState = 0;
    private int mDraggingOffset;
    private boolean mEnablePullToBack = true;
    private boolean mEnableFlingBack = true;
    private float mFinishAnchor = 0;

    private SwipeBackListener mListener;

    public SwipeBackLayout(Context context) {
        this(context, null);
    }

    public SwipeBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallBack());
    }

    /* === Public Methods === */
    public void setScrollChild(View view) {
        mScrollChild = view;
    }

    public void setEnablePullToBack(boolean b) {
        mEnablePullToBack = b;
    }

    public void setOnSwipeBackListener(SwipeBackListener listener) {
        mListener = listener;
    }

    public void setFinishAnchor(float offset) {
        mFinishAnchor = offset;
    }

    public void setEnableFlingBack(boolean b) {
        mEnableFlingBack = b;
    }

    public void setDragEdge(DragEdge dragEdge) {
        mDragEdge = dragEdge;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;
        ensureTarget();
        if(isEnabled()) {
            handled = mViewDragHelper.shouldInterceptTouchEvent(ev);
        } else {
            mViewDragHelper.cancel();
        }
        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if(mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean canChildScrollUp() {
        return ViewCompat.canScrollVertically(mScrollChild, -1);
    }

    public boolean canChildScrollDown() {
        return ViewCompat.canScrollVertically(mScrollChild, 1);
    }

    public boolean canChildScrollRight() {
        return ViewCompat.canScrollHorizontally(mScrollChild, -1);
    }

    public boolean canChildScrollLeft() {
        return ViewCompat.canScrollHorizontally(mScrollChild, 1);
    }

    /* === Protected Methods === */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if(getChildCount() == 0) return;

        View child = getChildAt(0);

        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childRight = childLeft + childWidth;
        int childBottom = childTop + childHeight;
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getChildCount() > 1) {
            throw new IllegalStateException("SwipeBackLayout must contains only one direct child.");
        }

        if(getChildCount() > 0) {
            int measureWidth = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY);
            int measureHeight = MeasureSpec.makeMeasureSpec(getMeasuredHeight()- getPaddingTop() - getPaddingBottom(),
                    MeasureSpec.EXACTLY);
            getChildAt(0).measure(measureWidth, measureHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mVerticalDragRange = h;
        mHorizontalDragRange = w;
        switch (mDragEdge) {
            case TOP:
            case BOTTOM:
                mFinishAnchor = mFinishAnchor > 0 ? mFinishAnchor : mVerticalDragRange * BACK_FACTOR;
                break;
            case LEFT:
            case RIGHT:
                mFinishAnchor = mFinishAnchor > 0 ? mFinishAnchor : mHorizontalDragRange * BACK_FACTOR;
                break;
        }
    }

    /* === Private Methods === */
    private void ensureTarget() {
        if(mTargeView == null) {
            if(getChildCount() > 1) {
                throw new IllegalStateException("SwipeBackLayout must contains only one direct child");
            }
            mTargeView = getChildAt(0);
            if(mScrollChild == null && mTargeView != null) {
                if(mTargeView instanceof ViewGroup) {
                    findScrollView((ViewGroup) mTargeView);
                } else {
                    mScrollChild = mTargeView;
                }
            }
        }
    }

    private void findScrollView(ViewGroup viewGroup) {
        mScrollChild = viewGroup;
        if(viewGroup.getChildCount() > 0) {
            int count = viewGroup.getChildCount();
            View child;
            for(int i = 0; i < count; ++i) {
                child = viewGroup.getChildAt(i);
                if(child instanceof AbsListView || child instanceof ScrollView
                        || child instanceof ViewPager || child instanceof WebView) {
                    mScrollChild = child;
                    return;
                }
            }
        }
    }

    private int getDragRange() {
        switch (mDragEdge) {
            case TOP:
            case BOTTOM:
                return mVerticalDragRange;
            case LEFT:
            case RIGHT:
                return mHorizontalDragRange;
            default:
                return mVerticalDragRange;
        }
    }

    private void finish() {
        Activity act = (Activity) getContext();
        act.finish();
        act.overridePendingTransition(0, android.R.anim.fade_out);
    }

    /* === Class and Inteface === */
    private class ViewDragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mTargeView && mEnablePullToBack;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mVerticalDragRange;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mHorizontalDragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int result = 0;
            if(mDragEdge == DragEdge.TOP && !canChildScrollUp() && top > 0) {
                final int topBound = getPaddingTop();
                final int bottomBound = mVerticalDragRange;
                result =  Math.min(Math.max(top, topBound), bottomBound);
            } else if(mDragEdge == DragEdge.BOTTOM && !canChildScrollDown() && top < 0){
                final int topBound = -mVerticalDragRange;
                final int bootomBound = getPaddingTop();
                result = Math.min(Math.max(top, topBound), bootomBound);
            }
            return result;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int result = 0;
            if(mDragEdge == DragEdge.LEFT && !canChildScrollRight() && left > 0) {
                final int leftBound = getPaddingLeft();
                final int rightBound = mHorizontalDragRange;
                result = Math.min(Math.max(left, leftBound), rightBound);
            } else if(mDragEdge == DragEdge.RIGHT && !canChildScrollLeft() && left < 0) {
                final int leftBound = -mHorizontalDragRange;
                final int rightBound = getPaddingLeft();
                result = Math.min(Math.max(left, leftBound), rightBound);
            }
            return result;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if(state == mDraggingState) return;

            if((mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                    mDraggingState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                if (mDraggingOffset == getDragRange()) {
                    finish();
                }
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            switch (mDragEdge) {
                case TOP:
                case BOTTOM:
                    mDraggingOffset = Math.abs(top);
                    break;
                case LEFT:
                case RIGHT:
                    mDraggingOffset = Math.abs(left);
                    break;
            }

            float fractionAnchor = (float) mDraggingOffset / mFinishAnchor;
            if(fractionAnchor >= 1) fractionAnchor = 1;

            float fractoinScreen = (float) mDraggingOffset / (float) getDragRange();
            if(fractoinScreen >= 1) fractoinScreen = 1 ;

            if(mListener != null) {
                mListener.onViewPositionChanged(fractionAnchor, fractoinScreen);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if(mDraggingOffset == 0) return;

            if(mDraggingOffset == getDragRange()) return;

            boolean isBack = false;

            if(mEnableFlingBack && backBySpeed(xvel, yvel)) {
                isBack = !canChildScrollUp();
            } else if(mDraggingOffset >= mFinishAnchor){
                isBack = true;
            } else if(mDraggingOffset < mFinishAnchor) {
                isBack = false;
            }

            int finalLeft;
            int finalTop;
            switch (mDragEdge) {
                case LEFT:
                    finalLeft = isBack ? mHorizontalDragRange : 0;
                    smoothScrollToX(finalLeft);
                    break;
                case RIGHT:
                    finalLeft = isBack ? -mHorizontalDragRange : 0;
                    smoothScrollToX(finalLeft);
                    break;
                case TOP:
                    finalTop = isBack ? mVerticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
                case BOTTOM:
                    finalTop = isBack ? -mVerticalDragRange : 0;
                    smoothScrollToY(finalTop);
                    break;
            }
        }

        private boolean backBySpeed(float xvel, float yvel) {
            switch (mDragEdge) {
                case TOP:
                case BOTTOM:
                    if(Math.abs(yvel) > Math.abs(xvel) && Math.abs(yvel) > AUTO_FINISHED_SPEED_LIMIT) {
                        return mDragEdge == DragEdge.TOP ? !canChildScrollUp() : !canChildScrollDown();
                    }
                    break;
                case LEFT:
                case RIGHT:
                    if(Math.abs(xvel) > Math.abs(yvel) && Math.abs(xvel) > AUTO_FINISHED_SPEED_LIMIT) {
                        return mDragEdge == DragEdge.LEFT ? !canChildScrollLeft() : !canChildScrollRight();
                    }
                    break;
            }
            return false;
        }

        private void smoothScrollToX(int finalLeft) {
            if(mViewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
                ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
            }
        }

        private void smoothScrollToY(int finalTop) {
            if(mViewDragHelper.settleCapturedViewAt(0, finalTop)) {
                ViewCompat.postInvalidateOnAnimation(SwipeBackLayout.this);
            }
        }

    }

    public interface SwipeBackListener {
        void onViewPositionChanged(float factionAnchor, float fractionScreen);
    }

}
