package com.twopibd.dactarbari.ambulance.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.model.RideRequestModel;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.twopibd.dactarbari.ambulance.data.sharedData.AMBULANCE_MODEL;
import static com.twopibd.dactarbari.ambulance.data.sharedData.RIDE_REQUEST_MODEL;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_DISTANCE;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_TIME_APPROX;

public class AmbulanceOrderActivity extends AppCompatActivity {
    @BindView(R.id.img_ambulance)
    ImageView img_ambulance;
    @BindView(R.id.tv_from)
    TextView tv_from;
    @BindView(R.id.tv_sub_total)
    TextView tv_sub_total;
    @BindView(R.id.tv_to)
    TextView tv_to;
    @BindView(R.id.tv_km)
    TextView tv_km;
    @BindView(R.id.tv_base)
    TextView tv_base;
    @BindView(R.id.tv_f_p_km)
    TextView tv_f_p_km;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_total_fair)
    TextView tv_total_fair;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_order);
        ButterKnife.bind(this);


        //    RIDE_REQUEST_MODEL=rideRequestModel;
        //AMBULANCE_MODEL
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("ambulance_request");
        String key=firebaseDatabase.getReference("ambulance_request").push().getKey();

        String APPROX_TIME = "";
        int SECONDS = TRAVEL_TIME_APPROX;
        int MINUTES = (SECONDS / 60) + 1;
        int HOUR = 0;
        APPROX_TIME = MINUTES + " Minutes";
        if (MINUTES > 60) {
            HOUR = (MINUTES / 60) + 1;
            APPROX_TIME = "" + HOUR + " Hour " + MINUTES + " Minutes";
        }

        tv_to.setText(RIDE_REQUEST_MODEL.getTo());
        tv_time.setText(APPROX_TIME);
        tv_from.setText(RIDE_REQUEST_MODEL.getFrom());
        tv_f_p_km.setText(AMBULANCE_MODEL.getCarFairPerKm() + " TK");
        tv_base.setText(AMBULANCE_MODEL.getCarBaseCharge() + " TK");
        Double d = Double.valueOf(RIDE_REQUEST_MODEL.getDistance());
        d = d / 1000;
        tv_km.setText(d + " KM");

        tv_total_fair.setText(AMBULANCE_MODEL.getCarBaseCharge() + " TK + (" + AMBULANCE_MODEL.getCarFairPerKm() + "  X  " + d + " ) TK");
        tv_sub_total.setText(AMBULANCE_MODEL.getCarBaseCharge() + (AMBULANCE_MODEL.getCarFairPerKm() * d) + " TK");

        double finalTotal_fair= AMBULANCE_MODEL.getCarBaseCharge() + (AMBULANCE_MODEL.getCarFairPerKm() * d);

        Glide.with(context).load(AMBULANCE_MODEL.getCarImage()).into(img_ambulance);
        RideRequestModel rideRequestModel = new RideRequestModel(MapsActivity.picLocationAddress, MapsActivity.picLocationAddress_destination, "pending", MapsActivity.USER_ID, false, false, false, false, (float)finalTotal_fair, 0d, TRAVEL_DISTANCE);


        // databaseReference.child(data.getUserID()).child(key).child("from").setValue(MapsActivity.picLocationAddress);
        // databaseReference.child(data.getUserID()).child(key).child("to").setValue(MapsActivity.picLocationAddress_destination);

        //passengers
        firebaseDatabase.getReference("user_status").child(MapsActivity.USER_ID).child("data").child("isRidingNow").setValue(2);
        firebaseDatabase.getReference("user_status").child(MapsActivity.USER_ID).child("data").child("driverID").setValue(AMBULANCE_MODEL.getUserID());

        //drivers
        firebaseDatabase.getReference("user_status").child(AMBULANCE_MODEL.getUserID()).child("data").child("isRidingNow").setValue(2);
        firebaseDatabase.getReference("user_status").child(AMBULANCE_MODEL.getUserID()).child("data").child("passengerID").setValue(MapsActivity.USER_ID);
        firebaseDatabase.getReference("user_status").child(MapsActivity.USER_ID).child("data").child("rideID").setValue(key);
        firebaseDatabase.getReference("user_status").child(AMBULANCE_MODEL.getUserID()).child("data").child("rideID").setValue(key);

        databaseReference.child(AMBULANCE_MODEL.getUserID()).child(AMBULANCE_MODEL.getUserID()).child("data").setValue(rideRequestModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Submit done", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, MapsActivity.class));
                ((Activity) context).finishAffinity();


            }
        });


    }
}
