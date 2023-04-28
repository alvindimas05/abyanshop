package com.aseli.abyanshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new ProfilTask(this).execute();
    }
    public void onTopUp(View v){
        EditText editText = findViewById(R.id.profil_isi_saldo);
        editText.setFocusableInTouchMode(false);
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(true);
        editText.setFocusable(true);
        new TopUpTask(this, editText.getText().toString()).execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }
}
class TopUpTask extends AsyncTask<Void, Void, JSONObject> {
    private Activity activity;
    private String jumlah;
    private ProgressDialog pd;
    public TopUpTask(Activity activity, String jumlah){
        this.activity = activity;
        this.jumlah = jumlah;
        this.pd = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        pd.setTitle("Loading...");
        pd.show();
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        try {
            SharedPreferences settings = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
            HashMap<String, String> data = new HashMap<>();
            data.put("user_id", settings.getString("user_id", null));
            data.put("jumlah", jumlah);

            return new JSONRequest()
                    .setMethod(JSONRequest.HTTP_POST)
                    .setPath("user/top_up")
                    .setData(data).execute();
        } catch (Exception e){
            Log.w("TopUp Error", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        pd.dismiss();
        try {
            boolean success = obj.getBoolean("status");
            new AlertDialog.Builder(activity)
                    .setTitle("Top Up")
                    .setMessage(success ? "Top Up berhasil!" : "Terjadi kesalahan!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        activity.startActivity(new Intent(activity, ProfilActivity.class));
                        activity.finish();
                    }).show();
        } catch(Exception e){
            Log.w("TopUp Error", e);
        }
    }
}
class ProfilTask extends AsyncTask<Void, Void, List<JSONObject>> {
    private Activity activity;
    private ProgressDialog pd;
    public ProfilTask(Activity activity){
        this.activity = activity;
    }
    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(activity);
        pd.setTitle("Loading...");
        pd.show();
    }

    @Override
    protected List<JSONObject> doInBackground(Void... voids) {
        SharedPreferences settings = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        JSONObject histori = new JSONRequest()
                .setMethod(JSONRequest.HTTP_GET)
                .setPath("user/riwayat?user_id=" + settings.getString("user_id", null))
                .execute();
        JSONObject user = new JSONRequest()
                .setMethod(JSONRequest.HTTP_GET)
                .setPath("user?user_id=" + settings.getString("user_id", null))
                .execute();
        List<JSONObject> result = new ArrayList<>();
        result.add(histori);
        result.add(user);
        return result;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onPostExecute(List<JSONObject> result) {
        pd.dismiss();
        try {
            JSONArray datas = result.get(0).getJSONArray("data");
            List<HistoriItem> items = new ArrayList<>();
            for(int i = 0; i < datas.length(); i++){
                JSONObject data = datas.getJSONObject(i);
                HistoriItem item = new HistoriItem();
                item.setNama(data.getString("tipe"));
                item.setJumlah(data.getString("nama"));
                items.add(item);
            }
            HistoriAdapter adapter = new HistoriAdapter(activity, items);
            RecyclerView recyclerView = activity.findViewById(R.id.profil_histori);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));

            JSONObject user = result.get(1).getJSONObject("data");
            TextView username = activity.findViewById(R.id.profil_username),
            saldo = activity.findViewById(R.id.profil_saldo);

            int harga = user.getInt("saldo");
            DecimalFormat format = new DecimalFormat("#,###");
            username.setText(user.getString("username"));
            saldo.setText("Rp " + format.format(harga));
        } catch (Exception e){
            Log.w("Error Profil", e);
        }
    }
}
