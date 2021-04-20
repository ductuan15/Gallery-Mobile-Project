package com.example.galleryproject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.zip.Inflater;

public class OneMediaViewFragment extends Fragment  {
    Uri uri;
    SubsamplingScaleImageView photoView;
    boolean isZoomed = false;

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
        this.photoView = root.findViewById(R.id.image_viewed);
        this.photoView.setMaxScale(10.0f);
        //TODO: Fix the scale
        this.photoView.setDoubleTapZoomScale(2.0f);
        this.photoView.setImage(ImageSource.uri(this.uri));
        return root;
    }
}