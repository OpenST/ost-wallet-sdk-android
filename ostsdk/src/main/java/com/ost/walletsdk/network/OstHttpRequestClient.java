/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.datatheorem.android.trustkit.TrustKit;
import com.datatheorem.android.trustkit.config.DomainPinningPolicy;
import com.datatheorem.android.trustkit.config.PublicKeyPin;
import com.datatheorem.android.trustkit.config.TrustKitConfiguration;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.ost.walletsdk.OstConfigs;
import com.ost.walletsdk.OstConstants;
import com.ost.walletsdk.OstSdk;
import com.ost.walletsdk.R;
import com.ost.walletsdk.ecKeyInteracts.OstApiSigner;
import com.ost.walletsdk.workflows.errors.OstError;
import com.ost.walletsdk.workflows.errors.OstErrors;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Buffer;

/**
 * HttpRequestClient to supports get and post requests with api signing.
 */
public class OstHttpRequestClient {
    private static final String TAG = "OstHttp";
    private static final String API_SIGNATURE = "api_signature";
    private String apiEndpoint;
    private long timeout;
    private OkHttpClient client;
    private static final Escaper FormParameterEscaper = UrlEscapers.urlFormParameterEscaper();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Boolean enableLog;
    private OstApiSigner mOstApiSigner;
    private ResponseParser mResponseParser;

    public void setOstApiSigner(OstApiSigner ostApiSigner) {
        mOstApiSigner = ostApiSigner;
    }

    public void setResponseParser(ResponseParser mResponseParser) {
        this.mResponseParser = mResponseParser;
    }

    static class HttpParam {
        private String paramName;
        private String paramValue;

        public HttpParam() {

        }

        public HttpParam(String paramName, String paramValue) {
            this.paramName = paramName;
            this.paramValue = paramValue;
        }

        public String getParamValue() {
            return paramValue;
        }

        public void setParamValue(String paramValue) {
            this.paramValue = paramValue;
        }

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

    }

