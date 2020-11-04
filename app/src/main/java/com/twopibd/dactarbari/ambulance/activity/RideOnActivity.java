package com.twopibd.dactarbari.ambulance.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.data.sharedData;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RideOnActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Context context = this;
    @BindView(R.id.view_black_transparent)
    View view_black_transparent;
    @BindView(R.id.relativeDashbody)
    RelativeLayout relativeDashbody;
    @BindView(R.id.tv_fair)
    TextView tv_fair;
    @BindView(R.id.tv_pay)
    TextView tv_pay;
    @BindView(R.id.tv_total_fair)
    TextView tv_total_fair;
    @BindView(R.id.cardRideCancel)
    CardView cardRideCancel;
    @BindView(R.id.tv_status)
    TextView tv_status;
    boolean isDriverOccupied = false;
    String USER_ID ;
    @BindView(R.id.tv_ride_status)
    TextView tv_ride_status;
    @BindView(R.id.tv_from)
    TextView tv_from;
    @BindView(R.id.tv_to)
    TextView tv_to;
    @BindView(R.id.tv_ride_id)
    TextView tv_ride_id;
    @BindView(R.id.tv_driver_name)
    TextView tv_driver_name;
    @BindView(R.id.tv_ambulance_model)
    TextView tv_ambulance_model;
    @BindView(R.id.tv_driver_phone)
    TextView tv_driver_phone;
    @BindView(R.id.tv_currentLocation)
    TextView tv_currentLocation;
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.IMG_CAR)
    ImageView IMG_CAR;
    //  MapView rosterMapView;
    //  GoogleMap gMap;
    static boolean active = false;
    DriverLocationRetrivedListener locationRetrivedListener;
    Double distance = 0d;
    String driver, rideID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_on);
        ButterKnife.bind(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        tv_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();

                firebaseDatabase.getReference("user_status").child(USER_ID).child("data").child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // firebaseDatabase.goOffline();
                        startActivity(new Intent(context, MapsActivity.class));
                        finishAffinity();
                    }
                });

            }
        });
        USER_ID= FirebaseAuth.getInstance().getUid();
        firebaseDatabase.getReference("user_status").child(USER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("mkl_v", dataSnapshot.getValue().toString());


                Log.i("mkl", dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    //  Log.i("mkl_v", "VIEW =>  "+dataSnapshot.toString());
                    HashMap<String, Long> hashMapInt = (HashMap<String, Long>) dataSnapshot.getValue();
                    HashMap<String, String> hashMapString = (HashMap<String, String>) dataSnapshot.getValue();
                    Log.i("mkl_v", "VIEW =>  " + hashMapInt.get("isRidingNow"));
                    Log.i("mkl_v", "VIEW =>  " + hashMapString.get("rideID"));

                    Long isRiding = hashMapInt.get("isRidingNow");
                    rideID = hashMapString.get("rideID");
                    driver = hashMapString.get("driverID");

                    String ambulance = hashMapString.get("currentAmbulance");


                    if (isRiding == 0) {
                        //driver is not on ride
                        Log.i("mkl", "passenger was free");
                        isDriverOccupied = false;
                        onBackPressed();
                        Toast.makeText(context, "passenger was free", Toast.LENGTH_SHORT).show();


                    } else if (isRiding == 2) {
                        //in proccecing state
                        Toast.makeText(context, "in proccecing state", Toast.LENGTH_SHORT).show();

                    } else {
                        //driver is hired
                        Toast.makeText(context, "driver is hired", Toast.LENGTH_SHORT).show();
                        if (active) {
                            Toast.makeText(context, "Download diver addess", Toast.LENGTH_SHORT).show();

                            getDriverInfo(driver,ambulance);
                        }else {
                            Toast.makeText(context, "is not active", Toast.LENGTH_SHORT).show();

                        }


                        //   Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("rideID"));
                        //  Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("isRidingNow"));
                        tv_ride_id.setText(rideID);
                        cardRideCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).child("data").child("status").setValue("cancel");
                                firebaseDatabase.getReference("user_status").child(driver).child("data").child("isRidingNow").setValue(0);
                                firebaseDatabase.getReference("user_status").child(USER_ID).child("data").child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // firebaseDatabase.goOffline();
                                        onBackPressed();
                                        //  startActivity(new Intent(context, MapsActivity.class));
                                        //finishAffinity();
                                    }
                                });
                            }
                        });

                        playSound();
                        Log.i("mkl", "passenger is allready occupied");
                        isDriverOccupied = true;

                        Toast.makeText(context, "driver  id => " + driver, Toast.LENGTH_SHORT).show();
                        //  Log.i("mkl_driver", driver);
                        firebaseDatabase.getReference("ambulance_request").child(rideID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                Log.i("mkl_feb_16",driver);
                                Log.i("mkl_feb_16",rideID);
                                Log.i("mkl_feb_16",dataSnapshot2.toString());


                                Log.i("mkl_v1", dataSnapshot2.getValue().toString());
                                Log.i("mkl_v1", dataSnapshot2.toString());


                                //   HashMap<String ,HashMap<String,String>>hashMap= (HashMap<String, HashMap<String,String>>) dataSnapshot2.getValue();
                                HashMap<String, String> hashMapN = (HashMap<String, String>) dataSnapshot2.getValue();
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot2.getValue();
                                tv_ride_status.setText(hashMapN.get("status"));
                                tv_to.setText(hashMapN.get("to"));
                                tv_from.setText(hashMapN.get("from"));
                                tv_ride_status.setText(hashMapN.get("status"));

                                if (hashMapBoolean.get("hasDriverCanceled") == true) {
                                    tv_status.setText("Trip is Cancelled by User");

                                } else if (hashMapBoolean.get("hasRideCompleated")) {
                                    tv_status.setText("Trip is Compleated");

                                    // firebaseDatabase.goOffline();

                                    view_black_transparent.setVisibility(View.VISIBLE);
                                    tv_total_fair.setText("Total Fair : 450 TK");
                                    tv_total_fair.setVisibility(View.VISIBLE);
                                    tv_pay.setVisibility(View.VISIBLE);
                                    relativeDashbody.setVisibility(View.VISIBLE);

                                } else if (hashMapBoolean.get("hasTripStarted")) {
                                    tv_status.setText("Trip has started");
                                    //showtraveledDistance();
                                    AlternativeshowtraveledDistance();


                                } else if (hashMapBoolean.get("hasArrived")) {
                                    tv_status.setText("Ambulance has Arrived");

                                } else {
                                    tv_status.setText("Ambulance is on the way");
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                      /*
                        firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot2, @Nullable String s) {
                                Log.i("mkl_v1", dataSnapshot2.getValue().toString());
                                Log.i("mkl_v1", dataSnapshot2.toString());


                                //   HashMap<String ,HashMap<String,String>>hashMap= (HashMap<String, HashMap<String,String>>) dataSnapshot2.getValue();
                                HashMap<String, String> hashMapN = (HashMap<String, String>) dataSnapshot2.getValue();
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot2.getValue();
                                tv_ride_status.setText(hashMapN.get("status"));
                                tv_to.setText(hashMapN.get("to"));
                                tv_from.setText(hashMapN.get("from"));
                                tv_ride_status.setText(hashMapN.get("status"));

                                if (hashMapBoolean.get("hasCancelled") == true) {
                                    tv_status.setText("Trip is Cancelled");

                                } else if (hashMapBoolean.get("hasRideCompleated")) {
                                    tv_status.setText("Trip is Compleated");

                                    // firebaseDatabase.goOffline();

                                    view_black_transparent.setVisibility(View.VISIBLE);
                                    tv_total_fair.setText("Total Fair : 450 TK");
                                    tv_total_fair.setVisibility(View.VISIBLE);
                                    tv_pay.setVisibility(View.VISIBLE);
                                    relativeDashbody.setVisibility(View.VISIBLE);

                                } else if (hashMapBoolean.get("hasTripStarted")) {
                                    tv_status.setText("Trip is started");
                                    //showtraveledDistance();
                                    AlternativeshowtraveledDistance();


                                } else if (hashMapBoolean.get("hasArrived")) {
                                    tv_status.setText("Ambulance is Arrived");

                                } else {
                                    tv_status.setText("Ambulance is Comming");
                                }


                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                if (hashMapBoolean.get("hasCancelled") == true) {
                                    tv_status.setText("Trip is Cancelled");

                                } else if (hashMapBoolean.get("hasRideCompleated")) {
                                    tv_status.setText("Trip is Compleated");

                                    //  firebaseDatabase.goOffline();

                                    view_black_transparent.setVisibility(View.VISIBLE);
                                    tv_total_fair.setText("Total Fair : 450 TK");
                                    tv_total_fair.setVisibility(View.VISIBLE);
                                    tv_pay.setVisibility(View.VISIBLE);
                                    relativeDashbody.setVisibility(View.VISIBLE);

                                } else if (hashMapBoolean.get("hasTripStarted")) {
                                    tv_status.setText("Trip is started");

                                    // showtraveledDistance();
                                    AlternativeshowtraveledDistance();

                                } else if (hashMapBoolean.get("hasArrived")) {
                                    tv_status.setText("Ambulance is Arrived");

                                } else {
                                    tv_status.setText("Ambulance is Comming");
                                }

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                       */
                    }


                } else {
                    Log.i("mkl_v", "no  data");
                    onBackPressed();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // databaseReference = firebaseDatabase.getReference("ambulanceLatestLocations");

/*
        rosterMapView = (MapView) findViewById(R.id.roster_map_view);
        rosterMapView.onCreate(savedInstanceState);
        rosterMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {


                        setLocationRetrivedListener(new DriverLocationRetrivedListener() {
                            @Override
                            public void onLocationRetrived(LatLng latLng) {

                                // LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());

                                if (latLng != null) {
                                    googleMap.clear();
                                    CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(latLng, 17);

                                    googleMap.addMarker(new MarkerOptions().position(latLng)).setIcon(BitmapDescriptorFactory.defaultMarker());

                                    googleMap.animateCamera(location1);
                                }

                            }
                        });

                    }
                });

            }
        });

 */
/*

        firebaseDatabase.getReference("user_status").child(USER_ID).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i("mkl_v", dataSnapshot.toString());


                Log.i("mkl", dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    //  Log.i("mkl_v", "VIEW =>  "+dataSnapshot.toString());
                    HashMap<String, Long> hashMapInt = (HashMap<String, Long>) dataSnapshot.getValue();
                    HashMap<String, String> hashMapString = (HashMap<String, String>) dataSnapshot.getValue();
                    Log.i("mkl_v", "VIEW =>  " + hashMapInt.get("isRidingNow"));
                    Log.i("mkl_v", "VIEW =>  " + hashMapString.get("rideID"));

                    Long isRiding = hashMapInt.get("isRidingNow");
                    rideID = hashMapString.get("rideID");
                    driver = hashMapString.get("driverID");
                    //String driver = hashMapString.get("driverID");


                    if (isRiding == 0) {
                        //driver is not on ride
                        Log.i("mkl", "passenger was free");
                        isDriverOccupied = false;
                        onBackPressed();


                    } else if (isRiding == 2) {
                        //in proccecing state

                    } else {
                        //driver is hired
                        if (active) {
                            getDriverInfo(driver);
                        }


                        //   Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("rideID"));
                        //  Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("isRidingNow"));
                        tv_ride_id.setText(rideID);
                        cardRideCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).child("data").child("status").setValue("cancel");
                                firebaseDatabase.getReference("user_status").child(driver).child("data").child("isRidingNow").setValue(0);
                                firebaseDatabase.getReference("user_status").child(USER_ID).child("data").child("isRidingNow").setValue(0).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // firebaseDatabase.goOffline();
                                        onBackPressed();
                                        //  startActivity(new Intent(context, MapsActivity.class));
                                        //finishAffinity();
                                    }
                                });
                            }
                        });

                        playSound();
                        Log.i("mkl", "passenger is allready occupied");
                        isDriverOccupied = true;

                        Toast.makeText(context, "driver  id => " + driver, Toast.LENGTH_SHORT).show();
                        //  Log.i("mkl_driver", driver);

                        firebaseDatabase.getReference("ambulance_request").child(driver).child(rideID).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot2, @Nullable String s) {
                                Log.i("mkl_v1", dataSnapshot2.getValue().toString());
                                Log.i("mkl_v1", dataSnapshot2.toString());


                                //   HashMap<String ,HashMap<String,String>>hashMap= (HashMap<String, HashMap<String,String>>) dataSnapshot2.getValue();
                                HashMap<String, String> hashMapN = (HashMap<String, String>) dataSnapshot2.getValue();
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot2.getValue();
                                tv_ride_status.setText(hashMapN.get("status"));
                                tv_to.setText(hashMapN.get("to"));
                                tv_from.setText(hashMapN.get("from"));
                                tv_ride_status.setText(hashMapN.get("status"));

                                if (hashMapBoolean.get("hasCancelled") == true) {
                                    tv_status.setText("Trip is Cancelled");

                                } else if (hashMapBoolean.get("hasRideCompleated")) {
                                    tv_status.setText("Trip is Compleated");

                                    // firebaseDatabase.goOffline();

                                    view_black_transparent.setVisibility(View.VISIBLE);
                                    tv_total_fair.setText("Total Fair : 450 TK");
                                    tv_total_fair.setVisibility(View.VISIBLE);
                                    tv_pay.setVisibility(View.VISIBLE);
                                    relativeDashbody.setVisibility(View.VISIBLE);

                                } else if (hashMapBoolean.get("hasTripStarted")) {
                                    tv_status.setText("Trip is started");
                                    //showtraveledDistance();
                                    AlternativeshowtraveledDistance();


                                } else if (hashMapBoolean.get("hasArrived")) {
                                    tv_status.setText("Ambulance is Arrived");

                                } else {
                                    tv_status.setText("Ambulance is Comming");
                                }


                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                HashMap<String, Boolean> hashMapBoolean = (HashMap<String, Boolean>) dataSnapshot.getValue();
                                if (hashMapBoolean.get("hasCancelled") == true) {
                                    tv_status.setText("Trip is Cancelled");

                                } else if (hashMapBoolean.get("hasRideCompleated")) {
                                    tv_status.setText("Trip is Compleated");

                                    //  firebaseDatabase.goOffline();

                                    view_black_transparent.setVisibility(View.VISIBLE);
                                    tv_total_fair.setText("Total Fair : 450 TK");
                                    tv_total_fair.setVisibility(View.VISIBLE);
                                    tv_pay.setVisibility(View.VISIBLE);
                                    relativeDashbody.setVisibility(View.VISIBLE);

                                } else if (hashMapBoolean.get("hasTripStarted")) {
                                    tv_status.setText("Trip is started");

                                    // showtraveledDistance();
                                    AlternativeshowtraveledDistance();

                                } else if (hashMapBoolean.get("hasArrived")) {
                                    tv_status.setText("Ambulance is Arrived");

                                } else {
                                    tv_status.setText("Ambulance is Comming");
                                }

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }


                } else {
                    Log.i("mkl_v", "no  data");
                    onBackPressed();

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                HashMap<String, Long> hashMapInt = (HashMap<String, Long>) dataSnapshot.getValue();

                Log.i("mkl00", dataSnapshot.getValue().toString());

                Long isRiding = hashMapInt.get("isRidingNow");
                if (isRiding == 0) {
                    //driver is now free
                    Log.i("mkl", "passenger is now free");

                    isDriverOccupied = false;
                    //    onBackPressed();

                    Toast.makeText(context, "Ride is finished Now", Toast.LENGTH_SHORT).show();
                    // firebaseDatabase.goOffline();
                    //  startActivity(new Intent(context, MapsActivity.class));
                    // finish();


                } else {
                    //driver is hired now
                    Log.i("mkl", "passenger is now occupied");
                    isDriverOccupied = true;

                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



 */
    }

    private void showtraveledDistance() {
        firebaseDatabase.getReference("ride_history_driver").child(driver).child(rideID).child("trace").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //  distance += Double.valueOf(dataSnapshot.getValue().toString());
                HashMap<String, Double> hashMap = (HashMap<String, Double>) dataSnapshot.getValue();
                Double d = hashMap.get("traveled");
                distance += d;

                DecimalFormat df = new DecimalFormat("#.##");
                distance = Double.valueOf(df.format(distance));
                tv_distance.setText("" + distance);
                Log.i("ddd", "Travelded => " + d);
                Toast.makeText(RideOnActivity.this, "ok", Toast.LENGTH_SHORT).show();
                firebaseDatabase.getReference("ride_history_driver").child(USER_ID).child(rideID).child("distance_covered").setValue(distance);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AlternativeshowtraveledDistance() {
        Log.i("mkl_al", "alternet started");
        firebaseDatabase.getReference("ride_history_driver").child(driver).child(rideID).child("distance_covered").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("mkl_al", dataSnapshot.toString());
                tv_distance.setText(dataSnapshot.getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        firebaseDatabase.getReference("ride_history_driver").child(driver).child(rideID).child("ride_cost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("mkl_al", dataSnapshot.toString());
                if (dataSnapshot.exists()) {
                    tv_fair.setText(dataSnapshot.getValue().toString());
                } else {
                    tv_fair.setText("Node not found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {

    }

    public interface DriverLocationRetrivedListener {
        void onLocationRetrived(LatLng latLng);
    }

    public void setLocationRetrivedListener(DriverLocationRetrivedListener l) {
        this.locationRetrivedListener = l;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // rosterMapView.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = true;
    }

    private void getDriverInfo(String driver, String ambulance) {
        firebaseDatabase.getReference("ambulanceLatestLocations").child(ambulance).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  Log.i("mkl12",dataSnapshot.getValue().toString());
                HashMap<String, String> hashMap = (HashMap<String, String>) dataSnapshot.getValue();
                HashMap<String, Double> hashDouble = (HashMap<String, Double>) dataSnapshot.getValue();

               // Log.i("mkl12", "Driver Name =>" + hashMap.get("car_driver_name"));
             //   Log.i("mkl12", "type  =>" + hashMap.get("type"));
              //  Log.i("mkl12", "car_image  =>" + hashMap.get("car_image"));
             //   Log.i("img_load",hashMap.get("photo"));
                try {
                    if (active) {
                        Glide.with(context).load(hashMap.get("photo")).into(IMG_CAR);
                    }
                  //  Glide.with(context).load("https://www.hindustantimes.com/rf/image_size_960x540/HT/p2/2017/10/21/Pictures/_279411b8-b658-11e7-8276-b04a35b0fb2c.jpg").into(IMG_CAR);
                } catch (Exception e) {
                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("img_load",e.getLocalizedMessage());

                }


                tv_ambulance_model.setText(hashMap.get("modelName"));
               // tv_driver_name.setText(hashMap.get("car_driver_name"));
                //tv_driver_phone.setText(hashMap.get("car_mobile"));

               // tv_ride_status.setText(hashMap.get("status"));


                String address = sharedData.getLocationName(context, new LatLng(hashDouble.get("lat"), hashDouble.get("lng")));

                tv_currentLocation.setText(address);

                if (locationRetrivedListener != null) {

                    locationRetrivedListener.onLocationRetrived(new LatLng(hashDouble.get("car_current_lat"), hashDouble.get("car_current_lng")));
                }


//                CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(new LatLng(hashDouble.get("car_current_lat"), hashDouble.get("car_current_lng")), 17);
/*
                if (gMap != null) {
                    // gMap.clear();

                    //  gMap.addMarker(new MarkerOptions().position(new LatLng(hashDouble.get("car_current_lat"), hashDouble.get("car_current_lng"))).icon(BitmapDescriptorFactory.defaultMarker()));

                    //  gMap.animateCamera(location1);
                    Toast.makeText(context, "Location downloaded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "gmap was null", Toast.LENGTH_SHORT).show();
                }

 */
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void playSound() {
        MediaPlayer mMediaPlayer = new MediaPlayer();
        Uri mediaPath = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.eventually);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), mediaPath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void back(View view) {
    }
}
