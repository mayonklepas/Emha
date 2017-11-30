package com.mizan.emha;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Minami on 26/11/2017.
 */

public class Kelengkapanadapter extends RecyclerView.Adapter<Kelengkapanadapter.Holder>{

    ArrayList<String> gambar=new ArrayList<>();
    ArrayList<String> keterangan=new ArrayList<>();
    Context ct;
    NumberFormat nf=NumberFormat.getInstance();

    public Kelengkapanadapter(ArrayList<String> gambar, ArrayList<String> keterangan, Context ct) {
        this.gambar = gambar;
        this.keterangan = keterangan;
        this.ct = ct;
    }

    public class Holder extends RecyclerView.ViewHolder{
        Typeface tp=Typeface.createFromAsset(ct.getAssets(),Config.fontname);
        public ImageView img_mobil,imghapus;
        public TextView keterangan;
        LinearLayout ll;
        public Holder(View itemView) {
            super(itemView);
            img_mobil=(ImageView) itemView.findViewById(R.id.img_mobil);
            imghapus=(ImageView) itemView.findViewById(R.id.imghapus);
            keterangan=(TextView) itemView.findViewById(R.id.keterangan);
        }
    }

    @Override
    public Kelengkapanadapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.kendaraan_adapter,parent,false);
        return new Kelengkapanadapter.Holder(v);
    }

    @Override
    public void onBindViewHolder(Kelengkapanadapter.Holder holder, final int position) {
        holder.keterangan.setText(keterangan.get(position));
        holder.imghapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb=new AlertDialog.Builder(ct);
                adb.setTitle("Konfirmasi");
                adb.setMessage("Yakin ingin menghapus data ini?");
                adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
            }
        });
        Glide.with(ct).
                load(gambar.get(position)).
                diskCacheStrategy(DiskCacheStrategy.ALL).
                crossFade().placeholder(R.drawable.placeholder).
                centerCrop().into(holder.img_mobil);
        holder.img_mobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return gambar.size();
    }
}
