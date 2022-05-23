package com.avit.apnamzp_partner.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentHomeBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.orders.OrderStatus;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(homeFragmentViewModel.class);

        View root = binding.getRoot();

        ordersAdapter = new OrdersAdapter(getContext(),new ArrayList<>(),this);
        binding.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        binding.orderItemsRecyclerView.setAdapter(ordersAdapter);

        ShopPartner shopPartner = LocalDB.getPartnerDetails(getContext());

        viewModel.getOrders(getContext(),shopPartner.getShopId(), shopPartner.getShopType(), OrderStatus.ORDER_PREPARING,1);
        viewModel.getOrderItemsMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(List<OrderItem> orderItems) {
                ordersAdapter.replaceItems(orderItems);
                binding.progressBar.setVisibility(View.INVISIBLE);
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

        return root;
    }

    @Override
    public void updateOrderStatus(String orderId, int newOrderStatus) {
        Log.i(TAG, "updateOrderStatus: " + orderId + " " + newOrderStatus);
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        binding.progressBar.setVisibility(View.VISIBLE);

        Call<ResponseBody> call = networkApi.updateOrderStatus(orderId,newOrderStatus);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                binding.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_LONG);
            }
        });

        if(newOrderStatus == 4){
            Call<NetworkResponse> assignDBoy = networkApi.assignDeliveryBoy(orderId,"25.133699","82.564430");
            assignDBoy.enqueue(new Callback<NetworkResponse>() {
                @Override
                public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                }

                @Override
                public void onFailure(Call<NetworkResponse> call, Throwable t) {
                    Toasty.error(getContext(),"Please Contact ApnaMzp",Toasty.LENGTH_LONG)
                            .show();
                    Log.e(TAG, "onFailure: ", t);
                }
            });

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}