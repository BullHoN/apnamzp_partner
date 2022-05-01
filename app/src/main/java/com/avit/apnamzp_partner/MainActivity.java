package com.avit.apnamzp_partner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.avit.apnamzp_partner.auth.AuthActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Authentication
        Intent homeActivity = new Intent(getApplicationContext(), AuthActivity.class);
        startActivity(homeActivity);

    }
}