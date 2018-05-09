package com.example.kidbank.kidbanknew.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.Network.AppController;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.Network.URL;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.Utility.AndyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal on 8/3/18.
 */

public class HomeActivityParent extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    CommanClass cc;

    ImageView iv_profile_link,iv_toolbar_back,iv_home;

    ImageView iv_penger,iv_oppdrag,iv_contact;

    ImageView iv_main_bg;

    TextView tv_user_name;

    ProgressDialog pDialog;

    String user_id;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_parent);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        user_id = cc.loadPrefString("userid");

        iv_profile_link = (ImageView)findViewById(R.id.iv_profile_link);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        iv_home.setImageResource(R.drawable.logout);


        tv_user_name = (TextView)findViewById(R.id.tv_user_name);

        iv_penger = (ImageView)findViewById(R.id.iv_penger);
        iv_oppdrag = (ImageView)findViewById(R.id.iv_oppdrag);
        iv_contact = (ImageView)findViewById(R.id.iv_contact);


        Glide.with(this)
                .load(R.drawable.mainbtn_pengar)
                .into(iv_penger);

        Glide.with(this)
                .load(R.drawable.ppdragsstyring)
                .into(iv_oppdrag);

        Glide.with(this)
                .load(R.drawable.contactbtn)
                .into(iv_contact);

        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);


        clickListener();
    }

    private void clickListener() {

        iv_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        iv_profile_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(HomeActivityParent.this, getString(R.string.no_internet));

                } else {

                    Intent intentProfile = new Intent(HomeActivityParent.this,EditProfile.class);
                    startActivity(intentProfile);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        });

        iv_penger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(HomeActivityParent.this, getString(R.string.no_internet));

                } else {

                    Intent intentPenger = new Intent(HomeActivityParent.this,SendKrActivity.class);
                    startActivity(intentPenger);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }

            }
        });


        iv_oppdrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(HomeActivityParent.this, getString(R.string.no_internet));

                } else {

                    Intent intentOppdrag = new Intent(HomeActivityParent.this,TaskManagerActivity.class);
                    startActivity(intentOppdrag);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                }

            }
        });

        iv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentKontakter = new Intent(HomeActivityParent.this,KontakterActivity.class);
                startActivity(intentKontakter);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               logoutDialog();
            }
        });
    }

    private void logoutDialog() {

        final Dialog dialog = new Dialog(HomeActivityParent.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);


        TextView tv_dialog_yes = (TextView) dialog.findViewById(R.id.tv_exit_yes);
        TextView tv_dialog_no = (TextView) dialog.findViewById(R.id.tv_exit_no);


        tv_dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cc.logoutapp();

                Intent intentLogin = new Intent(HomeActivityParent.this,LoginActivity.class);
                startActivity(intentLogin);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
                dialog.dismiss();
            }
        });

        tv_dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void getProfile(final String user_id) {

        pDialog = new ProgressDialog(HomeActivityParent.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_kid_get_profile,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:profile data", response);


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {

                                pDialog.dismiss();

                                JSONArray dataArray = jsonObject.getJSONArray("user_profile_data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject obj = dataArray.getJSONObject(i);

                                    tv_user_name.setText(obj.getString("name"));

                                    Glide.with(HomeActivityParent.this)
                                            .load(obj.getString("profile_pic"))
                                            .into(iv_profile_link);

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
    protected void onResume() {
        super.onResume();

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(HomeActivityParent.this, getString(R.string.no_internet));

        } else {

            getProfile(user_id);

        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        cc.showToast("Please click BACK again to exit");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;

            }
        }, 2000);
    }
}

