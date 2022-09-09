package com.avit.apnamzp_partner.services.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.avit.apnamzp_partner.HomeActivity;
import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.ui.order_notification.OrderNotification;
import com.avit.apnamzp_partner.utils.NotificationUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class NotificationService extends FirebaseMessagingService {

    public static final String CHANNEL_ORDER_ID = "OrdersStatusChannel";
    public static final String CHANNEL_OFFERS_ID = "OffersChannel";
    public static final String CHANNEL_NEWS_ID = "NewsChannel";
    private NotificationManagerCompat notificationManager;
    private static int NEWS_NOTIFICATION_ID = 1;
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
            String isDeliveryService = remoteMessage.getData().get("isDeliveryService");

            Log.i(TAG, "onMessageReceived: " + orderItems);
            Log.i(TAG, "onMessageReceived: " + orderId);
            Log.i(TAG, "onMessageReceived: " + userId);
            Log.i(TAG, "onMessageReceived: " + totalAmount);
            Log.i(TAG, "onMessageReceived: " + isDeliveryService);

            Gson gson = new Gson();
            ShopItemData orderItemsArray[] = gson.fromJson(orderItems,ShopItemData[].class);
            List<ShopItemData> orderItemsList = Arrays.asList(orderItemsArray);

            OrderItem actionNeededOrderItem = new OrderItem(Integer.valueOf(totalAmount),orderItemsList,userId,orderId);
            actionNeededOrderItem.setIsDeliveryServiceForActionNeededOrders(isDeliveryService.equals("true"));

            LocalDB.saveActionNeededOrder(getApplicationContext(),actionNeededOrderItem, "add");

            handleNewOrderNotification(orderItems,orderId,userId,totalAmount,isDeliveryService);

        }
        else {
            String title = remoteMessage.getData().get("title");
            String desc = remoteMessage.getData().get("desc");
            String image = remoteMessage.getData().get("imageUrl");
            showNewsNotification(title,desc,image);
        }

    }

    private void handleNewOrderNotification(String orderItems,String orderId,String userId,String totalAmount, String isDeliveryService){

            NotificationUtil.playSound(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(),OrderNotification.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION");

            intent.putExtra("orderItems",orderItems);
            intent.putExtra("orderId",orderId);
            intent.putExtra("userId",userId);
            intent.putExtra("totalAmount",totalAmount);
            intent.putExtra("isDeliveryService",isDeliveryService);

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

    private void showNewsNotification(String title, String desc, String imageUrl){
        Intent homeActivityIntent = new Intent(getApplicationContext(),HomeActivity.class);
        homeActivityIntent.setAction("com.avit.apnamzp_news_notification");


        PendingIntent pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, homeActivityIntent, PendingIntent.FLAG_IMMUTABLE);
        }
        else
        {
            pendingIntent =  PendingIntent.getActivity
                    (this,0,homeActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        }


        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_OFFERS_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.removed_bg_main_icon)
                .setContentText(desc)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(desc)
                )
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .build();


        if(imageUrl != null){

            Bitmap bitmap = null;
            try {
                bitmap = Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_OFFERS_ID)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.removed_bg_main_icon)
                    .setContentText(desc)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigLargeIcon(null)
                            .bigPicture(bitmap)
                    )
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)
                    .build();
        }

        if(NEWS_NOTIFICATION_ID > 1073741824){
            NEWS_NOTIFICATION_ID = 0;
        }

        notificationManager.notify(NEWS_NOTIFICATION_ID++,notification);

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
