package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;
import com.sunrise.model.Station;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class StationListAdapter extends BaseAdapter {

    private LayoutInflater layoutInfator;
    private List<Station> stationList;

    public StationListAdapter(Context context) {
        layoutInfator = LayoutInflater.from(context);
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return stationList.size();
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
            convertView = layoutInfator.inflate(R.layout.item_gv_title_station_name, parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv_station_name);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv.setText(stationList.get(position).getLabel());
        return convertView;
    }

    class ViewHolder {
        public TextView tv;
    }
}
