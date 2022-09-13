package com.avit.apnamzp_partner.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.avit.apnamzp_partner.R;

import java.io.IOException;
import java.util.List;

public class NotificationUtil {

    private static MediaPlayer mediaPlayer;
    private static Vibrator vibrator;

    // method checks if the app is in background or not
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static void playSound(Context context){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(context, R.raw.new_order);
            mediaPlayer.setLooping(true);
        }

        mediaPlayer.start();

    }


    public static void startVibration(Context context){
        if(vibrator == null){
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        long[] pattern = {500, 800};

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM) //key
                    .build();

            vibrator.cancel();
            vibrator.vibrate(pattern,1,audioAttributes);

    }

    public static void stopVibration(){
        if(vibrator == null) return;
        vibrator.cancel();
    }

    public static void stopSound(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
