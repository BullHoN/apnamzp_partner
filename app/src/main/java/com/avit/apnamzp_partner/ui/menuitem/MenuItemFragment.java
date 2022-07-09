package com.avit.apnamzp_partner.ui.menuitem;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.models.shop.ShopPricingData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import es.dmoral.toasty.Toasty;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MenuItemFragment extends Fragment {

    private FragmentMenuItemBinding binding;
    private Gson gson;
    private ShopItemData shopItemData;
    private EditablePricingAdapter editablePricingAdapter;
    private boolean isNewMenuItem;
    private ActivityResultLauncher<Intent> itemPickingLauncher;
    private String TAG = "MenuItemFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuItemBinding.inflate(inflater, container, false);
        gson = new Gson();

        Bundle bundle = getArguments();
        String shopItemString = bundle.getString("menuItem");
        String categoryName = bundle.getString("categoryName");

        if(shopItemString != null) {
            isNewMenuItem = false;
            shopItemData = gson.fromJson(shopItemString, ShopItemData.class);

            if (shopItemData.getImageURL() != null && shopItemData.getImageURL().length() > 0) {
                Glide.with(getContext())
                        .load(shopItemData.getImageURL())
                        .into(binding.itemImage);
            } else {
                // TODO: Default Image
            }

            binding.isVeg.setChecked(shopItemData.getVeg());
            binding.isAvailable.setChecked(shopItemData.getAvailable());

            binding.itemName.setText(shopItemData.getName());
            binding.itemDiscount.setText(shopItemData.getDiscount());
            binding.itemPackegingCharge.setText(shopItemData.getTaxOrPackigingPrice());
        }
        else {
            isNewMenuItem = true;
            shopItemData = new ShopItemData();
            binding.title.setText("Add New Menu Item");
            binding.saveChangesButton.setText("Add Item");
        }

        itemPickingLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        shopItemData.setImageURL(imageUri.getPath());
                        // Use the uri to load the image
                        Glide.with(getContext()).load(imageUri).into(binding.itemImage);

                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                        ImagePicker.Companion.getError(result.getData());
                    }
                });

        binding.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        editablePricingAdapter = new EditablePricingAdapter(getContext(), shopItemData.getPricings(), new EditablePricingAdapter.EditablePricingActionsInterface() {
            @Override
            public void removePricing(ShopPricingData shopPricingData) {
                shopItemData.removePricing(shopPricingData);
                editablePricingAdapter.updateItemsPricingList(shopItemData.getPricings());
            }
        });
        binding.itemPricingRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.itemPricingRecyclerview.setAdapter(editablePricingAdapter);

        binding.addPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceType = binding.priceType.getText().toString();
                String price = binding.price.getText().toString();

                // TODO: Validation

                shopItemData.addPricing(priceType,price);
                editablePricingAdapter.updateItemsPricingList(shopItemData.getPricings());

                binding.priceType.setText("");
                binding.price.setText("");

            }
        });

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

                String shopMenuItemsId = LocalDB.getPartnerDetails(getContext()).getShopItemsId();
//                saveChangesToServer(shopMenuItemsId, categoryName);
                putMenuItemToServer(shopMenuItemsId,categoryName);
            }
        });



        return binding.getRoot();
    }

    private void pickImage(){
        ImagePicker.Companion.with(getActivity())
                .crop()
                .cropSquare()
                .maxResultSize(500,500,true)
                .provider(ImageProvider.BOTH)
                .createIntentFromDialog((Function1)(new Function1(){
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public final void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        itemPickingLauncher.launch(it);
                    }
                }));
    }

    private void putMenuItemToServer(String shopMenuItemsId, String categoryName){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        MultipartBody.Part imagePart = null;
        if(shopItemData.getImageURL().contains("http")){

        }
        else if(shopItemData.getImageURL() != null && shopItemData.getImageURL().length() > 0){
            File imageFile = new File(shopItemData.getImageURL());
            RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("item_image", imageFile.getName(),imageBody);
        }

        RequestBody someData = RequestBody.create(MediaType.parse("application/json"),gson.toJson(shopItemData));

        Call<NetworkResponse> call = networkApi.putMenuItem(shopMenuItemsId,categoryName,isNewMenuItem,imagePart,someData);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                Toasty.success(getContext(),"Update Successfull", Toasty.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    private void saveChangesToServer(String shopMenuItemsId, String categoryName) {

        Log.i(TAG, "saveChangesToServer: " + shopMenuItemsId);
        Log.i(TAG, "saveChangesToServer: " + categoryName);
        Log.i(TAG, "saveChangesToServer: " + gson.toJson(shopItemData));

        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.updateMenuItem(shopMenuItemsId, categoryName,isNewMenuItem ,shopItemData);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                Toasty.success(getContext(), "Updated Successfully", Toasty.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(), t.getMessage(), Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

}