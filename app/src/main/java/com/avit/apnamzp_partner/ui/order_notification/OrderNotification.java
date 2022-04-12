package com.avit.apnamzp_partner.ui.order_notification;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.avit.apnamzp_partner.R;

public class OrderNotification extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.new_order);
        mediaPlayer.setLooping(true);
//        mediaPlayer.start();

    }
}