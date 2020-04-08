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
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.util.DbUtils;

public class ChooseSize extends Dialog {

    private static final int DELAY = 65;
    // private static final String TAG="ChooseSize";

    //TODO: put the MAX and MIN limit on size of app text
    private final static int DEFAULT_MIN_TEXT_SIZE = DbUtils.getMinAppSize();
    private final static int DEFAULT_MAX_TEXT_SIZE = DbUtils.getMaxAppSize();

    final private String appPackage;
    final private TextView textView;
    private final Handler handler = new Handler();
    private int appSize;
    private Runnable runnable;

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

        TextView plus = findViewById(R.id.btn_plus);
        TextView minus = findViewById(R.id.btn_minus);
        TextView size = findViewById(R.id.tv_size);

        size.setText(String.valueOf(appSize));


        plus.setOnClickListener(view -> {

            appSize++;

            if (appSize >= DEFAULT_MAX_TEXT_SIZE) {
                appSize = DEFAULT_MAX_TEXT_SIZE;
                //   plus.setClickable(false);
            }

            size.setText(String.valueOf(appSize));
            textView.setTextSize(appSize);
        });

        minus.setOnClickListener(view -> {
            --appSize;
            if (appSize < DEFAULT_MIN_TEXT_SIZE) {
                appSize = DEFAULT_MIN_TEXT_SIZE;
            }

            size.setText(String.valueOf(appSize));
            textView.setTextSize(appSize);
        });


        plus.setOnLongClickListener(view -> {
            runnable = () -> {
                if (!plus.isPressed()) {
                    handler.removeCallbacks(runnable);
                    return;
                }


                // increase value
                appSize++;

                if (appSize >= DEFAULT_MAX_TEXT_SIZE) {
                    appSize = DEFAULT_MAX_TEXT_SIZE;
                    //   plus.setClickable(false);
                }

                size.setText(String.valueOf(appSize));
                textView.setTextSize(appSize);
                handler.postDelayed(runnable, DELAY);
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DELAY);
            return true;
        });


        minus.setOnLongClickListener(view -> {
            runnable = () -> {
                if (!minus.isPressed()) {
                    handler.removeCallbacks(runnable);
                    return;
                }
                // decrease value
                --appSize;
                if (appSize < DEFAULT_MIN_TEXT_SIZE) {
                    appSize = DEFAULT_MIN_TEXT_SIZE;
                }

                size.setText(String.valueOf(appSize));
                textView.setTextSize(appSize);
                handler.postDelayed(runnable, DELAY);
            };
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, DELAY);
            return true;
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        DbUtils.putAppSize(appPackage, appSize);
    }
}
