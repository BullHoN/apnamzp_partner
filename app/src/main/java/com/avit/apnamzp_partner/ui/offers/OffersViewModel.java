package com.avit.apnamzp_partner.ui.offers;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OffersViewModel extends ViewModel {
    private MutableLiveData<List<OfferItem>> offersListMutableLiveData;

    public OffersViewModel(){
        offersListMutableLiveData = new MutableLiveData<>();
    }

    public void getDataFromServer(Context context, String shopName){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<OfferItem>> call = networkApi.getOffers(shopName);
        call.enqueue(new Callback<List<OfferItem>>() {
            @Override
            public void onResponse(Call<List<OfferItem>> call, Response<List<OfferItem>> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                offersListMutableLiveData.setValue(response.body());
            }

            @Override
            public void onFailure(Call<List<OfferItem>> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });
    }

    public MutableLiveData<List<OfferItem>> getOffersListMutableLiveData() {
        return offersListMutableLiveData;
    }

}
