package io.github.subhamtyagi.lastlauncher;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
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
import java.util.Random;

import io.github.subhamtyagi.lastlauncher.model.Apps;

import static android.content.Intent.ACTION_PACKAGE_ADDED;
import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_PACKAGE_REPLACED;

public class LauncherActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "LauncherActivity";

    private ArrayList<Apps> appsList;
    private int appsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        loadApps();
        displayAppOnHome();
        registerForReceiver();

    }

    private void displayAppOnHome() {
        ViewGroup homeLayout = findViewById(R.id.home_layout);
        for (Apps apps : appsList) {
            //*LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(this);
            //*tv.setLayoutParams(lparams);
            String name = apps.getAppName() + " ";
            tv.setText(name);
            tv.setTag((String) apps.getPackageName());
            tv.setOnClickListener(this::onClick);
            tv.setOnLongClickListener(this::onLongClick);

            //this will be implemented soon with user defined values in db
            //##############################################################
            //TODO: Color and Size
            Random rnd = new Random();
            int size = rnd.nextInt(50);
            if (size < 20) size = 20;
            tv.setTextSize(size);
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            tv.setTextColor(color);
            //##################################################################

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
        appsCount = activities.size();

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
}
