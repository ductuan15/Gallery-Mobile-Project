package com.example.galleryproject.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.galleryproject.data.Converter;
import com.example.galleryproject.entity.CreatedAlbum;
import com.example.galleryproject.dao.CreatedAlbumDao;

@Database(entities = {CreatedAlbum.class},version = 1)
@TypeConverters({Converter.class})
public abstract class CreatedAlbumDatabase extends RoomDatabase {

    private static CreatedAlbumDatabase instance;

    public abstract CreatedAlbumDao     createdAlbumDao();

    public static synchronized  CreatedAlbumDatabase getInstance(Context context){
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CreatedAlbumDatabase.class, "album_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
//
//    private static RoomDatabase.Callback roomCallback = new Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//        }
//    };
//
//    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void, Void>{
//        private CreatedAlbumDao createdAlbumDao;
//
//
//    }
}
