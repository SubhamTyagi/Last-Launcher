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

import android.view.Gravity;

import java.util.Map;

import io.github.subhamtyagi.lastlauncher.R;

/**
 * This is the our database class
 * This is purely based on Shared prefs bcz -
 * 1. faster than Sqlite3 Db
 * 2. low memory usage than Sqlite3 db
 * 3. low Cpu usage than sqlite3 db
 * 4. easy to add new column when updating the db
 * 5. easy to backup and restore.
 * 6. built in android(no cpp code) and no other library need such as room
 * 7. no overhead when updating db version
 * <p>
 * <p>
 * NB: all column are conceptual
 */
public class DbUtils {

    public static final int NULL_TEXT_SIZE = -1;
    public final static int NULL_TEXT_COLOR = -1;
    private static final String RANDOM_COLOR_FOR_APPS = "random_color_for_apps";
    private static final String READ_WRITE_PERMISSION = "read_write_permission";
    private static final String LAUNCHER_FONTS = "launcher_fonts";
    private static final String LAUNCHER_THEME = "launcher_theme";
    private static final String LAUNCHER_FREEZE_SIZE = "launcher_freeze_size";
    private static final String APPS_COLOR_FROM_EXTERNAL_SOURCE = "external_app_color";

    //new addition
    private static final String FLOW_LAYOUT_ALIGNMENT = "flow_layout_alignment";
    private static final String MAX_APP_SIZE = "max_app_size";
    private static final String MIN_APP_SIZE = "max_app_size";

    public static boolean isFirstStart() {
        return SpUtils.getInstance().getBoolean("sp_first_time_app_open", true);
    }

    public static void putAppOriginalName(String activityName, String value) {
        activityName = activityName.replaceAll("\\.", "_") + "_app_original_name";
        SpUtils.getInstance().putString(activityName, value);
    }

    public static void putAppName(String activityName, String value) {
        activityName = activityName.replaceAll("\\.", "_") + "_app_name";
        SpUtils.getInstance().putString(activityName, value);
    }

    public static void putAppSize(String activityName, int size) {
        activityName = activityName.replaceAll("\\.", "_") + "_size";
        SpUtils.getInstance().putInt(activityName, size);

    }

    public static void putAppColor(String activityName, int color) {
        activityName = activityName.replaceAll("\\.", "_") + "_color";
        SpUtils.getInstance().putInt(activityName, color);
    }

    public static String getAppOriginalName(String activityName, String defaultValue) {
        activityName = activityName.replaceAll("\\.", "_") + "_app_original_name";
        return SpUtils.getInstance().getString(activityName, defaultValue);
    }

    public static String getAppName(String activityName, String defaultValue) {
        activityName = activityName.replaceAll("\\.", "_") + "_app_name";
        return SpUtils.getInstance().getString(activityName, defaultValue);
    }

    public static int getAppSize(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_size";
        return SpUtils.getInstance().getInt(activityName, NULL_TEXT_SIZE);
    }

    public static int getAppColor(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_color";
        return SpUtils.getInstance().getInt(activityName, NULL_TEXT_COLOR);
    }

    public static void hideApp(String activityName, boolean value) {
        activityName = activityName.replaceAll("\\.", "_") + "_hide";
        SpUtils.getInstance().putBoolean(activityName, value);
    }

    public static void freezeAppSize(String activityName, boolean value) {
        activityName = activityName.replaceAll("\\.", "_") + "_freeze";
        SpUtils.getInstance().putBoolean(activityName, value);
    }

    public static boolean isAppFreezed(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_freeze";
        return SpUtils.getInstance().getBoolean(activityName, false);

    }

    public static boolean isAppHidden(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_hide";
        return SpUtils.getInstance().getBoolean(activityName, false);

    }

    public static void removeColor(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_color";
        SpUtils.getInstance().remove(activityName);
    }

    public static void removeSize(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_size";
        SpUtils.getInstance().remove(activityName);
    }

    public static void removeAppName(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "_app_name";
        SpUtils.getInstance().remove(activityName);
    }

    public static int getTheme() {
        return SpUtils.getInstance().getInt(LAUNCHER_THEME, R.style.AppTheme);
    }

    public static void setTheme(int id) {
        SpUtils.getInstance().putInt(LAUNCHER_THEME, id);
    }

    public static String getFonts() {
        return SpUtils.getInstance().getString(LAUNCHER_FONTS, null);
    }

    public static void setFonts(String path) {
        SpUtils.getInstance().putString(LAUNCHER_FONTS, path);
    }


    public static boolean isPermissionRequired() {
        return SpUtils.getInstance().getBoolean(READ_WRITE_PERMISSION, true);
    }

    public static void permissionRequired(boolean b) {
        SpUtils.getInstance().putBoolean(READ_WRITE_PERMISSION, b);
    }

    public static boolean isRandomColor() {
        return SpUtils.getInstance().getBoolean(RANDOM_COLOR_FOR_APPS, false);
    }

    public static void randomColor(boolean b) {
        SpUtils.getInstance().putBoolean(RANDOM_COLOR_FOR_APPS, b);
    }

    public static void freezeSize(boolean b) {
        SpUtils.getInstance().putBoolean(LAUNCHER_FREEZE_SIZE, b);
    }

    public static boolean isSizeFreezed() {
        return SpUtils.getInstance().getBoolean(LAUNCHER_FREEZE_SIZE, false);

    }

    public static String[] getAllHiddenApps() {
        Map<String, ?> entries = SpUtils.getInstance().getAll();
        for (Map.Entry<String, ?> entry : entries.entrySet()) {
            if (entry.getKey().contains("hide")) {

            }
        }

        return null;
    }

    public static boolean isExternalSourceColor() {
        return SpUtils.getInstance().getBoolean(APPS_COLOR_FROM_EXTERNAL_SOURCE, false);
    }

    public static void externalSourceColor(boolean b) {
        SpUtils.getInstance().putBoolean(APPS_COLOR_FROM_EXTERNAL_SOURCE, b);
    }

    public static int getAppColorExternalSource(String activityName) {
        activityName = activityName.replaceAll("\\.", "_") + "external_color";
        return SpUtils.getInstance().getInt(activityName, NULL_TEXT_COLOR);
    }

    public static void putAppColorExternalSource(String activityName, int color) {
        activityName = activityName.replaceAll("\\.", "_") + "external_color";
        SpUtils.getInstance().putInt(activityName, color);
    }

    public static int getFlowLayoutAlignment() {
        return SpUtils.getInstance().getInt(FLOW_LAYOUT_ALIGNMENT, Gravity.CENTER | Gravity.CENTER_VERTICAL);
    }

    public static void setFlowLayoutAlignment(int gravity) {
        SpUtils.getInstance().putInt(FLOW_LAYOUT_ALIGNMENT, gravity);
    }

    public static int getMaxAppSize() {
        return SpUtils.getInstance().getInt(MAX_APP_SIZE, 80);
    }

    public static void setMaxAppSize(int size) {
        SpUtils.getInstance().putInt(MAX_APP_SIZE, size);
    }

    public static int getMinAppSize() {
        return SpUtils.getInstance().getInt(MIN_APP_SIZE, 14);
    }

    public static void setMinAppSize(int size) {
        SpUtils.getInstance().putInt(MIN_APP_SIZE, size);
    }
}
