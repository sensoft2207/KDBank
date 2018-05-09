package com.example.kidbank.kidbanknew.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.Model.Country;
import com.example.kidbank.kidbanknew.Network.AppController;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.Network.URL;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.Utility.AndyUtils;
import com.example.kidbank.kidbanknew.adapter.ChildAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal on 8/3/18.
 */

public class SendKrActivity extends AppCompatActivity {

    CommanClass cc;

    ImageView iv_main_bg,iv_toolbar_back,iv_profile_pic,iv_home;

    TextView tv_head;

    ImageView iv_sender_one,iv_sender_two,iv_sender_three;
    TextView tv_sender_one,tv_sender_two,tv_sender_three,tv_target_child,tv_send_money,tv_close;

    EditText ed_amount,ed_message;

    public Dialog dialog,dialogChild;
    ProgressDialog pDialog;

    RecyclerView rv_child_list;
    ArrayList<Country> child_list;
    ChildAdapter childAdapter;

    String user_id,child_id,child_name,message,amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overfor);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        user_id = cc.loadPrefString("userid");

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_profile_pic = (ImageView)findViewById(R.id.iv_profile_pic);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        iv_sender_one = (ImageView)findViewById(R.id.iv_sender_one);
        iv_sender_two = (ImageView)findViewById(R.id.iv_sender_two);
        iv_sender_three = (ImageView)findViewById(R.id.iv_sender_three);

        tv_sender_one = (TextView)findViewById(R.id.tv_sender_one);
        tv_sender_two = (TextView)findViewById(R.id.tv_sender_two);
        tv_sender_three = (TextView)findViewById(R.id.tv_sender_three);

        tv_head = (TextView)findViewById(R.id.tv_head);

        tv_head.setText("Send Kr/Penger");

        Glide.with(this)
                .load(cc.loadPrefString("profile_pic"))
                .into(iv_profile_pic);

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

        iv_sender_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMoneyDialog(tv_sender_one.getText().toString());

            }
        });

        iv_sender_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMoneyDialog(tv_sender_two.getText().toString());

            }
        });

        iv_sender_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMoneyDialog(tv_sender_three.getText().toString());

            }
        });
    }

    private void sendMoneyDialog(final String behalf_of) {

        dialog = new Dialog(SendKrActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.send_money_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);


        ed_amount = (EditText)dialog.findViewById(R.id.ed_amount);
        ed_message = (EditText)dialog.findViewById(R.id.ed_message);
        tv_target_child = (TextView)dialog.findViewById(R.id.tv_target_child);
        tv_send_money = (TextView)dialog.findViewById(R.id.tv_send_money);
        tv_close = (TextView)dialog.findViewById(R.id.tv_close);


        tv_send_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMoney(behalf_of);
            }
        });

        tv_target_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(SendKrActivity.this, getString(R.string.no_internet));

                } else {

                    childDialogPopUp();

                }
            }
        });

        tv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void sendMoney(String behalf_of) {

        message = ed_message.getText().toString();
        amount = ed_amount.getText().toString();

        if (!cc.isConnectingToInternet()) {

        }else if (message.equals("")) {
            AndyUtils.showToast(SendKrActivity.this, getString(R.string.enter_message));

            ed_message.requestFocus();
        } else if (amount.equals("")) {

            AndyUtils.showToast(SendKrActivity.this, getString(R.string.enter_amount));
            ed_amount.requestFocus();
        } else if (child_name == null) {

            AndyUtils.showToast(SendKrActivity.this, getString(R.string.targetd_child));

        }
        else {

            if (!cc.isConnectingToInternet()) {

                AndyUtils.showToast(SendKrActivity.this, getString(R.string.no_internet));

            } else {

                sendMoneyWS(user_id,child_id,behalf_of,message,amount);

            }
        }
    }

    private void sendMoneyWS(final String user_id, final String child_id, final String behalf_type,
                             final String message, final String amount) {

        pDialog = new ProgressDialog(SendKrActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_parent_send_money,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:send data", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                cc.showToast("Money send successfully");

                                dialog.dismiss();

                                clearField();

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
                params.put("from_parentuser_id",user_id);
                params.put("to_kiduser_id",child_id);
                params.put("behalf_of",behalf_type);
                params.put("message",message);
                params.put("amount",amount);
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

    private void clearField() {

        ed_amount.setText("");
        ed_message.setText("");
    }

    private void childDialogPopUp() {

        dialogChild = new Dialog(SendKrActivity.this);

        dialogChild.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChild.setCancelable(false);
        dialogChild.setContentView(R.layout.target_child_display);
        dialogChild.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialogChild.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);

        dialogChild.show();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        ImageView iv_close = (ImageView)dialogChild.findViewById(R.id.iv_close);

        rv_child_list = (RecyclerView) dialogChild.findViewById(R.id.rv_child_list);
        rv_child_list.setLayoutManager(new LinearLayoutManager(SendKrActivity.this));
        rv_child_list.setItemAnimator(new DefaultItemAnimator());

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogChild.dismiss();
            }
        });

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(SendKrActivity.this, getString(R.string.no_internet));

        } else {

            getChildList();

        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            child_id = intent.getStringExtra("child_id");
            child_name = intent.getStringExtra("child_name");


            if(dialogChild != null){
                if(dialogChild.isShowing()){
                    tv_target_child.setText(child_name);
                    dialogChild.dismiss();
                }
            }

        }
    };


    public void getChildList() {

        pDialog = new ProgressDialog(SendKrActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_get_child,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:country data", response);


                        child_list = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                JSONArray dataArray = jsonObject.getJSONArray("contact");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject jsonObject1 = dataArray.getJSONObject(i);


                                    Country c_model = new Country();

                                    c_model.setId(jsonObject1.getString("userid"));
                                    c_model.setCountry_name(jsonObject1.getString("name"));

                                    child_list.add(c_model);
                                }

                                if (childAdapter != null) {
                                    childAdapter = null;
                                }

                                childAdapter = new ChildAdapter(child_list, R.layout.target_child_rv_item, SendKrActivity.this);
                                rv_child_list.setAdapter(childAdapter);


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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
