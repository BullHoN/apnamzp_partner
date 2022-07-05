package com.avit.apnamzp_partner.ui.manage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avit.apnamzp_partner.R;
import com.avit.apnamzp_partner.databinding.FragmentManageBinding;
import com.avit.apnamzp_partner.db.LocalDB;


public class ManageFragment extends Fragment {

    private FragmentManageBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater,container,false);


        return binding.getRoot();
    }
}