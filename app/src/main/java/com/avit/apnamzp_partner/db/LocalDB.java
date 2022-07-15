package com.avit.apnamzp_partner.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void saveActionNeededOrder(Context context, OrderItem orderItem, String action){
        SharedPreferences sf = LocalDB.getSharedPreference(context);

        Gson gson = new Gson();
        String actionRequiredOrdersString = sf.getString("actionRequiredOrders","[]");

        OrderItem actionRequiredOrders[] = gson.fromJson(actionRequiredOrdersString,OrderItem[].class);
        List<OrderItem> actionRequiredOrdersList =  new ArrayList<>(Arrays.asList(actionRequiredOrders));

        if(action.equals("add")){
            actionRequiredOrdersList.add(orderItem);
        }
        else if(action.equals("remove")){
            for(OrderItem orderItem1 : actionRequiredOrdersList){
                if(orderItem1.get_id().equals(orderItem.get_id())){
                    actionRequiredOrdersList.remove(orderItem1);
                    break;
                }
            }
        }

        actionRequiredOrdersString = gson.toJson(actionRequiredOrdersList);

        SharedPreferences.Editor editor = sf.edit();
        editor.putString("actionRequiredOrders",actionRequiredOrdersString);
        editor.apply();
    }

    public static List<OrderItem> getActionNeededOrders(Context context){
        SharedPreferences sf = LocalDB.getSharedPreference(context);

        Gson gson = new Gson();
        String actionRequiredOrdersString = sf.getString("actionRequiredOrders","[]");

        OrderItem actionRequiredOrders[] = gson.fromJson(actionRequiredOrdersString,OrderItem[].class);
        List<OrderItem> actionRequiredOrdersList =  Arrays.asList(actionRequiredOrders);

        return actionRequiredOrdersList;
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
