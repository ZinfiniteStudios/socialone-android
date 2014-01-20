package com.socialone.android.fivehundredpx.api.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.parse.entity.mime.HttpMultipartMode;
import com.parse.entity.mime.MultipartEntity;
import com.parse.entity.mime.content.FileBody;
import com.parse.entity.mime.content.StringBody;
import com.socialone.android.MainApp;
import com.socialone.android.R;
import com.socialone.android.fivehundredpx.api.auth.AccessToken;
import com.socialone.android.utils.Constants;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by david.hodge on 1/16/14.
 */
public class ImageUploadTask extends AsyncTask<Object, Void, String> {
    private static final String TAG = "ImageUploadTask";

    @Override
    protected String doInBackground(Object... pa) {

        final String photo_id = (String) pa[0];
        final String upload_key = (String) pa[1];
        final Uri selectedImage = (Uri) pa[2];
        final AccessToken accessToken = (AccessToken) pa[3];
        try {
            SharedPreferences preferences = MainApp.getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost("https://api.500px.com/v1/upload");

            final Context c = MainApp.getContext();

            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            File myFile = new File(Environment.getExternalStorageDirectory(), selectedImage.getPath());
            if (!myFile.exists()) {
                myFile.mkdirs();
                myFile.createNewFile();
            }
            entity.addPart("photo_id", new StringBody(photo_id));
            entity.addPart("upload_key", new StringBody(upload_key));
            entity.addPart("file",
                    new FileBody(new File(selectedImage.getPath())));

            entity.addPart("consumer_key",
                    new StringBody(c.getString(R.string.px_consumer_key)));
            entity.addPart("access_key", new StringBody(preferences.getString(Constants.PREF_ACCES_TOKEN, null)));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));

            String sResponse = reader.readLine();

            return sResponse;
        } catch (Exception e) {
            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... unsued) {

    }

    @Override
    protected void onPostExecute(String sResponse) {
        final Context c = MainApp.getContext();
        try {

            if (sResponse != null) {
                JSONObject JResponse = new JSONObject(sResponse);
                String message = JResponse.getString("error");
                if (!"None.".equals(message)) {

                    Toast.makeText(c.getApplicationContext(), message,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(c.getApplicationContext(),
                            "Photo uploaded successfully", Toast.LENGTH_SHORT)
                            .show();
                    Log.w(TAG, message);

                }
            }
        } catch (Exception e) {
            Toast.makeText(c.getApplicationContext(), "error...",
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

}
