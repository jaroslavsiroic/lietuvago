package com.example.ajs.LietuvaGO;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;



public class RegisterRequest extends StringRequest {

    private static  final  String REGISTER_REQUEST_URL = "https://lietuvago-maxleaf.c9users.io/reg";
    private Map<String, String> param;

    public RegisterRequest(String username, String password, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        param = new HashMap<>();
        param.put("username", username);
        param.put("password", password);
    }

    public Map<String, String> getParam() {
        return param;
    }
}
