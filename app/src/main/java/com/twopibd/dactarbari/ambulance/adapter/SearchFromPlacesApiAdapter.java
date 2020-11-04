package com.twopibd.dactarbari.ambulance.adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.activity.AmbulanceOrderActivity;
import com.twopibd.dactarbari.ambulance.activity.LiteHomeActivity;
import com.twopibd.dactarbari.ambulance.activity.MapsActivity;
import com.twopibd.dactarbari.ambulance.activity.SelectVehicleActivity;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.model.Prediction;
import com.twopibd.dactarbari.ambulance.model.RideRequestModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.twopibd.dactarbari.ambulance.data.sharedData.AMBULANCE_MODEL;
import static com.twopibd.dactarbari.ambulance.data.sharedData.RIDE_REQUEST_MODEL;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_DISTANCE;


public class SearchFromPlacesApiAdapter extends RecyclerView.Adapter<SearchFromPlacesApiAdapter.MyViewHolder> {
    private Context context;
    private List<Prediction> listFiltered = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_address, tv_model, tv_driver, tv_fair;
        ImageView img;
        View divider;


        public MyViewHolder(View view) {
            super(view);
            //  tv_name = view.findViewById(R.id.tv_name);
            tv_name = view.findViewById(R.id.tv_name);
            tv_address = view.findViewById(R.id.tv_address);


        }
    }


    public SearchFromPlacesApiAdapter(Context context, List<Prediction> list_) {
        this.context = context;
        this.listFiltered = list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);

        return new MyViewHolder(itemView);
    }

    //https://gl-images.condecdn.net/image/lN39xbMKeop/crop/405/f/Gal-Gadot-1.jpg
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String user_id = "mukul_8";
        final Prediction data = listFiltered.get(position);

        holder.tv_name.setText(data.getDescription());
        holder.tv_address.setText(data.getStructuredFormatting().getSecondaryText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiteHomeActivity.toLocation = data.getDescription();
                LiteHomeActivity.placeID = data.getPlace_id();
                holder.tv_name.getContext().startActivity(new Intent(  holder.tv_name.getContext(), SelectVehicleActivity.class));
            }
        });


    }


    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss EEE dd MMM yy");
        return formatter.format(new Date(milliSeconds));
    }

    @Override
    public int getItemCount() {
        return listFiltered.size();

    }


}