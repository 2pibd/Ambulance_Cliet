package com.twopibd.dactarbari.ambulance.adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.activity.SelectVehicleActivity;
import com.twopibd.dactarbari.ambulance.model.AmbulanceCostType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AmbulanceTypeAdapter extends RecyclerView.Adapter<AmbulanceTypeAdapter.MyViewHolder> {
    private Context context;
    private List<AmbulanceCostType> listFiltered = new ArrayList<>();

    public interface AmbulanceTypeSelectedListener {
        void onClicked(AmbulanceCostType i, String s);
    }

    AmbulanceTypeSelectedListener ambulanceTypeSelectedListener;

    public void setAmbulanceTypeSelectedListener(AmbulanceTypeSelectedListener listener) {
        this.ambulanceTypeSelectedListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_type, tv_ex, tv_model, tv_driver, tv_cost;
        ImageView img;
        View divider;


        public MyViewHolder(View view) {
            super(view);
            //  tv_name = view.findViewById(R.id.tv_name);
            img = view.findViewById(R.id.img);
            tv_type = view.findViewById(R.id.tv_type);
            tv_driver = view.findViewById(R.id.tv_driver);
            tv_model = view.findViewById(R.id.tv_model);
            tv_cost = view.findViewById(R.id.tv_cost);


        }
    }


    public AmbulanceTypeAdapter(Context context, List<AmbulanceCostType> list_) {
        this.context = context;
        this.listFiltered = list_;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ambulance_type_item, parent, false);

        return new MyViewHolder(itemView);
    }

    //https://gl-images.condecdn.net/image/lN39xbMKeop/crop/405/f/Gal-Gadot-1.jpg
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        String user_id = FirebaseAuth.getInstance().getUid();
        final AmbulanceCostType data = listFiltered.get(position);
        holder.tv_type.setText(data.getName());
        Float fair = (float) (SelectVehicleActivity.DISTANCE_METER / 1000) + data.getAmbulanceTypeCostFB().getBasefee();
        fair = (float) data.getAmbulanceTypeCostFB().getBasefee()+
                ((float) ((data.getAmbulanceTypeCostFB().getPerKm())*SelectVehicleActivity.DISTANCE_METER / 1000));

        Log.i("mkl_value_test",""+data.getAmbulanceTypeCostFB().getPerKm());
       // fair = (float) data.getAmbulanceTypeCostFB().getBasefee();
        holder.tv_cost.setText("" + fair + " BDT");
       // holder.tv_cost.setText("" + SelectVehicleActivity.DISTANCE_METER  + " KM*");

        Float finalFair = fair;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ambulanceTypeSelectedListener.onClicked(data, "" + finalFair);
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