package com.elirex.weather;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

/**
 * Created by Wang, Sheng-Yuan (Elirex) on 2016/3/20.
 */
public class LocationEditTextPreference extends EditTextPreference {

    private static final int DEFAULT_MINIMUM_LOCATION_LENGTH = 2;

    private int mMinLegth;

    public LocationEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.LocationEditTextPreference, 0, 0);
        try {
            mMinLegth = typedArray.getInteger(R.styleable.LocationEditTextPreference_minLenght,
                    DEFAULT_MINIMUM_LOCATION_LENGTH);
        } finally {
            typedArray.recycle();
        }
    }

}
