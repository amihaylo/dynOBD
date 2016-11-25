package com.luisa.alex.obd2_peek;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.MainThread;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.util.List;
import java.util.Locale;

public class LocatorActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private BoomMenuButton boomMenuButton;
    private boolean init = false;

    private static final int PERMISSIONS_REQ_CODE = 4001;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        boomMenuButton = (BoomMenuButton) findViewById(R.id.boom);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Get permission from the user
        requestLocationPermissions();
    }

    private void initApp() {
        //Check if Location(GPS) is enabled
        initLocationServices();
        //Get last known location
        Location lastKnownLocation = getLastLocation();
        //Get address from location
        Address address = locToAddress(lastKnownLocation);
        //Display the address to the user
        logAddress(address);
        //Show Address on the map
        showOnMap(address);
    }

    private void showOnMap(Address address) {
        if(address == null){return;}
        LatLng position = new LatLng(address.getLatitude(), address.getLongitude());

        //Setup and set the marker
        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.title("Marker!");
        mMap.addMarker(marker);

        //Zoom in with the camera
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(7);
        mMap.animateCamera(zoom);

        //TODO ADD ALL THE ADDRESS INFO TO THE MARKER

        mMap.setMinZoomPreference(4.0f);
        mMap.setMaxZoomPreference(10.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    private void logAddress(Address address) {
        String TAG = "showAddress";
        Log.d(TAG, "called()");
        if(address == null){return;}


        //Obtain all the information from the given address
        Double latitude = address.getLatitude();
        Double longitude = address.getLongitude();
        String address1 = address.getAddressLine(0);
        String address2 = address.getAddressLine(2);
        String city = address.getLocality();
        String province = address.getAdminArea();
        String country = address.getCountryName();
        String postal_code = address.getPostalCode();
        String phone = address.getPhone();
        String url = address.getUrl();


        //Log all the information
        if(latitude != null){Log.d(TAG, latitude + "");}
        if(longitude != null){Log.d(TAG, longitude + "");}
        if(address1 != null){Log.d(TAG, address1);}
        if(address2 != null){Log.d(TAG, address2);}
        if(city != null){Log.d(TAG, city);}
        if(province != null){Log.d(TAG, province);}
        if(country != null){Log.d(TAG, country);}
        if(postal_code != null){Log.d(TAG, postal_code);}
        if(phone != null){Log.d(TAG, phone);}
        if(url != null){Log.d(TAG, url);}
    }

    private Address locToAddress(Location location) {
        String TAG = "locToAddress";
        Log.d(TAG, "called()");
        if(location == null){return null;}
        Address address = null;

        //Obtain the Geocoder
        if(Geocoder.isPresent()){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try{
                List<Address> resultsAdr = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if(resultsAdr.size() > 0){
                    address = resultsAdr.get(0);
                    Log.d(TAG, "Found address:" + address.toString());

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

    private Location getLastLocation() {
        String TAG = "getLastLocation";
        Log.d(TAG, "called()");
        Location lastKnownLocation = null;
        //Get the address of the last known location of the phone
        if(checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "Permissions OK!");

            //Get the location Manager
            LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);

            //Finally obtain the last known location
            lastKnownLocation = locationManager.getLastKnownLocation(recommendedProvider);
            if(lastKnownLocation != null){
                Log.d("getLastLocation", "last known location: " + lastKnownLocation);
            }else{
                Log.d("getLastLocation", "location is null");
            }
        }else{
            Log.d(TAG, "Permissions Failed!");
        }
        return lastKnownLocation;
    }

    private void requestLocationPermissions() {
        String TAG = "reqLocationPermissions";
        //Get permission from the user to use the location services
        if(this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //The permissions has not been granted, must ask the user for it
            if(shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                //Explain to use why this is needed
                //showToast("Need to enable location please!");
            }
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQ_CODE);
            return;
        }else{
            Log.d(TAG, "Permissions OK");
            initApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQ_CODE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initApp();
                } else {
                    // tell the user that the feature will not work
                }
                return;
            }
        }
    }

    private void showToast(String message) {
        (Toast.makeText(this, message, Toast.LENGTH_LONG)).show();
    }

    private void initLocationServices() {
        //Determine if GPS is enabled, if not enable it
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Log.d("initLocationServices", "gps not enabled");
            //Not enabled, request user to enable it
            String locConfig = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            Intent enableGPSIntent = new Intent(locConfig);
            startActivity(enableGPSIntent);
        }else{
            Log.d("initLocationServices", "gps is enabled!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "LOCATION CHANGED");
        //Get address from location
        Address address = locToAddress(location);
        //Display the address to the user
        logAddress(address);
        //Show Address on the map
        showOnMap(address);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String TAG = "onStatusChanged";
        Log.d(TAG, "called()");
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Use a param to record whether the boom button has been initialized
        // Because we don't need to init it again when onResume()
        if (init)
            return;

        init = true;

        int[][] subButton1Colors = new int[1][2];
        int[][] subButton2Colors = new int[1][2];
        int[][] subButton3Colors = new int[1][2];
        int[][] subButton4Colors = new int[1][2];

        subButton1Colors[0][1] = ContextCompat.getColor(this, R.color.md_red_400);
        subButton1Colors[0][0] = Util.getInstance().getPressedColor(subButton1Colors[0][1]);

        subButton2Colors[0][1] = ContextCompat.getColor(this, R.color.md_light_blue_600);
        subButton2Colors[0][0] = Util.getInstance().getPressedColor(subButton2Colors[0][1]);

        subButton3Colors[0][1] = ContextCompat.getColor(this, R.color.md_amber_600);
        subButton3Colors[0][0] = Util.getInstance().getPressedColor(subButton3Colors[0][1]);

        subButton4Colors[0][1] = ContextCompat.getColor(this, R.color.md_deep_purple_400);
        subButton4Colors[0][0] = Util.getInstance().getPressedColor(subButton4Colors[0][1]);

        // Now with Builder, you can init BMB more convenient
        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.car), subButton1Colors[0], "About")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.home), subButton2Colors[0], "Home")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.help), subButton3Colors[0], "Help")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.past), subButton4Colors[0], "Trips")

                .button(ButtonType.CIRCLE)
                .boom(BoomType.HORIZONTAL_THROW_2)
                .place(PlaceType.CIRCLE_4_2)
                //.subButtonTextColor(Color.BLACK)
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {

                        //Prepare the intent to be returned to main
                        Intent resultIntent = new Intent();

                        switch (buttonIndex) {
                            case 0:
                                Log.d(TAG, "About was clicked");
                                setResult(MainActivity.ABOUT_REQ,resultIntent);
                                break;
                            case 1:
                                Log.d(TAG, "Home was clicked");
                                break;
                            case 2:
                                Log.d(TAG, "Help was clicked");
                                setResult(MainActivity.HELP_REQ,resultIntent);
                                break;
                            case 3:
                                Log.d(TAG, "Trips was clicked");
                                setResult(MainActivity.TRIPS_REQ,resultIntent);
                                break;
                            default:
                                Log.d(TAG, "There has been an error involving the subbuttons.");
                                break;
                        }
                        finish();
                    }
                })
                .init(boomMenuButton);
    }
}

