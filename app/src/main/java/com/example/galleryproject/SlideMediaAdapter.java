package com.example.galleryproject;

import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SlideMediaAdapter extends FragmentStateAdapter {

    SlideMediaActivity slideMediaActivity;
    Size screenSize;
    private final View.OnClickListener onItemClickListener;

    public SlideMediaAdapter(@NonNull SlideMediaActivity slideMediaActivity, View.OnClickListener onItemClickListener) {
        super(slideMediaActivity);
        this.slideMediaActivity = slideMediaActivity;
        this.onItemClickListener = onItemClickListener;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.slideMediaActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        screenSize = new Size(width, height);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new OneMediaViewFragment(this.slideMediaActivity.mediaArrayList.get(position), screenSize, this.onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.slideMediaActivity.mediaArrayList.size();
    }
}