package com.avit.apnamzp_partner.ui.orderdetails;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        binding.itemsTotal.setText(PrettyStrings.getCostInINR(orderItem.getBillingDetails().getItemTotal()));
        binding.totalTaxAndPackingCost.setText(PrettyStrings.getCostInINR(orderItem.getBillingDetails().getTotalTaxesAndPackingCharge()));
        binding.taxPercentage.setText(PrettyStrings.getCostInINR(orderItem.getBillingDetails().getTaxPercentage()));
        binding.totalDiscount.setText(PrettyStrings.getCostInINR(orderItem.getBillingDetails().getTotalDiscount()));

        if(orderItem.isShopOfferApplied()){
            binding.offerDiscountAmount.setText(PrettyStrings.getCostInINR(orderItem.getBillingDetails().getOfferDiscountedAmount()));
            binding.appliedOfferCode.setText(orderItem.getOfferCode());
        }
        else {
            binding.offerDiscountAmount.setText(PrettyStrings.getCostInINR(0));
            binding.appliedOfferCode.setText("No Shop Offer Applied");
        }

        binding.receivingAmount.setText(PrettyStrings.getCostInINR(orderItem.getTotalReceivingAmount()));

        // order details
        if(orderItem.getAssignedDeliveryBoy() == null || orderItem.getSpecialInstructions().length() == 0){
            binding.specialInstruction.setText("No Special Instructions");
        }
        else {
            binding.specialInstruction.setText(orderItem.getSpecialInstructions());
        }

        if(orderItem.getBillingDetails().getDeliveryService()){
            binding.orderDeliveryType.setText("Delivery Service");
        }
        else {
            binding.orderDeliveryType.setText("Self Service");
        }

        binding.orderPlacedAt.setText(orderItem.getCreatedAt().toLocaleString());

        // assigned delivery boy
        if(orderItem.getAssignedDeliveryBoy() == null || orderItem.getAssignedDeliveryBoy().length() == 0){
            binding.assignedDeliveryBoy.setText("Not Yet Assigned");
        }
        else {
            binding.assignedDeliveryBoy.setText("Delivery Sathi Number: " + orderItem.getAssignedDeliveryBoy());
        }


        return binding.getRoot();
    }
}