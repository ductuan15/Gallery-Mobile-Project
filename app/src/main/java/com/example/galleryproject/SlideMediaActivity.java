package com.example.galleryproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;


import java.util.ArrayList;
import java.util.HashSet;


public class SlideMediaActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 viewPager;
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    ArrayList<DefaultAlbum> defaultAlbumArrayList = new ArrayList<>();
    DefaultAlbum curDefaultAlbum;
    ImageButton shareBtn, deleteBtn, editBtn, favoriteBtn;
    LinearLayout buttonLayout;
    ActionBar actionBar;
    boolean isNavigateVisible = true;
    SlideMediaAdapter slideMediaAdapter;


    public HashSet<String> favoriteMediaHashSet = new HashSet<>();
    SharedPreferences favoriteSharedPreferences;
    SharedPreferences.Editor favoriteEditor;

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_media);

        // get shared preferences
        favoriteSharedPreferences = SharePreferenceHandler.getFavoriteSharePreferences(this);
        favoriteEditor = favoriteSharedPreferences.edit();
        favoriteEditor.apply();
        SharePreferenceHandler.getAllDataFromSharedPreference(favoriteSharedPreferences,favoriteMediaHashSet);



        this.actionBar = getSupportActionBar();
        this.buttonLayout = findViewById(R.id.button_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        curDefaultAlbum =  bundle.getParcelable("curAlbum");
        if(curDefaultAlbum != null){
            mediaArrayList.addAll(curDefaultAlbum.getMediaArrayList());
        }else{
            Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);
        }

        // get all media

        shareBtn = findViewById(R.id.share_button);
        shareBtn.setOnClickListener(v -> {
            try {
                Uri uriToImage = mediaArrayList.get(viewPager.getCurrentItem()).getUri();
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Share to"));
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

        });

        deleteBtn = findViewById(R.id.delete_button);
        deleteBtn.setOnClickListener(v -> {
            mediaArrayList.get(viewPager.getCurrentItem()).deleteMedia(this);
            onBackPressed();
        });

        editBtn = findViewById(R.id.edit_button);
        editBtn.setOnClickListener(v -> {                                                           // give intent to edit image
            Intent editIntent = new Intent(this, EditPic.class);
            Bundle data = new Bundle();
            Uri imageUri = mediaArrayList.get(viewPager.getCurrentItem()).getUri();                        // URI of the image to remove.
            data.putParcelable("imageUri", imageUri);
            editIntent.putExtras(data);
            startActivity(editIntent);
        });
        favoriteBtn = findViewById(R.id.favorite_button);
        favoriteBtn.setOnClickListener(v -> {
            int pos = viewPager.getCurrentItem();
            Media mediaSelected = mediaArrayList.get(pos);
            try {
                mediaSelected.changeFavoriteState();
                if (mediaArrayList.get(pos).isFavorite()) {
                    favoriteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_red24, getTheme()));
                    if (!favoriteMediaHashSet.contains(mediaSelected.getUri().toString())) ;
                    favoriteEditor.putString(mediaSelected.getUri().toString(), "");
                } else {
                    favoriteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24, getTheme()));
                    if (favoriteMediaHashSet.contains(mediaSelected.getUri().toString())) ;
                        favoriteEditor.remove(mediaSelected.getUri().toString());
                }
                favoriteEditor.apply();
            } catch (Exception e) {
                Log.e("", e.getMessage());
            }

        });
        // set pager adapter
        this.slideMediaAdapter = new SlideMediaAdapter(this, this);
        FragmentStateAdapter pagerAdapter = new SlideMediaAdapter(this, this);
        this.viewPager = findViewById(R.id.media_viewpager);
        this.viewPager.setAdapter(pagerAdapter);

        int curPos = bundle.getInt("mediaPos");
        this.viewPager.setCurrentItem(curPos);
        if (mediaArrayList.get(curPos).isFavorite()) {
            favoriteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_red24, getTheme()));
        }
        this.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mediaArrayList.get(position).isFavorite())
                    favoriteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_red24, getTheme()));
                else
                    favoriteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24, getTheme()));
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int curPos = viewPager.getCurrentItem();
        Media srcMedia = mediaArrayList.get(curPos);
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_location_tag_opt:

                return true;
            // move media to secure album
            case R.id.lock_picture:
                int res = FileHandler.moveToSecureAlbum(srcMedia, this);
                return true;
            case R.id.detail_button:
                if(curDefaultAlbum == null)
                    srcMedia.getMediaDetail(defaultAlbumArrayList.get(srcMedia.getAlbumIn()),getContentResolver(),this);
                else
                    srcMedia.getMediaDetail(curDefaultAlbum,getContentResolver(),this);
                View dialogRoot = LayoutInflater.from(this).inflate(R.layout.dialog_media_detail,null);
                TextView fileName = dialogRoot.findViewById(R.id.filename_text);
                TextView dataAdd = dialogRoot.findViewById(R.id.date_add_text);
                TextView albumPath = dialogRoot.findViewById(R.id.album_path_text);
                TextView size = dialogRoot.findViewById(R.id.fileSize_text);
                TextView resolution = dialogRoot.findViewById(R.id.resolution_text);
                TextView location = dialogRoot.findViewById(R.id.location_text);
                fileName.setText(srcMedia.getFileName());
                dataAdd.setText(Media.getDate(Long.parseLong(srcMedia.getDate())).toString());
                if(curDefaultAlbum == null){
                    albumPath.setText(defaultAlbumArrayList.get(srcMedia.getAlbumIn()).getAlbumPath());
                }else{
                    albumPath.setText(curDefaultAlbum.getAlbumPath());
                }
                size.setText(srcMedia.getTransferSize());
                resolution.setText(srcMedia.getResolution());
                location.setText(srcMedia.getLocation());

                new AlertDialog.Builder(this)
                        .setTitle(R.string.detail)
                        .setView(dialogRoot)
                        .create().show();
                return true;
            case R.id.setWallpaper_button:
                setImageAsWallpaper();
                return true;
            case R.id.copy_button:

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setImageAsWallpaper() {
//        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
//        // set the wallpaper by calling the setResource function and
//        // passing the drawable file
//        wallpaperManager.getCropAndSetWallpaperIntent(uriArrayList.get(viewPager.getCurrentItem()));
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(mediaArrayList.get(viewPager.getCurrentItem()).getUri(), "image/jpeg");
        intent.putExtra("mimeType", "image/jpeg");
        this.startActivity(Intent.createChooser(intent, "Set as:"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewpicture_menu, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        //hide top bar and buttons when click and show when click again
        if (this.actionBar != null && this.buttonLayout != null) {
            if (this.isNavigateVisible) {
                this.actionBar.hide();
                this.buttonLayout.setVisibility(View.GONE);
                this.isNavigateVisible = false;
            } else {
                this.actionBar.show();
                this.buttonLayout.setVisibility(View.VISIBLE);
                this.isNavigateVisible = true;
            }
        }
    }
}

