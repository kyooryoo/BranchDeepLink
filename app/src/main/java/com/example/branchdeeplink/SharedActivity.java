package com.example.branchdeeplink;

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

import java.io.InputStream;

public class SharedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared);

        TextView textView = findViewById(R.id.textView);
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");

        String link = intent.getStringExtra("link");
        String channel = intent.getStringExtra("channel");

        textView.setText(Html.fromHtml(String.format(
                "<b>Following URL shared via %s:</b><br>%s", channel, link)));
        new DownloadImageFromInternet(findViewById(R.id.imageView))
                .execute("https://branch.io/img/logo_icon_black.png");


        Button backButton=findViewById(R.id.button2);
        backButton.setOnClickListener(v -> finish());
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
            Toast.makeText(getApplicationContext(),
                    "Please wait, it may take a few minute...",
                    Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
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