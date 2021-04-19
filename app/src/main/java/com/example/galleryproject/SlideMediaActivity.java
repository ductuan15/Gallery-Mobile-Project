package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.galleryproject.data.ImageInfo;


import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SlideMediaActivity extends AppCompatActivity {
    ViewPager2 viewPager;
    ArrayList<Uri> uriArrayList;
    private FragmentStateAdapter pagerAdapter;
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
        pagerAdapter = new SlideMediaAdapter(this);
        this.viewPager.setAdapter(pagerAdapter);
        this.viewPager.setCurrentItem(bundle.getInt("imgPos"));
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

