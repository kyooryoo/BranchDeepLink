package com.example.branchdeeplink;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        Dictionary<String, String> received = new Hashtable<>();
        for (String param: params) {
            assert lpJson != null;
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
                : received.get("$og_image_url");
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(getBitmapFromURL(img_url));

        String title = received.get("$og_title");
        String m_title = received.get("$marketing_title");
        String description = received.get("$og_description");
        String source = received.get("source");
        String feature = received.get("~feature");
        String campaign = received.get("~campaign");
        String channel = received.get("~channel");
        String ref_link = received.get("~referring_link");

        String detail_message = getString(R.string.detail_message);
        String description_word = getString(R.string.desp);
        String campaign_word = getString(R.string.campaign);
        String shared_via = getString(R.string.shared_via);
        textView.setText(Html.fromHtml(String.format(
                "<p>%s:</p>%s%s%s%s%s"
                , detail_message
                , title!=null ? "<b>"+title+"</b><br>"
                        : m_title!=null ? "<b>"+m_title+"</b><br>" : ""
                , description != null && description.isEmpty() ?
                        description_word+": NA<br>" :
                        description_word+": "+description+"<br>"
                , campaign!=null ?  campaign_word+": <b>"+campaign+"</b>" : "NA"
                , source!=null ? " @ <b>"+source+"</b><br>" : " @ <b>Branch</b><br>"
                , channel!=null && feature!=null && ref_link!=null ?
                        shared_via+" <b>"+channel+"</b> / <b>"+feature+"</b>:<br>"+ref_link : ""
        )));

        Button backButton = findViewById(R.id.button2);
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}