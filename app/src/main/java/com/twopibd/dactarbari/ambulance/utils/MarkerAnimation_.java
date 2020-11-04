package com.twopibd.dactarbari.ambulance.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.util.Property;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class MarkerAnimation_ {
    static GoogleMap map;
    static ArrayList<LatLng> _trips = new ArrayList<>();
    static Marker _marker1;
    static Marker _marker2;
    static LatLngInterpolator _latLngInterpolator = new LatLngInterpolator.Spherical();

    public static void animateLine(ArrayList<LatLng> Trips, GoogleMap map, List<Marker> marker, Context current) {
        _trips.addAll(Trips);
        _marker1 = marker.get(0);
        _marker2 = marker.get(1);

        animateMarker();
    }


    public static void animateMarker() {
        TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
            @Override
            public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
                return _latLngInterpolator.interpolate(fraction, startValue, endValue);
            }
        };
        Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");

        ObjectAnimator animator1 = ObjectAnimator.ofObject(_marker1, property, typeEvaluator, _trips.get(0));
        ObjectAnimator animator2 = ObjectAnimator.ofObject(_marker2, property, typeEvaluator, _trips.get(0));

        //ObjectAnimator animator = ObjectAnimator.o(view, "alpha", 0.0f);
        animator1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //  animDrawable.stop();
                if (_trips.size() > 1) {
                    _trips.remove(0);
                    animateMarker();
                }
            }
        });

        animator1.setDuration(3000);
        animator1.start();

        animator2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                //  animDrawable.stop();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //  animDrawable.stop();
                if (_trips.size() > 1) {
                    _trips.remove(0);
                    animateMarker();
                }
            }
        });

        animator2.setDuration(3000);
        animator2.start();
    }
}