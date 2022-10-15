package com.avit.apnamzp_partner.ui.menuitems;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentMenuItemsBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.network.NetworkResponse;
import com.avit.apnamzp_partner.models.shop.ShopCategoryData;
import com.avit.apnamzp_partner.network.NetworkApi;
import com.avit.apnamzp_partner.network.RetrofitClient;
import com.avit.apnamzp_partner.utils.ErrorUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class menuItemsFragment extends Fragment implements MenuItemsCategoryAdapter.CategoriesActions{

    private FragmentMenuItemsBinding binding;
    private MenuItemsViewModel viewModel;
    private String TAG = "menuItemsFragment";
    private String shopItemsId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuItemsBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(MenuItemsViewModel.class);

        binding.categoriesRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        MenuItemsCategoryAdapter adapter = new MenuItemsCategoryAdapter(new ArrayList<>(),getContext(),this);
        binding.categoriesRecyclerview.setAdapter(adapter);

        binding.loading.setVisibility(View.VISIBLE);
        binding.loading.setAnimation(R.raw.categories_loading_animation);
        binding.loading.playAnimation();

        shopItemsId = LocalDB.getPartnerDetails(getContext()).getShopItemsId();
        viewModel.getCategoriesFromServer(getContext(),shopItemsId);

        viewModel.getShopCategoryDataMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<ShopCategoryData>>() {
            @Override
            public void onChanged(List<ShopCategoryData> shopCategoryData) {
                binding.loading.setVisibility(View.GONE);
                adapter.updateItems(shopCategoryData);
            }
        });

        binding.addNewCategoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewCategoryDialogBox();
            }
        });

        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.searchItems(query);
                return false;
            }
        });

        return binding.getRoot();
    }

    private void showNewCategoryDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_new_category,null, false);

        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        TextInputEditText textInputEditText = view.findViewById(R.id.category_name);

        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = textInputEditText.getText().toString();
                // TODO: Validation

                Log.i(TAG, "onClick: " + categoryName);
                sendNewCategoryToServer(shopItemsId, categoryName);
                alertDialog.dismiss();
            }
        });


    }

    private void sendNewCategoryToServer(String shopItemsId, String categoryName){
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        binding.loading.setVisibility(View.VISIBLE);
        binding.loading.setAnimation(R.raw.categories_loading_animation);
        binding.loading.playAnimation();

        Call<NetworkResponse> call = networkApi.addNewCategory(shopItemsId, new ShopCategoryData(categoryName));
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {

                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                Toasty.success(getContext(),"Added Successfully",Toasty.LENGTH_SHORT)
                        .show();

                binding.loading.setVisibility(View.GONE);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.menuItemsFragment);
            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void openCategoryMenuItems(ShopCategoryData shopCategoryData, String query) {
        Gson gson = new Gson();
        String shopCategoryDataString = gson.toJson(shopCategoryData);

        Bundle bundle = new Bundle();
        bundle.putString("shopCategoryData",shopCategoryDataString);
        bundle.putString("query",query);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_menuItemsFragment_to_categoryItemsFragment,bundle);
    }

    @Override
    public void deleteCategory(ShopCategoryData shopCategoryData) {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.editCategories("delete",shopCategoryData.getCategoryName(),shopItemsId,null);
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
                Navigation.findNavController(binding.getRoot()).navigate(R.id.menuItemsFragment);

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public void editCategoryName(ShopCategoryData shopCategoryData, String newCategoryName) {
        Retrofit retrofit = RetrofitClient.getInstance();
        NetworkApi networkApi = retrofit.create(NetworkApi.class);

        Call<NetworkResponse> call = networkApi.editCategories("edit",shopCategoryData.getCategoryName(),shopItemsId,newCategoryName);
        call.enqueue(new Callback<NetworkResponse>() {
            @Override
            public void onResponse(Call<NetworkResponse> call, Response<NetworkResponse> response) {
                if(!response.isSuccessful()){
                    NetworkResponse errorResponse = ErrorUtils.parseErrorResponse(response);
                    Toasty.error(getContext(),errorResponse.getDesc(),Toasty.LENGTH_SHORT)
                            .show();
                    return;
                }

                Navigation.findNavController(binding.getRoot()).navigate(R.id.menuItemsFragment);

            }

            @Override
            public void onFailure(Call<NetworkResponse> call, Throwable t) {
                Toasty.error(getContext(),t.getMessage(),Toasty.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        shopItemsId = LocalDB.getPartnerDetails(getContext()).getShopItemsId();
        viewModel.getCategoriesFromServer(getContext(),shopItemsId);
    }
}