package com.example.ajs.LietuvaGO.CustomFetchers;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomResponseListener<T> implements Response.Listener<T> {

    private Context context;
    private ProgressBar progressBar;

    public CustomResponseListener(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    public void onResponse(T res) {

        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if (res instanceof JSONObject) {
            JSONObject respond = ((JSONObject) res);
            try {
                if (!respond.has("success") || respond.getBoolean("success")) {
                    success(res);
                } else {
                    failure(res);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            success(res);
        }
    }

    public void success(T res) {

    }

    public void failure(T res) {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String message = "";
            if (res instanceof JSONObject) {
                try {
                    message = ((JSONObject) res).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                message = "ERROR while loading resources";
            }
            builder.setMessage(message)
                    .setNegativeButton("Ok", null)
                    .create()
                    .show();
        }
    }
}
