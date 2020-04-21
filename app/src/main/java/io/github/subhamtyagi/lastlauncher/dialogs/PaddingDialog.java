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
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import io.github.subhamtyagi.lastlauncher.R;
import io.github.subhamtyagi.lastlauncher.utils.DbUtils;

public class PaddingDialog extends Dialog implements View.OnLongClickListener, View.OnClickListener {

    private static final long DELAY = 10;

    private static int MAX_PADDING_LEFT = 99;
    private static int MAX_PADDING_RIGHT = 99;
    private static int MAX_PADDING_TOP = 999;
    private static int MAX_PADDING_BOTTOM = 999;

    private static int MIN_PADDING = 0;
    private final FlowLayout homeLayout;

    private final Handler handler = new Handler();

    private TextView left;
    private TextView right;
    private TextView top;
    private TextView bottom;

    private int topInt, leftInt, rightInt, bottomInt;

    private Runnable runnable;

    public PaddingDialog(Context context, FlowLayout mHomeLayout) {
        super(context);
        homeLayout = mHomeLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_padding);

        TextView btnLeftMinus = findViewById(R.id.btn_left_minus);
        btnLeftMinus.setOnLongClickListener(this);
        btnLeftMinus.setOnClickListener(this);

        TextView btnRightMinus = findViewById(R.id.btn_right_minus);
        btnRightMinus.setOnLongClickListener(this);
        btnRightMinus.setOnClickListener(this);

        TextView btnTopMinus = findViewById(R.id.btn_top_minus);
        btnTopMinus.setOnLongClickListener(this);
        btnTopMinus.setOnClickListener(this);

        TextView btnBottomMinus = findViewById(R.id.btn_bottom_minus);
        btnBottomMinus.setOnLongClickListener(this);
        btnBottomMinus.setOnClickListener(this);
        TextView btnLeftPlus = findViewById(R.id.btn_left_plus);
        btnLeftPlus.setOnLongClickListener(this);
        btnLeftPlus.setOnClickListener(this);

        TextView btnRightPlus = findViewById(R.id.btn_right_plus);
        btnRightPlus.setOnLongClickListener(this);
        btnRightPlus.setOnClickListener(this);
        TextView btnTopPlus = findViewById(R.id.btn_top_plus);
        btnTopPlus.setOnLongClickListener(this);
        btnTopPlus.setOnClickListener(this);

        TextView btnBottomPlus = findViewById(R.id.btn_bottom_plus);
        btnBottomPlus.setOnLongClickListener(this);
        btnBottomPlus.setOnClickListener(this);

        left = findViewById(R.id.tv_left_padding);
        right = findViewById(R.id.tv_right_padding);
        top = findViewById(R.id.tv_top_padding);
        bottom = findViewById(R.id.tv_bottom_padding);

        leftInt = DbUtils.getPaddingLeft();
        rightInt = DbUtils.getPaddingRight();
        topInt = DbUtils.getPaddingTop();
        bottomInt = DbUtils.getPaddingBottom();

        // Log.d(TAG, "onCreate: top value::::"+topInt);

