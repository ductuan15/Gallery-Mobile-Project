package com.example.galleryproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.data.Media;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;

public class SecureAlbumActivity extends AppCompatActivity {
    public ArrayList<Media> secureMediaArrayList = new ArrayList<>();
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secure_album);

        getAllSecureMedia();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAllSecureMedia(){
//        this.secureMediaArrayList =  FileHandler.getAllFileFromPath(this.getFilesDir() + File.separator + FileHandler.SECURE_ALBUM_INTERNAL_DIR_NAME);
//        for(int i = 0;i<secureMediaArrayList.size();i++){
//            try {
////                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
////                retriever.setDataSource(this.secureMediaArrayList.get(i).getAbsolutePath());
////                String a = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                Uri uri =  Uri.fromFile(secureMediaArrayList.get(i));
//                ImageView imageView = findViewById(R.id.image_secure);
//                Glide.with(this)
//                        .load(uri)
//                        .into(imageView);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}