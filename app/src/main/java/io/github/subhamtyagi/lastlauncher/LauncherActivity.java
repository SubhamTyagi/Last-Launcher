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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

        String appName = SpUtils.getInstance().getString(Utility.getAppNamePrefs(packageName), "");

        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                apps.getTextView().setTextSize(size);
                apps.getTextView().setTextColor(color);
                if (!appName.isEmpty())
                    apps.getTextView().setText(appName);
                apps.setAppName(appName);
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

            appName = SpUtils.getInstance().getString(Utility.getAppNamePrefs(packageName), resolveInfo.loadLabel(pm).toString());

            //TODO: before commit / take screen shot
            //if (appName.equalsIgnoreCase("KD campus")||appName.equalsIgnoreCase("kanyadaan")||appName.equalsIgnoreCase("getApps")||appName.equalsIgnoreCase("feedback")||appName.equalsIgnoreCase("gradeup")||appName.equalsIgnoreCase("mi remote")||appName.equalsIgnoreCase("pnb one")||appName.equalsIgnoreCase("play store")||appName.equalsIgnoreCase("drive")||appName.equalsIgnoreCase("duo"))
            //continue;

            textView = new TextView(this);
            textView.setText(appName);
            textView.setTag(packageName);//tag for identification

            textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);
            //textView.setTypeface(mTypeface);

            //TODO: move values to dimens
            textView.setPadding(10, -6, 0, -4);

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
            showPopup((String) view.getTag(), (TextView) view);
        } else if (view instanceof FlowLayout) {
            showGlobalSettings();
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
                        changeColor(packageName, view);
                        break;
                    case R.id.menu_size:
                        changeSize(packageName, view);
                        break;
                    case R.id.menu_rename:
                        renameApp(packageName, view);
                        break;
                    case R.id.menu_uninstall:
                        uninstallApp(packageName);
                        break;
                    case R.id.menu_app_info:
                        showAppInfo(packageName);

                    default:
                        return true;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void showAppInfo(String packageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        startActivity(intent);
    }

    private void uninstallApp(String packageName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, 97);
    }

    private void renameApp(String packageName, TextView textView) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename").setView(input).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String temp=input.getText().toString();
                if (!temp.isEmpty()) {
                    textView.setText(input.getText().toString());
                    SpUtils.getInstance().putString(Utility.getAppNamePrefs(packageName), temp);
                    refreshApps(packageName);
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        }).show();


        //Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
    }

    private void changeColor(String packageName, TextView view) {
        int color = SpUtils.getInstance().getInt(Utility.getColorPrefs(packageName), TEXT_COLOR);
        Dialog dialog = new ChooseColor(this, packageName, color, view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();

    }

    private void changeSize(String packageName, TextView view) {
        int size = SpUtils.getInstance().getInt(Utility.getSizePrefs(packageName), TEXT_SIZE);
        Dialog dialog = new ChooseSize(this, packageName, size, view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();
    }


    @Override
    public void onClick(View view) {
        if (view instanceof TextView) {
            String packageName = (String) view.getTag();
            try {
                startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
                refreshAppSize(packageName);
            } catch (Exception e) {
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
