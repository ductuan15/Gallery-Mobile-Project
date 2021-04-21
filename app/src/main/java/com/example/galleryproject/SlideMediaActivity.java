package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.galleryproject.data.Media;


import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlideMediaActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    ImageButton shareBtn, deleteBtn;


    @RequiresApi(api = Build.VERSION_CODES.R)
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

        // get all media
        Media.getAllMediaUri(this,mediaArrayList,null);

        // set ip adapter
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
                Uri uriToImage = mediaArrayList.get(viewPager.getCurrentItem()).getUri();
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
                Uri imageUri = mediaArrayList.get(viewPager.getCurrentItem()).getUri();                        // URI of the image to remove.
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
                    onBackPressed();
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

    private static class SlideMediaAdapter extends FragmentStateAdapter{

        SlideMediaActivity slideMediaActivity;
        public SlideMediaAdapter(@NonNull SlideMediaActivity slideMediaActivity) {
            super(slideMediaActivity);
            this.slideMediaActivity =  slideMediaActivity;

        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Uri uri = this.slideMediaActivity.mediaArrayList.get(position).getUri();

            return new OneMediaViewFragment(uri);
        }

        @Override
        public int getItemCount() {
            return this.slideMediaActivity.mediaArrayList.size();
        }
    }


}

