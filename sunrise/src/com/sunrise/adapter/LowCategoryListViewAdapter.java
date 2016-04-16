package com.sunrise.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sunrise.R;

import android.content.Context;
import android.text.InputType;
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

    public void setData(Collection<String> keys, Collection<String> values) {
        this.keys = new ArrayList<String>();
        this.keys.addAll(keys);
        this.values = new ArrayList<String>();
        this.values.addAll(values);
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
            holder.etKeyValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
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
