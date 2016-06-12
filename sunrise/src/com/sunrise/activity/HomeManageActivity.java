package com.sunrise.activity;

import java.util.ArrayList;
import java.util.List;

import com.sunrise.R;
import com.sunrise.adapter.GroupAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class HomeManageActivity extends Activity {
    private GridView gvHome;
    private static final String[] managements = { "设备管理", "任务管理", "巡视管理", "报警管理", "查岗管理", "运行分析", "交接班管理", "下载中心", "上载中心", "NFC标签管理" };
    private static int[] icons = { R.drawable.equipment_manager, R.drawable.task, R.drawable.tour, R.drawable.warn, R.drawable.inspect,
            R.drawable.run, R.drawable.shift, R.drawable.download_center, R.drawable.upload_center, R.drawable.nfc };

    private PopupWindow popupWindow;
    private ListView lvPopup;
    private View view0;
    private ImageView iv_popup;
    private List<String> groups;
    private List<Integer> images;

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
                    Intent intent5 = new Intent(HomeManageActivity.this, com.sunrise.activity.CurveActivity.class);
                    startActivity(intent5);
                    break;
                case 7:
                    Intent intent7 = new Intent(HomeManageActivity.this, DownloadCenterActivity.class);
                    startActivity(intent7);
                    break;
                case 8:
                    Intent intent8 = new Intent(HomeManageActivity.this, UploadCenterActivity.class);
                    startActivity(intent8);
                    break;
                default:
                    break;
                }

            }
        });

        iv_popup = (ImageView) findViewById(R.id.iv_popupWindow);
        iv_popup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });



    }

    protected void showPopupWindow(View parent) {
        if (popupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view0 = layoutInflater.inflate(R.layout.lv_popup_list, null);
            lvPopup = (ListView) view0.findViewById(R.id.lv_popup);

            groups = new ArrayList<String>();
            groups.add("关于系统");
            groups.add("退出登录");

            images=new ArrayList<Integer>();
            images.add(R.drawable.admin);
            images.add(R.drawable.exit);

            GroupAdapter groupAdapter = new GroupAdapter(this, groups, images);
            lvPopup.setAdapter(groupAdapter);

            // 创建一个PopupWindow对象
            popupWindow = new PopupWindow(view0, LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int xPos = windowManager.getDefaultDisplay().getWidth() / 2 - popupWindow.getWidth() / 2 - 130;
        popupWindow.showAsDropDown(parent, xPos, 30);

        lvPopup.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                case 0:
                    startActivity(new Intent(HomeManageActivity.this, AboutSystemActivity.class));

                    break;
                case 1:
                    AlertDialog.Builder builder=new AlertDialog.Builder(HomeManageActivity.this);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setTitle(R.string.quit);
                    builder.setMessage("确定要退出登录吗?");
                    builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(HomeManageActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();
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
