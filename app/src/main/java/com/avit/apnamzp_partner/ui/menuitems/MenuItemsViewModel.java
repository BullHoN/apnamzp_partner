package com.avit.apnamzp_partner.ui.menuitems;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;
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

    public void getCategoriesFromServer(Context context, String itemsId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<ShopCategoryData>> call = networkApi.getShopData(itemsId);
        call.enqueue(new Callback<List<ShopCategoryData>>() {
            @Override
            public void onResponse(Call<List<ShopCategoryData>> call, Response<List<ShopCategoryData>> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                shopCategoryDataMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<ShopCategoryData>> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

}
