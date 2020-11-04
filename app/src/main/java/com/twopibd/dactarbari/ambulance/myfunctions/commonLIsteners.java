package com.twopibd.dactarbari.ambulance.myfunctions;

public class commonLIsteners {
    public interface RouteDistanceRetrivedListener {
        void onRetrived(int distance, int time);

    }

    RouteDistanceRetrivedListener routeDistanceRetrivedListener;

    public void setRouteDistanceRetrivedListener(RouteDistanceRetrivedListener listener) {
        this.routeDistanceRetrivedListener = listener;
    }
}
