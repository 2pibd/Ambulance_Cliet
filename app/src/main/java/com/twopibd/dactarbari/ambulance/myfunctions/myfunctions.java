package com.twopibd.dactarbari.ambulance.myfunctions;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.twopibd.dactarbari.ambulance.R;
import com.twopibd.dactarbari.ambulance.activity.MapsActivity;
import com.twopibd.dactarbari.ambulance.api.getPolyline;
import com.twopibd.dactarbari.ambulance.listeners.PicUploadListener;
import com.twopibd.dactarbari.ambulance.model.Result;
import com.twopibd.dactarbari.ambulance.model.Route;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_DISTANCE;
import static com.twopibd.dactarbari.ambulance.data.sharedData.TRAVEL_TIME_APPROX;

public class myfunctions {
    public static void uploadPhoto(Uri resultUri, Context context, PicUploadListener.myPicUploadListener listener) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();


        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Image Uploading...");
        progressDialog.show();
        StorageReference ref;
        ref = storageReference.child("images/" + UUID.randomUUID().toString());

        ref.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, ref.getPath(), Toast.LENGTH_SHORT).show();
                        // Glide.with(context).load(resultUri).into(imgSetQuestionAnswer2);
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // FIRST_UPLOADED_IMAGE_LINK = uri.toString();
                                //upload second  image
                                progressDialog.dismiss();
                                Toast.makeText(context, uri.toString(), Toast.LENGTH_SHORT).show();
                                listener.onPicUploadSucced(uri.toString());


                            }
                        });

                        //uploadData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        listener.onPicUploadFailed(e.getMessage());
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }

    public static String getCityName(Context context, LatLng temp_location) {
        String name = "";
        // Toast.makeText(context, ""+temp_location.getLatitude()+"\n"+temp_location.getLongitude(), Toast.LENGTH_LONG).show();


        if (temp_location != null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(temp_location.latitude, temp_location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                name = knownName + "\n" + address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country;
                name = country;


            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            name = "No location available";
        }
        return name;


    }

    public static String getLocationName_(Context context, Location temp_location) {
        String name = "";
        // Toast.makeText(context, ""+temp_location.getLatitude()+"\n"+temp_location.getLongitude(), Toast.LENGTH_LONG).show();


        if (temp_location != null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(temp_location.getLatitude(), temp_location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                name = knownName + "\n" + address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country;
                name = address;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            name = "No location available";
        }
        return name;


    }

    public static String getLocationNameByLatLng(Context context, LatLng temp_location) {
        String name = "";
        // Toast.makeText(context, ""+temp_location.getLatitude()+"\n"+temp_location.getLongitude(), Toast.LENGTH_LONG).show();


        if (temp_location != null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(temp_location.latitude, temp_location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                name = knownName + "\n" + address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country;
                name = address;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            name = "No location available";
        }
        return name;


    }

    public static void initMapShow(final Activity context, final OnMapReadyCallback onMapReadyCallback, final FragmentManager supportFragmentManager) {

        Dexter.withActivity(context)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Log.i("mkl", "Permitted");

                            // do you work now
                            SupportMapFragment mapFragment = (SupportMapFragment) supportFragmentManager
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(onMapReadyCallback);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            Log.i("mkl", "denied");

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();


    }

    public static void getDirectionAndRoute(Context context, LatLng startLocation, String destination, commonLIsteners.RouteDistanceRetrivedListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getPolyline polyline = retrofit.create(getPolyline.class);
        String apiKey = context.getString(R.string.google_maps_key);
        //picLocation.latitude + "," + picLocation.longitude, picLocation_destination.latitude + "," + picLocation_destination.longitude
        polyline.getPolylineData_2("driving", "" + startLocation.latitude + "," + startLocation.longitude, "place_id:" + destination, apiKey)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                        // response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText()
                        //showBottomSheet();
                        Gson gson=new Gson();
                        Log.i("mkl_feb1",gson.toJson(response.body().getRoutes()));

                        if (response.body() != null && response.body().getRoutes() != null) {

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
                            listener.onRetrived(TRAVEL_DISTANCE, TRAVEL_TIME_APPROX);
                        } else {
                            Toast.makeText(context, "mo data Failed", Toast.LENGTH_SHORT).show();
                        }



                      /*
                        List<Route> routeList = response.body().getRoutes();
                        for (Route route : routeList) {
                            String polyLine = route.getOverviewPolyline().getPoints();
                            polyLineList = decodePoly(polyLine);
                            drawPolyLineAndAnimateCar();
                        }

                       */

                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, Throwable t) {
                        Log.i("mkl_r", t.getLocalizedMessage());
                        Toast.makeText(context, "", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public static void getDirectionAndRouteByLatLng(Context context, LatLng startLocation, LatLng destination, commonLIsteners.RouteDistanceRetrivedListener listener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        getPolyline polyline = retrofit.create(getPolyline.class);
        String apiKey = context.getString(R.string.google_maps_key);
        //picLocation.latitude + "," + picLocation.longitude, picLocation_destination.latitude + "," + picLocation_destination.longitude
        polyline.getPolylineData_2("driving", "" + startLocation.latitude + "," + startLocation.longitude, "" + destination.latitude + "," + destination.longitude, apiKey)
                .enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                        // response.body().getRoutes().get(0).getLegs().get(0).getDistance().getText()
                        //showBottomSheet();



                        Gson gson = new Gson();

                        Log.i("mklu",gson.toJson(response.body()));

                       // Toast.makeText(context, gson.toJson(response.body()), Toast.LENGTH_SHORT).show();
                        Log.i("mmm","driving "+"\n" + startLocation.latitude + "\n" + startLocation.longitude+ "\n" + destination.latitude + "\n" + destination.longitude+"\n"+apiKey);

                        if ( response.body() != null && response.body().getRoutes() != null && response.body().getRoutes().size() > 0) {
                            Toast.makeText(context, "route size "+response.body().getRoutes().size(), Toast.LENGTH_SHORT).show();

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
                            listener.onRetrived(TRAVEL_DISTANCE, TRAVEL_TIME_APPROX);
                        } else {
                           // Toast.makeText(context, "no data Failed hhhh", Toast.LENGTH_SHORT).show();
                        }



                      /*
                        List<Route> routeList = response.body().getRoutes();
                        for (Route route : routeList) {
                            String polyLine = route.getOverviewPolyline().getPoints();
                            polyLineList = decodePoly(polyLine);
                            drawPolyLineAndAnimateCar();
                        }

                       */

                    }

                    @Override
                    public void onFailure(@NonNull Call<Result> call, Throwable t) {
                        Log.i("mkl_r", t.getLocalizedMessage());
                        Toast.makeText(context, "this error "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
