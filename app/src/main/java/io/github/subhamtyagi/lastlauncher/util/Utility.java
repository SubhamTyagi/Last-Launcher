package io.github.subhamtyagi.lastlauncher.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

public class Utility {

public static int getRandomColor(String typeColor, Context context) {

    int returnColor = Color.BLACK;
    int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

    if (arrayId != 0)
    {
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

}
