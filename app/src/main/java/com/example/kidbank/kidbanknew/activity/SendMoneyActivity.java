package com.example.kidbank.kidbanknew.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal on 22/3/18.
 */

public class SendMoneyActivity extends AppCompatActivity {

    CommanClass cc;

    ImageView iv_main_bg,iv_toolbar_back,iv_profile_pic,iv_home;

    TextView tv_head,tv_target_child,tv_send_money,tv_behalf_name;

    EditText ed_amount,ed_message;

    public Dialog dialog;
    ProgressDialog pDialog;

    RecyclerView rv_child_list;
    ArrayList<Country> child_list;
    ChildAdapter childAdapter;

    String user_id,child_id,child_name,behalf_type,message,amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        cc = new CommanClass(this);

        init();
    }

    private void init() {

        user_id = cc.loadPrefString("userid");

        behalf_type = getIntent().getStringExtra("behalf_type");

        Log.e("@@userid",user_id);


        ed_amount = (EditText)findViewById(R.id.ed_amount);
        ed_message = (EditText)findViewById(R.id.ed_message);

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_profile_pic = (ImageView)findViewById(R.id.iv_profile_pic);
        iv_home = (ImageView)findViewById(R.id.iv_home);
        iv_home.setVisibility(View.INVISIBLE);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_target_child = (TextView)findViewById(R.id.tv_target_child);
        tv_send_money = (TextView)findViewById(R.id.tv_send_money);
        tv_behalf_name = (TextView)findViewById(R.id.tv_behalf_name);

        tv_head.setText("Send Kr/Penger");
        tv_behalf_name.setText(behalf_type);

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images/ImageKid.jpg");
        Bitmap bmp = BitmapFactory.decodeFile(myDir.getAbsolutePath());
        iv_profile_pic.setImageBitmap(bmp);



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

        tv_target_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.no_internet));

                } else {

                    childDialogPopUp();

                }
            }
        });

        tv_send_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMoney();
            }
        });

    }

    private void sendMoney() {

        message = ed_message.getText().toString();
        amount = ed_amount.getText().toString();

        if (!cc.isConnectingToInternet()) {
            AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.no_internet));

        }else if (message.equals("")) {
            AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.enter_message));

            ed_message.requestFocus();
        } else if (amount.equals("")) {

            AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.enter_amount));
            ed_amount.requestFocus();
        } else if (child_name == null) {

            AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.targetd_child));

        }
        else {

            if (!cc.isConnectingToInternet()) {

                AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.no_internet));

            } else {

                sendMoneyWS(user_id,child_id,behalf_type,message,amount);

            }
        }
    }

    private void sendMoneyWS(final String user_id, final String child_id, final String behalf_type, final String message, final String amount) {

        pDialog = new ProgressDialog(SendMoneyActivity.this);
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

        dialog = new Dialog(SendMoneyActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.target_child_display);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);

        dialog.show();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        ImageView iv_close = (ImageView)dialog.findViewById(R.id.iv_close);

        rv_child_list = (RecyclerView) dialog.findViewById(R.id.rv_child_list);
        rv_child_list.setLayoutManager(new LinearLayoutManager(SendMoneyActivity.this));
        rv_child_list.setItemAnimator(new DefaultItemAnimator());

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(SendMoneyActivity.this, getString(R.string.no_internet));

        } else {

            getChildList();

        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            child_id = intent.getStringExtra("child_id");
            child_name = intent.getStringExtra("child_name");


            if(dialog != null){
                if(dialog.isShowing()){
                    tv_target_child.setText(child_name);
                    dialog.dismiss();
                }
            }

        }
    };


    public void getChildList() {

        pDialog = new ProgressDialog(SendMoneyActivity.this);
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

                                childAdapter = new ChildAdapter(child_list, R.layout.target_child_rv_item, SendMoneyActivity.this);
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
