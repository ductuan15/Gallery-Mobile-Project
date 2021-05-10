package com.example.galleryproject.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.galleryproject.entity.CreatedAlbum;

import java.util.List;

@Dao
public interface CreatedAlbumDao {

    @Insert
    void insert(CreatedAlbum createdAlbum);

    @Delete
    void delete(CreatedAlbum createdAlbum);

    @Update
    void update(CreatedAlbum createdAlbum);

    @Query("SELECT * FROM create_album ORDER BY id ASC")
    LiveData<List<CreatedAlbum>> getAllCreatedAlbum();
}
