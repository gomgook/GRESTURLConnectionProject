package com.stewhouse.nproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gomguk on 16. 7. 13..
 */
public class NBaseSearchAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<String> mData = null;

    public NBaseSearchAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<String> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView title_text = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_listview_keyword_cell, parent, false);
            holder = new ViewHolder();
            holder.title_text = (TextView) convertView.findViewById(R.id.title_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String keyword = (String) getItem(position);

        if (keyword != null) {
            holder.title_text.setText(keyword);
        }

        return convertView;
    }
}
