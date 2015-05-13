package com.medialabamsterdam.checklistprototype.Utilities;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by
 * Jose Carlos Quintas Junior
 * juniorquintas@gmail.com
 * on 20/04/2015.
 */
public class LocationUtils {

    private final LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mLocationListener;
    private List<String> providers;

    public LocationUtils(Context context) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        providers = mLocationManager.getProviders(criteria, true);

        boolean isAnyEnabled = false;
        for (String provider : providers) {
            if (mLocationManager.isProviderEnabled(provider)){
                isAnyEnabled = true;
            }
        }
        if (isAnyEnabled) {
            Log.e("SUCCESS:", " GPS is enabled on providers: " + Arrays.toString(providers.toArray()));
        } else {
            Log.e("ERROR:", " GPS not enabled on providers: " + Arrays.toString(providers.toArray()));
        }

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        restart();
    }

    public void restart() {
        for (String provider : providers) {
            if (mLocationManager.isProviderEnabled(provider)){
                mLocationManager.requestLocationUpdates(provider, 1000, 0, mLocationListener);
            }
        }
    }

    public Location getLocation() {
        return mLocation;
    }

    public void stop() {
        mLocationManager.removeUpdates(mLocationListener);
    }
}
