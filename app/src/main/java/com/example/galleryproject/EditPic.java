package com.example.galleryproject;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;

import ja.burhanrashid52.photoeditor.PhotoEditorView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditPic extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        Bundle data = getIntent().getExtras();
        Uri imageUri = data.getParcelable("imageUri");
        PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.getSource().setImageURI(imageUri);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.editpicture_menu, menu);
        return true;
    }
}


