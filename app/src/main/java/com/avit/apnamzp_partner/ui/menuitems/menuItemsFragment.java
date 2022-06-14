package com.avit.apnamzp_partner.ui.menuitems;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentMenuItemsBinding;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;

import java.util.ArrayList;
import java.util.List;

public class menuItemsFragment extends Fragment {

    private FragmentMenuItemsBinding binding;
    private MenuItemsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuItemsBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(MenuItemsViewModel.class);

        binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        MenuItemsCategoryAdapter adapter = new MenuItemsCategoryAdapter(new ArrayList<>(),getContext());
        binding.categoriesRecyclerview.setAdapter(adapter);


        // TODO: GET THE SHOPITEMS ID
        viewModel.getCategoriesFromServer("6174fea0dbb0b2e38f7de220");

        viewModel.getShopCategoryDataMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ShopCategoryData>>() {
            @Override
            public void onChanged(List<ShopCategoryData> shopCategoryData) {
                adapter.updateItems(shopCategoryData);
            }
        });

        return binding.getRoot();
    }
}