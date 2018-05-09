package com.example.kidbank.kidbanknew.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.CountryAdapter;
import com.example.kidbank.kidbanknew.Model.Country;
import com.example.kidbank.kidbanknew.Model.TaskListData;
import com.example.kidbank.kidbanknew.Network.AppController;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.Network.URL;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.Utility.AndyUtils;
import com.example.kidbank.kidbanknew.adapter.TaskListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal on 8/3/18.
 */

public class TaskManagerActivity extends AppCompatActivity {

    CommanClass cc;

    TextView tv_add_new_task,tv_head,tv_no_task;

    ImageView iv_main_bg,iv_toolbar_back,iv_home;

    RecyclerView rv_task_list;
    ArrayList<TaskListData> task_list;
    TaskListAdapter taskAdapter;

    ProgressDialog pDialog;

    String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        user_id = cc.loadPrefString("userid");

        tv_add_new_task = (TextView)findViewById(R.id.tv_add_new_task);
        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_no_task = (TextView)findViewById(R.id.tv_no_task);

        rv_task_list = (RecyclerView)findViewById(R.id.rv_task_list);
        rv_task_list.setLayoutManager(new LinearLayoutManager(TaskManagerActivity.this));
        rv_task_list.setItemAnimator(new DefaultItemAnimator());


        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        tv_head.setText("Oppdragsstyring");

        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);


        clickListner();
    }

    private void clickListner() {

        tv_add_new_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentAddTask = new Intent(TaskManagerActivity.this,AddTaskActivity.class);
                startActivity(intentAddTask);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

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

    public void getTaskList() {

        pDialog = new ProgressDialog(TaskManagerActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_parent_task_list,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:task data", response);


                        task_list = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                JSONArray dataArray = jsonObject.getJSONArray("task data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject jsonObject1 = dataArray.getJSONObject(i);

                                    String last_date = jsonObject1.getString("last_date");

                                    String inputPattern = "yyyy-MM-dd";
                                    String outputPattern = "dd/MM/yyyy";
                                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                    Date date = null;
                                    String str = null;

                                    try {
                                        date = inputFormat.parse(last_date);
                                        str = outputFormat.format(date);

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                    TaskListData c_model = new TaskListData();

                                    c_model.setTask_id(jsonObject1.getString("task_id"));
                                    c_model.setTask_name(jsonObject1.getString("task_name"));
                                    c_model.setSalary_amount(jsonObject1.getString("salary_amount"));
                                    c_model.setTaskImage(jsonObject1.getString("TaskImage"));
                                    c_model.setTask_status(jsonObject1.getString("task_status"));
                                    c_model.setKidname(jsonObject1.getString("kdi_name"));
                                    c_model.setLast_date(str);

                                    task_list.add(c_model);
                                }

                                if (taskAdapter != null) {
                                    taskAdapter = null;
                                }

                                taskAdapter = new TaskListAdapter(task_list, R.layout.task_manager_list_item, TaskManagerActivity.this);
                                rv_task_list.setAdapter(taskAdapter);

                            } else {
                                pDialog.dismiss();
//                                cc.showToast(jsonObject.getString("message"));
                                if (task_list.size() == 0){

                                    tv_no_task.setVisibility(View.VISIBLE);
                                    rv_task_list.setVisibility(View.INVISIBLE);
                                }else {

                                    tv_no_task.setVisibility(View.INVISIBLE);
                                    rv_task_list.setVisibility(View.VISIBLE);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                cc.showToast("Something went wrong");
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid",user_id);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "delta141forceSEAL8PARA9MARCOSBRAHMOS");
                headers.put("UserAuth",cc.loadPrefString("api_token"));
                Log.i("request header", headers.toString());
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(TaskManagerActivity.this, getString(R.string.no_internet));

        } else {

            getTaskList();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
