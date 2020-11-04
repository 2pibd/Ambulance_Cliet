package com.twopibd.dactarbari.ambulance.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.adapter.AmbulanceTypeAdapter;
import com.twopibd.dactarbari.ambulance.listeners.locatiionRetrivedListener;
import com.twopibd.dactarbari.ambulance.model.AmbulanceCostType;
import com.twopibd.dactarbari.ambulance.model.AmbulanceTypeCostFB;
import com.twopibd.dactarbari.ambulance.myfunctions.commonLIsteners;
import com.twopibd.dactarbari.ambulance.myfunctions.myfunctions;
import com.twopibd.dactarbari.ambulance.utils.MyProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.twopibd.dactarbari.ambulance.activity.LiteHomeActivity.SELECTED_LOCATION;
import static com.twopibd.dactarbari.ambulance.activity.LiteHomeActivity.SELECTED_LOCATION_ADDRESS;
import static com.twopibd.dactarbari.ambulance.myfunctions.myfunctions.getDirectionAndRoute;
import static com.twopibd.dactarbari.ambulance.myfunctions.myfunctions.getDirectionAndRouteByLatLng;

public class SelectVehicleActivity extends AppCompatActivity {
    @BindView(R.id.cardCancelAmbulance)
    CardView cardCancelAmbulance;
    @BindView(R.id.cardCallAmbulance)
    CardView cardCallAmbulance;
    @BindView(R.id.tv_cost_confirmed)
    TextView tv_cost_confirmed;
    @BindView(R.id.relative_requestSheet)
    RelativeLayout relative_requestSheet;
    @BindView(R.id.relativeCancelSheet)
    RelativeLayout relativeCancelSheet;
    @BindView(R.id.tv_count)
    TextView tv_count;
    @BindView(R.id.tv_cost)
    TextView tv_cost;
    @BindView(R.id.tv_type)
    TextView tv_type;
    @BindView(R.id.tv_to)
    TextView tv_to;
    @BindView(R.id.tv_from)
    TextView tv_from;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<AmbulanceCostType> ambulanceTypeCostFBS = new ArrayList<>();
    AmbulanceTypeAdapter mAdapter;
    public static int DISTANCE_METER = 00;
    public static int TIME_SECONDs = 0;
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetBehavior bottomSheetBehaviorCancel;
    boolean isFirstBottomSheetOpened = false;
    Context context = this;
    List<AmbulanceModelDistance> ambulanceModelDistanceList = new ArrayList<>();
    int count = 0;
    int compleatedCount = 0;
    boolean shouldBackPressedCancel = false;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_vehicle);
        ButterKnife.bind(this);
        userID = FirebaseAuth.getInstance().getUid();

        showProgressBar();
        tv_from.setText(LiteHomeActivity.fromLocation);

        tv_to.setText(LiteHomeActivity.toLocation);
        initRecycler();
        if (SELECTED_LOCATION != null) {
            getDirectionAndRoute(getBaseContext(), SELECTED_LOCATION, LiteHomeActivity.placeID, new commonLIsteners.RouteDistanceRetrivedListener() {
                @Override
                public void onRetrived(int distance, int time) {
                    DISTANCE_METER = distance;
                    TIME_SECONDs = time;
                    //  Toast.makeText(SelectVehicleActivity.this, "" + distance + "\n" + time, Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(SelectVehicleActivity.this, "" + distance , Toast.LENGTH_SHORT).show();
                    prepareRatesList();
                }
            });
        } else {
            Toast.makeText(this, "Location error", Toast.LENGTH_SHORT).show();
        }


        //commonLIsteners.

