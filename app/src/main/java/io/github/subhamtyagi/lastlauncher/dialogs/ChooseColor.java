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
import android.view.Window;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;
import io.github.subhamtyagi.lastlauncher.util.SpUtils;
import io.github.subhamtyagi.lastlauncher.views.colorseekbar.ColorSeekBar;

public class ChooseColor extends Dialog {

    final private String appPackage;
    final private int appColor;
    final private TextView textView;

    public ChooseColor(Context context, String appPackage, int appColor, TextView textView) {
        super(context);
        this.appPackage = appPackage;
        this.appColor = appColor;
        this.textView = textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_color);


        ColorSeekBar colorSeekBar = findViewById(R.id.colorSlider1);
        colorSeekBar.setMaxPosition(100);
        //colorSeekBar.setColorSeeds(R.array.material_colors);
        colorSeekBar.setShowAlphaBar(true);
        colorSeekBar.setBarHeight(5);
        colorSeekBar.setColor(appColor);

        colorSeekBar.setOnColorChangeListener(new ColorSeekBar.OnColorChangeListener() {
            @Override
            public void onColorChangeListener(int colorBarPosition, int alphaBarPosition, int color) {
                textView.setTextColor(color);
                DbUtils.putAppColor(appPackage,color);
            }
        });

    }
}
