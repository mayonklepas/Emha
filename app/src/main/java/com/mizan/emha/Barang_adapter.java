package com.mizan.emha;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Minami on 08/09/2017.
 */

public class Barang_adapter extends RecyclerView.Adapter<Barang_adapter.Holder>{
    ArrayList<String> id_mobil=new ArrayList<>();
    ArrayList<String> nopol=new ArrayList<>();
    ArrayList<String> merk=new ArrayList<>();
    ArrayList<String> tipe=new ArrayList<>();
    ArrayList<String> tahun=new ArrayList<>();
    ArrayList<String> status=new ArrayList<>();
    ArrayList<String> warna=new ArrayList<>();
    ArrayList<String> img_mobil=new ArrayList<>();
    Context ct;
    NumberFormat nf=NumberFormat.getInstance();

    public Barang_adapter(ArrayList<String> id_mobil, ArrayList<String> nopol, ArrayList<String> merk,
                          ArrayList<String> tipe, ArrayList<String> tahun,ArrayList<String> status,
                          ArrayList<String> warna,ArrayList<String> img_mobil, Context ct) {
        this.id_mobil = id_mobil;
        this.nopol = nopol;
        this.merk = merk;
        this.tipe = tipe;
        this.tahun = tahun;
        this.status=status;
        this.warna=warna;
        this.img_mobil=img_mobil;
        this.ct = ct;
    }

    public class Holder extends RecyclerView.ViewHolder{
        Typeface tp=Typeface.createFromAsset(ct.getAssets(),Config.fontname);
        public ImageView img_mobil;
        public TextView tipe;
        public TextView nopol;
        public TextView merk;
        public TextView tahun;
        public TextView warna;
        public TextView status;
        public Button detail;
        LinearLayout ll;
        public Holder(View itemView) {
            super(itemView);
            img_mobil=(ImageView) itemView.findViewById(R.id.img_mobil);
            tipe=(TextView) itemView.findViewById(R.id.tipe);
            nopol=(TextView) itemView.findViewById(R.id.nopol);
            merk=(TextView) itemView.findViewById(R.id.merk);
            tahun=(TextView) itemView.findViewById(R.id.tahun);
            status=(TextView) itemView.findViewById(R.id.status);
            warna=(TextView) itemView.findViewById(R.id.warna);
            //detail=(Button) itemView.findViewById(R.id.detail);
            ll=(LinearLayout) itemView.findViewById(R.id.ll);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_barang,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        holder.tipe.setText(tipe.get(position));
        holder.nopol.setText(nopol.get(position));
        holder.merk.setText(merk.get(position));
        holder.tahun.setText(tahun.get(position));
        holder.status.setText(status.get(position));
        Glide.with(ct).
                load(img_mobil.get(position)).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                crossFade().placeholder(R.drawable.placeholder).
                centerCrop().into(holder.img_mobil);
        holder.img_mobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ct,Detail_barang_Activity.class);
                i.putExtra("idmobil",id_mobil.get(position));
                ct.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return id_mobil.size();
    }


}