//first sheet
        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        // update toggle button text
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // update collapsed button text
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        relative_requestSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//second sheet
        ConstraintLayout bottomSheetLayoutCancel = findViewById(R.id.bottom_sheet_cancel);
        bottomSheetBehaviorCancel = BottomSheetBehavior.from(bottomSheetLayoutCancel);
        bottomSheetBehaviorCancel.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        // update toggle button text
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        // update collapsed button text
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        bottomSheetBehaviorCancel.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        bottomSheetBehaviorCancel.setState(BottomSheetBehavior.STATE_HIDDEN);
        relativeCancelSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void prepareRatesList() {
        ambulanceTypeCostFBS.clear();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReferenceFromUrl("https://dactarbari-ambulance.firebaseio.com/ambulance_fee").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.i("mkl__", child.getValue().toString());
                    AmbulanceTypeCostFB ambulanceTypeCostFB = child.getValue(AmbulanceTypeCostFB.class);

                    HashMap<String, Long> hashMapLong = (HashMap<String, Long>) child.getValue();
                    ambulanceTypeCostFB.setPerKm(hashMapLong.get("per_km"));
                    ambulanceTypeCostFBS.add(new AmbulanceCostType(child.getKey(), ambulanceTypeCostFB));
                    Gson gson = new Gson();
                    Log.i("mkl__", gson.toJson(ambulanceTypeCostFB));
                }
                mAdapter.notifyDataSetChanged();
                hideProgressBar();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showProgressBar() {
        MyProgressBar.with(this);
    }

    private void hideProgressBar() {
        MyProgressBar.dismiss();
    }

    public class AmbulanceModelDistance {
        int distance;
        String ambulanceID, DriverID;
        int time;


        public AmbulanceModelDistance(int distance, String ambulanceID, String driverID, int time) {
            this.distance = distance;
            this.ambulanceID = ambulanceID;
            DriverID = driverID;
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public String getAmbulanceID() {
            return ambulanceID;
        }

        public void setAmbulanceID(String ambulanceID) {
            this.ambulanceID = ambulanceID;
        }

        public String getDriverID() {
            return DriverID;
        }

        public void setDriverID(String driverID) {
            DriverID = driverID;
        }
    }

    private void initRecycler() {
        mAdapter = new AmbulanceTypeAdapter(getBaseContext(), ambulanceTypeCostFBS);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext(), RecyclerView.VERTICAL, false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview.setAdapter(mAdapter);

        mAdapter.setAmbulanceTypeSelectedListener(new AmbulanceTypeAdapter.AmbulanceTypeSelectedListener() {
            @Override
            public void onClicked(AmbulanceCostType data, String costPrice) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                isFirstBottomSheetOpened = true;
                tv_type.setText(data.getName());
                tv_cost.setText(costPrice);
                tv_cost_confirmed.setText(costPrice);

                cardCallAmbulance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ambulanceModelDistanceList.clear();
                        //  firebaseDatabase.getReference("ambulanceLatestLocations").
                        LatLng targetLatlong = new LatLng(LiteHomeActivity.CURRENT_LOCATION.getLatitude(), LiteHomeActivity.CURRENT_LOCATION.getLongitude());
                        String currentCityName = myfunctions.getCityName(context, targetLatlong);
                        Log.i("mkl_feb",currentCityName);


                        if (currentCityName != null && currentCityName.length() > 0) {
                            //.orderByChild("city").equalTo(currentCityName)
                            firebaseDatabase.getReference("ambulanceLatestLocations").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //dataSnapshot.exists()
                                    if (dataSnapshot.exists()) {

                                       // getDirectionAndRouteByLatLng(getBaseContext(), SELECTED_LOCATION, new LatLng(lat, lng), new commonLIsteners.RouteDistanceRetrivedListener())

                                        Log.i("mkl_16_feb","here");
                                        count = (int) dataSnapshot.getChildrenCount();
                                        compleatedCount = 0;
                                        Log.i("mkl_16_feb",dataSnapshot.getValue().toString());
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Log.i("mkl__", child.getValue().toString());
                                            Log.i("mkl_16_feb","here 2");
                                            Long status = (Long) child.child("isOnService").getValue();
                                            if (status!=null&&status == 0) {
                                                Log.i("mkl_16_feb","here 3");
                                                Log.i("mkl_query", child.getKey());
                                                Double lat = (Double) child.child("lat").getValue();
                                                Double lng = (Double) child.child("lng").getValue();
                                                String ambulance_id = child.child("ambulanceID").getValue().toString();
                                                String owner_id = child.child("owner_id").getValue().toString();
                                                //  SELECTED_LOCATION


                                                getDirectionAndRouteByLatLng(getBaseContext(), SELECTED_LOCATION, new LatLng(lat, lng), new commonLIsteners.RouteDistanceRetrivedListener() {
                                                    @Override
                                                    public void onRetrived(int distance, int time) {
                                                        //Toast.makeText(context, "Im hit, please find me", Toast.LENGTH_SHORT).show();
                                                        compleatedCount++;
                                                        Log.i("mkl_16_feb","here 4");
                                                        //  DISTANCE_METER = distance;
                                                        //  TIME_SECONDs = time;
                                                        //  Toast.makeText(SelectVehicleActivity.this, "" + distance + "\n" + time, Toast.LENGTH_SHORT).show();

                                                        AmbulanceModelDistance ambulanceModelDistance = new AmbulanceModelDistance(distance, ambulance_id, owner_id, time);
                                                        ambulanceModelDistanceList.add(ambulanceModelDistance);

                                                        Log.i("mkl_16_feb", ambulanceModelDistance.getAmbulanceID() + " ambulance id is " + ambulanceModelDistance.getDistance() + " meter away" + ambulanceModelDistance.getTime() + " second away");
                                                        //Log.i("mkl_16_feb","here");

                                                        if (true | compleatedCount == (count)) {
                                                            tv_count.setText("" + ambulanceModelDistanceList.size());
                                                            Toast.makeText(context,  ""+compleatedCount, Toast.LENGTH_SHORT).show();

                                                            bottomSheetBehaviorCancel.setState(BottomSheetBehavior.STATE_EXPANDED);
                                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                                            hideProgressBar();
                                                            // sort
                                                            AmbulanceModelDistance temp;
                                                            for (int i = 1; i < ambulanceModelDistanceList.size(); i++) {
                                                                for (int j = i; j > 0; j--) {
                                                                    if (ambulanceModelDistanceList.get(j).getDistance() < ambulanceModelDistanceList.get(j - 1).getDistance()) {
                                                                        temp = ambulanceModelDistanceList.get(j);
                                                                        ambulanceModelDistanceList.set(j, ambulanceModelDistanceList.get(j - 1));
                                                                        ambulanceModelDistanceList.set((j - 1), temp);
                                                                    }
                                                                }
                                                            }
                                                            // send request automatically from the list ambulanceModelDistanceList
                                                            Log.i("mkl_16_feb","here 5");
                                                            int nowTryingAmbulanceIndex = 0;
                                                            FirebaseDatabase fdb = FirebaseDatabase.getInstance();
                                                            String key = fdb.getReference("ambulance_request").push().getKey();
                                                            fdb.getReference("ambulance_request").child(key).child("ambulance_id").setValue(ambulanceModelDistanceList.get(nowTryingAmbulanceIndex).getAmbulanceID());
                                                            fdb.getReference("ambulance_request").child(key).child("driver_id").setValue(ambulanceModelDistanceList.get(nowTryingAmbulanceIndex).getDriverID());
                                                            fdb.getReference("ambulance_request").child(key).child("hasUserCanceled").setValue(false);
                                                            fdb.getReference("ambulance_request").child(key).child("hasDriverCanceled").setValue(false);
                                                            fdb.getReference("ambulance_request").child(key).child("hasTripStarted").setValue(false);
                                                            fdb.getReference("ambulance_request").child(key).child("hasArrived").setValue(false);
                                                            fdb.getReference("ambulance_request").child(key).child("hasRideCompleated").setValue(false);
                                                            fdb.getReference("ambulance_request").child(key).child("from").setValue(SELECTED_LOCATION_ADDRESS);
                                                            fdb.getReference("ambulance_request").child(key).child("to").setValue(LiteHomeActivity.toLocation);
                                                            fdb.getReference("ambulance_request").child(key).child("distance").setValue(DISTANCE_METER);

                                                            //user status update

                                                            String driverID = ambulanceModelDistanceList.get(nowTryingAmbulanceIndex).getDriverID();

                                                            //passenger
                                                            fdb.getReference("user_status").child(userID).child("isRidingNow").setValue(2);
                                                            fdb.getReference("user_status").child(userID).child("driverID").setValue(driverID);
                                                            fdb.getReference("user_status").child(userID).child("rideID").setValue(key);

                                                            //driver
                                                            fdb.getReference("user_status").child(driverID).child("isRidingNow").setValue(2);
                                                            fdb.getReference("user_status").child(driverID).child("passengerID").setValue(userID);
                                                            fdb.getReference("user_status").child(driverID).child("rideID").setValue(key);

                                                            fdb.getReference("ambulance_request").child(key).child("est_cost").setValue(costPrice).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    shouldBackPressedCancel = true;
                                                                    //cancel request
                                                                    cardCancelAmbulance.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            //set both driver and passenger free
                                                                            fdb.getReference("user_status").child(driverID).child("isRidingNow").setValue(0);
                                                                            fdb.getReference("user_status").child(userID).child("isRidingNow").setValue(0);

                                                                            fdb.getReference("ambulance_request").child(key).child("hasUserCanceled").setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    startActivity(new Intent(context, LiteHomeActivity.class));
                                                                                    finishAffinity();

                                                                                }
                                                                            });

                                                                        }
                                                                    });
                                                                }
                                                            });


                                                        }
                                                    }
                                                });
                                            } else {
                                                compleatedCount++;
                                            }


                                        }
                                        //show new bottom sheet


                                    } else {
                                        Log.i("mkl_16_feb","Sorry,No Ambulance available in your location");
                                        Toast.makeText(context, "Sorry,No Ambulance available in your location" + "\n" + myfunctions.getCityName(context, SELECTED_LOCATION), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            showProgressBar();
                            Log.i("mkl_query", "Button clicked");
                        } else {
                            Toast.makeText(context, "No current Location", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (shouldBackPressedCancel == false) {
            if (bottomSheetBehaviorCancel.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehaviorCancel.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    isFirstBottomSheetOpened = false;
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            Toast.makeText(context, "Cancel your request first", Toast.LENGTH_SHORT).show();
        }
    }
}
