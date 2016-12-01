package com.example.ajs.LietuvaGO;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomErrorListener;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomResponseListener;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    private CustomAdapter listAdapter;

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBarList);
        final ListView listUi = (ListView) v.findViewById(R.id.listView);

        final ArrayList<CustomItem> items = new ArrayList<>();

        // Create ArrayAdapter using the planet list.

        // Set the ArrayAdapter as the ListView's adapter.
        CustomResponseListener<JSONArray> listener = new CustomResponseListener<JSONArray>(getActivity(), progressBar) {
            @Override
            public void success(JSONArray res) {
                JSONObject json;
                String des;
                for (int i = 0; i < res.length(); i++) {
                    try {
                        json = res.getJSONObject(i);
                        des = json.getString("description");
                        des = des.replaceFirst("^(.*?)>", "");
                        items.add(new CustomItem(json.getString("name"), json.getString("image"), des));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                listAdapter = new CustomAdapter(items,getActivity());
                listUi.setAdapter( listAdapter );
                listUi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        //CustomItem dataModel= items.get(position);
                        Log.e("List", view.toString());
                    }
                });

            }
        };
        CustomErrorListener errorListener = new CustomErrorListener(getActivity(), progressBar);

        FetchArrayRequest fetch = new FetchArrayRequest(Request.Method.GET, "api/users/myplacemarks", null, listener, errorListener);


        RequestQueue queue = Volley.newRequestQueue(ListFragment.this.getContext());
        queue.add(fetch);

        return v;
    }

}
