/*
 * Last Launcher
 * Copyright (C) 2022 LIU Tong
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
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.R;

public class ExitDialog extends Dialog implements View.OnClickListener{

    private final LauncherActivity context;

    ExitDialog(Context context, LauncherActivity launcherActivity) {
        super(context);
        this.context = launcherActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_exit);
        LinearLayout ll = findViewById(R.id.exit_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            ll.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.t1) {
            shutdown();
        }
        if (view.getId() == R.id.t2) {
            rollBack();
        }
    }

    private void shutdown() {
        System.exit(0);
    }

    private void rollBack() {
        cancel();
    }
}
