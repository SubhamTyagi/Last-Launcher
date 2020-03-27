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

package io.github.subhamtyagi.lastlauncher.views.textview;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

import io.github.subhamtyagi.lastlauncher.R;

/**
 * RainbowTextView
 * Created by hanks on 2017/3/14.
 */

public class AnimatedTextView extends TextView {

    private Matrix mMatrix;
    private float mTranslate;
    private float colorSpeed;
    private float colorSpace;

    private int[] colors = {0xFFFF2B22, 0xFFFF7F22, 0xFFEDFF22, 0xFF22FF22, 0xFF22F4FF, 0xFF2239FF, 0xFF5400F7};

    private LinearGradient mLinearGradient;

    public AnimatedTextView(Context context) {
        this(context, null);
    }

    public AnimatedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimatedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }


    private void init(AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AnimatedTextView);
        colorSpace = typedArray.getDimension(R.styleable.AnimatedTextView_colorSpace, dp2px(150));
        colorSpeed = typedArray.getDimension(R.styleable.AnimatedTextView_colorSpeed, dp2px(5));
        typedArray.recycle();

        mMatrix = new Matrix();
        initPaint();
    }

    public float getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(float colorSpace) {
        this.colorSpace = colorSpace;
    }

    public float getColorSpeed() {
        return colorSpeed;
    }

    public void setColorSpeed(float colorSpeed) {
        this.colorSpeed = colorSpeed;
    }

    public void setColors(int... colors) {
        this.colors = colors;
        initPaint();
    }

    private void initPaint() {
        mLinearGradient = new LinearGradient(0, 0, colorSpace, 0, colors, null, Shader.TileMode.MIRROR);
        getPaint().setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mTranslate += colorSpeed;
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);
        super.onDraw(canvas);
        postInvalidateDelayed(100);
    }

    public static int dp2px(float dp) {
        return Math.round(dp * getDisplayMetrics().density);
    }

    public static DisplayMetrics getDisplayMetrics() {
        // Math.round(150* Resources.getSystem().getDisplayMetrics().density);
        return Resources.getSystem().getDisplayMetrics();
    }


}