package com.mizan.emha;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Minami on 08/09/2017.
 */

public class Barang_fragment extends Fragment {

    SearchView sv;
    RecyclerView rv;
    RecyclerView.LayoutManager layman;
    ProgressBar pb;
    Barang_adapter badapter;
    Typeface tp;

    ArrayList<String> id_mobil=new ArrayList<>();
    ArrayList<String> nopol=new ArrayList<>();
    ArrayList<String> merk=new ArrayList<>();
    ArrayList<String> tipe=new ArrayList<>();
    ArrayList<String> tahun=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> warna=new ArrayList<>();
    ArrayList<String> img_mobil=new ArrayList<>();

    int cpage=0;
    String keyword;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_barang,container,false);
        sv=(SearchView) v.findViewById(R.id.sv);
        pb=(ProgressBar) v.findViewById(R.id.pb);
        rv=(RecyclerView) v.findViewById(R.id.rv);
        layman=new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layman);
        rv.setHasFixedSize(true);
        rv.setItemAnimator(new DefaultItemAnimator());
        badapter=new Barang_adapter(id_mobil,nopol,merk,tipe,tahun,status,warna,img_mobil,getActivity().getApplicationContext());
        rv.setAdapter(badapter);
        loaddata();
        rv.addOnScrollListener(new Endlessscrolllinear() {
            @Override
            public void onLoadMore() {
                if (keyword==null || keyword.isEmpty()){
                    loaddata();
                    cpage++;
                }else{
                    caridata(keyword);
                    cpage++;
                }

            }
        });

        sv.setQueryHint("Cari Barang");
        sv.setIconifiedByDefault(true);
        sv.setSubmitButtonEnabled(false);
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        SearchView.OnQueryTextListener quelist=new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                keyword=query;
                cpage=0;
                cleardata();
                rv.setAdapter(badapter);
                caridata(keyword);
                rv.addOnScrollListener(new Endlessscrolllinear() {
                    @Override
                    public void onLoadMore() {
                        if (keyword==null || keyword.isEmpty()){
                            loaddata();
                            cpage++;
                        }else{
                            caridata(keyword);
                            cpage++;
                        }
                    }
                });
                sv.setIconified(true);
                sv.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };

        sv.setOnQueryTextListener(quelist);

        return v;
    }


    private void cleardata(){
        id_mobil.clear();
        nopol.clear();
        merk.clear();
        tipe.clear();
        tahun.clear();
        status.clear();
        warna.clear();
        img_mobil.clear();
        badapter.notifyDataSetChanged();
    }

    private void loaddata(){
        pb.setVisibility(View.VISIBLE);
                RequestQueue rq= Volley.newRequestQueue(getActivity());
                StringRequest sr=new StringRequest(Request.Method.POST, Config.url+"/daftarkendaraan",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                    JSONArray ja=new JSONArray(response);

                                    for (int i = 0; i < ja.length() ; i++) {
                                        JSONObject jo=ja.getJSONObject(i);
                                        id_mobil.add(jo.getString("IDMOBIL"));
                                        nopol.add(jo.getString("NOPOL"));
                                        merk.add(jo.getString("MEREK"));
                                        tipe.add(jo.getString("TIPE"));
                                        tahun.add(jo.getString("TAHUN"));
                                        status.add(jo.getString("STATUS"));
                                        warna.add(jo.getString("WARNA"));
                                        img_mobil.add(Config.url+"/barang/"+jo.getString("ID_IMAGE")+".jpg");

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
                        params.put("pageinv", String.valueOf(cpage));
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
                        badapter.notifyDataSetChanged();
                        if(id_mobil.size()==0){
                            Snackbar.make(v,"Barang Tidak Ditemukan",3*1000).show();
                        }
                    }
                });

    }

    private void caridata(final String keyword){
            pb.setVisibility(View.VISIBLE);
                RequestQueue rq = Volley.newRequestQueue(getActivity());
                StringRequest sr = new StringRequest(Request.Method.POST, Config.url + "/daftarkendaraancari",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray ja = new JSONArray(response);
                                    System.out.println(ja.length());
                                    for (int i = 0; i < ja.length(); i++) {
                                        JSONObject jo = ja.getJSONObject(i);
                                        id_mobil.add(jo.getString("IDMOBIL"));
                                        nopol.add(jo.getString("NOPOL"));
                                        merk.add(jo.getString("MEREK"));
                                        tipe.add(jo.getString("TIPE"));
                                        tahun.add(jo.getString("TAHUN"));
                                        status.add(jo.getString("STATUS"));
                                        warna.add(jo.getString("WARNA"));
                                        img_mobil.add(Config.urlgambar + "/" + jo.getString("ID_IMAGE") + ".jpg");

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
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("cari", keyword);
                        params.put("pageinv", String.valueOf(cpage));
                        return params;
                    }
                };
                sr.setRetryPolicy(new DefaultRetryPolicy(1000 * 15,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rq.add(sr);
                rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                    @Override
                    public void onRequestFinished(Request<Object> request) {
                        pb.setVisibility(View.GONE);
                        badapter.notifyDataSetChanged();
                        if (id_mobil.size() == 0) {
                            Snackbar.make(v, "Barang Tidak Ditemukan", 3 * 1000).show();
                        }
                    }
                });
            }

}
