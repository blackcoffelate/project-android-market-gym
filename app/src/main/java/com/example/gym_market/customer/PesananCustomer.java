package com.example.gym_market.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.gym_market.R;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;

public class PesananCustomer extends AppCompatActivity {

    RecyclerView recyclerViewAllDataPesanan;
    RecyclerView.Adapter recyclerViewAllDataPesananAdapter;
    ModelUser modelUser;

    private RequestQueue mRequestQueue;

    Toolbar toolbar;
    ProgressDialog progressDialog, progressSuccess;
    String fullnameUser;

    TextView fullnameCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_customer);

        fullnameCustomer = findViewById(R.id.fullname_customer);

        toolbar = (Toolbar) findViewById(R.id.toolbar_pembeli);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_keyboard_arrow_left_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("DAFTAR PESANAN");

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        fullnameUser = modelUser.getFullname();

        fullnameCustomer.setText(fullnameUser);

    }
}
