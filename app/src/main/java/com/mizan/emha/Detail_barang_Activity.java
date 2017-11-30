package com.mizan.emha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Detail_barang_Activity extends AppCompatActivity {

    TextView nopol,pemilik,alamat,merek,tipe,tahun,warna,bbm,norangka,nobpkb,nomesin,berlaku_stnk,berlaku_pajak,
    harga_beli,harga_pokok,harga_jual;
    String snopol,spemilik,salamat,smerek,stipe,stahun,swarna,sbbm,snorangka,snobpkb,snomesin,sberlaku_stnk,sberlaku_pajak,
            sharga_beli,sharga_pokok,sharga_jual,simage;
    ImageView app_bar_image;
    Button kendaraan,berkas;
    NumberFormat nf=NumberFormat.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang_);
        nopol=(TextView) findViewById(R.id.nopol);
        pemilik=(TextView) findViewById(R.id.pemilik);
        alamat=(TextView) findViewById(R.id.alamat);
        merek=(TextView) findViewById(R.id.merek);
        tipe=(TextView) findViewById(R.id.tipe);
        tahun=(TextView) findViewById(R.id.tahun);
        warna=(TextView) findViewById(R.id.warna);
        bbm=(TextView) findViewById(R.id.bbm);
        norangka=(TextView) findViewById(R.id.norangka);
        nobpkb=(TextView) findViewById(R.id.nobpkb);
        nomesin=(TextView) findViewById(R.id.nomesin);
        berlaku_stnk=(TextView) findViewById(R.id.berlaku_stnk);
        berlaku_pajak=(TextView) findViewById(R.id.berlaku_pajak);
        harga_beli=(TextView) findViewById(R.id.harga_beli);
        harga_pokok=(TextView) findViewById(R.id.harga_pokok);
        harga_jual=(TextView) findViewById(R.id.harga_jual);

        kendaraan=(Button) findViewById(R.id.kendaraan);
        berkas=(Button) findViewById(R.id.berkas);

        app_bar_image=(ImageView) findViewById(R.id.app_bar_image);
        Bundle ex=getIntent().getExtras();
        final String idmobil=ex.getString("idmobil");
        loaddata(idmobil);

        kendaraan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Detail_barang_Activity.this, KelengkapanActivity.class);
                i.putExtra("idmobil",idmobil);
                i.putExtra("tipe", "100");
                startActivity(i);
            }
        });

        berkas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Detail_barang_Activity.this, KelengkapanActivity.class);
                i.putExtra("idmobil",idmobil);
                i.putExtra("tipe", "101");
                startActivity(i);
            }
        });

    }


    private void loaddata(final String idmobil){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setMessage("Memuat Data...");
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(Detail_barang_Activity.this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/kendaraandetail",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONArray ja=new JSONArray(response);
                            for (int i = 0; i < ja.length() ; i++) {
                                JSONObject jo=ja.getJSONObject(i);
                                snopol=jo.getString("NOPOL");
                                spemilik=jo.getString("NAMAPEMILIK");
                                salamat=jo.getString("ALAMATPEMILIK");
                                smerek=jo.getString("MEREK");
                                stipe=jo.getString("TIPE");
                                stahun=jo.getString("TAHUN");
                                swarna=jo.getString("WARNA");
                                sbbm=jo.getString("NAMABBM");
                                snorangka=jo.getString("NO_RANGKA");
                                snobpkb=jo.getString("NO_BPKB");
                                snomesin=jo.getString("NO_MESIN");
                                sberlaku_stnk=jo.getString("MASA_BERLAKU_STNK");
                                sberlaku_pajak=jo.getString("MASA_BERLAKU_PAJAK_STNK");
                                sharga_beli=nf.format(jo.getDouble("HARGABELI"));
                                sharga_pokok=nf.format(jo.getDouble("HARGAPOKOK"));
                                sharga_jual=nf.format(jo.getDouble("HARGAJUAL"));
                                simage=jo.getString("ID_IMAGE");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Detail_barang_Activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("idmobil",idmobil);
                return params;
            }
        };
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                nopol.setText(snopol);
                pemilik.setText(spemilik);
                alamat.setText(salamat);
                merek.setText(smerek);
                tipe.setText(stipe);
                tahun.setText(stahun);
                warna.setText(swarna);
                bbm.setText(sbbm);
                norangka.setText(snorangka);
                nobpkb.setText(snobpkb);
                nomesin.setText(snomesin);
                berlaku_stnk.setText(sberlaku_stnk);
                berlaku_pajak.setText(sberlaku_pajak);
                harga_beli.setText(sharga_beli);
                harga_pokok.setText(sharga_pokok);
                harga_jual.setText(sharga_jual);
                Glide.with(Detail_barang_Activity.this).
                        load(Config.urlgambar+"/"+simage+".jpg").
                        diskCacheStrategy(DiskCacheStrategy.ALL).
                        crossFade().centerCrop().placeholder(R.drawable.placeholder).
                        into(app_bar_image);
                pd.dismiss();
            }
        });

    }

}
