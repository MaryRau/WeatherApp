package com.example.weatherapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private final Context context;
    private final LocationManager locationManager;
    private final Geocoder geocoder;
    private static float latitude;
    private static float longitude;

    public LocationHelper(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.geocoder = new Geocoder(context, Locale.getDefault());
    }

    public static float getLat() {
        return latitude;
    }

    public static float getLon() {
        return longitude;
    }

    public void getLocation(OnLocationReceivedListener listener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionDenied();
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            latitude = (float) lastKnownLocation.getLatitude();
            longitude = (float) lastKnownLocation.getLongitude();
            listener.onLocationReceived(latitude, longitude);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                locationManager.removeUpdates(this);
                listener.onLocationReceived(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}

            @Override
            public void onProviderDisabled(String provider) {
                listener.onProviderDisabled();
            }
        });
    }

    public String getCityName(float latitude, float longitude) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                GetWeather.setTempUrl(latitude, longitude);
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Ошибка";
    }

    public interface OnLocationReceivedListener {
        void onLocationReceived(float latitude, float longitude);
        void onPermissionDenied();
        void onProviderDisabled();
    }
}