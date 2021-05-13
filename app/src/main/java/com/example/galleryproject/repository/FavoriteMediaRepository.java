package com.example.galleryproject.repository;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.galleryproject.dao.FavoriteMediaDao;
import com.example.galleryproject.database.FavoriteMediaDatabase;
import com.example.galleryproject.entity.CreatedAlbum;
import com.example.galleryproject.entity.FavoriteMedia;

import java.util.List;

public class FavoriteMediaRepository {
    private FavoriteMediaDao favoriteMediaDao;
    private LiveData<List<FavoriteMedia>> favoriteList;
    public FavoriteMediaRepository(Application application){
        FavoriteMediaDatabase favoriteMediaDatabase = FavoriteMediaDatabase.getInstance(application);
        favoriteMediaDao = favoriteMediaDatabase.favoriteMediaDao();
        favoriteList = favoriteMediaDao.getAllCreatedAlbum();
    }
    public void insert(FavoriteMedia favoriteMedia){
        FavoriteMediaDatabase.databaseWriteExecutor.execute(() -> {
          favoriteMediaDao.insert(favoriteMedia);
        });
    }
    public void delete(FavoriteMedia favoriteMedia){
        FavoriteMediaDatabase.databaseWriteExecutor.execute(() -> {
            favoriteMediaDao.delete(favoriteMedia);
        });

    }
    public LiveData<List<String>> getFavoriteMediaUriList(){
        return favoriteMediaDao.getAllFavoriteMediaUri();
    }
    public LiveData<List<FavoriteMedia>> getFavoriteList(){
        return this.favoriteList;
    }
//    public static class InsertHandlerThread extends HandlerThread {
//        private Handler handler;
//        private final FavoriteMediaDao favoriteMediaDao;
//        public InsertHandlerThread(FavoriteMediaDao favoriteMediaDao) {
//            super("InsertHandlerThread");
//            this.favoriteMediaDao = favoriteMediaDao;
//        }
//
//        @Override
//        protected void onLooperPrepared() {
//            handler = new Handler(Looper.getMainLooper());
//            handler.post(() -> {
//                this.favoriteMediaDao.insert();
//            });
//        }
//    }

}
