package com.example.galleryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.galleryproject.data.Album;
import com.example.galleryproject.data.Media;

import java.util.ArrayList;


public class AlbumViewActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnLongClickListener {
    ArrayList<Album> albumArrayList = new ArrayList<>();
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    RecyclerView listViewAllAlbum;
    RecyclerView gridViewAllMedia;
    ThumbnailPictureAdapter thumbnailPictureAdapter;
    int albumSelectedPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        Bundle data = getIntent().getExtras();

        //get all album data
        int len = this.albumSelectedPos = data.getInt("albumLen");
        for (int i = 0; i < len; i++)
            albumArrayList.add(data.getParcelable(String.valueOf(i)));

        this.albumSelectedPos = data.getInt("albumPos");

        // set up album recycleView
        ThumbnailAlbumAdapter thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(this.albumArrayList, this, this);

        this.listViewAllAlbum = findViewById(R.id.list_view_thumbnail_album);
        this.listViewAllAlbum.setAdapter(thumbnailAlbumAdapter);
        this.listViewAllAlbum.setLayoutManager(new LinearLayoutManager(this));

        //scrolling to current album
        listViewAllAlbum.scrollToPosition(this.albumSelectedPos);


        // get all media of album
        this.mediaArrayList = this.albumArrayList.get(this.albumSelectedPos).getMediaArrayList();

        //set up  recycleView
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList, this, this, this);
        this.gridViewAllMedia = findViewById(R.id.grid_view_thumbnail_media);
        this.gridViewAllMedia.setAdapter(thumbnailPictureAdapter);
        this.gridViewAllMedia.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    public void onClick(View v) {
        ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder viewHolder = (ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        if (pos != this.albumSelectedPos) {
            this.mediaArrayList = this.albumArrayList.get(pos).getMediaArrayList();
            this.thumbnailPictureAdapter.setMediaArrayList(this.mediaArrayList);
            this.thumbnailPictureAdapter.notifyDataChange();
            this.albumSelectedPos = pos;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
