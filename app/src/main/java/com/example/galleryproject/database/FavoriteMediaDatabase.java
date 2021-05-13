package com.example.galleryproject.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.galleryproject.dao.CreatedAlbumDao;
import com.example.galleryproject.dao.FavoriteMediaDao;
import com.example.galleryproject.entity.CreatedAlbum;
import com.example.galleryproject.entity.FavoriteMedia;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FavoriteMedia.class},version = 1)
public abstract class FavoriteMediaDatabase extends RoomDatabase {
    private static FavoriteMediaDatabase instance;

    public abstract FavoriteMediaDao favoriteMediaDao();
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized  FavoriteMediaDatabase getInstance(Context context){
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    FavoriteMediaDatabase.class, "favorite_media_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
