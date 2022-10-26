/*
 * Last Launcher
 * Copyright (C) 2019,2020 Shubham Tyagi
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

import java.util.ArrayList;
import java.util.List;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.model.Apps;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;
import io.github.subhamtyagi.lastlauncher.utils.Utils;
import io.github.subhamtyagi.lastlauncher.views.colorseekbar.ColorSeekBar;

import static io.github.subhamtyagi.lastlauncher.utils.Constants.DEFAULT_MAX_TEXT_SIZE;
import static io.github.subhamtyagi.lastlauncher.utils.Constants.DEFAULT_MIN_TEXT_SIZE;
import static io.github.subhamtyagi.lastlauncher.utils.Constants.DEFAULT_TEXT_SIZE_NORMAL_APPS;
import static io.github.subhamtyagi.lastlauncher.utils.Constants.DEFAULT_TEXT_SIZE_OFTEN_APPS;


public class GlobalColorSizeDialog extends Dialog {

    private static final long DELAY = 100;

    private final Handler handler = new Handler();
    private final List<Apps> mAppsList;
    private final List<String> oftenApps = Utils.getOftenAppsList();
    private Runnable runnable;
    private int appSize;
    private int mColor;

    public GlobalColorSizeDialog(Context context, List<Apps> appsList) {
        super(context);
        this.mAppsList = appsList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // no title please
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_color_size);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        ColorSeekBar colorSeekBar = findViewById(R.id.colorSlider1);
        colorSeekBar.setMaxPosition(100);
        //colorSeekBar.setColorSeeds(R.array.material_colors);
        colorSeekBar.setShowAlphaBar(true);
        colorSeekBar.setBarHeight(8);

        int colorDefault = DbUtils.getAppsColorDefault();

        if (colorDefault != DbUtils.NULL_TEXT_COLOR) {
            colorSeekBar.setColor(colorDefault);
            //} else {
            //do something
        }


        // set the color and save this to database
        colorSeekBar.setOnColorChangeListener((colorBarPosition, alphaBarPosition, color) -> {
            // set the color
            mColor = color;
            synchronized (mAppsList) {
                for (Apps apps : mAppsList) {
                    // only change the color of app, which had not set yet
                    if (DbUtils.getAppColor(apps.getActivityName()) == DbUtils.NULL_TEXT_COLOR) {
                        // change only the text view color
                        // do not save the color of individuals apps
                        apps.getTextView().setTextColor(color);
                    }
                }
            }
            // idea: save global color to Db
        });


        // size related
        TextView plus = findViewById(R.id.btn_plus);
        TextView minus = findViewById(R.id.btn_minus);
        TextView size = findViewById(R.id.tv_size);

        appSize = DbUtils.getGlobalSizeAdditionExtra();
        size.setText(String.valueOf(appSize));

        plus.setOnClickListener(view -> {

            appSize++;

            if (appSize >= DEFAULT_MAX_TEXT_SIZE) {
                appSize = DEFAULT_MAX_TEXT_SIZE;
                //   plus.setClickable(false);

            } else {
                synchronized (mAppsList) {
                    for (Apps apps : mAppsList) {
                        int textSize = DbUtils.getAppSize(apps.getActivityName());
                        // check if text size is null then set the size to default size
                        // size is null(-1) when user installed this app
                        if (textSize == DbUtils.NULL_TEXT_SIZE) {
                            if (oftenApps.contains(apps.getActivityName().split("/")[0])) {
                                textSize = DEFAULT_TEXT_SIZE_OFTEN_APPS;
                            } else {
                                textSize = DEFAULT_TEXT_SIZE_NORMAL_APPS;
                            }
                            /// DbUtils.putAppSize(activity, textSize);
                        }
                        apps.setSize(++textSize);

                    }
                }
            }
            size.setText(String.valueOf(appSize));
        });

        minus.setOnClickListener(view -> {
            --appSize;
            if (appSize < DEFAULT_MIN_TEXT_SIZE) {
                appSize = DEFAULT_MIN_TEXT_SIZE;
            } else {
                synchronized (mAppsList) {
                    for (Apps apps : mAppsList) {
                        int textSize = DbUtils.getAppSize(apps.getActivityName());
                        // check if text size is null then set the size to default size
                        // size is null(-1) when user installed this app
                        if (textSize == DbUtils.NULL_TEXT_SIZE) {
                            if (oftenApps.contains(apps.getActivityName().split("/")[0])) {
                                textSize = DEFAULT_TEXT_SIZE_OFTEN_APPS;
                            } else {
                                textSize = DEFAULT_TEXT_SIZE_NORMAL_APPS;
                            }

                            /// DbUtils.putAppSize(activity, textSize);
                        }
                        apps.setSize(--textSize);
                    }
                }
            }
            size.setText(String.valueOf(appSize));
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
                } else {
                    synchronized (mAppsList) {
                        for (Apps apps : mAppsList) {
                            int textSize = DbUtils.getAppSize(apps.getActivityName());
                            // check if text size is null then set the size to default size
                            // size is null(-1) when user installed this app
                            if (textSize == DbUtils.NULL_TEXT_SIZE) {
                                if (oftenApps.contains(apps.getActivityName().split("/")[0])) {
                                    textSize = DEFAULT_TEXT_SIZE_OFTEN_APPS;
                                } else {
                                    textSize = DEFAULT_TEXT_SIZE_NORMAL_APPS;
                                }

                                /// DbUtils.putAppSize(activity, textSize);
                            }
                            apps.setSize(++textSize);
                        }
                    }
                }
                size.setText(String.valueOf(appSize));
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
                } else {
                    synchronized (mAppsList) {
                        for (Apps apps : mAppsList) {
                            int textSize = DbUtils.getAppSize(apps.getActivityName());
                            // check if text size is null then set the size to default size
                            // size is null(-1) when user installed this app
                            if (textSize == DbUtils.NULL_TEXT_SIZE) {
                                if (oftenApps.contains(apps.getActivityName().split("/")[0])) {
                                    textSize = DEFAULT_TEXT_SIZE_OFTEN_APPS;
                                } else {
                                    textSize = DEFAULT_TEXT_SIZE_NORMAL_APPS;
                                }

                                /// DbUtils.putAppSize(activity, textSize);
                            }
                            apps.setSize(--textSize);
                        }
                    }
                }
                size.setText(String.valueOf(appSize));
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
        DbUtils.setGlobalSizeAdditionExtra(appSize);
        DbUtils.setAppsColorDefault(mColor);
    }

}
