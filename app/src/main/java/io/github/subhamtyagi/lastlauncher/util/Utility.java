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

public class Utility {

    public static String getSizePrefs(String packageName){
        return packageName.replaceAll("\\.","_")+"_size";
    }

    public static String getColorPrefs(String packageName){
        return packageName.replaceAll("\\.","_")+"_color";
    }

    public static String getAppNamePrefs(String packageName){
        return packageName.replaceAll("\\.","_")+"_app_name";
    }



}
