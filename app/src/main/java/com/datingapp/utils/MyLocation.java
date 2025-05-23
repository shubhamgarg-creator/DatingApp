package com.datingapp.utils;
/**
 * @package com.datingapp
 * @subpackage utils
 * @category MyLocation
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

/*****************************************************************
 MyLocation
 ****************************************************************/
/*
 * This class can be used to find out the status of the network connection and
 * the gps mode. The status can be checked every 5 seconds. If the GPS is on,
 * the location can be calculated from the GPS, otherwise the location can be
 * calculated from network location.
 */
public class MyLocation {

    public static MyLocation myLocation = null;
    private Timer timer1;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private Context context;
    LocationListener locationListenerGps = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            timer1.cancel();
            System.out.println("Check3");
            locationResult.gotLocation(location);
            try {
                int hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION);
                if (hasLocationPermission == PackageManager.PERMISSION_GRANTED || hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerNetwork);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Called when the provider is disabled by the user. If
         * requestLocationUpdates is called on an already disabled provider,
         * this method is called immediately.
         *
         * @param provider
         *            the name of the location provider associated with this
         *            update.
         */
        @Override
        public void onProviderDisabled(String provider) {
        }

        /**
         * Called when the provider is enabled by the user.
         *
         * @param provider
         *            the name of the location provider associated with this
         *            update.
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * Called when the provider status changes. This method is called when a
         * provider is unable to fetch a location or if the provider has
         * recently become available after a period of unavailability.
         *
         * @param provider
         *            the name of the location provider associated with this
         *            update.
         * @param status
         *            OUT_OF_SERVICE if the provider is out of service, and this
         *            is not expected to change in the near future;
         *            TEMPORARILY_UNAVAILABLE if the provider is temporarily
         *            unavailable but is expected to be available shortly; and
         *            AVAILABLE if the provider is currently available.
         * @param extras
         *            an optional Bundle which will contain provider specific
         *            status variables. A number of common key/value pairs for
         *            the extras Bundle are listed below. Providers that use any
         *            of the keys on this list must provide the corresponding
         *            value as described below. satellites - the number of
         *            satellites used to derive the fix
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    LocationListener locationListenerNetwork = new LocationListener() {
        /**
         * Called when the location has changed. There are no restrictions on
         * the use of the supplied Location object.
         *
         * @param location
         *            The new location, as a Location object.
         */
        @Override
        public void onLocationChanged(Location location) {
            timer1.cancel();

            locationResult.gotLocation(location);
            try {
                int hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION);
                if (hasLocationPermission == PackageManager.PERMISSION_GRANTED || hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerGps);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Called when the provider is disabled by the user. If
         * requestLocationUpdates is called on an already disabled provider,
         * this method is called immediately.
         *
         * @param provider
         *            the name of the location provider associated with this
         *            update.
         */
        @Override
        public void onProviderDisabled(String provider) {
        }

        /**
         * Called when the provider is enabled by the user.
         *
         * @param provider
         *            the name of the location provider associated with this
         *            update.
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * Called when the provider status changes. This method is called when a
         * provider is unable to fetch a location or if the provider has
         * recently become available after a period of unavailability.
         *
         * @param provider
         *            the name of the location provider associated with this
         *            update.
         * @param status
         *            OUT_OF_SERVICE if the provider is out of service, and this
         *            is not expected to change in the near future;
         *            TEMPORARILY_UNAVAILABLE if the provider is temporarily
         *            unavailable but is expected to be available shortly; and
         *            AVAILABLE if the provider is currently available.
         * @param extras
         *            an optional Bundle which will contain provider specific
         *            status variables. A number of common key/value pairs for
         *            the extras Bundle are listed below. Providers that use any
         *            of the keys on this list must provide the corresponding
         *            value as described below. satellites - the number of
         *            satellites used to derive the fix
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    public static MyLocation defaultHandler() {
        if (myLocation == null) {
            myLocation = new MyLocation();
        }
        return myLocation;
    }

    public boolean getLocation(Context context, LocationResult result) {
        // I use LocationResult callback class to pass location value from
        // MyLocation to user code.
        System.out.println("Check6");
        locationResult = result;
        this.context = context;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled)
            return false;

        try {
            int hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION);
            if (hasLocationPermission == PackageManager.PERMISSION_GRANTED || hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                if (gps_enabled) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
                }
                if (network_enabled) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
                }
                System.out.println("Check7");
                timer1 = new Timer();
                timer1.schedule(new GetLastLocation(), 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Checks the Gps status
     *
     * @return boolean value of location status
     */
    public boolean isLocationAvailable(Context context) {
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return gps_enabled || network_enabled;
    }

    /**
     * This abstract class is used to get the location from other class.
     */
    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }

    /**
     * The GPS location and Network location to be calculated in every 5 seconds
     * with the help of this class
     */
    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            try {
                System.out.println("Check8");
                int hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission_group.LOCATION);
                if (hasLocationPermission == PackageManager.PERMISSION_GRANTED || hasLocationPermission != PackageManager.PERMISSION_GRANTED) {
                    lm.removeUpdates(locationListenerGps);
                    lm.removeUpdates(locationListenerNetwork);

                    Location net_loc = null, gps_loc = null;
                    if (gps_enabled)
                        gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                    if (network_enabled)
                        net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    // if there are both values use the latest one
                    if (gps_loc != null && net_loc != null) {
                        System.out.println("Check2");
                        if (gps_loc.getTime() > net_loc.getTime())
                            locationResult.gotLocation(gps_loc);
                        else
                            locationResult.gotLocation(net_loc);
                        return;
                    }

                    if (gps_loc != null) {
                        System.out.println("Check9");
                        locationResult.gotLocation(gps_loc);
                        return;
                    }
                    if (net_loc != null) {
                        System.out.println("Check10");
                        locationResult.gotLocation(net_loc);
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //locationResult.gotLocation(null);
        }
    }
}
