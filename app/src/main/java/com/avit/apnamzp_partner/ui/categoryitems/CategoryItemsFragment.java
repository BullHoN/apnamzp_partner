package com.avit.apnamzp_partner.ui.categoryitems;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentCategoryItemsBinding;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.google.gson.Gson;

public class CategoryItemsFragment extends Fragment {

    private FragmentCategoryItemsBinding binding;
    private ShopCategoryData shopCategoryData;
    private CategoryItemsAdapter categoryItemsAdapter;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryItemsBinding.inflate(inflater,container,false);

        Bundle bundle = getArguments();
        String shopCategoryDataString = bundle.getString("shopCategoryData");

        gson = new Gson();
        shopCategoryData = gson.fromJson(shopCategoryDataString,ShopCategoryData.class);

        binding.categoryItemsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        categoryItemsAdapter = new CategoryItemsAdapter(shopCategoryData.getShopItemDataList(),getContext());
        binding.categoryItemsRecyclerview.setAdapter(categoryItemsAdapter);
        binding.categoryItemsRecyclerview.setNestedScrollingEnabled(false);


        return binding.getRoot();
    }
}