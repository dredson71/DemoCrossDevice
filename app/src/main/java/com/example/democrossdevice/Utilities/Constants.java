package com.example.democrossdevice.Utilities;

public class Constants {
    public static final int MIN_DISTANCE_OFF_BORDERS = 300;
    public static final String PORT = "8080";
    public static final int SHAKE_WAIT_TIME_MS = 250;
    public static final float SHAKE_THRESHOLD = 1.15f;
    public static final int MIN_SWIPE_DISTANCE = 150;
    public static final String CONFIRM_PAIRING = ".CONFIRM_PAIRING";
    public static final String CONFIRM_UNPAIRING = ".CONFIRM_UNPAIRING";
    public static final String SCROLLED_FLIPING = ".CONFIRM_FLIPING";
    public static final String SWIPE_ORIENTATION = ".SWIPE_ORIENTATION";
    public static final long DELAY_DETECT_PINCH = 300000;
    public enum ORIENTATION{
        TOP,
        RIGHT,
        BOTTOM,
        LEFT
    }
}
