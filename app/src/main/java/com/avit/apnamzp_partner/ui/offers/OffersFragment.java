package com.avit.apnamzp_partner.ui.offers;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.L;
import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentOffersBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.google.gson.Gson;

import java.lang.reflect.GenericSignatureFormatError;
import java.util.ArrayList;
import java.util.List;

public class OffersFragment extends Fragment implements OffersAdapter.deleteOfferInterface{

    private FragmentOffersBinding binding;
    private OffersViewModel viewModel;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOffersBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(OffersViewModel.class);

        gson = new Gson();

        binding.loading.setAnimation(R.raw.offers_loading);
        binding.loading.playAnimation();
        binding.loading.setVisibility(View.VISIBLE);

        viewModel.getDataFromServer(getContext(), LocalDB.getPartnerDetails(getContext()).getName());

        binding.offersList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        OffersAdapter offersAdapter = new OffersAdapter(getContext(),new ArrayList<>(),this);
        binding.offersList.setAdapter(offersAdapter);

        viewModel.getOffersListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<OfferItem>>() {
            @Override
            public void onChanged(List<OfferItem> offerItems) {
                offersAdapter.changeValues(offerItems);
                binding.loading.setVisibility(View.GONE);

                if(offerItems.size() == 0){
                    binding.emptyView.setVisibility(View.VISIBLE);
                    binding.emptyView.setAnimation(R.raw.no_orders_animation);
                    binding.emptyView.playAnimation();
                }
                else {
                    binding.emptyView.setVisibility(View.GONE);
                }

            }
        });

        binding.addNewOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.offerFragment);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void applyOffer(OfferItem offerItem) {
        Bundle bundle = new Bundle();
        String offerItemString = gson.toJson(offerItem);
        bundle.putString("offferItem",offerItemString);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_offersFragment_to_offerFragment,bundle);

    }
}