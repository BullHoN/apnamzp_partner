package com.avit.apnamzp_partner.ui.order_notification;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avit.apnamzp_partner.HomeActivity;
import com.avit.apnamzp_partner.MainActivity;
import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.orders.OrderItemsJsonConversion;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.avit.apnamzp_partner.utils.InfoConstats;
import com.avit.apnamzp_partner.utils.NotificationUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderNotification extends AppCompatActivity {

    private long waitTime = 1000 * 60 * 5;
    private CircularProgressIndicator waitTimeProgressBar;
    private LinearLayout acceptOrderButton, rejectOrderButton, selfPickUpContainer, specialInstructionContainer;
    private TextView reamingTimeTextview, specialInstructionView;
    private int minutes = 4;
    private int seconds = 60;
    private String TAG = "OrderNotifications";
    private Gson gson;
    private ShopItemData[] orderItems;
    private String userId, orderId, totalPay, isDeliveryService, specialInstructions;
    private MaterialButton callCustomerButton;
    private CountDownTimer cancelOrderTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_notification);

        if (!getIntent().getAction().equals("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        if (getIntent() == null || !getIntent().hasExtra("userId")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("NEW ORDER ARRIVED");

        TextView totalPriceView = findViewById(R.id.order_total_price);
        waitTimeProgressBar = findViewById(R.id.remaining_time_progress_bar);
        reamingTimeTextview = findViewById(R.id.remaining_time);
        acceptOrderButton = findViewById(R.id.accept_order_button);
        rejectOrderButton = findViewById(R.id.reject_order_button);
        selfPickUpContainer = findViewById(R.id.self_pickup_container);
        callCustomerButton = findViewById(R.id.call_customer);
        specialInstructionContainer = findViewById(R.id.special_instruction_container);
        specialInstructionView = findViewById(R.id.special_instruction);

        gson = new Gson();
        userId = getIntent().getStringExtra("userId");
        orderId = getIntent().getStringExtra("orderId");
        totalPay = getIntent().getStringExtra("totalAmount");
        isDeliveryService = getIntent().getStringExtra("isDeliveryService");
        orderItems = gson.fromJson(getIntent().getStringExtra("orderItems"), ShopItemData[].class);
        specialInstructions = getIntent().getStringExtra("specialInstructions");

        if(!isDeliveryService.equals("true")){
            selfPickUpContainer.setVisibility(View.VISIBLE);
            callCustomerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callingIntent = new Intent();
                    callingIntent.setAction(Intent.ACTION_DIAL);
                    callingIntent.setData(Uri.parse("tel: " + userId));
                    startActivity(callingIntent);
                }
            });
        }

        totalPriceView.setText("Total ₹" + totalPay);

        if(specialInstructions != null && specialInstructions.length() > 1){
            specialInstructionContainer.setVisibility(View.VISIBLE);
            specialInstructionView.setText(specialInstructions);
        }

        Log.i(TAG, "onCreate: " + getIntent().getStringExtra("orderItems"));
        Log.i(TAG, "onCreate: " + orderItems);
        Log.i(TAG, "onCreate: " + userId);
        Log.i(TAG, "onCreate: " + orderId);

        RecyclerView orderItemsRecyclerView = findViewById(R.id.order_items_recycler_view);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        OrderNotificationItemsAdapter orderNotificationItemsAdapter = new OrderNotificationItemsAdapter(orderItems, getApplicationContext());
        orderItemsRecyclerView.setAdapter(orderNotificationItemsAdapter);

        waitTimeProgressBar.setMax(60 * 5);

        cancelOrderTimer = new CountDownTimer(waitTime, waitTime / (5 * 60)) {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long l) {
                seconds--;
                if (seconds == 0) {
                    minutes--;
                    seconds = 60;
                }
                reamingTimeTextview.setText("Perform Action Within " + minutes + ":" + seconds + " Minutes ");
                waitTimeProgressBar.setProgress(waitTimeProgressBar.getProgress() + 1, true);
            }

            @Override
            public void onFinish() {
                // Cancel Order
                rejectOrder("Shop Didn't Respond");
                Log.i(TAG, "onFinish: cancel order");
            }
        }.start();

        acceptOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderNotification.this);
                View choseExpectedTimeView = getLayoutInflater().inflate(R.layout.dialog_select_expected_time, null);
                alertDialog.setView(choseExpectedTimeView);

                AlertDialog dialog = alertDialog.create();
                dialog.show();

                ChipGroup orderTimeOptions = choseExpectedTimeView.findViewById(R.id.order_time_options);
                orderTimeOptions.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(ChipGroup group, int checkedId) {
                        Log.i(TAG, "onCheckedChanged: ");
                        // Accept the order
                        String reason;
                        switch (checkedId) {
                            case R.id.min15: {
                                reason = "15min";
                                break;
                            }
                            case R.id.min20: {
                                reason = "20min";
                                break;
                            }
                            case R.id.min25: {
                                reason = "25min";
                                break;
                            }
                            case R.id.min30: {
                                reason = "30min";
                                break;
                            }
                            case R.id.min35: {
                                reason = "35min";
                                break;
                            }
                            default: {
                                reason = "60min above";
                            }
                        }

                        acceptOrder(reason);
                        NotificationUtil.stopSound();
                        NotificationUtil.stopVibration();
                        dialog.dismiss();
                    }
                });

            }
        });

        rejectOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderNotification.this);
                View cancelReasonView = getLayoutInflater().inflate(R.layout.dialog_cancel_reason, null);
                alertDialog.setView(cancelReasonView);

                AlertDialog dialog = alertDialog.create();
                dialog.show();

                TextInputEditText rejectReason = cancelReasonView.findViewById(R.id.reject_reason);


                LinearLayout rejectButtn = cancelReasonView.findViewById(R.id.reject_order_button);
                rejectButtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // reject the upcomming order
                        String reason = rejectReason.getText().toString();
                        if (reason.length() < 3) { // TODO: proper validation
                            Toasty.error(getApplicationContext(), "Enter Valid Reason", Toasty.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        rejectOrder(reason);
                        NotificationUtil.stopSound();
                        NotificationUtil.stopVibration();
                        dialog.dismiss();
                    }
                });

            }
        });

    }

    private void acceptOrder(String exptectedTime) {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.acceptOrder(orderId, userId, exptectedTime);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getApplicationContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                NetworkResponse networkResponse = response.body();
                if (networkResponse.getStatus()) {
                    Toasty.success(getApplicationContext(), "Order Accepted Successfully", Toasty.LENGTH_SHORT)
                            .show();
                    // Start Delivery Boy Countdown
                    if(isDeliveryService.equals("true")){
//                        startDeliveryBoySchedular(exptectedTime);
                    }

                    removeActionNeededOrder();
                    cancelOrderTimer.cancel();
                }
                Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(homeActivity);
                finish();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Some error occured", Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: accepting orders", t);
            }
        });

    }

    private void removeActionNeededOrder(){
        OrderItem orderItem = new OrderItem(Integer.valueOf(totalPay), Arrays.asList(orderItems),userId,orderId);
        LocalDB.saveActionNeededOrder(getApplicationContext(),orderItem,"remove");
    }

    private void rejectOrder(String cancelReason) {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.rejectOrder(orderId, userId, cancelReason);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

               if(!response.isSuccessful()){
                   NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                   Toasty.error(getApplicationContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                           .show();
                   return;
               }

                NetworkResponse networkResponse = response.body();
                if (networkResponse.getStatus()) {
                    Toasty.success(getApplicationContext(), "Order Rejected", Toasty.LENGTH_SHORT)
                            .show();
                    cancelOrderTimer.cancel();
                    removeActionNeededOrder();
                    Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeActivity);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Some error occured", Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: rejecting orders", t);
            }
        });
    }

    private void startDeliveryBoySchedular(String totalTime) {
        int waitingTime = Integer.parseInt(totalTime.split("m")[0]) - 15;
        Log.i(TAG, "startDeliveryBoySchedular: waiting time" + waitingTime);

        if (waitingTime <= 0) {
            assignDeliveryBoy();
            return;
        }

        waitingTime = waitingTime * 60 * 1000;

        new CountDownTimer(waitingTime, waitingTime / 2) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: delivery sathi ko dal do");
                assignDeliveryBoy();
            }
        }.start();

    }

    private void assignDeliveryBoy() {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        // TODO: CHANGE THE COORDINATES
        Call<NetworkResponse> assignDBoy = networkApi.assignDeliveryBoy(orderId, "25.133699", "82.564430");
        assignDBoy.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                Toasty.success(getApplicationContext(),"Delivery Boy is Assigned",Toasty.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(), "Please Contact ApnaMzp", Toasty.LENGTH_LONG)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}