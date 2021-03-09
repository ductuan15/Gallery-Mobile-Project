package com.example.galleryproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThumbnailAlbumAdapter extends RecyclerView.Adapter<ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder> {

    private String [] albumPath;
    private String [] albumName;



    // TODO: get all image
    public class ThumbnailAlbumViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        private final TextView albumNameTextView;
        public ThumbnailAlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            //TODO: Implement click listener
            this.imageView = (ImageView) itemView.findViewById(R.id.thumbnail_album_holder);
            this.albumNameTextView = (TextView) itemView.findViewById(R.id.album_name_holder);
        }
        public ImageView getImageView(){
            return this.imageView;
        }
        public TextView getAlbumNameTextView(){
            return this.albumNameTextView;
        }
    }

    //TODO: get all albums
    public  ThumbnailAlbumAdapter(String [] albumPath, String [] albumName){
        this.albumName = albumName;
        this.albumPath = albumPath;
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
        // TODO:replace the image that we need
        holder.getImageView().setImageResource(R.drawable.game_lauchericon_background);
        // TODO: replace by the name of albums
        holder.getAlbumNameTextView().setText("Album name");
    }


    //return number of items
    @Override
    public int getItemCount() {
        return this.albumPath.length;
    }
}
