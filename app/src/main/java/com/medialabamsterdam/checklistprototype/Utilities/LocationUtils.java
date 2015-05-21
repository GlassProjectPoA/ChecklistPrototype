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
 * <p/>
 * This class is meant to manage the GPS and acquire the device's location.
 */
public class LocationUtils {

    private final LocationManager mLocationManager;
    private Location mLocation;
    private LocationListener mLocationListener;
    private List<String> providers;

    public LocationUtils(Context context) {
        // Sets the criteria to use when querying for GPS data.
        // Currently using "ACCURACY_FINE".
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // Gets LocationManager.
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Applies the criteria.
        providers = mLocationManager.getProviders(criteria, true);
        // Checks if any of the Location Providers are Enabled.
        boolean isAnyEnabled = false;
        for (String provider : providers) {
            if (mLocationManager.isProviderEnabled(provider)) {
                isAnyEnabled = true;
            }
        }
        // Informs the debug accordingly.
        if (isAnyEnabled) {
            Log.e("SUCCESS:", " GPS is enabled on providers: " + Arrays.toString(providers.toArray()));
        } else {
            Log.e("ERROR:", " GPS not enabled on providers: " + Arrays.toString(providers.toArray()));
        }

        // Sets a Location Listener in order to change the mLocation variable when the location of
        // the device changes.
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Calls restart() to request location updates.
        restart();
    }

    /**
     * Starts or restarts the Location Updates.
     * Remember to use stop() when a location is acquired in order to save battery life.
     */
    public void restart() {
        for (String provider : providers) {
            if (mLocationManager.isProviderEnabled(provider)) {
                mLocationManager.requestLocationUpdates(provider, 1000, 0, mLocationListener);
            }
        }
    }

    /**
     * Stops the Location Updates.
     */
    public void stop() {
        mLocationManager.removeUpdates(mLocationListener);
    }

    public Location getLocation() {
        return mLocation;
    }
}
