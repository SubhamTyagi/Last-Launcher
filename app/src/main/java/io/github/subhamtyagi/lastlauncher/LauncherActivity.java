/*
 * Last Launcher
 * Copyright (C) 2019,2020 Shubham Tyagi
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
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.subhamtyagi.lastlauncher.dialogs.ColorSizeDialog;
import io.github.subhamtyagi.lastlauncher.dialogs.FrozenAppsDialogs;
import io.github.subhamtyagi.lastlauncher.dialogs.GlobalColorSizeDialog;
import io.github.subhamtyagi.lastlauncher.dialogs.GlobalSettingsDialog;
import io.github.subhamtyagi.lastlauncher.dialogs.HiddenAppsDialogs;
import io.github.subhamtyagi.lastlauncher.dialogs.PaddingDialog;
import io.github.subhamtyagi.lastlauncher.dialogs.RenameInputDialogs;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;
import io.github.subhamtyagi.lastlauncher.utils.Utils;
import io.github.subhamtyagi.lastlauncher.views.textview.AppTextView;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

/**
 * --------------------------------------------------------------------------
 * People can criticise me all the time they want,
 * by this is what I am and I won't change the way I live for them.
 * I live the way I want to flow.
 * -------------------------------------------------------------------------
 * -
 * If we don’t transform the world, who will? If not now, when?
 * If you have something to give, give it now
 * -
 * Do your little bit of good where you are;
 * it’s those little bits of good put together that overwhelm the world."
 * -
 * Don’t just think, do it. Now it is you turn,  do it now, go fast and open pull request
 * -
 * ----------------------------------------------------------------------------
 * This Activity extends the api 14 Activity Class not latest AppCompatActivity
 * Reason: Small apk size
 */
