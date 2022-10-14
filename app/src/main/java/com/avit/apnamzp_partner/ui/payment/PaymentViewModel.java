package com.avit.apnamzp_partner.ui.payment;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.payment.OnlinePaymentOrderIdPostData;
import com.avit.apnamzp_partner.models.payment.PaymentMetadata;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentViewModel extends ViewModel {

    private MutableLiveData<PaymentMetadata> paymentMetadataMutableLiveData;

    public PaymentViewModel(){
        paymentMetadataMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<PaymentMetadata> getPaymentMetadataMutableLiveData() {
        return paymentMetadataMutableLiveData;
    }

    public void getPaymentId(Context context, OnlinePaymentOrderIdPostData postData){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<PaymentMetadata> call = networkApi.getOrderPaymentId(postData);
        call.enqueue(new Callback<PaymentMetadata>() {
            @Override
            public void onResponse(Call<PaymentMetadata> call, Response<PaymentMetadata> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(context,errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                paymentMetadataMutableLiveData.setValue(response.body());

            }

            @Override
            public void onFailure(Call<PaymentMetadata> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

}
