package com.avit.apnamzp_partner.ui.menuitem;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentMenuItemBinding;
import com.avit.apnamzp_partner.databinding.FragmentMenuItemsBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ItemAvailableTimings;
import com.avit.apnamzp_partner.models.shop.ShopItemData;
import com.avit.apnamzp_partner.models.shop.ShopPricingData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
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
    private MaterialTimePicker fromTime, endTime;
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
            binding.itemName.setEnabled(false);

            binding.itemDiscount.setText(shopItemData.getDiscount());
            binding.itemPackegingCharge.setText(shopItemData.getTaxOrPackigingPrice());

            binding.isBestSeller.setChecked(shopItemData.isBestSeller());
        }
        else {
            isNewMenuItem = true;
            shopItemData = new ShopItemData();
            binding.title.setText("Add New Menu Item");
            binding.saveChangesButton.setText("Add Item");
            binding.removeItem.setVisibility(View.GONE);
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
                if(priceType.length() == 0){
                    binding.priceType.setError("Please Enter Valid Price Type");
                    return;
                }

                if(price.length() == 0){
                    binding.price.setError("Please Enter Valid Price");
                    return;
                }

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

        binding.isBestSeller.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                shopItemData.setBestSeller(b);
            }
        });

        if(shopItemData.getAvailableTimings() != null){
            binding.fromTime.setText(shopItemData.getAvailableTimings().getFrom());
            binding.toTime.setText(shopItemData.getAvailableTimings().getTo());
        }

        fromTime = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(7)
                .setTitleText("Select From Time")
                .build();

        fromTime.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromTime.getHour() > 12){
                    int hour = fromTime.getHour() - 12;
                    String time = String.format("%02d",hour) + ":" + String.format("%02d",fromTime.getMinute()) + " " + "PM";
                    binding.fromTime.setText(time);
                }
                else if(fromTime.getHour() == 12){
                    String time = String.format("%02d",fromTime.getHour()) + ":" + String.format("%02d",fromTime.getMinute()) + " " + "PM";
                    binding.fromTime.setText(time);
                }
                else {
                    String time = String.format("%02d",fromTime.getHour()) + ":" + String.format("%02d",fromTime.getMinute()) + " " + "AM";
                    binding.fromTime.setText(time);
                }
            }
        });

        fromTime.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.fromTime.setText("N/A");
            }
        });

        endTime = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(7)
                .setTitleText("Select End Time")
                .build();

        endTime.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(endTime.getHour() > 12){
                    int hour = endTime.getHour() - 12;
                    String time = String.format("%02d",hour) + ":" + String.format("%02d",endTime.getMinute()) + " " + "PM";
                    binding.toTime.setText(time);
                }
                else if(endTime.getHour() == 12){
                    String time = String.format("%02d",endTime.getHour()) + ":" + String.format("%02d",endTime.getMinute()) + " " + "PM";
                    binding.toTime.setText(time);
                }
                else {
                    String time = String.format("%02d",endTime.getHour()) + ":" + String.format("%02d",endTime.getMinute()) + " " + "AM";
                    binding.toTime.setText(time);
                }
            }
        });

        endTime.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.toTime.setText("N/A");
            }
        });


        binding.fromTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromTime.show(getActivity().getSupportFragmentManager(),"From Time");
            }
        });

        binding.endTimeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTime.show(getActivity().getSupportFragmentManager(),"End Time");
            }
        });

        binding.saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemName = binding.itemName.getText().toString();
                String discount = binding.itemDiscount.getText().toString();
                String packigingCharge = binding.itemPackegingCharge.getText().toString();

                if(shopItemData.getPricings().size() == 0){
                    Toasty.error(getContext(),"Please Add Pricing Details",Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                // TODO: Validation

                shopItemData.setName(itemName);
                shopItemData.setDiscount(discount);
                shopItemData.setTaxOrPackigingPrice(packigingCharge);

                if(discount.length() == 0){
                    shopItemData.setDiscount("0");
                }

                if(packigingCharge.length() == 0){
                    shopItemData.setTaxOrPackigingPrice("0");
                }


                if(!binding.fromTime.getText().equals("N/A") &&
                    !binding.toTime.getText().equals("N/A")){
                    shopItemData.setAvailableTimings(new ItemAvailableTimings(binding.fromTime.getText().toString(),
                            binding.toTime.getText().toString()));
                }else {
                    shopItemData.setAvailableTimings(null);
                }

                String shopMenuItemsId = LocalDB.getPartnerDetails(getContext()).getShopItemsId();
//                saveChangesToServer(shopMenuItemsId, categoryName);

                binding.loading.setVisibility(View.VISIBLE);
                binding.saveChangesButton.setEnabled(false);
                binding.loading.setAnimation(R.raw.upload_animation);
                binding.loading.playAnimation();

                putMenuItemToServer(shopMenuItemsId,categoryName,false);
            }
        });


        binding.removeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shopMenuItemsId = LocalDB.getPartnerDetails(getContext()).getShopItemsId();

                binding.loading.setVisibility(View.VISIBLE);
                binding.saveChangesButton.setEnabled(false);
                binding.loading.setAnimation(R.raw.upload_animation);
                binding.loading.playAnimation();

                putMenuItemToServer(shopMenuItemsId,categoryName,true);
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

    private void putMenuItemToServer(String shopMenuItemsId, String categoryName,boolean deleteItem){
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

        Call<NetworkResponse> call = networkApi.putMenuItem(deleteItem,shopMenuItemsId,categoryName,isNewMenuItem,imagePart,someData);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                Toasty.success(getContext(),"Update Successfull", Toasty.LENGTH_SHORT)
                        .show();
                binding.loading.setVisibility(View.GONE);
                binding.saveChangesButton.setEnabled(true);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_menuItemFragment_to_menuItemsFragment);
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

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

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