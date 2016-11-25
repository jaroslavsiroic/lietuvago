package com.example.ajs.LietuvaGO;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomErrorListener;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomResponseListener;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchArrayRequest;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this); //this is important

        progressBar = (ProgressBar) v.findViewById(R.id.progressBarMap);
        progressBar.setVisibility(View.VISIBLE);

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setMaxZoomPreference(15);

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
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(54.540076, 25.651242), 10));

            }
        };
        CustomErrorListener errorListener = new CustomErrorListener(MapFragment.this.getContext(), progressBar);

        FetchArrayRequest fetch = new FetchArrayRequest(Request.Method.GET, "api/placemarks", null, listener, errorListener);


        RequestQueue queue = Volley.newRequestQueue(MapFragment.this.getContext());
        queue.add(fetch);

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        // Retrieve the data from the marker.
        JSONObject json = (JSONObject) marker.getTag();
        marker.showInfoWindow();
        // Check if a click count was set, then display the click count.
//        if (clickCount != null) {
//            clickCount = clickCount + 1;
//            marker.setTag(clickCount);
//            Toast.makeText(this,
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
//        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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


}