package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;
import com.sunrise.model.Level1Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Level1DataAdapter extends BaseAdapter {
    private List<Level1Data> level1DataList;
    private LayoutInflater inflater;

    public Level1DataAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setLevelData(List<Level1Data> list) {
        this.level1DataList = list;
    }

    @Override
    public int getCount() {
        return level1DataList.size();
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
            convertView = inflater.inflate(R.layout.item_mid, parent, false);
            holder = new ViewHolder();
            holder.itemView = (TextView) convertView.findViewById(R.id.btn_mid);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();

        Level1Data level1Data = level1DataList.get(position);
        holder.itemView.setText(level1Data.getLabel());
        return convertView;
    }

    class ViewHolder {
        public TextView itemView;
    }
}
