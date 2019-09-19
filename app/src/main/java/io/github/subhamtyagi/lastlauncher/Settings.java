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

package io.github.subhamtyagi.lastlauncher;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rtugeek.android.colorseekbar.ColorSeekBar;

import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.util.Utility;

public class Settings extends Dialog {
    final private String appPackage,appName;
    final private Context context;
    final private int appColor;
    final private int appSize;


    public Settings(Context context, String appPackage, String appName, int appColor, int appSize) {
        super(context);
        this.context = context;
        this.appPackage = appPackage;
        this.appColor = appColor;
        this.appName=appName;
        this.appSize=appSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        TextView textView = findViewById(R.id.tv_example_text);
        textView.setText(appName);
        textView.setTextSize(appSize);
        textView.setTextColor(appColor);
        ColorSeekBar colorSeekBar = findViewById(R.id.colorSlider);
        colorSeekBar.setMaxPosition(100);
        //colorSeekBar.setColorSeeds(R.array.material_colors);
        colorSeekBar.setBarHeight(5);
        colorSeekBar.setColor(appColor);


        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                textView.setTextColor(color);
                SpUtils.getInstance().putInt(Utility.getColorPrefs(appPackage), color);
            }
        });

        SeekBar sizeSeekBar = findViewById(R.id.sb_size);
        sizeSeekBar.setProgress(appSize);

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int size = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                size = i;
                if (i < 20) {
                    size = 20;
                }
                textView.setTextSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sizeSeekBar.setProgress(size);
                SpUtils.getInstance().putInt(Utility.getSizePrefs(appPackage), size);
            }
        });

        /*ColorPicker picker = findViewById(R.id.picker);
        SVBar svBar = findViewById(R.id.svbar);

        picker.addSVBar(svBar);*/

    }

    @Override
    protected void onStop() {
        super.onStop();
        ((LauncherActivity) context).refreshApps(appPackage);
    }
}
