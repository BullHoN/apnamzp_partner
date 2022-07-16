package com.avit.apnamzp_partner.ui.dashboard;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentDashboardBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.orders.OrderItem;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.avit.apnamzp_partner.ui.home.OrdersAdapter;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DashboardFragment extends Fragment implements OrdersAdapter.NextStepInterface{

    private FragmentDashboardBinding binding;
    private DashboardViewModel viewModel;
    private ShopPartner shopPartner;
    private String TAG = "DashboardFragment";
    private OrdersAdapter ordersAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        shopPartner = LocalDB.getPartnerDetails(getContext());

        Date todayDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        viewModel.getOrders(getContext(), shopPartner.getShopId(), shopPartner.getShopType(), 6,simpleDateFormat.format(todayDate),1);

        binding.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        ordersAdapter = new OrdersAdapter(getContext(),new ArrayList<>(),this);
        binding.orderItemsRecyclerView.setAdapter(ordersAdapter);

        viewModel.getMutableOrderItemsLiveData().observe(getViewLifecycleOwner(), new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(List<OrderItem> orderItems) {
                ordersAdapter.replaceItems(orderItems);
                binding.loading.setVisibility(View.GONE);

                if(orderItems.size() == 0){
                    binding.emptyOrdersView.setVisibility(View.VISIBLE);
                    binding.emptyOrdersView.setAnimation(R.raw.no_orders_animation);
                    binding.emptyOrdersView.playAnimation();
                }
                else {
                    binding.emptyOrdersView.setVisibility(View.GONE);
                }

            }
        });

        binding.calenderView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dateOfMonth) {

                String monthString = String.valueOf((month+1)), dateOfMonthString = String.valueOf(dateOfMonth);
                if(month+1 < 10){
                    monthString = "0" + (month+1);
                }

                if(dateOfMonth < 10){
                    dateOfMonthString = "0" + dateOfMonth;
                }

                String dateString = year + "-" + monthString + "-" + dateOfMonthString;

                binding.loading.setVisibility(View.VISIBLE);
                binding.loading.setAnimation(R.raw.searching_orders_animation);
                binding.loading.playAnimation();

                viewModel.getOrders(getContext(),shopPartner.getShopId(),shopPartner.getShopType(),6,dateString,1);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void updateOrderStatus(String orderId, int newOrderStatus, boolean shopReceivedPayment) {

    }

    @Override
    public void openOrderDetailsFragment(OrderItem orderItem) {
        Gson gson = new Gson();
        String orderItemsString = gson.toJson(orderItem);

        Bundle bundle = new Bundle();
        bundle.putString("orderItem",orderItemsString);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_homeFragment_to_orderDetailsFragment,bundle);
    }
}