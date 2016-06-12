package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupAdapter extends android.widget.BaseAdapter {
    private Context context;
    private List<String> list;
    private List<Integer> lv;

    public GroupAdapter(Context context, List<String> list, List<Integer> lv) {
        super();
        this.context = context;
        this.list = list;
        this.lv=lv;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            convertView=LayoutInflater.from(context).inflate(R.layout.item_lv_popup, null);
            holder=new ViewHolder();
            convertView.setTag(holder);
            holder.item_popup=(TextView) convertView.findViewById(R.id.item_tv_pop);
            holder.iv_item_pop=(ImageView) convertView.findViewById(R.id.item_iv_pop);
        } else {
            holder=(ViewHolder) convertView.getTag();
        }
        holder.item_popup.setText(list.get(position));
        holder.iv_item_pop.setImageResource(lv.get(position));

        return convertView;
    }

    public class ViewHolder{

        ImageView iv_item_pop;
        TextView item_popup;

    }

}
