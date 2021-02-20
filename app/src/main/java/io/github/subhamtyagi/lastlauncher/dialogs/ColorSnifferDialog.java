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

package io.github.subhamtyagi.lastlauncher.dialogs;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.utils.Constants;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;


// color sniffer dialog
// may be removed from stable version
//Color sniffer developer halted the development of that app!!
public class ColorSnifferDialog extends Dialog implements View.OnClickListener {
    private final LauncherActivity launcherActivity;
    private final Context context;
    private TextView onOffSwitch;
    private TextView mStartColorSniffer;
    private boolean change = false;

    ColorSnifferDialog(Context context, LauncherActivity launcherActivity) {
        super(context);
        this.context = context;
        this.launcherActivity = launcherActivity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_color_sniffer_on_off:
                onOffColorSnifferCustomisation();
                break;
            case R.id.color_sniffer_clipboard:
                launcherActivity.clipboardData();
                break;
            case R.id.color_sniffer_external_app: {
                if (DbUtils.isExternalSourceColor())
                    startColorSnifferApp();

                break;
            }
        }
    }

    private void onOffColorSnifferCustomisation() {
        boolean b = DbUtils.isExternalSourceColor();
        DbUtils.externalSourceColor(!b);
        onOffSwitch.setText(!b ? R.string.on : R.string.off);
        change = !change;
        mStartColorSniffer.setVisibility(!b ? View.VISIBLE : View.GONE);
    }


    //TODO: uri update, data schema
    private void startColorSnifferApp() {
        //check app android compat currently colorSniffer api=19 and this app api=14
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            //is this correct call Rui Zhao?
            intent.setComponent(new ComponentName("ryey.colorsniffer", "ryey.colorsniffer.FormActivity"));
            //currently default color is only provided by Theme:
            //Is it required to send default colors of apps : YES
            // is it required/ to send theme related data for better experience : ask for color sniffer developer
            // 2121= dummy value
            intent.putExtra(Constants.DEFAULT_COLOR_FOR_APPS, 2121);
            launcherActivity.startActivityForResult(intent, Constants.COLOR_SNIFFER_REQUEST);
            // for activity result see LauncherActivity line 509
            cancel();
        } catch (ActivityNotFoundException e) {
            //this will never happen because this option is only shown after app is installed
            Uri uri = Uri.parse("market://details?id=ryey.colorsniffer");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_color_sniffer_settings);
        findViewById(R.id.text_color_sniffer_on_off).setOnClickListener(this);
        findViewById(R.id.color_sniffer_clipboard).setOnClickListener(this);
        mStartColorSniffer = findViewById(R.id.color_sniffer_external_app);
        mStartColorSniffer.setOnClickListener(this);
        onOffSwitch = findViewById(R.id.switch_color_sniffer_on_off);
        boolean onOff = DbUtils.isExternalSourceColor();
        onOffSwitch.setText(onOff ? R.string.on : R.string.off);
        mStartColorSniffer.setVisibility(onOff ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //else Window decor leak happen
        if (change) {
            launcherActivity.recreate();
        }

    }
}
