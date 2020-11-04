package com.twopibd.dactarbari.ambulance.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.activity.AmbulanceOrderActivity;
import com.twopibd.dactarbari.ambulance.activity.MapsActivity;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.model.RideRequestModel;
import com.twopibd.dactarbari.ambulance.utils.doForMe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.twopibd.dactarbari.ambulance.data.sharedData.AMBULANCE_MODEL;
import static com.twopibd.dactarbari.ambulance.data.sharedData.RIDE_REQUEST_MODEL;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_DISTANCE;


public class ambulanceAdapters extends RecyclerView.Adapter<ambulanceAdapters.MyViewHolder> {
    private Context context;
    private List<AmbulanceModel> listFiltered = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_ex, tv_model, tv_driver, tv_fair;
        ImageView img;
        View divider;


        public MyViewHolder(View view) {
            super(view);
            //  tv_name = view.findViewById(R.id.tv_name);
            img = view.findViewById(R.id.img);
            tv_fair = view.findViewById(R.id.tv_fair);
            tv_driver = view.findViewById(R.id.tv_driver);
            tv_model = view.findViewById(R.id.tv_model);


        }
    }


    public ambulanceAdapters(Context context, List<AmbulanceModel> list_) {
        this.context = context;
        this.listFiltered = list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ambulance_item, parent, false);

        return new MyViewHolder(itemView);
    }

    //https://gl-images.condecdn.net/image/lN39xbMKeop/crop/405/f/Gal-Gadot-1.jpg
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String user_id = "mukul_8";
        final AmbulanceModel data = listFiltered.get(position);
        Glide.with(holder.itemView.getContext()).load(data.getCarImage()).into(holder.img);
        Float BASE = Float.valueOf(data.getCarBaseCharge());
        Float DISTANCE = Float.valueOf(TRAVEL_DISTANCE / 1000);
        //int FAIR_PER_KM=data.getCarFairPerKm();
        Float total_fair = Float.valueOf(data.getCarBaseCharge()) + ((TRAVEL_DISTANCE / 1000) * data.getCarFairPerKm());

        Log.i("mkl_check_fair", "Bse " + Double.valueOf(data.getCarBaseCharge()) + "  TRAVEL_DISTANCE   " + TRAVEL_DISTANCE + "    fair per km  " + data.getCarFairPerKm());
        Log.i("mkl_check_fair", "now add " + total_fair);
        // total_fair = total_fair / 1000;
        Log.i("mkl_check_fair", "now add 2 " + total_fair);

        holder.tv_driver.setText(data.getCarDriverName());
        holder.tv_model.setText(data.getCarModel());
        holder.tv_fair.setText("" + total_fair + " BDT");
        Float finalTotal_fair = total_fair;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent=new Intent(context, AmbulanceOrderActivity.class);
               // intent.putExtra("from",MapsActivity.picLocationAddress);
               // intent.putExtra("to", MapsActivity.picLocationAddress_destination);
                context.startActivity(intent);


               /*
                Dialog dialog = doForMe.showDialog(holder.itemView.getContext(), R.layout.send_request_dialog);
                TextView picup = (TextView) dialog.findViewById(R.id.tv_picup);
                TextView tv_destination = (TextView) dialog.findViewById(R.id.tv_destination);
                CardView cardSubmit = (CardView) dialog.findViewById(R.id.cardSubmit);
                picup.setText(MapsActivity.picLocationAddress);
                tv_destination.setText(MapsActivity.picLocationAddress_destination);

                */
                RideRequestModel rideRequestModel = new RideRequestModel(MapsActivity.picLocationAddress, MapsActivity.picLocationAddress_destination, "pending", MapsActivity.USER_ID, false, false, false, false, finalTotal_fair, 0d, TRAVEL_DISTANCE);

                RIDE_REQUEST_MODEL=rideRequestModel;
                AMBULANCE_MODEL=data;



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