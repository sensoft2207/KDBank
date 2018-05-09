package com.example.kidbank.kidbanknew.Widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by aksahy on 29/7/17.
 */

public class MyTextViewMedium extends TextView {
    public MyTextViewMedium(Context context) {
        super(context);
        init();
    }

    public MyTextViewMedium(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewMedium(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MyTextViewMedium(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/FREDOKAONE-REGULAR.TTF");
        setTypeface(tf, 1);

    }
}
