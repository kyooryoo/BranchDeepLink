package com.example.branchdeeplink;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        TextView textView = findViewById(R.id.textView);
        Intent intent = getIntent();
        String lpString = intent.getStringExtra("lp");

        JSONObject lpJson = null;
        try {
            lpJson = new JSONObject(lpString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] params = {
                "$og_title",
                "$og_description",
                "$og_image_url",
                "$marketing_title",
                "source",
                "~feature",
                "~campaign",
                "~channel",
                "~referring_link",
        };

        Dictionary received = new Hashtable();
        for (String param: params) {
            if (lpJson.has(param)) {
                try {
                    received.put(param, lpJson.getString(param));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        String img_url = received.get("$og_image_url") == null ?
                "https://avatars.githubusercontent.com/u/16432725?v=4?s=400"
                : (String) received.get("$og_image_url");
        new DownloadImageFromInternet(findViewById(R.id.imageView))
                .execute(img_url);

        String title = (String) received.get("$og_title");
        String m_title = (String) received.get("$marketing_title");
        String description = (String) received.get("$og_description");
        String source = (String) received.get("source");
        String feature = (String) received.get("~feature");
        String campaign = (String) received.get("~campaign");
        String channel = (String) received.get("~channel");
        String ref_link = (String) received.get("~referring_link");

        textView.setText(Html.fromHtml(String.format(
                "<p>You opened a Deep Link with following details</p>%s%s%s%s%s"
                , title!=null ? "<b>"+title+"</b><br>"
                        : m_title!=null ? "<b>"+m_title+"</b><br>" : ""
                , description.isEmpty() ? "Description: NA<br>" : "Description: "+description+"<br>"
                , campaign!=null ? "Campaign: <b>"+campaign+"</b>" : "NA"
                , source!=null ? " on <b>"+source+"</b><br>" : " on <b>Branch</b><br>"
                , channel!=null && feature!=null && ref_link!=null ?
                        "Shared via <b>"+channel+"</b> of <b>"+feature+"</b> with:<br>"+ref_link : ""
        )));

        Button backButton = findViewById(R.id.button2);
        backButton.setOnClickListener(v -> finish());
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
            Toast.makeText(getApplicationContext(),
                    "Please wait, it may take a few minute...",
                    Toast.LENGTH_SHORT).show();
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}