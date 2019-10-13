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

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;

public class ChooseSize extends AppCompatDialog {
    final private String appPackage;
    final private int appSize;
    final private TextView textView;


    public ChooseSize(Context context, String appPackage, int appSize, TextView textView) {
        super(context);
        this.appPackage = appPackage;
        this.appSize = appSize;
        this.textView = textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_choose_size);

        SeekBar sizeSeekBar = findViewById(R.id.sb_size);
        sizeSeekBar.setProgress(appSize);
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int size = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                size = i;
                if (i < 15) {
                    size = 15;
                }
                textView.setTextSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sizeSeekBar.setProgress(size);
                DbUtils.putAppSize(appPackage, size);
                //no need to change in model App
            }
        });
    }

}
