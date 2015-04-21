package com.xavey.android.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by tinmaungaye on 4/21/15.
 */
public class GPSTracker extends Service implements android.location.LocationListener {
    /**
     * ATTRIBUTES
     */
    private static final String TAG = GPSTracker.class.getSimpleName();
    private static boolean showingDialog=false;
    private Context _context;
    private static GPSTracker   _instance;
    // The minimum distance to change Updates in meters
    private static final long   MIN_DISTANCE_CHANGE_FOR_UPDATES = 5;                                // meters
    // The minimum time between updates in milliseconds
    private static final long   MIN_TIME_BW_UPDATES             = 1000 * 30 * 1;                    // minute
    // Declaring a Location Manager
    protected LocationManager _locationManager;
    /**
     * PUBLIC ATTRIBUTES
     */
    boolean _isGPSEnabled = false;
    boolean _isNetworkEnabled = false;
    boolean _canGetLocation = false;
    public Location _location;
    double _latitude;
    double _longitude;

    public GPSTracker() {
        _context = null;
    }

    public static GPSTracker getInstance() {

        if (_instance == null) {
            _instance = new GPSTracker();
        }
        return _instance;
    }

    public void set_context(Context context) {

        this._context = context;
    }

    public Location getLocation(Context context) {

        _context = context;
        try {
            _locationManager = (LocationManager) _context.getSystemService(LOCATION_SERVICE);
            _isGPSEnabled = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            _isNetworkEnabled = false; //_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!_isGPSEnabled && !_isNetworkEnabled) {
                // no network provider is enabled
                this._canGetLocation=false;
            }
            else {
                this._canGetLocation = true;
                if (_isNetworkEnabled) {
                    _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (_locationManager != null) {
                        _location = _locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (_location != null) {
                            _latitude = _location.getLatitude();
                            _longitude = _location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (_isGPSEnabled) {
                    if (_location == null) {
                        _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (_locationManager != null) {
                            _location = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (_location != null) {
                                _latitude = _location.getLatitude();
                                _longitude = _location.getLongitude();
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            // do nothing
            this._canGetLocation=false;
        }
        return _location;
    }

    public void stopUsingGPS() {

        if (_locationManager != null) {
            _locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {

        if (_location != null) {
            _latitude = _location.getLatitude();
        }
        return _latitude;
    }

    public double getLongitude() {

        if (_location != null) {
            _longitude = _location.getLongitude();
        }
        return _longitude;
    }

    public boolean canGetLocation() {

        return this._canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert(String title, String msg) {

        if(!showingDialog) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
            alertDialog.setTitle(title);
            alertDialog.setMessage(msg);
            showingDialog=true;
            // Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showingDialog=false;
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    _context.startActivity(intent);
                }
            });
            // cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    showingDialog=false;
                    dialog.cancel();
                }
            });
            // show
            alertDialog.show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }
}
