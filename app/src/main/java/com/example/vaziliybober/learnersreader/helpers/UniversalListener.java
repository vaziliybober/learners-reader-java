package com.example.vaziliybober.learnersreader.helpers;


import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class UniversalListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;

    public UniversalListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void onSwipeLeft(){}
    public void onSwipeRight(){}
    public void onSwipeUp(){}
    public void onSwipeDown(){}

    public void onTouch(MotionEvent e) {}


    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        onTouch(event);
        return true;
    }






    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int MIN_SWIPE_DISTANCE = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > MIN_SWIPE_DISTANCE  && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }

            if (Math.abs(distanceX) < Math.abs(distanceY) && Math.abs(distanceY) > MIN_SWIPE_DISTANCE && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceY < 0)
                    onSwipeUp();
                else
                    onSwipeDown();
                return true;
            }

            return false;
        }
    }
}
