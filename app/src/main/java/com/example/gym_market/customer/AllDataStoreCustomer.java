package com.example.gym_market.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.adapter.FilterAdapterStoreCustomer;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.server.BaseURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AllDataStoreCustomer extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerViewAllDataStore;
    List<ModelStore> modelStores;
    FilterAdapterStoreCustomer recyclerViewAllDataStoreAdapterFilter;

    private RequestQueue mRequestQueue;

    private LinearLayout animationView;
    private ImageView backArrow;
    private SwipeRefreshLayout swipeRefreshLayoutStore;
    EditText searchView;
    CharSequence search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_data_store_customer);

        mRequestQueue = Volley.newRequestQueue(this);

        animationView = findViewById(R.id.empty_all_data_customer);
        backArrow = findViewById(R.id.back_arrow);
        recyclerViewAllDataStore = findViewById(R.id.data_all_data_customer);
        swipeRefreshLayoutStore = findViewById(R.id.refresh_all_data_customer);
        searchView = findViewById(R.id.search_item);

        recyclerViewAllDataStore.setHasFixedSize(true);
        recyclerViewAllDataStore.setLayoutManager(new GridLayoutManager(this, 2));
        modelStores = new ArrayList<>();
        recyclerViewAllDataStoreAdapterFilter = new FilterAdapterStoreCustomer(this, modelStores);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayoutStore.setOnRefreshListener(this);
        swipeRefreshLayoutStore.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutStore.setRefreshing(true);
                modelStores.clear();
                recyclerViewAllDataStoreAdapterFilter.notifyDataSetChanged();
                functionCheckStore();
            }
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                recyclerViewAllDataStoreAdapterFilter.getFilter().filter(charSequence);
                search = charSequence;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void functionCheckStore() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.get_store, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        swipeRefreshLayoutStore.setRefreshing(false);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String toko = response.getString("result");
                                JSONArray jsonArray = new JSONArray(toko);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelStore dataStore = new ModelStore();
                                    final String _id = jsonObject.getString("_id");
                                    final String namabarang = jsonObject.getString("namaBarang");
                                    final String deskripsibarang = jsonObject.getString("deskripsiBarang");
                                    final String hargabarang = jsonObject.getString("hargaBarang");
                                    final String stokbarang = jsonObject.getString("stokBarang");
                                    final String fotobarang = jsonObject.getString("fotoBarang");
                                    dataStore.setNamaBarang(namabarang);
                                    dataStore.setHargaBrang(hargabarang);
                                    dataStore.setStokBarang(stokbarang);
                                    dataStore.setDeskripsiBarang(deskripsibarang);
                                    dataStore.setFotoBarang(fotobarang);
                                    dataStore.set_id(_id);
                                    modelStores.add(dataStore);
                                    recyclerViewAllDataStore.setAdapter(recyclerViewAllDataStoreAdapterFilter);
                                    recyclerViewAllDataStoreAdapterFilter.notifyDataSetChanged();
                                }
                                animationView.setVisibility(View.GONE);
                                recyclerViewAllDataStore.setVisibility(View.VISIBLE);

                            } else {
                                animationView.setVisibility(View.VISIBLE);
                                recyclerViewAllDataStore.setVisibility(View.GONE);
                                swipeRefreshLayoutStore.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                swipeRefreshLayoutStore.setRefreshing(false);
            }
        });
        mRequestQueue.add(req);
    }

    @Override
    public void onRefresh() {
        modelStores.clear();
        recyclerViewAllDataStoreAdapterFilter.notifyDataSetChanged();
        functionCheckStore();
    }

}
