package com.twopibd.dactarbari.ambulance.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Property;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.adapter.AutoCompleteAdapter;
import com.twopibd.dactarbari.ambulance.api.getPolyline;
import com.twopibd.dactarbari.ambulance.fragments.AmbulanceACFragment;
import com.twopibd.dactarbari.ambulance.fragments.AmbulanceFREEZERFragment;
import com.twopibd.dactarbari.ambulance.fragments.AmbulanceICUFragment;
import com.twopibd.dactarbari.ambulance.fragments.AmbulanceNonACFragment;
import com.twopibd.dactarbari.ambulance.fragments.CarsFragments;
import com.twopibd.dactarbari.ambulance.listeners.locatiionRetrivedListener;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.model.Result;
import com.twopibd.dactarbari.ambulance.model.Route;
import com.twopibd.dactarbari.ambulance.model.TripHistoryModel;
import com.twopibd.dactarbari.ambulance.model.events.BeginJourneyEvent;
import com.twopibd.dactarbari.ambulance.model.events.EndJourneyEvent;
import com.twopibd.dactarbari.ambulance.myfunctions.myfunctions;
import com.twopibd.dactarbari.ambulance.utils.CartesianCoordinates;
import com.twopibd.dactarbari.ambulance.utils.JourneyEventBus;
import com.twopibd.dactarbari.ambulance.utils.LatLngInterpolator;
import com.twopibd.dactarbari.ambulance.utils.MarkerAnimation_;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.maps.model.JointType.ROUND;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_DISTANCE;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_TIME_APPROX;
import static com.twopibd.dactarbari.ambulance.myfunctions.myfunctions.initMapShow;

public class MapsActivity extends GPSOpenActivity {
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyLine;
    private GoogleMap mMap;
    Context context = this;
    AutoCompleteTextView autoCompleteTextView, ac_destination;
    //+
    Context mContext;
    GoogleApiClient mGoogleApiClient;
    private List<LatLng> listLatLng = new ArrayList<>();
    private static final String TAG = MapsActivity.class.getSimpleName();
    private SupportMapFragment mapFragment;
    private List<LatLng> polyLineList;
    private Marker marker;
    private float v;
    private double lat, lng;
    private Handler handler;
    private LatLng startPosition, endPosition;
    private int index, next;
    private LatLng sydney;
    private Button button;
    private EditText destinationEditText;
    private String destination;
    private LinearLayout linearLayout;

    private Disposable disposable;
    private FloatingActionButton mDriverModeOpenFB;
    private Polyline blackPolyLine;
    LinearLayout mParent;
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    EditText mSearchEdittext;
    ImageView mClear;
    //-
    private static final int AUTO_COMP_REQ_CODE = 2;
    AutoCompleteAdapter adapter_destination;
    AutoCompleteAdapter adapter;
    PlacesClient placesClient;
    public static String picLocationAddress;
    LatLng PicLocation;
    public static String picLocationAddress_destination;
    public static LatLng PicLocation_destination;
    Route normalOverlayPolyline;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<AmbulanceModel> ambulanceModels = new ArrayList<>();
    public static List<AmbulanceModel> ambulanceAC = new ArrayList<>();
    public static List<AmbulanceModel> ambulanceNON_AC = new ArrayList<>();
    public static List<AmbulanceModel> ambulanceICU = new ArrayList<>();
    public static List<AmbulanceModel> ambulanceFREEZER = new ArrayList<>();
    public static String USER_ID = "mukul_8";
    List<Marker> allAmbulanceMarkers = new ArrayList<>();

    HashMap<String, List<Marker>> markerHashMap = new HashMap<>();

    boolean isMarkerRotating = false;
    private float start_rotation;
    LinearLayout bottom;
    boolean isDriverOccupied = false;

