/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.network;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class MappyNetworkClient {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private final String mUrl;
    private final RequestQueue mRequestQueue;

    public MappyNetworkClient(String url, RequestQueue requestQueue) {
        this.mUrl = url;
        this.mRequestQueue = requestQueue;
    }

    public void createAccount(String userName, String password, ResponseCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put(USERNAME, userName);
            params.put(PASSWORD, password);
            sendRequest(Request.Method.POST, "signup", params, callback);
        } catch (Exception ex) {
            callback.onFailure(ex);
        }
    }

    public void logIn(String userName, String password, ResponseCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put(USERNAME, userName);
            params.put(PASSWORD, password);
            sendRequest(Request.Method.POST, "login", params, callback);
        } catch (Exception ex) {
            callback.onFailure(ex);
        }
    }

    private void sendRequest(int method, final String resource, JSONObject params , final ResponseCallback callback) {
        String logInUrl = String.format("%s%s", mUrl, resource);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                method,
                logInUrl,
                params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFailure(error.getCause());
                    }
                }
        );
        mRequestQueue.add(jsonObjectRequest);
    }

    public interface ResponseCallback {
        void onSuccess(JSONObject jsonObject);

        void onFailure(Throwable throwable);
    }
}
