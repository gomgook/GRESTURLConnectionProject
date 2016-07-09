package com.stewhouse.nproject.utility;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Gomguk on 16. 7. 8..
 */
public class GRESTURLConnection {
    private final static String CONNECTION_PARAM_URL = "url";
    private final static String CONNECTION_PARAM_TIMEOUT = "timeout";
    private final static String CONNECTION_PARAM_REQUEST_TYPE = "request_type";
    private final static String CONNECTION_PARAM_HEADERS = "headers";

    public enum SchemeType {
        HTTP, HTTPS
    }

    public enum RequestType {
        GET, POST, PUT, DELETE
    }

    private AsyncTask<HashMap, Object, Object> mAsyncTask = null;

    public static void execute(String url, int timeOut, RequestType requestType, HashMap headers) {
        GRESTURLConnection urlConnection = new GRESTURLConnection();
        HashMap<String, Object> connectionParams = new HashMap<>();

        connectionParams.put(CONNECTION_PARAM_URL, url);
        connectionParams.put(CONNECTION_PARAM_TIMEOUT, timeOut);
        connectionParams.put(CONNECTION_PARAM_REQUEST_TYPE, requestType);
        connectionParams.put(CONNECTION_PARAM_HEADERS, headers);

        urlConnection.executeInternal(connectionParams);
    }

    private void executeInternal(HashMap connectionParams) {
        mAsyncTask = new AsyncTask<HashMap, Object, Object>() {

            @Override
            protected Object doInBackground(HashMap... params) {
                HashMap<String, Object> requestParams = (HashMap) params[0];

                String urlStr = (String) requestParams.get(CONNECTION_PARAM_URL);
                int timeOut = (int) requestParams.get(CONNECTION_PARAM_TIMEOUT);
                RequestType requestType = (RequestType) requestParams.get(CONNECTION_PARAM_REQUEST_TYPE);

                StringBuilder stringBuilder = new StringBuilder();

                try {
                    URL url = new URL(urlStr);
                    URLConnection conn = url.openConnection();

                    if (timeOut > -1) {

                        // Set Integrated parameters.
                        conn.setConnectTimeout(timeOut);
                        if (requestParams.get(CONNECTION_PARAM_HEADERS) != null) {
                            setRequestHeader(conn, (HashMap<String, String>) requestParams.get(CONNECTION_PARAM_HEADERS));
                        }

                        if (checkScheme(urlStr).equals(SchemeType.HTTP)) {
                            HttpURLConnection httpConn = (HttpURLConnection) conn;

                            httpConn.setRequestMethod(requestType.toString());

                            if (httpConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                                String result;

                                while (true) {
                                    result = bufferedReader.readLine();

                                    if (result == null) {
                                        break;
                                    }
                                    stringBuilder.append(result + "\n");
                                }
                                bufferedReader.close();
                                httpConn.disconnect();
                            }

                        } else if (checkScheme(urlStr).equals(SchemeType.HTTPS)) {
                            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;

                            httpsConn.setRequestMethod(requestType.toString());

                            if (httpsConn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
                                String result;

                                while (true) {
                                    result = bufferedReader.readLine();

                                    if (result == null) {
                                        break;
                                    }
                                    stringBuilder.append(result + "\n");
                                }
                                bufferedReader.close();
                                httpsConn.disconnect();
                            }

                        } else {    // If the connection type is neither http nor https.
                            return null;
                        }
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }

                return stringBuilder.toString();
            }
        };
        mAsyncTask.execute(connectionParams);
    }

    private SchemeType checkScheme(String url) {
        if (url.startsWith("http:")) {
            return SchemeType.HTTP;
        } else if (url.startsWith("https:")) {
            return SchemeType.HTTPS;
        }

        return null;
    }

    private void setRequestHeader(URLConnection conn, HashMap<String, String> headers) {
        for (String key : headers.keySet()) {
            conn.addRequestProperty(key, headers.get(key));
        }
    }
}
