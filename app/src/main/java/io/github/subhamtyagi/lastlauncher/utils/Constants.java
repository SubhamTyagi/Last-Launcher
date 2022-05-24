/*
 * Last Launcher
 * Copyright (C) 2019,2020 Shubham Tyagi
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

package io.github.subhamtyagi.lastlauncher.utils;

public class Constants {

    public static final int COLOR_SNIFFER_REQUEST = 154;
    public final static String DEFAULT_COLOR_FOR_APPS = "default_color_for_apps";
    //various sorting constant
    //why constant? Why not enums for this ?
    // may be lack from Shared Preference DB
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_SIZE = 2;
    public static final int SORT_BY_COLOR = 3;
    public static final int SORT_BY_OPENING_COUNTS = 4;
    public static final int SORT_BY_CUSTOM = 5;
    public static final int SORT_BY_UPDATE_TIME = 6;
    public static final int SORT_BY_RECENT_OPEN = 7;

    public static final int RESTORE_REQUEST = 125;
    public static final int FONTS_REQUEST = 126;
    public static final int BACKUP_REQUEST = 128;
    public static final int DEFAULT_MAX_TEXT_SIZE = 10;
    public static final int DEFAULT_MIN_TEXT_SIZE = -10;
    public static final int MAX_PADDING_LEFT = 99;
    public static final int MAX_PADDING_RIGHT = 99;
    public static final int MAX_PADDING_TOP = 999;
    public static final int MAX_PADDING_BOTTOM = 99;
    public static final int MAX_PADDING_INTERVAL = 90;
    public static final int MIN_PADDING = 0;
    //TODO: Dynamic height
    public static int dynamicHeight = 20;
    public static final int DEFAULT_TEXT_SIZE_NORMAL_APPS = dynamicHeight;
    public static final int DEFAULT_TEXT_SIZE_OFTEN_APPS = dynamicHeight * 9 / 5;
    public static final int MAX_TEXT_SIZE_FOR_APPS = 90;
    public static final int MIN_TEXT_SIZE_FOR_APPS = 14;


}
