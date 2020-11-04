package com.twopibd.dactarbari.ambulance.api;

import com.google.gson.JsonObject;
import com.twopibd.dactarbari.ambulance.model.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by mohak on 2/8/17.
 */

public interface getPolyline {

    @GET("json")
    Call<JsonObject> getPolylineData(@Query("origin") String origin, @Query("destination") String destination, @Query("key") String key);
    //@Query("mode") String mode,
    //                                   @Query("transit_routing_preference") String routingPreference,

    @GET("json")
    Call<Result> getPolylineData_2(
            @Query("mode") String mode,
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("key") String key);

//    @GET("maps/api/directions/json")
//    Call Single<Result> getDirections(@Query("mode") String mode,
//                                        @Query("transit_routing_preference") String routingPreference,
//                                        @Query("origin") String origin, @Query("destination"), String destination, @Query("key") String key);
}