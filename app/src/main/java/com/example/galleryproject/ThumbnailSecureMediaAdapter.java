package com.example.galleryproject;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.data.Media;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

public class ThumbnailSecureMediaAdapter extends RecyclerView.Adapter<ThumbnailSecureMediaAdapter.ThumbnailSecureMediaViewHolder> {
    final ArrayList<File> secureMediaFilePath;
    // this interface will listen to click
    final View.OnClickListener onItemClickListener;
    final Context context;

    public ThumbnailSecureMediaAdapter(ArrayList<File> secureMediaFilePath, View.OnClickListener onItemClickListener, Context context) {
        this.secureMediaFilePath = secureMediaFilePath;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public ThumbnailSecureMediaViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view;
        if (viewType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_video, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_pic, parent, false);
        return new ThumbnailSecureMediaAdapter.ThumbnailSecureMediaViewHolder(view, viewType, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailSecureMediaViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return this.secureMediaFilePath.size();
    }

    public static class ThumbnailSecureMediaViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final Chip videoDurationChip;
        final CheckBox selectCheckBox;
        int mediaType;
        public ThumbnailSecureMediaViewHolder(@NotNull View itemView,int mediaType,View.OnClickListener onItemClickListener) {
            super(itemView);
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                this.imageView = itemView.findViewById(R.id.thumbnail_video_holder);
                videoDurationChip = itemView.findViewById(R.id.duration_chip);
            } else {
                this.imageView = itemView.findViewById(R.id.thumbnail_pic_holder);
                videoDurationChip = null;
            }
            this.selectCheckBox = itemView.findViewById(R.id.checkBox);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

}
