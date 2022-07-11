package com.avit.apnamzp_partner.ui.offer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentOfferBinding;
import com.avit.apnamzp_partner.models.offer.OfferItem;
import com.google.gson.Gson;


public class OfferFragment extends Fragment {

    private FragmentOfferBinding binding;
    private Gson gson;
    private OfferItem offerItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfferBinding.inflate(inflater,container,false);
        gson = new Gson();

        Bundle bundle = getArguments();
        if(bundle != null){
            String offerItemString = bundle.getString("offferItem");
            offerItem = gson.fromJson(offerItemString,OfferItem.class);
        }
        else {
            offerItem = new OfferItem();
        }

        String offerTypes[] = {"flat","percent"};

        ArrayAdapter ad = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,offerTypes);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.offerType.setAdapter(ad);

        binding.offerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return binding.getRoot();
    }
}