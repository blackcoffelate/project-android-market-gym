package com.example.gym_market.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.gym_market.R;
import com.example.gym_market.admin.MainAdmin;
import com.example.gym_market.customer.MainCustomer;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;
import com.example.gym_market.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    private Button doLogin, doRegist;
    private TextInputEditText dUsername, dPassword;

    private ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;

    boolean backPress = false;
    ModelUser modelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());

        if(Utils.isLoggedIn()){
            int dataRole = Integer.parseInt(modelUser.getRole());
            if (dataRole == 0){
                Intent i = new Intent(this , MainAdmin.class);
                startActivity(i);
                finish();
            }else if(dataRole == 1){
                Intent i = new Intent(this , MainCustomer.class);
                startActivity(i);
                finish();
            }
        }

        doLogin = (Button) findViewById(R.id.do_login);
        doRegist = (Button) findViewById(R.id.do_regist);

        dUsername = (TextInputEditText) findViewById(R.id.username);
        dPassword = (TextInputEditText) findViewById(R.id.password);

        mRequestQueue = Volley.newRequestQueue(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        doLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataUsername = dUsername.getText().toString();
                String dataPassword = dPassword.getText().toString();

                if (dataUsername.isEmpty()){
                    StyleableToast.makeText(Login.this, "Maaf username tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataPassword.isEmpty()){
                    StyleableToast.makeText(Login.this, "Maaf password tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else {
                    functionLogin(dataUsername, dataPassword);
                }
            }
        });

        doRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                Animatoo.animateSlideUp(Login.this);
            }
        });
    }

    private void functionLogin(String dataUsername, String dataPassword) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("username", dataUsername);
        params.put("password", dataPassword);

        progressDialog.setTitle("Mohon tunggu sebentar ...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                StyleableToast.makeText(Login.this, strMsg, R.style.toastStyleSuccess).show();

                                JSONObject user = response.getJSONObject("result");
                                String tRole = user.getString("role");
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(user.toString());

                                if (tRole.equals("0")) {
                                    startActivity(new Intent(Login.this, MainAdmin.class));
                                    Animatoo.animateSlideDown(Login.this);
                                } else {
                                    startActivity(new Intent(Login.this, MainCustomer.class));
                                    Animatoo.animateSlideDown(Login.this);
                                }
                            } else {
                                StyleableToast.makeText(Login.this, strMsg, R.style.toastStyleWarning).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });
        mRequestQueue.add(req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.layout_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setContentView(R.layout.layout_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onBackPressed() {
        if (backPress) {
            super.onBackPressed();
            return;
        }
        this.backPress = true;
        StyleableToast.makeText(this, "Tekan sekali lagi untuk keluar...", R.style.toastStyleDefault).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                backPress = false;
            }
        }, 2000);
    }
}
