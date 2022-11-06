package com.avit.apnamzp_partner.ui.orderdetails;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderDetailsViewModel extends ViewModel {

    private MutableLiveData<OrderItem> orderItemMutableLiveData;

    public OrderDetailsViewModel(){
        orderItemMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<OrderItem> getOrderItemMutableLiveData() {
        return orderItemMutableLiveData;
    }

    public void setOrderItemMutableLiveData(OrderItem orderItem) {
        orderItemMutableLiveData.setValue(orderItem);
    }

    public void findReviewOrder(Context context, String orderId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<OrderItem> call = networkApi.getOrder(orderId);
        call.enqueue(new Callback<OrderItem>() {
            @Override
            public void onResponse(Call<OrderItem> call, Response<OrderItem> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                orderItemMutableLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<OrderItem> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

}
