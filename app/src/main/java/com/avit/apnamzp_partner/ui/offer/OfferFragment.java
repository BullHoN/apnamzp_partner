package com.avit.apnamzp_partner.ui.offer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentOfferBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.avit.apnamzp_partner.utils.ValidateInput;
import com.google.gson.Gson;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class OfferFragment extends Fragment {

    private FragmentOfferBinding binding;
    private Gson gson;
    private OfferItem offerItem;
    private String shopName;
    private String TAG = "OfferFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfferBinding.inflate(inflater,container,false);
        gson = new Gson();

        shopName = LocalDB.getPartnerDetails(getContext()).getName();

        Bundle bundle = getArguments();
        if(bundle != null){
            String offerItemString = bundle.getString("offferItem");
            offerItem = gson.fromJson(offerItemString,OfferItem.class);

            binding.discountAbove.setText(offerItem.getDiscountAbove());

            if(offerItem.getOfferType().equals("flat")){
                binding.discountAmount.setText(offerItem.getDiscountAmount());
            }
            else {
                binding.discountPercentage.setText(offerItem.getDiscountPercentage());
                binding.maxDiscount.setText(offerItem.getMaxDiscount());
            }

        }
        else {
            offerItem = new OfferItem();
            offerItem.setShopName(shopName);
        }

        String offerTypes[] = {"flat","percent"};

        ArrayAdapter ad = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,offerTypes);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.offerType.setAdapter(ad);

        if(offerItem.getOfferType() != null && offerItem.getOfferType().length() > 0){
            binding.offerType.setSelection((offerItem.getOfferType().equals("flat") ? 0 : 1));
        }

        binding.offerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                offerItem.setOfferType(offerTypes[i]);
                if(i == 1){
                    binding.discountAmountContainer.setVisibility(View.GONE);

                    binding.discountPercentageContainer.setVisibility(View.VISIBLE);
                    binding.maxDiscountContainer.setVisibility(View.VISIBLE);
                }
                else {
                    binding.discountPercentageContainer.setVisibility(View.GONE);
                    binding.maxDiscountContainer.setVisibility(View.GONE);

                    binding.discountAmountContainer.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String discountPercentage, maxDiscount, discountAmount, discountAbove;

                // TODO: Validation
                if(!ValidateInput.isNumber(binding.discountAbove.getText().toString())){
                    binding.discountAbove.setError("Enter a valid Number");
                    return;
                }

                discountAbove = binding.discountAbove.getText().toString();
                offerItem.setDiscountAbove(discountAbove);

                if(offerItem.getOfferType().equals("flat")){
                    discountAmount = binding.discountAmount.getText().toString();

                    if(!ValidateInput.isNumber(discountAmount)){
                        binding.discountAmount.setError("Enter a valid Number");
                        return;
                    }

                    offerItem.setDiscountAmount(discountAmount);
                }
                else {
                    discountPercentage = binding.discountPercentage.getText().toString();
                    maxDiscount = binding.maxDiscount.getText().toString();

                    if(!ValidateInput.isNumber(discountPercentage)){
                        binding.discountPercentage.setError("Enter a valid Number");
                        return;
                    }

                    if(!ValidateInput.isNumber(maxDiscount)){
                        binding.maxDiscount.setError("Enter a valid Number");
                        return;
                    }

                    offerItem.setDiscountPercentage(discountPercentage);
                    offerItem.setMaxDiscount(maxDiscount);
                }

                if(offerItem.getCode() == null || offerItem.getCode().length() == 0){
                    offerItem.setCode(generateCode());
                }

                offerItem.setShopId(LocalDB.getPartnerDetails(getContext()).getShopId());

                saveToServer();
            }
        });

        if(offerItem.get_id() != null && offerItem.get_id().length() > 0){
            binding.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteOffer();
                }
            });
        }


        return binding.getRoot();
    }

    private void deleteOffer(){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.deleteOffer(offerItem.get_id(),LocalDB.getPartnerDetails(getContext()).getShopId());
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                NetworkResponse successResponse = response.body();
                if(successResponse.isSuccess()){
                    Toasty.success(getContext(),"Deleted Successfully",Toasty.LENGTH_SHORT)
                            .show();
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                }
                else{
                    Toasty.error(getContext(),successResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {

            }
        });

    }

    private void saveToServer(){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.putOffer(offerItem);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                NetworkResponse successResponse = response.body();
                if(successResponse.isSuccess()){
                    Toasty.success(getContext(),"Updated Successfully",Toasty.LENGTH_SHORT)
                            .show();
                    Navigation.findNavController(binding.getRoot()).popBackStack();
                }
                else {
                    Toasty.error(getContext(),successResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
                Log.e(TAG, "onFailure: ", t);
            }
        });

    }

    private String generateCode(){
        StringBuilder stringBuilder = new StringBuilder();
        String shopOfferName = shopName.toUpperCase().replace(" ","");
        if(shopOfferName.length() <= 4){
            stringBuilder.append(shopOfferName);
        }
        else {
            stringBuilder.append(shopOfferName.substring(0,5));
        }

        for(int i=0;i<4;i++){
            stringBuilder.append(Math.round(Math.random()*10));
        }

        return stringBuilder.toString();
    }

}