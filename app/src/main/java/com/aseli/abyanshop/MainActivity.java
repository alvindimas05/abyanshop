package com.aseli.abyanshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent = null;
        switch(item.getItemId()){
            case R.id.main_menu_login:
                intent = new Intent(this, LoginActivity.class);
                break;
            case R.id.main_menu_register:
                intent = new Intent(this, RegisterActivity.class);
                break;
            case R.id.main_menu_akun:
                return true;
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
                String kolom = dat.getString("kolom");
                CardView card = (CardView) layout.getChildAt(second ? 1 : 0);
                ImageView image = (ImageView) card.getChildAt(0);

                new ImageFromURL(image, id).setImage(activity);
                ((TextView) card.getChildAt(1)).setText(dat.getString("nama"));
                card.setVisibility(View.VISIBLE);
                card.setOnClickListener(v -> {
                    Intent intent = new Intent(activity, PembelianActivity.class);
                    intent.putExtra("id", id);
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
        return new ArrayList<>();
    }
    @Override
    protected void onPostExecute(List<LinearLayout> result){
        pd.dismiss();
        ScrollView main_layout = activity.findViewById(R.id.main_view);
        for(LinearLayout layout : result)
            main_layout.addView(layout);
    }
}