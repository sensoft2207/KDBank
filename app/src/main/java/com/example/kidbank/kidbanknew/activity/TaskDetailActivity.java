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

public class TaskDetailActivity extends AppCompatActivity {

    TextView tv_taskdetail_name,tv_task_status,tv_task_deadline,tv_task_kidname,tv_task_amount,tv_head;

    ImageView iv_task_image,iv_main_bg,iv_toolbar_back,iv_home;

    String t_id,t_name,t_amount,t_image,t_deadline,t_status,t_kidname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_detail_activity);


        t_id = getIntent().getStringExtra("t_id");
        t_name = getIntent().getStringExtra("t_name");
        t_amount = getIntent().getStringExtra("t_amount");
        t_image = getIntent().getStringExtra("t_image");
        t_deadline = getIntent().getStringExtra("t_deadline");
        t_status = getIntent().getStringExtra("t_status");
        t_kidname = getIntent().getStringExtra("t_kidname");

        init();
    }

    private void init() {

        tv_taskdetail_name = (TextView)findViewById(R.id.tv_taskdetail_name);
        tv_task_status = (TextView)findViewById(R.id.tv_task_status);
        tv_task_deadline = (TextView)findViewById(R.id.tv_task_deadline);
        tv_task_kidname = (TextView)findViewById(R.id.tv_task_kidname);
        tv_task_amount = (TextView)findViewById(R.id.tv_task_amount);
        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_head.setText("TASK MANAGER");


        iv_task_image = (ImageView)findViewById(R.id.iv_task_image);

        tv_taskdetail_name.setText(t_name);
        tv_task_status.setText(t_status);
        tv_task_deadline.setText("DEADLINE : " + t_deadline);
        tv_task_amount.setText("KR " + t_amount);
        tv_task_kidname.setText("Kid : " + t_kidname);

        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);

        Glide.with(this)
                .load(t_image)
                .into(iv_task_image);


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
