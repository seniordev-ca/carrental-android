package com.drizzle.carrental.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.drizzle.carrental.R;
import com.drizzle.carrental.globals.Constants;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import static com.google.android.libraries.places.widget.AutocompleteActivity.*;

public class AddLocationActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    final int AUTOCOMPLETE_REQUEST_CODE = 1;

    /**
     * UI Control Handlers
     */
    private ImageButton buttonBack;

    private Button buttonAddLocation;

    private GoogleMap map;

    SupportMapFragment mapView;

    private Boolean getLocation = false;
    /**
     * Vars for location address
     */
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private Location currentLocation;
    private LocationCallback locationCallback;

    private EditText editTextSearch;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back);

        buttonAddLocation = findViewById(R.id.button_submit);

        buttonBack.setOnClickListener(this);

        buttonAddLocation.setOnClickListener(this);

        mapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_location);
        mapView.getMapAsync(this);

        editTextSearch = findViewById(R.id.edittext_search);
        editTextSearch.setOnClickListener(this);
    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        getControlHandlersAndLinkActions();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                editTextSearch.setText(place.getName());

            } else if (resultCode == RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_back:
                finish();
                break;

//            case R.id.button_submit:
//                if (getLocation) {
//                    setResult(RESULT_OK);
//                    finish();
//                }
//                break;

            case R.id.button_submit:
                // Set the fields to specify which types of place data to
// return after the user has made a selection.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        getCurrentLocation();



        map.setOnMapClickListener(this);
    }

    private void getCurrentLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);

                map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Marker"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), Constants.DEFAULT_MAP_ZOOM_LEVEL));

                getLocation = true;
                Constants.selectedLocation = currentLocation;

                fusedLocationClient.removeLocationUpdates(locationCallback);

            }

        };
        startLocationUpdates();
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }



    @Override
    public void onMapClick(LatLng point) {

        Location location = new Location("addedLocation");
        location.setLatitude(point.latitude);
        location.setLongitude(point.longitude);

        Constants.selectedLocation = location;
    }

}
