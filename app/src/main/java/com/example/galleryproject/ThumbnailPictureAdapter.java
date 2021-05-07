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
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.data.ImageInfo;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.data.VideoInfo;
import com.google.android.material.chip.Chip;


import java.util.ArrayList;

import kotlinx.coroutines.selects.SelectBuilder;

public class ThumbnailPictureAdapter extends RecyclerView.Adapter<ThumbnailPictureAdapter.ThumbnailPictureViewHolder> {
    private ArrayList<Media> mediaArrayList;
    private final Context context;
    private boolean isMultiSelectMode;
    private SelectionTracker<Long> selectionTracker;

    // this interface will listen to click
    private final View.OnClickListener onItemClickListener;

    // ViewHolder class
    public static class ThumbnailPictureViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final Chip videoDurationChip;
        final CheckBox selectCheckBox;
        final AdapterView.OnClickListener onItemClickListener;

        public ThumbnailPictureViewHolder(@NonNull View itemView, int mediaType, AdapterView.OnClickListener onItemClickListener, boolean isSelectMode) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;

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
        }

        public void setIsSelectMode(boolean isSelectMode) {
            if (isSelectMode)
                this.selectCheckBox.setVisibility(View.VISIBLE);
            else {
                this.selectCheckBox.setChecked(false);
                this.selectCheckBox.setVisibility(View.INVISIBLE);
            }
        }

        public void changeSelectState() {
            this.selectCheckBox.setChecked(!this.selectCheckBox.isChecked());
        }

        public boolean isSelected() {
            return this.selectCheckBox.isChecked();
        }
    }

    public ThumbnailPictureAdapter(ArrayList<Media> mediaArrayList, Context context, SelectionTracker<Long> selectionTracker, View.OnClickListener onItemClickListener) {
        this.setHasStableIds(true);
        this.mediaArrayList = mediaArrayList;
        this.context = context;
        this.selectionTracker = selectionTracker;
        this.onItemClickListener = onItemClickListener;
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
        return new ThumbnailPictureViewHolder(view, viewType, this.onItemClickListener, isMultiSelectMode);
    }

    @Override
    public int getItemViewType(int position) {
        return this.mediaArrayList.get(position).getMEDIA_TYPE();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    // get element  according to position
    public void onBindViewHolder(@NonNull ThumbnailPictureViewHolder holder, int position) {

        //TODO: fix this code
        if (this.mediaArrayList.get(position).getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            ImageInfo curMedia = (ImageInfo) this.mediaArrayList.get(position);
            Glide.with(this.context)
                    .load(curMedia.getUri())
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);

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
        }
        boolean isSelected = false;
        if(selectionTracker != null){
            if(selectionTracker.hasSelection()){
                holder.selectCheckBox.setVisibility(View.VISIBLE);
                isSelected = selectionTracker.isSelected(getItemId(position));
                if(isSelected){
                    holder.selectCheckBox.setChecked(true);
                }else{
                    holder.selectCheckBox.setChecked(false);
                }
            }else{
                holder.selectCheckBox.setVisibility(View.INVISIBLE);
            }
        }
        holder.imageView.setActivated(isSelected);

    }

    //return number of items
    @Override
    public int getItemCount() {
        return this.mediaArrayList.size();
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    public long getItemId(int pos) {
        return (long) pos;
    }

    public boolean isMultiSelectMode() {
        return this.isMultiSelectMode;
    }

    public void notifyDataChange() {
        this.notifyDataSetChanged();
    }

    public void setMultiSelectMode(boolean isMultiSelectMode) {
        this.isMultiSelectMode = isMultiSelectMode;
    }
}
