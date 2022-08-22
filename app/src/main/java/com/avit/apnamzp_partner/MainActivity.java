package com.avit.apnamzp_partner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.avit.apnamzp_partner.auth.AuthActivity;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private int APP_UPDATE_CODE = 18;
    private AppUpdateManager appUpdateManager;
    private ShopPartner shopPartner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shopPartner = LocalDB.getPartnerDetails(getApplicationContext());

        appUpdateManager = AppUpdateManagerFactory.create(this);

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(@NonNull AppUpdateInfo appUpdateInfo) {
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,MainActivity.this,APP_UPDATE_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(),"Some Error Occurred While Updating Your App",Toasty.LENGTH_SHORT)
                                .show();
                    }
                }
                else {
                    moveToNextActivity();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                moveToNextActivity();
            }
        });

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(appUpdateManager == null) return;

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(@NonNull AppUpdateInfo appUpdateInfo) {
                if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,MainActivity.this,APP_UPDATE_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                        Toasty.error(getApplicationContext(),"Some Error Occurred While Updating Your App",Toasty.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });

    }

    private void moveToNextActivity(){
        if(shopPartner == null){
            Intent authActivity = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(authActivity);
        }
        else {
            Intent homeActivity = new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(homeActivity);
        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == APP_UPDATE_CODE){
            if(resultCode != RESULT_OK){
                Toasty.error(getApplicationContext(),"Update Failed Please Try Again",Toasty.LENGTH_SHORT)
                        .show();
            }
            else if(requestCode == RESULT_OK){
                Toasty.success(getApplicationContext(),"Update Was Successfull, Please Restart The App",Toasty.LENGTH_SHORT)
                        .show();
                moveToNextActivity();

            }
        }

    }
}