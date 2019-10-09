package com.threess.summership.treasurehunt.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.repository.GenericResponse;
import com.threess.summership.treasurehunt.model.repository.TreasureRepository;

import java.util.List;

import retrofit2.http.Body;

public class TreasureViewModel extends ViewModel {
    private TreasureRepository mTreasureRepository;
    private MutableLiveData<List<Treasure>> mMutableLiveData;
    private static Treasure mSelectedTreasure ;

    public TreasureViewModel(){
        super();
        mTreasureRepository = TreasureRepository.getInstance();
    }

    public void init() {
        if (mMutableLiveData != null) {
            return;
        }
        mTreasureRepository = TreasureRepository.getInstance();
        mMutableLiveData = mTreasureRepository.getData();
    }

    public void createTreasure(@Body Treasure treasure) {
        mTreasureRepository.createTreasure(treasure);
    }

    public MutableLiveData<GenericResponse> getResponseTreasure() {
        return mTreasureRepository.getResponseTreasure();
    }

    public MutableLiveData<List<Treasure>> getTreasureRepository() {
        return mMutableLiveData;
    }

    public void select(Treasure treasure) {
        mSelectedTreasure = treasure;
    }

    public Treasure getSelected() {
        return mSelectedTreasure;
    }

    public void claimTreasure(String username, String passcode, long score) {
        Log.d("TreasureViewModel", "claim");
        mTreasureRepository.claimTreasure(username, passcode, score);
    }

    public MutableLiveData<String> getClaimTreasureResponse() {
        return mTreasureRepository.getTreasureRepositoryData();
    }
}
