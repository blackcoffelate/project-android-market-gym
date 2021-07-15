package com.example.gym_market.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import com.example.gym_market.server.BaseURL;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    private Button doRegist, doBack;
    private TextInputEditText dFullname, dUsername, dPassword, dPhone, dAddress;

    private RequestQueue mRequestQueue;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        doRegist = (Button) findViewById(R.id.do_regist);
        doBack = (Button) findViewById(R.id.do_back);

        dFullname = (TextInputEditText) findViewById(R.id.fullname);
        dFullname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dUsername = (TextInputEditText) findViewById(R.id.username);
        dPassword = (TextInputEditText) findViewById(R.id.password);
        dPhone = (TextInputEditText) findViewById(R.id.phone);
        dAddress = (TextInputEditText) findViewById(R.id.address);
        dAddress.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        mRequestQueue = Volley.newRequestQueue(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        doRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataFullname = dFullname.getText().toString();
                String dataUsername = dUsername.getText().toString();
                String dataPassword = dPassword.getText().toString();
                String dataPhone = dPhone.getText().toString();
                String dataAddress = dAddress.getText().toString();

                if (dataFullname.isEmpty()){
                    StyleableToast.makeText(Register.this, "Maaf nama lengkap tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataUsername.isEmpty()){
                    StyleableToast.makeText(Register.this, "Maaf username tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataPassword.isEmpty()){
                    StyleableToast.makeText(Register.this, "Maaf password tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataPhone.isEmpty()){
                    StyleableToast.makeText(Register.this, "Maaf nomor telephone tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataAddress.isEmpty()){
                    StyleableToast.makeText(Register.this, "Maaf alamat tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else {
                    functionRegist(dataFullname, dataUsername, dataPassword, dataPhone, dataAddress);
                }
            }
        });

        doBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
                Animatoo.animateSlideDown(Register.this);
            }
        });

    }

    private void functionRegist(String dataFullname, String dataUsername, String dataPassword, String dataPhone, String dataAddress) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("fullname", dataFullname);
        params.put("username", dataUsername);
        params.put("password", dataPassword);
        params.put("phone", dataPhone);
        params.put("address", dataAddress);
        params.put("role", "1");

        progressDialog.setTitle("Mohon tunggu sebentar ...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.register, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                StyleableToast.makeText(Register.this, strMsg, R.style.toastStyleSuccess).show();
                                startActivity(new Intent(Register.this, Login.class));
                                Animatoo.animateSlideDown(Register.this);
                            } else {
                                StyleableToast.makeText(Register.this, strMsg, R.style.toastStyleWarning).show();
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
        startActivity(new Intent(Register.this, Login.class));
        Animatoo.animateSlideDown(Register.this);
    }
}
