package com.example.ajs.LietuvaGO;


import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends JsonObjectRequest {
    private static final  String LOGIN_REQUEST_URL = "https://lietuvago-maxleaf.c9users.io/api/users";
    public Map<String, String> params = new ArrayMap<>();

    public LoginRequest(JSONObject json, Response.Listener<JSONObject> listener){
        super(Request.Method.GET, LOGIN_REQUEST_URL, json, listener, null);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return params;
    }
}

