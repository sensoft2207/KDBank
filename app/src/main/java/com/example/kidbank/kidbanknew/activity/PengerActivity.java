package com.example.kidbank.kidbanknew.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.example.kidbank.kidbanknew.Model.Country;
import com.example.kidbank.kidbanknew.Model.MoneyGridData;
import com.example.kidbank.kidbanknew.Network.AppController;
import com.example.kidbank.kidbanknew.Network.CommanClass;
import com.example.kidbank.kidbanknew.Network.URL;
import com.example.kidbank.kidbanknew.R;
import com.example.kidbank.kidbanknew.Utility.AndyUtils;
import com.example.kidbank.kidbanknew.adapter.ChildAdapter;
import com.example.kidbank.kidbanknew.adapter.MoneyGridAdapter;
import com.example.kidbank.kidbanknew.dragevent.DragController;
import com.example.kidbank.kidbanknew.dragevent.DragLayer;
import com.jmedeisis.draglinearlayout.DragLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vishal on 7/3/18.
 */

public class PengerActivity extends AppCompatActivity implements View.OnTouchListener, View.OnDragListener, View.OnClickListener {


    CommanClass cc;

    ImageView iv_profile_pic,iv_main_bg,iv_toolbar_back,iv_home;

    TextView tv_head,tv_user_name,tv_balance,tv_no_money;

    GridView gridMoney;
    ArrayList<MoneyGridData> moneyList;

    MoneyGridAdapterFinal moneyGridAdapterFinal;

    int x_touch = 0;
    int y_touch = 0;

    private DragController mDragController;
    private DragLayer mDragLayer;

    ProgressDialog pDialog;

    String user_id;