public class LauncherActivity extends Activity implements View.OnClickListener,
        View.OnLongClickListener {

    public static final int COLOR_SNIFFER_REQUEST = 154;
    public final static String DEFAULT_COLOR_FOR_APPS = "default_color_for_apps";
    //various sorting constant
    //why constant? Why not enums for this ?
    // may be lack from DB side
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_SIZE = 2;
    public static final int SORT_BY_COLOR = 3;
    public static final int SORT_BY_OPENING_COUNTS = 4;
    public static final int SORT_BY_CUSTOM = 5;

    private static final int RESTORE_REQUEST = 125;
    private static final int FONTS_REQUEST = 126;
    private static final int PERMISSION_REQUEST = 127;
    private static final int DEFAUTL_TEXT_SIZE_NORMAL_APPS = 20;
    private static final int DEFAUTL_TEXT_SIZE_OFTEN_APPS = 36;
    private static ArrayList<Apps> mAppsList;
    private final String TAG = "LauncherActivity";
    private BroadcastReceiver broadcastReceiverAppInstall;
    private BroadcastReceiver broadcastReceiverShortcutInstall;
    private Typeface mTypeface;
    private FlowLayout mHomeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the shared prefs may be done in application class
        DbUtils.init(this);

        int theme = DbUtils.getTheme();
        //theme must be set before setContentView
        setTheme(theme);

        setContentView(R.layout.activity_launcher);

        // set the status bar color as per theme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setNavigationAndStatusBarColor(theme);
        }
        // set the fonts
        setFont();

        mHomeLayout = findViewById(R.id.home_layout);
        mHomeLayout.setOnLongClickListener(this);

        //set alignment default is center|center_vertical
        mHomeLayout.setGravity(DbUtils.getFlowLayoutAlignment());

        //set padding ..
        mHomeLayout.setPadding(DbUtils.getPaddingLeft(), DbUtils.getPaddingTop(), DbUtils.getPaddingRight(), DbUtils.getPaddingBottom());

        // loads the apps
        loadApps();
        // register the receiver for installed and  uninstall , update app
        registerForReceiver();

    }

    /**
     * set the color of status bar and navigation bar as per theme
     * if theme color is light then pass this to system so status icon color will turn into black
     *
     * @param theme current theme applied to launcher
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setNavigationAndStatusBarColor(int theme) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // getWindow().setNavigationBarColor(getResources().getColor(android.R.color.transparent));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (theme) {
                case R.style.White:
                case R.style.WhiteOnGrey:
                case R.style.BlackOnGrey: {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }
        }
    }

    private void setFont() {
        // get and set fonts
        String fontsPath = DbUtils.getFonts();
        if (fontsPath == null) {
            mTypeface = Typeface.createFromAsset(getAssets(), "fonts/raleway_bold.ttf");
        } else {
            mTypeface = Typeface.createFromFile(fontsPath);
        }

    }

    //TODO:  do something
    private void loadApps() {
        // get the apps installed on devices;
        Intent startupIntent = new Intent(Intent.ACTION_MAIN, null);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        int appsCount = activities.size();

        // check whether our app list is already initlaized so we can clear it
        //
        if (mAppsList != null)
            mAppsList.clear();
        // is above code necessary i think that is redundant
        mAppsList = new ArrayList<>(appsCount);

        // get the most used apps
        // a list of app that are popular on fdroid and my most used app
        List<String> oftenApps = Utils.getOftenAppsList();

        String packageName, appName;
        int color, textSize;
        boolean hide;
        // iterate over each app and initalize our base work
        for (ResolveInfo resolveInfo : activities) {

            packageName = resolveInfo.activityInfo.packageName;
            // activity name as com.example/com.example.MainActivity
            String activity = packageName + "/" + resolveInfo.activityInfo.name;
            /// save the app original name so that we can use this later e.g if user change
            /// the app name then we have the name in DB
            DbUtils.putAppOriginalName(activity, resolveInfo.loadLabel(pm).toString());
            // check whether user set the custom app name for eg. long name to small name
            appName = DbUtils.getAppName(activity, resolveInfo.loadLabel(pm).toString());
            // is app is hidden by user
            hide = DbUtils.isAppHidden(activity);
            // get the app text size
            textSize = DbUtils.getAppSize(activity);
            // check if text size is null then set the size to default size
            // size is null(-1) when user installed this app
            if (textSize == DbUtils.NULL_TEXT_SIZE) {
                if (oftenApps.contains(packageName)) {
                    textSize = DEFAUTL_TEXT_SIZE_OFTEN_APPS;
                } else {
                    textSize = DEFAUTL_TEXT_SIZE_NORMAL_APPS;
                }

                /// DbUtils.putAppSize(activity, textSize);
            }

            // get app color
            color = DbUtils.getAppColor(activity);

            // check for default color : set default colors if random color is not set
            if (!DbUtils.isRandomColor() && color == DbUtils.NULL_TEXT_COLOR) {
                color = DbUtils.getAppsColorDefault();
            }
            // whether app size is freezed
            boolean freeze = DbUtils.isAppFrozen(activity);

            // this is a separated implementation of ColorSniffer app
            // if User set the color from external app like ColorSniffer
            // then use that colors

            if (BuildConfig.enableColorSniffer) {
                if (DbUtils.isExternalSourceColor() && color == DbUtils.NULL_TEXT_COLOR) {
                    color = DbUtils.getAppColorExternalSource(activity);
                }
            } else if (DbUtils.isRandomColor() && color == DbUtils.NULL_TEXT_COLOR) {
                color = Utils.generateColorFromString(appName);
            }


            int openingCounts = DbUtils.getOpeningCounts(activity);
            // save all and add this is to app list
            mAppsList.add(new Apps(false, activity, appName, getCustomView(), color, textSize, hide, freeze, openingCounts));

        }

        int installedShortcut = DbUtils.getShortcutCount();

        for (int i = 1; i <= installedShortcut; i++) {
            String uri = DbUtils.getShortcutURI(i);
            String sName = DbUtils.getShortcutName(i);

            String sActivity = String.valueOf(Utils.hash(uri));
            int sColor = DbUtils.getAppColor(sActivity);
            int sSize = DbUtils.getAppSize(sActivity);

            if (sSize == DbUtils.NULL_TEXT_SIZE) {
                sSize = DEFAUTL_TEXT_SIZE_NORMAL_APPS;
            }

            if (sColor == DbUtils.NULL_TEXT_COLOR) {
                if (DbUtils.isRandomColor()) {
                    sColor = Utils.generateColorFromString(sName);
                } else {
                    sColor = DbUtils.getAppsColorDefault();
                }
            }
            boolean sFreeze = DbUtils.isAppFrozen(sActivity);
            int sOpeningCount = DbUtils.getOpeningCounts(sActivity);

            mAppsList.add(new Apps(true, uri, sName, getCustomView(), sColor, sSize, false, sFreeze, sOpeningCount));

        }

        // now sort the app list
        sortApps(DbUtils.getSortsTypes());
    }

    /**
     * @param type sorting type
     */
    public void sortApps(final int type) {
        // remove the app view for home layout these needs to be add later after sorting
        mHomeLayout.removeAllViews();

        DbUtils.setAppsSortsType(type);

        //sort the apps alphabetically
        Collections.sort(mAppsList, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getAppName().toString(),
                b.getAppName().toString()
        ));

        switch (type) {
            case SORT_BY_SIZE://descending
                Collections.sort(mAppsList, (apps, t1) -> t1.getSize() - apps.getSize());
                break;
            case SORT_BY_OPENING_COUNTS://descending
                Collections.sort(mAppsList, (apps, t1) -> t1.getOpeningCounts() - apps.getOpeningCounts());
                break;
            case SORT_BY_COLOR:
                Collections.sort(mAppsList, (apps, t1) -> {
                    float[] hsv = new float[3];
                    Color.colorToHSV(apps.getColor(), hsv);
                    float[] another = new float[3];
                    Color.colorToHSV(t1.getColor(), another);

                    for (int i = 0; i < 3; i++) {
                        if (hsv[i] != another[i]) {
                            return (hsv[i] < another[i]) ? -1 : 1;
                        }
                    }
                    return 0;
                });
                break;
            case SORT_BY_CUSTOM:
                // do nothing
                break;
        }

        // now add the app textView to home
        for (Apps apps : mAppsList) {
            mHomeLayout.addView(apps.getTextView(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }

    // increase the size of app and save this to DB
    private void increaseAppSize(String activityName) {
        for (Apps apps : mAppsList) {
            if (apps.getActivityName().equalsIgnoreCase(activityName)) {
                int size = apps.getSize() + 2;
                apps.setSize(size);
                break;
            }
        }
    }

    //  add a new app: generally called after reset
    private void addAppAfterReset(String activityName, boolean sortNeeded) {
        for (Apps apps : mAppsList) {
            if (apps.getActivityName().equalsIgnoreCase(activityName)) {
                mAppsList.remove(apps);
                //now add new App
                int color;
                if (DbUtils.isRandomColor()) {
                    color = Utils.generateColorFromString(activityName);
                } else {
                    color = DbUtils.getAppsColorDefault();
                }
                String appOriginalName = DbUtils.getAppOriginalName(activityName, "");

                String appName = DbUtils.getAppName(activityName, appOriginalName);

                int openingCounts = DbUtils.getOpeningCounts(activityName);
                boolean hide = apps.isHidden();
                boolean freezeSize = apps.isSizeFrozen();

                Apps newApp = new Apps(apps.isShortcut(), activityName, appName, getCustomView(), color, DEFAUTL_TEXT_SIZE_NORMAL_APPS, hide, freezeSize, openingCounts);

                mAppsList.add(newApp);
                if (sortNeeded)
                    sortApps(DbUtils.getSortsTypes());
                break;
            }
        }
    }

    // the text view and set the various parameters
    private AppTextView getCustomView() {
        //  AnimatedTextView textView=new AnimatedTextView(this);
        // textView.setColorSpace(15);
        AppTextView textView = new AppTextView(this);
        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);
        textView.setPadding(10, -6, 4, -2);
        textView.setTypeface(mTypeface);
        return textView;
    }

    // app text is clicked
    // so launch the app
    @Override
    public void onClick(View view) {
        if (view instanceof AppTextView) {
            // get the activity
            AppTextView appTextView = (AppTextView) view;

            if (appTextView.isShortcut()) {
                try {
                    startActivity(Intent.parseUri(appTextView.getUri(), 0));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                String activity = (String) view.getTag();
                String[] strings = activity.split("/");
                try {
                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.setClassName(strings[0], strings[1]);
                    intent.setComponent(new ComponentName(strings[0], strings[1]));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    appOpened(activity);

                    if (!DbUtils.isSizeFrozen() && !DbUtils.isAppFrozen(activity)) {
                        increaseAppSize(activity);
                        if (DbUtils.getSortsTypes() == SORT_BY_SIZE)
                            sortApps(SORT_BY_SIZE);
                    }
                } catch (Exception ignore) {
                    //  Log.e(TAG, "onClick: exception:::" + ignore);
                }
            }
            //Log.d(TAG, "onClick: starting app   ::"+activity);
            // split it into package name and class name
            // bcz activity formatted as com.foo.bar/com.foo.bar.MainActivity

        }
    }

    //show the option on long click
    @Override
    public boolean onLongClick(View view) {
        if (view instanceof AppTextView) {
            // show app setting
            showPopup((String) view.getTag(), (AppTextView) view);
        } else if (view instanceof FlowLayout) {
            // show launcher setting
            new GlobalSettingsDialog(this, this).show();
        }
        return true;
    }

    private void showPopup(String activityName, AppTextView view) {

        Context context;
        // set theme
        // if theme wallpaper ie transparent then we have to show other theme
        if (DbUtils.getTheme() == R.style.Wallpaper)
            context = new ContextThemeWrapper(this, R.style.AppTheme);
        else
            context = new ContextThemeWrapper(this, DbUtils.getTheme());

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

        /*for(int i=1;i<=popupMenu.getMenu().size();i++){
            MenuItem menuItem = popupMenu.getMenu().getItem(i);
            int item = menuItem.getItemId();
            if (item==R.id.menu_hide||item==R.id.menu_uninstall||item==R.id.menu_reset_to_default){

            }
        }
*/

        // set proper item based on Db value
        if (DbUtils.isAppFrozen(activityName)) {
            popupMenu.getMenu().findItem(R.id.menu_freeze_size).setTitle(R.string.unfreeze_size);
        }

        if (view.isShortcut()) {
            popupMenu.getMenu().findItem(R.id.menu_uninstall).setTitle(R.string.remove);
            popupMenu.getMenu().findItem(R.id.menu_hide).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_rename).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_app_info).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_color:
                    changeColorSize(activityName, view);
                    break;
                case R.id.menu_rename:
                    renameApp(activityName, view.getText().toString());
                    break;
                case R.id.menu_freeze_size:
                    freezeAppSize(activityName);
                    break;
                case R.id.menu_hide:
                    hideApp(activityName);
                    break;
                case R.id.menu_uninstall:
                    if (view.isShortcut()) {
                        removeShortcut(view);
                    } else {
                        uninstallApp(activityName);
                    }
                    break;
                case R.id.menu_app_info:
                    showAppInfo(activityName);
                    break;
                case R.id.menu_reset_to_default:
                    resetApp(activityName);
                    break;
                case R.id.menu_reset_color:
                    resetAppColor(activityName);
                    break;

                default:
                    return true;
            }
            return true;
        });
        // not forget to show popup
        popupMenu.show();
    }

    private void removeShortcut(AppTextView view) {
        Log.d(TAG, "removeShortcut: " + view.getUri());
        view.setVisibility(View.GONE);
        DbUtils.removeShortcut(view.getUri());
        loadApps();
    }

    //reset the app color to default color;
    //TODO: port to new textview
    private void resetAppColor(String activityName) {
        DbUtils.removeColor(activityName);
        boolean sortNeeded = (DbUtils.getSortsTypes() == SORT_BY_COLOR);
        addAppAfterReset(activityName, sortNeeded);
    }

    // as method name suggest
    private void freezeAppSize(String activityName) {
        boolean b = DbUtils.isAppFrozen(activityName);
        for (Apps apps : mAppsList) {
            if (activityName.equalsIgnoreCase(apps.getActivityName())) {
                apps.setFreeze(!b);
            }
        }

    }

    // as method name suggest
    private void hideApp(String activityName) {
        for (Apps apps : mAppsList) {
            if (activityName.equalsIgnoreCase(apps.getActivityName())) {
                apps.setAppHidden(true);
            }
        }

    }

    // show the app rename Dialog
    private void renameApp(String activityName, String appName) {
        Dialog dialog = new RenameInputDialogs(this, activityName, appName, this);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }

        dialog.show();

    }

    // this is called by RenameInput.class Dialog when user set the name and sort the apps
    public void onAppRenamed(String activityName, String appNewName) {
        for (Apps app : mAppsList) {
            if (app.getActivityName().equalsIgnoreCase(activityName)) {
                app.setAppName(appNewName.trim());
                if (SORT_BY_NAME == DbUtils.getSortsTypes()) {
                    sortApps(SORT_BY_NAME);
                }
                break;
            }
        }
    }

    // reset the app
    private void resetApp(String activityName) {
        DbUtils.removeAppName(activityName);
        DbUtils.removeColor(activityName);
        DbUtils.removeSize(activityName);
        addAppAfterReset(activityName, true);
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

    //show dialog(i.e a color seek bar) for change color
    private void changeColorSize(String activityName, TextView view) {
        int color = DbUtils.getAppColor(activityName);
        if (color == DbUtils.NULL_TEXT_COLOR) {
            color = view.getCurrentTextColor();
        }

        int size = DbUtils.getAppSize(activityName);
        if (size == DbUtils.NULL_TEXT_SIZE) {
            for (Apps apps : mAppsList) {
                if (apps.getActivityName().equals(activityName)) {
                    size = apps.getSize();
                    break;
                }
            }
        }
        Dialog dialog = new ColorSizeDialog(this, activityName, color, view, size);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }


        dialog.show();
    }


    //TO1DO: multi thread check for memory leaks if any, or check any bad behaviour;
    private void appOpened(String activity) {
       /* new Thread() {
            @Override
            public void run() {
                super.run();*/
        for (Apps apps : mAppsList) {
            if (apps.getActivityName().equalsIgnoreCase(activity)) {
                if (DbUtils.getSortsTypes() == SORT_BY_OPENING_COUNTS) {
                    int counter = apps.getOpeningCounts();
                    if (counter % 5 == 0) {
                        sortApps(SORT_BY_OPENING_COUNTS);
                    }
                }
                apps.increaseOpeningCounts();
            }
        }
        /*   }
        }.start();*/
    }

    @Override
    public void onBackPressed() {
    }

    // register the receiver
    // when new app installed, app updated and app uninstalled launcher have to reflect it
    private void registerForReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        broadcastReceiverAppInstall = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadApps();
            }
        };
        registerReceiver(broadcastReceiverAppInstall, intentFilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
        filter.addAction("com.android.launcher.action.CREATE_SHORTCUT");

        broadcastReceiverShortcutInstall = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent shortcutIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
                String uri = shortcutIntent.toUri(0);
                // check this is already included in list
                if (shortcutIntent.getAction() == null) {
                    shortcutIntent.setAction(Intent.ACTION_VIEW);

                }
                String name = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

                Log.d(TAG, "onReceive: uri:::" + uri);
                Log.d(TAG, "onReceive: name::" + name);
                //TODO: add this persistent
                addShortcut(uri, name);
            }
        };

        registerReceiver(broadcastReceiverShortcutInstall, filter);

    }


    // unregister the receiver on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverAppInstall);
        unregisterReceiver(broadcastReceiverShortcutInstall);
    }

    // request storage permission
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

    // browse the backup file
    public void browseFile() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFile.setType("file/plain");
        Intent intent = Intent.createChooser(chooseFile, this.getString(R.string.choose_old_backup_files));
        startActivityForResult(intent, RESTORE_REQUEST);
    }

    // browse the fonts
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
        // restore request
        if (requestCode == RESTORE_REQUEST) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            try {
                boolean b = DbUtils.loadDbFromFile(cr.openInputStream(uri));
                if (b) {
                    recreate();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // font request
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
            // this handle the request of ColorSniffer app
        } else if (requestCode == COLOR_SNIFFER_REQUEST) {
            //
            //GET DATA FROM COLOR SNIFFER APPS:
            //K,V ??? no
            // bundle yes
            // is it complex: may be
            //get the data
            colorSnifferCall(data.getBundleExtra("color_bundle"));


        }
    }

    //may be override of abstract class method to be called from color sniffer #3 types
    public void colorSnifferCall(Bundle bundle) {
        boolean defaultColorSet = false;// for change set
        int DEFAULT_COLOR = bundle.getInt(DEFAULT_COLOR_FOR_APPS);//keys
        // not set by ColorSniffer
        if (DEFAULT_COLOR != DbUtils.NULL_TEXT_COLOR) { //NULL_TEXT_COLOR=-1
            defaultColorSet = true;// to save cpu cycle
        }

        // get each value as proposed by Color Sniffer App developer
        for (Apps apps : mAppsList) {
            TextView textView = apps.getTextView();
            String appPackage = apps.getActivityName();
            int color = bundle.getInt(appPackage);
            if (color != DbUtils.NULL_TEXT_COLOR) {
                textView.setTextColor(color);
                DbUtils.putAppColorExternalSource(appPackage, color);
                // DbUtils.putAppColor(appPackage, color);
            } else if (defaultColorSet) {
                //set default color
                //TODO:
                DbUtils.putAppColor(appPackage, DEFAULT_COLOR);
                textView.setTextColor(DEFAULT_COLOR);
            }//else do nothing theme default color will apply
        }
    }

    //Clipboard manager
    public Map<String, Integer> clipboardData() {
        Map<String, Integer> result = null;
        Log.d(TAG, "clipboardData: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData.getItemCount() > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    String tabSepratedData = item.getText().toString();
                    Log.d(TAG, "clipboardData: " + tabSepratedData);
                    //validate tabSepratedData and get its data
                    //unique id bae73ae068dacc6cb659d1fb231e7b11 i.e LastLauncher-ColorSniffer MD5-128

                    String[] line = tabSepratedData.split("\n");//get each line

                    Map<String, Integer> colorsAndId = new ArrayMap<>(); // map to put all values in key and values format
                    // iterate over every line
                    for (String entry : line) {
                        String[] activityIdAndColor = entry.split("\t");// split line into id and color
                        int color = Color.parseColor(activityIdAndColor[1]);
                        colorsAndId.put(activityIdAndColor[0], color);// put id and color to map

                        Log.d(TAG, "clipboardData: app:" + activityIdAndColor[0] + "  color==" + color);

                    }
                    setAppsColorFromClipboard(colorsAndId);
                    result = colorsAndId;// return map
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // return empty null/
        return result;
    }

    private void setAppsColorFromClipboard(Map<String, Integer> colorsAndId) {
        if (colorsAndId == null) return;
        DbUtils.externalSourceColor(true);
        for (Apps apps : mAppsList) {
            try {
                TextView textView = apps.getTextView();
                String s = apps.getActivityName().toString();
                Integer newColor = colorsAndId.get(s);
                if (newColor == null) continue;
                textView.setTextColor(newColor);
                DbUtils.putAppColorExternalSource(s, newColor);
            } catch (NullPointerException ignore) {

            }
        }
    }

    // show the hidden app dialog
    public void showHiddenApps() {
        new HiddenAppsDialogs(this, mAppsList).show();
    }

    // show the frozen app dialog
    public void showFrozenApps() {
        new FrozenAppsDialogs(this, mAppsList).show();
    }

    //set the flow layout alignment it is called from global settings
    public void setFlowLayoutAlignment(int gravity) {
        mHomeLayout.setGravity(gravity);
        DbUtils.setFlowLayoutAlignment(gravity);
    }

    public void setPadding() {
        Dialog dialog = new PaddingDialog(this, mHomeLayout);
        // Window window = dialog.getWindow();
        // window.setGravity(Gravity.BOTTOM);
        // window.setBackgroundDrawableResource(android.R.color.transparent);
        // window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialog.show();
    }

    public void setColorsAndSize() {
        Dialog dialog = new GlobalColorSizeDialog(this, mAppsList);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }
        dialog.show();
    }

    private void addShortcut(String uri, String appName) {
        if (mAppsList == null) return;
        mAppsList.add(new Apps(true, uri, appName, getCustomView(), DbUtils.NULL_TEXT_COLOR, DEFAUTL_TEXT_SIZE_NORMAL_APPS, false, false, 0));

        DbUtils.addShortcut(uri, appName);
        sortApps(DbUtils.getSortsTypes());
    }
}