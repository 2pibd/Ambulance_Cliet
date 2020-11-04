package com.twopibd.dactarbari.ambulance.activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.adapter.SearchFromPlacesApiAdapter;
import com.twopibd.dactarbari.ambulance.api.getPlacesApiData;
import com.twopibd.dactarbari.ambulance.listeners.locatiionRetrivedListener;
import com.twopibd.dactarbari.ambulance.model.Prediction;
import com.twopibd.dactarbari.ambulance.model.SearchedPlaceModel;
import com.twopibd.dactarbari.ambulance.myfunctions.myfunctions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LiteHomeActivity extends GPSOpenActivity {
    BottomSheetBehavior bottomSheetBehavior;
    BottomSheetBehavior bottomSheetBehaviorpic_destination;
    @BindView(R.id.tv_currentLocation)
    TextView tv_currentLocation;
    @BindView(R.id.tv_userName)
    TextView tv_userName;
    @BindView(R.id.linearOwnLocation)
    LinearLayout linearOwnLocation;
    @BindView(R.id.ac_destination)
    EditText ac_destination;
    @BindView(R.id.home_profile_pic)
    CircleImageView home_profile_pic;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    public static String fromLocation;
    public static String toLocation;
    public static String placeID;
    String userID;

    FirebaseDatabase firebaseDatabase;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    public static Location CURRENT_LOCATION;
    List<Prediction> predictions = new ArrayList<>();

    SearchFromPlacesApiAdapter mAdapter;

    public static LatLng SELECTED_LOCATION;
    public static String SELECTED_LOCATION_ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lite_home);
        ButterKnife.bind(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getUid();
        firebaseDatabase.getReference("users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("photo").exists()) {
                        String user_img = dataSnapshot.child("photo").getValue().toString();
                        Glide.with(context).load(user_img).into(home_profile_pic);

                    }
                    if (dataSnapshot.child("name").exists()) {
                        tv_userName.setText(dataSnapshot.child("name").getValue().toString());
                    }
                } else {
                    tv_userName.setText("No username set yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        locatiionRetrivedListener.setMyLocationRetriver_(new locatiionRetrivedListener.myLocationRetriver() {
            @Override
            public Location onLocationretrived(Location location, String address, String cityName) {
                if (location != null) {
                    CURRENT_LOCATION = location;
                    tv_currentLocation.setText(address);
                    fromLocation = address;

                } else {
                    Toast.makeText(context, "Null location", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });

        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
        ConstraintLayout bottom_sheet_pic_destination = findViewById(R.id.bottom_sheet_pic_destination);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehaviorpic_destination = BottomSheetBehavior.from(bottom_sheet_pic_destination);

        bottomSheetBehaviorpic_destination.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        bottomSheetBehaviorpic_destination.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });
        linearOwnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CURRENT_LOCATION != null) {
                    SELECTED_LOCATION = new LatLng(CURRENT_LOCATION.getLatitude(), CURRENT_LOCATION.getLongitude());
                    SELECTED_LOCATION_ADDRESS = myfunctions.getLocationNameByLatLng(context, new LatLng(CURRENT_LOCATION.getLatitude(), CURRENT_LOCATION.getLongitude()));
                    bottomSheetBehavior.setPeekHeight(0);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehaviorpic_destination.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    Toast.makeText(context, "Please wait while retriving location", Toast.LENGTH_SHORT).show();
                }
                /*
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetBehaviorpic_destination.setState(BottomSheetBehavior.STATE_COLLAPSED);

                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehaviorpic_destination.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                 */
            }
        });


        initRecycler();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/autocomplete/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getPlacesApiData getPlacesApiData = retrofit.create(getPlacesApiData.class);
        String apiKey = getString(R.string.google_maps_key);

        ac_destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    String key = s.toString();
                    if (CURRENT_LOCATION != null) {

                        getPlacesApiData.getPlaces(key, "" + CURRENT_LOCATION.getLatitude() + "," + CURRENT_LOCATION.getLongitude(), apiKey).enqueue(new Callback<SearchedPlaceModel>() {
                            @Override
                            public void onResponse(Call<SearchedPlaceModel> call, Response<SearchedPlaceModel> response) {
                                Log.i("mkl", response.body().toString());
                                if (response != null && response.isSuccessful() && response.body() != null) {
                                    //  Toast.makeText(context, response.body().getPredictions().get(0).getDescription(), Toast.LENGTH_SHORT).show();
                                    predictions.clear();
                                    predictions.addAll(response.body().getPredictions());
                                    mAdapter.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(context, "null or unsuccessfull", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<SearchedPlaceModel> call, Throwable t) {

                            }
                        });
                    }

                } else {

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        checkUserStatus();


    }

    private void checkUserStatus() {

        firebaseDatabase.getReference("user_status").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&&dataSnapshot.child("isRidingNow").exists()&&dataSnapshot.child("isRidingNow").getValue().toString().equals("1")) {
                    startActivity(new Intent(context, RideOnActivity.class));
                   // String isRidingNow = dataSnapshot.child("isRidingNow").getValue().toString();
                   // String passengerID = dataSnapshot.child("driverID").getValue().toString();
                   // String rideID = dataSnapshot.child("rideID").getValue().toString();
                  //  Toast.makeText(context, "" + isRidingNow + "\n" + passengerID + "\n" + rideID, Toast.LENGTH_LONG).show();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecycler() {
        mAdapter = new SearchFromPlacesApiAdapter(context, predictions);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        // recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(mAdapter);
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // bottomSheetBehavior.setPeekHeight(0);
        if (bottomSheetBehaviorpic_destination.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setPeekHeight(320);
            bottomSheetBehaviorpic_destination.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetBehaviorpic_destination.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                super.onBackPressed();
            }
        }


    }

    public void OpenProfileActivity(View view) {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}

