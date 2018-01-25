package com.stewhouse.nproject.model;

import org.json.JSONObject;

/**
 * Created by Gomguk on 16. 7. 11..
 */
public class Item {

    static final String JSON_PARAM_ROOT = "item";

    private static final String JSON_PARAM_TITLE = "title";

    private String mTitle = null;

    public String getTitle() {
        return mTitle;
    }

    static Item parse(JSONObject jsonObject) {
        Item item = new Item();

        try {
            if (jsonObject.has(JSON_PARAM_TITLE)) {
                String titleStr = (String) jsonObject.get(JSON_PARAM_TITLE);

                if (titleStr != null) {
                    item.mTitle = titleStr;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return item;
    }
}