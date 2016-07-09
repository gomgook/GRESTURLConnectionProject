package com.stewhouse.nproject.utility;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Gomguk on 16. 7. 8..
 */
public class GRESTURLConnection {
    public enum SchemeType {
        HTTP, HTTPS
    }

    public enum RequestType {
        GET, POST, PUT, DELETE
    }

    private AsyncTask<Object, Object, Object> mAsyncTask = null;

    public static void execute(String url, int timeOut, RequestType requestType) {
        GRESTURLConnection urlConnection = new GRESTURLConnection();
        ArrayList<Object> connectionParams = new ArrayList<>();

        connectionParams.add(url);
        connectionParams.add(timeOut);
        connectionParams.add(requestType);

        urlConnection.executeInternal(connectionParams);
    }

    private void executeInternal(ArrayList<Object> connectionParams) {
        mAsyncTask = new AsyncTask<Object, Object, Object>() {

            @Override
            protected Object doInBackground(Object... params) {
                String urlStr = (String) params[0];
                int timeOut = (int) params[1];
                RequestType requestType = (RequestType) params[2];

                StringBuilder stringBuilder = new StringBuilder();

                try {
                    URL url = new URL(urlStr);

                    if (timeOut > -1) {
                        if (checkScheme(urlStr).equals(SchemeType.HTTP)) {
                            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                            httpConn.setRequestMethod(requestType.toString());
                            httpConn.setConnectTimeout(timeOut);

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
                            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();

                            httpsConn.setRequestMethod(requestType.toString());
                            httpsConn.setConnectTimeout(timeOut);

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
}
