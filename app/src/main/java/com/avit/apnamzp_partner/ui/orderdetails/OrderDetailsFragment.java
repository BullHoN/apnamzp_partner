package com.avit.apnamzp_partner.ui.orderdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentOrderDetailsBinding;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.ui.home.OrderItemsAdapter;
import com.avit.apnamzp_partner.utils.PrettyStrings;
import com.google.gson.Gson;

public class OrderDetailsFragment extends Fragment {

    private FragmentOrderDetailsBinding binding;
    private Gson gson;
    private OrderItem orderItem;
    private OrderItemsAdapter orderItemsAdapter;
    private String TAG = "OrderDetailsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderDetailsBinding.inflate(inflater,container,false);
        gson = new Gson();

        String orderItemString = getArguments().getString("orderItem");
        orderItem = gson.fromJson(orderItemString,OrderItem.class);

        binding.orderId.setText("Order Id: #" + orderItem.get_id());

        binding.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        orderItemsAdapter = new OrderItemsAdapter(orderItem.getOrderItems(),getContext());
        binding.orderItemsRecyclerView.setAdapter(orderItemsAdapter);

        setText(binding.itemsTotal, orderItem.getBillingDetails().getItemTotal(),false);
        setText(binding.totalTaxAndPackingCost, orderItem.getBillingDetails().getTotalTaxesAndPackingCharge(), false);
        setText(binding.totalDiscount, orderItem.getBillingDetails().getTotalDiscount(),true);

        binding.taxPercentage.setText(orderItem.getBillingDetails().getTaxPercentage() + "%");

        if(orderItem.isShopOfferApplied()){
            setText(binding.offerDiscountAmount,orderItem.getBillingDetails().getOfferDiscountedAmount(),true);
            binding.appliedOfferCode.setText(orderItem.getOfferCode());
        }
        else {
            setText(binding.offerDiscountAmount,0,false);
            binding.appliedOfferCode.setText("No Shop Offer Applied");
        }

        if(orderItem.isFreeDelivery()){
            setText(binding.freeDeliveryPrice,orderItem.getBillingDetails().getDeliveryCharge(),true);
        }
        else {
            setText(binding.freeDeliveryPrice,0,false);
        }

        setText(binding.receivingAmount,orderItem.getTotalReceivingAmount(),false);

        // order details
        if(orderItem.getSpecialInstructions() == null || orderItem.getSpecialInstructions().length() == 0){
            binding.specialInstruction.setText("No Special Instructions");
        }
        else {
            binding.specialInstruction.setText(orderItem.getSpecialInstructions());
        }

        binding.callCustomer.setText("CALL +91" + orderItem.getUserId());
        binding.callCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = orderItem.getUserId();
                Intent callingIntent = new Intent();
                callingIntent.setAction(Intent.ACTION_DIAL);
                callingIntent.setData(Uri.parse("tel: " + phoneNo));
                startActivity(callingIntent);
            }
        });

        if(orderItem.getBillingDetails().getDeliveryService()){
            binding.orderDeliveryType.setText("Delivery Service");
        }
        else {
            binding.orderDeliveryType.setText("Self Pickup");
        }

        binding.orderPlacedAt.setText(orderItem.getCreatedAt().toLocaleString());

        // assigned delivery boy
        if(orderItem.getAssignedDeliveryBoy() == null || orderItem.getAssignedDeliveryBoy().length() == 0){
            binding.assignedDeliveryBoy.setText("Not Yet Assigned");
        }
        else {
            binding.assignedDeliveryBoy.setText("+91" + orderItem.getAssignedDeliveryBoy());
        }


        return binding.getRoot();
    }

    void setText(TextView view, String value, boolean negative){
        view.setText(PrettyStrings.getCostInINR(value));
        if(!negative){
            view.setTextColor(getResources().getColor(R.color.successColor));
        }
        else {
            view.setTextColor(getResources().getColor(R.color.errorColor));
        }
    }

    void setText(TextView view, int value, boolean negative){
        view.setText(PrettyStrings.getCostInINR(value));
        if(!negative){
            view.setTextColor(getResources().getColor(R.color.successColor));
        }
        else {
            view.setTextColor(getResources().getColor(R.color.errorColor));
        }
    }

}