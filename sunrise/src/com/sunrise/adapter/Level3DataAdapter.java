package com.sunrise.adapter;

import java.util.List;
import java.util.Map;

import com.sunrise.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Level3DataAdapter extends BaseAdapter {

    private List<Map<String, String>> level3Data;
    private LayoutInflater inflater;

    public Level3DataAdapter(Context cxt) {
        inflater = LayoutInflater.from(cxt);
    }

    public void setLevelData(List<Map<String, String>> list) {
        this.level3Data = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (level3Data == null)
            return 0;
        return level3Data.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mid3, parent, false);
            holder = new ViewHolder();
            holder.itemView = (TextView) convertView.findViewById(R.id.btn_mid);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.itemView.setText(level3Data.get(position).get("name"));
        return convertView;
    }

    class ViewHolder {
        public TextView itemView;
    }
}
