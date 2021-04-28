package com.example.galleryproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.galleryproject.data.Media;
import com.google.android.material.chip.Chip;

import java.io.IOException;


public class OneMediaViewFragment extends Fragment implements View.OnClickListener {
    Media media;

    SubsamplingScaleImageView photoView;
    Size screenSize;
    private View.OnClickListener onItemClickListener;
    public OneMediaViewFragment(Media media, Size screenSize,View.OnClickListener onItemClickListener) {
        this.media = media;
        this.screenSize = screenSize;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_view_one_media, container, false);
        this.photoView = root.findViewById(R.id.image_viewed);
        this.photoView.setOnClickListener(this.onItemClickListener);
        this.photoView.setMaxScale(10.0f);
        //TODO: Fix the scale
        this.photoView.setDoubleTapZoomScale(2.0f);
        if (this.media.getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            this.photoView.setImage(ImageSource.uri(this.media.getUri()));
        }else{
            try {
                Bitmap thumbnail = requireActivity().getApplicationContext().getContentResolver().loadThumbnail(
                        this.media.getUri(),
                        this.screenSize,
                        null
                );
                Chip chip = root.findViewById(R.id.playVideo_chip);
                chip.setVisibility(View.VISIBLE);
                chip.setOnClickListener(this);
                this.photoView.setImage(ImageSource.bitmap(thumbnail));
                this.photoView.setOrientation(this.media.getOrientation());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("", "onCreateView: ERROR");
            }
        }

        return root;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.getActivity(),VideoPlayActivity.class);
        Bundle data = new Bundle();
        data.putParcelable("videoUri",this.media.getUri());
        intent.putExtras(data);
        startActivity(intent);
    }
}