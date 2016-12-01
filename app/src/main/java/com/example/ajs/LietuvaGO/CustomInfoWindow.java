package com.example.ajs.LietuvaGO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.StringLoader;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomErrorListener;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomResponseListener;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchArrayRequest;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomInfoWindow extends Activity{



    static final double _eQuatorialEarthRadius = 6378.1370D;
    static final double _d2r = (Math.PI / 180D);

    public static int HaversineInM(double lat1, double long1, double lat2, double long2) {
        return (int) (1000D * HaversineInKM(lat1, long1, lat2, long2));
    }

    public static double HaversineInKM(double lat1, double long1, double lat2, double long2) {
        double dlong = (long2 - long1) * _d2r;
        double dlat = (lat2 - lat1) * _d2r;
        double a = Math.pow(Math.sin(dlat / 2D), 2D) + Math.cos(lat1 * _d2r) * Math.cos(lat2 * _d2r)
                * Math.pow(Math.sin(dlong / 2D), 2D);
        double c = 2D * Math.atan2(Math.sqrt(a), Math.sqrt(1D - a));
        double d = _eQuatorialEarthRadius * c;

        return d;
    }




    public final double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        /*
        var R = 6371e3; // metres
        var φ1 = lat1.toRadians();
        var φ2 = lat2.toRadians();
        var Δφ = (lat2-lat1).toRadians();
        var Δλ = (lon2-lon1).toRadians();

        var a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ/2) * Math.sin(Δλ/2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        var d = R * c;
         */
        double R = 6378.1370D; //meters
        double fi1 = toRad(lat1);
        double fi2 = toRad(lat2);
        double deltafi = toRad(lat2 - lat1);
        double deltalambda = toRad(lon2 = lon1);

        double a = Math.sin(deltafi/2) * Math.sin(deltafi/2) +
                Math.cos(fi1) * Math.cos(fi2) * Math.sin(deltalambda/2) * Math.sin(deltalambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R * c;
    }
    public double toRad (double degrees) {
        return (degrees * Math.PI) / 180;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custominfowindowview);
        final TextView titleUi = ((TextView) findViewById(R.id.titleView));
        final TextView descriptionUi = ((TextView) findViewById(R.id.descriptionInfo));
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarInfoWin);
        final Button buttonVisit = (Button) findViewById(R.id.markAsVisited);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        progressBar.setVisibility(View.VISIBLE);


        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        final Intent intent = getIntent();
        final String title = intent.getStringExtra("title");
        final String id = intent.getStringExtra("id");

        final Double myLat = intent.getDoubleExtra("latitude", 1);
        final Double myLong = intent.getDoubleExtra("longitude", 1);

        CustomResponseListener<JSONObject> listener = new CustomResponseListener<JSONObject>(CustomInfoWindow.this, progressBar) {
            @Override
            public void success(JSONObject res) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MapFragment.this.getContext());
//                builder.setMessage(res.toString())
//                        .setNegativeButton("Ok", null)
//                        .create()
//                        .show();
                if (title != null) {
                    titleUi.setText(title);
                }
                try {
                    Glide.with(CustomInfoWindow.this).load(res.getString("image")).into(imageView);
                    imageView.setVisibility(View.VISIBLE);
                    double pointLat = res.getDouble("latitude");
                    double pointLong = res.getDouble("longitude");
                    double dist = HaversineInM(pointLat, pointLong, myLat, myLong);//calcDistance(pointLat, pointLong, myLat, myLong);
                    //Toast.makeText(getApplicationContext(), String.valueOf(dist), Toast.LENGTH_SHORT).show();
                    Log.e("mano kordinates", myLat+" "+myLong);
                    Log.e("mano kordinates", pointLat+" "+pointLong);
                    Log.e("dis", dist+"");
                    if (dist <= 600) {
                        buttonVisit.setVisibility(View.VISIBLE);
                    }
                    String des = res.getString("description");
                    //des = des.substring(0, )
                    des = des.replaceFirst("^(.*?)>", "");

                    descriptionUi.setText(Html.fromHtml(des));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        CustomErrorListener errorListener = new CustomErrorListener(CustomInfoWindow.this, progressBar);

        FetchRequest fetch = new FetchRequest(Request.Method.GET, "api/placemarks?id="+id, null, listener, errorListener);


        RequestQueue queue = Volley.newRequestQueue(CustomInfoWindow.this);
        queue.add(fetch);

        buttonVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                buttonVisit.setVisibility(View.INVISIBLE);

                CustomResponseListener<JSONObject> listener = new CustomResponseListener<JSONObject>(CustomInfoWindow.this, progressBar) {
                    @Override
                    public void success(JSONObject res) {
                        try {
                            Toast.makeText(CustomInfoWindow.this, "success: "+res.getString("success"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                CustomErrorListener errorListener = new CustomErrorListener(CustomInfoWindow.this, progressBar);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("placemarkId", id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FetchRequest fetch = new FetchRequest(Request.Method.PUT, "api/users/addplacemark", jsonObject, listener, errorListener);


                RequestQueue queue = Volley.newRequestQueue(CustomInfoWindow.this);
                queue.add(fetch);
            }
        });


    }
}
