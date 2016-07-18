package com.tamic.inject.http;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HeaderElement;
import cz.msebera.android.httpclient.ParseException;

/**
 * Created by Tamic on 2016-7-16.
 */
public class TamicHttpClient {

    private static String BASE_URL = "";

    public final static String TAG = "TamicHttpClient";

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static RequestParams requestParams;
    private static Header[] reqHeaders;
    private static HashMap<String, String> headers;
    private static Header header;

    public static void get(String url, Map<String, String> params, AsyncHttpResponseHandler responseHandler) {
        changeParameter(headers, params);
        client.get(getAbsoluteUrl(url), requestParams, responseHandler);

    }

    public static void post(String url, Map<String, String> params, AsyncHttpResponseHandler responseHandler) {
        changeParameter(headers, params);
        client.post(getAbsoluteUrl(url), requestParams, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        Log.d(TAG, BASE_URL + relativeUrl);
        return BASE_URL + relativeUrl;
    }

    public static RequestHandle get(Context context, String url, final Map<String, String> headers, Map<String, String> params, AsyncHttpResponseHandler responseHandler) {
        if(headers == null) {
            get(url,  params, responseHandler);
            return null;
        }
        Log.d(TAG, url);
        changeParameter(headers, params);
        return client.get(context, getAbsoluteUrl(url), reqHeaders, requestParams, responseHandler);
    }

    public static RequestHandle post(Context context, String url, Map<String, String> headers, Map<String, String> params, String contentType,
                            AsyncHttpResponseHandler responseHandler) {
        if(headers == null) {
            post(url, params, responseHandler);
            return null;
        }
        changeParameter(headers, params);

        return client.post(context, getAbsoluteUrl(url), reqHeaders, requestParams, contentType, responseHandler);
    }

    public static void cancle(String tag, boolean isRunning) {
        client.cancelRequestsByTAG(tag, isRunning);
    }

    public static void cancleAll(boolean isRunning) {
        client.cancelAllRequests(isRunning);
    }

    /**
     * setTimeOut
     *
     * @param time 秒数
     */
    public static void setTimeOut(int time) {
        client.setTimeout(time);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    public static void setClient(AsyncHttpClient client) {
        TamicHttpClient.client = client;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    private static void changeParameter(final Map<String, String> headers, Map<String, String> params) {
        if (headers != null && headers.size() > 0) {
            reqHeaders = new Header[headers.size()];
            Set<String> keys = headers.keySet();
            int index = 0;
            for (final String mykey : keys) {
                header = new Header() {
                    @Override
                    public String getName() {
                        return mykey;
                    }

                    @Override
                    public String getValue() {
                        return headers.get(mykey);
                    }

                    @Override
                    public HeaderElement[] getElements() throws ParseException {
                        return new HeaderElement[0];
                    }
                };
                reqHeaders[index++] = header;
                Log.d(TAG, mykey + ":" + headers.get(mykey));
            }

        }

        if (params != null && params.size() > 0) {
            requestParams = new RequestParams();
            Set<String> keys = params.keySet();
            for (String paramsKey : keys) {
                requestParams.add(paramsKey, params.get(paramsKey));
                Log.d(TAG, paramsKey + ":" + params.get(paramsKey));
            }
        }

    }
}
