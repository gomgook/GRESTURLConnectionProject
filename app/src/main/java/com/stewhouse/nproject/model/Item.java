package com.stewhouse.nproject.model;

import org.json.JSONObject;

/**
 * Created by Gomguk on 16. 7. 11..
 */
public class Item {
    public static final String JSON_PARAM_ROOT = "item";

    private static final String JSON_PARAM_TITLE = "title";

    public String mTitle = null;

    public String getTitle() {
        return mTitle;
    }

    public static Item parseJSONObject(JSONObject jsonObject) {
        Item item = new Item();

        try {
            if (jsonObject.has(JSON_PARAM_TITLE) == true) {
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