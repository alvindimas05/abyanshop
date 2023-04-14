package com.aseli.abyanshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    LoginTask task;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void onLogin(View v){
        task = new LoginTask(this);
        task.execute();
    }
    public void onPause(){
        task.cancel(true);
        super.onPause();
    }
}
class LoginTask extends AsyncTask<Void, Void, JSONObject> {
    ProgressDialog pd;
    Activity activity;
    public LoginTask(Activity activity){
        this.activity = activity;
    }
    public void onPreExecute(){
        pd = new ProgressDialog(activity);
        pd.show();
    }
    public JSONObject doInBackground(Void... voids){
        try {
            String url = activity.getResources().getString(R.string.url);
            URL api = new URL(url + "user/login");
            HttpURLConnection conn = (HttpURLConnection) api.openConnection();
            HashMap<String, String> postData = new HashMap<String, String>();
            postData.put("username", ((EditText) activity.findViewById(R.id.login_username)).getText().toString());
            postData.put("password", ((EditText) activity.findViewById(R.id.login_password)).getText().toString());

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postData));
            writer.flush();
            writer.close();
            os.close();

            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) throw new Exception("Error Login!");

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
            return obj;
        } catch (Exception e){
            Log.w("Login Error", e);
        }
        return null;
    }
    @Override
    protected void onPostExecute(JSONObject obj){
        try {
            Boolean success = obj != null && obj.getBoolean("status");
            if(success){
                SharedPreferences settings = activity.getSharedPreferences("user_data", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("user_id", obj.getString("data"));
                editor.apply();
            }
            pd.dismiss();
            new AlertDialog.Builder(activity)
                    .setTitle("Login")
                    .setMessage((success ? "Berhasil" : "Gagal") + " login!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (Exception e) {
            Log.w("Login Error", e);
        }
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}