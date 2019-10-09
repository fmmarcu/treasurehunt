package com.threess.summership.treasurehunt.model.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.threess.summership.treasurehunt.model.Treasure;

@Database(entities = Treasure.class, version = 1, exportSchema = false)
public abstract class TreasureDatabase extends RoomDatabase {
    
    public static final String TAG = TreasureDatabase.class.getSimpleName();

    private static TreasureDatabase instance;

    public abstract TreasureDao treasureDao();

    public static synchronized TreasureDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    TreasureDatabase.class, "treasure_database")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
            Log.d(TAG, "getInstance: s a creat baza de date");
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
            Log.d(TAG, "onCreate: populate database");
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {

        private TreasureDao treasureDao;

        private PopulateDbAsyncTask(TreasureDatabase db) {
            treasureDao = db.treasureDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: insert something");
            return null;
        }
    }
}
