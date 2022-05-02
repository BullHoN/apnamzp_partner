package com.avit.apnamzp_partner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.avit.apnamzp_partner.auth.AuthActivity;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.user.ShopPartner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShopPartner shopPartner = LocalDB.getPartnerDetails(getApplicationContext());

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
}