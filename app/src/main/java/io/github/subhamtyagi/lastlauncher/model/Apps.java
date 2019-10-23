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


public class Apps {

    //final private CharSequence packageName;

    final private CharSequence activity;
    private CharSequence appName;

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    private TextView textView;
    private int color;
    private int size;

    private boolean freezeSize;
    private boolean hide;

    public boolean isFreezeSize() {
        return freezeSize;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
        textView.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    public int getSize() {
        return size;
    }

    /**
     * @param activity    executable activity path
     * @param appName     App name
     * @param tv          a text view corresponding to App
     * @param color       Text color
     * @param size        Text Size
     * @param hide        boolean to tell 'is app hide
     * @param freezeSize  is app size to freeze
     */
    public Apps(String activity, String appName, TextView tv, int color, int size, boolean hide, boolean freezeSize) {

       // this.packageName = packageName;
        this.activity=activity;
        this.appName = appName;
        this.textView = tv;
        this.color = color;
        this.size = size;
        this.freezeSize = freezeSize;
        this.hide = hide;

        textView.setText(appName);
        textView.setTag(activity);
        textView.setTextSize(size);
        if (color != -1)
            textView.setTextColor(color);

        textView.setVisibility(hide ? View.GONE : View.VISIBLE);
    }

    public CharSequence getActivityName() {
        return activity;
    }

    public CharSequence getAppName() {
        return appName;
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

    public void setAppName(CharSequence appName) {
        this.appName = appName;
        textView.setText(appName);
    }

    public void setSize(int size) {
        this.size = size;
        textView.setTextSize(size);
    }
}
