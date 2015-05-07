package com.medialabamsterdam.checklistprototype.Utilities;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

    public LocationUtils(Context context) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String provider = mLocationManager.getBestProvider(criteria, true);
        boolean isEnabled = mLocationManager.isProviderEnabled(provider);
        if (isEnabled) {
            Log.e("SUCCESS:", " GPS is enabled on provider " + provider);
            // Define a listener that responds to location updates
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

            // Register the listener with the Location Manager to receive location updates
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
        } else {
            Log.e("ERROR:", " GPS not enabled on provider " + provider);
        }
    }

    public void restart() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
    }

    public Location getLocation() {
        return mLocation;
    }

    public void stop() {
        mLocationManager.removeUpdates(mLocationListener);
    }
}
