package com.ost.ostsdk.network;

import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.ost.ostsdk.OstSdk;
import com.ost.ostsdk.security.OstApiSigner;
import com.ost.ostsdk.security.OstKeyManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Buffer;

public class OstHttpRequestClient {
    private String apiEndpoint;
    private long timeout;
    private static final Gson gson = new Gson();
    private OkHttpClient client;
    private static final Escaper FormParameterEscaper = UrlEscapers.urlFormParameterEscaper();
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static Boolean DEBUG = ("true").equalsIgnoreCase( System.getenv("OST_KYC_SDK_DEBUG") );
    private static Boolean VERBOSE = false;

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

    public OstHttpRequestClient(String baseUrl) {
        this.apiEndpoint = baseUrl;
        this.timeout = 30;


        //To-Do: Discuss Dispatcher config with Team.
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(500);
        dispatcher.setMaxRequestsPerHost(150);

        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 2, TimeUnit.MINUTES))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .dispatcher(dispatcher)
                .retryOnConnectionFailure(false)
                .build();

    }

    private static String GET_REQUEST = "GET";
    private static String POST_REQUEST = "POST";
    private static String SocketTimeoutExceptionString = "{'success':'false','err':{'code':'GATEWAY_TIMEOUT','internal_id':'TIMEOUT_ERROR','msg':'','error_data':[]}}";


    public JSONObject get(String resource, Map<String, Object> queryParams) throws IOException {
        return send(GET_REQUEST, resource, queryParams);
    }

    public JSONObject post(String resource, Map<String, Object> queryParams) throws IOException {
        return send(POST_REQUEST, resource, queryParams);
    }

    private JSONObject send(String requestType, String resource, Map<String, Object> mapParams) throws IOException {
        // Basic Sanity.
        if (!requestType.equalsIgnoreCase(POST_REQUEST) && !requestType.equalsIgnoreCase(GET_REQUEST)) {
            throw new IOException("Invalid requestType");
        }
        if (null == mapParams) {
            mapParams = new HashMap<String, Object>();
        }

        // Start Building the request, url of request and request form body.
        Request.Builder requestBuilder = new Request.Builder();
        HttpUrl baseUrl = HttpUrl.parse(apiEndpoint + resource);
        HttpUrl.Builder urlBuilder = baseUrl.newBuilder(resource);

        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (null == urlBuilder) {
            throw new IOException("Failed to instanciate HttpUrl.Builder. resource or Api Endpoint is incorrect.");
        }

        // Evaluate the url generated so far.
        HttpUrl url = urlBuilder.build();

        // Start Building HMAC Input Buffer by parsing the url.
        Buffer hmacInputBuffer = new Buffer();

        hmacInputBuffer.writeUtf8(resource);
        hmacInputBuffer.writeByte('?');

        //Reset urlBuilder.
        urlBuilder = baseUrl.newBuilder();

        mapParams.put("request_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        ArrayList<HttpParam> params = new ArrayList<HttpParam>();
        String paramKey;
        String paramVal;

        params = buildNestedQuery(params, "", mapParams);

        // Add params to url/form-body & hmacInputBuffer.
        Iterator it = params.iterator();
        boolean firstParam = true;
        while (it.hasNext()) {
            HttpParam pair = (HttpParam) it.next();

            paramKey = pair.getParamName();
            paramVal = pair.getParamValue();

            paramKey = specialCharacterEscape(paramKey);
            paramVal = specialCharacterEscape(paramVal);

            if (!firstParam) {
                hmacInputBuffer.writeByte('&');
            }
            firstParam = false;

            hmacInputBuffer.writeUtf8(paramKey);
            hmacInputBuffer.writeByte('=');
            hmacInputBuffer.writeUtf8(paramVal);
            if (DEBUG) System.out.println("paramKey " + paramKey + " paramVal " + paramVal);

            if (GET_REQUEST.equalsIgnoreCase(requestType)) {
                urlBuilder.addEncodedQueryParameter(paramKey, paramVal);
            } else {
                formBodyBuilder.addEncoded(paramKey, paramVal);
            }
        }

        // Add signature to Params.
        paramKey = "signature";
        paramVal = signQueryParams(hmacInputBuffer);
        if (GET_REQUEST.equalsIgnoreCase(requestType)) {
            urlBuilder.addEncodedQueryParameter(paramKey, paramVal);
        } else {
            formBodyBuilder.addEncoded(paramKey, paramVal);
        }

        // Build the url.
        url = urlBuilder.build();
        if (DEBUG) System.out.println("url = " + url.toString());

        // Set url in requestBuilder.
        requestBuilder.url(url);

        // Build the request Object.
        Request request;
        if (GET_REQUEST.equalsIgnoreCase(requestType)) {
            requestBuilder.get().addHeader("Content-Type", "application/x-www-form-urlencoded");
            requestBuilder.get().addHeader("User-Agent", "ost-sdk-js 1.1.0");
        } else {
            FormBody formBody = formBodyBuilder.build();
            if (DEBUG && VERBOSE) {
                for (int i = 0; i < formBody.size(); i++) {
                    System.out.println(formBody.name(i) + "\t\t" + formBody.value(i));
                }
            }

            requestBuilder.post(formBody);
        }
        request = requestBuilder.build();

        // Make the call and execute.
        String responseBody;
        Call call = client.newCall(request);
        try {
            okhttp3.Response response = call.execute();
            responseBody = getResponseBodyAsString(response);
        }catch (SocketTimeoutException e)
        {
            System.out.println("SocketTimeoutException occured");
            responseBody =  SocketTimeoutExceptionString;
        }
        return buildApiResponse(responseBody);
    }


    private String signQueryParams(Buffer hmacInputBuffer) {
        // Generate Signature for Params.
        byte[] bytes = hmacInputBuffer.readByteArray();
        if (DEBUG) System.out.println("bytes to sign: " + new String(bytes, UTF_8));
        // Encryption of bytes

        OstApiSigner ostApiSigner = new OstKeyManager(OstSdk.getCurrentUserId()).getApiSigner();

        String signature = ostApiSigner.sign(bytes);
        if (DEBUG) System.out.println("signature: " + signature);
        return signature;
    }

    private static String SOMETHING_WRONG_RESPONSE = "{'success': false, 'err': {'code': 'SOMETHING_WENT_WRONG', 'internal_id': 'SDK(SOMETHING_WENT_WRONG)', 'msg': '', 'error_data':[]}}";

    private static String getResponseBodyAsString(okhttp3.Response response) {
        // Process the response.
        String responseBody;
        if (response.body() != null) {
            try {
                responseBody = response.body().string();
                if (responseBody.length() > 0) {
                    if (DEBUG) System.out.println("responseCode: "+response.code()+"\nresponseBody:\n" + responseBody + "\n");
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

        if (DEBUG) System.out.println("local responseBody:\n" + responseBody + "\n");
        return responseBody;
    }

    private static JSONObject buildApiResponse(String jsonString) {
        try {
            return new JSONObject(jsonString);
        } catch (JSONException e) {
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
                if (paramKeyPrefix.isEmpty()){
                    prefix = key;
                }else{
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
            if(paramValObj != null){
                params.add(new HttpParam(paramKeyPrefix, paramValObj.toString()));
            }else{
                params.add(new HttpParam(paramKeyPrefix, ""));
            }

        }
        return params;
    }

    private static String specialCharacterEscape(String stringToEscape){
        stringToEscape = FormParameterEscaper.escape(stringToEscape);
        stringToEscape = stringToEscape.replace("*", "%26");
        return stringToEscape;
    }
}