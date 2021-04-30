package com.example.galleryproject.data;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {CreatedAlbum.class},version = 1)
@TypeConverters({Converter.class})
public abstract class CreatedAlbumDatabase extends RoomDatabase {

    private static CreatedAlbumDatabase instance;

    public abstract CreatedAlbumDao createdAlbumDao();

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
