package com.avit.apnamzp_partner.ui.home;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.subscription.Subscription;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class homeFragmentViewModel extends ViewModel {

    private MutableLiveData<List<OrderItem>> orderItemsMutableLiveData;
    private MutableLiveData<ShopPartner> mutableShopPartnerLiveData;
    private MutableLiveData<List<OrderItem>> actionNeededLiveData;
    private MutableLiveData<Subscription> subscriptionMutableLiveData;
    private String TAG = "homeFragment";

    public homeFragmentViewModel(){
        mutableShopPartnerLiveData = new MutableLiveData<>();
        orderItemsMutableLiveData = new MutableLiveData<>();
        actionNeededLiveData = new MutableLiveData<>();
        subscriptionMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Subscription> getSubscriptionMutableLiveData() {
        return subscriptionMutableLiveData;
    }

    public MutableLiveData<List<OrderItem>> getActionNeededLiveData() {
        return actionNeededLiveData;
    }

    public MutableLiveData<ShopPartner> getMutableShopPartnerLiveData() {
        return mutableShopPartnerLiveData;
    }

    public void getActiveSubscription(Context context, String shopId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<Subscription> call = networkApi.getSubscription(shopId);
        call.enqueue(new Callback<Subscription>() {
            @Override
            public void onResponse(Call<Subscription> call, Response<Subscription> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                subscriptionMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Subscription> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

    public void getActionNeeded(Context context, String shopId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<OrderItem>> call = networkApi.getActionNeededOrders(shopId);
        call.enqueue(new Callback<List<OrderItem>>() {
            @Override
            public void onResponse(Call<List<OrderItem>> call, Response<List<OrderItem>> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                actionNeededLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderItem>> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });

    }

    public void getShopStatus(Context context, String shopId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<ShopPartner> call = networkApi.getShopStatus(shopId);
        call.enqueue(new Callback<ShopPartner>() {
            @Override
            public void onResponse(Call<ShopPartner> call, Response<ShopPartner> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                mutableShopPartnerLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<ShopPartner> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });

    }

    public void getOrders(Context context,String shopId, String shopCategory, int orderStatus, int pageNumber){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Date todayDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Call<List<OrderItem>> call = networkApi.getAllOrders(shopCategory,shopId,orderStatus,simpleDateFormat.format(todayDate),false);
        call.enqueue(new Callback<List<OrderItem>>() {
            @Override
            public void onResponse(Call<List<OrderItem>> call, Response<List<OrderItem>> response) {
                orderItemsMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderItem>> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    public MutableLiveData<List<OrderItem>> getOrderItemsMutableLiveData() {
        return orderItemsMutableLiveData;
    }
}
