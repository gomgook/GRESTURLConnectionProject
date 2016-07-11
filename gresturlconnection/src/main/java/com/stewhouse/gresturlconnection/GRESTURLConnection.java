package com.stewhouse.gresturlconnection;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Gomguk on 16. 7. 8..
 */
public class GRESTURLConnection extends AsyncTask<HashMap, Object, Object> {

    public interface GRESTURLConnectionListener {
        void onPostExecute(Object result);
    }

    private final static String CONNECTION_PARAM_URL = "url";
    private final static String CONNECTION_PARAM_PARAMS = "params";
    private final static String CONNECTION_PARAM_TIMEOUT = "timeout";
    private final static String CONNECTION_PARAM_REQUEST_TYPE = "request_type";
    private final static String CONNECTION_PARAM_HEADERS = "headers";
    private final static String CONNECTION_PARAM_REQUEST_BODY = "request_body";
    private final static String CONNECTION_PARAM_REQUEST_BODY_TYPE = "request_body_type";

    public enum SchemeType {
        HTTP, HTTPS
    }

    public enum RequestType {
        GET, POST, PUT, DELETE
    }

    private GRESTURLConnectionListener mListener = null;

    public void setListener(GRESTURLConnectionListener listener) {
        mListener = listener;
    }

    public void execute(String url, HashMap<String, String> params, int timeOut, RequestType requestType, HashMap<String, String> headers, String requestBody, String requestBodyType) {
        HashMap<String, Object> connectionParams = new HashMap<>();

        connectionParams.put(CONNECTION_PARAM_URL, url);
        connectionParams.put(CONNECTION_PARAM_PARAMS, params);
        connectionParams.put(CONNECTION_PARAM_TIMEOUT, timeOut);
        connectionParams.put(CONNECTION_PARAM_REQUEST_TYPE, requestType);
        connectionParams.put(CONNECTION_PARAM_HEADERS, headers);
        connectionParams.put(CONNECTION_PARAM_REQUEST_BODY, requestBody);
        connectionParams.put(CONNECTION_PARAM_REQUEST_BODY_TYPE, requestBodyType);

        this.execute(connectionParams);
    }

    @Override
    protected Object doInBackground(HashMap... params) {
        try {
            if (mListener != null) {
                HashMap<String, Object> requestParams = (HashMap) params[0];

                String urlStr = (String) requestParams.get(CONNECTION_PARAM_URL);
                if (requestParams.get(CONNECTION_PARAM_PARAMS) != null) {
                    urlStr = setParams((HashMap<String, String>) requestParams.get(CONNECTION_PARAM_PARAMS), urlStr);
                }
                int timeOut = (int) requestParams.get(CONNECTION_PARAM_TIMEOUT);
                RequestType requestType = (RequestType) requestParams.get(CONNECTION_PARAM_REQUEST_TYPE);

                StringBuilder stringBuilder = new StringBuilder();

                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();

                if (timeOut > -1) {

                    // Set Integrated parameters.
                    conn.setConnectTimeout(timeOut);

                    if (requestParams.get(CONNECTION_PARAM_HEADERS) != null) {
                        setRequestHeader(conn, (HashMap<String, String>) requestParams.get(CONNECTION_PARAM_HEADERS));
                    }

                    if (requestParams.get(CONNECTION_PARAM_REQUEST_BODY) != null && requestParams.get(CONNECTION_PARAM_REQUEST_BODY_TYPE) != null) {
                        String requestBody = (String) requestParams.get(CONNECTION_PARAM_REQUEST_BODY);
                        String requestBodyType = (String) requestParams.get(CONNECTION_PARAM_REQUEST_BODY_TYPE);

                        if (requestBodyType != null) {
                            conn.addRequestProperty("content_type", requestBodyType);
                        }
                        conn.setDoOutput(true);
                        OutputStream outputStream = conn.getOutputStream();
                        outputStream.write(requestBody.getBytes("UTF-8"));
                        outputStream.close();
                    }

                    // Set parameters which is classified by whether the connection is HTTP or HTTPS.
                    if (requestType != null) {
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
                            } else {
                                return new GException(GException.ErrorType.RESPONSE_CODE_ERROR);
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
                            } else {
                                return new GException(GException.ErrorType.RESPONSE_CODE_ERROR);
                            }

                        } else {    // If the connection type is neither http nor https.
                            return new GException(GException.ErrorType.SCHEME_NOT_SUPPORTED);
                        }
                    } else {
                        return new GException(GException.ErrorType.REQUEST_TYPE_NOT_SUPPORTED);
                    }
                } else {
                    return new GException(GException.ErrorType.TIMEOUT_VALUE_INVALID);
                }

                return stringBuilder.toString();
            } else {
                return new GException(GException.ErrorType.LISTENER_NULL_POINTER);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        };

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        mListener.onPostExecute(o);
    }

    private SchemeType checkScheme(String url) {
        if (url.startsWith("http:")) {
            return SchemeType.HTTP;
        } else if (url.startsWith("https:")) {
            return SchemeType.HTTPS;
        }

        return null;
    }

    private String setParams(HashMap<String, String> params, String url) {
        StringBuilder stringBuilder = new StringBuilder(url);

        stringBuilder.append("?");
        for (String key : params.keySet()) {
            stringBuilder.append(key + "=" + params.get(key) + "&");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());

        return stringBuilder.toString();
    }

    private void setRequestHeader(URLConnection conn, HashMap<String, String> headers) {
        for (String key : headers.keySet()) {
            conn.addRequestProperty(key, headers.get(key));
        }
    }
}