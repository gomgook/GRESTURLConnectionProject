package com.stewhouse.nproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
        ImageView btn_delete = null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_listview_keyword_cell, parent, false);
            holder = new ViewHolder();
            holder.title_text = (TextView) convertView.findViewById(R.id.title_text);
            holder.btn_delete = (ImageView) convertView.findViewById(R.id.btn_delete);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String keyword = (String) getItem(position);

        if (keyword != null) {
            holder.title_text.setText(keyword);
        }
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NSQLiteOpenHelper nsqLiteOpenHelper = new NSQLiteOpenHelper(mContext);

                nsqLiteOpenHelper.deleteKeyword(nsqLiteOpenHelper.getWritableDatabase(), keyword);
                setData(nsqLiteOpenHelper.getKeywords(nsqLiteOpenHelper.getReadableDatabase()));
            }
        });

        return convertView;
    }
}
