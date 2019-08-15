package io.github.subhamtyagi.lastlauncher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.usage.UsageStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.util.MyUsageStats;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.util.Utility;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "LauncherActivity";

    private static final int RESULT_ACTION_USAGE_ACCESS_SETTINGS = 1;
    private ArrayList<Apps> appsList;

    //private int appsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        loadApps();
        displayAppOnHome();
        registerForReceiver();
        SpUtils.getInstance().init(this).putBoolean(getString(R.string.sp_first_time_app_open), false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //displayAppOnHome();
    }


    private void requestPermission() {
        if (!MyUsageStats.isStatAccessPermissionSet(this)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, RESULT_ACTION_USAGE_ACCESS_SETTINGS);
        }

    }

    private void displayAppOnHome() {
        ViewGroup homeLayout = findViewById(R.id.home_layout);
        //homeLayout.removeAllViews();
        for (Apps apps : appsList) {
            //*LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(this);
            //*tv.setLayoutParams(lparams);
            String name = apps.getAppName().toString();
            tv.setText(name);
            tv.setTag(apps.getPackageName());
            tv.setOnClickListener(this::onClick);
            tv.setOnLongClickListener(this::onLongClick);

            Random rnd = new Random();
            int size = rnd.nextInt(50);
            if (size < 20) size = 20;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Map<String, UsageStats> usageStatsMap = MyUsageStats.getInstance(this).getUsageStats();
                UsageStats usageStats = usageStatsMap.get(((String) apps.getPackageName()).toLowerCase());
                if (usageStats != null) {
                    Log.d(TAG, "displayAppOnHome: App   ===" + apps.getAppName() + ": total time in foreground==" + usageStats.getTotalTimeInForeground());
                    size = Utility.getSize(usageStats.getTotalTimeInForeground());
                }
            }
            tv.setTextSize(size);

            ///int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            Integer color = (rnd.nextInt(7) + 1) * 100;
            tv.setTextColor(Utility.getRandomColor(color.toString(), this));
            homeLayout.addView(tv, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            //* homeLayout.addView(tv);
        }
    }


    //this must be done in background
    private void loadApps() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "Found " + activities.size() + " activities");
        int appsCount = activities.size();

        appsList = new ArrayList<>(appsCount);

        int id = 0;
        for (ResolveInfo resolveInfo : activities) {
            appsList.add(
                    new Apps(++id,
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.loadLabel(pm).toString()
                    )
            );
        }


        Collections.sort(appsList, new Comparator<Apps>() {
            @Override
            public int compare(Apps a, Apps b) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.getAppName().toString(),
                        b.getAppName().toString()
                );
            }
        });
        //send signal to update ui
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


    //Launcher hack
    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View view) {
        String packageName = (String) view.getTag();
        try {
            startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
        } catch (Exception e) {
            //Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        //String packageName= (String) view.getTag();
        //set various setting for this app
        //TODO: Individual App Settings
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ACTION_USAGE_ACCESS_SETTINGS) {
            if (!MyUsageStats.isStatAccessPermissionSet(this)) {
                Log.d(TAG, "onActivityResult: permission is not set");
                if (SpUtils.getInstance().init(this).getBoolean(getString(R.string.sp_request_usage_stat), true))
                    requestPermission();
            } else {
                SpUtils.getInstance().init(LauncherActivity.this).putBoolean(LauncherActivity.this.getString(R.string.sp_request_usage_stat), false);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
