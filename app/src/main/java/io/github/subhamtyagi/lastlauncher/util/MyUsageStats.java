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

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Map;


//Not in use
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MyUsageStats {
    private static MyUsageStats myUsageStats;
    private UsageStatsManager usageStatsManager;

    public static MyUsageStats getInstance(Context context) {
        if (myUsageStats == null) {
            myUsageStats = new MyUsageStats(context);
        }
        return myUsageStats;
    }

    private MyUsageStats(Context context) {
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public Map<String, UsageStats> getUsageStats() {
        long now = System.currentTimeMillis();
        long lastSevenDays = now - 7 * 24 * 60 * 60 * 1000L;
        return usageStatsManager.queryAndAggregateUsageStats(lastSevenDays, now);
        //return usageStatsManager.queryAndAggregateUsageStats(lastSevenDays, now);
    }

    public static boolean isStatAccessPermissionSet(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName);
                return appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

}
