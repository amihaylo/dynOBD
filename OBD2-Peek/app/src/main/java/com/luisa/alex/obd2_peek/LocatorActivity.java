package com.luisa.alex.obd2_peek;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

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
        //requestLocationPermissions();

        initApp();
    }

    private void initApp() {
        //Get last known location
        Location currentLocation = LocationHelper.getLastLocation(this, this);
        //Get address from location
        Address address = LocationHelper.locToAddress(this, currentLocation);
        //Display the address to the user
        logAddress(address);
        //Show Address on the map
        showOnMap(address);
    }

    private void showOnMap(Address address) {
        if(address == null){return;}
        mMap.clear();
        LatLng position = new LatLng(address.getLatitude(), address.getLongitude());

        //Marker stuff
        MarkerOptions marker = new MarkerOptions();
        marker.position(position);
        marker.title("You are here");
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker));
        marker.snippet(address.getAddressLine(0) + ", \n" + address.getAddressLine(1) + ", \n" + address.getAddressLine(2));
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
              return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

              Context context = LocatorActivity.this;

              LinearLayout info = new LinearLayout(context);
              info.setOrientation(LinearLayout.VERTICAL);

              TextView title = new TextView(context);
              title.setTextColor(Color.BLACK);
              title.setGravity(Gravity.CENTER);
              title.setTypeface(null, Typeface.BOLD);
              title.setText(marker.getTitle());

              TextView snippet = new TextView(context);
              snippet.setTextColor(Color.GRAY);
              snippet.setText(marker.getSnippet());

              info.addView(title);
              info.addView(snippet);

              return info;
            }
        });


        mMap.addMarker(marker).showInfoWindow();

        //UI Controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setPadding(0, 0, 0, 200);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        //Camera position
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)      // Sets the center of the mMap to location user
                .zoom(19)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(60)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

    private void showToast(String message) {
        (Toast.makeText(this, message, Toast.LENGTH_LONG)).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "LOCATION CHANGED");
        //Get address from location
        Address address = LocationHelper.locToAddress(this, location);
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

