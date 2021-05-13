package com.example.galleryproject.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.galleryproject.entity.CreatedAlbum;
import com.example.galleryproject.entity.FavoriteMedia;
import com.example.galleryproject.repository.CreatedAlbumRepository;
import com.example.galleryproject.repository.FavoriteMediaRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FavoriteMediaViewModel extends AndroidViewModel {
    private FavoriteMediaRepository favoriteMediaRepository;
    private LiveData<List<FavoriteMedia>> favoriteList;

    public FavoriteMediaViewModel(@NotNull Application application){
        super(application);
        favoriteMediaRepository = new FavoriteMediaRepository(application);
        favoriteList = favoriteMediaRepository.getFavoriteList();
    }

    public void insert(FavoriteMedia favoriteMedia){
        favoriteMediaRepository.insert(favoriteMedia);
    }
    public void delete(FavoriteMedia favoriteMedia){
        favoriteMediaRepository.delete(favoriteMedia);
    }
    public LiveData<List<FavoriteMedia>> getFavoriteList(){
        return this.favoriteList;
    }
    public LiveData<List<String>> getFavoriteUriList(){
        return this.favoriteMediaRepository.getFavoriteMediaUriList();
    }

}
