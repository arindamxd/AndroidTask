package com.arindam.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private FetchLocationInfo fetchLocationInfo;
    private Location mLocation;
    private String address;
    private TextView fullAddress;
    private ProgressBar addressLoader;
    private MenuItem buttonDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fullAddress = (TextView) findViewById(R.id.full_address);
        addressLoader = (ProgressBar) findViewById(R.id.addressLoader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMap);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        gpsTracker = new GPSTracker(getApplicationContext());
        mLocation = gpsTracker.getLocation();
        fetchLocationInfo = new FetchLocationInfo(getApplicationContext());
        address = fetchLocationInfo.getFullAddress(mLocation);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.choose_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLng coordinate = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
                mMap.animateCamera(loc);

                addressLoader.setVisibility(View.INVISIBLE);
                fullAddress.setVisibility(View.VISIBLE);
                fullAddress.setText(address);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                mMap.setMyLocationEnabled(true);
            }
        });

        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {

        buttonDone.setEnabled(true);
        addressLoader.setVisibility(View.VISIBLE);
        fullAddress.setVisibility(View.INVISIBLE);

        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(point));

        mLocation = new Location(LocationManager.GPS_PROVIDER);
        mLocation.setLatitude(point.latitude);
        mLocation.setLongitude(point.longitude);
        address = fetchLocationInfo.getFullAddress(mLocation);

        addressLoader.setVisibility(View.INVISIBLE);
        fullAddress.setVisibility(View.VISIBLE);
        fullAddress.setText(address);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.map_menu, menu);
        buttonDone = menu.getItem(0);
        buttonDone.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_done) {
            Intent returnIntent = getIntent();
            returnIntent.putExtra("latitude", String.valueOf(mLocation.getLatitude()));
            returnIntent.putExtra("longitude", String.valueOf(mLocation.getLongitude()));
            returnIntent.putExtra("address", address);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}