package com.twopibd.dactarbari.ambulance.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.twopibd.dactarbari.ambulance.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarsFragments extends BottomSheetDialogFragment {


    public CarsFragments() {
        // Required empty public constructor
    }

    public static CarsFragments newInstance(){
        return  new CarsFragments();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_cars_fragments, container, false);
    }

}
