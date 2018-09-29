package com.example.haruka.rescue_aid.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.haruka.rescue_aid.utils.MedicalCertification;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.haruka.rescue_aid.activities.InterviewActivity.InputStreamToString;

public class LocationActivity extends ReadAloudTestActivity implements LocationListener {

    private LocationManager mLocationManager;
    private MedicalCertification medicalCertification_;

    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 0);
            return;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    protected void onPause() {
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        super.onPause();
    }

    private void getAddress(final Location location){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://qa-server.herokuapp.com/address/" + location.getLatitude() + ":" + location.getLongitude());
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    String str = InputStreamToString(con.getInputStream());
                    Log.d("HTTP", str);

                } catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();
    }


    @Override
    public void onLocationChanged(final Location location) {
        Log.d("Location changed", "location");
        try {
            medicalCertification.updateLocation(location, this);
        } catch (NullPointerException ne){
            ne.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("Longitude", String.valueOf(location.getLongitude()));
                    Log.d("Latitude", String.valueOf(location.getLatitude()));
                    medicalCertification_.updateLocation(location, LocationActivity.this);
                    URL url = new URL("https://qa-server.herokuapp.com/address/" + location.getLatitude() + ":" + location.getLongitude());
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    String address = InputStreamToString(con.getInputStream());
                    Log.d("HTTP", address);
                    medicalCertification_.setAddressString(address);
                } catch(Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.v("Status", "AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.v("Status", "OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.v("Status", "TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
