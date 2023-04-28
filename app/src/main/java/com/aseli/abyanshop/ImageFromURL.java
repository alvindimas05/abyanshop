package com.aseli.abyanshop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class ImageFromURL {
    private ImageView image;
    private int id;
    public ImageFromURL(ImageView image, int id){
        this.image = image;
        this.id = id;
    }
    public void setImage(Activity activity){
        new ImageFromURLAsync(activity, image, id).execute();
    }
}
class ImageFromURLAsync extends AsyncTask<Void, Void, Bitmap> {
    private String url = "http://ancritbat.my.id:8880/api/";
    private ImageView image;
    private Activity activity;
    private int id;
    public ImageFromURLAsync(Activity activity, ImageView image, int id){
        this.activity = activity;
        this.image = image;
        this.id = id;
    }
    @Override
    public Bitmap doInBackground(Void... voids) {
        try {
            InputStream is = (InputStream) new URL(url + "../images/" + id).getContent();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.w("Async Image", e);
        }
        return null;
    }
    @Override
    public void onPostExecute(Bitmap bitmap){
        activity.runOnUiThread(() -> image.setImageBitmap(bitmap));
    }
}
