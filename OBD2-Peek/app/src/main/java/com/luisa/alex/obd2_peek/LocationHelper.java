package com.luisa.alex.obd2_peek;

import android.util.Log;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;


import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by alex on 2016-11-25.
 */

public class LocationHelper {

    public static Location getLastLocation(FragmentActivity activity, LocationListener locListener) {
        String TAG = "getLastLocation";
        Log.d(TAG, "called()");
        Location lastKnownLocation = null;
        //Get the address of the last known location of the phone
        if(activity.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permissions OK!");

            //Get the location Manager
            LocationManager locationManager = (LocationManager)activity.getSystemService(LOCATION_SERVICE);

            //Set the criteria for FINE LOCATION
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            criteria.setCostAllowed(false);

            //Get the best gps provider
            String recommendedProvider = locationManager.getBestProvider(criteria, true);
            //Bind the location manager to the listener
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locListener);

            //Finally obtain the last known location
            lastKnownLocation = locationManager.getLastKnownLocation(recommendedProvider);
            if(lastKnownLocation != null){
                //Log.d("getLastLocation", "last known location: " + lastKnownLocation);
            }else{
                Log.d("getLastLocation", "location is null");
            }
        }else{
            Log.d(TAG, "Permissions Failed!");
        }
        return lastKnownLocation;
    }

    public static Address locToAddress(FragmentActivity activity, Location location) {
        String TAG = "locToAddress";
        Log.d(TAG, "called()");
        if(location == null){return null;}
        Address address = null;

        //Obtain the Geocoder
        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

            try{
                List<Address> resultsAdr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(resultsAdr.size() > 0){
                    address = resultsAdr.get(0);
                    //Log.d(TAG, "Found address:" + address.toString());

                }else{
                    Log.d(TAG, "No result addresses found");
                }
            }catch(Exception e){

                e.printStackTrace();
            }
        }else{
            Log.d(TAG , "Geocoder not found");
        }

        return address;
    }

}

