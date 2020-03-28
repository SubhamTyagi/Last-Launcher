/*
 * Last Launcher
 * Copyright (C) 2019 Shubham Tyagi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.subhamtyagi.lastlauncher.model;

import android.view.View;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.util.DbUtils;

// a model class that hold everything related to an app
public class Apps {

    // app activity name format package.name/package.name.ClassName
    // for eg. com.example.appname/com.example.appname.MainActivity
    // for eg  io.github.subhamtyagi.lastlauncher/io.github.subhamtyagi.lastlauncher/LauncherActivity
    final private CharSequence activity;
    // app name to shown on screen
    private CharSequence appName;
    // a text view or a subclass
    private TextView textView;
    // app color
    private int color;
    // app size
    private int size;
    // is app size freezed
    private boolean freezeSize;
    // is app hidden from home screen
    private boolean hide;


    /**
     * @param activity    activity path
     * @param appName    App name
     * @param tv         a text view corresponding to App
     * @param color      Text color
     * @param size       Text Size
     * @param hide       boolean to tell 'is app hide
     * @param freezeSize is app size to freeze
     */
    public Apps(String activity, String appName, TextView tv, int color, int size, boolean hide, boolean freezeSize) {

        this.activity = activity;
        this.appName = appName;
        this.textView = tv;
        this.color = color;
        this.size = size;

        textView.setText(appName);
        textView.setTag(activity);
        textView.setTextSize(size);

        // if color is not -1 then set this to color
        // else not set the color default theme text color will handle the color
        if (color != DbUtils.NULL_TEXT_COLOR)
            textView.setTextColor(color);

        setHide(hide);
        setFreeze(freezeSize);

    }

    public boolean isFreezeSize() {
        return freezeSize;
    }

    public boolean isHidden() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
        textView.setVisibility(hide ? View.GONE : View.VISIBLE);
        DbUtils.hideApp(activity.toString(), hide);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        textView.setTextSize(size);
    }

    public void setFreeze(boolean freezeSize) {
        this.freezeSize = freezeSize;
        DbUtils.freezeAppSize(activity.toString(), freezeSize);
    }

    public CharSequence getActivityName() {
        return activity;
    }

    public CharSequence getAppName() {
        return appName;
    }

    public void setAppName(CharSequence appName) {
        this.appName = appName;
        textView.setText(appName);
    }

    public TextView getTextView() {
        return textView;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        textView.setTextColor(color);
    }


}
