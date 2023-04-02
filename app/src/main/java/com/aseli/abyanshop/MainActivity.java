package com.aseli.abyanshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            URL url = new URL(getResources().getString(R.string.url));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

            LinearLayout ll = findViewById(R.id.main_view);
            LinearLayout parent = (LinearLayout) ll.getParent();

            for(int i = 0; i < data.length(); i += 2){
                for(int j = 0; j < 1; j++){
                    CardView view = (CardView) ll.getChildAt(j);
                    TextView text = (TextView) view.getChildAt(j);
                    ImageView image = (ImageView) view.getChildAt(j);
                    JSONObject dat = data.getJSONObject(i + (j > 0 ? 1 : 0));

                    text.setText(dat.getString("nama"));
                    InputStream is = (InputStream) new URL(url + "/images/" + data.getInt(i)).getContent();
                    Bitmap bm = BitmapFactory.decodeStream(is);
                    image.setImageBitmap(bm);
                }
                parent.addView(ll);
            }
            parent.removeView(ll);
        } catch (Exception e){
            Log.w("Error Data", e);
        }
    }
}