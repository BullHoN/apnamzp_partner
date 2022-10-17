package com.avit.apnamzp_partner.ui.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.payment.OnlinePaymentOrderIdPostData;
import com.avit.apnamzp_partner.models.payment.PaymentMetadata;
import com.avit.apnamzp_partner.models.subscription.Subscription;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private PaymentViewModel viewModel;
    private String subscriptionId, shopId, orderPaymentId;
    private int amount;
    private boolean createNewPlan;
    private String TAG = "PaymentActivitys";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Checkout.preload(this);

        subscriptionId = getIntent().getStringExtra("subscriptionId");
        shopId = getIntent().getStringExtra("shopId");
        amount = getIntent().getIntExtra("amount",0);
        createNewPlan = getIntent().getBooleanExtra("createNewPlan",false);

        viewModel = new ViewModelProvider(this).get(PaymentViewModel.class);
        viewModel.getPaymentId(this, new OnlinePaymentOrderIdPostData(amount,shopId));

        viewModel.getPaymentMetadataMutableLiveData().observe(this, new Observer<PaymentMetadata>() {
            @Override
            public void onChanged(PaymentMetadata paymentMetadata) {
                orderPaymentId = paymentMetadata.getPaymentId();
                Log.i(TAG, "onCreate: " + orderPaymentId);
                startPayment();
            }
        });

    }

    private void startPayment(){
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.main_icon);

        try{
            JSONObject options = new JSONObject();
            options.put("name", "Apna MZP");
            options.put("description", "Reference No. " + orderPaymentId);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderPaymentId);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", String.valueOf(amount * 100));//pass amount in currency subunits
            options.put("prefill.contact", LocalDB.getPartnerDetails(getApplicationContext()).getPhoneNo());
            options.put("prefill.email","");
            options.put("readonly.email",true);

            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(this,options);

        }
        catch(Exception e){
            Toasty.error(this,"Error in payment " + e.getMessage(),Toasty.LENGTH_SHORT)
                    .show();
        }

    }

    private void checkout(String paymentId){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.checkout(new Subscription(subscriptionId,paymentId,amount,createNewPlan));
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getApplicationContext(),errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                Toasty.success(getApplicationContext(),"Payment Sucessfull",Toasty.LENGTH_LONG)
                        .show();
                finish();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(),"Payment Failed: Contact Apna MZP", Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

    @Override
    public void onPaymentSuccess(String s) {
        Log.i(TAG, "onPaymentSuccess: " + s);
        checkout(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.i(TAG, "onPaymentError: " + s);
        Toasty.error(this,"Payment failed",Toasty.LENGTH_SHORT)
                .show();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toasty.error(this,"Payment was Cancelled",Toasty.LENGTH_LONG)
                .show();
        finish();
    }
}