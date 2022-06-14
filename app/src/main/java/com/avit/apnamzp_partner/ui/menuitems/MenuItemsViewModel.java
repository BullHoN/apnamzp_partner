package com.avit.apnamzp_partner.ui.menuitems;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MenuItemsViewModel extends ViewModel {
    private MutableLiveData<List<ShopCategoryData>> shopCategoryDataMutableLiveData;
    private String TAG = "MenuItemsViewModel";

    public MenuItemsViewModel(){
        shopCategoryDataMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<ShopCategoryData>> getShopCategoryDataMutableLiveData() {
        return shopCategoryDataMutableLiveData;
    }

    public void getCategoriesFromServer(String itemsId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<ShopCategoryData>> call = networkApi.getShopData(itemsId);
        call.enqueue(new Callback<List<ShopCategoryData>>() {
            @Override
            public void onResponse(Call<List<ShopCategoryData>> call, Response<List<ShopCategoryData>> response) {
                shopCategoryDataMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ShopCategoryData>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

}
