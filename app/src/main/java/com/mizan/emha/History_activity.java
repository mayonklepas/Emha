package com.mizan.emha;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Minami on 08/09/2017.
 */

public class History_activity extends AppCompatActivity {

    ImageView imgback;
    ProgressBar pb;
    TableLayout tbl;
    NumberFormat nf=NumberFormat.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        pb=(ProgressBar) findViewById(R.id.pb);
        tbl=(TableLayout) findViewById(R.id.tbl);
        imgback=(ImageView) findViewById(R.id.imgback);
        Bundle ex=getIntent().getExtras();
        loaddata(ex.getString("idtransaksi"));
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void loaddata(final String idtransaksi){
        pb.setVisibility(View.VISIBLE);
        RequestQueue rq= Volley.newRequestQueue(this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/historitransaksijurnal",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray ja=new JSONArray(response);
                            for (int i = 0; i < ja.length() ; i++) {
                                JSONObject jo=ja.getJSONObject(i);
                                TableRow tbr=new TableRow(History_activity.this);
                                tbr.setWeightSum(0);
                                tbr.setBackground(getResources().getDrawable(R.drawable.tableline));
                                TextView akun=new TextView(History_activity.this);
                                akun.setPadding(5,5,5,5);
                                akun.setWidth(1);
                                TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                                akun.setLayoutParams(params);
                                TextView debet=new TextView(History_activity.this);
                                debet.setPadding(5,5,5,5);
                                debet.setGravity(Gravity.RIGHT);
                                TextView kredit=new TextView(History_activity.this);
                                kredit.setPadding(5,5,5,5);
                                kredit.setGravity(Gravity.RIGHT);
                                akun.setText(jo.getString("KODEAKUN")+" "+jo.getString("NAMAAKUN"));
                                debet.setText(nf.format(jo.getDouble("DEBIT")));
                                kredit.setText(nf.format(jo.getDouble("KREDIT")));
                                tbr.addView(akun);
                                tbr.addView(debet);
                                tbr.addView(kredit);
                                tbl.addView(tbr, i+1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(History_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("idtransaksi",idtransaksi);
                return params;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(1000*15,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                pb.setVisibility(View.GONE);
            }
        });
    }

}
