package com.avit.apnamzp_partner.services.notification;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.avit.apnamzp_partner.utils.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    private String TAG = "NotificationService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i(TAG, "onNewToken: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(TAG, "onMessageReceived: ");
        handleNotification();
    }

    private void handleNotification(){
        if(!NotificationUtil.isAppIsInBackground(getApplicationContext())){
            Log.i(TAG, "handleNotification: in Foreground");

            Intent intent = new Intent();
            intent.setAction("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION");
            sendBroadcast(intent);

        }
        else {
            Log.i(TAG, "handleNotification: in Background");
        }
    }

}
