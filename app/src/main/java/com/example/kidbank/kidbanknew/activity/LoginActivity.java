package com.example.kidbank.kidbanknew.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    CommanClass cc;

    LinearLayout ln_login, ln_signup;

    EditText et_login_username, et_login_password;

    ImageView iv_heading,iv_footer;

    ImageView iv_main_bg;

    ProgressDialog pDialog;

    int PERMISSION_ALL = 1;

    String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cc = new CommanClass(this);

        deviceId= Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            init();
        }
    }

    private void init() {

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);

        iv_heading = (ImageView)findViewById(R.id.iv_heading);
        iv_footer = (ImageView)findViewById(R.id.iv_footer);

        ln_login = (LinearLayout) findViewById(R.id.ln_login);
        ln_signup = (LinearLayout) findViewById(R.id.ln_signup);

        et_login_username = (EditText) findViewById(R.id.et_login_username);
        et_login_password = (EditText) findViewById(R.id.et_login_password);

        Glide.with(this)
                .load(R.drawable.logo)
                .into(iv_heading);

        Glide.with(this)
                .load(R.drawable.welcome)
                .into(iv_footer);

        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);



        clickListner();

        if (cc.loadPrefString("isLogin").equals("loginKid")){

            Intent intentHomeKid = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(intentHomeKid);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();

        }else if (cc.loadPrefString("isLogin").equals("loginParent")){

            Intent intentHomeParent = new Intent(LoginActivity.this,HomeActivityParent.class);
            startActivity(intentHomeParent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
        }else {

        }

    }

    private void clickListner() {

        ln_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(LoginActivity.this, getString(R.string.no_internet));

                } else {

                    if (isValidate()) {

                        makeJsonCallForLogin(et_login_username.getText().toString(),et_login_password.getText().toString(),deviceId);
                    }


                }
            }
        });
    }

    private boolean isValidate() {
        boolean validate = false;

        if (et_login_username.getText().toString().isEmpty()) {
            cc.showToast("Please Enter UserName");
        } else if (et_login_password.getText().toString().isEmpty()) {
            cc.showToast("Please Enter Password");
        } else {
            validate = true;
        }

        return validate;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    showErrorDialog();
                }
                break;
        }
    }

    public void showErrorDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setTitle(getResources().getString(R.string.app_name));
        alert.setMessage(getResources().getString(R.string.permission_Req));
        alert.setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (!hasPermissions(LoginActivity.this, PERMISSIONS)) {
                    ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, PERMISSION_ALL);
                }
            }
        });
        alert.show();
    }

    private void makeJsonCallForLogin(final String userId, final String password, final String deviceId) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_login,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("response:login", response);
                        try {
                            String status = "";
                            String message = "";

                            pDialog.dismiss();
                            JSONObject jObject = new JSONObject(response);

                            message = jObject.getString("message");
                            status = jObject.getString("status");
                            if (status.equals("200")) {

                                JSONObject obj = jObject.getJSONObject("user_data");

                                cc.showToast(message);

                                cc.savePrefString("userid", obj.getString("userid"));
                                cc.savePrefString("user_name", obj.getString("user_name"));
                                cc.savePrefString("name", obj.getString("name"));
                                cc.savePrefString("user_type", obj.getString("user_type"));
                                cc.savePrefString("api_token", obj.getString("api_token"));
                                cc.savePrefString("profile_pic", obj.getString("profile_pic"));
                                cc.savePrefString("balance", obj.getString("balance"));

                                Log.e("profile_pic",obj.getString("profile_pic"));

                                if (obj.getString("user_type").equals("Kid")){

                                    Intent intentHomeKid = new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(intentHomeKid);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    finish();
                                    cc.savePrefString("isLogin","loginKid");

                                }else {

                                    Intent intentHomeParent = new Intent(LoginActivity.this,HomeActivityParent.class);
                                    startActivity(intentHomeParent);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    finish();
                                    cc.savePrefString("isLogin","loginParent");

                                }

                            } else {
                                cc.showToast(message);
                            }

                        } catch (JSONException e) {
                            Log.e("Error : Exception", e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                map.put("user_name", userId);
                map.put("password", password);
                map.put("device_type", "Android");
                map.put("notify_id", deviceId);

                Log.i("@@request lOGIN", map.toString());

                return map;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "delta141forceSEAL8PARA9MARCOSBRAHMOS");
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

}
