package com.example.galleryproject;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ViewPicture extends AppCompatActivity {
    ImageView image_viewed;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        image_viewed = findViewById(R.id.image_viewed);
        image_viewed.setImageResource(R.drawable.game_lauchericon_foreground);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent gettedIntent = getIntent();
        Bundle gettedData =  gettedIntent.getExtras();

        String uriString = gettedData.getString("uriImgString");
        Uri img = Uri.parse(uriString);
        image_viewed.setImageURI(img);
    }
}