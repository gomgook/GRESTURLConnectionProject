package com.stewhouse.nproject;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.stewhouse.nproject.model.Item;

import java.util.ArrayList;

/**
 * Created by Gomguk on 16. 7. 11..
 */
public class NBaseAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<Item> mData = null;

    private String mSearchKeyword = null;

    public NBaseAdapter(Context context) {
        mContext = context;
    }

    public void setData(ArrayList<Item> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public ArrayList<Item> getData() {
        return mData;
    }

    public void setSearchKeyword(String searchKeyword) {
        mSearchKeyword = searchKeyword;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_listview_cell, parent, false);
            holder = new ViewHolder();
            holder.title_text = (TextView) convertView.findViewById(R.id.title_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Item item = (Item) getItem(position);

        if (item != null) {
            if (item.getTitle() != null) {
                String htmlStr = item.getTitle();
                htmlStr = htmlStr.replace(mSearchKeyword, "<font color=\"" + getColor(mContext, R.color.view_listview_cell_title_highlight) + "\">" + mSearchKeyword + "</font>");

                holder.title_text.setText(Html.fromHtml(htmlStr));
            }
        }
        return convertView;
    }

    private int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            return context.getResources().getColor(id);
        }
    }
}
