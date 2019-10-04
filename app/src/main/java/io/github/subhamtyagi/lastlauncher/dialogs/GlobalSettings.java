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
import android.widget.Toast;

import io.github.subhamtyagi.lastlauncher.R;

public class GlobalSettings extends Dialog implements View.OnClickListener {


    public GlobalSettings(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_global_settings);
        findViewById(R.id.settings_fonts).setOnClickListener(this);
        findViewById(R.id.settings_bg_color).setOnClickListener(this);
        findViewById(R.id.settings_reset_to_defaults).setOnClickListener(this);
        findViewById(R.id.settings_backup).setOnClickListener(this);
        findViewById(R.id.settings_primary_color).setOnClickListener(this);
        findViewById(R.id.settings_setup_random_colors).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_fonts:
                setFonts();
                break;
            case R.id.settings_bg_color:
                bgColor();
                break;
            case R.id.settings_backup:
                backup();
                break;
            case R.id.settings_primary_color:
                setPrimaryColor();
                break;
            case R.id.settings_reset_to_defaults:
                defaultSettings();
                break;
            case R.id.settings_setup_random_colors:
                randomColor();
                break;
        }
    }

    private void randomColor() {
    }

    private void bgColor() {
    }

    private void defaultSettings() {
    }

    private void setPrimaryColor() {
    }

    private void backup() {

    }
    private void setFonts() {
        Toast.makeText(getContext(), "Not implemnted yet", Toast.LENGTH_SHORT).show();
    }
}
