package io.github.subhamtyagi.lastlauncher.model;

import android.graphics.Color;


public class Apps {

    //in future may be useful for search etc...

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    final private CharSequence packageName;


    final private CharSequence appName;

    private Color appTextColor;
    private int appTextSize;

    public Apps(int id, String packageName,  String appName) {
        this.id=id;
        this.packageName = packageName;
        this.appName = appName;
        //currently color and size not consider for MVP
    }

    public CharSequence getPackageName() {
        return packageName;
    }


    public CharSequence getAppName() {
        return appName;
    }



    public Color getAppTextColor() {
        return appTextColor;
    }

    public void setAppTextColor(Color appTextColor) {
        this.appTextColor = appTextColor;
    }

    public int getAppTextSize() {
        return appTextSize;
    }

    public void setAppTextSize(int appTextSize) {
        this.appTextSize = appTextSize;
    }
}
