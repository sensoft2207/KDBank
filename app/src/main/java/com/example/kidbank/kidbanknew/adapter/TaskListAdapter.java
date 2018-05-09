package com.example.kidbank.kidbanknew.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.Model.Country;
import com.example.kidbank.kidbanknew.Model.TaskListData;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.activity.TaskDetailActivity;

import java.util.List;

/**
 * Created by vishal on 8/3/18.
 */

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    static CommanClass cc;

    private List<TaskListData> countries;
    private int rowLayout;
    public static Context mContext;


    public TaskListAdapter(List<TaskListData> countries, int rowLayout, Context context) {

        cc = new CommanClass(context);

        this.countries = countries;
        this.rowLayout = rowLayout;
        this.mContext = context;

    }

    @Override
    public int getItemCount() {
        return countries == null ? 0 : countries.size();
    }

    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new TaskListAdapter.ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final TaskListAdapter.ViewHolder viewHolder, int i) {
        TaskListData myTask = countries.get(i);

        viewHolder.tv_task_name.setText(myTask.getTask_name());
        viewHolder.tv_deadline.setText("DEADLINE :"+" "+myTask.getLast_date());
        viewHolder.tv_salary_amount.setText("AMOUNT : KR"+" "+myTask.getSalary_amount());

        if (myTask.getTask_status().equals("Pending")){

            viewHolder.status_display.setBackground(mContext.getDrawable(R.drawable.red_circle));

        }else {

            viewHolder.status_display.setBackground(mContext.getDrawable(R.drawable.green_circle));
        }

        Glide.with(mContext)
                .load(myTask.getTaskImage())
                .into(viewHolder.iv_task_image);

        viewHolder.header_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTaskDataByPosition(viewHolder.getAdapterPosition());
            }
        });

    }

    private void getTaskDataByPosition(int adapterPosition) {

        TaskListData t_data = countries.get(adapterPosition);

        String t_id = t_data.getTask_id();
        String t_name = t_data.getTask_name();
        String t_amount = t_data.getSalary_amount();
        String t_deadline = t_data.getLast_date();
        String t_status = t_data.getTask_status();
        String t_image = t_data.getTaskImage();
        String t_kidname = t_data.getKidname();


        Intent intentTaskDetail = new Intent(mContext, TaskDetailActivity.class);
        intentTaskDetail.putExtra("t_id",t_id);
        intentTaskDetail.putExtra("t_name",t_name);
        intentTaskDetail.putExtra("t_amount",t_amount);
        intentTaskDetail.putExtra("t_deadline",t_deadline);
        intentTaskDetail.putExtra("t_status",t_status);
        intentTaskDetail.putExtra("t_image",t_image);
        intentTaskDetail.putExtra("t_kidname",t_kidname);
        mContext.startActivity(intentTaskDetail);


    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_task_name,tv_deadline,tv_salary_amount,status_display;
        public LinearLayout header_click;
        ImageView iv_task_image;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_task_name = (TextView) itemView.findViewById(R.id.tv_task_name);
            tv_deadline = (TextView) itemView.findViewById(R.id.tv_deadline);
            tv_salary_amount = (TextView) itemView.findViewById(R.id.tv_salary_amount);
            status_display = (TextView) itemView.findViewById(R.id.status_display);

            header_click = (LinearLayout)itemView.findViewById(R.id.header_click);

            iv_task_image = (ImageView)itemView.findViewById(R.id.iv_task_image);
        }
    }

}

