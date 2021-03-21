package com.example.galleryproject;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import java.io.IOException;
import java.util.ArrayList;

public class ThumbnailPictureAdapter extends RecyclerView.Adapter<ThumbnailPictureAdapter.ThumbnailPictureViewHolder> {
    private ArrayList<Uri> uriArrayList;
    private Fragment fragment;
    public static class ThumbnailPictureViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ThumbnailPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO: Implement click listener
            this.imageView = itemView.findViewById(R.id.thumbnail_pic_holder);
        }

        public ImageView getImageView() {
            return this.imageView;
        }
    }

    // TODO: get all image
    public ThumbnailPictureAdapter(ArrayList<Uri> uriArrayList, Fragment fragment) {
        this.uriArrayList = uriArrayList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ThumbnailPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_pic,parent,false);
        return new ThumbnailPictureViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @NonNull
    @Override
    // get element  according to position
    public void onBindViewHolder(@NonNull ThumbnailPictureViewHolder holder, int position) {
        // TODO:replace the image that we need
        String url = this.uriArrayList.get(position).toString();
        Glide.with(this.fragment)
                .load(url)
                .optionalCenterCrop()
                .placeholder(R.drawable.ic_noun_cat_search_232263)
                .into(holder.getImageView());
    }

    //return number of items
    @Override
    public int getItemCount() {
        return this.uriArrayList.size();
    }


}
