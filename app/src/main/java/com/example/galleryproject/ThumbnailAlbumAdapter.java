package com.example.galleryproject;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.galleryproject.data.Album;

import java.util.ArrayList;

public class ThumbnailAlbumAdapter extends RecyclerView.Adapter<ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder> {


    ArrayList<Album> albumArrayList;
    Context context;


    // TODO: get all image
    public static class ThumbnailAlbumViewHolder extends RecyclerView.ViewHolder{
        final ImageView imageView;
        final TextView albumNameTextView;
        public ThumbnailAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO: Implement click listener
            this.imageView = (ImageView) itemView.findViewById(R.id.thumbnail_album_holder);

            this.albumNameTextView = (TextView) itemView.findViewById(R.id.album_name_holder);

        }
    }

    //TODO: get all albums
    public  ThumbnailAlbumAdapter(ArrayList<Album> albumArrayList, Context context){
        this.context = context;
        this.albumArrayList = albumArrayList;
    }

    @NonNull
    @Override
    public ThumbnailAlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thumbnail_album,parent,false);

        return new ThumbnailAlbumViewHolder(view);
    }

    // TODO: get element  according to position
    @Override
    public void onBindViewHolder(@NonNull ThumbnailAlbumViewHolder holder, int position) {
        Album album = this.albumArrayList.get(position);
        // TODO:replace the image that we need
        Glide.with(this.context)
                .load(album.getUriThumbnail())
                .placeholder(R.drawable.ic_noun_cat_search_232263)
                .error(R.drawable.ic_noun_cat_search_232263)
                .centerCrop()
                .fitCenter()
                .into(holder.imageView);
        // TODO: replace by the name of albums
        holder.albumNameTextView.setText(album.getAlbumName());

    }


    //return number of items
    @Override
    public int getItemCount() {
        return this.albumArrayList.size();
    }
}