    //bottom nav
    @BindView(R.id.tv_distance)
    TextView tv_distance;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.tv_estimated_fair)
    TextView tv_estimated_fair;
    @BindView(R.id.cardPendingCallCancel)
    CardView cardPendingCallCancel;
    @BindView(R.id.linerPendingRequest)
    LinearLayout linerPendingRequest;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.ed_desti_clear)
    ImageView ed_desti_clear;
    @BindView(R.id.relativeDashbody)
    RelativeLayout relativeDashbody;
    public static final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Linear();
    boolean isBottomSheetOpen = false;
    public static final TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            return latLngInterpolator.interpolate(fraction, startValue, endValue);
        }
    };
    public static final Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");

    public static final long DURATION = 5000;

    boolean isPathShowing = false;
    boolean isRideActivityOpenTriggered = false;

    ArgbEvaluator argbEvaluator;
    int spacialCounter = 0;

    @BindView(R.id.tv_destination)
    TextView tv_destination;
    @BindView(R.id.tv_picup)
    TextView picup;
    String USER_STATUS = "user_status";
    String DATA = "data";
    String AMBULANCE_REQUEST = "ambulance_request";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        autoCompleteTextView = findViewById(R.id.search_place);
        bottom = findViewById(R.id.bottom);
        hideBottomNavigationViewFull(bottom);

        locatiionRetrivedListener.setMyLocationRetriver_(new locatiionRetrivedListener.myLocationRetriver() {
            @Override
            public Location onLocationretrived(Location location, String address, String cityName) {
                if (location != null) {
                    picLocationAddress = address;
                    autoCompleteTextView.setText(picLocationAddress);
                    autoCompleteTextView.clearFocus();
                    ac_destination.requestFocus();
                    PicLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                    CameraUpdate location1 = CameraUpdateFactory.newLatLngZoom(sydney, 17);
                    if (mMap == null) {

                        init_map();
                    } else {
                        if (isBottomSheetOpen == false) {
                            mMap.animateCamera(location1);
                        }


                    }


                } else {
                    Toast.makeText(context, "Null location", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });

        ac_destination = findViewById(R.id.ac_destination);
        ac_destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    ed_desti_clear.setVisibility(View.VISIBLE);
                    // Toast.makeText(context, s.toString(), Toast.LENGTH_SHORT).show();

                } else {
                    ed_desti_clear.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ed_desti_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initDatabase();
                ac_destination.setText("");
                PicLocation_destination = null;
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        // databaseReference = firebaseDatabase.getReference("ambulanceLatestLocations");
        checkStatus();


        //

        argbEvaluator = new ArgbEvaluator();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int devHeight = displayMetrics.heightPixels;
        int devWidth = displayMetrics.widthPixels;

        // setUpPagerAdapter();


    }


    private void initAllAutoComplete() {
        String apiKey = getString(R.string.google_maps_key);


        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        placesClient = Places.createClient(this);
        //  initAutoCompleteTextView();
        initAutoCompleteTextViewDestination();
    }

    private void checkStatus() {
        Log.i("mkl_his", "" + spacialCounter);
        // spacialCounter++;

        firebaseDatabase.getReference(USER_STATUS).child(USER_ID).addChildEventListener(new ChildEventListener() {
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
                    //   Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("rideID"));
                    //  Log.i("mkl_v", "VIEW =>  "+hashMapInt.get("isRidingNow"));


                    if (isRiding == 0) {
                        //driver is not on ride
                        Log.i("mkl", "passenger was free");
                        isDriverOccupied = false;
                        relativeDashbody.setVisibility(View.VISIBLE);
                        // init_map();
                    } else if (isRiding == 2) {
                        //in proccecing state
                        Toast.makeText(context, "Processing state", Toast.LENGTH_SHORT).show();
                        linerPendingRequest.animate().translationY(0);
                        relativeDashbody.setVisibility(View.GONE);

                        String RIDE_ID = hashMapString.get("rideID");
                        String DRIVER_ID = hashMapString.get("driverID");
                        if (RIDE_ID != null && RIDE_ID.length() > 0 && DRIVER_ID != null && DRIVER_ID.length() > 0) {

                            firebaseDatabase.getReference(AMBULANCE_REQUEST).child(DRIVER_ID).child(RIDE_ID).child(DATA).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //  Log.i("mkl_his",""+spacialCounter);

                                    TripHistoryModel tripHistoryModel = dataSnapshot.getValue(TripHistoryModel.class);
                                    //Log.i("mkl_his", tripHistoryModel.getFrom());
                                    picup.setText(tripHistoryModel.getFrom());
                                    tv_destination.setText(tripHistoryModel.getTo());
                                    //tv_estimated_fair.setText(tripHistoryModel.getEstimated() + " BDT");

                                    Double distance = Double.valueOf(tripHistoryModel.getDistance());
                                    distance = distance / 1000;
                                    tv_distance.setText(distance + " KM");
                                    tv_estimated_fair.setText(tripHistoryModel.getEstimated() + " BDT");

                                    cardPendingCallCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            firebaseDatabase.getReference(USER_STATUS).child(DRIVER_ID).child(DATA).child("isRidingNow").setValue(0);
                                            firebaseDatabase.getReference(USER_STATUS).child(USER_ID).child(DATA).child("isRidingNow").setValue(0);
                                            linerPendingRequest.animate().translationY(linerPendingRequest.getHeight());
                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        //driver is hired
                        Log.i("mkl", "passenger is allready occupied");
                        isDriverOccupied = true;

                        if (isRideActivityOpenTriggered == false) {
                            startActivity(new Intent(context, RideOnActivity.class));
                        }
                        isRideActivityOpenTriggered = true;


                        // onBackPressed();


                    }


                } else {
                    Log.i("mkl_v", "no  data");
                    //init_map();
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                Log.i("mkl11", dataSnapshot.toString());

                HashMap<String, Long> hashMapInt = (HashMap<String, Long>) dataSnapshot.getValue();
                Long isRiding = hashMapInt.get("isRidingNow");
                if (isRiding == 0) {
                    //driver is now free
                    Log.i("mkl", "passenger is now free");

                    isDriverOccupied = false;
                    relativeDashbody.setVisibility(View.VISIBLE);


                    linerPendingRequest.animate().translationY(linerPendingRequest.getHeight());
                    relativeDashbody.setVisibility(View.VISIBLE);

                    //init_map();
                } else if (isRiding == 2) {
                    //in proccecing state
                    Toast.makeText(context, "Processing state", Toast.LENGTH_SHORT).show();
                    linerPendingRequest.animate().translationY(0);
                    relativeDashbody.setVisibility(View.GONE);


                } else {
                    //driver is hired now
                    Log.i("mkl", "passenger is now occupied");
                    isDriverOccupied = true;

                    if (isRideActivityOpenTriggered == false) {
                        startActivity(new Intent(context, RideOnActivity.class));
                    }
                    isRideActivityOpenTriggered = true;

                    // onBackPressed();

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

    private void init_map() {
        OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {


                Log.i("mkl", "map ready");

                mMap = googleMap;
                mMap.setMyLocationEnabled(true);
                mMap.setPadding(00, 400, 20, 00);


                MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.maps_style);
                googleMap.setMapStyle(style);
                mMap.setMaxZoomPreference(20);

                //  mMap.getUiSettings().setAllGesturesEnabled(false);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setZoomGesturesEnabled(false);


                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location_) {
                        picLocationAddress = myfunctions.getLocationName_(context, location_);
                        autoCompleteTextView.setText(picLocationAddress);
                        autoCompleteTextView.clearFocus();
                        ac_destination.requestFocus();
                        PicLocation = new LatLng(location_.getLatitude(), location_.getLongitude());
                        picLocationAddress = picLocationAddress;
                        Log.i("mkl", "location updated from listener" + location_.toString());

                        LatLng sydney = new LatLng(location_.getLatitude(), location_.getLongitude());
                        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(PicLocation, 17);
                        if (true) {
                            // mMap.animateCamera(location);
                        }


                    }
                });


                initDatabase();


            }
        };

        initMapShow(MapsActivity.this, onMapReadyCallback, getSupportFragmentManager());

        mContext = MapsActivity.this;


        setUpStatusbar();
        //

        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBottomSheetOpen == false) {
                    showBottomNavigationView(bottom);
                    isBottomSheetOpen = true;
                }
            }
        });
    }

    private void hideBottomNavigationView(LinearLayout view) {
        view.animate().translationY(view.getHeight() - 70);
    }

    private void hideBottomNavigationViewFull(LinearLayout view) {
        view.animate().translationY(view.getHeight());
    }

    private void showBottomNavigationView(LinearLayout view) {
        // Gets linearlayout
// Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = view.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.height = getScreenResolution(context) - 300;
        params.width = view.getWidth();
        view.setLayoutParams(params);

        view.animate().translationY(0);
    }

    void changePositionSmoothly(Marker marker, LatLng newLatLng) {
        if (marker == null) {
            return;
        }
        ValueAnimator animation = ValueAnimator.ofFloat(0f, 100f);
        final float[] previousStep = {0f};
        double deltaLatitude = newLatLng.latitude - marker.getPosition().latitude;
        double deltaLongitude = newLatLng.longitude - marker.getPosition().latitude;
        animation.setDuration(1500);
        animation.addUpdateListener(animation1 -> {
            float deltaStep = (Float) animation1.getAnimatedValue() - previousStep[0];
            previousStep[0] = (Float) animation1.getAnimatedValue();
            marker.setPosition(new LatLng(marker.getPosition().latitude + deltaLatitude * deltaStep * 1 / 100, marker.getPosition().latitude + deltaStep * deltaLongitude * 1 / 100));
        });
        animation.start();
    }

    private void animatedMarker(final LatLng startPosition, final LatLng nextPosition, final Marker mMarker) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(
                        startPosition.latitude * (1 - t) + nextPosition.latitude * t,
                        startPosition.longitude * (1 - t) + nextPosition.longitude * t);

                mMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 1);
                } else {
                    if (hideMarker) {
                        mMarker.setVisible(false);
                    } else {
                        mMarker.setVisible(true);
                    }
                }
            }
        });

    }

    private void initDatabase() {

        if (mMap != null) {
            mMap.clear();
        }

        ambulanceAC.clear();
        ambulanceNON_AC.clear();
        ambulanceICU.clear();
        ambulanceFREEZER.clear();
        // showBottomNavigationView(bottom);
        hideBottomNavigationViewFull(bottom);
        isBottomSheetOpen = false;
        ambulanceModels.clear();
        markerHashMap.clear();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ambulanceLatestLocations");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                try {


                    AmbulanceModel ambulanceModel = dataSnapshot.getValue(AmbulanceModel.class);
                    Log.i("mkl", dataSnapshot.toString());


                    HashMap<String, String> hashStrings = (HashMap<String, String>) dataSnapshot.getValue();
                    HashMap<String, Boolean> hashBools = (HashMap<String, Boolean>) dataSnapshot.getValue();
                    HashMap<String, Double> hashDouble = (HashMap<String, Double>) dataSnapshot.getValue();
                    HashMap<String, Integer> hashInt = (HashMap<String, Integer>) dataSnapshot.getValue();
                    HashMap<String, Long> hashLong = (HashMap<String, Long>) dataSnapshot.getValue();
                    HashMap<String, Long> hashInteger = (HashMap<String, Long>) dataSnapshot.getValue();
                    HashMap<String, ArrayList<String>> hashArrayList = (HashMap<String, ArrayList<String>>) dataSnapshot.getValue();

                    ambulanceModel.setCarDriverName(hashStrings.get("car_driver_name"));
                    ambulanceModel.setCarModel(hashStrings.get("car_model"));
                    ambulanceModel.setCarImage(hashStrings.get("car_image"));
                    ambulanceModel.setCarMobile(hashStrings.get("car_mobile"));
                    ambulanceModel.setCarType(hashArrayList.get("car_type"));

                    ambulanceModel.setCarIsOnTrip(hashBools.get("car_is_on_trip"));

                    ambulanceModel.setCarCurrentLat(hashDouble.get("car_current_lat"));
                    ambulanceModel.setCarCurrentLng(hashDouble.get("car_current_lng"));

                    ambulanceModel.setCarFairPerKm(hashLong.get("car_fair_per_km"));
                    ambulanceModel.setCarBaseCharge(hashLong.get("car_base_charge"));
                    ambulanceModel.setCarFarePerMinute(hashLong.get("car_fare_per_minute"));

                    ambulanceModel.setUserID(dataSnapshot.getKey());

                    //ambulanceModel.setCarType();

                    // ambulanceModel.setBearer((hashInteger.get("bearer")));

                    ambulanceModels.add(ambulanceModel);
                    LatLng position = new LatLng(ambulanceModel.getCarCurrentLat(), ambulanceModel.getCarCurrentLng());

                    //  BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ambulance_40);
                    //mMap.addMarker(new MarkerOptions().position(position)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_small));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(position).flat(true));
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, ambulanceModel.getUserID())));
                    marker.setAnchor(+1f, 1.25f);
                    // marker.setRotation(ambulanceModel.getBearer());


                    //addMarker_(position);

                    Gson gson = new Gson();

                    // Log.i("mkl_s", gson.toJson(ambulanceModel));
                    Log.i("mkl_s", ambulanceModel.getCarType().toString());
                    // addOverlay(position);


                    Drawable circleDrawable = getResources().getDrawable(R.drawable.marker_rectangle);
                    BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

                    Marker box = mMap.addMarker(new MarkerOptions().position(position).icon(markerIcon));

                    List<Marker> doubleModel = new ArrayList<>();
                    doubleModel.add(marker);
                    doubleModel.add(box);


                    markerHashMap.put(dataSnapshot.getKey(), doubleModel);

                } catch (Exception e) {
                    Log.i("mkl_s", e.getLocalizedMessage());
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                try {
                    HashMap<String, Double> hashDouble = (HashMap<String, Double>) dataSnapshot.getValue();
                    HashMap<String, Integer> hashInt = (HashMap<String, Integer>) dataSnapshot.getValue();
                    HashMap<String, Long> hashLong = (HashMap<String, Long>) dataSnapshot.getValue();


                    if ((hashDouble.get("car_current_lat") != null) && (hashDouble.get("car_current_lng") != null)) {

                        LatLng newLatlng = new LatLng(hashDouble.get("car_current_lat"), hashDouble.get("car_current_lng"));
                        List<Marker> doubleModel = new ArrayList<>();
                        doubleModel = markerHashMap.get(dataSnapshot.getKey());
                        // markerToMove.setRotation(hashLong.get("bearer"));


                        //  MarkerAnimation.animateMarkerToICS(markerToMove, newLatlng, new com.twopibd.dactarbari.ambulance.utils.LatLngInterpolator_.Spherical());
                        // animateMKL(markerToMove, newLatlng, new com.twopibd.dactarbari.ambulance.utils.LatLngInterpolator_.Spherical());
                        //  float bearing = (float) bearingBetweenLocations(markerToMove.getPosition(), newLatlng);
                        // rotateMarker(markerToMove, hashLong.get("bearer"), newLatlng);
                        //   rotateMarker(markerToMove, bearing, newLatlng);
                        //  markerToMove.setPosition(newLatlng);

                        // animatedMarker(markerToMove.getPosition(),newLatlng,markerToMove);
                        // changePositionSmoothly(markerToMove,newLatlng);
                        // animateMarker(newLatlng,markerToMove);

                        ArrayList<LatLng> Trips = new ArrayList<>();
                        Trips.add(doubleModel.get(0).getPosition());
                        Trips.add(newLatlng);

                        MarkerAnimation_.animateLine(Trips, mMap, doubleModel, context);
                        //   MarkerAnimation_.animateLine(Trips, mMap, doubleModel.get(1), context);


                    }
                } catch (Exception e) {
                    Log.i("mkl", e.getLocalizedMessage());

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
        initAllAutoComplete();

    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void addMarker_(LatLng destination) {

        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);

    }

    public void addOverlay(LatLng place) {
        /*

        GroundOverlay groundOverlay = mMap.addGroundOverlay(new
                GroundOverlayOptions()
                .position(place, 100)
                .transparency(1f)
                .zIndex(3)
                .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getDrawable(R.drawable.map_overlay)))));

        startOverlayAnimation(groundOverlay);

         */
    }


    private void startOverlayAnimation(final GroundOverlay groundOverlay) {

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 100);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final Integer val = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(val);

            }
        });

        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());
        tAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                groundOverlay.setTransparency(val);
            }
        });

        animatorSet.setDuration(3000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }

    private static void animateMarker(final Marker marker, final int current, final LatLng[] line) {
        if (line == null || line.length == 0 || current >= line.length) {
            return;
        }

        ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, line[current]);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animateMarker(marker, current + 1, line);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.setDuration(DURATION);
        animator.start();
    }

    private static LatLng[] bezier(LatLng p1, LatLng p2, double arcHeight, double skew, boolean up) {
        ArrayList<LatLng> list = new ArrayList<>();
        try {
            if (p1.longitude > p2.longitude) {
                LatLng tmp = p1;
                p1 = p2;
                p2 = tmp;
            }

            LatLng c = new LatLng((p1.latitude + p2.latitude) / 2, (p1.longitude + p2.longitude) / 2);

            double cLat = c.latitude;
            double cLon = c.longitude;

            //add skew and arcHeight to move the midPoint
            if (Math.abs(p1.longitude - p2.longitude) < 0.0001) {
                if (up) {
                    cLon -= arcHeight;
                } else {
                    cLon += arcHeight;
                    cLat += skew;
                }
            } else {
                if (up) {
                    cLat += arcHeight;
                } else {
                    cLat -= arcHeight;
                    cLon += skew;
                }
            }

            list.add(p1);
            //calculating points for bezier
            double tDelta = 1.0 / 10;
            CartesianCoordinates cart1 = new CartesianCoordinates(p1);
            CartesianCoordinates cart2 = new CartesianCoordinates(p2);
            CartesianCoordinates cart3 = new CartesianCoordinates(cLat, cLon);

            for (double t = 0; t <= 1.0; t += tDelta) {
                double oneMinusT = (1.0 - t);
                double t2 = Math.pow(t, 2);

                double y = oneMinusT * oneMinusT * cart1.y + 2 * t * oneMinusT * cart3.y + t2 * cart2.y;
                double x = oneMinusT * oneMinusT * cart1.x + 2 * t * oneMinusT * cart3.x + t2 * cart2.x;
                double z = oneMinusT * oneMinusT * cart1.z + 2 * t * oneMinusT * cart3.z + t2 * cart2.z;
                LatLng control = CartesianCoordinates.toLatLng(x, y, z);
                list.add(control);
            }

            list.add(p2);
        } catch (Exception e) {
            Log.e(TAG, "bezier error : ", e);
        }

        LatLng[] result = new LatLng[list.size()];
        result = list.toArray(result);

        return result;
    }

    public static void animateMKL(final Marker marker, final LatLng finalPosition, final LatLngInterpolator latLngInterpolator) {
        final LatLng startPosition = marker.getPosition();
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 2000;
        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);
                marker.setPosition(latLngInterpolator.interpolate(v, startPosition, finalPosition));
                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {

        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }


    private void rotateMarker(final Marker marker, final float toRotation, LatLng newLatlng) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();
            final long duration = 2000;

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);

                    float rot = t * toRotation + (1 - t) * startRotation;

                    float bearing = -rot > 180 ? rot / 2 : rot;

                    marker.setRotation(bearing);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 1);
                    } else {
                        isMarkerRotating = false;
                        marker.setPosition(newLatlng);
                    }
                }
            });
        }

        //  MarkerAnimation.animateMarkerToICS(marker, newLatlng, new LatLngInterpolator.Spherical());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setUpStatusbar() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initAutoCompleteTextView() {


        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener(autocompleteClickListener);
        adapter = new AutoCompleteAdapter(this, placesClient);
        autoCompleteTextView.setAdapter(adapter);
    }

    private void initAutoCompleteTextViewDestination() {
        Log.i("mkl", "autocomplease detination");


        ac_destination.setThreshold(1);
        ac_destination.setOnItemClickListener(autocompleteClickListenerDestination);
        adapter_destination = new AutoCompleteAdapter(this, placesClient);
        ac_destination.setAdapter(adapter_destination);
    }


    private AdapterView.OnItemClickListener autocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapter.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Log.i("mkl", task.getPlace().getLatLng().toString());
                            // Toast.makeText(context, "" + task.getPlace().getLatLng().toString(), Toast.LENGTH_SHORT).show();
                            autoCompleteTextView.setText(task.getPlace().getLatLng().latitude + " , " + task.getPlace().getLatLng().longitude);

                            picLocationAddress = task.getPlace().getAddress();
                            PicLocation = new LatLng(task.getPlace().getLatLng().latitude, task.getPlace().getLatLng().longitude);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i("mkl", e.toString());

                            e.printStackTrace();
                            autoCompleteTextView.setText(e.getMessage());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };
    private AdapterView.OnItemClickListener autocompleteClickListenerDestination = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            try {
                final AutocompletePrediction item = adapter_destination.getItem(i);
                String placeID = null;
                if (item != null) {
                    placeID = item.getPlaceId();
                }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.

                List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.LAT_LNG);

                FetchPlaceRequest request = null;
                if (placeID != null) {
                    request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build();
                }

                if (request != null) {
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(FetchPlaceResponse task) {
                            Log.i("mkl", task.getPlace().getLatLng().toString());
                            //  Toast.makeText(context, "" + task.getPlace().getLatLng().toString(), Toast.LENGTH_SHORT).show();
                            // ac_destination.setText(task.getPlace().getLatLng().latitude + " , " + task.getPlace().getLatLng().longitude);

                            ac_destination.setText(task.getPlace().getAddress());

                            ac_destination.clearFocus();
                            picLocationAddress_destination = task.getPlace().getAddress();
                            PicLocation_destination = new LatLng(task.getPlace().getLatLng().latitude, task.getPlace().getLatLng().longitude);
                            hideKeyboard(MapsActivity.this);


                            //drawLines(PicLocation, PicLocation_destination);
                            if (PicLocation != null) {
                                if (PicLocation_destination != null) {
                                    drawLines_2(PicLocation, PicLocation_destination);
                                } else {

                                }

                            }

                            ambulanceAC.clear();
                            ambulanceNON_AC.clear();
                            ambulanceICU.clear();
                            ambulanceFREEZER.clear();


                            if (ambulanceModels.size() > 0) {


                                for (int i = 0; i < ambulanceModels.size(); i++) {
                                    // Log.i("mkl_s","size +> "+ambulanceModels.get(i).getCarType().size());
                                    Gson gson = new Gson();
                                    //  Log.i("mkl_s", "> " + gson.toJson(ambulanceModels.get(i)));


                                    for (int k = 0; k < ambulanceModels.get(i).getCarType().size(); k++) {
                                        if (ambulanceModels.get(i).getCarType().get(k).equals("AC")) {
                                            ambulanceAC.add(ambulanceModels.get(i));
                                        } else if (ambulanceModels.get(i).getCarType().get(k).equals("Non-AC")) {
                                            ambulanceNON_AC.add(ambulanceModels.get(i));
                                        } else if (ambulanceModels.get(i).getCarType().get(k).equals("ICU")) {
                                            ambulanceICU.add(ambulanceModels.get(i));
                                        } else if (ambulanceModels.get(i).getCarType().get(k).equals("FREEZER")) {
                                            ambulanceFREEZER.add(ambulanceModels.get(i));
                                        }
                                    }


                                }
                            } else {
                                Toast.makeText(context, "No Ambulance on Database", Toast.LENGTH_LONG).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                            ac_destination.setText(e.getMessage());
                            Log.i("mkl", "err 2 " + e.getLocalizedMessage());

                        }
                    });
                }
            } catch (Exception e) {
                Log.i("mkl", "err 1" + e.getLocalizedMessage());

                e.printStackTrace();
            }

        }
    };

    private void drawLines_2(LatLng picLocation, LatLng picLocation_destination) {

        Double bearing = bearingBetweenLocations(picLocation, picLocation_destination);
        Log.i("mkl", "Bearing => " + bearing);
        //Toast.makeText(context, "" + bearing, Toast.LENGTH_SHORT).show();


        mMap.clear();
        // initDatabase();


        Log.i("mkl", "Going to hit retro");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getPolyline polyline = retrofit.create(getPolyline.class);
        String apiKey = getString(R.string.google_maps_key);
        //picLocation.latitude + "," + picLocation.longitude, picLocation_destination.latitude + "," + picLocation_destination.longitude
        polyline.getPolylineData_2("driving", picLocation.latitude + "," + picLocation.longitude, picLocation_destination.latitude + "," + picLocation_destination.longitude, apiKey)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                        // response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText()
                        //showBottomSheet();
                        int Second = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getValue();
                        int minute = Second % 60;
                        int hour = 0;
                        String time = "";
                        if (minute > 60) {
                            hour = minute % 60;
                            time += hour + " H " + minute + " M";
                        } else {
                            time += minute + " M";
                        }

                        TRAVEL_DISTANCE = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getValue();
                        TRAVEL_TIME_APPROX = response.body().getRoutes().get(0).getLegs().get(0).getDuration().getValue();

                        List<Route> routeList = response.body().getRoutes();
                        for (Route route : routeList) {
                            String polyLine = route.getOverviewPolyline().getPoints();
                            polyLineList = decodePoly(polyLine);
                            drawPolyLineAndAnimateCar();
                        }
                        LatLng real = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude), (polyLineList.get(polyLineList.size() - 1).longitude));

                        // mMap.addMarker(new MarkerOptions().position(real)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.place_holder_red));
                        // Marker marker=new MarkerOptions().position(real)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, ac_destination.getText().toString()))
