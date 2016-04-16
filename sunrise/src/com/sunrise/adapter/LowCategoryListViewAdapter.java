package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class LowCategoryListViewAdapter extends BaseAdapter {
    private List<String> keys;
    private List<String> values;
    private LayoutInflater layoutInflater;

    public LowCategoryListViewAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<String> keys, List<String> values) {
        this.keys = keys;
        this.values = values;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (keys == null)
            return 0;
        return keys.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_detail, parent, false);
            holder = new ViewHolder();
            holder.tvKey = (TextView) convertView.findViewById(R.id.item_tv_detail);
            holder.etKeyValue = (EditText) convertView.findViewById(R.id.item_et_detail);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tvKey.setText(keys.get(position));
        holder.etKeyValue.setText(values.get(position));
        return convertView;
    }

    class ViewHolder {
        public EditText etKeyValue;
        public TextView tvKey;
    }
}
