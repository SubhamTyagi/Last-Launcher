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

package io.github.subhamtyagi.lastlauncher;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.util.Utility;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {
    // private static final String TAG = "LauncherActivity";

    //private static final int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;

    private ArrayList<Apps> appsList;
    private static final int TEXT_SIZE = 30;
    private static int TEXT_COLOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        SpUtils.getInstance().init(this);

        TEXT_COLOR = getResources().getColor(R.color.default_apps_colors);

        loadApps();
        // refreshAllApps();
        registerForReceiver();
        SpUtils.getInstance().init(this).putBoolean(getString(R.string.sp_first_time_app_open), false);
    }

    private void refreshAppSize(String packageName) {
        int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(packageName), TEXT_SIZE) + 1;
        SpUtils.getInstance().putInt(Utility.getSizePrefs(packageName), size);
        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                apps.getTextView().setTextSize(size);
                apps.setSize(size);
            }
        }
    }


    void refreshApps(String packageName) {
        int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(packageName), TEXT_SIZE) + 1;
        int color = SpUtils.getInstance().getInt(Utility.getColorPrefs(packageName), TEXT_COLOR);
        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                apps.getTextView().setTextSize(size);
                apps.getTextView().setTextColor(color);
                apps.setSize(size);
                apps.setColor(color);
            }
        }
    }

    //this must be done in background
    private void loadApps() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString()
                );
            }
        });
        /////////////////////////////////////////////
        FlowLayout homeLayout = findViewById(R.id.home_layout);

        homeLayout.setOnLongClickListener(this::onLongClick);
        homeLayout.setOnClickListener(this::onClick);


        homeLayout.removeAllViews();

        int appsCount = activities.size();
        int id = 0;
        String packageName, appName;
        TextView textView;

        appsList = new ArrayList<>(appsCount);
        int color, textSize;

        for (ResolveInfo resolveInfo : activities) {
            packageName = resolveInfo.activityInfo.packageName;
            appName = resolveInfo.loadLabel(pm).toString();

            //TODO: before commit / take screen shot
            //if (appName.equalsIgnoreCase("KD campus")||appName.equalsIgnoreCase("kanyadaan")||appName.equalsIgnoreCase("getApps")||appName.equalsIgnoreCase("feedback")||appName.equalsIgnoreCase("gradeup")||appName.equalsIgnoreCase("mi remote")||appName.equalsIgnoreCase("pnb one")||appName.equalsIgnoreCase("play store")||appName.equalsIgnoreCase("drive")||appName.equalsIgnoreCase("duo"))
            //continue;

            //set text color size,weight
            textView = new TextView(this);
            textView.setText(appName);
            textView.setTag(packageName);//tag for identification
            textView.setOnClickListener(this::onClick);
            textView.setOnLongClickListener(this::onLongClick);

            //TODO: move values to dimens
            textView.setPadding(10, -5, 0, -2);

            textSize = SpUtils.getInstance().init(this).getInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
            color = SpUtils.getInstance().getInt(Utility.getColorPrefs(packageName), TEXT_COLOR);
            textView.setTextSize(textSize);
            textView.setTextColor(color);

            appsList.add(
                    new Apps(++id,
                            packageName,
                            appName,
                            textView,
                            color,
                            textSize
                    )
            );
            homeLayout.addView(textView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

    }


    @Override
    public boolean onLongClick(View view) {
        //String packageName= (String) view.getTag();
        //set various setting for this app
        //TODO: Individual App Settings
        if (view instanceof TextView) {
            showSettings((String) view.getTag());
        }

        return true;
    }

    private void resetBackgroundColor() {
        FlowLayout homeLayout = findViewById(R.id.home_layout);
        homeLayout.setBackgroundColor(Color.BLACK);
        int color = SpUtils.getInstance().getInt(Utility.getColorPrefs(BuildConfig.APPLICATION_ID), TEXT_COLOR);
        int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(BuildConfig.APPLICATION_ID), TEXT_SIZE);
        Settings settings = new Settings(this, BuildConfig.APPLICATION_ID,getString(R.string.app_name),color,size);
        // settings.setLastLauncher();
        settings.show();
        //finish();
    }

    private void showSettings(String packageName) {

        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                int color = SpUtils.getInstance().getInt(Utility.getColorPrefs(packageName), TEXT_COLOR);
                int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
                Dialog settings = new Settings(this,packageName,apps.getAppName().toString(),color,size);
                settings.show();
                break;
            }

        }

        // SpUtils.getInstance().putInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                int color = SpUtils.getInstance().getInt(Utility.getColorPrefs(packageName), TEXT_COLOR);
                int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
                apps.getTextView().setTextSize(size);
                apps.getTextView().setTextColor(color);
                apps.setColor(color);
                apps.setSize(size);
                break;
            }

        }
    }

    @Override
    public void onClick(View view) {
        if (view instanceof TextView) {
            String packageName = (String) view.getTag();
            try {
                startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
                refreshAppSize(packageName);
            } catch (Exception e) {
                //Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Launcher hack
    @Override
    public void onBackPressed() {
    }

    private void registerForReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadApps();
            }
        }, intentFilter);
    }

}
