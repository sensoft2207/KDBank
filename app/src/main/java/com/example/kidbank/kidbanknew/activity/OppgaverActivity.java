package com.example.kidbank.kidbanknew.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.Model.KidTaskData;
import com.example.kidbank.kidbanknew.Model.TaskListData;
import com.example.kidbank.kidbanknew.Network.AppController;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.Network.URL;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.Utility.AndyUtils;
import com.example.kidbank.kidbanknew.adapter.ViewPagerAdapter;

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
 * Created by vishal on 7/3/18.
 */

public class OppgaverActivity extends AppCompatActivity {

    CommanClass cc;

    ViewPager vp_oppgaver;

    ImageView iv_main_bg,iv_toolbar_back,iv_previous,iv_next,iv_profile_pic,iv_home,iv_done_task,iv_reject_task;

    TextView tv_head,tv_user_name,tv_balance;

    LinearLayout ln_task_not_assigned,ln_task_bottom;
    FrameLayout fl_task_slider;

    private ArrayList<KidTaskData> images;
    private BitmapFactory.Options options;

    private FragmentStatePagerAdapter adapter;

    private final static int[] resourceIDs = new int[]{R.drawable.car1,R.drawable.car2,R.drawable.car3,R.drawable.car4};

    ProgressDialog pDialog;

    String user_id;

    ArrayList<KidTaskData> kid_task_list;

    String viewPagerPosition = "0";

