package com.example.kidbank.kidbanknew.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.CountryAdapter;
import com.example.kidbank.kidbanknew.Model.Country;
import com.example.kidbank.kidbanknew.Network.AppController;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.Network.URL;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.Utility.AndroidMultiPartEntity;
import com.example.kidbank.kidbanknew.Utility.AndyUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by vishal on 6/3/18.
 */

public class EditProfile extends AppCompatActivity {

    CommanClass cc;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    long totalSize = 0;

    Calendar myCalendar = Calendar.getInstance();

    EditText ed_alias,ed_name,ed_birthdate;

    LinearLayout ln_country_popup,ln_ok_profile_update;

    TextView tv_country_name,tv_user_name,tv_balance;

    CircleImageView iv_profile_pic;

    public Dialog dialog;
    ProgressDialog pDialog;

    RecyclerView rv_country_list;
    ArrayList<Country> country_list;
    CountryAdapter countryAdapter;

    ImageView iv_main_bg,iv_toolbar_back,iv_home;

    String country_id, country_code, country_name, user_id,birthdate,name,alias,pic_first_time;

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        cc = new CommanClass(this);

        init();
    }

    private void init() {

        user_id = cc.loadPrefString("userid");

        ln_country_popup = (LinearLayout) findViewById(R.id.ln_country_popup);
        ln_ok_profile_update = (LinearLayout) findViewById(R.id.ln_ok_profile_update);

        tv_country_name = (TextView) findViewById(R.id.tv_country_name);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_balance = (TextView) findViewById(R.id.tv_balance);

        iv_profile_pic = (CircleImageView) findViewById(R.id.iv_profile_pic);
        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        ed_alias = (EditText)findViewById(R.id.ed_alias);
        ed_name = (EditText)findViewById(R.id.ed_name);
        ed_birthdate = (EditText)findViewById(R.id.ed_birthdate);


        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);


        clickListner();

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(EditProfile.this, getString(R.string.no_internet));

        } else {

            getProfile(user_id);

        }
    }

    private void clickListner() {

        iv_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectfile();
            }
        });

        ed_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(EditProfile.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ln_country_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(EditProfile.this, getString(R.string.no_internet));

                } else {

                    countryDialogPopUp();

                }
            }
        });

        iv_toolbar_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        ln_ok_profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(EditProfile.this, getString(R.string.no_internet));

                } else {

                    updateProfile();

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
    }

    private void updateProfile() {

        alias = ed_alias.getText().toString();
        name = ed_name.getText().toString();
        birthdate = ed_birthdate.getText().toString();

        changeDateFormate(birthdate);


        new UploadFileToServer(alias,name,birthdate,user_id,country_id).execute();

    }

    public String changeDateFormate(String date_input) {
        String inputPattern = "dd/MM/yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(date_input);
            str = outputFormat.format(date);

            birthdate = str;

            Log.e("Birthdate", birthdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void countryDialogPopUp() {

        dialog = new Dialog(EditProfile.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.country_list_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, ActionBar.LayoutParams.WRAP_CONTENT);

        dialog.show();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        ImageView iv_close = (ImageView)dialog.findViewById(R.id.iv_close);

        rv_country_list = (RecyclerView) dialog.findViewById(R.id.rv_country_list);
        rv_country_list.setLayoutManager(new LinearLayoutManager(EditProfile.this));
        rv_country_list.setItemAnimator(new DefaultItemAnimator());

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(EditProfile.this, getString(R.string.no_internet));

        } else {

            getCountryList();

        }

    }

    private void getProfile(final String user_id) {

        pDialog = new ProgressDialog(EditProfile.this);
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

                                    country_code = obj.getString("country_code");
                                    country_id = obj.getString("country_id");
                                    pic_first_time = obj.getString("profile_pic");

                                    String birthdate = obj.getString("birthdate");

                                    String inputPattern = "yyyy-MM-dd";
                                    String outputPattern = "dd/MM/yyyy";
                                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                    Date date = null;
                                    String str = null;

                                    try {
                                        date = inputFormat.parse(birthdate);
                                        str = outputFormat.format(date);



                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    tv_country_name.setText(country_code);
                                    tv_user_name.setText(obj.getString("name"));
                                    tv_balance.setText(obj.getString("balance"));

                                    ed_alias.setText(obj.getString("user_name"));
                                    ed_name.setText(obj.getString("name"));
                                    ed_birthdate.setText(str);

                                    Glide.with(EditProfile.this)
                                            .load(obj.getString("profile_pic"))
                                            .into(iv_profile_pic);


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



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            country_id = intent.getStringExtra("country_id");
            country_code = intent.getStringExtra("country_code");
            country_name = intent.getStringExtra("country_name");

            if(dialog != null){
                if(dialog.isShowing()){
                    tv_country_name.setText(country_name);
                    dialog.dismiss();
                }
            }

        }
    };

    public void getCountryList() {

        pDialog = new ProgressDialog(EditProfile.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(com.android.volley.Request.Method.GET, URL.Url_get_country,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:country data", response);


                        country_list = new ArrayList<>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                JSONArray dataArray = jsonObject.getJSONArray("country_data");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject jsonObject1 = dataArray.getJSONObject(i);


                                    Country c_model = new Country();

                                    c_model.setId(jsonObject1.getString("id"));
                                    c_model.setCountry_code(jsonObject1.getString("country_code"));
                                    c_model.setCountry_name(jsonObject1.getString("nicename"));

                                    country_list.add(c_model);
                                }

                                if (countryAdapter != null) {
                                    countryAdapter = null;
                                }

                                countryAdapter = new CountryAdapter(country_list, R.layout.country_list_rv_item, EditProfile.this);
                                rv_country_list.setAdapter(countryAdapter);


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
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "delta141forceSEAL8PARA9MARCOSBRAHMOS");
                Log.i("request header", headers.toString());
                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(jsonObjReq, "Temp");

    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ed_birthdate.setText(sdf.format(myCalendar.getTime()));
    }

    private void selectfile() {
        try {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, SELECT_PICTURE);


        } catch (Exception e) {
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == EditProfile.this.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();

                try {
                    selectedImagePath = getPath(selectedImageUri);
                    Log.e("Selected File", selectedImagePath);

                    ExifInterface ei = null;
                    Bitmap mybitmap = null;
                    Bitmap retVal = null;
                    try {
                        ei = new ExifInterface(selectedImagePath);
                        mybitmap = BitmapFactory.decodeFile(selectedImagePath);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Matrix matrix = new Matrix();
                    int orientation = ei.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_UNDEFINED);
                    Log.e("Oriention", orientation + "");

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_NORMAL:
                            matrix.postRotate(0);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;

                        case ExifInterface.ORIENTATION_ROTATE_90:

                            matrix.postRotate(90);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:

                            matrix.postRotate(180);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:

                            matrix.postRotate(270);
                            retVal = Bitmap.createBitmap(mybitmap, 0, 0,
                                    mybitmap.getWidth(), mybitmap.getHeight(),
                                    matrix, true);
                            break;

                    }

                    File file = new File(selectedImagePath);
                    long fileSizeInBytes = file.length();

                    long fileSizeInKB = fileSizeInBytes / 1024;

                    long fileSizeInMB = fileSizeInKB / 1024;

                    if (fileSizeInMB > 10) {
                        selectedImagePath = "";
                        new AlertDialog.Builder(EditProfile.this)
                                .setMessage("You can't upload more than 10 MB file")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .show();
                    } else {
                        Uri uri = Uri.fromFile(new File(selectedImagePath));

                        Glide.with(EditProfile.this)
                                .load(uri)
                                .into(iv_profile_pic);

                        if (orientation != 0) {

                            GenerateImage(retVal);

                        }

                    }


                } catch (URISyntaxException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public String getPath(Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = EditProfile.this.getContentResolver().query(uri,
                        projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void GenerateImage(Bitmap bm) {

        OutputStream fOut = null;
        Uri outputFileUri;
        try {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, "MIP.jpg");
            outputFileUri = Uri.fromFile(file);
            fOut = new FileOutputStream(file);
        } catch (Exception e) {

        }
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
        }

        File path = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "KID.jpg");
        selectedImagePath = file.toString();

        Log.e("PATHH",selectedImagePath);


    }


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        HttpClient httpclient;
        HttpPost httppost;
        String alias, name, birthdate, user_id, country_id;



        public UploadFileToServer(String alias, String name, String birthdate, String user_id,
                                  String country_id) {

            this.alias = alias;
            this.name = name;
            this.birthdate = birthdate;
            this.user_id = user_id;
            this.country_id = country_id;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProfile.this);
            pDialog.show();
            pDialog.setCancelable(false);

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            pDialog.setMessage(String.valueOf("Loading..." + progress[0])
                    + " %");

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            httpclient = new DefaultHttpClient();
            httppost = new HttpPost(URL.Url_kid_update_profile);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (selectedImagePath == null){

                    entity.addPart("profile_pic", new StringBody(pic_first_time));

                }else {

                    if (selectedImagePath != null || !selectedImagePath.equals("")) {
                        File sourceFile = new File(selectedImagePath);
                        entity.addPart("profile_pic", new FileBody(sourceFile));
                    }
                }

                entity.addPart("name", new StringBody(name));
                entity.addPart("birthdate", new StringBody(birthdate));
                entity.addPart("userid", new StringBody(user_id));
                entity.addPart("country", new StringBody(country_id));

                httppost.addHeader("Authorization", "delta141forceSEAL8PARA9MARCOSBRAHMOS");
                httppost.addHeader("UserAuth", cc.loadPrefString("api_token"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();


                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    return responseString;
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Edit: result", "Response from server: " + result);
            try {
                pDialog.dismiss();
                JSONObject jObject = new JSONObject(result);
                if (jObject.getString("status").equals("200")) {

                    cc.showToast(jObject.getString("message"));

                    getProfileUpdate(user_id);

                } else {
                    cc.showToast(jObject.getString("message"));
                }

            } catch (JSONException e) {
                Log.e("Error : Exception", e.getMessage());
            }
        }

    }

    private void getProfileUpdate(final String user_id) {

        pDialog = new ProgressDialog(EditProfile.this);
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

                                    country_code = obj.getString("country_code");
                                    country_id = obj.getString("country_id");
                                    pic_first_time = obj.getString("profile_pic");

                                    String birthdate = obj.getString("birthdate");

                                    String inputPattern = "yyyy-MM-dd";
                                    String outputPattern = "dd/MM/yyyy";
                                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                    Date date = null;
                                    String str = null;

                                    try {
                                        date = inputFormat.parse(birthdate);
                                        str = outputFormat.format(date);



                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }



                                    tv_country_name.setText(country_code);
                                    tv_user_name.setText(obj.getString("name"));
                                    tv_balance.setText(obj.getString("balance"));

                                    ed_alias.setText(obj.getString("user_name"));
                                    ed_name.setText(obj.getString("name"));
                                    ed_birthdate.setText(str);


                                    Glide.with(EditProfile.this)
                                            .load(obj.getString("profile_pic"))
                                            .into(iv_profile_pic);

                                    cc.savePrefString("name", obj.getString("name"));
                                    cc.savePrefString("profile_pic", obj.getString("profile_pic"));
                                    cc.savePrefString("balance", obj.getString("balance"));

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
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
