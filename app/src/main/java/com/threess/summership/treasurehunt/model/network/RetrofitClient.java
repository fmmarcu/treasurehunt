package com.threess.summership.treasurehunt.model.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "http://5.254.125.248:3000/";

    private static HttpLoggingInterceptor sInterceptor = new HttpLoggingInterceptor();
    private static OkHttpClient sHttpClient = new OkHttpClient.Builder().addNetworkInterceptor(new StethoInterceptor())
            .addInterceptor(sInterceptor).build();

    private static Retrofit sRetrofit = new Retrofit.Builder()
            .client(sHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    public static <S> S createService(Class<S> serviceClass) {
        return sRetrofit.create(serviceClass);
    }
}
