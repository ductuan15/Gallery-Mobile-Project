package com.example.galleryproject.ui;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.ThumbnailPictureAdapter;

public class DetailsLookup extends ItemDetailsLookup<Long> {
    RecyclerView recyclerView;
    public DetailsLookup(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Nullable
    @Override
    public ItemDetails<Long> getItemDetails(@NonNull MotionEvent e) {
        View view = recyclerView.findChildViewUnder(e.getX(),e.getY());
        if(view != null){
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if(viewHolder instanceof ThumbnailPictureAdapter.ThumbnailPictureViewHolder){
                return new ItemDetails<Long>() {
                    @Override
                    public int getPosition() {
                        return viewHolder.getAdapterPosition();
                    }

                    @Nullable
                    @Override
                    public Long getSelectionKey() {
                        return viewHolder.getItemId();
                    }
                };
            }
        }
        return null;
    }
}
