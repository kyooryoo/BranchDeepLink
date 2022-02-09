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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SharedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TextView textView = findViewById(R.id.textView);
        Intent intent = getIntent();

        String link = intent.getStringExtra("link");
        String channel = intent.getStringExtra("channel");
        String shared_message = getString(R.string.shared_message);

        textView.setText(Html.fromHtml(String.format(
                "%s<br><b>%s</b><br>%s", shared_message, channel, link)));
        String img_url = "https://branch.io/img/logo_icon_black.png";
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(getBitmapFromURL(img_url));

        Button backButton=findViewById(R.id.button2);
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