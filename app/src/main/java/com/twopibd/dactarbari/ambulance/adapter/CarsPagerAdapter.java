package com.twopibd.dactarbari.ambulance.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.activity.MapsActivity;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by mohak on 29/7/17.
 */

public class CarsPagerAdapter extends PagerAdapter {


    List<String> typeList;
    List<List<AmbulanceModel>> ambulanceData;
    String passedtext;

    public CarsPagerAdapter(List<String> titleList, List<List<AmbulanceModel>> ambulanceModels) {

        this.typeList = titleList;
        this.ambulanceData = ambulanceModels;
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        TextView textView;
        // view = (View) inflater.inflate(R.layout.u_non_ac, null);


        if (position == 0) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.u_non_ac, container, false);
        } else if (position == 1) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.u_non_ac, container, false);
        } else if (position == 2) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.u_non_ac, container, false);
        } else {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.u_non_ac, container, false);
        }


        textView = (TextView) view.findViewById(R.id.tvuberEconomy);


        textView.setText(typeList.get(position));

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);

        ambulanceAdapters mAdapter = new ambulanceAdapters(container.getContext(), ambulanceData.get(position));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(container.getContext(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((View) object);
    }
}
