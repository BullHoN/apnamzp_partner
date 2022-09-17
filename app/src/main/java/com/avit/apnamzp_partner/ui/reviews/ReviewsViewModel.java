package com.avit.apnamzp_partner.ui.reviews;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ReviewData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReviewsViewModel extends ViewModel {
    private MutableLiveData<List<ReviewData>> mutableReviewsLiveData;

    public ReviewsViewModel(){
        mutableReviewsLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<ReviewData>> getMutableReviewsLiveData() {
        return mutableReviewsLiveData;
    }

    public void getReviews(Context context, String shopId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<List<ReviewData>> call = networkApi.getReviews(shopId);
        call.enqueue(new Callback<List<ReviewData>>() {
            @Override
            public void onResponse(Call<List<ReviewData>> call, Response<List<ReviewData>> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                mutableReviewsLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<List<ReviewData>> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

}
