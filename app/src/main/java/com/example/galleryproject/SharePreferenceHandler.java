package com.example.galleryproject;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public abstract class SharePreferenceHandler {
    public static final String SECURE_SHARED_PREFERENCES = "secure_file_shared_preferences";
    public static final String FAVORITE_SHARED_PREFERENCES= "favorite_file_shared_preferences";

    public static SharedPreferences getSecureSharePreferences(Context context){
        return context.getSharedPreferences(SECURE_SHARED_PREFERENCES,Context.MODE_PRIVATE);
    }
    public static SharedPreferences getFavoriteSharePreferences(Context context){
        return context.getSharedPreferences(FAVORITE_SHARED_PREFERENCES,Context.MODE_PRIVATE);
    }
    public static void getAllDataFromSharedPreference(SharedPreferences sharedPreferences, HashSet<String> stringHashSet ){
        if(stringHashSet == null) stringHashSet = new HashSet<>();
        stringHashSet.clear();
        Map<String,?> favoriteMediaMap = sharedPreferences.getAll();
        for(Map.Entry<String,?> entry : favoriteMediaMap.entrySet()) {
            stringHashSet.add(entry.getKey());
        }
    }
    public static void getAllDataFromSharedPreference(SharedPreferences sharedPreferences, ArrayList<String> stringArrayList){
        if(stringArrayList == null) stringArrayList = new ArrayList<>();
        stringArrayList.clear();
        Map<String,?> favoriteMediaMap = sharedPreferences.getAll();
        for(Map.Entry<String,?> entry : favoriteMediaMap.entrySet()) {
            stringArrayList.add(entry.getKey());
        }
    }
}
