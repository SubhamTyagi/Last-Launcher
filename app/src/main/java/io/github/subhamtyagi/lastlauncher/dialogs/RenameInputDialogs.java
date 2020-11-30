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
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;

public class RenameInputDialogs extends Dialog implements TextView.OnEditorActionListener {

    final private String appPackage;
    private final LauncherActivity launcherActivity;
    private final String oldAppName;


    private EditText mAppName;

    public RenameInputDialogs(Context context, String appPackage, String oldAppName, LauncherActivity launcherActivity) {
        super(context);
        this.appPackage = appPackage;
        this.launcherActivity = launcherActivity;
        this.oldAppName = oldAppName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_rename_input);
        mAppName = findViewById(R.id.ed_input);
        mAppName.setText(oldAppName);
        mAppName.setOnEditorActionListener(this);
        mAppName.setEnabled(true);
        mAppName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    @Override
    public boolean onEditorAction(TextView tv, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (i == EditorInfo.IME_ACTION_DONE) {
            String temp = mAppName.getText().toString();
            if (!temp.isEmpty()) {
                DbUtils.putAppName(appPackage, temp);
                //reflect this on screen immediately
                launcherActivity.onAppRenamed(appPackage, temp);
                cancel();
            }
            handled = true;
        }
        return handled;
    }
}
