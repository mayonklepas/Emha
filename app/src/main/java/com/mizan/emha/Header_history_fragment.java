package com.mizan.emha;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Minami on 08/09/2017.
 */

public class Header_history_fragment extends Fragment {

    RecyclerView rv;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;
    Header_history_adapter hadapter;
    Typeface tp;
    Button filter;
    String tanggalpick;

    ArrayList<String> idtransaksi=new ArrayList<>();
    ArrayList<String> noref=new ArrayList<>();
    ArrayList<String> nopol=new ArrayList<>();
    ArrayList<String> tanggal=new ArrayList<>();
    ArrayList<String> keterangan=new ArrayList<>();
    ArrayList<String> nama=new ArrayList<>();
    ArrayList<Double> total=new ArrayList<>();
    int cpage=0;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_header_history,container,false);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        rv=(RecyclerView) v.findViewById(R.id.rv);
        filter=(Button) v.findViewById(R.id.filter);
        layman=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        hadapter=new Header_history_adapter(idtransaksi,tanggal,noref,nopol,keterangan,nama,total,getActivity());
        rv.setAdapter(hadapter);
        loaddata();
        rv.addOnScrollListener(new Endlessscrolllinear() {
            @Override
            public void onLoadMore() {
                loaddata();
                cpage++;
            }
        });
        datepickeract();

        return v;
    }


    private DatePickerDialog dpd(){
        Calendar c=Calendar.getInstance();
        DatePickerDialog dpdin=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                DateFormat displayFormat = new SimpleDateFormat("d MMMM , yyyy", Locale.ENGLISH);
                Date skrgdate=null;
                try {
                    skrgdate=new SimpleDateFormat("yyyy-MM-dd").parse(i+"-"+(i1+1)+"-"+i2);
                } catch (ParseException e) {
                    e.printStackTrace();
                };
                filter.setText(displayFormat.format(skrgdate));
                cpage=0;
                cleardata();
                rv.setAdapter(hadapter);
                tanggalpick=i+"-"+(i1+1)+"-"+i2;
                loaddatacari(tanggalpick);
                rv.addOnScrollListener(new Endlessscrolllinear() {
                    @Override
                    public void onLoadMore() {
                        loaddatacari(tanggalpick);
                        cpage++;
                    }
                });


            }
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
        return dpdin;
    }

    private void datepickeract(){
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dpd().show();
            }
        });

    }

    private void cleardata(){
        idtransaksi.clear();
        tanggal.clear();
        noref.clear();
        keterangan.clear();
        nama.clear();
        total.clear();
        hadapter.notifyDataSetChanged();
    }

    private void loaddata(){
        pb.setVisibility(View.VISIBLE);
                RequestQueue rq= Volley.newRequestQueue(getActivity());
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/historitransaksi",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    System.out.println(response);
                                    JSONArray ja=new JSONArray(response);
                                    for (int i = 0; i < ja.length() ; i++) {
                                        JSONObject jo=ja.getJSONObject(i);
                                        idtransaksi.add(jo.getString("IDTRANSAKSI"));
                                        noref.add(jo.getString("NOREF"));
                                        nopol.add(jo.getString("NOPOL"));
                                        tanggal.add(jo.getString("TANGGAL"));
                                        keterangan.add(jo.getString("KETERANGAN"));
                                        nama.add(jo.getString("NAMA"));
                                        total.add(jo.getDouble("TOTAL"));

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params=new HashMap<String, String>();
                        params.put("tanggal",new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                        params.put("halaman", String.valueOf(cpage));
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
                        hadapter.notifyDataSetChanged();
                        if(idtransaksi.size()==0){
                            //Snackbar.make(v,"History Kosong",3*1000).show();
                            Toast.makeText(getActivity(), "History Kosong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

    private void loaddatacari(final String tanggalsekarang){
        pb.setVisibility(View.VISIBLE);
        RequestQueue rq= Volley.newRequestQueue(getActivity());
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/historitransaksi",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println(response);
                            JSONArray ja=new JSONArray(response);
                            for (int i = 0; i < ja.length() ; i++) {
                                JSONObject jo=ja.getJSONObject(i);
                                idtransaksi.add(jo.getString("IDTRANSAKSI"));
                                noref.add(jo.getString("NOREF"));
                                nopol.add(jo.getString("NOPOL"));
                                tanggal.add(jo.getString("TANGGAL"));
                                keterangan.add(jo.getString("KETERANGAN"));
                                nama.add(jo.getString("NAMA"));
                                total.add(jo.getDouble("TOTAL"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params=new HashMap<String, String>();
                params.put("tanggal",tanggalsekarang);
                params.put("halaman", String.valueOf(cpage));
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
                hadapter.notifyDataSetChanged();
                if(idtransaksi.size()==0){
                    //Snackbar.make(v,"History Kosong",3*1000).show();
                    Toast.makeText(getActivity(), "History Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
