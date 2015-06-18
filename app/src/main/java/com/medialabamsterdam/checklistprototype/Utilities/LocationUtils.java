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
 * <p>
 * This class is meant to manage the GPS and acquire the device's location.
 */
public class LocationUtils {

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static final int FIVE_SECONDS = 1000 * 5;

    private final LocationManager mLocationManager;
    final private LocationListener mLocationListener;
    final private List<String> providers;
    private Location mLocation;

    public LocationUtils(Context context) {
        // Sets the criteria to use when querying for GPS data.
        // Currently using "ACCURACY_FINE".
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
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
                if (isBetterLocation(location, mLocation)) {
                    mLocation = location;
                }
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
        mLocation = null;
        for (String provider : providers) {
            if (mLocationManager.isProviderEnabled(provider)) {
                mLocationManager.requestLocationUpdates(provider, FIVE_SECONDS, 0, mLocationListener);
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

    /** Determines whether one Location reading is better than the current Location
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }
        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
