package com.threess.summership.treasurehunt.model.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.threess.summership.treasurehunt.Constants;
import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.network.GetDataService;
import com.threess.summership.treasurehunt.model.network.RetrofitClient;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

import static com.threess.summership.treasurehunt.Constants.CLAIM_SUCCESS;
import static com.threess.summership.treasurehunt.Constants.TREASURE_EXISTS;

public class TreasureRepository {

    public static final String TAG = TreasureRepository.class.getSimpleName();

    private static TreasureRepository mTreasureRepository;
    private GetDataService getDataService;
    private MutableLiveData<GenericResponse> mResponseTreasure = new MutableLiveData<>();
    private final MutableLiveData<String> mTreasureRepositoryData = new MutableLiveData<>();

    public static TreasureRepository getInstance() {
        if (mTreasureRepository == null) {
            mTreasureRepository = new TreasureRepository();
        }
        return mTreasureRepository;
    }

    private TreasureRepository() {
        getDataService = RetrofitClient.createService(GetDataService.class);
    }

    public MutableLiveData<List<Treasure>> getData() {

        final MutableLiveData<List<Treasure>> treasureData = new MutableLiveData<>();

        getDataService.getAllTreasures().enqueue(new Callback<List<Treasure>>() {

            @Override
            public void onResponse(Call<List<Treasure>> call, Response<List<Treasure>> response) {

                if (response.isSuccessful()) {
                    treasureData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Treasure>> call, Throwable t) {

                treasureData.setValue(null);

            }
        });

        return treasureData;
    }

    public void createTreasure(@Body Treasure treasure) {
        getDataService.createTreasure(treasure).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {

                if (response.isSuccessful()) {
                    Log.d(TAG, "Treasure created successfully");

                    mResponseTreasure.setValue(new GenericResponse(Constants.CREATE_SUCCESS, true));
                } else {
                    Log.d(TAG, "Treasure couldn't be created");
                    mResponseTreasure.setValue(new GenericResponse(TREASURE_EXISTS, true));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                mResponseTreasure.setValue(new GenericResponse(false));
                Log.e(TAG, "Create treasure failure");
            }
        });
    }

    public MutableLiveData<GenericResponse> getResponseTreasure() {
        return mResponseTreasure;
    }

    public void claimTreasure(final String username, final String passcode, final long score) {

        getDataService.claimTreasure(username, passcode).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                String backEndError = CLAIM_SUCCESS;//messages from back-end
                if (response.errorBody() != null) {
                    try {
                        backEndError = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Back-end error message: " + backEndError);
                }

                if (response.isSuccessful()) {
                    getDataService.updateScore(username, score).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            Log.d(TAG, "Update score was successful.");
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.d(TAG, "Update score was failure.");
                        }
                    });
                }
                String messageBuilder = response.message() +
                        ": " +
                        backEndError.substring(backEndError.indexOf(':') + 2, backEndError.indexOf('!'));
                mTreasureRepositoryData.setValue(messageBuilder);
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                mTreasureRepositoryData.setValue("Claim treasure was failure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public MutableLiveData<String> getTreasureRepositoryData() {
        return mTreasureRepositoryData;
    }

}