//.setAnchor(1f,1f)

                        Drawable circleDrawable = getResources().getDrawable(R.drawable.marker_rectangle);
                        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

                        mMap.addMarker(new MarkerOptions().position(real).icon(markerIcon));


                        //  mMap.addMarker(new MarkerOptions().position(new LatLng(PicLocation.latitude-0.003,PicLocation.longitude))).setIcon(BitmapDescriptorFactory.fromBitmap(createStarterMarker(MapsActivity.this, R.drawable.circle_inner_gap, picLocationAddress)));
                        //  initDatabase();
                        if (bearing > -1 && bearing < 90) {
                            //0,1
                            Toast.makeText(context, "Top right", Toast.LENGTH_SHORT).show();

                            mMap.addMarker(new MarkerOptions().position(real).anchor(+1f, 1.2f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, ac_destination.getText().toString())));
                            mMap.addMarker(new MarkerOptions().position(PicLocation).anchor(0.0f, +0.0f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, "Pick Location")));


                        } else if (bearing > 90 && bearing < 180) {
                            Toast.makeText(context, "top left", Toast.LENGTH_SHORT).show();
                            mMap.addMarker(new MarkerOptions().position(real).anchor(+1.0f, -0.0f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, ac_destination.getText().toString())));
                            mMap.addMarker(new MarkerOptions().position(PicLocation).anchor(0.0f, +1.2f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, "Pick Location")));


                        } else if (bearing > 180 && bearing < 270) {
                            Toast.makeText(context, "down left", Toast.LENGTH_SHORT).show();
                            mMap.addMarker(new MarkerOptions().position(real).anchor(+0.0f, +0.0f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, ac_destination.getText().toString())));
                            mMap.addMarker(new MarkerOptions().position(PicLocation).anchor(0.0f, +1.2f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, "Pick Location")));


                        } else if (bearing > 270 && bearing < 360) {
                            Toast.makeText(context, "bottom right", Toast.LENGTH_SHORT).show();
                            //
                            mMap.addMarker(new MarkerOptions().position(real).anchor(0.0f, +1.2f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, ac_destination.getText().toString())));
                            mMap.addMarker(new MarkerOptions().position(PicLocation).anchor(0.0f, +1.2f)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, "Pick Location")));


                        }
                        showAvailableAmbulance(PicLocation);
                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, Throwable t) {
                        Log.i("mkl", t.getLocalizedMessage());

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkStatus();

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void showAvailableAmbulance(LatLng picLocation) {
        hideKeyboard(MapsActivity.this);
        showBottomNavigationView(bottom);
        relativeDashbody.setVisibility(View.GONE);
        isBottomSheetOpen = true;
        tabLayout.setTabTextColors(getResources().getColor(R.color.normal_color), getResources().getColor(R.color.selected_color));
        //tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        setupViewPager(viewpager);


        tabLayout.setupWithViewPager(viewpager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new AmbulanceACFragment().getInstance(ambulanceAC), "AC");
        adapter.addFragment(new AmbulanceNonACFragment().getInstance(ambulanceNON_AC), "Non AC");
        adapter.addFragment(new AmbulanceICUFragment().getInstance(ambulanceICU), "ICU");
        adapter.addFragment(new AmbulanceFREEZERFragment().getInstance(ambulanceFREEZER), "Freezer");


        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public void onBackPressed() {

        mMap.setPadding(000, 00, 000, 000);
        if (isBottomSheetOpen == true) {
            relativeDashbody.setVisibility(View.VISIBLE);

            hideBottomNavigationView(bottom);
            isBottomSheetOpen = false;
            initDatabase();
            ac_destination.setText("");
        } else {
            super.onBackPressed();
        }


    }

    public static Bitmap createStarterMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.start_marker, null);

        // CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        // markerImage.setImageResource(resource);
        TextView txt_name = (TextView) marker.findViewById(R.id.tv_start);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker, null);

        // CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        // markerImage.setImageResource(resource);
        TextView txt_name = (TextView) marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

    public void showBottomSheet() {
        CarsFragments carsFragments =
                CarsFragments.newInstance();
        carsFragments.show(getSupportFragmentManager(),
                "cars");
    }

    public int getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return height;
    }

    private void drawPolyLineAndAnimateCar() {

        mMap.setPadding(000, 50, 000, (getScreenResolution(context) - 400));
        //Adjusting bounds
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : polyLineList) {
            builder.include(latLng);
        }
        final int MAP_BOUND_PADDING = 50;  /* In dp */
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_BOUND_PADDING);
        mMap.animateCamera(mCameraUpdate);
        //  mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.parseColor("#ABEBC6"));
        polylineOptions.width(5);
        polylineOptions.startCap(new SquareCap());
        polylineOptions.endCap(new SquareCap());
        polylineOptions.jointType(ROUND);
        polylineOptions.addAll(polyLineList);
        greyPolyLine = mMap.addPolyline(polylineOptions);

        blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(5);
        blackPolylineOptions.color(Color.parseColor("#58D68D"));
        blackPolylineOptions.startCap(new SquareCap());
        blackPolylineOptions.endCap(new SquareCap());
        blackPolylineOptions.jointType(ROUND);
        blackPolyline = mMap.addPolyline(blackPolylineOptions);
        LatLng real = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude), (polyLineList.get(polyLineList.size() - 1).longitude));
        Log.i("mkl", polyLineList.get(polyLineList.size() - 1).toString());

        LatLng target = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude + 0.0000), (polyLineList.get(polyLineList.size() - 1).longitude + 0.00000));
        LatLng target1 = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude + 0.0003), (polyLineList.get(polyLineList.size() - 1).longitude - 0.00));
        LatLng target2 = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude - 0.000), (polyLineList.get(polyLineList.size() - 1).longitude - 0.002));
        LatLng target3 = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude - 0.000), (polyLineList.get(polyLineList.size() - 1).longitude - 0.003));
        LatLng target4 = new LatLng((polyLineList.get(polyLineList.size() - 1).latitude - 0.000), (polyLineList.get(polyLineList.size() - 1).longitude - 0.004));
        Log.i("mkl", target.toString());

        //mMap.addMarker(new MarkerOptions().position(real)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.place_holder_red));

        // mMap.addMarker(new MarkerOptions().position(target1)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this, R.drawable.circle_inner_gap, null)));
       /* mMap.addMarker(new MarkerOptions().position(target1)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this,R.drawable.circle_inner_gap,null)));
        mMap.addMarker(new MarkerOptions().position(target2)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this,R.drawable.circle_inner_gap,null)));
        mMap.addMarker(new MarkerOptions().position(target3)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this,R.drawable.circle_inner_gap,null)));
        mMap.addMarker(new MarkerOptions().position(target4)).setIcon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(MapsActivity.this,R.drawable.circle_inner_gap,null)));

        */

        // mMap.addMarker(new MarkerOptions().position(target));


        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0, 100);
        polylineAnimator.setDuration(3000);
        polylineAnimator.setInterpolator(new LinearInterpolator());
        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                List<LatLng> points = greyPolyLine.getPoints();
                int percentValue = (int) valueAnimator.getAnimatedValue();
                int size = points.size();
                int newPoints = (int) (size * (percentValue / 100.0f));
                List<LatLng> p = points.subList(0, newPoints);
                blackPolyline.setPoints(p);
            }
        });
        polylineAnimator.start();
        polylineAnimator.setRepeatCount(ValueAnimator.INFINITE);
