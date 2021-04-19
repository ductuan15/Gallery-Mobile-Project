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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
    ImageButton shareBtn;
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

        shareBtn = findViewById(R.id.share_button);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uriToImage = this.slideMediaActivity.uriArrayList.get(position);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
                    shareIntent.setType("image/jpeg");
                    startActivity(Intent.createChooser(shareIntent, "Share to");
                } catch (Exception e){
                    Log.e("Error", e.getMessage());
                }

            }
        })
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

