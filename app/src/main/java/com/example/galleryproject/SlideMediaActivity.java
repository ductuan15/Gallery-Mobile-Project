package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.galleryproject.data.ImageInfo;


import java.io.IOException;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlideMediaActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    ArrayList<Uri> uriArrayList;
    ImageButton shareBtn, deleteBtn;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_media);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        bundle.getInt("imgPos");
        this.viewPager  = findViewById(R.id.media_viewpager);
        this.uriArrayList =  ImageInfo.getAllPic(this);
        FragmentStateAdapter pagerAdapter = new SlideMediaAdapter(this);
        this.viewPager.setAdapter(pagerAdapter);
        this.viewPager.setCurrentItem(bundle.getInt("imgPos"));

        ActionBar actionBar = getSupportActionBar();                                                            //change color for actionbar
        ColorDrawable colorDrawable;
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            colorDrawable = new ColorDrawable(getResources().getColor(R.color.purple_200));
            actionBar.setBackgroundDrawable(colorDrawable);
        }
        else {
            colorDrawable = new ColorDrawable(Color.parseColor("#0F9D58"));
            actionBar.setBackgroundDrawable(colorDrawable);
        }


        shareBtn = findViewById(R.id.share_button);
        shareBtn.setOnClickListener(v -> {
            try {
                Uri uriToImage = uriArrayList.get(viewPager.getCurrentItem());
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                shareIntent.setType("image/jpeg");
                startActivity(Intent.createChooser(shareIntent, "Share to"));
            } catch (Exception e){
                Log.e("Error", e.getMessage());
            }

        });

        deleteBtn = findViewById(R.id.delete_button);
        deleteBtn.setOnClickListener(v -> {
            try {
                ContentResolver resolver = getApplicationContext().getContentResolver();            // Remove a specific media item.
                Uri imageUri = uriArrayList.get(viewPager.getCurrentItem());                        // URI of the image to remove.
                // Perform the actual removal.
                int numImagesRemoved = resolver.delete(
                        imageUri,
                        null,
                        null);
                if(numImagesRemoved == 0){
                    Toast.makeText(this, "Delete unsuccessfully", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Delete successfully", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Log.e("Error", e.getMessage());
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viewpicture_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_location_tag_opt:

                return true;
            case R.id.lock_picture:

                return true;
            case R.id.detail_button:

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
        intent.setDataAndType(uriArrayList.get(viewPager.getCurrentItem()), "image/jpeg");
        intent.putExtra("mimeType", "image/jpeg");
        this.startActivity(Intent.createChooser(intent, "Set as:"));
    }

    private static class SlideMediaAdapter extends FragmentStateAdapter{

        SlideMediaActivity slideMediaActivity;
        public SlideMediaAdapter(@NonNull SlideMediaActivity slideMediaActivity) {
            super(slideMediaActivity);
            this.slideMediaActivity =  slideMediaActivity;

        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Uri uri = this.slideMediaActivity.uriArrayList.get(position);

            return new OneMediaViewFragment(uri);
        }

        @Override
        public int getItemCount() {
            return this.slideMediaActivity.uriArrayList.size();
        }
    }


}

