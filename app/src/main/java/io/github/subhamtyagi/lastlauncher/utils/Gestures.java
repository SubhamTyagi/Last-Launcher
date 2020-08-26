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

package io.github.subhamtyagi.lastlauncher.utils;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class Gestures extends GestureDetector.SimpleOnGestureListener {

    private static final int minScrollDistanceX = 50;
    private static final int minScrollDistanceY = 50;

    private final GestureDetector detector;
    private final OnSwipeListener listener;

    public Gestures(Activity context,
                    OnSwipeListener onSwipeListener) {

        this.detector = new GestureDetector(context, this);
        this.listener = onSwipeListener;
    }

    public void onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        boolean consumed = false;
        float absDistanceX = Math.abs(distanceX);
        float absDistanceY = Math.abs(distanceY);
        if (absDistanceX > absDistanceY) {
            // horizontal scroll
            if (absDistanceX > minScrollDistanceX) {
                if (distanceX > 0) {
                    this.listener.onSwipe(Direction.SWIPE_LEFT);
                } else {
                    this.listener.onSwipe(Direction.SWIPE_RIGHT);
                }
                consumed = true;
            }

        } else {
            // vertical scroll
            if (absDistanceY > minScrollDistanceY) {
                if (distanceY > 0) {
                    this.listener.onSwipe(Direction.SWIPE_UP);
                } else {
                    this.listener.onSwipe(Direction.SWIPE_DOWN);
                }
                consumed = true;
            }

        }
        return consumed;
    }

    @Override
    public boolean onDoubleTap(MotionEvent arg) {
        this.listener.onDoubleTap();
        return true;
    }


    public enum Direction {
        SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT
    }

    public interface OnSwipeListener {
        void onSwipe(Direction direction);

        void onDoubleTap();
    }

}