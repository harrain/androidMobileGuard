package com.phonesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug.MemoryInfo;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.util.Log;

import com.phonesafe.R;
import com.phonesafe.bean.TaskInfo;
import com.phonesafe.engine.models.AndroidAppProcess;


public class TaskInfoParser {

	public static List<TaskInfo> getTaskInfos(Context context) {

		PackageManager packageManager = context.getPackageManager();

		List<TaskInfo> TaskInfos = new ArrayList<TaskInfo>();

		// 获取到进程管理器
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		// 获取到手机上面所有运行的进程
		/*List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();*/
		List<AndroidAppProcess> appProcesses = ProcessManager.getRunningAppProcesses();
		Log.e("TaskInfoParser","RunningAppProcessInfo:"+appProcesses.size()+"");

		for (AndroidAppProcess runningAppProcessInfo : appProcesses) {

			TaskInfo taskInfo = new TaskInfo();

			// 获取到进程的名字
			String processName = runningAppProcessInfo.name;

			taskInfo.setPackageName(processName);

			try {
				// 获取到内存基本信息
				/**
				 * 这个里面一共只有一个数据
				 */
				MemoryInfo[] memoryInfo = activityManager
						.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
				// Dirty弄脏
				// 获取到总共弄脏多少内存(当前应用程序占用多少内存)
				int totalPrivateDirty = memoryInfo[0].getTotalPrivateDirty() * 1024;


//				System.out.println("==========="+totalPrivateDirty);

				taskInfo.setMemorySize(totalPrivateDirty);

				PackageInfo packageInfo = packageManager.getPackageInfo(
						processName, 0);

				// /获取到图片
				Drawable icon = packageInfo.applicationInfo
						.loadIcon(packageManager);

				taskInfo.setIcon(icon);
				// 获取到应用的名字
				String appName = packageInfo.applicationInfo.loadLabel(
						packageManager).toString();

				taskInfo.setAppName(appName);

				System.out.println("-------------------");
				System.out.println("processName="+processName);
				System.out.println("appName="+appName);
				//获取到当前应用程序的标记
				//packageInfo.applicationInfo.flags 我们写的答案
				//ApplicationInfo.FLAG_SYSTEM表示老师的该卷器
				int flags = packageInfo.applicationInfo.flags;
				//ApplicationInfo.FLAG_SYSTEM 表示系统应用程序
				if((flags & ApplicationInfo.FLAG_SYSTEM) != 0 ){
					//系统应用
					taskInfo.setUserApp(false);
				}else{
//					/用户应用
					taskInfo.setUserApp(true);

				}




			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// 系统核心库里面有些系统没有图标。必须给一个默认的图标

				taskInfo.setAppName(processName);
				taskInfo.setIcon(context.getResources().getDrawable(
						R.mipmap.ic_launcher));
			}

			TaskInfos.add(taskInfo);
		}

		return TaskInfos;
	}



}
