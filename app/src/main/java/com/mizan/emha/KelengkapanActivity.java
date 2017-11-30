package com.mizan.emha;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KelengkapanActivity extends AppCompatActivity {

    RecyclerView rv;
    Kelengkapanadapter kadapter;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;
    ArrayList<String> gambar=new ArrayList<>();
    ArrayList<String> keterangan=new ArrayList<>();
    ImageView imgtambah,imgback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelengkapan);
        pb=(ProgressBar) findViewById(R.id.pb);
        rv=(RecyclerView) findViewById(R.id.rv);
        imgtambah=(ImageView) findViewById(R.id.imgtambah);
        imgback=(ImageView) findViewById(R.id.imgback);
        layman=new LinearLayoutManager(this);
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        kadapter=new Kelengkapanadapter(gambar,keterangan,this);
        rv.setAdapter(kadapter);
        Bundle ex=getIntent().getExtras();
        final String idmobil=ex.getString("idmobil");
        final String tipe=ex.getString("tipe");
        loaddata(idmobil,tipe);
        imgtambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(KelengkapanActivity.this,UploadActivity.class);
                i.putExtra("idmobil",idmobil);
                i.putExtra("tipe", tipe);
                startActivity(i);
                finish();
            }
        });
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void loaddata(final String idmobil, final String tipe){
        pb.setVisibility(View.VISIBLE);
        RequestQueue rq= Volley.newRequestQueue(KelengkapanActivity.this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/downloadimage",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONArray ja=new JSONArray(response);
                            for (int i = 0; i < ja.length() ; i++) {
                                JSONObject jo=ja.getJSONObject(i);
                                keterangan.add(jo.getString("KETERANGAN"));
                                gambar.add(Config.urlkelengkapan+"/"+jo.getString("LOKASI")+"/"+jo.getString("ID_IMAGE")+".jpg");
                                System.out.println(Config.urlkelengkapan+"/"+jo.getString("LOKASI")+"/"+jo.getString("ID_IMAGE")+".jpg");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(KelengkapanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<>();
                params.put("idmobil",idmobil);
                params.put("tipe", tipe);
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
                kadapter.notifyDataSetChanged();
                if(gambar.size()==0){
                    Toast.makeText(KelengkapanActivity.this,"Data Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
