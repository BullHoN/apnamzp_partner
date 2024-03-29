package com.avit.apnamzp_partner.ui.manage;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentManageBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.user.ShopPartner;
import com.avit.apnamzp_partner.models.user.ShopPrices;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.common.util.RetainForClient;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;

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


public class ManageFragment extends Fragment {

    private FragmentManageBinding binding;
    private ShopPartner shopPartner;
    private String TAG = "ManageFragment";
    private ActivityResultLauncher<Intent> bannerImagePickerLauncher;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater,container,false);
        gson = new Gson();

        shopPartner = LocalDB.getPartnerDetails(getContext());

        if(shopPartner.getBannerImage() != null && shopPartner.getBannerImage().length() > 0){
            Glide.with(getContext())
                    .load(shopPartner.getBannerImage())
                    .into(binding.bannerImage);
        }

        if(shopPartner.getShopTimings() != null){
            binding.shopTimings.setText(shopPartner.getShopTimings());
        }

        binding.shopName.setText(shopPartner.getName());
        binding.shopType.setText(shopPartner.getShopType());

        if(shopPartner.getFssaiCode() != null) binding.fssaiLicence.setText(shopPartner.getFssaiCode());

        binding.shopTagline.setText(shopPartner.getTagLine());

        if (shopPartner.getPricingDetails() == null){
            binding.shopMinOrderPrice.setText("0");
            binding.shopMinFreeDeliveryPrice.setText("0");
        }
        else {
            binding.shopMinOrderPrice.setText(shopPartner.getPricingDetails().getMinOrderPrice());
            binding.shopMinFreeDeliveryPrice.setText(shopPartner.getPricingDetails().getMinFreeDeliveryPrice());
        }


        binding.shopTaxPercentage.setText(shopPartner.getTaxPercentage());

        bannerImagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        shopPartner.setBannerImage(imageUri.getPath());
                        // Use the uri to load the image
                        Glide.with(getContext()).load(imageUri).into(binding.bannerImage);

                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        // Use ImagePicker.Companion.getError(result.getData()) to show an error
                        ImagePicker.Companion.getError(result.getData());
                    }
                });

        binding.bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });


        binding.saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tagline, minOrderPrice, minFreeDeliveryPrice, taxPercentage, fssaiCode, shopTimings;
                tagline = binding.shopTagline.getText().toString();
                minOrderPrice = binding.shopMinOrderPrice.getText().toString();
                minFreeDeliveryPrice = binding.shopMinFreeDeliveryPrice.getText().toString();
                taxPercentage = binding.shopTaxPercentage.getText().toString();
                fssaiCode = binding.fssaiLicence.getText().toString();
                shopTimings = binding.shopTimings.getText().toString();

                // TODO: Validation on everything

                if(fssaiCode != null && fssaiCode.length() > 0){
                    shopPartner.setFssaiCode(fssaiCode);
                }

                shopPartner.setTagLine(tagline);
                shopPartner.setPricingDetails(new ShopPrices(minOrderPrice,minFreeDeliveryPrice));
                shopPartner.setTaxPercentage(taxPercentage);
                shopPartner.setShopTimings(shopTimings);

                LocalDB.savePartnerDetails(getContext(),shopPartner);

                binding.loading.setVisibility(View.VISIBLE);
                binding.saveChangesButton.setEnabled(false);
                binding.loading.setAnimation(R.raw.upload_animation);
                binding.loading.playAnimation();

                sendDataToServer();
            }
        });

        return binding.getRoot();
    }

    private void sendDataToServer(){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        MultipartBody.Part bannerImagePart = null;
        if(shopPartner.getBannerImage() != null && shopPartner.getBannerImage().length() > 0
                && !shopPartner.getBannerImage().contains("http")){
            File file = new File(shopPartner.getBannerImage());
            RequestBody bannerImageRequestBody = RequestBody.create(MediaType.parse("image/*"),file);
            bannerImagePart = MultipartBody.Part.createFormData("banner_image", file.getName(),bannerImageRequestBody);
        }

        RequestBody someData = RequestBody.create(MediaType.parse("application/json"),gson.toJson(shopPartner));

        Call<NetworkResponse> call = networkApi.updateShopDetails(bannerImagePart,someData);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                Toasty.success(getContext(),"Update Successfull",Toasty.LENGTH_SHORT)
                        .show();

                binding.loading.setVisibility(View.GONE);
                binding.saveChangesButton.setEnabled(true);

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

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
                        bannerImagePickerLauncher.launch(it);
                    }
                }));
    }

}