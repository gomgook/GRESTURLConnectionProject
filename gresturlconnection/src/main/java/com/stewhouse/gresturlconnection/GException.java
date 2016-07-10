package com.stewhouse.gresturlconnection;

import android.util.Log;

/**
 * Created by Gomguk on 2016-07-10.
 */
public class GException extends Exception {
    public enum ErrorType {
        RESPONSE_CODE_ERROR, SCHEME_NOT_SUPPORTED, REQUEST_TYPE_NOT_SUPPORTED, TIMEOUT_VALUE_INVALID, LISTENER_NULL_POINTER
    }

    public GException(ErrorType errorType) {
        switch (errorType) {
            case RESPONSE_CODE_ERROR:
                Log.e(errorType.toString(), "GRESTURLConnectionListener is null.\nGRESTURLConnectionListener should be allocated before GRESTURLConnection is used.");
            case SCHEME_NOT_SUPPORTED:
                Log.e(errorType.toString(), "Not supported scheme. We support HTTP or HTTPS only.");
            case REQUEST_TYPE_NOT_SUPPORTED:
                Log.e(errorType.toString(), "Not supported REST request type. We support GET, POST, PUT, DELETE only.");
            case TIMEOUT_VALUE_INVALID:
                Log.e(errorType.toString(), "The connection response code is not 200.");
            case LISTENER_NULL_POINTER:
                Log.e(errorType.toString(), "The timeout value is not invalid. The timeout value should be over 0.");
        }
    }
}
