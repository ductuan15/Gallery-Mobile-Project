package com.example.galleryproject.data;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

@Entity(tableName = "create_album")
public class    CreatedAlbum {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @TypeConverters(Converter.class)
    LinkedHashMap<Uri, Media> mediaLinkedHashMap;

    String name;

    boolean isLocked;


    public CreatedAlbum(LinkedHashMap<Uri, Media> mediaLinkedHashMap, String name, boolean isLocked) {
        this.mediaLinkedHashMap = mediaLinkedHashMap;
        this.name = name;
        this.isLocked = isLocked;
    }

    public LinkedHashMap<Uri, Media> getMediaLinkedHashMap() {
        return mediaLinkedHashMap;
    }

    public void setMediaLinkedHashMap(LinkedHashMap<Uri, Media> mediaLinkedHashMap) {
        this.mediaLinkedHashMap = mediaLinkedHashMap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }



}
