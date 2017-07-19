package com.arindam.androidtask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final int MY_PERMISSION_LOCATION = 1;

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private FetchLocationInfo fetchLocationInfo;
    private Location mLocation;
    private String address;
    private TextView mapAddress;
    private ProgressBar mapLoader;
    private ImageButton myLocation;
    private ImageView mapOverlay;
    private EditText categoryText;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbarHome);
        toolbar.setTitle("Add a place");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        mapAddress = (TextView) findViewById(R.id.map_address);
        categoryText = (EditText) findViewById(R.id.choose_category);
        mapLoader = (ProgressBar) findViewById(R.id.mapLoader);
        myLocation = (ImageButton) findViewById(R.id.myLocation);
        mapOverlay = (ImageView) findViewById(R.id.map_overlay);

        mapOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapActivity.class);
                startActivityForResult(i, 1);
            }
        });

        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CategoryActivity.class);
                startActivityForResult(i, 2);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        } else {
            FetchGPSLocation();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng ll = new LatLng(12.9716, 77.5946);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 10);
        mMap.moveCamera(update);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent i = new Intent(MainActivity.this, MapActivity.class);
                startActivityForResult(i, 1);
            }
        });
    }

    public void FetchGPSLocation() {
        try {
            gpsTracker = new GPSTracker(getApplicationContext());
            mLocation = gpsTracker.getLocation();
            fetchLocationInfo = new FetchLocationInfo(getApplicationContext());
            address = fetchLocationInfo.getFullAddress(mLocation);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    public void fetchLocation(View view) {

        try {
            mapOverlay.setVisibility(View.INVISIBLE);
            myLocation.setVisibility(View.INVISIBLE);
            mapLoader.setVisibility(View.VISIBLE);

            LatLng coordinate = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            CameraUpdate loc = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
            mMap.animateCamera(loc);
            mapAddress.setText(address);

            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            myLocation.setVisibility(View.VISIBLE);
            mapLoader.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);

            mapOverlay.setVisibility(View.VISIBLE);
            myLocation.setVisibility(View.VISIBLE);
            mapLoader.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK) {
                mapOverlay.setVisibility(View.INVISIBLE);

                String latitude = data.getStringExtra("latitude");
                String longitude = data.getStringExtra("longitude");
                String result = data.getStringExtra("address");
                mapAddress.setText(result);

                LatLng coordinate = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                CameraUpdate camLoc = CameraUpdateFactory.newLatLngZoom(coordinate, 17);

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(coordinate));
                mMap.animateCamera(camLoc);
            }
        }

        if (requestCode == 2) {
            if(resultCode == Activity.RESULT_OK) {
                mapOverlay.setVisibility(View.INVISIBLE);

                String result = data.getStringExtra("name");
                categoryText.setText(result);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    FetchGPSLocation();
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_send) {
            Toast.makeText(getApplication(), "Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
