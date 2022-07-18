package com.avit.apnamzp_partner.ui.dashboard;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
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

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<List<OrderItem>> mutableOrderItemsLiveData;
    private String TAG = "DashboardViewModel";

    public DashboardViewModel(){
        mutableOrderItemsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<OrderItem>> getMutableOrderItemsLiveData() {
        return mutableOrderItemsLiveData;
    }

    public void getOrders(Context context, String shopId, String shopCategory, int orderStatus,String date, int pageNumber){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<OrderItem>> call = networkApi.getAllOrders(shopCategory,shopId,orderStatus,date);
        call.enqueue(new Callback<List<OrderItem>>() {
            @Override
            public void onResponse(Call<List<OrderItem>> call, Response<List<OrderItem>> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                mutableOrderItemsLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OrderItem>> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

}
