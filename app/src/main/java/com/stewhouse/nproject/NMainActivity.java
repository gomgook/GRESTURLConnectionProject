package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stewhouse.nproject.utility.GURLConnection;

public class NMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String[] params = new String[] {"https://apis.daum.net/search/book?apikey=b5a623fe41c1e7dca3566b82ce436985&q=위인&output=json&pageno=1&result=20", "3000"};
        GURLConnection.execute(params);
    }
}
