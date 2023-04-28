package com.aseli.abyanshop;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
    }
    public void onLogin(View v){
        new LoginTask(this).execute();
    }
}
class LoginTask extends AsyncTask<Void, Void, JSONObject> {
    private ProgressDialog pd;
    private Activity activity;
    public LoginTask(Activity activity){
        this.activity = activity;
    }
    public void onPreExecute(){
        pd = new ProgressDialog(activity);
        pd.show();
    }
    public JSONObject doInBackground(Void... voids){
        try {
            // Set parameter username dan password ke hashmap
            HashMap<String, String> postData = new HashMap<>();
            postData.put("username", ((EditText) activity.findViewById(R.id.login_username)).getText().toString());
            postData.put("password", ((EditText) activity.findViewById(R.id.login_password)).getText().toString());

            // Mengambil hasil
            return new JSONRequest()
                    .setMethod(JSONRequest.HTTP_POST)
                    .setPath("user/login")
                    .setData(postData)
                    .execute();
        } catch (Exception e){
            Log.w("Login Error", e);
        }
        return null;
    }
    @Override
    protected void onPostExecute(JSONObject obj){
        try {
            boolean success = obj != null && obj.getBoolean("status");
            if(success){
                SharedPreferences settings = activity.getSharedPreferences("user_data", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("user_id", obj.getString("data"));
                editor.apply();
            }
            pd.dismiss();
            new AlertDialog.Builder(activity)
                    .setTitle("Login")
                    .setMessage((success ? "Berhasil" : "Gagal") + " login!")
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.cancel();
                        if(!success) return;
                        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                        List<ActivityManager.AppTask> runningTasks = activityManager.getAppTasks();
                        for(ActivityManager.AppTask task : runningTasks) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                    task.getTaskInfo().baseActivity.getClassName().equals(MainActivity.class.getSimpleName())) {
                                task.finishAndRemoveTask();
                                break;
                            }
                        }
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                    })
                    .show();
        } catch (Exception e) {
            Log.w("Login Error", e);
        }
    }
}