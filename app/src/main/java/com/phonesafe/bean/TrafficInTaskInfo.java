package com.phonesafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by stephen on 2017/3/11.
 */

public class TrafficInTaskInfo {

    private Drawable icon;

    private String packageName;

    private String appName;

    private long downBytes;

    private long upBytes;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getDownBytes() {
        return downBytes;
    }

    public void setDownBytes(long inBytes) {
        this.downBytes = inBytes;
    }

    public long getUpBytes() {
        return upBytes;
    }

    public void setUpBytes(long outBytes) {
        this.upBytes = outBytes;
    }
}
