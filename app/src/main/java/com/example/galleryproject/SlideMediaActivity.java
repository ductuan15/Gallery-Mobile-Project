package com.example.galleryproject;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.example.galleryproject.data.ImageInfo;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.data.VideoInfo;


import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import wseemann.media.FFmpegMediaMetadataRetriever;


public class    SlideMediaActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 viewPager;
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    ArrayList<DefaultAlbum> defaultAlbumArrayList = new ArrayList<>();
    DefaultAlbum curDefaultAlbum;
    ImageButton shareBtn, deleteBtn, editBtn, favoriteBtn;
    LinearLayout buttonLayout;
    ActionBar actionBar;
    boolean isNavigateVisible = true;
    boolean isSlideShow = false;
    boolean isSecureMode = false;

    SlideMediaAdapter slideMediaAdapter;

    Timer timerSlideShow;


    public HashSet<String> favoriteMediaHashSet = new HashSet<>();
    SharedPreferences favoriteSharedPreferences;
    SharedPreferences.Editor favoriteEditor;
    public final static int VIEW_MODE_ALL = 0;
    public final static int VIEW_MODE_ALBUM = 1;
    public final static int VIEW_MODE_SECURE_ALBUM = 2;
    public final static int VIEW_MODE_TYPE_MEDIA = 3;
    public final static int VIEW_MODE_ACTION_SEND = 4;

    int viewMode = VIEW_MODE_ALL;


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_media);


        // get shared preferences
        favoriteSharedPreferences = SharePreferenceHandler.getFavoriteSharePreferences(this);
        favoriteEditor = favoriteSharedPreferences.edit();
        favoriteEditor.apply();
        SharePreferenceHandler.getAllDataFromSharedPreference(favoriteSharedPreferences, favoriteMediaHashSet);

        Toolbar toolbar = findViewById(R.id.mainTopAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        this.actionBar = getSupportActionBar();
        if (this.actionBar != null) {
            this.actionBar.setTitle("");
        }
        this.buttonLayout = findViewById(R.id.button_layout);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (getIntent().getData() != null) {
            viewMode = VIEW_MODE_ACTION_SEND;

        } else {
            viewMode = bundle.getInt("view_mode");
        }
        getDataSet(intent, bundle, viewMode);


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
                    if (!favoriteMediaHashSet.contains(mediaSelected.getUri().toString()))
                        favoriteEditor.putString(mediaSelected.getUri().toString(), "");
                } else {
                    favoriteBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_24, getTheme()));
                    if (favoriteMediaHashSet.contains(mediaSelected.getUri().toString()))
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
        this.viewPager.setPageTransformer(new DepthPageTransformer());

        isSlideShow = bundle.getBoolean("isSlideShow");
        if (isSlideShow) {
            Handler handler = new Handler();
            Runnable update = () -> {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            };
            timerSlideShow = new Timer();
            timerSlideShow.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, 1000, 1000);
        }

    }

    private void getDataSet(Intent intent, Bundle bundle, int viewMode) {

        if (viewMode == VIEW_MODE_ALL) {
            Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);
        } else if (viewMode == VIEW_MODE_SECURE_ALBUM) {
            Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);
            curDefaultAlbum = bundle.getParcelable("curAlbum");
            mediaArrayList.clear();
            mediaArrayList.addAll(curDefaultAlbum.getMediaArrayList());
        } else if (viewMode == VIEW_MODE_ALBUM) {
            curDefaultAlbum = bundle.getParcelable("curAlbum");
            mediaArrayList.clear();
            mediaArrayList.addAll(curDefaultAlbum.getMediaArrayList());
        } else if (viewMode == VIEW_MODE_TYPE_MEDIA) {
            Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);
            curDefaultAlbum = bundle.getParcelable("curAlbum");
            mediaArrayList.clear();
            mediaArrayList.addAll(curDefaultAlbum.getMediaArrayList());
        } else if (viewMode == VIEW_MODE_ACTION_SEND) {
            Uri imgUri = intent.getData();
            String filePath = FileHandler.getFilePath(imgUri, getContentResolver());
            if (filePath != null) {
                File file = new File(filePath);
                FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
                mmr.setDataSource(filePath);
                String size = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE);
                String resolution = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) + "x" + mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                String fileName = FileHandler.getFileName(file);
                if (duration > 0) {
                    VideoInfo newVideo = new VideoInfo(imgUri, size, null, resolution, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, fileName, null, duration, 0, false, false);
                    this.mediaArrayList.add(newVideo);
                } else {
                    ImageInfo newImage = new ImageInfo(imgUri, size, null, resolution, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, fileName, null, 0, false, false);
                    this.mediaArrayList.add(newImage);
                }
            } else {
                Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);
            }
        }
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
                if (viewMode != VIEW_MODE_ALBUM)
                    srcMedia.getMediaDetail(defaultAlbumArrayList.get(srcMedia.getAlbumIn()), getContentResolver(), this);
                else
                    srcMedia.getMediaDetail(curDefaultAlbum, getContentResolver(), this);
                View dialogRoot = LayoutInflater.from(this).inflate(R.layout.dialog_media_detail, null);
                TextView fileName = dialogRoot.findViewById(R.id.filename_text);
                TextView dataAdd = dialogRoot.findViewById(R.id.date_add_text);
                TextView albumPath = dialogRoot.findViewById(R.id.album_path_text);
                TextView size = dialogRoot.findViewById(R.id.fileSize_text);
                TextView resolution = dialogRoot.findViewById(R.id.resolution_text);
                TextView location = dialogRoot.findViewById(R.id.location_text);
                fileName.setText(srcMedia.getFileName());
                dataAdd.setText(Media.getDate(Long.parseLong(srcMedia.getDate())).toString());
                if (curDefaultAlbum == null) {
                    albumPath.setText(defaultAlbumArrayList.get(srcMedia.getAlbumIn()).getAlbumPath());
                } else {
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
            case R.id.stop_slideshow_opt:
                if (timerSlideShow != null) {
                    timerSlideShow.cancel();
                    isSlideShow = false;
                    invalidateOptionsMenu();
                }
                return true;
            case R.id.unlock_opt:
                FragmentManager fragmentManager = getSupportFragmentManager();
                AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(this.defaultAlbumArrayList,
                        this.mediaArrayList,
                        viewPager.getCurrentItem(),
                        AlbumSelectDialogFragment.MOVE_TO_ALBUM_MODE);
                newFragment.show(fragmentManager, "dialog");
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setImageAsWallpaper() {
//        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
//        // set the wallpaper by calling the setResource function and
//        // passing the drawable file
//        wallpaperManager.getCropAndSetWallpaperIntent(uriArrayList.get(viewPager.getCurrentItem()));
        Intent setWallPaperIntent = new Intent(Intent.ACTION_ATTACH_DATA);
        setWallPaperIntent.addCategory(Intent.CATEGORY_DEFAULT);
        setWallPaperIntent.setDataAndType(mediaArrayList.get(viewPager.getCurrentItem()).getUri(), "image/jpeg");
        setWallPaperIntent.putExtra("mimeType", "image/jpeg");
        this.startActivity(Intent.createChooser(setWallPaperIntent, "Set as:"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isSlideShow)
            inflater.inflate(R.menu.slideshow_menu, menu);
        else if (isSecureMode)
            inflater.inflate(R.menu.secure_media_view_menu, menu);
        else
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
                if (!isSecureMode) {
                    this.buttonLayout.setVisibility(View.VISIBLE);
                }
                this.isNavigateVisible = true;
            }
        }
    }


}

