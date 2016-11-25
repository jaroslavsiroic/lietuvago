package com.example.ajs.LietuvaGO.CustomFetchers;

import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.Map;

public class FetchArrayRequest extends JsonArrayRequest {

    private Map<String, String> params = new ArrayMap<>();

    public FetchArrayRequest(int method, String url, JSONArray jsonRequest, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, FetchRequest.getBaseUrl() + url, jsonRequest, listener, errorListener);

        if (FetchRequest.getToken() != null)
            params.put(FetchRequest.getXtoken(), FetchRequest.getToken());
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return params;
    }
}
