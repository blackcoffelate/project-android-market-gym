package com.example.gym_market.customer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.adapter.AdapterKeranjangCustomer;
import com.example.gym_market.model.ModelKeranjang;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;
import com.google.gson.Gson;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FragmentKeranjangCustomer extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView recyclerViewAllDataKeranjang;
    RecyclerView.Adapter recyclerViewAllDataKeranjangAdapter;
    List<ModelKeranjang> modelKeranjang;

    private RequestQueue mRequestQueue;
    ModelUser modelUser;
    ProgressDialog progressDialog, progressSuccess;

    String _idPembeli;

    private LinearLayout animationView;
    private SwipeRefreshLayout swipeRefreshLayoutStore;
    TextView value;
    String showTotal, idPembeliKirim, randDataKirim, totalKirim;
    int randDataShow;
    Button checkoutItem;
    private int time_milis = 1500;
    private FragmentActivity myContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_keranjang_customer, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        progressSuccess = new ProgressDialog(getActivity());
        progressSuccess.setCancelable(false);

        animationView = v.findViewById(R.id.empty_all_data_keranjang);
        recyclerViewAllDataKeranjang = v.findViewById(R.id.data_all_keranjang);
        swipeRefreshLayoutStore = v.findViewById(R.id.refresh_data_keranjang);
        value = v.findViewById(R.id.val);
        checkoutItem = v.findViewById(R.id.checkout_item);

        recyclerViewAllDataKeranjang.setHasFixedSize(false);
        recyclerViewAllDataKeranjang.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        modelKeranjang = new ArrayList<>();
        recyclerViewAllDataKeranjangAdapter = new AdapterKeranjangCustomer(getActivity(), modelKeranjang);

        swipeRefreshLayoutStore.setOnRefreshListener(this);
        swipeRefreshLayoutStore.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutStore.setRefreshing(true);
                modelKeranjang.clear();
                recyclerViewAllDataKeranjangAdapter.notifyDataSetChanged();
                functionCheckKeranjang();
            }
        });

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        _idPembeli = modelUser.get_id();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("custom-message"));

        checkoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataBelanja();
            }
        });

        return v;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showTotal = intent.getStringExtra("total");
            value.setText(showTotal);
        }
    };

    private void sendDataBelanja() {
        Random rand = new Random();
        int randData = 99999999;
        randDataShow = rand.nextInt(randData);
        randDataKirim = "TRX-" + String.valueOf(randDataShow);
        totalKirim = showTotal;
        idPembeliKirim = _idPembeli;

        HashMap<String, String> params = new HashMap<>();
        params.put("idPembeli", idPembeliKirim);
        params.put("idTransaksi", randDataKirim);
        params.put("total", totalKirim);
        params.put("data", new Gson().toJson(modelKeranjang));
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,BaseURL.checkout , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("message");
                            boolean status= response.getBoolean("status");
                            if(status == true){
                                showDialogSuccess();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        gotoPesanan();
                                        hideDialogSuccess();
                                    }
                                }, time_milis);
                            }else {
                                StyleableToast.makeText(getActivity().getApplicationContext(), "FAILED", R.style.toastStyleWarning).show();
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

    private void gotoPesanan() {
        startActivity(new Intent(getActivity().getApplicationContext(), PesananCustomer.class));
    }

    private void functionCheckKeranjang() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.get_cart + _idPembeli, null,
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
                                    final ModelKeranjang dataStore = new ModelKeranjang();
                                    final String idKeranjang = jsonObject.getString("_id");
                                    final String _id = jsonObject.getString("idBarang");
                                    final String namabarang = jsonObject.getString("namaBarang");
                                    final String deskripsibarang = jsonObject.getString("deskripsiBarang");
                                    final String hargabarang = jsonObject.getString("hargaBarang");
                                    final String qty = jsonObject.getString("qty");
                                    final String stokbarang = jsonObject.getString("stokBarang");
                                    dataStore.setNamaBarang(namabarang);
                                    dataStore.setHargaBrang(hargabarang);
                                    dataStore.setQty(qty);
                                    dataStore.setDeskripsiBarang(deskripsibarang);
                                    dataStore.setIdBarang(_id);
                                    dataStore.setIdKeranjang(idKeranjang);
                                    dataStore.setStokBarang(stokbarang);
                                    modelKeranjang.add(dataStore);
                                    recyclerViewAllDataKeranjang.setAdapter(recyclerViewAllDataKeranjangAdapter);
                                    recyclerViewAllDataKeranjangAdapter.notifyDataSetChanged();
                                }
                                animationView.setVisibility(View.GONE);
                                recyclerViewAllDataKeranjang.setVisibility(View.VISIBLE);

                            } else {
                                animationView.setVisibility(View.VISIBLE);
                                recyclerViewAllDataKeranjang.setVisibility(View.GONE);
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
        modelKeranjang.clear();
        recyclerViewAllDataKeranjangAdapter.notifyDataSetChanged();
        functionCheckKeranjang();
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

    private void showDialogSuccess() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.success_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialogSuccess() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setContentView(R.layout.success_dialog);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }
}
