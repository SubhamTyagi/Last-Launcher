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

import io.github.subhamtyagi.lastlauncher.R;

public class DbUtils {

    public static final int NULL_TEXT_SIZE = -1;
    public static int NULL_TEXT_COLOR =-1;


    public static boolean isFirstStart(){
        return SpUtils.getInstance().getBoolean("sp_first_time_app_open",true);
    }


    public static void putAppOriginalName(String packageName, String value) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_original_name";
        SpUtils.getInstance().putString(packageName, value);
    }

    public static void putAppName(String packageName, String value) {
        packageName = packageName.replaceAll("\\.", "_") + "_app_name";
        SpUtils.getInstance().putString(packageName, value);
    }

    public static void putAppSize(String packageName, int size) {
        packageName = packageName.replaceAll("\\.", "_") + "_size";
        SpUtils.getInstance().putInt(packageName, size);

    }

    public static void putAppColor(String packageName, int color) {
        packageName = packageName.replaceAll("\\.", "_") + "_color";
        SpUtils.getInstance().putInt(packageName, color);
    }

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
        return SpUtils.getInstance().getInt(packageName, NULL_TEXT_SIZE);
    }

    public static int getAppColor(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_color";
        return SpUtils.getInstance().getInt(packageName, NULL_TEXT_COLOR);
    }

    public static void hideApp(String packageName, boolean value) {
        packageName = packageName.replaceAll("\\.", "_") + "_hide";
        SpUtils.getInstance().putBoolean(packageName, value);
    }


    public static void freezeAppSize(String packageName, boolean value) {
        packageName = packageName.replaceAll("\\.", "_") + "_freeze";
        SpUtils.getInstance().putBoolean(packageName, value);
    }

    public static boolean isAppFreezed(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_freeze";
        return SpUtils.getInstance().getBoolean(packageName, false);

    }



    public static boolean isAppHidden(String packageName) {
        packageName = packageName.replaceAll("\\.", "_") + "_hide";
        return SpUtils.getInstance().getBoolean(packageName, false);

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



    public static void setTheme(int id){
        SpUtils.getInstance().putInt(LAUNCHER_THEME,id);
    }

    public static int getTheme(){
       return SpUtils.getInstance().getInt(LAUNCHER_THEME, R.style.AppTheme);
    }

    public static void setFonts(String path){
        SpUtils.getInstance().putString(LAUNCHER_FONTS,path);
    }

    public static String getFonts(){
        return SpUtils.getInstance().getString(LAUNCHER_FONTS,null);
    }

    public static boolean isPermissionRequired(){
        return SpUtils.getInstance().getBoolean(READ_WRITE_PERMISSION,true);
    }


    public static void permissionRequired(boolean b){
         SpUtils.getInstance().putBoolean(READ_WRITE_PERMISSION,b);
    }

    public static boolean isRandomColor() {
        return SpUtils.getInstance().getBoolean(RANDOM_COLOR_FOR_APPS,false);
    }
    public static void randomColor(boolean b) {
        SpUtils.getInstance().putBoolean(RANDOM_COLOR_FOR_APPS,b);
    }
    public static void freezeSize(boolean b) {
        SpUtils.getInstance().putBoolean(LAUNCHER_FREEZE_SIZE,b);
    }

    public static boolean isSizeFreezed() {
        return SpUtils.getInstance().getBoolean(LAUNCHER_FREEZE_SIZE, false);

    }

    private static final String  RANDOM_COLOR_FOR_APPS="random_color_for_apps";
    private static final String READ_WRITE_PERMISSION="read_write_permission";
    private static final String LAUNCHER_FONTS="launcher_fonts";
    private static final String LAUNCHER_THEME="launcher_theme";
    private static final String LAUNCHER_FREEZE_SIZE="launcher_freeze_size";
}
