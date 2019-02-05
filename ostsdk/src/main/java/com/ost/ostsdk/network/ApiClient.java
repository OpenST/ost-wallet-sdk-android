package com.ost.ostsdk.network;

import android.text.TextUtils;
import android.util.Log;

import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.security.OstApiSigner;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://s4-api.stagingost.com/testnet/v2/";
    private static final long WAIT_TIME = 10;
    private static final String SUCCESS = "success";

    private static final String API_SIGNER_ADDRESS = "personal_sign_address";
    private static final String REQUEST_TIMESTAMP = "request_timestamp";
    private static final String SIGNATURE_KIND = "signature_kind";
    private static final String TOKEN_ID = "token_id";
    private static final String WALLET_ADDRESS = "wallet_address";
    private static final String USER_ID = "user_id";

    private static Retrofit retrofit = null;


    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();

            Headers.Builder headerBuilder = new Headers.Builder();
            headerBuilder.add("Content-Type", "application/x-www-form-urlencoded");
            headerBuilder.add("User-Agent", "ost-sdk-js 1.1.0");

            builder.headers(headerBuilder.build());
            HttpUrl url = originalRequest.url();

            OstApiSigner ostApiSigner = new OstApiSigner(Numeric.hexStringToByteArray("0x6edc3804eb9f70b26731447b4e43955c5532f2195a6fe77cbed287dbd3c762ce"));

            HttpUrl.Builder urlBuilder = url.newBuilder();
            urlBuilder.addQueryParameter(API_SIGNER_ADDRESS, ostApiSigner.getAddress());
            urlBuilder.addQueryParameter(REQUEST_TIMESTAMP, String.valueOf((int)(System.currentTimeMillis()/1000)));
            urlBuilder.addQueryParameter(SIGNATURE_KIND, "OST1-PS");
            urlBuilder.addQueryParameter(TOKEN_ID, OstSdk.getCurrentTokenId());
            urlBuilder.addQueryParameter(USER_ID,OstSdk.getCurrentUserId());
            urlBuilder.addQueryParameter(WALLET_ADDRESS, "0x60A20Cdf6a21a73Fb89475221D252865C695e302");
            if ("GET".equals(originalRequest.method())) {
                String urlQuery = TextUtils.isEmpty(urlBuilder.build().query()) ? "" : urlBuilder.build().query();
                List<String> queriesList = Arrays.asList(urlQuery.split("&"));
                Collections.sort(queriesList);
                String queryParams = joinString("&", queriesList);
                List<String> pathSegments = url.pathSegments();
                String resource = buildResourceString(pathSegments);
                String signatureParam = resource + "?" + queryParams;
                String signatureData = ostApiSigner.sign(signatureParam.getBytes());
                urlBuilder.addQueryParameter("signature", signatureData);
                builder.url(urlBuilder.build());
            } else {

            }

            Request newRequest = builder.build();
            Log.i("ApiClient url", newRequest.url().toString());
            return chain.proceed(newRequest);
        }
    }).build();

    private static String buildResourceString(List<String> pathSegments) {
        HttpUrl httpUrl = HttpUrl.parse(BASE_URL);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/");
        int start = httpUrl.pathSegments().size() - 1;
        for (; start < pathSegments.size() - 1; start++) {
            stringBuilder.append(pathSegments.get(start));
            stringBuilder.append("/");
        }
        return stringBuilder.toString();
    }

    private static String joinString(String delimiter, List<String> queriesList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String query : queriesList) {
            stringBuilder.append(query);
            stringBuilder.append(delimiter);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
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

                    jsonObjectArray[0].put(SUCCESS, false);
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