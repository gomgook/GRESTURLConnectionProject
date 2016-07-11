package com.stewhouse.nproject;

import android.util.Log;

import com.stewhouse.gresturlconnection.GRESTURLConnection;

import org.junit.Test;

import java.util.HashMap;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest implements GRESTURLConnection.GRESTURLConnectionListener {
    @Test
    public void notSupportedScheme() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Not supported scheme.
        connection.execute("jar://file", null, 0, null, null, null, null);
    }

    @Test
    public void requestParams() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Add request parameters.
        HashMap<String, Object> params = new HashMap<>();
        params.put("apikey", "b5a623fe41c1e7dca3566b82ce436985");
        params.put("q", "위인");
        params.put("output", "json");
        params.put("pageno", 1);
        params.put("result", 20);
        connection.execute("https://apis.daum.net/search/book", params, 0, null, null, null, null);
    }

    @Test
    public void notSupportedRequestType() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Not supported request type.
        connection.execute("https://apis.daum.net/search/book", null, 0, null, null, null, null);
    }

    @Test
    public void invalidTimeout() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Invalid timeout.
        connection.execute("https://apis.daum.net/search/book", null, -3000, GRESTURLConnection.RequestType.GET, null, null, null);
    }

    @Test
    public void requestHeader() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Add request header.
        String headerStr = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)";
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", headerStr);
        connection.execute("https://apis.daum.net/search/book", null, 3000, GRESTURLConnection.RequestType.GET, headers, null, null);
    }

    @Test
    public void requestBodyWithoutType() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Add request body.
        String bodyStr = "{\"id\":\"1\",\"title\":\"Hello World!\",\"body\":\"This is my first post!\"}";
        connection.execute("https://apis.daum.net/search/book", null, 3000, GRESTURLConnection.RequestType.GET, null, bodyStr, null);
    }

    @Test
    public void requestBodyWithType() throws Exception {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        // Add request body with body type.
        String bodyStr = "{\"id\":\"1\",\"title\":\"Hello World!\",\"body\":\"This is my first post!\"}";
        connection.execute("https://apis.daum.net/search/book", null, 3000, GRESTURLConnection.RequestType.GET, null, bodyStr, "application/json");
    }

    @Override
    public void onPostExecute(Object result) {
        Log.e("TEST RESULT", "" + result.toString());
    }
}