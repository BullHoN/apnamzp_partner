package com.avit.apnamzp_partner.ui.subscription;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.subscription.BannerData;
import com.avit.apnamzp_partner.models.subscription.Subscription;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubscriptionViewModel extends ViewModel {

    private MutableLiveData<List<BannerData>> mutableBannerImagesLiveData;
    private MutableLiveData<Subscription> mutableSubscriptionLiveData;

    public SubscriptionViewModel(){
        mutableBannerImagesLiveData = new MutableLiveData<>();
        mutableSubscriptionLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<Subscription> getMutableSubscriptionLiveData() {
        return mutableSubscriptionLiveData;
    }

    public MutableLiveData<List<BannerData>> getMutableBannerImagesLiveData() {
        return mutableBannerImagesLiveData;
    }

    public void clearViewModel(){
        mutableSubscriptionLiveData.setValue(null);
    }

    public void getSubscriptionImages(Context context){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<BannerData>> call = networkApi.getSubscriptionBannerImages();
        call.enqueue(new Callback<List<BannerData>>() {
            @Override
            public void onResponse(Call<List<BannerData>> call, Response<List<BannerData>> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                mutableBannerImagesLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<List<BannerData>> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });

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

                mutableSubscriptionLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<Subscription> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

}
