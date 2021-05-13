package com.example.galleryproject.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.galleryproject.entity.CreatedAlbum;
import com.example.galleryproject.entity.FavoriteMedia;

import java.util.List;

@Dao
public interface FavoriteMediaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(FavoriteMedia favoriteMedia);

    @Delete
    void delete(FavoriteMedia favoriteMedia);

    @Query("SELECT * FROM favorite_media")
    LiveData<List<FavoriteMedia>> getAllCreatedAlbum();

    @Query("SELECT uri FROM favorite_media")
    LiveData<List<String>> getAllFavoriteMediaUri();

}
