package com.avit.apnamzp_partner.ui.reviews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentReviewsBinding;
import com.avit.apnamzp_partner.db.LocalDB;
import com.avit.apnamzp_partner.models.shop.ReviewData;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends Fragment implements ReviewsAdapter.ReviewsActions {

    private FragmentReviewsBinding binding;
    private ReviewsAdapter reviewsAdapter;
    private ReviewsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReviewsBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);

        viewModel.getReviews(getContext(), LocalDB.getPartnerDetails(getContext()).getShopId());
        binding.shopReviews.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        reviewsAdapter = new ReviewsAdapter(getContext(),new ArrayList<>(),this);
        binding.shopReviews.setAdapter(reviewsAdapter);


        viewModel.getMutableReviewsLiveData().observe(getViewLifecycleOwner(), new Observer<List<ReviewData>>() {
            @Override
            public void onChanged(List<ReviewData> reviewData) {
                reviewsAdapter.changeData(reviewData);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void openReviewOrder(String orderId) {
        Bundle bundle = new Bundle();
        bundle.putString("orderId",orderId);

        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_reviewsFragment_to_orderDetailsFragment,bundle);

    }
}