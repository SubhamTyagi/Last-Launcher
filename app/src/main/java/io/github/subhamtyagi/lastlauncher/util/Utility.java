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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

public class Utility {

    public static int getRandomColor(String typeColor, Context context) {

        int returnColor = Color.BLACK;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }


    public static int getSize(long duration) {
        duration = ((duration / 1000L) / 60) / 60;
        if (duration > 10) return 50;
        if (duration > 9) return 48;
        if (duration > 8) return 45;
        if (duration > 7) return 42;
        if (duration > 6) return 38;
        if (duration > 5) return 35;
        if (duration > 4) return 33;
        if (duration > 3) return 29;
        if (duration > 2) return 26;
        if (duration > 1) return 23;

        return 20;
    }


    public static String getSizePrefs(String packageName){
        return packageName.replaceAll("\\.","_")+"_size";
    }


}
