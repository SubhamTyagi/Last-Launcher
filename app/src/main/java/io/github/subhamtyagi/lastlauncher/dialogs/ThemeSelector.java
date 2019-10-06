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
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import io.github.subhamtyagi.lastlauncher.LauncherActivity;
import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;

public class ThemeSelector extends Dialog implements View.OnClickListener {

    LauncherActivity context;

    public ThemeSelector(Context context, LauncherActivity launcherActivity) {
        super(context);
        this.context = launcherActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_theme_selector);
        LinearLayout ll = findViewById(R.id.theme_linear_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            ll.getChildAt(i).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.t1:
                setTheme(R.style.AppTheme);
                break;
            case R.id.t2:
                setTheme(R.style.Wallpaper);

                break;
            case R.id.t3:
                setTheme(R.style.Black);

                break;
            case R.id.t4:
                setTheme(R.style.White);
                break;
            case R.id.t5:
                setTheme(R.style.BlackonGrey);
                break;
            case R.id.t6:
                setTheme(R.style.A);
                break;
            case R.id.t7:
                setTheme(R.style.B);

                break;
            case R.id.t8:
                setTheme(R.style.C);

                break;
            case R.id.t9:
                setTheme(R.style.D);
                break;
            case R.id.t10:
                setTheme(R.style.E);

                break;
            case R.id.t11:
                setTheme(R.style.F);
                break;
            case R.id.t12:
                setTheme(R.style.G);

                break;
            case R.id.t13:
                setTheme(R.style.H);

                break;
            case R.id.t14:
                setTheme(R.style.I);

                break;
            case R.id.t15:
                setTheme(R.style.J);

                break;
            case R.id.t16:
                setTheme(R.style.K);
                break;
            case R.id.t17:
                setTheme(R.style.L);

                break;
            case R.id.t18:
                setTheme(R.style.M);

                break;
            case R.id.t19:
                setTheme(R.style.N);

                break;
            case R.id.t20:
                setTheme(R.style.O);

                break;
            case R.id.t21:
                setTheme(R.style.P);

                break;
            case R.id.t22:
                setTheme(R.style.Q);

                break;
            case R.id.t23:
                setTheme(R.style.R);

                break;
            case R.id.t24:
                setTheme(R.style.S);

                break;
            case R.id.t25:
                setTheme(R.style.T);

                break;
            case R.id.t26:
                setTheme(R.style.U);

                break;
            case R.id.t27:
                setTheme(R.style.V);

                break;
            case R.id.t28:
                setTheme(R.style.W);

                break;
            case R.id.t29:
                setTheme(R.style.X);

                break;
            case R.id.t30:
                setTheme(R.style.Y);

                break;
            case R.id.t31:
                setTheme(R.style.Z);

                break;
            case R.id.t32:
                setTheme(R.style.AA);

                break;
            case R.id.t33:
                setTheme(R.style.AB);

                break;
            case R.id.t34:
                setTheme(R.style.AC);

                break;
            case R.id.t35:
                setTheme(R.style.Hacker_green);

                break;
            case R.id.t36:
                setTheme(R.style.Hacker_red);

                break;
        }
    }

    private void setTheme(int appTheme) {
        DbUtils.setTheme(appTheme);
        cancel();
        context.recreate();
    }
}
