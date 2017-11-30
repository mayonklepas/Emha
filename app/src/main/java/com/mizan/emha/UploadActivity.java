package com.mizan.emha;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

    ImageView imgback;
    EditText keterangan;
    Button kirim,ambilfoto;
    ImageView gambar;
    int status=10;
    String b64,idmobil,tipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        keterangan=(EditText) findViewById(R.id.keterangan);
        gambar=(ImageView) findViewById(R.id.gambar);
        kirim=(Button) findViewById(R.id.kirim);
        ambilfoto=(Button) findViewById(R.id.ambilfoto);
        imgback=(ImageView) findViewById(R.id.imgback);
        Bundle ex=getIntent().getExtras();
        idmobil=ex.getString("idmobil");
        tipe=ex.getString("tipe");
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertgambar(idmobil,tipe);
            }
        });
        ambilfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getfoto();
            }
        });

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(UploadActivity.this,KelengkapanActivity.class);
                i.putExtra("idmobil",idmobil);
                i.putExtra("tipe",tipe);
                startActivity(i);
                finish();
            }
        });

    }


    private void getfoto() {
        /*if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},1);
                return;
            }
            Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i,1);

        }*/
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1 || resultCode==RESULT_OK && data != null && data.getData() != null){
            Uri uri=data.getData();
            InputStream is=null;
            try {
                is=getContentResolver().openInputStream(uri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap bmp=BitmapFactory.decodeStream(is);
            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
            int bitmapByteCount= BitmapCompat.getAllocationByteCount(bmp);
            Toast.makeText(this, String.valueOf(bitmapByteCount/1000), Toast.LENGTH_SHORT).show();
            gambar.setImageBitmap(bmp);
        }

    }

    public String imagetostr(){
        BitmapDrawable bmd=(BitmapDrawable) gambar.getDrawable();
        Bitmap bmp=bmd.getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,20,baos);
        byte[] imagebyte=baos.toByteArray();
        String imagecompress= Base64.encodeToString(imagebyte,Base64.NO_WRAP);
        return imagecompress;
    }

    private void insertgambar(final String idmobil,final String tipe){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Mengirim Data...");
        pd.setCancelable(false);
        pd.show();
        RequestQueue rq= Volley.newRequestQueue(this);
        StringRequest sr=new StringRequest(Request.Method.POST, Config.url + "/uploadimage", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("berhasil")){
                    status=0;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map=new HashMap<>();
                map.put("imgket",keterangan.getText().toString());
                map.put("idmobil",idmobil);
                map.put("tipe",tipe);
                map.put("imgsrc",imagetostr());

                return map;
            }
        };
        sr.setRetryPolicy(new DefaultRetryPolicy(1000*15,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(sr);
        rq.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(status==0){
                    pd.dismiss();
                    AlertDialog.Builder adb=new AlertDialog.Builder(UploadActivity.this);
                    adb.setTitle("Informasi");
                    adb.setMessage("Upload Berhasil");
                    adb.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    adb.show();
                }else{
                    pd.dismiss();
                    AlertDialog.Builder adb=new AlertDialog.Builder(UploadActivity.this);
                    adb.setTitle("Informasi");
                    adb.setMessage("Gagal Upload");
                    adb.setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    adb.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(UploadActivity.this,KelengkapanActivity.class);
        i.putExtra("idmobil",idmobil);
        i.putExtra("tipe",tipe);
        startActivity(i);
        finish();
    }
}
