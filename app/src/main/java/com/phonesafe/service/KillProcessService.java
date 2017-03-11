package com.phonesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Time;
/**
 * ============================================================
 *
 *
 *
 * ============================================================
 **/
public class KillProcessService extends Service {

    private LockScreenReceiver receiver;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private class LockScreenReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取到进程管理器
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            //获取到手机上面所以正在运行的进程
            List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            for (RunningAppProcessInfo runningAppProcessInfo : appProcesses) {
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
            }


        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        receiver = new LockScreenReceiver();
        //锁屏的过滤器
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        //注册一个锁屏的广播
        registerReceiver(receiver, filter);

//		Timer timer = new Timer();
//
//		TimerTask task = new TimerTask() {
//
//			@Override
//			public void run() {
//				// 写我们的业务逻辑
//				System.out.println("我被调用了");
//			}
//		};
//		//进行定时调度
//		/**
//		 * 第一个参数  表示用那个类进行调度
//		 *
//		 * 第二个参数表示时间
//		 */
//		timer.schedule(task, 0,1000);

    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //当应用程序推出的时候。需要把广播反注册掉
        unregisterReceiver(receiver);
        //手动回收
        receiver = null;
    }

}
