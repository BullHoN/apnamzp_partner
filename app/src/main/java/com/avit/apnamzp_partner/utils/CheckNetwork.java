package com.avit.apnamzp_partner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;

import com.avit.apnamzp_partner.R;


public class CheckNetwork {
    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if(networkInfo == null || !cm.getActiveNetworkInfo().isConnected()){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet,null,false);
            builder.setView(view);
            builder.show();
        }

        return networkInfo != null && cm.getActiveNetworkInfo().isConnected();
    }
}
