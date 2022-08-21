package com.avit.apnamzp_partner.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.avit.apnamzp_partner.HomeActivity;
import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentHomeBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.orders.OrderStatus;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.ui.order_notification.OrderNotification;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class homeFragment extends Fragment implements OrdersAdapter.NextStepInterface {

    private FragmentHomeBinding binding;
    private homeFragmentViewModel viewModel;
    private String TAG = "homeFragment";
    private OrdersAdapter ordersAdapter;
    private Gson gson;
    private List<OrderItem> actionNeededOrders;
    private ShopPartner shopPartner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(homeFragmentViewModel.class);
        gson = new Gson();

        View root = binding.getRoot();

        ordersAdapter = new OrdersAdapter(getContext(),new ArrayList<>(),this);
        binding.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        binding.orderItemsRecyclerView.setAdapter(ordersAdapter);

        shopPartner = LocalDB.getPartnerDetails(getContext());
        viewModel.getShopStatus(getContext(),shopPartner.getShopId());

        viewModel.getOrders(getContext(),shopPartner.getShopId(), shopPartner.getShopType(), OrderStatus.ORDER_PREPARING,1);
        viewModel.getOrderItemsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(List<OrderItem> orderItems) {
                ordersAdapter.replaceItems(orderItems);
                binding.progressBar.setVisibility(View.INVISIBLE);

                if(orderItems.size() == 0){
                    binding.ordersNotFoundAnimation.setVisibility(View.VISIBLE);
                    binding.ordersNotFoundAnimation.setAnimation(R.raw.no_orders_animation);
                    binding.ordersNotFoundAnimation.playAnimation();
                }
                else {
                    binding.ordersNotFoundAnimation.setVisibility(View.GONE);
                }

            }
        });

        // chipgroup
       binding.ordersFilter.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(ChipGroup group, int checkedId) {

               binding.progressBar.setVisibility(View.VISIBLE);
               ShopPartner shopPartner =  LocalDB.getPartnerDetails(getContext());

               if(checkedId == R.id.all_filter){
                   Log.i(TAG, "onCheckedChanged: " + "ALL");
               }
               else if(checkedId == R.id.preparing_filter){
                   Log.i(TAG, "onCheckedChanged: " + "Preparing");
                   viewModel.getOrders(getContext(), shopPartner.getShopId(), shopPartner.getShopType(), OrderStatus.ORDER_PREPARING,1);
               }
               else if(checkedId == R.id.ready_filter){
                   Log.i(TAG, "onCheckedChanged: " + "Ready");
                   viewModel.getOrders(getContext(), shopPartner.getShopId(), shopPartner.getShopType(),OrderStatus.ORDER_READY,1);
               }
               else if(checkedId == R.id.completed_filter){
                   Log.i(TAG, "onCheckedChanged: " + "Completed");
                   // TODO: do something about the pagination
                   viewModel.getOrders(getContext(),shopPartner.getShopId(),shopPartner.getShopType(),OrderStatus.ORDER_COMPLETED,1);
               }
           }
       });

       // ACTION NEEDED ORDERS
        actionNeededOrders = LocalDB.getActionNeededOrders(getContext());
        Log.i(TAG, "onCreateView: " + gson.toJson(actionNeededOrders));

        if(actionNeededOrders.size() == 0){
            binding.actionNeededContainer.setVisibility(View.GONE);
        }
        else {
            binding.alertActionOrdersAnimation.setAnimation(R.raw.alert_animation);
            binding.alertActionOrdersAnimation.playAnimation();

            rejectAllPendingOrder();
        }

        binding.actionNeededContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllActionRequiredOrders();
            }
        });

        setShopStatus();

        binding.shopStatusContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shopPartner.setOpen(!shopPartner.isOpen());
                changeOrderStatus(shopPartner.getPhoneNo(),!shopPartner.isOpen());
                LocalDB.savePartnerDetails(getContext(),shopPartner);
                setShopStatus();
            }
        });

        viewModel.getMutableShopPartnerLiveData().observe(getViewLifecycleOwner(), new Observer<ShopPartner>() {
            @Override
            public void onChanged(ShopPartner shopPartnerStatus) {
                shopPartner.setOpen(shopPartnerStatus.isOpen());
                shopPartner.setBannerImage(shopPartnerStatus.getBannerImage());
                LocalDB.savePartnerDetails(getContext(),shopPartner);
                setShopStatus();
            }
        });

        return root;
    }

    private void changeOrderStatus(String phoneNo,boolean isOpen){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.changeShopStatus(phoneNo,isOpen);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    private void setShopStatus(){
        if(!shopPartner.isOpen()){
            binding.shopStatusContainer.setBackgroundColor(getResources().getColor(R.color.successColor));
            binding.shopStatusImage.setImageResource(R.drawable.ic_open);
            binding.shopStatusButton.setText("Shop Opened");
            binding.shopStatusButton.setTextColor(getResources().getColor(R.color.successColor));
        }
        else {
            binding.shopStatusContainer.setBackgroundColor(getResources().getColor(R.color.failure));
            binding.shopStatusImage.setImageResource(R.drawable.ic_closed);
            binding.shopStatusButton.setText("Shop Closed");
            binding.shopStatusButton.setTextColor(getResources().getColor(R.color.failure));
        }
    }

    private void showAllActionRequiredOrders(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Action Needed Orders");

        String orderIds[] = new String[actionNeededOrders.size()];
        for(int i=0;i<actionNeededOrders.size();i++){
            orderIds[i] = actionNeededOrders.get(i).get_id();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,orderIds);
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getContext(), OrderNotification.class);
                intent.setAction("com.avit.apnamzp_partner.NEW_ORDER_NOTIFICATION");

                OrderItem actionNeededOrderItemShowPage = actionNeededOrders.get(i);

                Log.i(TAG, "onClick: " + actionNeededOrderItemShowPage.getItemTotal());

                intent.putExtra("userId", actionNeededOrderItemShowPage.getUserId());
                intent.putExtra("orderId", actionNeededOrderItemShowPage.get_id());
                intent.putExtra("totalAmount", String.valueOf(actionNeededOrderItemShowPage.getItemTotal()));
                intent.putExtra("orderItems", gson.toJson(actionNeededOrderItemShowPage.getOrderItems()));
                intent.putExtra("isDeliveryService", String.valueOf(actionNeededOrderItemShowPage.getBillingDetails().getDeliveryService()));

                startActivity(intent);

            }
        });

        builder.show();

    }



    private void rejectAllPendingOrder(){
        long waitTime = 1000 * 60 * 2;
        for(OrderItem  orderItem : actionNeededOrders){
            CountDownTimer countDownTimer =  new CountDownTimer(waitTime, waitTime / 2) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    rejectOrder("Shop Didn't Responded",orderItem);
                }
            }.start();
        }
    }

    private void rejectOrder(String cancelReason,OrderItem orderItem) {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.rejectOrder(orderItem.get_id(), orderItem.getUserId(), cancelReason);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                NetworkResponse networkResponse = response.body();
                if (networkResponse.getStatus()) {
                    Toasty.error(getContext(), "Order " + orderItem.get_id() + "was rejected due to inactivity", Toasty.LENGTH_SHORT)
                            .show();

                    Log.i(TAG, "onResponse: Order Rejected" );

                    binding.actionNeededContainer.setVisibility(View.GONE);
                    removeActionNeededOrder(orderItem);

                }
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(), "Some error occured", Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: rejecting orders", t);
            }
        });
    }

    private void removeActionNeededOrder(OrderItem orderItem){
        LocalDB.saveActionNeededOrder(getContext(),orderItem,"remove");
    }

    @Override
    public void updateOrderStatus(String orderId, int newOrderStatus, boolean shopReceivedPayment) {
        Log.i(TAG, "updateOrderStatus: " + orderId + " " + newOrderStatus);
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        binding.progressBar.setVisibility(View.VISIBLE);

        Call<NetworkResponse> call = networkApi.updateOrderStatus(orderId,newOrderStatus,shopReceivedPayment);

        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                binding.progressBar.setVisibility(View.INVISIBLE);
                Toasty.success(getContext(),"Chaanges Saved",Toasty.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_LONG);
            }
        });

    }

    @Override
    public void openOrderDetailsFragment(OrderItem orderItem) {
        Gson gson = new Gson();
        String orderItemsString = gson.toJson(orderItem);

        Bundle bundle = new Bundle();
        bundle.putString("orderItem",orderItemsString);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_orderDetailsFragment,bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}