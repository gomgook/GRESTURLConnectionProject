package com.stewhouse.nproject;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class NMainActivity extends AppCompatActivity
        implements GRESTURLConnection.GRESTURLConnectionListener {
    private static final String API_PARAM_API_KEY = "apikey";
    private static final String API_PARAM_KEYWORD = "q";
    private static final String API_PARAM_OUTPUT = "output";
    private static final String API_PARAM_PAGE_NO = "pageno";
    private static final String API_PARAM_RESULT = "result";

    private static final String API_URL = "https://apis.daum.net/search/book";

    private String mSearchKeyword = null;
    private NSQLiteOpenHelper mSQLiteOpenHelper = null;

    private RelativeLayout mSearchLayout = null;
    private ListView mSearchListView = null;
    private GSwipeRefreshLayout mSwipeRefreshLayout = null;

    private NewNBaseResultAdapter mResultAdapter = null;

    private int mPage = -1;
    private int mTotalCount = -1;
    private boolean mCanLoadExtra = false;
    private boolean mIsLoading = false;

    // Flag to determine if search history list should be shown or not at first time.
    private boolean mIsSearchStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Set Search layout.
        final ImageView deleteBtn = findViewById(R.id.btn_delete);
        final EditText editText = findViewById(R.id.edit_search);

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
                        mResultAdapter.setSearchKeyword(mSearchKeyword);
                        doSearch(false);
                    }

                    return false;
                }
            });
        }

        mSQLiteOpenHelper = NSQLiteOpenHelper.getInstance(this);
        mSearchLayout = findViewById(R.id.layout_search_list);
        RelativeLayout searchDeleteBtn = findViewById(R.id.btn_search_delete_all);

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
        TextView deleteBtnText = findViewById(R.id.text_search_delete_all);

        if (deleteBtnText != null) {
            String str = "검색기록 <font color=\""
                    + GUtil.getColor(this, R.color.bg_search_delete_all_cell_highlight)
                    + "\">삭제</font>";

            deleteBtnText.setText(Html.fromHtml(str));
        }
        mSearchListView = findViewById(R.id.view_search_list);
        if (mSearchListView != null) {
            mSearchListView.setDivider(null);
            setSearchListView();
        }

        mResultAdapter = new NewNBaseResultAdapter();

        // Set SwipeRefreshLayout.
        mSwipeRefreshLayout = findViewById(R.id.layout_swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                doSearch(false);
            }
        });
        mSwipeRefreshLayout.setListView((RecyclerView) findViewById(R.id.view_list));
        mSwipeRefreshLayout.setAdapter(mResultAdapter);
        mSwipeRefreshLayout.setLayoutManager(new LinearLayoutManager(this));
        mSwipeRefreshLayout.getListView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) mSwipeRefreshLayout.getListView().getLayoutManager();

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= NConstants.LIST_EXTRA_LOADING_PRE_COUNT + 1) {
                    if (mCanLoadExtra && !mIsLoading) {
                        doSearch(true);
                    }
                }
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.view_listview_footer, null);

        if (view != null) {
            mSwipeRefreshLayout.setFooterLoadingView(view);
        }

        mPage = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mIsSearchStarted) {
            setListViewsVisibility(true);
        } else {
            setListViewsVisibility(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mIsSearchStarted) {
            mIsSearchStarted = false;
        }
    }

    private void doSearch(boolean isDataAdd) {
        if (!mIsSearchStarted) {
            mIsSearchStarted = true;
        }

        mIsLoading = true;

        setListViewsVisibility(true);
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

        params.put(API_PARAM_API_KEY, NConstants.API_KEY);
        try {
            params.put(API_PARAM_KEYWORD, URLEncoder.encode(mSearchKeyword, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put(API_PARAM_OUTPUT, "json");
        params.put(API_PARAM_PAGE_NO, String.valueOf(mPage));
        params.put(API_PARAM_RESULT, String.valueOf(NConstants.LIST_EXTRA_LOADING_PRE_COUNT));
        connection.execute(API_URL,
                params,
                NConstants.CONNECTION_TIMEOUT,
                GRESTURLConnection.RequestType.GET,
                null,
                null,
                null);

    }

    @Override
    public void onPostExecute(String result) {
        mIsLoading = false;

        if (result == null) {
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result);

            if (!jsonObject.has(Channel.JSON_PARAM_ROOT)) {
                return;
            }

            Channel channel = Channel.parse(jsonObject.getJSONObject(Channel.JSON_PARAM_ROOT));

            if (channel != null) {
                if (channel.getTotalCount() > -1) {
                    mTotalCount = channel.getTotalCount();
                    ArrayList<Item> data = channel.getItems();

                    setListView(data, mPage > 1);

                    mCanLoadExtra = checkCanLoadExtra();
                    mResultAdapter.setLastPageLoaded(!checkCanLoadExtra());
                } else {

                    // TODO: Handle when totalCount is invalid value.
                }
            } else {

                // TODO: Handle when the Channel model is null.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSearchListView() {
        ArrayList<String> data = mSQLiteOpenHelper.getKeywords(mSQLiteOpenHelper.getReadableDatabase());
        NBaseSearchAdapter listAdapter;

        if (data != null) {
            if (mSearchListView.getAdapter() == null) {
                listAdapter = new NBaseSearchAdapter(this);
                listAdapter.setData(data);
                mSearchListView.setAdapter(listAdapter);
            } else {
                listAdapter = (NBaseSearchAdapter) mSearchListView.getAdapter();
                listAdapter.setData(data);
            }
        }
    }

    private void setListView(ArrayList<Item> data, boolean isDataAdd) {
        ArrayList<Item> listData;

        if (isDataAdd) {
            listData = mResultAdapter.getData();
            listData.addAll(data);
        } else {
            listData = data;
        }

        mResultAdapter.setData(listData);
        mSwipeRefreshLayout.getListView().setAdapter(mResultAdapter);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private boolean checkCanLoadExtra() {

        // Limited result pages to 3
        // because the API always returns 3 pages even totalCount of data is over 3 pages.
        return NConstants.LIST_EXTRA_LOADING_PRE_COUNT * mPage < mTotalCount
                && mPage < NConstants.API_PAGE_LIMIT;
    }

    private void setListViewsVisibility(boolean showSwipeLayout) {
        if (showSwipeLayout) {
            if (mSearchLayout.getVisibility() == View.VISIBLE
                    && mSwipeRefreshLayout.getVisibility() == View.GONE) {
                mSearchLayout.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (mSwipeRefreshLayout.getVisibility() == View.VISIBLE
                    && mSearchLayout.getVisibility() == View.GONE) {
                setSearchListView();
                mSearchLayout.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setVisibility(View.GONE);
            }
        }
    }
}