package com.phonesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.phonesafe.R;
import com.phonesafe.activity.TaskManagerSettingActivity;
import com.phonesafe.bean.TaskInfo;
import com.phonesafe.engine.TaskInfoParser;
import com.phonesafe.utils.SharedPreferencesUtils;
import com.phonesafe.utils.SystemInfoUtils;
import com.phonesafe.utils.UIUtils;

public class TaskManagerActivity extends Activity {
	@ViewInject(R.id.tv_task_process_count)
	private TextView tv_task_process_count;
	@ViewInject(R.id.tv_task_memory)
	private TextView tv_task_memory;
	@ViewInject(R.id.list_view)
	private ListView list_view;
	private long totalMem;
	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemAppInfos;
	private TaskManagerAdapter adapter;
	private int processCount;
	private long availMem;
//	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//		sp = getSharedPreferences("config", 0);

		initUI();
		initData();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}

	private class TaskManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			/**
			 * 判断当前用户是否需要展示系统进程
			 * 如果需要就全部展示
			 * 如果不需要就展示用户进程
			 */
			boolean result = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, "is_show_system", false);
			if(result){
				return userTaskInfos.size() + 1 + systemAppInfos.size() + 1;
			}else{
				return userTaskInfos.size() + 1;
			}


		}

		@Override
		public Object getItem(int position) {

			if (position == 0) {
				return null;
			} else if (position == userTaskInfos.size() + 1) {
				return null;
			}

			TaskInfo taskInfo;

			if (position < (userTaskInfos.size() + 1)) {
				// 用户程序
				taskInfo = userTaskInfos.get(position - 1);// 多了一个textview的标签 ，
				// 位置需要-1
			} else {
				// 系统程序
				int location = position - 1 - userTaskInfos.size() - 1;
				taskInfo = systemAppInfos.get(location);
			}
			return taskInfo;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				// 第0个位置显示的应该是 用户程序的个数的标签。
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户程序：" + userTaskInfos.size() + "个");
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统程序：" + systemAppInfos.size() + "个");
				return tv;
			}
			ViewHolder holder;
			View view;
			if (convertView != null && convertView instanceof LinearLayout) {
				view = convertView;

				holder = (ViewHolder) view.getTag();

			} else {
				view = View.inflate(TaskManagerActivity.this,
						R.layout.item_task_manager, null);

				holder = new ViewHolder();

				holder.iv_app_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);

				holder.tv_app_name = (TextView) view
						.findViewById(R.id.tv_app_name);

				holder.tv_app_memory_size = (TextView) view
						.findViewById(R.id.tv_app_memory_size);

				holder.tv_app_status = (CheckBox) view
						.findViewById(R.id.tv_app_status);

				view.setTag(holder);
			}

			TaskInfo taskInfo;

			if (position < (userTaskInfos.size() + 1)) {
				// 用户程序
				taskInfo = userTaskInfos.get(position - 1);// 多了一个textview的标签 ，
				// 位置需要-1
			} else {
				// 系统程序
				int location = position - 1 - userTaskInfos.size() - 1;
				taskInfo = systemAppInfos.get(location);
			}
			// 这个是设置图片控件的大小
			// holder.iv_app_icon.setBackgroundDrawable(d)
			// 设置图片本身的大小
			holder.iv_app_icon.setImageDrawable(taskInfo.getIcon());

			holder.tv_app_name.setText(taskInfo.getAppName());

			holder.tv_app_memory_size.setText("内存占用:"
					+ Formatter.formatFileSize(TaskManagerActivity.this,
					taskInfo.getMemorySize()));

			if (taskInfo.isChecked()) {
				holder.tv_app_status.setChecked(true);
			} else {
				holder.tv_app_status.setChecked(false);
			}
			//判断当前展示的item是否是自己的程序。如果是。就把程序给隐藏
			if(taskInfo.getPackageName().equals(getPackageName())){
				//隐藏
				holder.tv_app_status.setVisibility(View.INVISIBLE);
			}else{
				//显示
				holder.tv_app_status.setVisibility(View.VISIBLE);
			}

			return view;
		}

	}

	static class ViewHolder {
		ImageView iv_app_icon;
		TextView tv_app_name;
		TextView tv_app_memory_size;
		CheckBox tv_app_status;
	}

	private void initData() {
		new Thread() {

			public void run() {
				taskInfos = TaskInfoParser
						.getTaskInfos(TaskManagerActivity.this);

				userTaskInfos = new ArrayList<TaskInfo>();

				systemAppInfos = new ArrayList<TaskInfo>();

				for (TaskInfo taskInfo : taskInfos) {

					if (taskInfo.isUserApp()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemAppInfos.add(taskInfo);
					}

				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						adapter = new TaskManagerAdapter();
						list_view.setAdapter(adapter);
					}
				});

			};
		}.start();

	}

	/**
	 * 区别：
	 *
	 * ActivityManager 活动管理器(任务管理器)
	 *
	 * packageManager 包管理器
	 */

	private void initUI() {
		setContentView(R.layout.activity_task_manager);
		ViewUtils.inject(this);

		processCount = SystemInfoUtils.getProcessCount(this);

		tv_task_process_count.setText("进程:" + processCount + "个");

		availMem = SystemInfoUtils.getAvailMem(this);

		totalMem = SystemInfoUtils.getTotalMem(this);

		tv_task_memory.setText("剩余/总内存:"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// 得到当前点击listview的对象
				Object object = list_view.getItemAtPosition(position);


				if (object != null && object instanceof TaskInfo) {

					TaskInfo taskInfo = (TaskInfo) object;

					ViewHolder holder = (ViewHolder) view.getTag();

					if(taskInfo.getPackageName().equals(getPackageName())){
						return;
					}

					// 判断当前的item是否被勾选上
					/**
					 * 如果被勾选上了。那么就改成没有勾选。 如果没有勾选。就改成已经勾选
					 */
					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
						holder.tv_app_status.setChecked(false);
					} else {
						taskInfo.setChecked(true);
						holder.tv_app_status.setChecked(true);
					}

				}

			}

		});
	}

	/**
	 * 全选
	 *
	 * @param view
	 */

	public void selectAll(View view) {

		for (TaskInfo taskInfo : userTaskInfos) {

			// 判断当前的用户程序是不是自己的程序。如果是自己的程序。那么就把文本框隐藏

			if (taskInfo.getPackageName().equals(getPackageName())) {
				continue;
			}

			taskInfo.setChecked(true);
		}

		for (TaskInfo taskInfo : systemAppInfos) {
			taskInfo.setChecked(true);
		}
		// 一定要注意。一旦数据发生改变一定要刷新
		adapter.notifyDataSetChanged();

	}

	/**
	 * 反选
	 *
	 * @param view
	 */
	public void selectOppsite(View view) {
		for (TaskInfo taskInfo : userTaskInfos) {
			// 判断当前的用户程序是不是自己的程序。如果是自己的程序。那么就把文本框隐藏

			if (taskInfo.getPackageName().equals(getPackageName())) {
				continue;
			}
			taskInfo.setChecked(!taskInfo.isChecked());
		}
		for (TaskInfo taskInfo : systemAppInfos) {
			taskInfo.setChecked(!taskInfo.isChecked());
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 清理进程
	 *
	 * @param view
	 */
	public void killProcess(View view) {

		// 想杀死进程。首先必须得到进程管理器

		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		// 清理进程的集合
		List<TaskInfo> killLists = new ArrayList<TaskInfo>();

		// 清理的总共的进程个数
		int totalCount = 0;
		// 清理的进程的大小
		int killMem = 0;
		for (TaskInfo taskInfo : userTaskInfos) {

			if (taskInfo.isChecked()) {
				killLists.add(taskInfo);
				// userTaskInfos.remove(taskInfo);
				totalCount++;
				killMem += taskInfo.getMemorySize();

			}
		}

		for (TaskInfo taskInfo : systemAppInfos) {

			if (taskInfo.isChecked()) {
				killLists.add(taskInfo);
				// systemAppInfos.remove(taskInfo);
				totalCount++;
				killMem += taskInfo.getMemorySize();

			}
		}
		/**
		 * 注意： 当集合在迭代的时候。不能修改集合的大小
		 */
		for (TaskInfo taskInfo : killLists) {
			// 判断是否是用户app
			if (taskInfo.isUserApp()) {
				userTaskInfos.remove(taskInfo);
				// 杀死进程 参数表示包名
				activityManager.killBackgroundProcesses(taskInfo
						.getPackageName());
			} else {
				systemAppInfos.remove(taskInfo);
				// 杀死进程 参数表示包名
				activityManager.killBackgroundProcesses(taskInfo
						.getPackageName());
			}
		}

		UIUtils.showToast(
				TaskManagerActivity.this,
				"共清理"
						+ totalCount
						+ "个进程,释放"
						+ Formatter.formatFileSize(TaskManagerActivity.this,
						killMem) + "内存");
		//processCount 表示总共有多少个进程
		//totalCount 当前清理了多少个进程
		processCount -= totalCount;
		tv_task_process_count.setText("进程:"+ processCount +"个");
		//
		tv_task_memory.setText("剩余/总内存:"
				+ Formatter.formatFileSize(TaskManagerActivity.this, availMem + killMem)
				+ "/"
				+ Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

		// 刷新界面
		adapter.notifyDataSetChanged();

	}
	/**
	 * 打开设置界面
	 * @param view
	 */
	public void openSetting(View view){
		Intent intent = new Intent(this,TaskManagerSettingActivity.class);
		startActivity(intent);
	}
}
