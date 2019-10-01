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

import android.widget.TextView;


public class Apps {

    final private int id;

    final private CharSequence packageName;
    private CharSequence appName;
    final private TextView textView;
    private int color;
    private int size;

    /**
     * @param id          a auto increment id
     * @param packageName apps package name
     * @param appName     App name
     * @param tv          a text view corresponding to App
     * @param color       Text color
     * @param size        Text Size
     */
    public Apps(int id, String packageName, String appName, TextView tv, int color, int size) {
        this.id = id;
        this.packageName = packageName;
        this.appName = appName;
        this.textView = tv;
        this.color = color;
        this.size = size;
    }

    public CharSequence getPackageName() {
        return packageName;
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
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
