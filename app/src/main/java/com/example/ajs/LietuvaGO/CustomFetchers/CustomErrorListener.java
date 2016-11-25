package com.example.ajs.LietuvaGO.CustomFetchers;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class CustomErrorListener implements Response.ErrorListener {

    private ProgressBar progressBar;
    private Context context;

    public CustomErrorListener(Context context, ProgressBar progressBar) {
        this.context = context;
        this.progressBar = progressBar;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            String res;
            if (error.networkResponse != null) {
                res = error.networkResponse.statusCode + "";
            } else {
                res = "[failed to get status]";
            }
            builder.setMessage("Connection failed, status: " + res)
                    .setNegativeButton("Ok", null)
                    .create()
                    .show();
        }
    }
}
