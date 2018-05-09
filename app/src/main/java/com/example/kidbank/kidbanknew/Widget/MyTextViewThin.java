package com.example.kidbank.kidbanknew.Widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Akshay on 30-Jun-17.
 */

public class MyTextViewThin extends TextView {

    public MyTextViewThin(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyTextViewThin(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextViewThin(Context context) {
        super(context);
        init();
    }

    public void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/FREDOKAONE-REGULAR.TTF");
        setTypeface(tf, 1);

    }
}
