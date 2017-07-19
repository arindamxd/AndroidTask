package com.arindam.androidtask;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Arindam Karmakar on 19/07/2017.
 */

public class FetchLocationInfo {

    private Context context;
    private String fullAddress;

    public FetchLocationInfo(Context context) {
        this.context = context;
    }

    public String getFullAddress(Location location) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                fullAddress = knownName + ", " + address + ", " + city + ", " + state + ", " + country + " - " + postalCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullAddress;
    }
}
