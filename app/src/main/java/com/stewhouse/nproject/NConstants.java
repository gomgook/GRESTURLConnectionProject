package com.stewhouse.nproject;

/**
 * Created by Gomguk on 16. 7. 11..
 */
public class NConstants {

    public static final String API_KEY = "b5a623fe41c1e7dca3566b82ce436985";

    // Limited result pages to 3
    // because the API always returns 3 pages even totalCount of data is over 3 pages.
    public static final int API_PAGE_LIMIT = 3;

    public static final int LIST_EXTRA_LOADING_PRE_COUNT = 20;

    public static final int CONNECTION_TIMEOUT = 3000;

    public static final int SEARCH_KEYWORD_LIMIT = 20;
}
