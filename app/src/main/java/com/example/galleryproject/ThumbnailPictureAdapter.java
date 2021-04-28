package com.example.galleryproject;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
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
    private boolean isMultiSelectMode;

    // this interface will listen to click
    private final View.OnClickListener onItemClickListener;
    private final View.OnLongClickListener onLongClickListener;

    // ViewHolder class
    public static class ThumbnailPictureViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final Chip videoDurationChip;
        final CheckBox selectCheckBox;
        final AdapterView.OnClickListener onItemClickListener;
        final View.OnLongClickListener onLongClickListener;

        public ThumbnailPictureViewHolder(@NonNull View itemView, int mediaType, AdapterView.OnClickListener onItemClickListener, View.OnLongClickListener onLongClickListener, boolean isSelectMode) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            this.onLongClickListener = onLongClickListener;

            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                this.imageView = itemView.findViewById(R.id.thumbnail_video_holder);
                videoDurationChip = itemView.findViewById(R.id.duration_chip);
            } else {
                this.imageView = itemView.findViewById(R.id.thumbnail_pic_holder);
                videoDurationChip = null;
            }
            this.selectCheckBox = itemView.findViewById(R.id.checkBox);
            this.imageView.setTag(this);
            this.imageView.setOnClickListener(this.onItemClickListener);
            this.imageView.setOnLongClickListener(this.onLongClickListener);
        }

        public void setIsSelectMode(boolean isSelectMode){
            if(isSelectMode)
                this.selectCheckBox.setVisibility(View.VISIBLE);
            else{
                this.selectCheckBox.setChecked(false);
                this.selectCheckBox.setVisibility(View.INVISIBLE);
            }
        }

        public void changeSelectState(){
            this.selectCheckBox.setChecked(!this.selectCheckBox.isChecked());
        }
    }

    public ThumbnailPictureAdapter(ArrayList<Media> mediaArrayList, Context context, View.OnClickListener onItemClickListener, View.OnLongClickListener onLongClickListener) {
        this.mediaArrayList = mediaArrayList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    public void setMediaArrayList(ArrayList<Media> mediaArrayList) {
        this.mediaArrayList = mediaArrayList;
        this.notifyDataChange();
    }

    @NonNull
    @Override
    public ThumbnailPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_video, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_pic, parent, false);
        return new ThumbnailPictureViewHolder(view, viewType, this.onItemClickListener, onLongClickListener, isMultiSelectMode);
    }

    @Override
    public int getItemViewType(int position) {
        return this.mediaArrayList.get(position).getMEDIA_TYPE();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    // get element  according to position
    public void onBindViewHolder(@NonNull ThumbnailPictureViewHolder holder, int position) {
        if (this.mediaArrayList.get(position).getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            ImageInfo curMedia = (ImageInfo) this.mediaArrayList.get(position);
            Glide.with(this.context)
                    .load(curMedia.getUri())
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
            holder.setIsSelectMode(this.isMultiSelectMode);
        } else {
            VideoInfo curMedia = (VideoInfo) this.mediaArrayList.get(position);
            if (holder.videoDurationChip != null) {
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
            holder.setIsSelectMode(this.isMultiSelectMode);
        }
    }


    //return number of items
    @Override
    public int getItemCount() {
        return this.mediaArrayList.size();
    }

    public boolean isMultiSelectMode(){
        return this.isMultiSelectMode;
    }
    public void notifyDataChange() {
        this.notifyDataSetChanged();
    }

    public void setMultiSelectMode(boolean isMultiSelectMode) {
        this.isMultiSelectMode = isMultiSelectMode;
    }
}
