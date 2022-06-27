package com.avit.apnamzp_partner.ui.menuitem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentMenuItemBinding;
import com.avit.apnamzp_partner.databinding.FragmentMenuItemsBinding;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MenuItemFragment extends Fragment {

    private FragmentMenuItemBinding binding;
    private Gson gson;
    private ShopItemData shopItemData;
    private EditablePricingAdapter editablePricingAdapter;
    private String TAG = "MenuItemFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuItemBinding.inflate(inflater,container,false);
        gson = new Gson();

        Bundle bundle = getArguments();
        if(bundle != null){
            String shopItemString = bundle.getString("menuItem");
            String categoryName = bundle.getString("categoryName");

            shopItemData = gson.fromJson(shopItemString,ShopItemData.class);

            if(shopItemData.getImageURL() != null && shopItemData.getImageURL().length() > 0){
                Glide.with(getContext())
                        .load(shopItemData.getImageURL())
                        .into(binding.itemImage);
            }
            else {
                // TODO: Default Image
            }

            binding.isVeg.setChecked(shopItemData.getVeg());
            binding.isAvailable.setChecked(shopItemData.getAvailable());

            binding.itemName.setText(shopItemData.getName());
            binding.itemDiscount.setText(shopItemData.getDiscount());
            binding.itemPackegingCharge.setText(shopItemData.getTaxOrPackigingPrice());

            editablePricingAdapter = new EditablePricingAdapter(getContext(),shopItemData.getPricings());
            binding.itemPricingRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
            binding.itemPricingRecyclerview.setAdapter(editablePricingAdapter);

            // Add Listeners for everything
            binding.isVeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    shopItemData.setVeg(b);
                }
            });

            binding.isAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    shopItemData.setAvailable(b);
                }
            });

            binding.saveChangesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String itemName = binding.itemName.getText().toString();
                    String discount = binding.itemDiscount.getText().toString();
                    String packigingCharge = binding.itemPackegingCharge.getText().toString();

                    // TODO: Validation

                    shopItemData.setName(itemName);
                    shopItemData.setDiscount(discount);
                    shopItemData.setTaxOrPackigingPrice(packigingCharge);

                    // TODO: GET THIS FROM THE LOCAL STORAGE
                    String shopMenuItemsId = "6174fea0dbb0b2e38f7de220";
                    saveChangesToServer(shopMenuItemsId, categoryName);
                }
            });

        }

        return binding.getRoot();
    }

    private void saveChangesToServer(String shopMenuItemsId, String categoryName){

        Log.i(TAG, "saveChangesToServer: " + shopMenuItemsId);
        Log.i(TAG, "saveChangesToServer: " + categoryName);
        Log.i(TAG, "saveChangesToServer: " + gson.toJson(shopItemData));

        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.updateMenuItem(shopMenuItemsId, categoryName, shopItemData);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                Toasty.success(getContext(),"Updated Successfully",Toasty.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

}