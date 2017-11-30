package com.mizan.emha;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Minami on 08/09/2017.
 */

public class Header_history_adapter extends RecyclerView.Adapter<Header_history_adapter.Holder>{
    ArrayList<String> idtransaksi=new ArrayList<>();
    ArrayList<String> tanggal=new ArrayList<>();
    ArrayList<String> noref=new ArrayList<>();
    ArrayList<String> nopol=new ArrayList<>();
    ArrayList<String> keterangan=new ArrayList<>();
    ArrayList<String> nama=new ArrayList<>();
    ArrayList<Double> total=new ArrayList<>();
    Context ct;
    NumberFormat nf=NumberFormat.getInstance();

    public Header_history_adapter(ArrayList<String> idtransaksi,
                                  ArrayList<String> tanggal, ArrayList<String> noref,ArrayList<String> nopol,
                                  ArrayList<String> keterangan,ArrayList<String> nama,ArrayList<Double> total, Context ct) {
        this.idtransaksi = idtransaksi;
        this.tanggal = tanggal;
        this.noref = noref;
        this.nopol = nopol;
        this.keterangan = keterangan;
        this.nama = nama;
        this.total = total;
        this.ct = ct;
    }


    public class Holder extends RecyclerView.ViewHolder{
        public ImageView img_order;
        public TextView noref;
        public TextView nopol;
        public TextView tanggal;
        public TextView keterangan;
        public TextView nama;
        public TextView total;
        public LinearLayout linearbt;
        public Holder(View itemView) {
            super(itemView);
            tanggal=(TextView) itemView.findViewById(R.id.tanggal);
            noref=(TextView) itemView.findViewById(R.id.noref);
            nopol=(TextView) itemView.findViewById(R.id.nopol);
            keterangan=(TextView) itemView.findViewById(R.id.keterangan);
            nama=(TextView) itemView.findViewById(R.id.nama);
            total=(TextView) itemView.findViewById(R.id.total);
            img_order=(ImageView) itemView.findViewById(R.id.img_order);
            linearbt=(LinearLayout) itemView.findViewById(R.id.linearbt);
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_header_history,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        Date dt=null;
        try {
            dt=new SimpleDateFormat("yyyy-MM-dd").parse(tanggal.get(position));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tanggalterformat=new SimpleDateFormat("dd/MM/yyyy").format(dt);
        holder.tanggal.setText(tanggalterformat);
        holder.noref.setText(noref.get(position));
        holder.nopol.setText(nopol.get(position));
        holder.keterangan.setText(keterangan.get(position));
        holder.nama.setText(nama.get(position));
        holder.total.setText("Rp. "+nf.format(total.get(position)));
        holder.linearbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ct,History_activity.class);
                i.putExtra("idtransaksi",idtransaksi.get(position));
                ct.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return idtransaksi.size();
    }


}
