package com.example.kidbank.kidbanknew.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.R;

/**
 * Created by vishal on 13/3/18.
 */

public class AddImageSparemal extends AppCompatActivity {

    TextView tv_head;
    ImageView iv_main_bg,iv_toolbar_back,iv_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_image);


        init();

    }

    private void init() {

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_head.setText("Sparemal");

        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);

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

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