    String money_id,amount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penger);

        init();
    }

    private void init() {

        cc = new CommanClass(this);

        user_id = cc.loadPrefString("userid");

        mDragController = new DragController(this);


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));


        iv_profile_pic = (ImageView)findViewById(R.id.iv_profile_pic);
        iv_main_bg = (ImageView)findViewById(R.id.iv_main_bg);
        iv_toolbar_back = (ImageView)findViewById(R.id.iv_toolbar_back);
        iv_home = (ImageView)findViewById(R.id.iv_home);

        tv_head = (TextView)findViewById(R.id.tv_head);
        tv_user_name = (TextView)findViewById(R.id.tv_user_name);
        tv_balance = (TextView)findViewById(R.id.tv_balance);
        tv_no_money = (TextView)findViewById(R.id.tv_no_money);

        tv_head.setText("Kr/Penger");
        tv_user_name.setText(cc.loadPrefString("name"));
        tv_balance.setText(cc.loadPrefString("balance"));



        Glide.with(this)
                .load(cc.loadPrefString("profile_pic"))
                .into(iv_profile_pic);


        Glide.with(this)
                .load(R.drawable.kidbank_screen2)
                .into(iv_main_bg);


        DragController dragController = mDragController;

        mDragLayer = (DragLayer) findViewById(R.id.fl_drag_view);
        mDragLayer.setOnDragListener(this);
        mDragLayer.setDragController(dragController);
        dragController.addDropTarget(mDragLayer);

        clickListner();


        if (!cc.isConnectingToInternet()) {

            AndyUtils.showToast(PengerActivity.this, getString(R.string.no_internet));

        } else {

            getKidMoneyListWS();

        }
    }

    private void getKidMoneyListWS() {

        pDialog = new ProgressDialog(PengerActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_kid_recieved_meney_list,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:piggy data", response);


                        moneyList = new ArrayList<MoneyGridData>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                JSONArray dataArray = jsonObject.getJSONArray("Recieved amount");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject jsonObject1 = dataArray.getJSONObject(i);

                                    if (jsonObject1.getString("pg_status").equals("Not Add")){

                                        MoneyGridData mData = new MoneyGridData();

                                        String money = jsonObject1.getString("amount");

                                        String[] separated = money.split("\\.");
                                        String moneyFinal = separated[0];
                                        String moneyFinal2 = separated[1];

                                        mData.setTitle(moneyFinal);
                                        mData.setMoney_id(jsonObject1.getString("id"));

                                        moneyList.add(mData);

                                    }else {

                                    }
                                }

                                if (moneyGridAdapterFinal != null) {
                                    moneyGridAdapterFinal = null;
                                }


                                gridMoney = (GridView) findViewById(R.id.gridMoney);
                                moneyGridAdapterFinal = new MoneyGridAdapterFinal(getApplication(),R.layout.grid_money_layout,moneyList);
                                gridMoney.setAdapter(moneyGridAdapterFinal);

                                if (moneyList.size() == 0){

                                    tv_no_money.setVisibility(View.VISIBLE);


                                }else {

                                    tv_no_money.setVisibility(View.INVISIBLE);

                                }


                            } else {
                                pDialog.dismiss();

                                tv_no_money.setVisibility(View.VISIBLE);

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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:

                break;

            case DragEvent.ACTION_DRAG_EXITED:

                break;

            case DragEvent.ACTION_DRAG_ENDED:

                break;

            case DragEvent.ACTION_DROP:

                Log.e("Drop", "Yes its Activity Drop");

                if (!cc.isConnectingToInternet()) {
                    AndyUtils.showToast(PengerActivity.this, getString(R.string.no_internet));

                }else if (money_id == null) {

                    AndyUtils.showToast(PengerActivity.this, getString(R.string.money_id));

                }else if (amount == null) {

                    AndyUtils.showToast(PengerActivity.this, getString(R.string.money_id));

                }
                else {

                    addMoneyToPiggyBank(user_id,money_id,amount);

                }

                break;
            default:
                break;
        }
        return true;
    }

    private void addMoneyToPiggyBank(final String user_id, final String money_id, final String amount) {

        pDialog = new ProgressDialog(PengerActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_add_to_pg,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:piggy add data", response);


                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                updateMoneyGrid();

                                cc.showToast("Money added successfully");

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
                params.put("send_money_id",money_id);
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

    private void updateMoneyGrid() {

        pDialog = new ProgressDialog(PengerActivity.this);
        pDialog.setMessage("Please Wait");
        pDialog.show();

        StringRequest jsonObjReq = new StringRequest(Request.Method.POST, URL.Url_kid_recieved_meney_list,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        Log.e("response:piggy data", response);


                        moneyList = new ArrayList<MoneyGridData>();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("status").equals("200")) {
                                pDialog.dismiss();

                                JSONArray dataArray = jsonObject.getJSONArray("Recieved amount");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    JSONObject jsonObject1 = dataArray.getJSONObject(i);

                                    if (jsonObject1.getString("pg_status").equals("Not Add")){

                                        MoneyGridData mData = new MoneyGridData();

                                        String money = jsonObject1.getString("amount");

                                        String[] separated = money.split("\\.");
                                        String moneyFinal = separated[0];
                                        String moneyFinal2 = separated[1];

                                        mData.setTitle(moneyFinal);
                                        mData.setMoney_id(jsonObject1.getString("id"));

                                        moneyList.add(mData);

                                    }else {

                                    }
                                }

                                if (moneyGridAdapterFinal != null) {
                                    moneyGridAdapterFinal = null;
                                }


                                gridMoney = (GridView) findViewById(R.id.gridMoney);
                                moneyGridAdapterFinal = new MoneyGridAdapterFinal(getApplication(),R.layout.grid_money_layout,moneyList);
                                gridMoney.setAdapter(moneyGridAdapterFinal);

                                if (moneyList.size() == 0){

                                    tv_no_money.setVisibility(View.VISIBLE);


                                }else {

                                    tv_no_money.setVisibility(View.INVISIBLE);

                                }


                            } else {
                                pDialog.dismiss();

                                tv_no_money.setVisibility(View.VISIBLE);

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
    public void onClick(final View view_main) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


class MoneyGridAdapterFinal extends ArrayAdapter<MoneyGridData> {

    Context context;

    int layoutResourceId;

    ArrayList<MoneyGridData> data = new ArrayList<MoneyGridData>();

    public MoneyGridAdapterFinal(Context context, int layoutResourceId,
                                 ArrayList<MoneyGridData> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;

        RecordHolder holder = null;

        if (row == null) {

            LayoutInflater inflater = (PengerActivity.this).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.tv_amount = (TextView) row.findViewById(R.id.tv_amount);
            holder.iv_currency = (ImageView) row.findViewById(R.id.iv_currency);
            holder.ln_iv_main = (LinearLayout) row.findViewById(R.id.ln_iv_main);
            row.setTag(holder);


            final View shape = holder.ln_iv_main;
            /*holder.ln_iv_main.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    final int item = position;
                    final DragData state = new DragData(item, shape.getWidth(), shape.getHeight());
                    final View.DragShadowBuilder shadow = new View.DragShadowBuilder(shape);
                    ViewCompat.startDragAndDrop(shape, null, shadow, state, 0);

                    getGridPos(position);

                    return true;
                }
            });*/

            holder.ln_iv_main.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    final int item = position;
                    final DragData state = new DragData(item, shape.getWidth(), shape.getHeight());
                    final View.DragShadowBuilder shadow = new View.DragShadowBuilder(shape);
                    ViewCompat.startDragAndDrop(shape, null, shadow, state, 0);

                    getGridPos(position);

                    return true;
                }
            });


        } else {
            holder = (RecordHolder) row.getTag();
        }

        MoneyGridData item = data.get(position);

        holder.tv_amount.setText("Kr"+" "+item.getTitle());

        return row;

    }

    private void getGridPos(int position) {

        MoneyGridData mdata = data.get(position);

        String money_id = mdata.getMoney_id();
        String amount = mdata.getTitle();

        Intent intent = new Intent("custom-event-name");
        intent.putExtra("money_id", money_id);
        intent.putExtra("amount", amount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }

    class RecordHolder {

        TextView tv_amount;
        ImageView iv_currency;
        LinearLayout ln_iv_main;

    }
}

public class DragData {

    public final int item;
    public final int width;
    public final int height;

    public DragData(int item, int width, int height) {
        this.item = item;
        this.width = width;
        this.height = height;
    }

}

    public boolean startDrag(View v) {

        Object obj = v;
        mDragController.startDrag(v, mDragLayer, obj, DragController.DRAG_ACTION_MOVE);

        return true;

    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        boolean handledHere = false;

        final int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                x_touch = (int) ev.getX();
                y_touch = (int) ev.getY();

                handledHere = startDrag(v);
                if (handledHere) {
                    v.performClick();
                }

                break;

            case MotionEvent.ACTION_UP:
                x_touch = (int) ev.getX();
                y_touch = (int) ev.getY();

                break;
        }

        return handledHere;
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            money_id = intent.getStringExtra("money_id");
            amount = intent.getStringExtra("amount");

        }
    };
}

