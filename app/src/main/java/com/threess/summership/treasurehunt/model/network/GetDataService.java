package com.threess.summership.treasurehunt.model.network;

import com.threess.summership.treasurehunt.model.Treasure;
import com.threess.summership.treasurehunt.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import com.threess.summership.treasurehunt.model.repository.GenericResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface GetDataService {

    @FormUrlEncoded
    @POST("/users/register")
        //for registration: post user
    Call<GenericResponse> registerUser(@Field("username") String userName, @Field("password") String password);

    @GET("/users")
    Call<List<User>> getAllUsers();

    @POST("/users/login")
    Call<GenericResponse> LoginUser(@Body User user);

    @GET("/users/{username}/")
    Call<User> getUserProfile(@Path("username") String username);

    @GET("/treasures")
    Call<List<Treasure>> getAllTreasures();

    @POST("/treasures/create")
    Call<GenericResponse> createTreasure(@Body Treasure treasure);

    @FormUrlEncoded
    @POST("/treasures/claim")
    Call<GenericResponse> claimTreasure(@Field("username") String username, @Field("passcode") String passcode);

    @Multipart
    @POST("/users/update/{username}/")
    Call<ResponseBody> updatePhoto(@Path("username") String username, @Part MultipartBody.Part file, @Part("name") RequestBody requestBody);

    @FormUrlEncoded
    @POST("/users/update/{username}/")
    Call<ResponseBody> updateScore(@Path("username") String username,@Field("score") long score);
}
