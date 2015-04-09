package com.medialabamsterdam.checklistprototype;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LocationUtils {

    private static final int SECONDS_LAST_LOCATION = 15;
    private static Context mContext;

    public static Location getLastLocation() {
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.NO_REQUIREMENT);
        List<String> providers = manager.getProviders(criteria, true);
        List<Location> locations = new ArrayList<>();
        for (String provider : providers) {
            Location location = manager.getLastKnownLocation(provider);
            if (location != null) {
                locations.add(location);
            }
        }
        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location location, Location location2) {
                return (int) (location.getAccuracy() - location2.getAccuracy());
            }
        });
        if (locations.size() > 0) {
            return locations.get(0);
        }
        return null;
    }

    public static String getLatLon(Context context) {
        mContext = context;
        return getLatLon(getLastLocation());
    }

    public static String getLatLon(Location location) {
        if (location == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(location.getLatitude()).append(',').append(location.getLongitude());
        return sb.toString();
    }

    public static int getAgeInSeconds(long time) {
        return (int) ((System.currentTimeMillis() - time) / 1000);
    }

    public static void getRecentLocation(final LocationListener listener) {
        Location last = LocationUtils.getLastLocation();
        if (last == null || LocationUtils.getAgeInSeconds(last.getTime()) > SECONDS_LAST_LOCATION) {

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
            final LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(criteria, true);
            if (providers.size() == 0) {
                listener.onLocationFailed();
                return;
            }
            locationSuccess = false;
            final android.location.LocationListener locationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    locationSuccess = true;
                    locationManager.removeUpdates(this);
                    listener.onLocationAcquired(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            for (String provider : providers) {
                Log.v("DEBUG", provider);
                locationManager.requestLocationUpdates(provider, 1000, 5, locationListener);
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!locationSuccess) {
                        locationManager.removeUpdates(locationListener);
                        listener.onLocationFailed();
                    }
                }
            }, 10000);
        } else
            listener.onLocationAcquired(last);
    }

    private static boolean locationSuccess = false;

    public interface LocationListener {
        public void onLocationAcquired(Location location);

        public void onLocationFailed();
    }
}