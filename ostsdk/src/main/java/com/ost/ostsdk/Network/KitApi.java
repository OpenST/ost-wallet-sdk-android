package com.ost.ostsdk.Network;

import org.json.JSONObject;

import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface KitApi {
    @GET("")
    Call<Response> initAction(JSONObject payload, String signature);

    @POST("")
    Call<Response> postKey(byte[] encryptedKey, String signature);
}