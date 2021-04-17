package com.example.galleryproject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.zip.Inflater;

public class OneMediaViewFragment extends Fragment {
    Uri uri;

    public OneMediaViewFragment(Uri uri) {
        this.uri = uri;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_one_media, container, false);
        Glide.with(this)
            .load(this.uri)
            .fitCenter()
            .error(R.drawable.ic_noun_cat_search_232263)
            .into((ImageView) root.findViewById(R.id.image_viewed));
        return root;
    }
}