package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stewhouse.gresturlconnection.GRESTURLConnection;
import com.stewhouse.nproject.model.Channel;
import com.stewhouse.nproject.model.Item;
import com.stewhouse.nproject.util.GSwipeRefreshLayout;
import com.stewhouse.nproject.util.GUtil;

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

    private String mSearchKeyword = null;
    private NSQLiteOpenHelper mSQLiteOpenHelper = null;

    private RelativeLayout mSearchLayout = null;
    private ListView mSearchListView = null;
    private GSwipeRefreshLayout mSwipeRefreshLayout = null;

    private int mPage = -1;
    private int mTotalCount = -1;
    private boolean mCanLoadExtra = false;
    private boolean mIsSearchStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set Search layout.
        final ImageView deleteBtn = (ImageView) findViewById(R.id.btn_delete);
        final EditText editText = (EditText) findViewById(R.id.edit_search);
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (editText != null) {
                        editText.setText("");
                    }
                }
            });
        }

        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (deleteBtn != null) {
                        if (s.length() > 0) {
                            deleteBtn.setVisibility(View.VISIBLE);
                        } else {
                            deleteBtn.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        mSearchKeyword = v.getText().toString();
                        mSwipeRefreshLayout.getAdapter().setSearchKeyword(mSearchKeyword);
                        doSearch(false);
                    }

                    return false;
                }
            });
        }

        mSQLiteOpenHelper = NSQLiteOpenHelper.getInstance(this);
        mSearchLayout = (RelativeLayout) findViewById(R.id.layout_search_list);
        RelativeLayout searchDeleteBtn = (RelativeLayout) findViewById(R.id.btn_search_delete_all);

        if (searchDeleteBtn != null) {
            searchDeleteBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    NSQLiteOpenHelper nsqLiteOpenHelper = NSQLiteOpenHelper.getInstance(NMainActivity.this);
                    NBaseSearchAdapter searchAdapter = (NBaseSearchAdapter) mSearchListView.getAdapter();

                    nsqLiteOpenHelper.deleteAllKeywords(nsqLiteOpenHelper.getWritableDatabase());
                    searchAdapter.setData(nsqLiteOpenHelper.getKeywords(nsqLiteOpenHelper.getReadableDatabase()));
                }
            });
        }
        TextView deleteBtnText = (TextView) findViewById(R.id.text_search_delete_all);

        if (deleteBtnText != null) {
            String str = "검색기록 <font color=\"" + GUtil.getColor(this, R.color.bg_search_delete_all_cell_highlight) + "\">삭제</font>";

            deleteBtnText.setText(Html.fromHtml(str));
        }
        mSearchListView = (ListView) findViewById(R.id.view_search_list);
        if (mSearchListView != null) {
            mSearchListView.setDivider(null);
        }
        setSearchListView();

        // Set SwipeRefreshLayout.
        mSwipeRefreshLayout = (GSwipeRefreshLayout) findViewById(R.id.layout_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                doSearch(false);
            }
        });
        mSwipeRefreshLayout.setListView((ListView) findViewById(R.id.view_list));
        mSwipeRefreshLayout.setAdapter(new NBaseResultAdapter(this));
        mSwipeRefreshLayout.getListView().setOnScrollListener(this);
        mSwipeRefreshLayout.getListView().setDivider(null);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_listview_footer, null);

        if (view != null) {
            mSwipeRefreshLayout.setFooterLoadingView(view);
        }

        mPage = 1;
    }

    private void doSearch(boolean isDataAdd) {
        if (!mIsSearchStarted) {
            mIsSearchStarted = true;
        }
        setListViewsVisibility();
        mSQLiteOpenHelper.insertKeyword(mSQLiteOpenHelper.getWritableDatabase(), mSearchKeyword);

        if (!isDataAdd) {
            mPage = 1;
        } else {
            mPage++;
        }
        loadData();
    }

    private void loadData() {
        mSwipeRefreshLayout.addLoadingFooter();

        GRESTURLConnection connection = new GRESTURLConnection();
        HashMap<String, String> params = new HashMap<>();

        connection.setListener(this);

        params.put(API_PARAM_APIKEY, NConstants.API_KEY);
        params.put(API_PARAM_KEYWORD, mSearchKeyword);
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

                    if (jsonObject.has(Channel.JSON_PARAM_ROOT)) {
                        Channel channel = Channel.parse(jsonObject.getJSONObject(Channel.JSON_PARAM_ROOT));

                        if (channel != null) {
                            if (channel.getTotalCount() > -1) {
                                mTotalCount = channel.getTotalCount();

                                if (channel.getItems() != null) {
                                    ArrayList<Item> data = channel.getItems();

                                    setListView(data, mPage > 1);
                                    checkCanLoadExtra();
                                } else {

                                    // TODO: Handle when Item list is null.
                                }
                            } else {

                                // TODO: Handle when totalCount is invalid value.
                            }
                        } else {

                            // TODO: Handle when the Channel model is null.
                        }
                    } else {

                        // TODO: Handle when the result doesn't have ROOT JSON parameter.
                    }
                } else {

                    // TODO: Handle when connection result is not String(Error case).
                }
            } else {

                // TODO: Handle when connection result is null(Error case).
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSearchListView() {
        ArrayList<String> data = mSQLiteOpenHelper.getKeywords(mSQLiteOpenHelper.getReadableDatabase());
        NBaseSearchAdapter listAdapter = new NBaseSearchAdapter(this);

        if (data != null) {
            listAdapter.setData(data);
            mSearchListView.setAdapter(listAdapter);
        }
    }

    private void setListView(ArrayList<Item> data, boolean isDataAdd) {
        ArrayList<Item> listData;
        NBaseResultAdapter listAdapter = mSwipeRefreshLayout.getAdapter();

        if (isDataAdd) {
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
        mCanLoadExtra = NConstants.LIST_EXTRA_LOADING_PRE_COUNT * mPage < mTotalCount && mPage < NConstants.API_PAGE_LIMIT;
    }

    private void setListViewsVisibility() {
        if (mSearchLayout.getVisibility() == View.VISIBLE && mSwipeRefreshLayout.getVisibility() == View.GONE) {
            mSearchLayout.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        NBaseResultAdapter listAdapter = mSwipeRefreshLayout.getAdapter();

        if (listAdapter != null) {
            if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount > totalItemCount - NConstants.LIST_EXTRA_LOADING_PRE_COUNT) {
            if (mCanLoadExtra) {
                mCanLoadExtra = false;
                doSearch(true);
            }
        }
    }
}