    String statusMain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppgaver);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        user_id = cc.loadPrefString("userid");

        images = new ArrayList<>();

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_previous = (ImageView)findViewById(R.id.iv_previous);
        iv_next = (ImageView)findViewById(R.id.iv_next);
        iv_profile_pic = (ImageView)findViewById(R.id.iv_profile_pic);
        iv_home = (ImageView)findViewById(R.id.iv_home);
        iv_done_task = (ImageView)findViewById(R.id.iv_done_task);
        iv_reject_task = (ImageView)findViewById(R.id.iv_reject_task);


        vp_oppgaver = (ViewPager) findViewById(R.id.vp_oppgaver);

        ln_task_not_assigned = (LinearLayout) findViewById(R.id.ln_task_not_assigned);
        ln_task_bottom = (LinearLayout) findViewById(R.id.ln_task_bottom);
        fl_task_slider = (FrameLayout) findViewById(R.id.fl_task_slider);

        ln_task_bottom.setVisibility(View.INVISIBLE);
        ln_task_not_assigned.setVisibility(View.INVISIBLE);
        fl_task_slider.setVisibility(View.INVISIBLE);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_user_name = (TextView)findViewById(R.id.tv_user_name);
        tv_balance = (TextView)findViewById(R.id.tv_balance);

        tv_head.setText("Oppgaver");
        tv_user_name.setText(cc.loadPrefString("name"));


        Glide.with(this)
                .load(cc.loadPrefString("profile_pic"))
                .into(iv_profile_pic);



        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);

        vp_oppgaver.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

                for (int j = 0; j <kid_task_list.size() ; j++) {

                    KidTaskData kData = kid_task_list.get(j);

                    if (j == i){

                        viewPagerPosition = kData.getTask_id();

                        tv_balance.setText(kData.getSalary_amount());

                        Log.e("FinalWSData",viewPagerPosition);
                        Log.e("FinalWSData",kData.getTask_name());


                    }else {

                    }
                }

            }

            @Override
            public void onPageSelected( int i) {
                // here you will get the position of selected page



                Log.e("VIEWPAGERPOSSelected", String.valueOf(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


        clickListner();


        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(OppgaverActivity.this, getString(R.string.no_internet));

        } else {

            getKidTaskList();

        }

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

                if (vp_oppgaver.getCurrentItem() > 0) {
                    vp_oppgaver.setCurrentItem(vp_oppgaver.getCurrentItem() - 1);
                }

            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vp_oppgaver.getCurrentItem() < vp_oppgaver.getAdapter().getCount() - 1) {
                    vp_oppgaver.setCurrentItem(vp_oppgaver.getCurrentItem() + 1);
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

        iv_done_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("DoneTask",viewPagerPosition);

                statusMain = "Completed";

                taskDoneOrRejectWS(user_id,viewPagerPosition,statusMain);
            }
        });

        iv_reject_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statusMain = "Declined";

                taskDoneOrRejectWS(user_id,viewPagerPosition,statusMain);

                Log.e("RejectTask",viewPagerPosition);
            }
        });
    }

    private void taskDoneOrRejectWS(final String user_id, final String viewPagerPosition, final String statusMain) {

        pDialog = new ProgressDialog(OppgaverActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_kid_submit_task,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:task data", response);


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {

                                pDialog.dismiss();



                                if (statusMain.equals("Completed")){

//                                    getKidTaskListTwo();

                                    Intent intentOppdrag = new Intent(OppgaverActivity.this,OppgaverActivity.class);
                                    startActivity(intentOppdrag);
                                    finish();

                                    cc.showToast("Task Done Successfully");

                                }else {

//                                    getKidTaskListTwo();


                                    Intent intentOppdrag = new Intent(OppgaverActivity.this,OppgaverActivity.class);
                                    startActivity(intentOppdrag);
                                    finish();

                                    cc.showToast("Task Decline Successfully");
                                }

                            } else {
                                pDialog.dismiss();
                                cc.showToast(jsonObject.getString("message"));


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
                params.put("task_id",viewPagerPosition);
                params.put("status",statusMain);

                Log.e("request", String.valueOf(params));

                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "delta141forceSEAL8PARA9MARCOSBRAHMOS");
                headers.put("UserAuth", cc.loadPrefString("api_token"));
                Log.i("request header", headers.toString());
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    private void setImagesData() {

        for (int i = 0; i < kid_task_list.size(); i++) {

            KidTaskData kd = kid_task_list.get(i);

            kd.getTaskImage();
            kd.getTask_id();

            images.add(kd);
        }
    }

    private void getKidTaskList() {


        pDialog = new ProgressDialog(OppgaverActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_kid_task_list,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:task data", response);

                        kid_task_list = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);



                            if (jsonObject.getString("status").equals("200")) {

                                pDialog.dismiss();



                                JSONArray dataArray = jsonObject.getJSONArray("task data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject obj = dataArray.getJSONObject(i);

                                    Log.e("TaskName",obj.getString("task_name"));


                                    if (obj.getString("status").equals("Pending")){

                                        KidTaskData kData = new KidTaskData();

                                        kData.setTask_id(obj.getString("task_id"));
                                        kData.setReciever_id(obj.getString("reciever_id"));
                                        kData.setSalary_amount(obj.getString("salary_amount"));
                                        kData.setSender_id(obj.getString("sender_id"));
                                        kData.setTaskImage(obj.getString("TaskImage"));
                                        kData.setTask_name(obj.getString("task_name"));

                                        kid_task_list.add(kData);

                                    }else {

                                    }

                                }

                                setImagesData();

                                adapter = new ViewPagerAdapter(getSupportFragmentManager(), images);
                                vp_oppgaver.setAdapter(adapter);

                                if (kid_task_list.size() == 0){

                                    ln_task_bottom.setVisibility(View.INVISIBLE);
                                    ln_task_not_assigned.setVisibility(View.VISIBLE);
                                    fl_task_slider.setVisibility(View.INVISIBLE);

                                }else {

                                    ln_task_bottom.setVisibility(View.VISIBLE);
                                    ln_task_not_assigned.setVisibility(View.INVISIBLE);
                                    fl_task_slider.setVisibility(View.VISIBLE);
                                }


                            } else {
                                pDialog.dismiss();
                                ln_task_bottom.setVisibility(View.INVISIBLE);
                                ln_task_not_assigned.setVisibility(View.VISIBLE);
                                fl_task_slider.setVisibility(View.INVISIBLE);
                                cc.showToast(jsonObject.getString("message"));
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
                headers.put("UserAuth", cc.loadPrefString("api_token"));
                Log.i("request header", headers.toString());
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    private void getKidTaskListTwo() {

        pDialog = new ProgressDialog(OppgaverActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_kid_task_list,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:task data", response);

                        kid_task_list = new ArrayList<>();

                        pDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {


                                JSONArray dataArray = jsonObject.getJSONArray("task data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject obj = dataArray.getJSONObject(i);

                                    Log.e("TaskName",obj.getString("task_name"));


                                    if (obj.getString("status").equals("Pending")){

                                        KidTaskData kData = new KidTaskData();

                                        kData.setTask_id(obj.getString("task_id"));
                                        kData.setReciever_id(obj.getString("reciever_id"));
                                        kData.setSalary_amount(obj.getString("salary_amount"));
                                        kData.setSender_id(obj.getString("sender_id"));
                                        kData.setTaskImage(obj.getString("TaskImage"));
                                        kData.setTask_name(obj.getString("task_name"));

                                        kid_task_list.add(kData);

                                    }else {

                                    }

                                    for (int j = 0; j <kid_task_list.size() ; j++) {

                                        KidTaskData kd = kid_task_list.get(j);

                                        if (j == 0){

                                            tv_balance.setText(kd.getSalary_amount());
                                        }
                                    }

                                }

                                setImagesData();

                                adapter = new ViewPagerAdapter(getSupportFragmentManager(), images);
                                vp_oppgaver.setAdapter(adapter);

                                adapter.notifyDataSetChanged();

                                if (kid_task_list.size() == 0){

                                    ln_task_bottom.setVisibility(View.INVISIBLE);
                                    ln_task_not_assigned.setVisibility(View.VISIBLE);
                                    fl_task_slider.setVisibility(View.INVISIBLE);
                                    tv_balance.setText("00");

                                }else {

                                    ln_task_bottom.setVisibility(View.VISIBLE);
                                    ln_task_not_assigned.setVisibility(View.INVISIBLE);
                                    fl_task_slider.setVisibility(View.VISIBLE);
                                }


                            } else {

                                ln_task_bottom.setVisibility(View.INVISIBLE);
                                ln_task_not_assigned.setVisibility(View.VISIBLE);
                                fl_task_slider.setVisibility(View.INVISIBLE);

                                cc.showToast(jsonObject.getString("message"));
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
                headers.put("UserAuth", cc.loadPrefString("api_token"));
                Log.i("request header", headers.toString());
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
