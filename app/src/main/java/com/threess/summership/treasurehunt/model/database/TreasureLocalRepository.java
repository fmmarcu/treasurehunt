package com.threess.summership.treasurehunt.model.database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.threess.summership.treasurehunt.model.Treasure;

import java.util.List;

public class TreasureLocalRepository {
    private TreasureDao mTreasureDao;
    private MutableLiveData<List<Treasure>> allTreasures = new MutableLiveData<>();


    public TreasureLocalRepository(Application application){
        TreasureDatabase database = TreasureDatabase.getInstance(application);
        mTreasureDao = database.treasureDao();
        allTreasures.setValue(mTreasureDao.getAllTreasures());
    }

    public void insert(Treasure treasure){
        new CRUDTreasureAsyncTask(mTreasureDao, "insert").execute(treasure);

    }
    public void update(Treasure treasure){
        new CRUDTreasureAsyncTask(mTreasureDao, "update").execute(treasure);

    }
    public void delete(Treasure treasure){
        new CRUDTreasureAsyncTask(mTreasureDao, "delete").execute(treasure);

    }
    public void deleteAllTreasures(){
        new CRUDTreasureAsyncTask(mTreasureDao,"delete_all").execute();

    }
    @NonNull
    public MutableLiveData<List<Treasure>> getAllTreasures(){
        allTreasures.setValue(mTreasureDao.getAllTreasures());
        return allTreasures;
    }

    private static class CRUDTreasureAsyncTask extends AsyncTask<Treasure, Void, Void>{

        private TreasureDao treasureDao;
        private String operation;

        public CRUDTreasureAsyncTask(TreasureDao treasureDao, String operation) {
            this.treasureDao = treasureDao;
            this.operation = operation;
        }

        @Override
        protected Void doInBackground(Treasure... treasureEntities) {
            switch(this.operation){
                case "insert":
                    treasureDao.insert(treasureEntities[0]);
                    break;
                case "update":
                    treasureDao.update(treasureEntities[0]);
                    break;
                case "delete":
                    treasureDao.delete(treasureEntities[0]);
                    break;
                case "delete_all":
                    treasureDao.deleteAllTreasures();
                    break;
            }
            return null;
        }
    }


}
