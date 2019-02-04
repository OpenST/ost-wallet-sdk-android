package com.ost.ostsdk.network;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface KitApi {
    @GET("")
    Call<ResponseBody> initAction(JSONObject payload, String signature);

    @POST("")
    Call<ResponseBody> postKey(byte[] encryptedKey, String signature);

    @GET("bins/1bsqcn/")
    Call<ResponseBody> getNoticeData();
}