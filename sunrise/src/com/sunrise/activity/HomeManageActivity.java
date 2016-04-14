package com.sunrise.activity;

import com.sunrise.R;
import com.sunrise.TourActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeManageActivity extends Activity {
    private GridView gvHome;
    private static final String[] managements = { "设备管理", "任务管理", "巡视管理", "报警管理", "查岗管理", "运行分析", "交接班管理" };
    private static int[] icons = { R.drawable.equipment, R.drawable.task, R.drawable.tour, R.drawable.warn,
            R.drawable.inspect, R.drawable.run, R.drawable.shift };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_manage);
        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0:
                    Intent intent = new Intent(HomeManageActivity.this, StationListActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    Intent intent2 = new Intent(HomeManageActivity.this, TourActivity.class);
                    startActivity(intent2);
                    break;
                case 5:
                    Intent intent5 = new Intent(HomeManageActivity.this, com.sunrise.CurveActivity.class);
                    startActivity(intent5);
                    break;

                default:
                    break;
                }

            }
        });
    }

    private class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return managements.length;
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
            View view = View.inflate(getApplicationContext(), R.layout.item_home, null);
            TextView tvHomeName = (TextView) view.findViewById(R.id.tv_homeitem_name);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_homeitem_icon);
            tvHomeName.setText(managements[position]);
            ivIcon.setImageResource(icons[position]);

            return view;
        }

    }

}