        left.setText(String.valueOf(leftInt));
        right.setText(String.valueOf(rightInt));
        top.setText(String.valueOf(topInt));
        bottom.setText(String.valueOf(bottomInt));


    }

    // private static final String TAG = "PaddingDialog";
    @Override
    protected void onStop() {
        super.onStop();
        //Log.d("PaddingDialog", "onStop: values are being saved to db");
        // dialog is about to finish so store the latest values to DB
        DbUtils.setPaddingLeft(leftInt);
        DbUtils.setPaddingRight(rightInt);
        DbUtils.setPaddingTop(topInt);
        DbUtils.setPaddingBottom(bottomInt);
    }

    @Override
    public boolean onLongClick(View button) {
        switch (button.getId()) {
            case R.id.btn_left_minus:
                runner((TextView) button, left, -2, Padding.LEFT);
                break;
            case R.id.btn_left_plus:
                runner((TextView) button, left, 2, Padding.LEFT);
                break;
            case R.id.btn_right_minus:
                runner((TextView) button, right, -2, Padding.RIGHT);
                break;
            case R.id.btn_right_plus:
                runner((TextView) button, right, 2, Padding.RIGHT);
                break;
            case R.id.btn_top_minus:
                runner((TextView) button, top, -2, Padding.TOP);
                break;
            case R.id.btn_top_plus:
                runner((TextView) button, top, 2, Padding.TOP);
                break;
            case R.id.btn_bottom_minus:
                runner((TextView) button, bottom, -2, Padding.BOTTOM);
                break;
            case R.id.btn_bottom_plus:
                runner((TextView) button, bottom, 2, Padding.BOTTOM);
                break;
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left_minus:
                //decrease the value as minus button is pressed
                leftInt--;
                // check the lower limit i.e. 0
                if (leftInt < MIN_PADDING) {
                    leftInt = MIN_PADDING;
                }
                //reflect it on screen
                left.setText(String.valueOf(leftInt));
                break;
            case R.id.btn_left_plus:
                leftInt++;
                // check the upper limit
                if (leftInt > MAX_PADDING_LEFT) {
                    leftInt = MAX_PADDING_LEFT;
                }
                left.setText(String.valueOf(leftInt));
                break;
            case R.id.btn_right_minus:
                rightInt--;
                if (rightInt < MIN_PADDING) {
                    rightInt = MIN_PADDING;
                }
                right.setText(String.valueOf(rightInt));
                break;
            case R.id.btn_right_plus:
                rightInt++;
                if (rightInt > MAX_PADDING_RIGHT) {
                    rightInt = MAX_PADDING_RIGHT;
                }
                right.setText(String.valueOf(rightInt));
                break;
            case R.id.btn_top_minus:
                topInt--;
                if (topInt < MIN_PADDING) {
                    topInt = MIN_PADDING;
                }
                top.setText(String.valueOf(topInt));
                break;
            case R.id.btn_top_plus:
                topInt++;
                if (topInt > MAX_PADDING_TOP) {
                    topInt = MAX_PADDING_TOP;
                }
                top.setText(String.valueOf(topInt));
                break;
            case R.id.btn_bottom_minus:
                bottomInt--;
                if (bottomInt < MIN_PADDING) {
                    bottomInt = MIN_PADDING;
                }
                bottom.setText(String.valueOf(bottomInt));
                break;
            case R.id.btn_bottom_plus:
                bottomInt++;
                if (bottomInt > MAX_PADDING_BOTTOM) {
                    bottomInt = MAX_PADDING_BOTTOM;
                }
                bottom.setText(String.valueOf(bottomInt));
                break;
        }
        // apply all padding to home layout
        homeLayout.setPadding(leftInt, topInt, rightInt, bottomInt);
    }


    /**
     * This runner continuously update the value when button is pressed continuously
     *
     * @param button    which button is being pressed
     * @param view      which text view will be changed or updated
     * @param step      how much we increase or decrease the value -2 means we have to decrease the value by 2
     * @param whichSide to which side we apply padding usually @button @view and this param is interrelated
     */
    private void runner(final TextView button, final TextView view, final int step, Padding whichSide) {
        runnable = () -> {
            if (!button.isPressed()) {
                // button is released so destroy these calls
                handler.removeCallbacks(runnable);
                return;
            }

            switch (whichSide) {
                case LEFT:
                    leftInt += step;
                    // if step is positive means we are increasing the value so we have to check upper limit
                    //else we have to check lower limit i.e. 0
                    if (step > 0) {
                        if (leftInt > MAX_PADDING_LEFT) {
                            leftInt = MAX_PADDING_LEFT;
                        }
                    } else {
                        if (leftInt < MIN_PADDING) {
                            leftInt = MIN_PADDING;
                        }
                    }
                    // reflect this in dialog view
                    view.setText(String.valueOf(leftInt));
                    break;
                case RIGHT:
                    rightInt += step;
                    if (step > 0) {
                        if (rightInt > MAX_PADDING_RIGHT) {
                            rightInt = MAX_PADDING_RIGHT;
                        }
                    } else {
                        if (rightInt < MIN_PADDING) {
                            rightInt = MIN_PADDING;
                        }
                    }
                    view.setText(String.valueOf(rightInt));
                    break;
                case TOP:
                    topInt += step;
                    if (step > 0) {
                        if (topInt > MAX_PADDING_TOP) {
                            topInt = MAX_PADDING_TOP;
                        }
                    } else {
                        if (topInt < MIN_PADDING) {
                            topInt = MIN_PADDING;
                        }
                    }
                    view.setText(String.valueOf(topInt));
                    break;
                case BOTTOM:
                    bottomInt += step;
                    if (step > 0) {
                        if (bottomInt > MAX_PADDING_BOTTOM) {
                            bottomInt = MAX_PADDING_BOTTOM;
                        }
                    } else {
                        if (bottomInt < MIN_PADDING) {
                            bottomInt = MIN_PADDING;
                        }
                    }
                    view.setText(String.valueOf(bottomInt));
                    break;
            }

            // set the padding to home layout
            homeLayout.setPadding(leftInt, topInt, rightInt, bottomInt);
            // currently button is still pressed so again call this runnable
            handler.postDelayed(runnable, DELAY);

        };
        // remove callbacks if any
        handler.removeCallbacks(runnable);
        // first time runner
        handler.postDelayed(runnable, DELAY);
    }

    //enums for better understanding
    private enum Padding {
        LEFT, RIGHT, TOP, BOTTOM
    }

}
