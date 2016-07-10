package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.stewhouse.gresturlconnection.GRESTURLConnection;

public class NMainActivity extends AppCompatActivity implements GRESTURLConnection.GRESTURLConnectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);
        connection.execute("https://apis.daum.net/search/book?apikey=b5a623fe41c1e7dca3566b82ce436985&q=위인&output=json&pageno=1&result=20", -2, GRESTURLConnection.RequestType.GET, null, null, null);
    }

    @Override
    public void onPostExecute(Object result) {
        Log.e("RESULT", "" + result.toString());
    }
}
