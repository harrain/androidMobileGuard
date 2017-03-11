package com.phonesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phonesafe.R;
import com.phonesafe.utils.MD5Utils;

/**
 * 主页面
 */
public class HomeActivity extends Activity {

    private GridView gvHome;

    private String[] mItems = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理",
            "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心"};

    private int[] mPics = new int[]{R.mipmap.home_safe,
            R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
            R.mipmap.home_taskmanager, R.mipmap.home_netmanager,
            R.mipmap.home_trojan, R.mipmap.home_sysoptimize,
            R.mipmap.home_tools, R.mipmap.home_settings};

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mPref = getSharedPreferences("config", MODE_PRIVATE);

        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());

        // 设置监听
        gvHome.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        // 手机防盗
                        showPasswordDialog();
                        break;
                    case 1:
                        // 通讯卫士
                        startActivity(new Intent(HomeActivity.this,
                                CallSafeActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomeActivity.this,
                                AppManagerActivity.class));
                        break;
                    case 3://进程管理
                        Intent intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        Intent intent1 = new Intent(HomeActivity.this, TrafficStatisticsActivity.class);
                        startActivity(intent1);
                        break;
                    case 5:
                        startActivity(new Intent(HomeActivity.this,
                                AntivirusActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(HomeActivity.this,
                                CleanCacheActivity.class));
                        break;
                    case 7:
                        // 高级工具
                        startActivity(new Intent(HomeActivity.this,
                                AToolsActivity.class));
                        break;
                    case 8:
                        // 设置中心
                        startActivity(new Intent(HomeActivity.this,
                                SettingActivity.class));
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
     * 显示密码弹窗
     */
    protected void showPasswordDialog() {
        // 判断是否设置密码
        String savedPassword = mPref.getString("password", null);
        if (!TextUtils.isEmpty(savedPassword)) {
            // 输入密码弹窗
            showPasswordInputDialog();
        } else {
            // 如果没有设置过, 弹出设置密码的弹窗
            showPasswordSetDailog();
        }
    }

    /**
     * 输入密码弹窗
     */
    private void showPasswordInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_input_password, null);
        //dialog.setView(view);// 将自定义的布局文件设置给dialog
        dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    String savedPassword = mPref.getString("password", null);

                    if (MD5Utils.encode(password).equals(savedPassword)) {
                        // Toast.makeText(HomeActivity.this, "登录成功!",
                        // Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        // 跳转到手机防盗页
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();

        try {

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width= 700;
            //params.height=700;
            params.alpha = 1f;
            dialog.getWindow().setAttributes(params);

        }catch (NullPointerException e){
            Log.wtf("alertDialogwindow",e);
        }
    }

    /**
     * 设置密码的弹窗
     */
    private void showPasswordSetDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dailog_set_password, null);
        dialog.setView(view);// 将自定义的布局文件设置给dialog
        //dialog.setView(view, 0, 0, 0, 0);// 设置边距为0,保证在2.x的版本上运行没问题

        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view
                .findViewById(R.id.et_password_confirm);

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        btnOK.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                // password!=null && !password.equals("")
                if (!TextUtils.isEmpty(password) && !passwordConfirm.isEmpty()) {
                    if (password.equals(passwordConfirm)) {
                        // Toast.makeText(HomeActivity.this, "登录成功!",
                        // Toast.LENGTH_SHORT).show();

                        // 将密码保存起来
                        mPref.edit()
                                .putString("password",
                                        MD5Utils.encode(password)).commit();

                        dialog.dismiss();

                        // 跳转到手机防盗页
                        startActivity(new Intent(HomeActivity.this,
                                LostFindActivity.class));
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "输入框内容不能为空!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();// 隐藏dialog
            }
        });

        dialog.show();

        try {

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width= 700;
            //params.height=700;
            params.alpha = 1f;
            dialog.getWindow().setAttributes(params);

        }catch (NullPointerException e){
            Log.wtf("alertDialogwindow",e);
        }
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(HomeActivity.this,
                        R.layout.home_list_item, null);
                holder = new ViewHolder();
                holder.ivItem = (ImageView) convertView.findViewById(R.id.iv_item);
                holder.tvItem = (TextView) convertView.findViewById(R.id.tv_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvItem.setText(mItems[position]);
            holder.ivItem.setImageResource(mPics[position]);
            return convertView;
        }

        class ViewHolder {
            ImageView ivItem;
            TextView tvItem;
        }

    }
}
