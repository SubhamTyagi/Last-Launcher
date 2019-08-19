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
    private static final int TEXT_SIZE=25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!MyUsageStats.isStatAccessPermissionSet(this))
                if (SpUtils.getInstance().init(this)
                        .getBoolean(getString(R.string.sp_first_time_app_open), true)
                        ||
                        SpUtils.getInstance().init(this)
                                .getBoolean(getString(R.string.sp_request_usage_stat), true)
                ) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.app_usage_permission_title))
                            .setMessage(R.string.app_usage_permission_message)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    SpUtils.getInstance().init(LauncherActivity.this).putBoolean(LauncherActivity.this.getString(R.string.sp_request_usage_stat), false);
                                    requestPermission();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    SpUtils.getInstance().init(LauncherActivity.this).putBoolean(LauncherActivity.this.getString(R.string.sp_request_usage_stat), false);
                                }
                            }).show();

                }
        }
       */
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
        Random rnd = new Random();

        for (ResolveInfo resolveInfo : activities) {
            packageName = resolveInfo.activityInfo.packageName;
            appName = resolveInfo.loadLabel(pm).toString();

            //set text color size,weight
            textView = new TextView(this);
            textView.setText(appName);
            textView.setTag(packageName);//tag for identification
            textView.setOnClickListener(this::onClick);
            textView.setOnLongClickListener(this::onLongClick);
            //textView.set

            textView.setPadding(10,5,5,10);

            //int color=getResources().getColor(R.color.default_app_text_color);
            //size=(int)getResources().getDimension(R.dimen.default_app_text_size);
            int color = (rnd.nextInt(7) + 1) * 100;
            color = Utility.getRandomColor(String.valueOf(color), this);
            textView.setTextColor(color);

            int textSize=SpUtils.getInstance().init(this).getInt(Utility.getSizePrefs(packageName),TEXT_SIZE);
            textView.setTextSize(textSize);

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
        if (view instanceof TextView)
            resetAppSize((String) view.getTag());
        else
            resetBackgroundColor();

        return true;
    }

    private void resetBackgroundColor() {
        FlowLayout homeLayout = findViewById(R.id.home_layout);
        homeLayout.setBackgroundColor(Color.BLACK);
        //finish();
    }

    private void resetAppSize(String packageName) {
        SpUtils.getInstance().putInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                apps.getTextView().setTextSize(TEXT_SIZE);
                apps.setSize(TEXT_SIZE);
            }
        }
        Dialog settings=new Settings(this);
        settings.show();
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
        } else {


           int color = Utility.getRandomColor("900", this);
            FlowLayout homeLayout = findViewById(R.id.home_layout);
            homeLayout.setBackgroundColor(color);

        }
    }

    /*

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            if (!MyUsageStats.isStatAccessPermissionSet(this)) {
               // Log.d(TAG, "onActivityResult: permission is not set");
                if (SpUtils.getInstance().init(this).getBoolean(getString(R.string.sp_request_usage_stat), true))
                    requestPermission();
            } else {
                SpUtils.getInstance().init(LauncherActivity.this).putBoolean(LauncherActivity.this.getString(R.string.sp_request_usage_stat), false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
 @Override
    protected void onRestart() {
        super.onRestart();
        //refreshAllApps();

    }
@Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver();
    }*/

    //Launcher hack
    @Override
    public void onBackPressed() {
    }

    /*private void requestPermission() {
        if (!MyUsageStats.isStatAccessPermissionSet(this)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
        }
    }*/

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
