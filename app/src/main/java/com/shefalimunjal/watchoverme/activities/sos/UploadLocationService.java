package com.shefalimunjal.watchoverme.activities.sos;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.shefalimunjal.watchoverme.R;
import com.shefalimunjal.watchoverme.networking.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class UploadLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static final String LOG_TAG = "ForegroundService";
    private static volatile boolean started = false;
    private static final int PERMISSIONS_REQUEST_CODE = 1001;

    @Nullable private GoogleApiClient mGoogleApiClient;
    @Nullable  private Location mLastLocation;
    private final LocationRequest mLocationRequest = createLocationRequest();
    @Nullable private static ServiceStatusListener listener;
    @Nullable private static LocationListener locationListener;

    public static boolean isStarted() {
        return started;
    }

    private static void setStarted(boolean st) {
        started = st;
        if (listener != null) listener.onServiceStatusChanged(started);
    }

    public static interface ServiceStatusListener {
        void onServiceStatusChanged(boolean connected);
    }



    public static void setServiceStatusListener(ServiceStatusListener statusListener) {
        listener = statusListener;
    }
    public static void setLocationListner(LocationListener listner) {
        locationListener = listner;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setStarted(true);
        tryConnectingGoogleApiClient();
        Log.i(LOG_TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Received Start Foreground Intent ");
        startInForeground();
        return START_NOT_STICKY;
    }

    private void startInForeground() {
        initChannels();

        Intent notificationIntent = new Intent(this, AlertButtonActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification =
                new NotificationCompat.Builder(this, "default")
                        .setContentTitle("Keeping you secured!")
                        .setContentText("We are sharing your location with your emergency contacts. Click to mark yourself safe")
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentIntent(pendingIntent)
                        .build();

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setStarted(false);
        tryDisconnectingGoogleApiClient();
        Log.i(LOG_TAG, "onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }

    private void initChannels() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("default",
                    "Channel name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel description");
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void tryConnectingGoogleApiClient() {
        if (mGoogleApiClient == null && isLocationPermissionGranted()) {
            mGoogleApiClient =
                    new GoogleApiClient.Builder(this)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .addApi(LocationServices.API)
                            .build();
        }

        if (mGoogleApiClient != null) mGoogleApiClient.connect();
    }

    private void tryDisconnectingGoogleApiClient() {
        if (mGoogleApiClient != null) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * GoogleApiClient callbacks
     */
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(TAG,"GoogleClientApi connected");
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d(TAG,
                        "current location lat:"
                                + mLastLocation.getLatitude()
                                + ", long:"
                                + mLastLocation.getLongitude());

                startLocationUpdates();
            }
        } catch (SecurityException e) {
            Log.e(TAG, "failed to get location", e);
        }
    }

    @Override
    public void onConnectionSuspended(int var1) {
        Log.d(TAG,"GoogleClientApi connection suspended");
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(TAG,"Failed to connect to GoogleClientApi");
        mGoogleApiClient = null;
    }
    /**
     * Implementing Location settings
     */
    private LocationRequest createLocationRequest() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return request;
    }

    private void startLocationUpdates() {
        createLocationRequest();
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates
                    (mGoogleApiClient, mLocationRequest,
                            this);
        }
        catch (SecurityException e) {
            Log.e(TAG, "failed to get location update", e);
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null
                && mGoogleApiClient.isConnected()
                && mLocationRequest != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location){
        mLastLocation = location;
        sendLocationToFakeUrl(mLastLocation);
        if(locationListener != null) {
            locationListener.onLocationChanged(mLastLocation);
        }
        Log.i(TAG,"Last location after updating is:" + mLastLocation);

    }

    private void sendLocationToFakeUrl(Location mLastLocation)  {
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude",mLastLocation.getLatitude());
            jsonObject.put("longitude",mLastLocation.getLongitude());

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    "http://192.168.1.101/fakeurl/",
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        public void onResponse(JSONObject response){
                            Log.d(TAG,"response is:" +response.toString());
                            try {
                                Log.d(TAG, "getting the response object");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "error making server request", error);
                        }
                    }
            );

            VolleySingleton
                    .getInstance(getApplicationContext())
                    .getRequestQueue(this.getApplicationContext())
                    .add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}