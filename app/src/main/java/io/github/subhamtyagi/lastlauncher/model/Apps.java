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

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.util.DbUtils;


public class Apps implements Parcelable {

    final private CharSequence activity;
    private CharSequence appName;
    private TextView textView;
    private int color;
    private int size;
    private boolean freezeSize;
    private boolean hide;

    private Apps(Parcel in) {
        activity = in.readString();
        appName = in.readString();
        freezeSize = in.readByte() != 0;
        hide = in.readByte() != 0;
    }

    public static final Creator<Apps> CREATOR = new Creator<Apps>() {
        @Override
        public Apps createFromParcel(Parcel in) {
            return new Apps(in);
        }

        @Override
        public Apps[] newArray(int size) {
            return new Apps[size];
        }
    };

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
        this.freezeSize = freezeSize;
        this.hide = hide;
        textView.setText(appName);
        textView.setTag(activity);
        textView.setTextSize(size);
        if (color != DbUtils.NULL_TEXT_COLOR)
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

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(activity.toString());
        parcel.writeString(appName.toString());
        parcel.writeByte((byte) (freezeSize ? 1 : 0));
        parcel.writeByte((byte) (hide ? 1 : 0));
    }
}