//        marker = mMap.addMarker(new MarkerOptions().position(sydney)
//                .flat(true)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        handler = new Handler();
        index = -1;
        next = 1;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (index < polyLineList.size() - 1) {
                    index++;
                    next = index + 1;
                }
                if (index < polyLineList.size() - 1) {
                    startPosition = polyLineList.get(index);
                    endPosition = polyLineList.get(next);
                }
                if (index == 0) {
                    BeginJourneyEvent beginJourneyEvent = new BeginJourneyEvent();
                    beginJourneyEvent.setBeginLatLng(startPosition);
                    JourneyEventBus.getInstance().setOnJourneyBegin(beginJourneyEvent);
                }
                if (index == polyLineList.size() - 1) {
                    EndJourneyEvent endJourneyEvent = new EndJourneyEvent();
                    endJourneyEvent.setEndJourneyLatLng(new LatLng(polyLineList.get(index).latitude,
                            polyLineList.get(index).longitude));
                    JourneyEventBus.getInstance().setOnJourneyEnd(endJourneyEvent);
                }
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(3000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                        /*
                        v = valueAnimator.getAnimatedFraction();
                        lng = v * endPosition.longitude + (1 - v)
                                * startPosition.longitude;
                        lat = v * endPosition.latitude + (1 - v)
                                * startPosition.latitude;
                        LatLng newPos = new LatLng(lat, lng);
                        CurrentJourneyEvent currentJourneyEvent = new CurrentJourneyEvent();
                        currentJourneyEvent.setCurrentLatLng(newPos);
                        JourneyEventBus.getInstance().setOnJourneyUpdate(currentJourneyEvent);
                        marker.setPosition(newPos);
                        marker.setAnchor(0.5f, 0.5f);
                        marker.setRotation(getBearing(startPosition, newPos));
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition
                                (new CameraPosition.Builder().target(newPos)
                                        .zoom(15.5f).build()));

                         */
                    }
                });
                valueAnimator.start();
                if (index != polyLineList.size() - 1) {
                    handler.postDelayed(this, 3000);
                }
            }
        }, 3000);
    }


    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ignored) {
        }

        return routes;
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    void drawPolyline(List<List<HashMap<String, String>>> result) {

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            this.listLatLng.addAll(points);
        }

        lineOptions.width(10);
        lineOptions.color(Color.BLACK);
        lineOptions.startCap(new SquareCap());
        lineOptions.endCap(new SquareCap());
        lineOptions.jointType(ROUND);
        blackPolyLine = mMap.addPolyline(lineOptions);

        PolylineOptions greyOptions = new PolylineOptions();
        greyOptions.width(10);
        greyOptions.color(Color.GRAY);
        greyOptions.startCap(new SquareCap());
        greyOptions.endCap(new SquareCap());
        greyOptions.jointType(ROUND);
        greyPolyLine = mMap.addPolyline(greyOptions);

        animatePolyLine();
    }

    private void animatePolyLine() {

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize < newPoints) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }


            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();

    }

    private void addMarker(LatLng destination) {

        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

            addMarker(listLatLng.get(listLatLng.size() - 1));
        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);

            blackPolyLine.setZIndex(2);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {


        }
    };

    public void chooseLocation(View view) {
        startActivity(new Intent(this, LocationPickActivity.class));
    }
}



