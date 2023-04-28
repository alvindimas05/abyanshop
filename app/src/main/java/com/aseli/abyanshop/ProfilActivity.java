package com.aseli.abyanshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProfilActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new ProfilTask(this).execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
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
