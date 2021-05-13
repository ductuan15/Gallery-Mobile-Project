package com.example.galleryproject.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "favorite_media")
public class FavoriteMedia {

    @PrimaryKey
    @NonNull
    String uri;

    public FavoriteMedia(@NotNull String uri) {
        this.uri = uri;
    }


    @NotNull
    public String getUri() {
        return uri;
    }


    public void setUri(@NotNull String uri) {
        this.uri = uri;
    }
}
