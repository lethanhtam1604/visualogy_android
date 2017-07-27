package com.visualogyx.manager;

import android.content.Context;
import android.graphics.Bitmap;

import com.visualogyx.interfaceUI.BackToCamera;
import com.visualogyx.model.PipeType;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Global {

    public static Context context;
    public static SettingsManager settingsManager;
    public static float screenRatio;

    public static int screenWidth;
    public static int screenHeight;

    public static String currentPathVideo = "";
    public static String currentPicturePath = "";

    public static boolean isRecordVideo = false;
    public static boolean isCanRotateOrientation = false;

    public static boolean sendForTraining = false;
    public static PipeType pipeType;
    public static Bitmap currentBitmap;

    public static int mOrientation =  -1;

    public static final int ORIENTATION_PORTRAIT_NORMAL =  1;
    public static final int ORIENTATION_PORTRAIT_INVERTED =  2;
    public static final int ORIENTATION_LANDSCAPE_NORMAL =  3;
    public static final int ORIENTATION_LANDSCAPE_INVERTED =  4;
    public static BackToCamera backToCamera;

    public static String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}
