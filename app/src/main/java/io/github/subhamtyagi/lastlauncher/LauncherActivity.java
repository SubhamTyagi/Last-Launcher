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
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.util.Utility;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {
    private ArrayList<Apps> appsList;
    //Typeface mTypeface;
    private static final int TEXT_SIZE = 30;
    FlowLayout homeLayout;
    private static int TEXT_COLOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SpUtils.getInstance().init(this);

        //TODO: check the memory footprint
        // mTypeface = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa.ttf");

        TEXT_COLOR = getResources().getColor(R.color.default_apps_colors);

        loadApps();

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
                break;
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

        Collections.sort(activities, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(pm).toString(),
                b.loadLabel(pm).toString()
        ));

        homeLayout = findViewById(R.id.home_layout);
        homeLayout.setOnLongClickListener(this);
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

            textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);
            //textView.setTypeface(mTypeface);

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
        if (view instanceof TextView) {
            //showSettings((String) view.getTag());
            showPopup((String) view.getTag(), (TextView) view);
        } else if (view instanceof FlowLayout) {
            showGlobalSettings();
            // showSettings(BuildConfig.APPLICATION_ID);
        }
        return true;
    }

    private void showGlobalSettings() {
        PopupMenu popupMenu = new PopupMenu(this, homeLayout);
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting_global, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_fonts://change fonts
                        break;
                    case R.id.menu_background: //change background color
                        break;
                    case R.id.menu_primary_colors: //set primary colors;
                        break;
                    case R.id.menu_backup://backup
                        break;
                    case R.id.menu_reset: //reset
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void showPopup(String packageName, TextView view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_color:
                        changeColor(packageName);
                        break;
                    case R.id.menu_size:
                        changeSize(packageName);
                        break;
                    case R.id.menu_rename:
                        renameApp(packageName);
                        break;
                    case R.id.menu_uninstall:
                        uninstallApp(packageName);
                        break;

                    default:
                        return true;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void uninstallApp(String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, 97);
    }

    private void renameApp(String packageName) {
        // show input dialog
        //resets apps list
        //save changes to prefs
    }

    private void changeSize(String packageName) {
        showSettings(packageName);
    }

    private void changeColor(String packageName) {
        showSettings(packageName);
    }


    private void showSettings(String packageName) {

        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                int color = SpUtils.getInstance().getInt(Utility.getColorPrefs(packageName), TEXT_COLOR);
                int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
                Dialog settings = new Settings(this, packageName, apps.getAppName().toString(), color, size);
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
