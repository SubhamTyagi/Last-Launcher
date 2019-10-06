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
import android.view.Gravity;
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

import io.github.subhamtyagi.lastlauncher.dialogs.ChooseColor;
import io.github.subhamtyagi.lastlauncher.dialogs.ChooseSize;
import io.github.subhamtyagi.lastlauncher.dialogs.GlobalSettings;
import io.github.subhamtyagi.lastlauncher.dialogs.RenameInput;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Apps> appsList;
    //Typeface mTypeface;

    FlowLayout homeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        SpUtils.getInstance().init(this);
        int theme = DbUtils.getTheme();

        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //TOD O: check the memory footprint
        // mTypeface = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa.ttf");
        //DbUtils.TEXT_COLOR = getResources().getColor(R.color.default_apps_colors);

        loadApps();
        registerForReceiver();
        SpUtils.getInstance().putBoolean(getString(R.string.sp_first_time_app_open), false);
    }

    private void refreshAppSize(String packageName) {
        int size = DbUtils.getAppSize(packageName) + 1;
        DbUtils.putAppSize(packageName, size);

        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                apps.getTextView().setTextSize(size);
                apps.setSize(size);
                break;
            }
        }
    }

    void refreshApps(String packageName) {

        int size = DbUtils.getAppSize(packageName);
        int color = DbUtils.getAppColor(packageName);
        String appOriginalName = DbUtils.getAppOriginalName(packageName, "");
        String appName = DbUtils.getAppName(packageName, appOriginalName);

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
        Intent startupIntent = new Intent(Intent.ACTION_MAIN, null);
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

            DbUtils.putAppOriginalName(packageName, resolveInfo.loadLabel(pm).toString());
            appName = DbUtils.getAppName(packageName, resolveInfo.loadLabel(pm).toString());

            //TODO: before commit / take screen shot
            //if (appName.equalsIgnoreCase("KD campus") || appName.equalsIgnoreCase("kanyadaan") || appName.equalsIgnoreCase("getApps") || appName.equalsIgnoreCase("feedback") || appName.equalsIgnoreCase("gradeup") || appName.equalsIgnoreCase("mi remote") || appName.equalsIgnoreCase("pnb one") || appName.equalsIgnoreCase("play store") || appName.equalsIgnoreCase("drive") || appName.equalsIgnoreCase("duo"))
            //    continue;

            boolean hide = DbUtils.isAppHidden(packageName);

            if (hide) continue;

            textView = new TextView(this);
            textView.setText(appName);
            textView.setTag(packageName);//tag for identification

            textView.setOnClickListener(this);
            textView.setOnLongClickListener(this);
            //textView.setTypeface(mTypeface);

            //TODO: move values to dimens/ improve this in @FlowLayout: Temp fix
            textView.setPadding(10, -6, 0, -4);

            textSize = DbUtils.getAppSize(packageName);
            color = DbUtils.getAppColor(packageName);

            textView.setTextSize(textSize);

            if (color != -1)
                textView.setTextColor(color);

            appsList.add(
                    new Apps(++id,
                            packageName,
                            appName,
                            textView,
                            0xFFFFFF,
                            textSize,
                            false,
                            false
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
            new GlobalSettings(this, this).show();
        }
        return true;
    }


    private void showPopup(String packageName, TextView view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        if (DbUtils.isAppFreezed(packageName)) {
            popupMenu.getMenu().findItem(R.id.menu_freeze_size).setTitle(R.string.unfreeze);
        }
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
                    case R.id.menu_freeze_size: {
                        freezeSize(packageName);
                    }
                    break;
                    case R.id.menu_hide:
                        hideApp(packageName, view);
                        break;
                    case R.id.menu_uninstall:
                        uninstallApp(packageName);
                        break;
                    case R.id.menu_app_info:
                        showAppInfo(packageName);
                        break;
                    case R.id.menu_reset_to_default:
                        resetApp(packageName, view);

                    default:
                        return true;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void hideApp(String packageName, TextView view) {
        //DbUtils.hideApp(packageName, true);
        view.setVisibility(View.GONE);
        //refreshApps(packageName);
    }

    private void freezeSize(String packageName) {
        DbUtils.freezeAppSize(packageName, true);
    }

    private void renameApp(String packageName, TextView view) {
        Dialog dialog = new RenameInput(this, packageName, view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();
    }

    private void resetApp(String packageName, TextView view) {

        DbUtils.removeAppName(packageName);
        DbUtils.removeColor(packageName);
        DbUtils.removeSize(packageName);
        refreshApps(packageName);
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

    private void changeColor(String packageName, TextView view) {
        int color = DbUtils.getAppColor(packageName);
        Dialog dialog = new ChooseColor(this, packageName, color, view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();

    }

    private void changeSize(String packageName, TextView view) {
        int size = DbUtils.getAppSize(packageName);
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
                if (!DbUtils.isAppFreezed(packageName)) {
                    refreshAppSize(packageName);
                }
            } catch (Exception ignore) {
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
