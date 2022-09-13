package com.avit.apnamzp_partner.ui.registernewshowform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ShopRegistrationPostData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.google.android.material.textfield.TextInputEditText;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterNewShopActivity extends AppCompatActivity {

    private String shopTypes[] = {"Sweets","Pure Vegetarian Resturants","Non veg Resturants","Street Food","Home Chefs",
            "Groceries", "Medicines"};
    private Spinner shopTypesSpinner;
    private TextInputEditText shopNameView, shopAddressView, shopContactNumberView, shopWhatsappNumberView;
    private ShopRegistrationPostData postData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_shop);

        postData = new ShopRegistrationPostData();
        shopTypesSpinner = findViewById(R.id.shop_type);
        shopNameView = findViewById(R.id.shopName);
        shopAddressView = findViewById(R.id.shopAddress);
        shopContactNumberView = findViewById(R.id.shopContact);
        shopWhatsappNumberView = findViewById(R.id.shopWhatsapp);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,shopTypes);
        shopTypesSpinner.setAdapter(arrayAdapter);

        shopTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                postData.setShopType(shopTypes[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                postData.setShopType(shopTypes[0]);
            }
        });

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shopName, shopAddress, shopContactNumber, shopWhatsappNumber;

                shopName = shopNameView.getText().toString();
                shopAddress = shopAddressView.getText().toString();
                shopContactNumber = shopContactNumberView.getText().toString();
                shopWhatsappNumber = shopWhatsappNumberView.getText().toString();

                if(shopName.length() == 0 || shopAddress.length() == 0 || shopContactNumber.length() != 10
                    || shopWhatsappNumber.length() != 10){
                    Toasty.error(getApplicationContext(),"All Fields Are Required",Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                postData.setName(shopName);
                postData.setAddress(shopAddress);
                postData.setPhoneNo(shopContactNumber);
                postData.setWhatsappNumber(shopWhatsappNumber);

                sendShopRegistrationData();
            }
        });

    }

    private void sendShopRegistrationData(){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.sendRegisterForm(postData);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getApplicationContext(),errorResponse.getDesc(),Toasty.LENGTH_LONG)
                            .show();
                    return;
                }

                Toasty.success(getApplicationContext(),"Our Team will Contact you",Toasty.LENGTH_LONG)
                        .show();
                finish();

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getApplicationContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });

    }

}