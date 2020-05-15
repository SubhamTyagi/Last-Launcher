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
import android.content.res.Resources;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class Gestures extends GestureDetector.SimpleOnGestureListener {

    public final static int MODE_SOLID = 1;
    public final static int MODE_DYNAMIC = 2;

    private final static int ACTION_FAKE = -13; // just an unlikely number
    private int mode = MODE_DYNAMIC;
    private boolean running = true;

    private static int swipeMinVelocity;
    private static int swipeMinVelocityRightLeft;
    private static int swipeMinDistance = getSwipeMinDistance();
    private static int swipeMinDistanceRightLeft = getSwipeMinDistanceRightLeft();





    private boolean tapIndicator = false;
    private Activity context;
    private GestureDetector detector;
    private OnSwipeListener listener;

    public Gestures(Activity context,
                    OnSwipeListener onSwipeListener) {

        this.context = context;
        this.detector = new GestureDetector(context, this);
        this.listener = onSwipeListener;
    }

    public void onTouchEvent(MotionEvent event) {

        if (!this.running)
            return;

        boolean result = this.detector.onTouchEvent(event);
        // Get the gesture
        if (this.mode == MODE_SOLID)
            event.setAction(MotionEvent.ACTION_CANCEL);
        else if (this.mode == MODE_DYNAMIC) {
            if (event.getAction() == ACTION_FAKE)
                event.setAction(MotionEvent.ACTION_UP);
            else if (result)
                event.setAction(MotionEvent.ACTION_CANCEL);
            else if (this.tapIndicator) {
                event.setAction(MotionEvent.ACTION_DOWN);
                this.tapIndicator = false;
            }

        }
        // else just do nothing, it's Transparent
    }

    private static int getSwipeMinDistanceRightLeft() {
        return Resources.getSystem().getDisplayMetrics().widthPixels / 2;
    }

    private static int getSwipeMinDistance() {
        int x = Resources.getSystem().getDisplayMetrics().heightPixels;

        if (x > 1900) {
            swipeMinVelocity = 800;
            return x / 3;//33%
        } else if (x > 1200) {
            swipeMinVelocity = 975;
            return x * 2 / 5;//40%
        } else {
            swipeMinVelocity = 1100;
            return x / 2; //50%
        }

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        final float xDistance = Math.abs(e1.getX() - e2.getX());
        final float yDistance = Math.abs(e1.getY() - e2.getY());

       /* if (xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance)
            return false;
*/
        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);

        Log.d("Gesture", "onFling: velocity==" + velocityY);
        Log.d("Gesture", "onFling: min velocity==" + swipeMinVelocity);

        boolean result = false;



        if (velocityX > swipeMinVelocityRightLeft && xDistance > swipeMinDistanceRightLeft) {
            if (e1.getX() > e2.getX()) { // right to left
                this.listener.onSwipe(Direction.SWIPE_LEFT);
            } else {
                this.listener.onSwipe(Direction.SWIPE_RIGHT);
            }
            result = true;

        } else if (velocityY > swipeMinVelocity && yDistance > swipeMinDistance) {
            if (e1.getY() > e2.getY()) { // bottom to up
                this.listener.onSwipe(Direction.SWIPE_UP);
            } else {
                this.listener.onSwipe(Direction.SWIPE_DOWN);
            }
            result = true;
        }

        return result;
        // return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        this.tapIndicator = true;
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent arg) {
        this.listener.onDoubleTap();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent arg) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent arg) {

        if (this.mode == MODE_DYNAMIC) { // we owe an ACTION_UP, so we fake an
            arg.setAction(ACTION_FAKE); // action which will be converted to an
            // ACTION_UP later.
            this.context.dispatchTouchEvent(arg);
        }

        return false;
    }

    public enum Direction {
        SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT
    }

    public interface OnSwipeListener {
        void onSwipe(Direction direction);

        void onDoubleTap();
    }

}