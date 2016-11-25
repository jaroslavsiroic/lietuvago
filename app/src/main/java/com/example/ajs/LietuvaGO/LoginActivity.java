package com.example.ajs.LietuvaGO;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomErrorListener;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomResponseListener;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                progressBar.setVisibility(View.VISIBLE);

                CustomResponseListener<JSONObject> listener = new CustomResponseListener<JSONObject>(LoginActivity.this, progressBar) {
                    @Override
                    public void success(JSONObject res) {
                        try {
                            FetchRequest.setTokenStatic(res.getString("token"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("username", username);
                        //todo set nav_menu USERNAME

                        LoginActivity.this.startActivity(intent);
                    }
                };

                CustomErrorListener errorListener = new CustomErrorListener(LoginActivity.this, progressBar);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FetchRequest fetch = new FetchRequest(Request.Method.POST, "auth", jsonObject, listener, errorListener);


                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(fetch);
            }
        });
    }
}
