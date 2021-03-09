package com.example.galleryproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThumbnailPictureAdapter extends RecyclerView.Adapter<ThumbnailPictureAdapter.ThumbnailPictureViewHolder> {
    private String[] imgPath;

    public class ThumbnailPictureViewHolder extends RecyclerView.ViewHolder{
        private  final ImageView imageView;
        public ThumbnailPictureViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO: Implement click listener
            this.imageView = (ImageView) itemView.findViewById(R.id.thumbnail_pic_holder);
        }
        public ImageView getImageView(){
            return this.imageView;
        }
    }

    // TODO: get all image
    public ThumbnailPictureAdapter(String[] imgPath){
        this.imgPath = imgPath;
    }

    @NonNull
    @Override
    public ThumbnailPictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_pic,parent,false);
        return  new ThumbnailPictureViewHolder(view);
    }

    @Override
    // get element  according to position
    public void onBindViewHolder(@NonNull ThumbnailPictureViewHolder holder, int position) {
        // TODO:replace the image that we need
        holder.getImageView().setImageResource(R.drawable.game_lauchericon_background);
    }
    //return number of items
    @Override
    public int getItemCount() {
        return this.imgPath.length;
    }


}
