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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.ArrayMap;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.io.FileNotFoundException;
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
import io.github.subhamtyagi.lastlauncher.model.Shortcut;
import io.github.subhamtyagi.lastlauncher.utils.CrashUtils;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;
import io.github.subhamtyagi.lastlauncher.utils.Gestures;
import io.github.subhamtyagi.lastlauncher.utils.ShortcutUtils;
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
        View.OnLongClickListener,
        Gestures.OnSwipeListener {

    public static final int COLOR_SNIFFER_REQUEST = 154;
    public final static String DEFAULT_COLOR_FOR_APPS = "default_color_for_apps";
    //various sorting constant
    //why constant? Why not enums for this ?
    // may be lack from Shared Preference DB
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_SIZE = 2;
    public static final int SORT_BY_COLOR = 3;
    public static final int SORT_BY_OPENING_COUNTS = 4;
    public static final int SORT_BY_CUSTOM = 5;
    public static final int SORT_BY_UPDATE_TIME = 6;
    public static final int SORT_BY_RECENT_OPEN = 7;

    private static final int RESTORE_REQUEST = 125;
    private static final int FONTS_REQUEST = 126;
    private static final int PERMISSION_REQUEST = 127;
    private static final int DEFAULT_TEXT_SIZE_NORMAL_APPS = 20;
    private static final int DEFAULT_TEXT_SIZE_OFTEN_APPS = 36;

    public static ArrayList<Apps> mAppsList;
    private static FlowLayout mHomeLayout;

    // when search bar is appear this will be true and show search result
    private static boolean searching = false;
    private static int recentlyUsedCounter = 0;
    private final String TAG = "LauncherActivity";
    private BroadcastReceiver broadcastReceiverAppInstall;
    private BroadcastReceiver broadcastReceiverShortcutInstall;
    private Typeface mTypeface;
    private Dialog dialogs;
    //search box
    private EditText mSearchBox;
    private InputMethodManager imm;
    // gesture detector
    private Gestures detector;

    private ShortcutUtils shortcutUtils;

    private static void showSearchResult(ArrayList<Apps> filteredApps) {
        if (!searching) return;

        mHomeLayout.removeAllViews();
        //mHomeLayout.
        //Log.d(TAG, "showSearchResult: yes search result show ");
        mHomeLayout.setPadding(0, 150, 0, 0);
        /*//sort the apps alphabetically
        Collections.sort(filteredApps, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                a.getAppName(),
                b.getAppName()
        ));*/
        for (Apps apps : filteredApps) {
            mHomeLayout.addView(apps.getTextView(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //pass touch event to detector
        detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the shared prefs may be done in application class
        DbUtils.init(this);
        shortcutUtils = ShortcutUtils.getInstance(this);

        if (BuildConfig.DEBUG) {
            new CrashUtils(getApplicationContext(), "");
        }

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

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //our search box
        mSearchBox = findViewById(R.id.search_box);
        //setup listeners on mSearchBox
        setSearchBoxListeners();

        //set alignment default is center|center_vertical
        mHomeLayout.setGravity(DbUtils.getFlowLayoutAlignment());

        //set padding ..
        mHomeLayout.setPadding(DbUtils.getPaddingLeft(), DbUtils.getPaddingTop(), DbUtils.getPaddingRight(), DbUtils.getPaddingBottom());

        detector = new Gestures(this, this);

        // initGestures();

        // loads the apps
        loadApps();
        // register the receiver for installed, uninstall, update apps and shortcut pwa add
        registerForReceivers();

    }

    private void setSearchBoxListeners() {
        mSearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do here search
                new SearchTask().execute(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

                // do everything
            }
        });

        mSearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mSearchBox.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
                //do something on clicking enter
                return true;
            }
            return false;
        });
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

    public void setFont() {
        // get and set fonts
        String fontsPath = DbUtils.getFonts();
        if (fontsPath == null) {
            mTypeface = Typeface.createFromAsset(getAssets(), "fonts/raleway_bold.ttf");
        } else {
            try {
                mTypeface = Typeface.createFromFile(fontsPath);
            } catch (Exception i) {
                mTypeface = Typeface.createFromAsset(getAssets(), "fonts/raleway_bold.ttf");
            }
        }

    }

    public void loadApps() {
        // get the apps installed on devices;
        Intent startupIntent = new Intent(Intent.ACTION_MAIN, null);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);


        PackageManager pm = getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        // check whether our app list is already initialized if yes then clear this(when new app or shortcut installed)
        if (mAppsList != null) {
            mAppsList.clear();
        }

        // shortcut or pwa counts


        int installedShortcut = shortcutUtils.getShortcutCounts();

        // Log.d(TAG, "loadApps: install shortcut sizes::" + installedShortcut);
        int appsCount = activities.size();

        mAppsList = new ArrayList<>(appsCount + installedShortcut);

        // get the most used apps
        // a list of app that are popular on f-droid and some of my apps
        List<String> oftenApps = Utils.getOftenAppsList();
        List<String> coloredAppsList = Utils.getColoredAppsList();

        String packageName, appName;
        int color, textSize;
        boolean hide;
        // iterate over each app and initialize app list
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
                    textSize = DEFAULT_TEXT_SIZE_OFTEN_APPS;
                } else {
                    textSize = DEFAULT_TEXT_SIZE_NORMAL_APPS;
                }

                /// DbUtils.putAppSize(activity, textSize);
            }

            // get app color
            color = DbUtils.getAppColor(activity);

            // check for default color : set default colors if random color is not set
            if (!DbUtils.isRandomColor() && color == DbUtils.NULL_TEXT_COLOR) {
                color = DbUtils.getAppsColorDefault();
                if (coloredAppsList.contains(packageName)) {
                    color = Utils.getColor();
                }

            }
            // whether app size is frozen
            boolean freeze = DbUtils.isAppFrozen(activity);

            // this is a separate implementation of ColorSniffer app
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

            int updateTime;
            try {
                //ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                long time = pm.getPackageInfo(packageName, 0).lastUpdateTime;
                time = time / 10000;
                updateTime = (int) time;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                updateTime = 0;
            }
            // save all and add this is to app list
            mAppsList.add(new Apps(false, activity, appName, getCustomView(), color, textSize, hide, freeze, openingCounts, updateTime));

        }


        // now adds Shortcut
        // shortcut are stored in DB, android doesn't store them

        ArrayList<Shortcut> shortcuts = shortcutUtils.getAllShortcuts();
        if (shortcuts != null) {
            for (Shortcut s : shortcuts) {

                // shortcut only have URI
                String uri = s.getUri();
                // shortcut name
                String sName = s.getName();

               /* if (uri.isEmpty()) {
                    ++installedShortcut;
                    continue;
                }*/


                // this is the unique code for each uri
                // let store them in activity field app
                // As we have to store some uniquely identified info in Db
                // this be used as key as i have done for Each apps(see above)
                // Usually URI sting is too long and so it will take more memory and storage
                String sActivity = String.valueOf(Utils.hash(uri));

                // get color and size for this shortcut
                int sColor = DbUtils.getAppColor(sActivity);
                int sSize = DbUtils.getAppSize(sActivity);

                if (sSize == DbUtils.NULL_TEXT_SIZE) {
                    sSize = DEFAULT_TEXT_SIZE_NORMAL_APPS;
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

                // add this shortcut to list
                // currently shortcut hide is disabled
                mAppsList.add(new Apps(true, uri, sName, getCustomView(), sColor, sSize, false, sFreeze, sOpeningCount, 0));

            }
        }

        // now sort the app list
        // and display this
        sortApps(DbUtils.getSortsTypes());
    }

    /**
     * @param type sorting type
     */
    public void sortApps(final int type) {
        new SortTask().execute(type);
    }

    // the text view and set the various parameters
    //TODO: new animated field for this(test randomly)
    private AppTextView getCustomView() {
        //  AnimatedTextView textView=new AnimatedTextView(this);
        // textView.setColorSpace(15);
        AppTextView textView = new AppTextView(this);
        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);
        textView.setPadding(10, 0, 4, -2);
        textView.setTypeface(mTypeface);
        return textView;
    }

    // app text is clicked
    // so launch the app
    @Override
    public void onClick(View view) {
        if (view instanceof AppTextView) {

            // get the activity
            String activity = (String) view.getTag();
            AppTextView appTextView = (AppTextView) view;


            if (searching) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
                mSearchBox.setVisibility(View.GONE);
            }

            if (appTextView.isShortcut()) {
                try {
                    Intent intent = Intent.parseUri(appTextView.getUri(), 0);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    appOpened(activity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                //Notes to me:if view store package and component name then this could reduce this splits
                String[] strings = activity.split("/");
                try {
                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                    intent.setClassName(strings[0], strings[1]);
                    intent.setComponent(new ComponentName(strings[0], strings[1]));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    // tell the our db that app is opened
                    appOpened(activity);
                } catch (Exception ignore) {
                    //  Log.e(TAG, "onClick: exception:::" + ignore);
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
        if (searching) {
            mSearchBox.setVisibility(View.GONE);
            searching = false;
            imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
            mHomeLayout.setPadding(DbUtils.getPaddingLeft(), DbUtils.getPaddingTop(), DbUtils.getPaddingRight(), DbUtils.getPaddingBottom());
            sortApps(DbUtils.getSortsTypes());
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
            dialogs = new GlobalSettingsDialog(this, this);
            dialogs.show();
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


        int color = Color.parseColor("#E53935");

        SpannableString s = new SpannableString(getString(R.string.hide));

        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        popupMenu.getMenu().findItem(R.id.menu_hide).setTitle(s);

        s = new SpannableString(getString(R.string.uninstall));
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        popupMenu.getMenu().findItem(R.id.menu_uninstall).setTitle(s);

        s = new SpannableString(getString(R.string.reset_to_default));
        s.setSpan(new ForegroundColorSpan(color), 0, s.length(), 0);
        popupMenu.getMenu().findItem(R.id.menu_reset_to_default).setTitle(s);


        // set proper item based on Db value
        if (DbUtils.isAppFrozen(activityName)) {
            popupMenu.getMenu().findItem(R.id.menu_freeze_size).setTitle(R.string.unfreeze_size);
        }

        //disable some item for shortcut
        // and change the uninstall to remove
        if (view.isShortcut()) {

            SpannableString s1 = new SpannableString(getString(R.string.remove));
            s1.setSpan(new ForegroundColorSpan(Color.parseColor("#E53935")), 0, s1.length(), 0);
            popupMenu.getMenu().findItem(R.id.menu_uninstall).setTitle(s1);

            popupMenu.getMenu().findItem(R.id.menu_hide).setVisible(false);
            //renaming is also disabled;
            // consider it later
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

    //reset the app color to default color;
    private void resetAppColor(String activityName) {
        DbUtils.removeColor(activityName);
        boolean sortNeeded = (DbUtils.getSortsTypes() == SORT_BY_COLOR);
        addAppAfterReset(activityName, sortNeeded);
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
                int appUpdateTime = apps.getUpdateTime();
                Apps newApp = new Apps(apps.isShortcut(), activityName, appName, getCustomView(), color, DEFAULT_TEXT_SIZE_NORMAL_APPS, hide, freezeSize, openingCounts, appUpdateTime);
                mAppsList.add(newApp);
                if (sortNeeded)
                    sortApps(DbUtils.getSortsTypes());
                break;
            }
        }
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
        dialogs = new RenameInputDialogs(this, activityName, appName, this);
        Window window = dialogs.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }

        dialogs.show();

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
        dialogs = new ColorSizeDialog(this, activityName, color, view, size);

        Window window = dialogs.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }


        dialogs.show();
    }

    //TO1DO: multi thread check for memory leaks if any, or check any bad behaviour;
    private void appOpened(String activity) {
        /* new Thread() {
            @Override
            public void run() {
                super.run();*/
        for (Apps apps : mAppsList) {
            if (apps.getActivityName().equalsIgnoreCase(activity)) {
                apps.increaseOpeningCounts();// save to Db that app is opened by user
                recentlyUsedCounter++;
                apps.setRecentUsedWeight(recentlyUsedCounter);

                if (DbUtils.getSortsTypes() == SORT_BY_OPENING_COUNTS) {
                    int counter = apps.getOpeningCounts();
                    if (counter % 5 == 0) {
                        sortApps(SORT_BY_OPENING_COUNTS);
                    }
                } else if (DbUtils.getSortsTypes() == SORT_BY_RECENT_OPEN) {
                    sortApps(SORT_BY_RECENT_OPEN);
                }

                // increase the app view size if not frozen
                if (!DbUtils.isSizeFrozen() && !DbUtils.isAppFrozen(activity)) {
                    int size = DbUtils.getAppSize(activity);
                    size += 2;
                    apps.setSize(size);
                    if (DbUtils.getSortsTypes() == SORT_BY_SIZE) {
                        sortApps(SORT_BY_SIZE);
                    }
                }


                break;
            }
        }
        /*   }
        }.start();*/
    }

    @Override
    public void onBackPressed() {
        mSearchBox.setVisibility(View.GONE);
        if (searching) {
            searching = false;
            mHomeLayout.setPadding(DbUtils.getPaddingLeft(), DbUtils.getPaddingTop(), DbUtils.getPaddingRight(), DbUtils.getPaddingBottom());
            sortApps(DbUtils.getSortsTypes());
        }
    }

    // register the receiver
    // when new app installed, app updated and app uninstalled launcher have to reflect it
    private void registerForReceivers() {
        /*if (broadcastReceiverShortcutInstall!=null){
            unregisterReceiver(broadcastReceiverShortcutInstall);
        }
        if (broadcastReceiverAppInstall!=null){
            unregisterReceiver(broadcastReceiverAppInstall);
        }*/
        //app install and uninstall receiver

        //  Log.d("WTF", "registerForReceivers: called ");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PACKAGE_ADDED);
        intentFilter.addAction(ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        if (broadcastReceiverAppInstall == null) {
            broadcastReceiverAppInstall = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    loadApps();
                }
            };
            registerReceiver(broadcastReceiverAppInstall, intentFilter);
        }

        //shortcut install receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
        filter.addAction("com.android.launcher.action.CREATE_SHORTCUT");

        if (broadcastReceiverShortcutInstall == null) {
            broadcastReceiverShortcutInstall = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Intent shortcutIntent = intent.getParcelableExtra(Intent.EXTRA_SHORTCUT_INTENT);
                    String uri = shortcutIntent.toUri(0);
                    if (shortcutIntent.getAction() == null) {
                        shortcutIntent.setAction(Intent.ACTION_VIEW);

                    }
                    String name = intent.getStringExtra(Intent.EXTRA_SHORTCUT_NAME);

                    if (shortcutUtils.isShortcutToApp(uri)) {
                        return;
                    }

                    if (!shortcutUtils.isShortcutAlreadyAvailable(name)) {
                        addShortcut(uri, name);
                    }
                }
            };
            registerReceiver(broadcastReceiverShortcutInstall, filter);
        }

    }

    // unregister the receivers on destroy
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialogs != null) {
            dialogs.dismiss();
            dialogs = null;
        }
        unregisterReceiver(broadcastReceiverAppInstall);
        unregisterReceiver(broadcastReceiverShortcutInstall);
        broadcastReceiverAppInstall = null;
        broadcastReceiverShortcutInstall = null;
        shortcutUtils.close();
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

        Intent chooseFile;
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        //   chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //}else {
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        // }
        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        //chooseFile.setType("application/x-font-ttf");
        chooseFile.setType("file/plain");
        Intent intent = Intent.createChooser(chooseFile, this.getString(R.string.choose_old_backup_files));
        startActivityForResult(intent, RESTORE_REQUEST);
    }

    // browse the fonts
    public void browseFonts() {
        if (isPermissionRequired()) {
            requestPermission();
        }

        Intent chooseFile;
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
        //    chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // }else {
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        //}

        chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
        //chooseFile.setType("application/x-font-ttf");
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

                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                cursor.close();
                //Log.i(TAG, "onActivityResult: " + path);
                mTypeface = Typeface.createFromFile(path);

                DbUtils.setFonts(path);
                loadApps();
            } catch (Exception i) {
                //i.printStackTrace();

                mTypeface = Typeface.createFromAsset(getAssets(), "fonts/raleway_bold.ttf");
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
                DbUtils.putAppColor(appPackage, DEFAULT_COLOR);
                textView.setTextColor(DEFAULT_COLOR);
            }//else do nothing theme default color will apply
        }
    }

    //Clipboard manager
    public Map<String, Integer> clipboardData() {
        Map<String, Integer> result = null;
        // Log.d(TAG, "clipboardData: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = clipboardManager.getPrimaryClip();
                if (clipData.getItemCount() > 0) {
                    ClipData.Item item = clipData.getItemAt(0);
                    String tabSeparatedData = item.getText().toString();

                    //validate tab Separated Data and get its data
                    //unique id bae73ae068dacc6cb659d1fb231e7b11 i.e LastLauncher-ColorSniffer MD5-128

                    String[] line = tabSeparatedData.split("\n");//get each line

                    Map<String, Integer> colorsAndId = new ArrayMap<>(); // map to put all values in key and values format
                    // iterate over every line
                    for (String entry : line) {
                        String[] activityIdAndColor = entry.split("\t");// split line into id and color
                        int color = Color.parseColor(activityIdAndColor[1]);
                        colorsAndId.put(activityIdAndColor[0], color);// put id and color to map

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
                String s = apps.getActivityName();
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
        dialogs = new HiddenAppsDialogs(this, mAppsList);
        dialogs.show();
    }

    // show the frozen app dialog
    public void showFrozenApps() {
        dialogs = new FrozenAppsDialogs(this, mAppsList);
        dialogs.show();
    }

    //set the flow layout alignment it is called from global settings
    public void setFlowLayoutAlignment(int gravity) {
        mHomeLayout.setGravity(gravity);
        DbUtils.setFlowLayoutAlignment(gravity);
    }

    public void setPadding() {
        dialogs = new PaddingDialog(this, mHomeLayout);
        // Window window = dialogs.getWindow();
        // window.setGravity(Gravity.BOTTOM);
        // window.setBackgroundDrawableResource(android.R.color.transparent);
        // window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        dialogs.show();
    }

    public void setColorsAndSize() {
        dialogs = new GlobalColorSizeDialog(this, mAppsList);

        Window window = dialogs.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        }
        dialogs.show();
    }

    private void addShortcut(String uri, String appName) {
        if (mAppsList == null) return;
        mAppsList.add(new Apps(true, uri, appName, getCustomView(), DbUtils.NULL_TEXT_COLOR, DEFAULT_TEXT_SIZE_NORMAL_APPS, false, false, 0, (int) System.currentTimeMillis() / 1000));
        shortcutUtils.addShortcut(new Shortcut(appName, uri));
        // Log.d(TAG, "addShortcut: shortcut name==" + appName);
        sortApps(DbUtils.getSortsTypes());
    }

    /**
     * remove the shortcut
     *
     * @param view shortcut view to be removed...
     */
    private void removeShortcut(AppTextView view) {
        // view.setVisibility(View.GONE);
        shortcutUtils.removeShortcut(new Shortcut(view.getText().toString(), view.getUri()));

        // if (b)
        loadApps();
    }

    @Override
    public void onSwipe(Gestures.Direction direction) {
        if (direction == Gestures.Direction.SWIPE_RIGHT) {
            searching = true;
            mSearchBox.setText("");
            mSearchBox.setVisibility(View.VISIBLE);
            mSearchBox.requestFocus();
            imm.showSoftInput(mSearchBox, InputMethodManager.SHOW_IMPLICIT);
        } else if (direction == Gestures.Direction.SWIPE_LEFT) {
            if (searching) {
                mSearchBox.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(mSearchBox.getWindowToken(), 0);
                onResume();
            }
        }
    }

    @Override
    public void onDoubleTap() {

    }

    static class SearchTask extends AsyncTask<CharSequence, Void, ArrayList<Apps>> {
        @Override
        protected void onPostExecute(ArrayList<Apps> filteredApps) {
            super.onPostExecute(filteredApps);
            showSearchResult(filteredApps);
        }

        @Override
        protected ArrayList<Apps> doInBackground(CharSequence... charSequences) {
            ArrayList<Apps> filteredApps = new ArrayList<>();
            for (Apps app : mAppsList) {
                if (charSequences[0].length() == 0) {
                    filteredApps.add(app);
                } else if (Utils.simpleFuzzySearch(charSequences[0], app.getAppName())) {
                    filteredApps.add(app);
                }
            }
            return filteredApps;
        }
    }

    static class SortTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHomeLayout.removeAllViews();
            // now add the app textView to home
            // FlowLayoutManager.LayoutParams params = new FlowLayoutManager.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            // params.setNewLine(true);
            for (Apps apps : mAppsList) {
                mHomeLayout.addView(apps.getTextView(), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            }

        }

        @Override
        protected Void doInBackground(Integer... integers) {
            int type = integers[0];
            DbUtils.setAppsSortsType(type);

            //sort the apps alphabetically
            Collections.sort(mAppsList, (a, b) -> String.CASE_INSENSITIVE_ORDER.compare(
                    a.getAppName(),
                    b.getAppName()
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
                case SORT_BY_UPDATE_TIME://descending
                    Collections.sort(mAppsList, (apps, t1) -> t1.getUpdateTime() - apps.getUpdateTime());
                    break;
                case SORT_BY_RECENT_OPEN://descending
                    Collections.sort(mAppsList, (apps, t1) -> (t1.getRecentUsedWeight() - apps.getRecentUsedWeight()));
                    break;
            }
            return null;
        }
    }
}