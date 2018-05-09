package com.example.kidbank.kidbanknew.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.adapter.ViewPagerAdapter;
import com.example.kidbank.kidbanknew.adapter.ViewpagerAdapterSparemal;

import java.util.ArrayList;

/**
 * Created by vishal on 7/3/18.
 */

public class SparemalActivity extends AppCompatActivity {

    CommanClass cc;

    TextView tv_head,tv_user_name,tv_balance;

    ImageView iv_main_bg,iv_toolbar_back,iv_previous,iv_next,iv_profile_pic,iv_home,iv_add_wishlist;

    ViewPager vp_sparemal;

    private ArrayList<Integer> images;
    private BitmapFactory.Options options;

    private FragmentStatePagerAdapter adapter;

    private final static int[] resourceIDs = new int[]{R.drawable.car1,R.drawable.car2};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sparemal);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        images = new ArrayList<>();

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_previous = (ImageView)findViewById(R.id.iv_previous);
        iv_next = (ImageView)findViewById(R.id.iv_next);
        iv_profile_pic = (ImageView)findViewById(R.id.iv_profile_pic);
        iv_home = (ImageView)findViewById(R.id.iv_home);
        iv_add_wishlist = (ImageView)findViewById(R.id.iv_add_wishlist);

        vp_sparemal = (ViewPager) findViewById(R.id.vp_sparemal);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_user_name = (TextView)findViewById(R.id.tv_user_name);
        tv_balance = (TextView)findViewById(R.id.tv_balance);

        tv_head.setText("Sparemal");
        tv_user_name.setText(cc.loadPrefString("name"));
        tv_balance.setText(cc.loadPrefString("balance"));


        Glide.with(this)
                .load(cc.loadPrefString("profile_pic"))
                .into(iv_profile_pic);


        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);

        setImagesData();

        adapter = new ViewpagerAdapterSparemal(getSupportFragmentManager(), images);
        vp_sparemal.setAdapter(adapter);
        //inflateThumbnails();

        clickListner();
    }

    private void clickListner() {

        iv_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vp_sparemal.getCurrentItem() > 0) {
                    vp_sparemal.setCurrentItem(vp_sparemal.getCurrentItem() - 1);
                }

            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vp_sparemal.getCurrentItem() < vp_sparemal.getAdapter().getCount() - 1) {
                    vp_sparemal.setCurrentItem(vp_sparemal.getCurrentItem() + 1);
                }

            }
        });

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        iv_add_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentAddSparemal = new Intent(SparemalActivity.this,AddImageSparemal.class);
                startActivity(intentAddSparemal);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void setImagesData() {
        for (int i = 0; i < resourceIDs.length; i++) {
            images.add(resourceIDs[i]);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
