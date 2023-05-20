package com.aseli.abyanshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ProdukTask(this).execute();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        SharedPreferences settings = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        if(settings.contains("user_id")){
            menu.findItem(R.id.main_menu_register).setVisible(false);
            menu.findItem(R.id.main_menu_login).setVisible(false);
            menu.findItem(R.id.main_menu_akun).setVisible(true);
            menu.findItem(R.id.main_menu_logout).setVisible(true);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Peringatan")
                .setMessage("Apakah kamu yakin ingin keluar?")
                .setCancelable(true)
                .setNegativeButton("Batalkan", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Konfirmasi", (dialogInterface, i) -> finishAndRemoveTask()).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView search = (SearchView) menu.findItem(R.id.main_menu_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                ScrollView scroll = findViewById(R.id.main_view);
                for(int i = 0; i < scroll.getChildCount(); i++){
                    LinearLayout layout = (LinearLayout) scroll.getChildAt(i);
                    for(int j = 0; j < layout.getChildCount(); j++){
                        CardView card = (CardView) layout.getChildAt(j);
                        TextView text = (TextView) card.getChildAt(1);

                        String oldText = text.getText().toString().toLowerCase();
                        card.setVisibility(oldText.contains(newText.toLowerCase()) && !oldText.equals("")
                                ? View.VISIBLE : View.GONE);
                    }
                }
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()){
            case R.id.main_menu_login:
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.main_menu_register:
                intent = new Intent(this, RegisterActivity.class);
                break;
            case R.id.main_menu_akun:
                intent = new Intent(this, ProfilActivity.class);
                break;
            case R.id.main_menu_logout:
                new AlertDialog.Builder(this)
                        .setTitle("Peringatan")
                        .setMessage("Apakah kamu yakin akan log out?")
                        .setCancelable(true)
                        .setPositiveButton("Konfirmasi", (dialogInterface, i) -> {
                            SharedPreferences settings = getSharedPreferences("user_data", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.remove("user_id");
                            editor.apply();

                            Intent intent1 = new Intent(this, MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }).setNegativeButton("Batalkan", (dialogInterface, i) -> dialogInterface.dismiss()).show();
            default: return true;
        }
        startActivity(intent);
        return true;
    }
}
class ProdukTask extends AsyncTask<Void, Void, List<LinearLayout>> {
    private ProgressDialog pd;
    private Activity activity;
    public ProdukTask(Activity activity){
        this.activity = activity;
        pd = new ProgressDialog(activity);
    }
    @Override
    protected void onPreExecute(){
        pd.setMessage("Loading...");
        pd.show();
    }
    @Override
    protected List<LinearLayout> doInBackground(Void... voids){
        try {
            JSONObject obj = new JSONRequest()
                    .setPath("tipe")
                    .setMethod(JSONRequest.HTTP_GET)
                    .execute();
            JSONArray data = obj.getJSONArray("data");

            LinearLayout layout = null;
            boolean second = false;
            List<LinearLayout> layouts = new ArrayList<>();
            for(int i = 0; i < data.length(); i++){
                if(!second) layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.main_tipe, null);

                JSONObject dat = data.getJSONObject(i);
                int id = dat.getInt("id");
                String kolom = dat.getString("kolom"),
                    nama = dat.getString("nama");
                CardView card = (CardView) layout.getChildAt(second ? 1 : 0);
                ImageView image = (ImageView) card.getChildAt(0);

                new ImageFromURL(image, id).setImage(activity);
                ((TextView) card.getChildAt(1)).setText(nama);
                card.setVisibility(View.VISIBLE);
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, PembelianActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("nama", nama);
                    intent.putExtra("kolom", kolom);
                    activity.startActivity(intent);
                });

                if(second || i == data.length() - 1){
                    second = false;
                    layouts.add(layout);
                } else second = true;
            }
            return layouts;
        } catch (Exception e){
            Log.w("Produk Task", e);
        }
        return null;
    }
    @Override
    protected void onPostExecute(List<LinearLayout> result){
        pd.dismiss();
        ScrollView main_layout = activity.findViewById(R.id.main_view);
        for(LinearLayout layout : result)
            main_layout.addView(layout);
    }
}