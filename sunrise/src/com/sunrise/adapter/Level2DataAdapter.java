package com.sunrise.adapter;

import java.util.List;

import com.sunrise.R;
import com.sunrise.activity.HighCategoryActivity;
import com.sunrise.model.Level1Data;
import com.sunrise.model.Level2Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class Level2DataAdapter extends BaseExpandableListAdapter {
    private List<Level2Data> level2DataList;
    private LayoutInflater inflater;
    private HighCategoryActivity activity;
    private Level2Data level2Data;
    private List<Level1Data> level1DataList;

    public Level2DataAdapter(Context cxt) {
        inflater = LayoutInflater.from(cxt);
    }

    public void setLevelData(List<Level2Data> level2DataList) {
        this.level2DataList = level2DataList;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        if (level2DataList == null)
            return 0;
        return (level2DataList.size());
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        if (level2DataList.get(groupPosition).getData()==null)
            return 0;
        return level2DataList.get(groupPosition).getData().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mid2, parent, false);
            holder = new ViewHolder();
            holder.itemView = (TextView) convertView.findViewById(R.id.btn_mid);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        level2Data = level2DataList.get(groupPosition);
        holder.itemView.setText("           "+level2Data.getLabel());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mid3, parent, false);
            holder = new ViewHolder();
            holder.itemView2 = (TextView) convertView.findViewById(R.id.btn_mid);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.itemView2.setText("                  "+level2DataList.get(groupPosition).getData().get(childPosition).get("name"));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

     class ViewHolder {
     public TextView itemView2;
    public TextView itemView;
     }

}
