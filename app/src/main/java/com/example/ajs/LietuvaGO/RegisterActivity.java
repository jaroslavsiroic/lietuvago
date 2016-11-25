package com.example.ajs.LietuvaGO;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomErrorListener;
import com.example.ajs.LietuvaGO.CustomFetchers.CustomResponseListener;
import com.example.ajs.LietuvaGO.CustomFetchers.FetchRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBarReg);

        progressBar.setVisibility(View.INVISIBLE);

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                CustomResponseListener<JSONObject> listener = new CustomResponseListener<JSONObject>(RegisterActivity.this, progressBar) {
                    @Override
                    public void success(JSONObject res) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                        builder.setMessage("Registration success! Now Login")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    RegisterActivity.this.startActivity(intent);
                                }
                            })
                            .create()
                            .show();
                    }
                };

                CustomErrorListener errorListener = new CustomErrorListener(RegisterActivity.this, progressBar);
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("username", username);
                    jsonObject.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FetchRequest fetch = new FetchRequest(Request.Method.POST, "reg", jsonObject, listener, errorListener);

                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(fetch);
            }
        });
    }
}
