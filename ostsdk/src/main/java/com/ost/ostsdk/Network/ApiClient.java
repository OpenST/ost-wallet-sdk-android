package com.ost.ostsdk.Network;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://api.myjson.com/";
    private static final long WAIT_TIME = 10;
    private static final String SUCCESS = "success";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public static JSONObject syncApiCall(Call<ResponseBody> call) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final JSONObject[] jsonObjectArray = new JSONObject[1];

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Log.i("ApiClient", response.message());
                try {
                    jsonObjectArray[0] = new JSONObject(response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ApiClient", t.getMessage());
                try {
                jsonObjectArray[0] = new JSONObject();

                    jsonObjectArray[0].put(SUCCESS,false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return jsonObjectArray[0];
    }
}