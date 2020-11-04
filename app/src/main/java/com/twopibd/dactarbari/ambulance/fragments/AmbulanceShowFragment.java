package com.twopibd.dactarbari.ambulance.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.adapter.ambulanceAdapters;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AmbulanceShowFragment extends Fragment {
    View view;
    Context context;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_no_ambulance)
    TextView tv_no_ambulance;
    List<AmbulanceModel> list = new ArrayList<>();


    public AmbulanceShowFragment(List<AmbulanceModel> list_) {
        this.list = list_;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ambulance_show, container, false);
        context = view.getContext();
        ButterKnife.bind(this, view);

        if (list.size() > 0) {
            tv_no_ambulance.setVisibility(View.GONE);

            ambulanceAdapters mAdapter = new ambulanceAdapters(context, list);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
            recyclerview.setLayoutManager(mLayoutManager);
            recyclerview.setItemAnimator(new DefaultItemAnimator());
            // recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
            //recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, false));
            recyclerview.setAdapter(mAdapter);
        } else {
            tv_no_ambulance.setVisibility(View.VISIBLE);
        }
        return view;
    }

}
