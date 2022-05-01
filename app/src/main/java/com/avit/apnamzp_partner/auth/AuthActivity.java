package com.avit.apnamzp_partner.auth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ValidateInput;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthActivity extends AppCompatActivity {

    private LinearLayout loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

    }

    private void showLoginDialog(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_login,null);

        Button signInUserButton = view.findViewById(R.id.signin_button);
        EditText phoneNoView = view.findViewById(R.id.phoneNo);
        EditText passwordView = view.findViewById(R.id.password);

        signInUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = phoneNoView.getText().toString();
                String password = passwordView.getText().toString();

                if(!ValidateInput.isValidPhoneNo(phoneNo) && !ValidateInput.isValidPassword(password)){
                    return;
                }

                logIn(phoneNo,password);

            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }


    private void logIn(String phoneNo,String password){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<ResponseBody> call = networkApi.login(phoneNo,password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toasty.success(getApplicationContext(),"Login Successfull",Toasty.LENGTH_SHORT)
                        .show();
                // SAVE IN SHARED PRWF
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toasty.error(getApplicationContext(),t.getMessage(),Toasty.LENGTH_LONG)
                        .show();
            }
        });

    }


}