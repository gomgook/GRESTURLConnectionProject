package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.stewhouse.gresturlconnection.GRESTURLConnection;

import java.util.HashMap;

public class NMainActivity extends AppCompatActivity implements GRESTURLConnection.GRESTURLConnectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);

        HashMap<String, String> params = new HashMap<>();
        params.put("apikey", "b5a623fe41c1e7dca3566b82ce436985");
        params.put("q", "위인");
        params.put("output", "json");
        params.put("pageno", "1");
        params.put("result", "20");
        connection.execute("https://apis.daum.net/search/book", params, 3000, GRESTURLConnection.RequestType.GET, null, null, null);
    }

    @Override
    public void onPostExecute(Object result) {
        Log.e("RESULT", "" + result.toString());
    }
}