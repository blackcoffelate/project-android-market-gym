package com.example.gym_market.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.gym_market.adapter.AdapterStore;
import com.example.gym_market.R;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.server.BaseURL;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentStore extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerViewStore;
    RecyclerView.Adapter recyclerViewStoreAdapter;
    List<ModelStore> modelStores;

    private RequestQueue mRequestQueue;

    private FloatingActionButton addProduct;
    private LinearLayout animationView;
    private SwipeRefreshLayout swipeRefreshLayoutStore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        addProduct = v.findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProductAdd.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        animationView = v.findViewById(R.id.empty_store);
        recyclerViewStore = v.findViewById(R.id.data_store);
        swipeRefreshLayoutStore = v.findViewById(R.id.refresh_store);

        recyclerViewStore.setHasFixedSize(true);
        recyclerViewStore.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        modelStores = new ArrayList<>();
        recyclerViewStoreAdapter = new AdapterStore(getActivity(), modelStores);

//        functionCheckStore();

        swipeRefreshLayoutStore.setOnRefreshListener(this);
        swipeRefreshLayoutStore.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutStore.setRefreshing(true);
                modelStores.clear();
                recyclerViewStoreAdapter.notifyDataSetChanged();
                functionCheckStore();
            }
        });

        return v;
    }

    private void functionCheckStore() {
        swipeRefreshLayoutStore.setRefreshing(true);
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
                                if (toko.isEmpty()) {
                                    animationView.setVisibility(View.VISIBLE);
                                    recyclerViewStore.setVisibility(View.GONE);
                                    swipeRefreshLayoutStore.setRefreshing(false);
                                } else {
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

//                                        modelStores.clear();
                                        modelStores.add(dataStore);
                                        recyclerViewStore.setAdapter(recyclerViewStoreAdapter);
                                        recyclerViewStoreAdapter.notifyDataSetChanged();
                                    }
                                    animationView.setVisibility(View.GONE);
                                    recyclerViewStore.setVisibility(View.VISIBLE);
                                }
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
        recyclerViewStoreAdapter.notifyDataSetChanged();
        functionCheckStore();
    }
}
