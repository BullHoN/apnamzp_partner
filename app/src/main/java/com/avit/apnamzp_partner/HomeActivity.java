package com.avit.apnamzp_partner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.ui.order_notification.OrderNotification;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.net.NetPermission;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavController navController;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseCrashlytics.getInstance().setUserId(LocalDB.getPartnerDetails(getApplicationContext()).getPhoneNo());

//        Intent intent = new Intent(getApplicationContext(),OrderNotification.class);
//        startActivity(intent);

        // Set a Toolbar to replace the ActionBar.
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this,navController,drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(!task.isSuccessful()){
                            Log.i(TAG, "FCM failed");
                        }

                        String token = task.getResult();
                        Log.i("NotificationService", "onComplete: " + token);

                        ShopPartner shopPartner = LocalDB.getPartnerDetails(getApplicationContext());
                        if(shopPartner.getFcmId() == null || shopPartner.getFcmId() != token){
                            shopPartner.setFcmId(token);
                            LocalDB.savePartnerDetails(getApplicationContext(),shopPartner);
                            updateFCMID(shopPartner);
                        }

                    }
                });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == "com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION") {
                    Log.i("NotificationService", "onReceive: Broadcast receiver");
                    Intent orderNotificationActivityIntent = new Intent(getApplicationContext(), OrderNotification.class);
                    startActivity(orderNotificationActivityIntent);
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION");

        Dexter.withContext(getApplicationContext())
                .withPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }

    private void updateFCMID(ShopPartner shopPartner) {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.updateFcmToken(shopPartner);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getApplicationContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                Log.i(TAG, "onResponse: updated");

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error while updating fcmID",t);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver,intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController,drawerLayout);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }
}