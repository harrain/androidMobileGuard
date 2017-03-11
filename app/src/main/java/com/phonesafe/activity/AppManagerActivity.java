package com.phonesafe.activity;

import android.app.Activity;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.phonesafe.R;
import com.phonesafe.bean.AppInfo;
import com.phonesafe.engine.AppInfos;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.list_view)
    private ListView listView;
    @ViewInject(R.id.tv_rom)
    private TextView tv_rom;
    @ViewInject(R.id.tv_sd)
    private TextView tv_sd;
    private List<AppInfo> appInfos;
    private List<AppInfo> userAppInfos;
    private List<AppInfo> systemAppInfos;
    @ViewInject(R.id.tv_app)
    private TextView tv_app;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;
    private UninstallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //分享
            case R.id.ll_share:

                Intent share_localIntent = new Intent("android.intent.action.SEND");
                share_localIntent.setType("text/plain");
                share_localIntent.putExtra("android.intent.extra.SUBJECT", "f分享");
                share_localIntent.putExtra("android.intent.extra.TEXT",
                        "Hi！推荐您使用软件：" + clickAppInfo.getApkName()+"下载地址:"+"https://play.google.com/store/apps/details?id="+clickAppInfo.getApkPackageName());
                this.startActivity(Intent.createChooser(share_localIntent, "分享"));
                popupWindowDismiss();

                break;

            //运行
            case R.id.ll_start:

                Intent start_localIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getApkPackageName());
                this.startActivity(start_localIntent);
                popupWindowDismiss();
                break;
            //卸载
            case R.id.ll_uninstall:

                Intent uninstall_localIntent = new Intent("android.intent.action.DELETE", Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(uninstall_localIntent);
                popupWindowDismiss();
                break;

            case R.id.ll_detail:
                Intent detail_intent = new Intent();
                detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                detail_intent.setData(Uri.parse("package:" + clickAppInfo.getApkPackageName()));
                startActivity(detail_intent);
                break;
        }

    }

    private class AppManagerAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {

            if (position == 0) {
                return null;
            } else if (position == userAppInfos.size() + 1) {
                return null;
            }
            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);

            } else {

                int location = userAppInfos.size() + 2;

                appInfo = systemAppInfos.get(position - location);
            }

            return appInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //如果当前的position等于0 表示应用程序
            if (position == 0) {

                TextView textView = new TextView(AppManagerActivity.this);

                textView.setTextColor(Color.WHITE);

                textView.setBackgroundColor(Color.GRAY);

                textView.setText("用户程序(" + userAppInfos.size() + ")");

                return textView;
                //表示系统程序
            } else if (position == userAppInfos.size() + 1) {


                TextView textView = new TextView(AppManagerActivity.this);


                textView.setTextColor(Color.WHITE);

                textView.setBackgroundColor(Color.GRAY);

                textView.setText("系统程序(" + systemAppInfos.size() + ")");

                return textView;

            }

            AppInfo appInfo;

            if (position < userAppInfos.size() + 1) {
                //把多出来的特殊的条目减掉
                appInfo = userAppInfos.get(position - 1);

            } else {

                int location = userAppInfos.size() + 2;

                appInfo = systemAppInfos.get(position - location);
            }


            View view = null;
            ViewHolder holder;
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();

            } else {


                view = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);

                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_apk_size = (TextView) view.findViewById(R.id.tv_apk_size);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);

                view.setTag(holder);
            }


            holder.iv_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_apk_size.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getApkSize()));

            holder.tv_name.setText(appInfo.getApkName());

            if (appInfo.isRom()) {
                holder.tv_location.setText("手机内存");
            } else {
                holder.tv_location.setText("外部存储");
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_apk_size;
        TextView tv_location;
        TextView tv_name;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AppManagerAdapter adapter = new AppManagerAdapter();
            listView.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //获取到所有安装到手机上面的应用程序
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);
                //appInfos拆成 用户程序的集合 + 系统程序的集合

                //用户程序的集合
                userAppInfos = new ArrayList<AppInfo>();
                //系统程序的集合
                systemAppInfos = new ArrayList<AppInfo>();

                for (AppInfo appInfo : appInfos) {
                    //用户程序
                    if (appInfo.isUserApp()) {
                        userAppInfos.add(appInfo);
                    } else {
                        systemAppInfos.add(appInfo);
                    }
                }


                handler.sendEmptyMessage(0);

            }
        }.start();

    }

    private void initUI() {

        setContentView(R.layout.activity_app_manager);
        ViewUtils.inject(this);
        //获取到rom内存的运行的剩余空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取到SD卡的剩余空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();

        System.out.println("内存可用:" + rom_freeSpace);

        System.out.println("sd卡可用:" + sd_freeSpace);

        //格式化大小
        tv_rom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));
        tv_sd.setText("sd卡可用" + Formatter.formatFileSize(this, sd_freeSpace));

    }

    @Override
    protected void onStart() {
        super.onStart();
        addBroadcast();
    }

    private void addBroadcast() {
        receiver = new UninstallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(receiver, intentFilter);

        //设置listview的滚动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见的条的位置
             * @param visibleItemCount 一页可以展示多少个条目
             * @param totalItemCount   总共的item的个数
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                popupWindowDismiss();

                if (userAppInfos != null && systemAppInfos != null) {
                    if (firstVisibleItem > (userAppInfos.size() + 1)) {
                        //系统应用程序
                        tv_app.setText("系统程序(" + systemAppInfos.size() + ")个");
                    } else {
                        //用户应用程序
                        tv_app.setText("用户程序(" + userAppInfos.size() + ")个");
                    }
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取到当前点击的item对象
                Object obj = listView.getItemAtPosition(position);

                if (obj != null && obj instanceof AppInfo) {

                    clickAppInfo = (AppInfo) obj;

                    View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);


                    LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);

                    LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

                    LinearLayout ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);

                    LinearLayout ll_detail = (LinearLayout) contentView.findViewById(R.id.ll_detail);

                    ll_uninstall.setOnClickListener(AppManagerActivity.this);

                    ll_share.setOnClickListener(AppManagerActivity.this);

                    ll_start.setOnClickListener(AppManagerActivity.this);

                    ll_detail.setOnClickListener(AppManagerActivity.this);


                    popupWindowDismiss();


                    // -2表示包裹内容
                    popupWindow = new PopupWindow(contentView, -2, -2);
                    //需要注意：使用PopupWindow 必须设置背景。不然没有动画
                    popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    int[] location = new int[2];
                    //获取view展示到窗体上面的位置
                    view.getLocationInWindow(location);

                    popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, location[1]);


                    ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                    sa.setDuration(3000);

                    contentView.startAnimation(sa);


                }
            }
        });
    }

    private class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("接收到卸载的广播");
        }
    }

    private void popupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        popupWindowDismiss();

        super.onDestroy();
    }
}
