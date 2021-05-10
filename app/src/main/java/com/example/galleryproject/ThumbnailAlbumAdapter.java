package com.example.galleryproject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.data.DefaultAlbum;

import java.util.ArrayList;

public class ThumbnailAlbumAdapter extends RecyclerView.Adapter<ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder> {


    ArrayList<DefaultAlbum> defaultAlbumArrayList;
    Context context;
    // this interface will listen to click
    private final View.OnClickListener onItemClickListener;


    // TODO: get all image
    public static class ThumbnailAlbumViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView albumNameTextView;
        public ThumbnailAlbumViewHolder(@NonNull View itemView,View.OnClickListener onItemClickListener) {
            super(itemView);
            // set tag as itemview to get itemview when click
            itemView.setTag(this);
            // set onclick Fragment/Activity
            itemView.setOnClickListener(onItemClickListener);
            this.imageView = (ImageView) itemView.findViewById(R.id.thumbnail_album_holder);
            this.albumNameTextView = (TextView) itemView.findViewById(R.id.album_name_holder);
        }

    }

    public  ThumbnailAlbumAdapter(ArrayList<DefaultAlbum> defaultAlbumArrayList, View.OnClickListener onItemClickListener, Context context){
        this.context = context;
        this.defaultAlbumArrayList = defaultAlbumArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ThumbnailAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_album,parent,false);

        return new ThumbnailAlbumViewHolder(view,this.onItemClickListener);
    }

    // TODO: get element  according to position
    @Override
    public void onBindViewHolder(@NonNull ThumbnailAlbumViewHolder holder, int position) {
        DefaultAlbum defaultAlbum = this.defaultAlbumArrayList.get(position);
        // TODO:replace the image that we need
        Uri thumbnailUri = defaultAlbum.getUriThumbnail();
        if(thumbnailUri != null){
            Glide.with(this.context)
                    .load(defaultAlbum.getUriThumbnail())
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
        }
        else{
            Glide.with(this.context)
                    .load(R.drawable.ic_noun_cat_search_232263)
                    .placeholder(R.drawable.ic_noun_cat_search_232263)
                    .error(R.drawable.ic_noun_cat_search_232263)
                    .centerCrop()
                    .fitCenter()
                    .into(holder.imageView);
        }
        // TODO: replace by the name of albums
        holder.albumNameTextView.setText(defaultAlbum.getAlbumName());
    }


    //return number of items
    @Override
    public int getItemCount() {
        return this.defaultAlbumArrayList.size();
    }
    public void notifyDataChange(){
        this.notifyDataSetChanged();
    }
}
