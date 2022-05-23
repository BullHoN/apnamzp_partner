package com.avit.apnamzp_partner.services.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.avit.apnamzp_partner.HomeActivity;
import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.ui.order_notification.OrderNotification;
import com.avit.apnamzp_partner.utils.NotificationUtil;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

    public static final String CHANNEL_ORDER_ID = "OrdersStatusChannel";
    public static final String CHANNEL_OFFERS_ID = "OffersChannel";
    public static final String CHANNEL_NEWS_ID = "NewsChannel";
    private NotificationManagerCompat notificationManager;
    private String TAG = "NotificationService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.i(TAG, "onNewToken: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationType = remoteMessage.getData().get("type");
        Log.i(TAG, "onMessageReceived: " + notificationType);


        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        createNotificationChannels();

        if(notificationType.equals("new_order")){
            String orderItems = remoteMessage.getData().get("orderItems");
            String orderId = remoteMessage.getData().get("_id");
            String userId = remoteMessage.getData().get("userId");
            String totalAmount = remoteMessage.getData().get("totalPay");

            Log.i(TAG, "onMessageReceived: " + orderItems);
            Log.i(TAG, "onMessageReceived: " + orderId);
            Log.i(TAG, "onMessageReceived: " + userId);
            Log.i(TAG, "onMessageReceived: " + totalAmount);

            handleNewOrderNotification(orderItems,orderId,userId,totalAmount);

        }



    }

    private void handleNewOrderNotification(String orderItems,String orderId,String userId,String totalAmount){

            NotificationUtil.playSound(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(),OrderNotification.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setAction("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION");

            intent.putExtra("orderItems",orderItems);
            intent.putExtra("orderId",orderId);
            intent.putExtra("userId",userId);
            intent.putExtra("totalAmount",totalAmount);

            startActivity(intent);

//            getApplicationContext().sendBroadcast(intent);


//        if(!NotificationUtil.isAppIsInBackground(getApplicationContext())){
//            Log.i(TAG, "handleNotification: in Foreground");
//
//            NotificationUtil.playSound(getApplicationContext());
//            Intent intent = new Intent();
//            intent.setAction("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION");
//
//            intent.putExtra("orderItems",orderItems);
//            intent.putExtra("orderId",orderId);
//            intent.putExtra("userId",userId);
//            intent.putExtra("totalAmount",totalAmount);
//
//            getApplicationContext().sendBroadcast(intent);
//
//        }
//        else {
//            Log.i(TAG, "handleNotification: in Background");
//
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(intent);
//
//            showOrderNotification(orderItems,orderId,userId,totalAmount);
//        }
    }

    private void showOrderNotification(String orderItems,String orderId,String userId,String totalAmount){
        // TODO: set this up

        Intent notificationIntent = new Intent(getApplicationContext(), OrderNotification.class);
        notificationIntent.putExtra("orderItems",orderItems);
        notificationIntent.putExtra("orderId",orderId);
        notificationIntent.putExtra("userId",userId);
        notificationIntent.putExtra("totalAmount",totalAmount);
        notificationIntent.setAction("com.avit.apnamzppartner_newNotificationBackgroundAction");


        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ORDER_ID)
                .setContentTitle("Order title")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText("New Order")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        // play notification custom sound
        NotificationUtil.playSound(getApplicationContext());

        notificationManager.notify(1,notification);
    }

    private void showOffersNotification(){

    }

    private void showNewsNotification(){

    }

    public void createNotificationChannels(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel orderStatusChannel = new NotificationChannel(CHANNEL_ORDER_ID,"Orders Status Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            orderStatusChannel.setDescription("This channel is responsible for all the notification regarding your order status.");

            NotificationChannel offersChannel = new NotificationChannel(CHANNEL_OFFERS_ID,"Offers Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            orderStatusChannel.setDescription("This channel is responsible for all the notification regarding new exclusive offers on ApnaMZP");

            NotificationChannel newsChannel = new NotificationChannel(CHANNEL_NEWS_ID,"News Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            orderStatusChannel.setDescription("This channel is responsible for all the notification regarding what's new and recommendations");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(orderStatusChannel);
            manager.createNotificationChannel(offersChannel);
            manager.createNotificationChannel(newsChannel);

        }
    }


}
