package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.galleryproject.data.ImageInfo;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.data.VideoInfo;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class SecureAlbumActivity extends AppCompatActivity implements View.OnClickListener {
    public ArrayList<Media> secureMediaArrayList = new ArrayList<>();
    public RecyclerView thumbnailMediaRecyclerView;
    SharedPreferences defaultSharedPreferences;
    String curPass;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get current password
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        curPass = defaultSharedPreferences.getString(getString(R.string.pin_key), getString(R.string.default_pin_key));

        setContentView(R.layout.activity_secure_album);
        getAllSecureMedia();
        thumbnailMediaRecyclerView = findViewById(R.id.secure_recycleView);
        ThumbnailPictureAdapter thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.secureMediaArrayList, this, null, this);
        this.thumbnailMediaRecyclerView.setHasFixedSize(true);
        this.thumbnailMediaRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.thumbnailMediaRecyclerView.setAdapter(thumbnailPictureAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAllSecureMedia() {
        this.secureMediaArrayList.clear();
        ArrayList<File> secureMediaFileArrayList = FileHandler.getAllFileFromPath(this.getFilesDir() + File.separator + FileHandler.SECURE_ALBUM_INTERNAL_DIR_NAME);
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        for (int i = 0; i < secureMediaFileArrayList.size(); i++) {
            try {

                Uri uri = Uri.fromFile(secureMediaFileArrayList.get(i));
                mmr.setDataSource(secureMediaFileArrayList.get(i).getAbsolutePath());
                String size = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE);
                int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                if (duration > 0) {
                    VideoInfo newVideo = new VideoInfo(uri, size, null, null, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, null, null, duration, 0, false, false);
                    this.secureMediaArrayList.add(newVideo);
                } else {
                    ImageInfo newImage = new ImageInfo(uri, size, null, null, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, null, null, 0, false, false);
                    this.secureMediaArrayList.add(newImage);
                }
            } catch (Exception e) {
                Log.e("TAG", "getAllSecureMedia: ");
            }
        }
        mmr.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.secure_album_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.change_password) {
            AtomicBoolean enterCorrectPassword = new AtomicBoolean(false);
            View enterCurDialogView = LayoutInflater.from(this).inflate(R.layout.diglog_input, null);
            EditText curPassText = enterCurDialogView.findViewById(R.id.input_text);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Enter current password")
                    .setView(enterCurDialogView)
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        String inputPassword = curPassText.getText().toString();
                        if (curPassText.getText().toString().compareTo(curPass) == 0) {
                            View dialogView = LayoutInflater.from(this).inflate(R.layout.diglog_input, null);
                            EditText newPassText = dialogView.findViewById(R.id.input_text);
                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Enter new password")
                                    .setView(dialogView)
                                    .setNegativeButton("Cancel", (dialog1, which1) -> {
                                        dialog.dismiss();
                                    })
                                    .setPositiveButton("OK", (dialog1, which1) -> {
                                        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                                        editor.apply();
                                        editor.putString(getString(R.string.pin_key), newPassText.getText().toString());
                                        editor.apply();
                                        finish();
                                    }).create().show();
                        } else {
                            Toast.makeText(getBaseContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        }
        return true;
    }


    @Override
    public void onClick(View v) {

    }
}