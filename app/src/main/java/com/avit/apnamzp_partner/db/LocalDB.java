package com.avit.apnamzp_partner.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.google.gson.Gson;

public class LocalDB {

    public static String localDBNAME = "com.avit.apnamzp_parnter_localDB";
    public static String shopPartnerPrefName = "shopPartner";
    private static SharedPreferences sharedPreferences;

    public static SharedPreferences getSharedPreference(Context context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(LocalDB.localDBNAME,Context.MODE_PRIVATE);
        }

        return sharedPreferences;
    }


    public static void savePartnerDetails(Context context,ShopPartner shopPartner){
        SharedPreferences sf = LocalDB.getSharedPreference(context);

        Gson gson = new Gson();
        String shopPartnerJsonString = gson.toJson(shopPartner);

        SharedPreferences.Editor editor = sf.edit();
        editor.putString(shopPartnerPrefName,shopPartnerJsonString);

        editor.apply();

    }

    public static ShopPartner getPartnerDetails(Context context) {
        SharedPreferences sf = LocalDB.getSharedPreference(context);
        Gson gson = new Gson();

        String shopPartnerJsonString = sf.getString(LocalDB.shopPartnerPrefName,null);
        if(shopPartnerJsonString == null) return null;

        return gson.fromJson(shopPartnerJsonString,ShopPartner.class);
    }

}
