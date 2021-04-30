package com.example.galleryproject.data;

import android.net.Uri;

import androidx.room.TypeConverter;

import java.util.LinkedHashMap;
import java.util.Objects;


public class Converter {
    @TypeConverter
    public static String converterLinkedHashMapToString(LinkedHashMap<Uri, Media> mediaLinkedHashMap) {
        if(mediaLinkedHashMap == null){
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Uri key : mediaLinkedHashMap.keySet()) {
            stringBuilder.append(key.toString());
            stringBuilder.append("\n");
            stringBuilder.append(Objects.requireNonNull(mediaLinkedHashMap.get(key)).toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @TypeConverter
    public static LinkedHashMap<Uri, Media> converterStringToMediaLinkedHashMap(String data) {
        if(data.equals("")) return null;
        String datas[] = data.split("\n");
        LinkedHashMap<Uri, Media> mediaLinkedHashMap = new LinkedHashMap<>();
        for (int i = 0; i < datas.length; i += 2) {
            mediaLinkedHashMap.put(Uri.parse(datas[i]), Media.parseString(datas[i + 1]));
        }
        return mediaLinkedHashMap;
    }
}
