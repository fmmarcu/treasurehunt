package com.threess.summership.treasurehunt.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.database.TreasureLocalRepository;

import java.util.ArrayList;
import java.util.List;

public class TreasureLocalViewModel extends AndroidViewModel {
    public static final String TAG = TreasureLocalViewModel.class.getSimpleName();
    private TreasureLocalRepository repository;
    private MutableLiveData<List<Treasure>> allTreasures;

    public TreasureLocalViewModel(@NonNull Application application) {
        super(application);
        repository = new TreasureLocalRepository(application);
        allTreasures = repository.getAllTreasures();

    }
    public void init(){
        allTreasures.setValue(repository.getAllTreasures().getValue());
    }

    public void insert (Treasure treasure){
        repository.insert(treasure);
    }

    public void update (Treasure treasure){
        repository.update(treasure);
    }

    public void delete (Treasure treasure){
        repository.delete(treasure);
    }

    public void deleteAllTreasure(){
        repository.deleteAllTreasures();
    }

    private List<Treasure> getFavorites(){

        List<Treasure> treasures = new ArrayList<>();
        for(Treasure treasure : allTreasures.getValue()){
            if(treasure.isFavorite()) {
                treasures.add(treasure);
            }
        }
        return treasures;
    }

    public void setFavorites(){
        allTreasures.setValue(getFavorites());
    }

    public void setAllTreasures(){
        allTreasures.setValue(repository.getAllTreasures().getValue());
        Log.d(TAG, "setAllTreasures: " + allTreasures.getValue().size());
    }

    public MutableLiveData<List<Treasure>> getAllTreasures() {
        return allTreasures;
    }
}
