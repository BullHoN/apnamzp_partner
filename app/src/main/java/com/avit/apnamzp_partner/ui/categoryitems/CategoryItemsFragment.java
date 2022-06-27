package com.avit.apnamzp_partner.ui.categoryitems;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentCategoryItemsBinding;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.google.gson.Gson;

public class CategoryItemsFragment extends Fragment implements CategoryItemsAdapter.CategoryItemsActions{

    private FragmentCategoryItemsBinding binding;
    private ShopCategoryData shopCategoryData;
    private CategoryItemsAdapter categoryItemsAdapter;
    private String TAG = "CategoryItemsFragment";
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
        categoryItemsAdapter = new CategoryItemsAdapter(shopCategoryData.getShopItemDataList(),getContext(),this);
        binding.categoryItemsRecyclerview.setAdapter(categoryItemsAdapter);
        binding.categoryItemsRecyclerview.setNestedScrollingEnabled(false);


        return binding.getRoot();
    }

    @Override
    public void openEditFragment(ShopItemData shopItemData) {
        String shopItemText = gson.toJson(shopItemData);
        Bundle bundle = new Bundle();
        bundle.putString("menuItem", shopItemText);
//        Log.i(TAG, "openEditFragment: " + shopCategoryData.getCategoryName());
        bundle.putString("categoryName", shopCategoryData.getCategoryName());

        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_categoryItemsFragment_to_menuItemFragment,bundle);

    }
}