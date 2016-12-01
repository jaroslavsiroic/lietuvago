package com.example.ajs.LietuvaGO;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomErrorListener;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomResponseListener;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchArrayRequest;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private ProgressBar progressBar;

    private Location mBestReading;
    private double mlat;
    private double mlong;
    // Reference to the LocationManager and LocationListener
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long MEASURE_TIME = 1000 * 30;
    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;
    private static final float MIN_DISTANCE = 10.0f;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important
        progressBar = (ProgressBar) v.findViewById(R.id.progressBarMap);
        progressBar.setVisibility(View.VISIBLE);

        if (null == (mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE)))
            Toast.makeText(MapFragment.this.getActivity(), "Cant get location manager", Toast.LENGTH_SHORT).show();

        // Get best last location measurement
        mBestReading = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);

        // Display last reading information
        if (null != mBestReading) {
            mlat = mBestReading.getLatitude();
            mlong = mBestReading.getLongitude();
        }

        mLocationListener = new LocationListener() {

            // Called back when location changes

            public void onLocationChanged(Location location) {


                // Determine whether new location is better than current best
                // estimate

                if (null == mBestReading
                        || location.getAccuracy() < mBestReading.getAccuracy()) {

                    // Update best estimate
                    mBestReading = location;

                    // Update display


                    if (mBestReading.getAccuracy() < MIN_ACCURACY)
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                    mLocationManager.removeUpdates(mLocationListener);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setOnMarkerClickListener(this);
        //mGoogleMap.setMaxZoomPreference(15);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mGoogleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getContext(), R.raw.style_silver));

            if (!success) {
                Log.e("MapsActivityRaw", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivityRaw", "Can't find style.", e);
        }
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlat, mlong), 15));
        } else {
            Toast.makeText(getActivity(), "Negalima parodyti jūsų buvimo vietos.", Toast.LENGTH_SHORT).show();
            // Show rationale and request permission.
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.5394, 25.649), 15));
        }


        CustomResponseListener<JSONArray> listener = new CustomResponseListener<JSONArray>(MapFragment.this.getContext(), progressBar) {
            @Override
            public void success(JSONArray res) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MapFragment.this.getContext());
//                builder.setMessage(res.toString())
//                        .setNegativeButton("Ok", null)
//                        .create()
//                        .show();
                JSONObject json;
                int value;
                for (int i = 0; i < res.length(); i++) {
                    try {
                        json = res.getJSONObject(i);
                        value = R.drawable.building;
                        if (json.getString("style").equals("#icon-1598-A52714")) {
                            value = R.drawable.castle;
                        } else if (json.getString("style").equals("#icon-1706-0097A7")) {
                            value = R.drawable.church;
                        } else if (json.getString("style").equals("#icon-1720-558B2F")) {
                            value = R.drawable.trees;
                        } else if (json.getString("style").equals("#icon-1502-FBC02D")) {
                            value = R.drawable.star;
                        } else if (json.getString("style").equals("#icon-1596-757575")) {
                            value = R.drawable.hiking;
                        } else if (json.getString("style").equals("#icon-1636-01579B")) {
                            value = R.drawable.museum;
                        } else if (json.getString("style").equals("#icon-1621-795548")) {
                            value = R.drawable.tower;
                        } else if (json.getString("style").equals("#icon-1592-9C27B0")) {
                            value = R.drawable.puzzle;
                        } else if (json.getString("style").equals("#icon-1577-F57C00")) {
                            value = R.drawable.food;
                        }
                        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(json.getDouble("latitude"), json.getDouble("longitude")))
                                .icon(BitmapDescriptorFactory.fromResource(value)).title(json.getString("name")).snippet(json.getString("style"))).setTag(json);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        CustomErrorListener errorListener = new CustomErrorListener(MapFragment.this.getContext(), progressBar);

        FetchArrayRequest fetch = new FetchArrayRequest(Request.Method.GET, "api/placemarks", null, listener, errorListener);


        RequestQueue queue = Volley.newRequestQueue(MapFragment.this.getContext());
        queue.add(fetch);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Retrieve the data from the marker.
        JSONObject json = (JSONObject) marker.getTag();
        //marker.showInfoWindow();

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage("Message")
//                .setTitle("title");
//        AlertDialog dialog = builder.create();
//        dialog.show();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
//        builder.setTitle("Title")
//                .setCancelable(true)
//                .setPositiveButton("Go", new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//
//                    }
//                });

//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        View dialoglayout = inflater.inflate(R.layout.custominfowindowview, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setView(dialoglayout);
//        builder.show();
//
//        AlertDialog alert = builder.create();
//        alert.show();


        Intent intent = new Intent(MapFragment.this.getContext(), CustomInfoWindow.class);
        try {
            intent.putExtra("id", json.getString("_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("title", marker.getTitle());
        //Toast.makeText(getActivity(), gpsTracker.getLatitude() + " " + gpsTracker.getLongitude(), Toast.LENGTH_SHORT).show();
        //TODO
        intent.putExtra("latitude", mlat);
        intent.putExtra("longitude", mlong);
        startActivity(intent);


        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (null == mBestReading
                || mBestReading.getAccuracy() > MIN_LAST_READ_ACCURACY
                || mBestReading.getTime() < System.currentTimeMillis()
                - TWO_MIN) {

            // Register for network location updates
            if (null != mLocationManager
                    .getProvider(LocationManager.NETWORK_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, POLLING_FREQ,
                        MIN_DISTANCE, mLocationListener);
            }

            // Register for GPS location updates
            if (null != mLocationManager
                    .getProvider(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, POLLING_FREQ,
                        MIN_DISTANCE, mLocationListener);
            }

            // Schedule a runnable to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                @Override
                public void run() {

                    Log.i(TAG, "location updates cancelled");

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListener);

                }
            }, MEASURE_TIME, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }

    private Location bestLastKnownLocation(float minAccuracy, long maxAge) {

        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestAge = Long.MIN_VALUE;

        List<String> matchingProviders = mLocationManager.getAllProviders();

        for (String provider : matchingProviders) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return bestResult;
            }
            Location location = mLocationManager.getLastKnownLocation(provider);

            if (location != null) {

                float accuracy = location.getAccuracy();
                long time = location.getTime();

                if (accuracy < bestAccuracy) {

                    bestResult = location;
                    bestAccuracy = accuracy;
                    bestAge = time;

                }
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy
                || (System.currentTimeMillis() - bestAge) > maxAge) {
            return null;
        } else {
            return bestResult;
        }
    }
}