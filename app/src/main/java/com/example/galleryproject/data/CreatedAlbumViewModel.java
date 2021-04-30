package com.example.galleryproject.data;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CreatedAlbumViewModel  extends AndroidViewModel {
    private CreatedAlbumRepository respository;
    private LiveData<List<CreatedAlbum>> createdAlbumList;

    public CreatedAlbumViewModel(@NotNull Application application){
        super(application);
        respository = new CreatedAlbumRepository(application);
        createdAlbumList = respository.getAllCreatedAlbum();
    }
    public void insert(CreatedAlbum createdAlbum){
        respository.insert(createdAlbum);
    }

    public void update(CreatedAlbum createdAlbum){
        respository.update(createdAlbum);
    }

    public void delete(CreatedAlbum createdAlbum){
        respository.delete(createdAlbum);
    }

    public LiveData<List<CreatedAlbum>> getAllCreatedAlbum(){
        return this.createdAlbumList;
    }
}
