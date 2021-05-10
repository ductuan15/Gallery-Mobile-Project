package com.example.galleryproject.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.galleryproject.database.CreatedAlbumDatabase;
import com.example.galleryproject.entity.CreatedAlbum;
import com.example.galleryproject.dao.CreatedAlbumDao;

import java.util.List;

public class CreatedAlbumRepository {
    private CreatedAlbumDao createdAlbumDao;
    private LiveData<List<CreatedAlbum>> arrayListCreatedAlbum;

    public CreatedAlbumRepository(Application application){
        CreatedAlbumDatabase createdAlbumDatabase = CreatedAlbumDatabase.getInstance(application);
        createdAlbumDao = createdAlbumDatabase.createdAlbumDao();
        arrayListCreatedAlbum = createdAlbumDao.getAllCreatedAlbum();
    }
    public void insert(CreatedAlbum createdAlbum){
        new InsertCreateAlbumAsyncTask(this.createdAlbumDao).execute(createdAlbum);
    }
    public void delete(CreatedAlbum createdAlbum){
        new DeleteCreateAlbumAsyncTask(this.createdAlbumDao).execute(createdAlbum);
    }
    public void update(CreatedAlbum createdAlbum){
        new UpdateCreateAlbumAsyncTask(this.createdAlbumDao).execute(createdAlbum);
    }
    public LiveData<List<CreatedAlbum>> getAllCreatedAlbum(){
        return this.arrayListCreatedAlbum;
    }

    //TODO: change this
    public static class InsertCreateAlbumAsyncTask extends  AsyncTask<CreatedAlbum,Void, Void>{
        private final CreatedAlbumDao createdAlbumDao;
        private InsertCreateAlbumAsyncTask(CreatedAlbumDao createdAlbumDao){
            this.createdAlbumDao = createdAlbumDao;
        }

        @Override
        protected Void doInBackground(CreatedAlbum... createdAlbums) {
            Log.e("","Size: " + createdAlbums.length);
            this.createdAlbumDao.insert(createdAlbums[0]);
            return null;
        }
    }
    public static class DeleteCreateAlbumAsyncTask extends  AsyncTask<CreatedAlbum,Void, Void>{
        private final CreatedAlbumDao createdAlbumDao;
        private DeleteCreateAlbumAsyncTask(CreatedAlbumDao createdAlbumDao){
            this.createdAlbumDao = createdAlbumDao;
        }


        @Override
        protected Void doInBackground(CreatedAlbum... createdAlbums) {
            this.createdAlbumDao.delete(createdAlbums[0]);
            return null;
        }
    }
    public static class UpdateCreateAlbumAsyncTask extends  AsyncTask<CreatedAlbum,Void, Void>{
        private final CreatedAlbumDao createdAlbumDao;
        private UpdateCreateAlbumAsyncTask(CreatedAlbumDao createdAlbumDao){
            this.createdAlbumDao = createdAlbumDao;
        }


        @Override
        protected Void doInBackground(CreatedAlbum... createdAlbums) {
            this.createdAlbumDao.update(createdAlbums[0]);
            return null;
        }
    }
}
