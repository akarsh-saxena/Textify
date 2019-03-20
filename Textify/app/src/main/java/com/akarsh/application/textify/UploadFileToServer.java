package com.akarsh.application.textify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

class UploadFileToServer extends AsyncTask<String, Integer, String> {
    private long totalSize = 0;
    private ProgressDialog progressDialog;
    private Context context;
    OnDataSendToActivity dataSendToActivity;

    UploadFileToServer(Context context, Activity activity) {
        this.context = context;
        dataSendToActivity = (OnDataSendToActivity)activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        return uploadFile(params[0], params[1]);
    }

    private String uploadFile(String opFilePath, String modelType) {
        String responseString = null;
        Log.d("Log", "File path" + opFilePath);
        HttpClient httpclient = new DefaultHttpClient();
//        HttpPost httppost = new HttpPost("http://127.0.0.1:5000/api/test");
        SharedPreferences sharedPreferences = context.getSharedPreferences("server_url", Context.MODE_PRIVATE);
        String server_url = sharedPreferences.getString("server_url", "hellakarsh.pythonanywhere.com");
        HttpPost httppost = new HttpPost("http://"+server_url+"/api/"+modelType);
//        HttpPost httppost = new HttpPost("http://192.168.1.2:5000/api/"+modelType);
        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });
            ExifInterface newIntef = new ExifInterface(opFilePath);
            newIntef.setAttribute(ExifInterface.TAG_ORIENTATION,String.valueOf(2));
            File file = new File(opFilePath);
            entity.addPart("pic", new FileBody(file));
//            entity.addPart("model", new StringBody("1"));
            totalSize = entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();


            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
                Log.d("Log", responseString);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode + " -> " + response.getStatusLine().getReasonPhrase();
                Log.d("Log", responseString);
            }

        } catch (ClientProtocolException e) {
            responseString = e.toString();
        } catch (IOException e) {
            responseString = e.toString();
        }

        return responseString;
    }

    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        progressDialog.setProgress(progress[0]);
        if(progress[0]==100) {
            progressDialog.dismiss();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Recognizing");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("LOG", s);
        System.out.print(s);
        progressDialog.dismiss();
        dataSendToActivity.sendData(s);
    }
}