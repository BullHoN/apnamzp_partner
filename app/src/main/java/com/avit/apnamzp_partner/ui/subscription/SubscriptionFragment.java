package com.avit.apnamzp_partner.ui.subscription;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentSubscriptionBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.subscription.BannerData;
import com.avit.apnamzp_partner.models.subscription.Subscription;
import com.avit.apnamzp_partner.models.subscription.SubscriptionPricings;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.ui.payment.PaymentActivity;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.avit.apnamzp_partner.utils.PrettyStrings;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.jama.carouselview.CarouselViewListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SubscriptionFragment extends Fragment {

    private FragmentSubscriptionBinding binding;
    private SubscriptionViewModel viewModel;
    private Subscription currentSubscription;
    private SubscriptionPricings currPricing;
    private String TAG = "SubscriptionFragments";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubscriptionBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(SubscriptionViewModel.class);

        return binding.getRoot();
    }

    private void setUpBannerImages(){
        viewModel.getMutableBannerImagesLiveData().observe(getViewLifecycleOwner(), new Observer<List<BannerData>>() {
            @Override
            public void onChanged(List<BannerData> bannerData) {
                binding.bannerCarousel.setSize(bannerData.size());

                binding.bannerCarousel.setCarouselViewListener(new CarouselViewListener() {
                    @Override
                    public void onBindView(View view, int position) {
                        ImageView imageView = view.findViewById(R.id.imageView);
                        Glide.with(getContext())
                                .load(bannerData.get(position).getImageURL())
                                .into(imageView);
                    }
                });

                binding.bannerCarousel.show();
            }
        });
    }

    private void setUpCurrentValidPlan(Subscription subscription){
        currentSubscription = subscription;

        if(subscription.getId() == null){
            binding.noSubscriptionText.setVisibility(View.VISIBLE);
            binding.currPlansContainer.setVisibility(View.GONE);
            setUpPlans(subscription.getSubscriptionPricings());
            binding.loading.setVisibility(View.GONE);

            return;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM");

        binding.fromDate.setText(simpleDateFormat.format(currentSubscription.getStartDate()));
        binding.endDate.setText(simpleDateFormat.format(currentSubscription.getEndDate()));

        binding.totalEarnings.setText("Total Sales: " + PrettyStrings.getCostInINR(currentSubscription.getTotalEarning()));

        for(SubscriptionPricings pricings : currentSubscription.getSubscriptionPricings()){
            if(currentSubscription.getTotalEarning() >= pricings.getFrom() && currentSubscription.getTotalEarning() <= pricings.getTo()){
                currPricing = new SubscriptionPricings(pricings);
                break;
            }
        }

        if(subscription.isFree()){
            binding.expectedPay.setText("Expected Pay: Free!! 🎉🎉");
        }
        else {
            binding.expectedPay.setText("Expected Pay: " + PrettyStrings.getCostInINR(currPricing.getAmount()));
        }

        Date currDate = new Date();
        if(currDate.compareTo(subscription.getEndDate()) > 0){
            binding.payButton.setVisibility(View.VISIBLE);
            shopPayNowDialog();
        }

        setUpPlans(subscription.getSubscriptionPricings());
        binding.loading.setVisibility(View.GONE);

        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaymentTypeDialog();
            }
        });
    }

    private void openPaymentTypeDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_payment_type,null,false);

        LinearLayout pendingPaymentContainer = view.findViewById(R.id.pending_payment_container);
        if(!currentSubscription.isFree()){
            pendingPaymentContainer.setVisibility(View.VISIBLE);
            TextView pendingPaymentText = view.findViewById(R.id.pending_payment_text);
            pendingPaymentText.setText(PrettyStrings.getCostInINR(currPricing.getAmount()) + " your pending payment for this month");
        }

        TextView continueServiceText = view.findViewById(R.id.continue_service_text);
        continueServiceText.setText(PrettyStrings.getCostInINR(currentSubscription.getNewPlanPrice()) + " to continue our service for next month");

        MaterialCheckBox continueServiceCheckbox = view.findViewById(R.id.continue_service_checkbox);
        MaterialButton payButton = view.findViewById(R.id.pay_button);
        continueServiceCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int total_payable_amount = !currentSubscription.isFree() ? currPricing.getAmount() : 0;
                if(continueServiceCheckbox.isChecked()){
                    total_payable_amount += currentSubscription.getNewPlanPrice();
                }

                payButton.setText("Pay " + PrettyStrings.getCostInINR(total_payable_amount));
            }
        });

        int total_payable_amount = !currentSubscription.isFree() ? currPricing.getAmount() : 0;
        if(continueServiceCheckbox.isChecked()){
            total_payable_amount += currentSubscription.getNewPlanPrice();
        }

        payButton.setText("Pay " + PrettyStrings.getCostInINR(total_payable_amount));

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int total_payable_amount = !currentSubscription.isFree() ? currPricing.getAmount() : 0;
                if(continueServiceCheckbox.isChecked()){
                    total_payable_amount += currentSubscription.getNewPlanPrice();
                }
                openPaymentActivity(total_payable_amount,continueServiceCheckbox.isChecked());
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(view);

        bottomSheetDialog.show();
    }

    private void openPaymentActivity(int totalAmount,boolean isContinuingService){
        Intent intent = new Intent(getContext(),PaymentActivity.class);
        intent.putExtra("subscriptionId", currentSubscription.getId());
        intent.putExtra("amount", totalAmount);
        intent.putExtra("createNewPlan",isContinuingService);
        intent.putExtra("shopId", LocalDB.getPartnerDetails(getContext()).getShopId());

        startActivity(intent);
    }

    private void shopPayNowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Your Payment is Due For This Month");

        builder.setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                openPaymentTypeDialog();
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void setUpPlans(List<SubscriptionPricings> pricings){
        binding.plans.removeAllViewsInLayout();
        for(SubscriptionPricings subs : pricings){
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_plan,null,false);

            TextView sales_range = view.findViewById(R.id.sales_range);
            TextView payable_amount = view.findViewById(R.id.payable_amount);
            LinearLayout plan_container = view.findViewById(R.id.plan_container);

            sales_range.setText("Sales Between: " + PrettyStrings.getCostInINR(subs.getFrom()) + " - " + PrettyStrings.getCostInINR(subs.getTo()));
            payable_amount.setText("Payable Amount: " + PrettyStrings.getCostInINR(subs.getAmount() + currentSubscription.getNewPlanPrice()));

            if(currentSubscription.getTotalEarning() >= subs.getFrom() && currentSubscription.getTotalEarning() <= subs.getTo()){
                plan_container.setBackgroundColor(getResources().getColor(R.color.success));
                sales_range.setTextColor(getResources().getColor(R.color.white));
                payable_amount.setTextColor(getResources().getColor(R.color.white));
            }

            binding.plans.addView(view);
        }
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

                setUpCurrentValidPlan(response.body());
            }

            @Override
            public void onFailure(Call<Subscription> call, Throwable t) {
                Toasty.error(context,t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        binding.payButton.setVisibility(View.GONE);
        binding.loading.setVisibility(View.VISIBLE);
        viewModel.getSubscriptionImages(getContext());
        getActiveSubscription(getContext(), LocalDB.getPartnerDetails(getContext()).getShopId());
        setUpBannerImages();

    }

}