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

package io.github.subhamtyagi.lastlauncher.util;

public class DbUtils {

    private static final int TEXT_SIZE = 30;
    public static int TEXT_COLOR;


    public static void putAppOriginalName(String packageName, String value) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_original_name";
        SpUtils.getInstance().putString(packageName, value);
    }

    public static void putAppName(String packageName, String value) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_name";
        SpUtils.getInstance().putString(packageName, value);
    }

    public static void putAppSize(String packageName,int size) {
        packageName = packageName.replaceAll("\\.", "_") + "_size";
        SpUtils.getInstance().putInt(packageName, size);

    }

    public static void putAppColor(String packageName,int color) {
        packageName = packageName.replaceAll("\\.", "_") + "_color";
        SpUtils.getInstance().putInt(packageName, color);
    }

    //-----------------------
    public static String getAppOriginalName(String packageName, String defaultValue) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_original_name";
        return SpUtils.getInstance().getString(packageName, defaultValue);
    }

    public static String getAppName(String packageName, String defaultValue) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_name";
        return SpUtils.getInstance().getString(packageName, defaultValue);
    }

    public static int getAppSize(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_size";
        return SpUtils.getInstance().getInt(packageName, TEXT_SIZE);
    }

    public static int getAppColor(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_color";
        return SpUtils.getInstance().getInt(packageName, TEXT_COLOR);
    }





    public static void removeColor(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_color";
        SpUtils.getInstance().remove(packageName);
    }

    public static void removeSize(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_size";
        SpUtils.getInstance().remove(packageName);
    }

    public static void removeAppName(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_name";
        SpUtils.getInstance().remove(packageName);
    }

}
