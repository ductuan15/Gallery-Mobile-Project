package com.example.galleryproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.DetailsLookup;

import java.util.ArrayList;


public class AlbumViewActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnLongClickListener {
    ArrayList<DefaultAlbum> defaultAlbumArrayList = new ArrayList<>();
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    RecyclerView listViewAllAlbum;
    RecyclerView gridViewAllMedia;
    ThumbnailPictureAdapter thumbnailPictureAdapter;
    SelectionTracker<Long> selectionTracker;
    int albumSelectedPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        Bundle data = getIntent().getExtras();

        //get all album data
        int len = this.albumSelectedPos = data.getInt("albumLen");
        for (int i = 0; i < len; i++)
            defaultAlbumArrayList.add(data.getParcelable(String.valueOf(i)));

        this.albumSelectedPos = data.getInt("albumPos");

        // set up album recycleView
        ThumbnailAlbumAdapter thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(this.defaultAlbumArrayList, this, this);

        this.listViewAllAlbum = findViewById(R.id.list_view_thumbnail_album);
        this.listViewAllAlbum.setAdapter(thumbnailAlbumAdapter);
        this.listViewAllAlbum.setLayoutManager(new LinearLayoutManager(this));

        //scrolling to current album
        listViewAllAlbum.scrollToPosition(this.albumSelectedPos);


        // get all media of album
        this.mediaArrayList = this.defaultAlbumArrayList.get(this.albumSelectedPos).getMediaArrayList();

        //set up  recycleView
//        StableIdKeyProvider keyProvider = new StableIdKeyProvider(this.gridViewAllMedia);
//        selectionTracker = new SelectionTracker.Builder<>(
//                "media_select",
//                this.gridViewAllMedia,
//                keyProvider,
//                new DetailsLookup(this.gridViewAllMedia),
//                StorageStrategy.createLongStorage()).build();
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList,this, selectionTracker, this);
        this.gridViewAllMedia = findViewById(R.id.grid_view_thumbnail_media);
        this.gridViewAllMedia.setAdapter(thumbnailPictureAdapter);
        this.gridViewAllMedia.setLayoutManager(new GridLayoutManager(this, 3));
    }

    @Override
    public void onClick(View v) {
        ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder viewHolder = (ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        if (pos != this.albumSelectedPos) {
            this.mediaArrayList = this.defaultAlbumArrayList.get(pos).getMediaArrayList();
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
