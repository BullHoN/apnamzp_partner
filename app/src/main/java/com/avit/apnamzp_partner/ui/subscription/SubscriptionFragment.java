package com.avit.apnamzp_partner.ui.subscription;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentSubscriptionBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.subscription.BannerData;
import com.avit.apnamzp_partner.models.subscription.Subscription;
import com.avit.apnamzp_partner.models.subscription.SubscriptionPricings;
import com.avit.apnamzp_partner.ui.payment.PaymentActivity;
import com.avit.apnamzp_partner.utils.PrettyStrings;
import com.bumptech.glide.Glide;
import com.jama.carouselview.CarouselViewListener;

import java.util.Date;
import java.util.List;


public class SubscriptionFragment extends Fragment {

    private FragmentSubscriptionBinding binding;
    private SubscriptionViewModel viewModel;
    private Subscription currentSubscription;
    private SubscriptionPricings currPricing;

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

    private void setUpCurrentValidPlan(){
        viewModel.getMutableSubscriptionLiveData().observe(getViewLifecycleOwner(), new Observer<Subscription>() {
            @Override
            public void onChanged(Subscription subscription) {
                currentSubscription = subscription;

                if(subscription.getId() == null){
                    binding.noSubscriptionText.setVisibility(View.VISIBLE);
                    binding.currPlansContainer.setVisibility(View.GONE);
                    setUpPlans(subscription.getSubscriptionPricings());
                    binding.loading.setVisibility(View.GONE);

                    return;
                }

                binding.fromDate.setText(currentSubscription.getStartDate().toLocaleString().split(" ")[0]);
                binding.endDate.setText(currentSubscription.getEndDate().toLocaleString().split(" ")[0]);

                binding.totalEarnings.setText("Total Sales: " + PrettyStrings.getCostInINR(currentSubscription.getTotalEarning()));

                for(SubscriptionPricings pricings : currentSubscription.getSubscriptionPricings()){
                    if(currentSubscription.getTotalEarning() >= pricings.getFrom() && currentSubscription.getTotalEarning() <= pricings.getTo()){
                        currPricing = pricings;
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
                if(currDate.compareTo(subscription.getEndDate()) > 0 && !subscription.isFree()){
                    binding.payButton.setVisibility(View.VISIBLE);
                    shopPayNowDialog();
                }

                setUpPlans(subscription.getSubscriptionPricings());
                binding.loading.setVisibility(View.GONE);

                binding.payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openPaymentActivity();
                    }
                });
            }
        });
    }

    private void openPaymentActivity(){
        Intent intent = new Intent(getContext(),PaymentActivity.class);
        intent.putExtra("subscriptionId", currentSubscription.getId());
        intent.putExtra("amount", currPricing.getAmount());
        intent.putExtra("shopId", LocalDB.getPartnerDetails(getContext()).getShopId());

        startActivity(intent);
    }

    private void shopPayNowDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Your Payment is Due For This Month");

        builder.setPositiveButton("Pay Now", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openPaymentActivity();
                dialogInterface.dismiss();
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
            payable_amount.setText("Payable Amount: " + PrettyStrings.getCostInINR(subs.getAmount()));

            if(subs == currPricing){
                plan_container.setBackgroundColor(getResources().getColor(R.color.success));
                sales_range.setTextColor(getResources().getColor(R.color.white));
                payable_amount.setTextColor(getResources().getColor(R.color.white));
            }

            binding.plans.addView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.loading.setVisibility(View.VISIBLE);
        viewModel.getSubscriptionImages(getContext());
        viewModel.getActiveSubscription(getContext(), LocalDB.getPartnerDetails(getContext()).getShopId());
        setUpBannerImages();
        setUpCurrentValidPlan();

    }

}