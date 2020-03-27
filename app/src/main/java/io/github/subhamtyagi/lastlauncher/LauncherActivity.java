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
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.subhamtyagi.lastlauncher.dialogs.ChooseColor;
import io.github.subhamtyagi.lastlauncher.dialogs.ChooseSize;
import io.github.subhamtyagi.lastlauncher.dialogs.GlobalSettings;
import io.github.subhamtyagi.lastlauncher.dialogs.RenameInput;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.util.UserUtils;
import io.github.subhamtyagi.lastlauncher.util.Utils;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    public static final int COLOR_SNIFFER_REQUEST = 154;
    public final static String DEFAULT_COLOR_FOR_APPS = "default_color_for_apps";
    private static final int RESTORE_REQUEST = 125;
    private static final int FONTS_REQUEST = 126;
    private static final int PERMISSION_REQUEST = 127;
    private static final int DEFAUTL_TEXT_SIZE_NORMAL_APPS = 20;
    private static final int DEFAUTL_TEXT_SIZE_OFTEN_APPS = 32;
    private final String TAG = "LauncherActivity";
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<Apps> mAppsList;
    private Typeface mTypeface;
    private FlowLayout mHomeLayout;

    //not in use;
    @TargetApi(21)
    public static List<Apps> loadAppsMINLolipop(Activity activity, boolean hideHidden) {
        List<Apps> appsList = new ArrayList<>();
        PackageManager manager = activity.getPackageManager();

        UserUtils userUtils = new UserUtils(activity);

        UserManager userManager = null;

        userManager = (UserManager) activity.getSystemService(Context.USER_SERVICE);

        LauncherApps launcher = (LauncherApps) activity.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        for (UserHandle profile : userManager.getUserProfiles()) {

            for (LauncherActivityInfo activityInfo : launcher.getActivityList(null, profile)) {

                String componentName = activityInfo.getComponentName().flattenToString();
                String useractivityName;
                long user = userManager.getSerialNumberForUser(profile);
                if (user != userUtils.getCurrentSerial()) {
                    useractivityName = user + "-" + componentName;
                } else {
                    useractivityName = componentName;
                }

                String appName = activityInfo.getLabel().toString();

            }
        }
        return appsList;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SpUtils.getInstance().init(this);
        int theme = DbUtils.getTheme();
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        String fontsPath = DbUtils.getFonts();
        if (fontsPath == null || DbUtils.isFirstStart())
            mTypeface = Typeface.createFromAsset(getAssets(), "fonts/raleway_bold.ttf");
        else
            mTypeface = Typeface.createFromFile(fontsPath);

        mHomeLayout = findViewById(R.id.home_layout);
        mHomeLayout.setOnLongClickListener(this);

        loadApps();
        registerForReceiver();
        SpUtils.getInstance().putBoolean(getString(R.string.sp_first_time_app_open), false);
        System.gc();
    }

    private void loadApps() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN, null);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        int appsCount = activities.size();
        if (mAppsList != null)
            mAppsList.clear();
        mAppsList = new ArrayList<>(appsCount);

        List<String> oftenApps = Utils.getOftenAppsList();

        String packageName, appName;
        int color, textSize;
        boolean hide;

        for (ResolveInfo resolveInfo : activities) {
            packageName = resolveInfo.activityInfo.packageName;
            String activity = packageName + "/" + resolveInfo.activityInfo.name;
            DbUtils.putAppOriginalName(activity, resolveInfo.loadLabel(pm).toString());
            appName = DbUtils.getAppName(activity, resolveInfo.loadLabel(pm).toString());
            hide = DbUtils.isAppHidden(activity);

            if (hide) {
                //Temp hide
                continue;
            }

            textSize = DbUtils.getAppSize(activity);
            if (textSize == DbUtils.NULL_TEXT_SIZE) {
                if (oftenApps.contains(packageName)) {
                    textSize = DEFAUTL_TEXT_SIZE_OFTEN_APPS;
                } else {
                    textSize = DEFAUTL_TEXT_SIZE_NORMAL_APPS;
                }
                DbUtils.putAppSize(activity, textSize);
            }


            color = DbUtils.getAppColor(activity);
            boolean freeze = DbUtils.isAppFreezed(activity);

            if (DbUtils.isExternalSourceColor() && color == DbUtils.NULL_TEXT_COLOR) {
                color = DbUtils.getAppColorExternalSource(activity);
            }

            mAppsList.add(new Apps(activity, appName, getCustomView(), color, textSize, hide, freeze));
        }

        sortApps();
    }

    //TODO: others sorts
    private void sortApps() {
        mHomeLayout.removeAllViews();
        Collections.sort(mAppsList, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getAppName().toString(),
                b.getAppName().toString()
        ));
        for (Apps apps : mAppsList) {
            mHomeLayout.addView(apps.getTextView(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    private void refreshAppSize(String activityName) {
        for (Apps apps : mAppsList) {
            if (apps.getActivityName().toString().equalsIgnoreCase(activityName)) {
                int size = DbUtils.getAppSize(activityName)+2;
                apps.setSize(size);
                DbUtils.putAppSize(activityName, size);
                break;
            }
        }
    }

    private void refreshApps(String activityName) {
        for (Apps apps : mAppsList) {
            if (apps.getActivityName().toString().equalsIgnoreCase(activityName)) {
                mAppsList.remove(apps);
                //now add new App
                int size = apps.getSize();
                int color = DbUtils.getAppColor(activityName);
                String appOriginalName = DbUtils.getAppOriginalName(activityName, "");
                String appName = DbUtils.getAppName(activityName, appOriginalName);

                boolean hide = apps.isHide();
                boolean freezeSize = apps.isFreezeSize();

                Apps newApp = new Apps(activityName, appName, getCustomView(), color, size, hide, freezeSize);
                mAppsList.add(newApp);
                sortApps();
                break;
            }
        }
    }

    private TextView getCustomView() {
        TextView textView = new TextView(this);
        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);
        textView.setPadding(10, -6, 4, -2);
        textView.setTypeface(mTypeface);
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

    private void showPopup(String activityName, TextView view) {
        Context context;
        if (DbUtils.getTheme() == R.style.Wallpaper)
            context = new ContextThemeWrapper(this, R.style.AppTheme);
        else
            context = new ContextThemeWrapper(this, DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        if (DbUtils.isAppFreezed(activityName)) {
            popupMenu.getMenu().findItem(R.id.menu_freeze_size).setTitle(R.string.unfreeze_size);
        }
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_color:
                    changeColor(activityName, view);
                    break;
                case R.id.menu_size:
                    changeSize(activityName, view);
                    break;
                case R.id.menu_rename:
                    renameApp(activityName, view.getText().toString());
                    break;
                case R.id.menu_freeze_size: {
                    boolean b = DbUtils.isAppFreezed(activityName);
                    DbUtils.freezeAppSize(activityName, !b);
                }
                break;
                case R.id.menu_hide:
                    hideApp(activityName, view);
                    break;
                case R.id.menu_uninstall:
                    uninstallApp(activityName);
                    break;
                case R.id.menu_app_info:
                    showAppInfo(activityName);
                    break;
                case R.id.menu_reset_to_default:
                    resetApp(activityName);

                default:
                    return true;
            }
            return true;
        });
        popupMenu.show();
    }

    private void hideApp(String activityName, TextView view) {
        //Toast.makeText(this, "Current Hide is not fully implemented\n After hide app you will not access that app from this launcher", Toast.LENGTH_LONG).show();
        DbUtils.hideApp(activityName, true);
        view.setVisibility(View.GONE);
        //refreshApps(activityName);
    }

    private void renameApp(String activityName, String appName) {
        Dialog dialog = new RenameInput(this, activityName, appName, this);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();

    }

    public void onAppRenamed(String activityName, String appNewName) {
        for (Apps app : mAppsList) {
            if (app.getActivityName().toString().equalsIgnoreCase(activityName)) {
                app.setAppName(appNewName.trim());
                sortApps();
                break;
            }
        }

    }

    private void resetApp(String activityName) {
        DbUtils.removeAppName(activityName);
        DbUtils.removeColor(activityName);
        DbUtils.removeSize(activityName);
        refreshApps(activityName);
    }

    private void showAppInfo(String activityName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activityName.split("/")[0]));
        startActivity(intent);
    }

    private void uninstallApp(String activityName) {
        Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
        intent.setData(Uri.parse("package:" + activityName.split("/")[0]));
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
        startActivityForResult(intent, 97);
    }

    private void changeColor(String activityName, TextView view) {
        int color = DbUtils.getAppColor(activityName);
        if (color == DbUtils.NULL_TEXT_COLOR) {
            color = view.getCurrentTextColor();
        }
        Dialog dialog = new ChooseColor(this, activityName, color, view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();
    }

    private void changeSize(String activityName, TextView view) {
        int size = DbUtils.getAppSize(activityName);
        if (size == DbUtils.NULL_TEXT_SIZE) {
            for (Apps apps : mAppsList) {
                if (apps.getActivityName().equals(activityName)) {
                    size = apps.getSize();
                }
            }
        }
        Dialog dialog = new ChooseSize(this, activityName, size, view);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        if (view instanceof TextView) {
            String activity = (String) view.getTag();
            String[] strings = activity.split("/");
            try {
                //TODO: apps is not in recent menus
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.setClassName(strings[0], strings[1]);
                intent.setComponent(new ComponentName(strings[0], strings[1]));
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                if (!DbUtils.isSizeFreezed() && !DbUtils.isAppFreezed(activity)) {
                    refreshAppSize(activity);
                }
            } catch (Exception ignore) {
                Log.e(TAG, "onClick: " + ignore);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void registerForReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadApps();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST
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
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("file/plain");
        Intent intent = Intent.createChooser(chooseFile, this.getString(R.string.choose_old_backup_files));
        startActivityForResult(intent, RESTORE_REQUEST);
    }

    public void browseFonts() {
        if (isPermissionRequired()) {
            requestPermission();
        }
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("file/plain");
        Intent intent = Intent.createChooser(chooseFile, "Choose Fonts");
        startActivityForResult(intent, FONTS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == RESTORE_REQUEST) {
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
        } else if (requestCode == FONTS_REQUEST) {
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                cursor.close();
                Log.i(TAG, "onActivityResult: " + path);
                DbUtils.setFonts(path);
                mTypeface = Typeface.createFromFile(path);
                loadApps();
            } catch (Exception i) {
                i.printStackTrace();
            }
        } else if (requestCode == COLOR_SNIFFER_REQUEST) {
            Log.d(TAG, "onActivityResult: yes i am called !~!");
            //TODO: data schema consensus
            //GET DATA FROM COLOR SNIFFER APPS:
            //K,V ??? no
            // bundle yes
            // is it complex: may be
            //Bundle is null wtf
            colorSnifferCall(getIntent().getExtras().getBundle("color_bundle"));

        }
    }

    //hidden app support
    public void showHiddenApps() {
        // Intent i = new Intent(this, MyList.class);
        // i.putParcelableArrayListExtra("arrays", mAppsList);
        // startActivity(i);

    }

    //may be override of abstract class method to be called from color sniffer #3 types
    public void colorSnifferCall(Bundle bundle) {
        Log.d(TAG, "colorSnifferCall: i am called now it is my duty");
        int DEFAULT_COLOR = -1;//-1 represent null or not set
        boolean defaultColorSet = false;// for change set
        String sDefaultColor = bundle.getString(DEFAULT_COLOR_FOR_APPS);//keys
        if (sDefaultColor != null) {// not set by ColorSniffer
            DEFAULT_COLOR = Integer.valueOf(sDefaultColor);
            if (DEFAULT_COLOR != DbUtils.NULL_TEXT_COLOR) { //NULL_TEXT_COLOR=-1
                defaultColorSet = true;// to save cpu cycle
            }
        }

        // get each value as proposed by Color Sniffer App developer

        for (Apps apps : mAppsList) {
            TextView textView = apps.getTextView();
            String appPackage = apps.getActivityName().toString();
            String sColor = bundle.getString(appPackage);// get each value as proposed by Color Sniffer App developer
            if (sColor != null) {
                int color = Integer.valueOf(sColor);
                textView.setTextColor(color);
                DbUtils.putAppColor(appPackage, color);
            } else if (defaultColorSet) {
                //set default color
                DbUtils.putAppColor(appPackage, DEFAULT_COLOR);
                textView.setTextColor(DEFAULT_COLOR);
            }//else do nothing theme default color will apply
        }
    }

    //Clipboard manager
    public Map<String, Integer> clipboardData() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData.getItemCount() > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    String tsv = item.getText().toString();
                    // Log.d(TAG, "clipboardData: " + tsv);
                    //validate tsv and get its data
                    //unique id bae73ae068dacc6cb659d1fb231e7b11 i.e LastLauncher-ColorSniffer MD5-128
                    String[] entries = tsv.split("\n");//get each line
                    Map<String, Integer> colorsAndId = new ArrayMap<>(); // map to put all values in key and values format
                    for (int i = 0, entriesLength = entries.length; i < entriesLength; i++) {
                        String entry = entries[i];// iterate over every line
                        String[] activityIdAndColor = entry.split("\t");// split line into id and color
                        int color = Color.parseColor(activityIdAndColor[1]);
                        colorsAndId.put(activityIdAndColor[0], color);// put id and color to map
                    }
                    setAppsColorFromClipboard(colorsAndId);
                    return colorsAndId;// return map
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return null;// return empty null/
    }

    private void setAppsColorFromClipboard(Map<String, Integer> colorsAndId) {
        if (colorsAndId == null) return;
        DbUtils.externalSourceColor(true);
        for (Apps apps : mAppsList) {
            try {
                TextView textView = apps.getTextView();
                String activityName = apps.getActivityName().toString().split("/")[0];
                Integer newColor = colorsAndId.get(activityName);
                if (newColor == null) continue;
                textView.setTextColor(newColor);
                DbUtils.putAppColorExternalSource(apps.getActivityName().toString(), newColor);
            } catch (NullPointerException ignore) {
                ignore.printStackTrace();
            }
        }
    }
}

