package com.example.ajs.LietuvaGO.CustomFetchers;

import android.util.ArrayMap;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;


public class FetchRequest extends JsonObjectRequest {
    private static final  String BASE_URL = "https://lietuvago-maxleaf.c9users.io/";
    private Map<String, String> params = new ArrayMap<>();
    private static String token;
    private static final String xtoken = "x-access-token";

    public FetchRequest(int method, String path, JSONObject body, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        super(method, BASE_URL+path, body, listener, errorListener);
        if (token != null &&  token != "") {
            params.put(xtoken, token);
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return params;
    }

    public void setToken(String newToken) {
        token = newToken;
        params.put(xtoken, newToken);
    }
    public static void setTokenStatic(String newToken) {
        token = newToken;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getToken() {
        return token;
    }

    public static String getXtoken() {
        return xtoken;
    }
}

