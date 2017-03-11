package com.phonesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import com.phonesafe.bean.TrafficInTaskInfo;
import com.phonesafe.engine.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stephen on 2017/3/11.
 */

public class TrafficParser {

    public static List<TrafficInTaskInfo> getAllTrafficList(Context context){
        // 获取到进程管理器
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        List<AndroidAppProcess> appProcesses = ProcessManager.getRunningAppProcesses();
        List<TrafficInTaskInfo> trafficsList = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        for (AndroidAppProcess process:appProcesses) {
            TrafficInTaskInfo trafficInfo = new TrafficInTaskInfo();
            trafficInfo.setPackageName(process.name);
            try {
                PackageInfo packageInfo = process.getPackageInfo(context,0);
                trafficInfo.setIcon(packageInfo.applicationInfo.loadIcon(pm));
                trafficInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
                int uid = process.uid;
                trafficInfo.setDownBytes(TrafficStats.getUidRxBytes(uid));
                trafficInfo.setUpBytes(TrafficStats.getUidTxBytes(uid));
                trafficsList.add(trafficInfo);

                Log.e("....","----------");
                Log.e("appname",trafficInfo.getAppName());
                Log.e("appdown",trafficInfo.getDownBytes()+"");
                Log.e("appup",trafficInfo.getUpBytes()+"");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return trafficsList;
    }
}
