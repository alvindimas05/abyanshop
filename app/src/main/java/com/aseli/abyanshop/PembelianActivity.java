package com.aseli.abyanshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PembelianActivity extends AppCompatActivity {
    JSONArray finalData;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }
    @Override
    protected void onStart() {
        super.onStart();
        Bundle extras = getIntent().getExtras();
        String[] koloms = extras.getString("kolom").split(",");

        for(String kolom : koloms){
            LinearLayout layout = (LinearLayout) LinearLayout.inflate(this, R.layout.pembelian_input, null);
            kolom = kolom.replaceAll("_", " ").toUpperCase(Locale.ROOT);
            ((TextView) layout.getChildAt(0)).setText(kolom);
            ((LinearLayout) findViewById(R.id.pembelian_inputs)).addView(layout);
        }
        ((TextView) findViewById(R.id.pembelian_nama)).setText(extras.getString("nama"));
        JSONArray data = null;
        try {
            data = new PembelianTask(this, extras.getInt("id")).execute().get();
        } catch (Exception e) {
            Log.w("Pembelian Error", e);
        }
        new BeliImageTask(this, findViewById(R.id.pembelian_image), extras.getInt("id")).execute();
        // Spinner onSelected
        Spinner spinner = findViewById(R.id.pembelian_produk);
        finalData = data;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    int harga = finalData.getJSONObject(position).getInt("harga");
                    DecimalFormat format = new DecimalFormat("#,###");
                    ((TextView) findViewById(R.id.pembelian_harga)).setText(
                            "Rp " + format.format(harga));
                } catch (Exception e) {
                    Log.w("Harga Pembelian", e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    public void onBeli(View v){
        SharedPreferences settings = getSharedPreferences("user_data", MODE_PRIVATE);
        if(settings.contains("user_id")){
            new AlertDialog.Builder(this)
                    .setTitle("Konfirmasi pembelian")
                    .setMessage("Apakah kamu yakin ini membeli produk ini?")
                    .setPositiveButton("Konfirmasi", (dialog, which) -> {
                        dialog.cancel();
                        try {
                            Spinner spinner = findViewById(R.id.pembelian_produk);
                            int id_produk = finalData.getJSONObject(spinner.getSelectedItemPosition()).getInt("id");
                            new BeliTask(PembelianActivity.this,
                                    settings.getString("user_id", null), id_produk)
                                    .execute();
                        } catch (Exception e){
                            Log.w("Beli Error", e);
                        }
                    })
                    .setNegativeButton("Batalkan", (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Gagal Pembelian")
                    .setMessage("Kamu harus login atau registrasi terlebih dahulu!")
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel())
                    .show();
        }
    }
}
class BeliImageTask extends AsyncTask<Void, Void, Void> {
    private ImageView imageView;
    private int id;
    private Activity activity;
    public BeliImageTask(Activity activity, ImageView imageView, int id){
        this.activity = activity;
        this.imageView = imageView;
        this.id = id;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            new ImageFromURL(imageView, id).setImage(activity);
        } catch (Exception e) {
            Log.w("Pembelian Image", e);
        }
        return null;
    }
}

class BeliTask extends AsyncTask<Void, Void, JSONObject>{
    private ProgressDialog pd;
    private Activity activity;
    private String user_id;
    private int id_produk;
    public BeliTask(Activity activity, String user_id, int id_produk){
        this.activity = activity;
        this.user_id = user_id;
        this.id_produk = id_produk;
        pd = new ProgressDialog(activity);
    }
    @Override
    protected void onPreExecute(){
        pd.setMessage("Loading...");
        pd.show();
    }
    @Override
    protected JSONObject doInBackground(Void... voids){
        HashMap<String, String> data = new HashMap<>();
        data.put("user_id", user_id);
        data.put("id_produk", String.valueOf(id_produk));
        return new JSONRequest()
                .setMethod(JSONRequest.HTTP_POST)
                .setPath("user/beli")
                .setData(data)
                .execute();
    }
    @Override
    protected void onPostExecute(JSONObject obj){
        pd.dismiss();
        try {
            boolean success = obj.getBoolean("status");
            new AlertDialog.Builder(activity)
                    .setTitle("Pembelian")
                    .setMessage(success ? "Pembelian berhasil!" : obj.getString("message"))
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                        if(success){
                            activity.startActivity(new Intent(activity, ProfilActivity.class));
                            activity.finish();
                        }
                    }).show();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
class PembelianTask extends AsyncTask<Void, Void, JSONArray> {
    ProgressDialog pd;
    int id;
    Activity activity;
    public PembelianTask(Activity activity, int id){
        this.activity = activity;
        this.id = id;
    }
    @Override
    protected void onPreExecute(){
        pd = new ProgressDialog(activity);
        pd.setTitle("Loading...");
    }
    @Override
    protected JSONArray doInBackground(Void... voids) {
        try {
            JSONObject obj = new JSONRequest()
                    .setPath("produk?id_tipe=" + id)
                    .setMethod(JSONRequest.HTTP_GET)
                    .execute();
            return obj.getJSONArray("data");
        } catch (Exception e){
            Log.w("Pembelian Task", e);
        }
        return null;
    }
    @Override
    public void onPostExecute(JSONArray data){
        pd.dismiss();

        List<String> list = new ArrayList<>();
        for(int i = 0; i < data.length(); i++){
            try {
                list.add(data.getJSONObject(i).getString("nama"));
            } catch (JSONException e) {
                Log.w("Pembelian Activity", e);
            }
        }
        Spinner spinner = activity.findViewById(R.id.pembelian_produk);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
    }
}