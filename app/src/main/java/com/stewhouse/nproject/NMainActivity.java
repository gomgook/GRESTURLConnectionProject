package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.stewhouse.gresturlconnection.GRESTURLConnection;
import com.stewhouse.nproject.model.Channel;
import com.stewhouse.nproject.model.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class NMainActivity extends AppCompatActivity implements GRESTURLConnection.GRESTURLConnectionListener, AbsListView.OnScrollListener {

    private static final String API_PARAM_APIKEY = "apikey";
    private static final String API_PARAM_KEYWORD = "q";
    private static final String API_PARAM_OUTPUT = "output";
    private static final String API_PARAM_PAGENO = "pageno";
    private static final String API_PARAM_RESULT = "result";

    private static final String API_URL = "https://apis.daum.net/search/book";

    private GSwipeRefreshLayout mSwipeRefreshLayout = null;

    private int mPage = -1;
    private int mTotalCount = -1;
    private boolean mCanLoadExtra = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set Search layout.
        final ImageView deleteBtn = (ImageView) findViewById(R.id.btn_delete);
        final EditText editText = (EditText) findViewById(R.id.edit_search);
        deleteBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    deleteBtn.setVisibility(View.VISIBLE);
                } else {
                    deleteBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set SwipeRefreshLayout.
        mSwipeRefreshLayout = (GSwipeRefreshLayout) findViewById(R.id.layout_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                mPage = 1;
                loadData();
            }
        });
        mSwipeRefreshLayout.setListView((ListView) findViewById(R.id.view_list));
        mSwipeRefreshLayout.setAdapter(new NBaseAdapter(this));
        mSwipeRefreshLayout.setFooterLoadingView(getLayoutInflater().inflate(R.layout.view_listview_footer, null));
        mSwipeRefreshLayout.getListView().setOnScrollListener(this);
        mSwipeRefreshLayout.getListView().setDivider(null);

        mPage = 1;
        loadData();
    }

    private void loadData() {
        mSwipeRefreshLayout.addLoadingFooter();

        GRESTURLConnection connection = new GRESTURLConnection();
        connection.setListener(this);
        HashMap<String, String> params = new HashMap<>();

        params.put(API_PARAM_APIKEY, NConstants.API_KEY);
        params.put(API_PARAM_KEYWORD, "위인");
        params.put(API_PARAM_OUTPUT, "json");
        params.put(API_PARAM_PAGENO, String.valueOf(mPage));
        params.put(API_PARAM_RESULT, String.valueOf(NConstants.LIST_EXTRA_LOADING_PRE_COUNT));
        connection.execute(API_URL, params, NConstants.CONNECTION_TIMEOUT, GRESTURLConnection.RequestType.GET, null, null, null);
    }

    @Override
    public void onPostExecute(Object result) {
        mSwipeRefreshLayout.removeLoadingFooter();

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

                                        setListView(data, mPage > 1);
                                        checkCanLoadExtra();
                                    }
                                }
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

    private void setListView(ArrayList<Item> data, boolean isDataAdd) {
        ArrayList<Item> listData;
        NBaseAdapter listAdapter = mSwipeRefreshLayout.getAdapter();

        if (isDataAdd == true) {
            listData = listAdapter.getData();
            listData.addAll(data);
        } else {
            listData = data;
        }
        listAdapter.setData(listData);
        mSwipeRefreshLayout.getListView().setAdapter(listAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void checkCanLoadExtra() {
        if (NConstants.LIST_EXTRA_LOADING_PRE_COUNT * mPage < mTotalCount && mPage < NConstants.API_PAGE_LIMIT) {
            mCanLoadExtra = true;
        } else {
            mCanLoadExtra = false;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        NBaseAdapter listAdapter = mSwipeRefreshLayout.getAdapter();

        if (listAdapter != null) {
            if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount > totalItemCount - NConstants.LIST_EXTRA_LOADING_PRE_COUNT) {
            if (mCanLoadExtra == true) {
                mCanLoadExtra = false;
                mPage++;

                loadData();
            }
        }
    }
}