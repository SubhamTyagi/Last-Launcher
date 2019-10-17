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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.apmem.tools.layouts.FlowLayout;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import io.github.subhamtyagi.lastlauncher.dialogs.ChooseColor;
import io.github.subhamtyagi.lastlauncher.dialogs.ChooseSize;
import io.github.subhamtyagi.lastlauncher.dialogs.GlobalSettings;
import io.github.subhamtyagi.lastlauncher.dialogs.RenameInput;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.util.UserUtils;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    private final String TAG = "LaucnherActivity";

    //private Map<String,Apps> appsList;
    private ArrayList<Apps> appsList;
    //Typeface mTypeface;

    FlowLayout homeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SpUtils.getInstance().init(this);
        int theme = DbUtils.getTheme();
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        //TOD O: check the memory footprint
        // mTypeface = Typeface.createFromAsset(getAssets(),"fonts/Comfortaa.ttf");

        homeLayout = findViewById(R.id.home_layout);
        homeLayout.setOnLongClickListener(this);


        loadApps();
        registerForReceiver();
        SpUtils.getInstance().putBoolean(getString(R.string.sp_first_time_app_open), false);

    }


    private void loadApps() {

        Intent startupIntent = new Intent(Intent.ACTION_MAIN, null);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        int appsCount = activities.size();

        appsList = new ArrayList<>(appsCount);

        String packageName, appName;
        int color, textSize;
        boolean hide;

        for (ResolveInfo resolveInfo : activities) {
            packageName = resolveInfo.activityInfo.packageName;
            String activity = resolveInfo.activityInfo.name + "&" + packageName;

            DbUtils.putAppOriginalName(packageName, resolveInfo.loadLabel(pm).toString());
            appName = DbUtils.getAppName(packageName, resolveInfo.loadLabel(pm).toString());
            hide = DbUtils.isAppHidden(packageName);

            if (hide) {
                //Temp hide
                continue;
            }

            textSize = DbUtils.getAppSize(packageName);
            color = DbUtils.getAppColor(packageName);
            boolean freeze = DbUtils.isAppFreezed(packageName);

            if (DbUtils.isRandomColor() && color == -1) {
                Random rnd = new Random();
                color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            }
            appsList.add(new Apps(packageName, activity, appName, getCustomView(), color, textSize, hide, freeze));
        }

        sortApps();
    }

    private void sortApps() {
        homeLayout.removeAllViews();
        Collections.sort(appsList, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getAppName().toString(),
                b.getAppName().toString()
        ));
        for (Apps apps : appsList) {
            homeLayout.addView(apps.getTextView(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void refreshAppSize(String packageName) {
        int size = DbUtils.getAppSize(packageName) + 2;
        DbUtils.putAppSize(packageName, size);
        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                apps.setSize(size);
                break;
            }
        }
    }

    void refreshApps(String packageName) {
        for (Apps apps : appsList) {
            if (apps.getPackageName().toString().equalsIgnoreCase(packageName)) {
                appsList.remove(apps);
                //now add new App
                int size = DbUtils.getAppSize(packageName);
                int color = DbUtils.getAppColor(packageName);
                String appOriginalName = DbUtils.getAppOriginalName(packageName, "");
                String appName = DbUtils.getAppName(packageName, appOriginalName);

                boolean hide = apps.isHide();
                boolean freezeSize = apps.isFreezeSize();

                Apps newApp = new Apps(packageName, null, appName, getCustomView(), color, size, hide, freezeSize);
                appsList.add(newApp);
                sortApps();
                break;
            }
        }
    }

    private TextView getCustomView() {
        TextView textView = new TextView(this);
        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);
        textView.setPadding(10, -6, 0, -4);
        //textView.setTypeface(mTypeface);
        return textView;
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
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_color:
                    changeColor(packageName, view);
                    break;
                case R.id.menu_size:
                    changeSize(packageName, view);
                    break;
                case R.id.menu_rename:
                    renameApp(packageName, view.getText().toString());
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
                    resetApp(packageName);

                default:
                    return true;
            }
            return true;
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

    private void renameApp(String packageName, String appName) {
        Dialog dialog = new RenameInput(this, packageName, appName, this);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();

    }

    public void onAppRenamed(String packageName, String appNewName) {
        for (Apps app : appsList) {
            if (app.getPackageName().toString().equalsIgnoreCase(packageName)) {
                app.setAppName(appNewName);
                sortApps();
                break;
            }
        }

    }

    private void resetApp(String packageName) {
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
        if (color == -1) {
            color = view.getCurrentTextColor();
        }
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
            String activity = (String) view.getTag();
            String[] strings = activity.split("&");
            try {
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                if (strings[0].contains(strings[1])) {
                    intent.setClassName(strings[1], strings[0]);
                    //Log.d(TAG, "onClick: app name" + activity);
                    intent.setComponent(new ComponentName(strings[1], strings[0]));
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                } else
                    startActivity(getPackageManager().getLaunchIntentForPackage(strings[1]));

                if (!DbUtils.isAppFreezed(activity)) {
                    refreshAppSize(activity);
                }
            } catch (Exception ignore) {
                Log.e(TAG, "onClick: " + ignore);
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


    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    148
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 148) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                DbUtils.permissionRequired(false);
            }
        }
    }

    public boolean isPermissionRequired() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public void browseFile() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("file/plain");
        intent = Intent.createChooser(chooseFile, "Choose backup file");
        startActivityForResult(intent, 125);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == 125) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            try {
                boolean b = SpUtils.getInstance().loadSharedPreferencesFromFile(cr.openInputStream(uri));
                if (b) {
                    recreate();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<Apps> loadAppsMINLolipop(Activity activity, boolean hideHidden) {
        List<Apps> appsList = new ArrayList<>();
        PackageManager manager = activity.getPackageManager();

        UserUtils userUtils = new UserUtils(activity);

        UserManager userManager = (UserManager) activity.getSystemService(Context.USER_SERVICE);
        LauncherApps launcher = (LauncherApps) activity.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        for (UserHandle profile : userManager.getUserProfiles()) {

            for (LauncherActivityInfo activityInfo : launcher.getActivityList(null, profile)) {

                String componentName = activityInfo.getComponentName().flattenToString();
                String userPackageName;
                long user = userManager.getSerialNumberForUser(profile);
                if (user != userUtils.getCurrentSerial()) {
                    userPackageName = user + "-" + componentName;
                } else {
                    userPackageName = componentName;
                }

                String appName = activityInfo.getLabel().toString();

            }
        }
        return appsList;
    }

}

