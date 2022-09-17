package com.avit.apnamzp_partner.ui.reviews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentReviewsBinding;

public class ReviewsFragment extends Fragment {

    private FragmentReviewsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReviewsBinding.inflate(inflater,container,false);



        return binding.getRoot();
    }
}