package com.elirex.bitmapsample.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.jar.Attributes;


/**
 * @author Sheng-Yuan Wang (2015/11/4).
 */
public class CustomView extends View {

    private Bitmap mBitmap;
    private static int sScreenWidth;
    private static int sScreenHeight;
    private static DisplayMetrics sDisplayMetrics;

    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImage(int imageResource) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        sDisplayMetrics = dm;
        sScreenWidth = dm.widthPixels;
        sScreenHeight = dm.heightPixels;
        Bitmap bmp = ((BitmapDrawable) ResourcesCompat
                .getDrawable(getResources(), imageResource, null)).getBitmap();
        mBitmap = bmp;
        // mBitmap = Bitmap.createScaledBitmap(bmp, sScreenWidth, sScreenHeight, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        // canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawBitmap(mBitmap, null, new Rect(0, 0, 500, 500), null);
    }

    public static int convertPixelsToDp(float px) {
       return (int)(px / (sDisplayMetrics.densityDpi / 160f));
    }
}
