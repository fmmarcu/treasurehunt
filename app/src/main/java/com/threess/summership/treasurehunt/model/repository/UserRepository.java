package com.threess.summership.treasurehunt.model.repository;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.threess.summership.treasurehunt.model.User;
import com.threess.summership.treasurehunt.model.network.GetDataService;
import com.threess.summership.treasurehunt.model.network.GenericCallback;

import androidx.lifecycle.MutableLiveData;

import com.threess.summership.treasurehunt.model.network.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

import static com.threess.summership.treasurehunt.Constants.BASE_URL;
import static com.threess.summership.treasurehunt.Constants.LOGIN_SUCCESS;
import static com.threess.summership.treasurehunt.Constants.WRONG_USER;
import static com.threess.summership.treasurehunt.Constants.NONE;

public class UserRepository {

    private static final String TAG = UserRepository.class.getSimpleName();

    private GetDataService mGetDataService;
    private MutableLiveData<GenericResponse> mResponse = new MutableLiveData<>();

    private UserRepository(){
        mGetDataService = RetrofitClient.createService(GetDataService.class);
    }

    public static UserRepository getInstance() {
        return new UserRepository();
    }

    public MutableLiveData<User> getUsersProfile(final String userName) {
        final MutableLiveData<User> userData = new MutableLiveData<>();
        mGetDataService.getUserProfile(userName).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User user = response.body();
                if (user != null) {
                    if (TextUtils.isEmpty(user.getUserName())) {
                        user.setUserName(NONE);
                    }
                    if (!TextUtils.isEmpty(user.getProfilePicture())) {
                        user.setProfilePicture(BASE_URL + user.getProfilePicture());
                    }
                    userData.setValue(user);
                } else {
                    user = new User(NONE, NONE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.d(TAG, t.toString());
            }
        });

        return userData;
    }

    public void updatePhoto(String username, String filePath) {

        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type

        RequestBody fileReqBody = RequestBody.create(file, MediaType.parse("image/*"));
        // Create MultipartBody.Part using file request-body,file name and part name

        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create("image-type", MediaType.parse("text/plain"));
        mGetDataService.updatePhoto(username, part, description).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void registerUser(final User user, final GenericCallback<GenericResponse> genericCallback) {

        mGetDataService.registerUser(user.getUserName(), user.getPassword()).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call,
                                   @NonNull Response<GenericResponse> response) {
                StringBuilder messageBuilder = new StringBuilder();
                String backEndError = "Registration successful!";//messages from back-end
                if (response.errorBody() != null) {
                    try {
                        backEndError = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Back-end error message: " + backEndError);
                }

                messageBuilder.append(response.message());
                messageBuilder.append(": ");
                messageBuilder.append(backEndError.substring(backEndError.indexOf(':') + 2, backEndError.length() - 2));
                GenericResponse genericResponse = new GenericResponse(messageBuilder.toString(), response.isSuccessful());
                genericCallback.onResponseReady(genericResponse);
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                GenericResponse genericResponse = new GenericResponse("Registration was failure: " + t.getMessage(), false);
                genericCallback.onResponseReady(genericResponse);

                t.printStackTrace();
            }
        });
    }

    public void getAllUsers(final GenericCallback<List<User>> genericCallback) {

        mGetDataService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call,
                                   @NonNull Response<List<User>> response) {
                ArrayList<User> allUsers = new ArrayList<>(Objects.requireNonNull(response.body()));
                Collections.sort(allUsers, new Comparator<User>() {
                    @Override
                    public int compare(User user1, User user2) {
                        //first sort after score
                        int compare = Long.compare(user2.getScore(), user1.getScore());
                        //after scores are equal, sort after name
                        return (compare == 0)? user1.getUserName().compareTo(user2.getUserName()) : compare;
                    }
                });
                genericCallback.onResponseReady(allUsers);
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                t.printStackTrace();
                genericCallback.onResponseReady(null);
            }
        });
    }

    public void loginUser(@Body User user) {

        mGetDataService.LoginUser(user).enqueue(new Callback<GenericResponse>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse> call,
                                   @NonNull Response<GenericResponse> response) {

                if (response.isSuccessful()) {
                    Log.d(TAG, "Login successful repo");
                    mResponse.setValue(new GenericResponse(LOGIN_SUCCESS, true));


                } else {
                    Log.d(TAG, "Wrong user credentials repo" + " " + response.code());
                    mResponse.setValue(new GenericResponse(WRONG_USER, true));
                }

            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse> call, @NonNull Throwable t) {
                mResponse.setValue(new GenericResponse(false));
            }
        });
    }

    public MutableLiveData<GenericResponse> getResponse() {
        return mResponse;
    }
}
