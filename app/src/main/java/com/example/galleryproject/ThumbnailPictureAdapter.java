package com.example.galleryproject;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.data.ImageInfo;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.data.VideoInfo;
import com.google.android.material.chip.Chip;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kotlinx.coroutines.selects.SelectBuilder;

public class ThumbnailPictureAdapter extends RecyclerView.Adapter<ThumbnailPictureAdapter.ThumbnailPictureViewHolder> {
    private ArrayList<Media> mediaArrayList;
    private final Context context;
    private SelectionTracker<Long> selectionTracker;
    // this interface will listen to click
    int showDatePos = -1;
    private final View.OnClickListener onItemClickListener;

    static int VIEW_BY_DATE = 1;
    static int VIEW_BY_MONTH = 2;
    static int VIEW_BY_YEAR = 3;
    static int VIEW_HOLDER_DATE_TYPE = -1;
    static SimpleDateFormat simpleDateFormat =  new  SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US );
    int curViewBy = VIEW_BY_DATE;


    boolean isSmall = false;

    // ViewHolder class
    public static class ThumbnailPictureViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        Chip videoDurationChip;
        CheckBox selectCheckBox;
        ImageView isFavoriteIcon;
        TextView textDate;
        int mediaType;
        AdapterView.OnClickListener onItemClickListener;

        public ThumbnailPictureViewHolder(@NonNull View itemView, int mediaType, AdapterView.OnClickListener onItemClickListener) {
            super(itemView);
            this.onItemClickListener = onItemClickListener;
            this.mediaType = mediaType;
            textDate = itemView.findViewById(R.id.data_show_text);
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                this.imageView = itemView.findViewById(R.id.thumbnail_video_holder);
                videoDurationChip = itemView.findViewById(R.id.duration_chip);
            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                this.imageView = itemView.findViewById(R.id.thumbnail_pic_holder);
                videoDurationChip = null;
            }
            this.selectCheckBox = itemView.findViewById(R.id.checkBox);
            this.isFavoriteIcon = itemView.findViewById(R.id.is_favorite_imageView);
            itemView.setTag(this);
            itemView.setOnClickListener(this.onItemClickListener);
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
        return new ThumbnailPictureViewHolder(view, viewType, this.onItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
//        if(position == 0){
//            return VIEW_HOLDER_DATE_TYPE;
//        }
//        Date nextDate = null;
//        try {
//          nextDate = simpleDateFormat.parse(mediaArrayList.get(position).getDate());
//            if( ((String)(DateFormat.format("dd",  curDate))).compareTo((String)DateFormat.format("dd",nextDate))==0){
//                curDate = nextDate;
//                return VIEW_HOLDER_DATE_TYPE;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return mediaArrayList.get(position).getMEDIA_TYPE();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    // get element  according to position
    public void onBindViewHolder(@NonNull ThumbnailPictureViewHolder holder, int position) {
        //TODO: fix this code
        if (holder.mediaType == VIEW_HOLDER_DATE_TYPE){
            holder.textDate.setText("Date");
            return;
        }
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
                if (isSmall) {
                    holder.videoDurationChip.setVisibility(View.INVISIBLE);
                } else {
                    holder.videoDurationChip.setVisibility(View.VISIBLE);
                    holder.videoDurationChip.setText(curMedia.getDuration());
                    holder.videoDurationChip.setChipIconResource(R.drawable.ic_baseline_play_arrow_24);
                }
            }

            Glide.with(this.context)
                    .load(curMedia.getUri())
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
        }
        if (mediaArrayList.get(position).isFavorite()) {
            holder.isFavoriteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.isFavoriteIcon.setVisibility(View.INVISIBLE);
        }
        boolean isSelected = false;
        if (selectionTracker != null) {
            if (selectionTracker.hasSelection()) {
                holder.selectCheckBox.setVisibility(View.VISIBLE);
                isSelected = selectionTracker.isSelected(getItemId(position));
                holder.selectCheckBox.setChecked(isSelected);
            } else {
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


    public void notifyDataChange() {
        this.notifyDataSetChanged();
    }

    public void setIsSmall(boolean isSmall) {
        this.isSmall = isSmall;
    }

    public void setShowDatePos(int showDatePos) {
        this.showDatePos = showDatePos;
    }
}
