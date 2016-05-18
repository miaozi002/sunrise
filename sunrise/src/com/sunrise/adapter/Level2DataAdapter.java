package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;
import com.sunrise.model.Level2Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Level2DataAdapter extends BaseAdapter {

    private List<Level2Data> level2DataList;
    private LayoutInflater inflater;

    public Level2DataAdapter(Context cxt) {
        inflater = LayoutInflater.from(cxt);
    }

    @Override
    public int getCount() {
        if (level2DataList == null)
            return 0;
        return (level2DataList.size());
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
            convertView = inflater.inflate(R.layout.item_mid2, parent, false);
            holder = new ViewHolder();
            holder.itemView = (TextView) convertView.findViewById(R.id.btn_mid);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        Level2Data level2Data = level2DataList.get(position);
        holder.itemView.setText(level2Data.getLabel());
        return convertView;
    }

    public void setLevelData(List<Level2Data> level2DataList) {
        this.level2DataList = level2DataList;
        notifyDataSetChanged();
    }

    class ViewHolder {
        public TextView itemView;
    }
}
