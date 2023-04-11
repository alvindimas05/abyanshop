package com.aseli.abyanshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        new GetData(this).execute();
    }
}
class GetData extends AsyncTask<Void, Void, Void>{
    Activity activity;
    public GetData(Activity activity){
        this.activity = activity;
    }
    @Override
    protected Void doInBackground(Void... voids){
        try {
            String url = activity.getResources().getString(R.string.url);
            URL api = new URL(url + "tipe");
            HttpURLConnection conn = (HttpURLConnection) api.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new Exception("Failed to connect to the server!");
            }

            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            stream.close();

            JSONObject obj = new JSONObject(builder.toString());
            JSONArray data = obj.getJSONArray("data");

            ScrollView main_layout = activity.findViewById(R.id.main_view);

            for(int i = 0; i < data.length(); i += 2){
                LinearLayout layout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.tipe, null);
                for(int j = 0; j < (i + 1 < data.length() ? 2 : 1); j++){
                    JSONObject dat = data.getJSONObject(i);
                    CardView card = (CardView) layout.getChildAt(i);
                    InputStream is = (InputStream) new URL(url + "../images/" + dat.getInt("id")).getContent();
                    ((ImageView) card.getChildAt(0)).setImageBitmap(BitmapFactory.decodeStream(is));
                    ((TextView) card.getChildAt(1)).setText(dat.getString("nama"));
                    if(i + 1 >= data.length()) card.setVisibility(View.VISIBLE);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        main_layout.addView(layout);
                    }
                });
            }
        } catch (Exception e){
            Log.w("Error Data", e);
        }
        return null;
    }
}