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
import com.example.kidbank.kidbanknew.adapter.ChildAdapter;

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

/**
 * Created by vishal on 8/3/18.
 */

public class AddTaskActivity extends AppCompatActivity {

    CommanClass cc;

    private static final int SELECT_PICTURE = 1;

    private String selectedImagePath;
    long totalSize = 0;

    public Dialog dialog;
    ProgressDialog pDialog;

    Calendar myCalendar = Calendar.getInstance();

    ImageView iv_main_bg,iv_toolbar_back,iv_profile_pic,iv_home;

    EditText ed_task_name,ed_set_value,ed_deadline;

    TextView tv_head,tv_target_child,tv_submit_add_task;

    RecyclerView rv_child_list;
    ArrayList<Country> child_list;
    ChildAdapter childAdapter;

    String user_id,child_id,child_name,task_name,set_value,deadline,targeted_child;


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
        setContentView(R.layout.activity_add_task);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        user_id = cc.loadPrefString("userid");

        Log.e("@@userid",user_id);

        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_profile_pic = (ImageView)findViewById(R.id.iv_profile_pic);
        iv_home = (ImageView)findViewById(R.id.iv_home);
        iv_home.setVisibility(View.INVISIBLE);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_target_child = (TextView)findViewById(R.id.tv_target_child);
        tv_submit_add_task = (TextView)findViewById(R.id.tv_submit_add_task);

        ed_task_name = (EditText) findViewById(R.id.ed_task_name);
        ed_set_value = (EditText) findViewById(R.id.ed_set_value);
        ed_deadline = (EditText) findViewById(R.id.ed_deadline);

        tv_head.setText("Add Task");

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

        ed_deadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(AddTaskActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        iv_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectfile();
            }
        });

        tv_target_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cc.isConnectingToInternet()) {

                    AndyUtils.showToast(AddTaskActivity.this, getString(R.string.no_internet));

                } else {

                    childDialogPopUp();

                }
            }
        });

        tv_submit_add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addTaskWS();

            }
        });
    }

    private void addTaskWS() {

        task_name = ed_task_name.getText().toString();
        set_value = ed_set_value.getText().toString();
        deadline = ed_deadline.getText().toString();

        changeDateFormate(deadline);

        if (!cc.isConnectingToInternet()) {
            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.no_internet));

        }else if (selectedImagePath == null) {

            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.profile_pic));

        }else if (task_name.equals("")) {
            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.task_name));

            ed_task_name.requestFocus();
        } else if (set_value.equals("")) {

            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.set_value));
            ed_set_value.requestFocus();
        } else if (deadline.equals("")) {

            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.deadline));

        }else if (child_name == null) {

            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.targetd_child));

        }
        else {

            new UploadFileToServer(task_name,set_value,deadline,user_id,child_id).execute();
        }

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

            deadline = str;

            Log.e("Deadline", deadline);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
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
        if (resultCode == AddTaskActivity.this.RESULT_OK) {
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
                        new AlertDialog.Builder(AddTaskActivity.this)
                                .setMessage("You can't upload more than 10 MB file")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })

                                .show();
                    } else {
                        Uri uri = Uri.fromFile(new File(selectedImagePath));

                        Glide.with(AddTaskActivity.this)
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
                cursor = AddTaskActivity.this.getContentResolver().query(uri,
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


    }


    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        ed_deadline.setText(sdf.format(myCalendar.getTime()));
    }

    private void childDialogPopUp() {

        dialog = new Dialog(AddTaskActivity.this);

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
        rv_child_list.setLayoutManager(new LinearLayoutManager(AddTaskActivity.this));
        rv_child_list.setItemAnimator(new DefaultItemAnimator());

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(AddTaskActivity.this, getString(R.string.no_internet));

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

        pDialog = new ProgressDialog(AddTaskActivity.this);
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

                                childAdapter = new ChildAdapter(child_list, R.layout.target_child_rv_item, AddTaskActivity.this);
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


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        HttpClient httpclient;
        HttpPost httppost;
        String task_name, set_value, deadline, user_id, child_id;



        public UploadFileToServer(String task_name, String set_value, String deadline, String user_id,
                                  String child_id) {

            this.task_name = task_name;
            this.set_value = set_value;
            this.deadline = deadline;
            this.user_id = user_id;
            this.child_id = child_id;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddTaskActivity.this);
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
            httppost = new HttpPost(URL.Url_parent_add_task);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (selectedImagePath != null || !selectedImagePath.equals("")) {
                    File sourceFile = new File(selectedImagePath);
                    entity.addPart("task_pic", new FileBody(sourceFile));
                }

                entity.addPart("task_name", new StringBody(task_name));
                entity.addPart("salary_amount", new StringBody(set_value));
                entity.addPart("last_date", new StringBody(deadline));
                entity.addPart("from_parentuser_id", new StringBody(user_id));
                entity.addPart("to_kiduser_id", new StringBody(child_id));

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

                    onBackPressed();

                } else {
                    cc.showToast(jObject.getString("message"));
                }

            } catch (JSONException e) {
                Log.e("Error : Exception", e.getMessage());
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
