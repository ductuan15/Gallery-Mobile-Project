package com.example.galleryproject.data;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.LinkedHashMap;

@Entity(tableName = "createAlbum")
public class CreatedAlbum {
    @PrimaryKey
    private int id;

    LinkedHashMap<Uri, Media> mediaLinkedHashMap;

    String name;

    boolean isLocked;

    public LinkedHashMap<Uri, Media> getMediaLinkedHashMap() {
        return mediaLinkedHashMap;
    }

    public void setMediaLinkedHashMap(LinkedHashMap<Uri, Media> mediaLinkedHashMap) {
        this.mediaLinkedHashMap = mediaLinkedHashMap;
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
