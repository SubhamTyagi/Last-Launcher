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


public class Apps {

    final private CharSequence activity;
    private CharSequence appName;
    private TextView textView;
    private int color;
    private int size;
    private boolean freezeSize;
    private boolean hide;


    /**
     * @param activity   executable activity path
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
