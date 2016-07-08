package com.stewhouse.nproject.utility;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Gomguk on 16. 7. 8..
 */
public class GURLConnection {
    public enum ConnectionType {
        HTTP, HTTPS
    }

    private AsyncTask<String, Object, Object> mAsyncTask = null;
    private String mUrl = null;
    private int mTimeOut = -1;

    public static void execute(String[] params) {
        GURLConnection urlConnection = new GURLConnection();

        urlConnection.executeInternal(params);
    }

    private void executeInternal(String[] params) {
        mAsyncTask = new AsyncTask<String, Object, Object>() {

            @Override
            protected Object doInBackground(String... params) {
                StringBuilder stringBuilder = new StringBuilder();

                setParams(params);

                try {
                    URL url = new URL(mUrl);

                    if (mTimeOut > -1) {
                        if (checkConnectionType(mUrl) == ConnectionType.HTTP) {
                            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                            httpConn.setRequestMethod("GET");
                            httpConn.setConnectTimeout(mTimeOut);

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

                        } else if (checkConnectionType(mUrl) == ConnectionType.HTTPS) {
                            HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();

                            httpsConn.setRequestMethod("GET");
                            httpsConn.setConnectTimeout(mTimeOut);

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
                        }
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }

                return stringBuilder.toString();
            }
        };
        mAsyncTask.execute(params);
    }

    private void setParams(String[] params) {
        mUrl = params[0];
        mTimeOut = Integer.parseInt(params[1]);
    }

    private ConnectionType checkConnectionType(String url) {
        if (url.startsWith("https:")) {
            return ConnectionType.HTTPS;
        }

        return ConnectionType.HTTP;
    }
}
