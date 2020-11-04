package com.twopibd.dactarbari.ambulance.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.twopibd.dactarbari.ambulance.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeBottomSheet extends BottomSheetDialogFragment {


    public HomeBottomSheet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_bottom_sheet, container, false);
    }

}
