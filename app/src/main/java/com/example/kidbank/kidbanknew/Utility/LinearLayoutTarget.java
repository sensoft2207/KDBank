package com.example.kidbank.kidbanknew.Utility;

import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

/**
 * Created by vishal on 7/3/18.
 */

public class LinearLayoutTarget extends ViewGroupTarget<Drawable> {

    public LinearLayoutTarget(LinearLayout linearLayout) {

        super(linearLayout);
    }

    /**
     * Sets the {@link android.graphics.Bitmap} on the view using
     * {@link android.widget.ImageView#setImageBitmap(android.graphics.Bitmap)}.
     *
     * @param resource The bitmap to display.
     */
    @Override
    protected void setResource(Drawable resource) {

        view.setBackground(resource);
    }

}
