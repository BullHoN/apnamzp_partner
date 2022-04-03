package com.avit.apnamzp.ui.home;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.avit.apnamzp.models.orders.OrderItem;
import com.avit.apnamzp.network.NetworkApi;
import com.avit.apnamzp.network.RetrofitClient;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class homeFragmentViewModel extends ViewModel {

    private MutableLiveData<List<OrderItem>> orderItemsMutableLiveData;
    private String TAG = "homeFragment";

    public homeFragmentViewModel(){
        orderItemsMutableLiveData = new MutableLiveData<>();
    }

    public void getOrders(Context context,String shopId, String shopCategory, int orderStatus, int pageNumber){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<OrderItem>> call = networkApi.getAllOrders(shopCategory,shopId,orderStatus);
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
