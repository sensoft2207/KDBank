package com.example.kidbank.kidbanknew.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.R;

/**
 * Created by vishal on 7/3/18.
 */

public class PageFragment extends Fragment {

    private String imageResource,imageId;
    private Bitmap bitmap;

    public static PageFragment getInstance(String resourceID,String task_id) {
        PageFragment f = new PageFragment();
        Bundle args = new Bundle();
        args.putString("image_source",resourceID);
        args.putString("image_id",task_id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageResource = getArguments().getString("image_source");
        imageId = getArguments().getString("image_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 4;
        o.inDither = false;
       // bitmap = BitmapFactory.decodeResource(getResources(), imageResource, o);

        Log.e("IMAGEPAGER",imageResource);
        Log.e("IMAGEPAGER",imageId);

        Glide.with(this)
                .load(imageResource)
                .into(imageView);
    }
}
