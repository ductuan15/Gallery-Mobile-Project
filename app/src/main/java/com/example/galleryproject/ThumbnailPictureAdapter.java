package com.example.galleryproject;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.data.ImageInfo;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.data.VideoInfo;
import com.google.android.material.chip.Chip;


import java.util.ArrayList;

public class ThumbnailPictureAdapter extends RecyclerView.Adapter<ThumbnailPictureAdapter.ThumbnailPictureViewHolder> {
    private ArrayList<Media> mediaArrayList;
    private final Context context;


    // this interface will listen to click
    private final AdapterView.OnItemClickListener onItemClickListener;


    // ViewHolder class
    public static class ThumbnailPictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final Chip videoDurationChip;
        final AdapterView.OnItemClickListener onItemClickListener;

        public ThumbnailPictureViewHolder(@NonNull View itemView, int mediaType, AdapterView.OnItemClickListener onItemClickListener) {
            super(itemView);

            if(mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO){
                this.imageView = itemView.findViewById(R.id.thumbnail_video_holder);
                videoDurationChip = itemView.findViewById(R.id.duration_chip);
            }else{
                this.imageView = itemView.findViewById(R.id.thumbnail_pic_holder);
                videoDurationChip = null;
            }

            this.imageView.setOnClickListener(this);

            this.onItemClickListener = onItemClickListener;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        @Override
        public void onClick(View v) {
            this.onItemClickListener.onItemClick(null, v, getAdapterPosition(), v.getId());
        }
    }

    public ThumbnailPictureAdapter(ArrayList<Media> mediaArrayList, Context context, AdapterView.OnItemClickListener onItemClickListener) {
        this.mediaArrayList = mediaArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        notifyDataSetChanged();
    }

    public void setMediaArrayList(ArrayList<Media> mediaArrayList) {
        this.mediaArrayList = mediaArrayList;
        this.notifyDataChange();
    }

    @NonNull
    @Override
    public ThumbnailPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_video, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_pic, parent, false);
        return new ThumbnailPictureViewHolder(view, viewType,this.onItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return this.mediaArrayList.get(position).getMEDIA_TYPE();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    // get element  according to position
    public void onBindViewHolder(@NonNull ThumbnailPictureViewHolder holder, int position) {
        if(this.mediaArrayList.get(position).getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
            ImageInfo curMedia = (ImageInfo) this.mediaArrayList.get(position);
            Glide.with(this.context)
                    .load(curMedia.getUri())
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
        }else{
            VideoInfo curMedia = (VideoInfo) this.mediaArrayList.get(position);
            if(holder.videoDurationChip!=null) {
                holder.videoDurationChip.setText(curMedia.getDuration());
                holder.videoDurationChip.setChipIconResource(R.drawable.ic_baseline_play_arrow_24);
            }
            Glide.with(this.context)
                    .load(curMedia.getUri())
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
        }
    }


    //return number of items
    @Override
    public int getItemCount() {
        return this.mediaArrayList.size();
    }

    public void notifyDataChange() {
        this.notifyDataSetChanged();
    }
}
