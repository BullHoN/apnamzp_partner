package com.avit.apnamzp_partner.ui.order_notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.orders.OrderItemsJsonConversion;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.utils.NotificationUtil;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OrderNotification extends AppCompatActivity {

    private long waitTime = 1000 * 60 * 5;
    private CircularProgressIndicator waitTimeProgressBar;
    private LinearLayout acceptOrderButton,rejectOrderButton;
    private TextView reamingTimeTextview;
    private int minutes = 4;
    private int seconds = 60;
    private String TAG = "OrderNotifications";
    private Gson gson;
    private ShopItemData[] orderItems;
    private String userId,orderId,totalPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("NEW ORDER ARRIVED");

        waitTimeProgressBar = findViewById(R.id.remaining_time_progress_bar);
        reamingTimeTextview = findViewById(R.id.remaining_time);
        acceptOrderButton = findViewById(R.id.accept_order_button);
        rejectOrderButton = findViewById(R.id.reject_order_button);

        NotificationUtil.stopSound();

        gson = new Gson();
        userId = getIntent().getStringExtra("userId");
        orderId = getIntent().getStringExtra("orderId");
        totalPay = getIntent().getStringExtra("totalAmount");
        orderItems = gson.fromJson(getIntent().getStringExtra("orderItems"), ShopItemData[].class);

        Log.i(TAG, "onCreate: " + getIntent().getStringExtra("orderItems"));
        Log.i(TAG, "onCreate: " + orderItems);
        Log.i(TAG, "onCreate: " + userId);
        Log.i(TAG, "onCreate: " + orderId);

        RecyclerView orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        OrderNotificationItemsAdapter orderNotificationItemsAdapter = new OrderNotificationItemsAdapter(orderItems,getApplicationContext());
        orderItemsRecyclerView.setAdapter(orderNotificationItemsAdapter);

        waitTimeProgressBar.setMax(60*5);

        new CountDownTimer(waitTime,waitTime/(5*60)){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long l) {
                seconds--;
                if(seconds == 0){
                    minutes--;
                    seconds = 60;
                }
                reamingTimeTextview.setText("Perform Action Within " + minutes + ":" + seconds + " Minutes ");
                waitTimeProgressBar.setProgress(waitTimeProgressBar.getProgress() + 1,true);
            }

            @Override
            public void onFinish() {
                // TODO: Cancel Order
                Log.i(TAG, "onFinish: cancel order");
            }
        }.start();

        acceptOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderNotification.this);
                View choseExpectedTimeView = getLayoutInflater().inflate(R.layout.dialog_select_expected_time,null);
                alertDialog.setView(choseExpectedTimeView);

                AlertDialog dialog = alertDialog.create();
                dialog.show();

                ChipGroup orderTimeOptions = choseExpectedTimeView.findViewById(R.id.order_time_options);
                orderTimeOptions.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(ChipGroup group, int checkedId) {
                        Log.i(TAG, "onCheckedChanged: ");
                        // TODO: Accept the order
                        dialog.dismiss();
                    }
                });

            }
        });

        rejectOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderNotification.this);
                View cancelReasonView = getLayoutInflater().inflate(R.layout.dialog_cancel_reason,null);
                alertDialog.setView(cancelReasonView);

                AlertDialog dialog = alertDialog.create();
                dialog.show();

                TextInputEditText rejectReason = cancelReasonView.findViewById(R.id.reject_reason);


                LinearLayout rejectButtn =  cancelReasonView.findViewById(R.id.reject_order_button);
                rejectButtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO reject the upcomming order
                        String reason = rejectReason.getText().toString();
                        if(reason.length() < 3){ // TODO: proper validation
                            Toasty.error(getApplicationContext(),"Enter Valid Reason",Toasty.LENGTH_LONG)
                                    .show();
                            return;
                        }
                        dialog.dismiss();
                    }
                });

            }
        });

    }

    private void acceptOrder(){

    }

    private void rejectOrder(){

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}