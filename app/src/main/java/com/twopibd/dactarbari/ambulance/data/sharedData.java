package com.twopibd.dactarbari.ambulance.data;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.twopibd.dactarbari.ambulance.model.AmbulanceModel;
import com.twopibd.dactarbari.ambulance.model.RideRequestModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class sharedData {

    public  static int TRAVEL_DISTANCE=0;
    public  static int TRAVEL_TIME_APPROX=0;
    public  static RideRequestModel RIDE_REQUEST_MODEL;
    public  static AmbulanceModel AMBULANCE_MODEL;

    public  static String getLocationName(Context context, LatLng temp_location) {
        String name="";
        // Toast.makeText(context, ""+temp_location.getLatitude()+"\n"+temp_location.getLongitude(), Toast.LENGTH_LONG).show();


        if (temp_location!=null) {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context, Locale.getDefault());

            if (geocoder != null) {

                try {
                    addresses = geocoder.getFromLocation(temp_location.latitude, temp_location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                  if (addresses!=null&&addresses.size()>0) {
                      String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                      String city = addresses.get(0).getLocality();
                      String state = addresses.get(0).getAdminArea();
                      String country = addresses.get(0).getCountryName();
                      String postalCode = addresses.get(0).getPostalCode();
                      String knownName = addresses.get(0).getFeatureName();
                      name = knownName + "\n" + address + "\n" + city + "\n" + state + "\n" + postalCode + "\n" + country;
                      name = address;
                  }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                name = "No location available";
            }
        }
        return  name;



    }
}