    public OstHttpRequestClient(String baseUrl, boolean enableLog) {
        this.enableLog = enableLog;
        this.apiEndpoint = baseUrl;
        this.timeout = 30;

        //To-Do: Discuss Dispatcher config with Team.
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(500);
        dispatcher.setMaxRequestsPerHost(150);

        String hostName = "ost.com";
        try {
            hostName = new URL(baseUrl).getHost();
        } catch (MalformedURLException e) {
            errorLog(TAG, "URL parsing error");
        }

        ensureTrustKitInitialization(hostName);

        client = new OkHttpClient.Builder()
                .sslSocketFactory(TrustKit.getInstance().getSSLSocketFactory(hostName),
                        TrustKit.getInstance().getTrustManager(hostName))
                .connectionPool(new ConnectionPool(10, 2, TimeUnit.MINUTES))
                .connectTimeout(OstConfigs.getInstance().getREQUEST_TIMEOUT_DURATION(), TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .retryOnConnectionFailure(false)
                .build();

    }

    private void ensureTrustKitInitialization(String hostName) {
        TrustKitConfiguration configuration = TrustKit.getInstance().getConfiguration();
        DomainPinningPolicy domainPinningPolicy = configuration.getPolicyForHostname(hostName);
        if (null == domainPinningPolicy) {
            throw new OstError("ost_hrc_etki_1", OstErrors.ErrorCode.INVALID_CERTIFICATE);
        }
        TrustKitConfiguration trustKitConfiguration;
        try {
            trustKitConfiguration = TrustKitConfiguration.fromXmlPolicy(
                    OstSdk.getContext(), OstSdk.getContext().getResources().getXml(R.xml.network_security_config)
            );
        } catch (XmlPullParserException | IOException e) {
            throw new OstError("Could not parse network security policy file", OstErrors.ErrorCode.INVALID_CERTIFICATE);
        } catch (CertificateException e) {
            throw new OstError("Could not find the debug certificate in the " +
                    "network security police file", OstErrors.ErrorCode.INVALID_CERTIFICATE);
        }

        DomainPinningPolicy sdkDomainPinningPolicy =  trustKitConfiguration.getPolicyForHostname(hostName);
        if (null == sdkDomainPinningPolicy) throw new OstError("ost_hrc_etki_2", OstErrors.ErrorCode.INVALID_CERTIFICATE);

        Set<PublicKeyPin> sdkPublicKeyPins = sdkDomainPinningPolicy.getPublicKeyPins();
        Set<PublicKeyPin> publicKeyPins = domainPinningPolicy.getPublicKeyPins();

        for (PublicKeyPin sdkPublicKeyPin : sdkPublicKeyPins) {
            if (!publicKeyPins.contains(sdkPublicKeyPin)) {
                throw new OstError("ost_hrc_etki_3", OstErrors.ErrorCode.INVALID_CERTIFICATE);
            }
        }
    }

    private static String GET_REQUEST = "GET";
    private static String POST_REQUEST = "POST";
    private static String SocketTimeoutExceptionString = "{'success':'false','err':{'code':'REQUEST_TIMEOUT','internal_id':'SDK(TIMEOUT_ERROR)','msg':'','error_data':[]}}";
    private static String IOExceptionString = "{'success':'false','err':{'code':'IOException','internal_id':'SDK(IO_EXCEPTION)','msg':'','error_data':[]}}";
    private static String NetworkExceptionString = "{'success':'false','err':{'code':'NO_NETWORK','internal_id':'SDK(NO_NETWORK)','msg':'','error_data':[]}}";
    private static final String CertificateErrorString = "{'success':'false','err':{'code':'INVALID_CERTIFICATE','internal_id':'SDK(INVALID_CERTIFICATE)','msg':'','error_data':[]}}";;

    private static JSONObject CertificateErrorJsonResponse;

    static {
        try {
            CertificateErrorJsonResponse = new JSONObject(CertificateErrorString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject get(String resource, Map<String, Object> queryParams) throws OstError {
        return send(GET_REQUEST, resource, queryParams);
    }

    public JSONObject post(String resource, Map<String, Object> queryParams) throws OstError {
        return send(POST_REQUEST, resource, queryParams);
    }

    private JSONObject send(String requestType, String resource, Map<String, Object> mapParams) throws OstError {
//        //TODO: Make POST_REQUEST & GET_REQUEST ENUMS
//        if (!requestType.equalsIgnoreCase(POST_REQUEST) && !requestType.equalsIgnoreCase(GET_REQUEST)) {
//            throw new IOException("Invalid requestType");
//        }
        if (null == mapParams) {
            mapParams = new HashMap<String, Object>();
        }

        // Start Building the request, url of request and request form body.
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl baseUrl = HttpUrl.parse(apiEndpoint + resource);
        HttpUrl.Builder urlBuilder = baseUrl.newBuilder(resource);

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (null == urlBuilder) {
            JSONObject errorInfo = new JSONObject();

            try {
                errorInfo.putOpt("apiEndpoint", apiEndpoint);
            } catch (JSONException e) {
                // Ignore
            }
            try {
                errorInfo.putOpt("resource", resource);
            } catch (JSONException e) {
                // Ignore
            }

            throw new OstError("ost_hrc_s_1", OstErrors.ErrorCode.SDK_ERROR, errorInfo);
        }

        // Evaluate the url generated so far.
        HttpUrl url = urlBuilder.build();

        // Start Building Query-String Input Buffer by parsing the url.
        Buffer qsInputBuffer = new Buffer();

        qsInputBuffer.writeUtf8(resource);
        qsInputBuffer.writeByte('?');

        //Reset urlBuilder.
        urlBuilder = baseUrl.newBuilder();

        ArrayList<HttpParam> params = new ArrayList<HttpParam>();
        String paramKey;
        String paramVal;

        params = buildNestedQuery(params, "", mapParams);

        // Add params to url/form-body & qsInputBuffer.
        Iterator it = params.iterator();
        boolean firstParam = true;
        while (it.hasNext()) {
            HttpParam pair = (HttpParam) it.next();

            paramKey = pair.getParamName();
            paramVal = pair.getParamValue();

            paramKey = specialCharacterEscape(paramKey);
            paramVal = specialCharacterEscape(paramVal);

            if (!firstParam) {
                qsInputBuffer.writeByte('&');
            }
            firstParam = false;

            qsInputBuffer.writeUtf8(paramKey);
            qsInputBuffer.writeByte('=');
            qsInputBuffer.writeUtf8(paramVal);
            debugLog(TAG, "paramKey " + paramKey + " paramVal " + paramVal);

            if (GET_REQUEST.equalsIgnoreCase(requestType)) {
                urlBuilder.addEncodedQueryParameter(paramKey, paramVal);
            } else {
                formBodyBuilder.addEncoded(paramKey, paramVal);
            }
        }

        if (null != mOstApiSigner) {
            // Add signature to Params.
            paramKey = API_SIGNATURE;
            paramVal = signQueryParams(qsInputBuffer);
            if (GET_REQUEST.equalsIgnoreCase(requestType)) {
                urlBuilder.addEncodedQueryParameter(paramKey, paramVal);
            } else {
                formBodyBuilder.addEncoded(paramKey, paramVal);
            }
        }
        // Build the url.
        url = urlBuilder.build();
        infoLog(TAG, "url = " + url.toString());

        // Set url in requestBuilder.
        requestBuilder.url(url);

        // Build the request Object.
        Request request;
        if (GET_REQUEST.equalsIgnoreCase(requestType)) {
            requestBuilder.get().addHeader("Content-Type", OstConstants.CONTENT_TYPE);
            requestBuilder.get().addHeader("User-Agent", OstConstants.USER_AGENT);
        } else {
            FormBody formBody = formBodyBuilder.build();
            if ( this.enableLog ) {
                for (int i = 0; i < formBody.size(); i++) {
                    debugLog(TAG, formBody.name(i) + "\t\t" + formBody.value(i));
                }
            }

            requestBuilder.post(formBody);
        }
        request = requestBuilder.build();

        // Make the call and execute.
        String responseBody;
        Call call = client.newCall(request);
        try {
            if (isNetworkAvailable()) {
                okhttp3.Response response = call.execute();
                responseBody = this.getResponseBodyAsString(response);
            } else {
                responseBody = NetworkExceptionString;
            }
        }
        catch (SocketTimeoutException e) {
            errorLog(TAG, "SocketTimeoutException occurred.");
            responseBody =  SocketTimeoutExceptionString;
        } catch (SSLHandshakeException e) {
            throw new OstApiError("ost_hrc_s_3", OstErrors.ErrorCode.INVALID_CERTIFICATE, CertificateErrorJsonResponse);
        } catch (IOException e) {
            JSONObject errorInfo = new JSONObject();

            try {
                errorInfo.putOpt("apiEndpoint", apiEndpoint);
            } catch (JSONException e1) {
                // Ignore
            }
            try {
                errorInfo.putOpt("resource", resource);
            } catch (JSONException e2) {
                // Ignore
            }

            try {
                errorInfo.putOpt("details", "The request could not be executed due to cancellation, a connectivity problem or timeout. Because networks can fail during an exchange, it is possible that the remote server accepted the request before the failure.");
            } catch (JSONException e3) {
                // Ignore
            }

            throw new OstError("ost_hrc_s_2", OstErrors.ErrorCode.NETWORK_ERROR, errorInfo);
        }

        JSONObject jsonResponse = buildApiResponse(responseBody);

        if (null != mResponseParser) mResponseParser.parse(jsonResponse);

        return jsonResponse;
    }


    private String signQueryParams(Buffer qsInputBuffer) {
        // Generate Signature for Params.
        byte[] bytes = qsInputBuffer.readByteArray();
        debugLog(TAG,"bytes to sign: " + new String(bytes, UTF_8));
        // Encryption of bytes

        String signature = mOstApiSigner.sign(bytes);
        debugLog(TAG,"signature:" + signature);
        return signature;
    }

    private static String SOMETHING_WRONG_RESPONSE = "{'success': false, 'err': {'code': 'SOMETHING_WENT_WRONG', 'internal_id': 'SDK(SOMETHING_WENT_WRONG)', 'msg': '', 'error_data':[]}}";

    private String getResponseBodyAsString(okhttp3.Response response) {
        // Process the response.
        String responseBody;
        if (response.body() != null) {
            try {
                responseBody = response.body().string();
                if (responseBody.length() > 0) {
                    debugLog(TAG,"responseCode:" + response.code()+ "\nresponseBody:\n" + responseBody + "\n");
                    return responseBody;
                }
            } catch (IOException e) {
                // Silently handle the error.
                e.printStackTrace();
            }
        }

        // Response does not have a body. Lets create one.
        switch (response.code()) {
            case 502:
                responseBody = "{'success': false, 'err': {'code': 'BAD_GATEWAY', 'internal_id': 'SDK(BAD_GATEWAY)', 'msg': '', 'error_data':[]}}";
                break;
            case 503:
                responseBody = "{'success': false, 'err': {'code': 'SERVICE_UNAVAILABLE', 'internal_id': 'SDK(SERVICE_UNAVAILABLE)', 'msg': '', 'error_data':[]}}";
                break;
            case 504:
                responseBody = "{'success': false, 'err': {'code': 'GATEWAY_TIMEOUT', 'internal_id': 'SDK(GATEWAY_TIMEOUT)', 'msg': '', 'error_data':[]}}";
                break;
            default:
                responseBody = SOMETHING_WRONG_RESPONSE;
        }

        debugLog(TAG, "local responseBody:\n" + responseBody + "\n");
        return responseBody;
    }

    private void errorLog(String tag, String msg) {
        if( this.enableLog ) {
            Log.e(tag, msg);
        }
    }

    private void debugLog(String tag, String msg) {
        if( this.enableLog ) {
            Log.d(tag, msg);
        }
    }

    private void infoLog(String tag, String msg) {
        if( this.enableLog ) {
            Log.i(tag, msg);
        }
    }

    private static JSONObject buildApiResponse(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            return new JSONObject(SOMETHING_WRONG_RESPONSE);
        } catch (JSONException e) {
            //I promise to never be here.
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<HttpParam> buildNestedQuery(ArrayList<HttpParam> params, String paramKeyPrefix, Object paramValObj) {

        if (paramValObj instanceof Map) {

            //            sort map.
            Map<String, Object> sortedMap = new TreeMap<String, Object>((Map<? extends String, ?>) paramValObj);
            for (Object paramPair : sortedMap.entrySet()) {
                Map.Entry pair = (Map.Entry) paramPair;
                String key = (String) pair.getKey();
                Object value = pair.getValue();
                String prefix = "";
                if (paramKeyPrefix.isEmpty()) {
                    prefix = key;
                } else {
                    prefix = paramKeyPrefix + "[" + key + "]";
                }

                params = buildNestedQuery(params, prefix, value);
            }

        } else if (paramValObj instanceof Collection) {
            Iterator<Object> iterator = ((Collection) paramValObj).iterator();

            while (iterator.hasNext()) {
                Object value = iterator.next();
                String prefix = paramKeyPrefix + "[]";
                params = buildNestedQuery(params, prefix, value);
            }
        } else {
            if (paramValObj != null) {
                params.add(new HttpParam(paramKeyPrefix, paramValObj.toString()));
            } else {
                params.add(new HttpParam(paramKeyPrefix, ""));
            }

        }
        return params;
    }

    private static String specialCharacterEscape(String stringToEscape) {
        stringToEscape = FormParameterEscaper.escape(stringToEscape);
        stringToEscape = stringToEscape.replace("*", "%26");
        return stringToEscape;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) OstSdk.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public interface ResponseParser {
        void parse(JSONObject jsonObject);
    }

    public interface ApiSigner {
        String sign(byte[] bytes);
    }
}