package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.stewhouse.gresturlconnection.GRESTURLConnection;
import com.stewhouse.nproject.model.Channel;
import com.stewhouse.nproject.model.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NMainActivity extends AppCompatActivity implements GRESTURLConnection.GRESTURLConnectionListener {

    private ListView mListView = null;
    private NBaseAdapter mListAdapter = null;
    private int mPage = -1;
    private int mTotalCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.view_list);
        mListAdapter = new NBaseAdapter(this);
        mPage = 1;

        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mListView != null) {
            mListView = null;
        }
        if (mListAdapter != null) {
            mListAdapter = null;
        }
        if (mPage > -1) {
            mPage = -1;
        }
        if (mTotalCount > -1) {
            mTotalCount = -1;
        }
    }

    private void loadData() {
        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);
        HashMap<String, String> params = new HashMap<>();

        params.put("apikey", NConstants.API_KEY);
        params.put("q", "위인");
        params.put("output", "json");
        params.put("pageno", String.valueOf(mPage));
        params.put("result", String.valueOf(NConstants.LIST_EXTRA_LOADING_PRE_COUNT));
        connection.execute("https://apis.daum.net/search/book", params, 3000, GRESTURLConnection.RequestType.GET, null, null, null);
    }

    @Override
    public void onPostExecute(Object result) {
        try {
            if (result != null) {
                if (result instanceof String) {
                    JSONObject jsonObject = new JSONObject(result.toString());

                    if (jsonObject != null) {
                        if (jsonObject.has(Channel.JSON_PARAM_ROOT) == true) {
                            Channel channel = Channel.parseJSONObject(jsonObject.getJSONObject(Channel.JSON_PARAM_ROOT));

                            if (channel != null) {
                                if (channel.getTotalCount() > -1) {
                                    mTotalCount = channel.getTotalCount();

                                    if (channel.getItems() != null) {
                                        ArrayList<Item> data = channel.getItems();

                                        setListView(data);
                                    }
                                }
//                                if (channel != null && channel.getItems() != null) {
//                                    ArrayList<Item> data;
//
//                                    if (mCanLoadExtra == true) {
//                                        data = mListAdapter.getData();
//                                        data.addAll(channel.getItems());
//                                    } else {
//                                        data = channel.getItems();
//                                    }
//                                    mListAdapter.setData(data);
//                                    mListView.setAdapter(mListAdapter);
//                                }
                            }
                        }
                    }
                } else {

                    // TODO: Process each Exception cases.
                }
            } else {

                // TODO: Process error case.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setListView(ArrayList<Item> data) {
        mListAdapter.setData(data);
        mListView.setAdapter(mListAdapter);
    }
}