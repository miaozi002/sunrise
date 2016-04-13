package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;
import com.sunrise.model.StationDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StationListAdapter extends BaseAdapter {

    private LayoutInflater layoutInfator;
    private List<StationDetail> stationDetailList;
    private List<Integer> updateList;

    public StationListAdapter(Context context) {
        layoutInfator = LayoutInflater.from(context);
    }

    public void setStationList(List<StationDetail> n) {
        this.stationDetailList = n;
        notifyDataSetChanged();
    }

    public void setUpdateList(List<Integer> updateList) {
        this.updateList = updateList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (stationDetailList == null)
            return 0;
        return stationDetailList.size();
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
            convertView = layoutInfator.inflate(R.layout.item_lv_title_station_name, parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.item_tv_station_name);
            holder.iv = (ImageView) convertView.findViewById(R.id.item_tv_update);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tv.setText(stationDetailList.get(position).getName());
        holder.iv.setVisibility(View.INVISIBLE);
        if (updateList != null && updateList.contains(stationDetailList.get(position).getId())) {
            holder.iv.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        public TextView tv;
        public ImageView iv;
    }
}
