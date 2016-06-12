package com.sunrise.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sunrise.R;
import com.sunrise.activity.LowCategoryActivity;
import com.sunrise.model.Level2Data;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class LowCategoryListViewAdapter extends BaseAdapter {
    private List<String> keys;
    private List<String> values;
    private List<String> editableIndicators;
    private LayoutInflater layoutInflater;
    private LowCategoryActivity activity;
    private Level2Data level2Data;

    public LowCategoryListViewAdapter(LowCategoryActivity context) {
        activity = context;
        layoutInflater = LayoutInflater.from(activity);
    }


    public void setData(Collection<String> keys, Collection<String> values, Collection<String> editableIndicators) {
        this.keys = new ArrayList<String>();
        this.keys.addAll(keys);
        this.values = new ArrayList<String>();
        this.values.addAll(values);
        this.editableIndicators=new ArrayList<String>();
        this.editableIndicators.addAll(editableIndicators);

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

    public List<String> getValues() {
        return values;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_detail, parent, false);
        TextView tvKey = (TextView) convertView.findViewById(R.id.item_tv_detail);
        EditText etKeyValue = (EditText) convertView.findViewById(R.id.item_et_detail);
        tvKey.setText(keys.get(position));
        etKeyValue.setText(values.get(position));

        if (editableIndicators.get(position).equals(String.valueOf(0)))
        {
            etKeyValue.setEnabled(false);
        }
        else
        {
            etKeyValue.setEnabled(true);
        }

        etKeyValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activity.setSaveButtonVisible();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                values.set(position, s.toString());
            }
        });

        return convertView;
    }
}
