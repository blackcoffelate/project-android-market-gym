package com.example.gym_market.customer;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.gym_market.adapter.AdapterDaftarPesanan;
import com.example.gym_market.model.ModelDaftarPesanan;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FragmentDaftarPesanan extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerViewAllDataPesananPending;
    RecyclerView.Adapter recyclerViewAllDataPesananAdapterPending;
    ModelUser modelUser;
    List<ModelDaftarPesanan> modelDaftarPesanan;

    ProgressDialog progressDialog;
    String fullnameUser, _idPembeli;

    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout swipeRefreshLayoutPesananPending;
    private LinearLayout animationView;

    TextView fullnameCustomer;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daftar_pesanan, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        fullnameCustomer = v.findViewById(R.id.fullname_customer_pending);

        animationView = v.findViewById(R.id.empty_all_data_pesanan_pending);
        swipeRefreshLayoutPesananPending = v.findViewById(R.id.refresh_all_data_pesanan_pending);

        recyclerViewAllDataPesananPending = v.findViewById(R.id.data_all_data_pesanan_pending);
        recyclerViewAllDataPesananPending.setHasFixedSize(false);
        recyclerViewAllDataPesananPending.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        modelDaftarPesanan = new ArrayList<>();
        recyclerViewAllDataPesananAdapterPending = new AdapterDaftarPesanan(getActivity(), modelDaftarPesanan);

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        fullnameUser = modelUser.getFullname();
        _idPembeli = modelUser.get_id();

        fullnameCustomer.setText(fullnameUser);

        swipeRefreshLayoutPesananPending.setOnRefreshListener(this);
        swipeRefreshLayoutPesananPending.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutPesananPending.setRefreshing(true);
                modelDaftarPesanan.clear();
                recyclerViewAllDataPesananAdapterPending.notifyDataSetChanged();
                functionGetAllPesanan();
            }
        });

        return v;
    }

    private void functionGetAllPesanan() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.log + _idPembeli, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        swipeRefreshLayoutPesananPending.setRefreshing(false);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String toko = response.getString("result");
                                JSONArray jsonArray = new JSONArray(toko);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelDaftarPesanan daftarPesanan = new ModelDaftarPesanan();
                                    final String idtransaksi = jsonObject.getString("idTransaksi");
                                    final String totalbelanja = jsonObject.getString("total");
                                    final String statuspesanan = jsonObject.getString("status");
                                    final String created_at = jsonObject.getString("created_at");

                                    daftarPesanan.setIdTransaksi(idtransaksi);
                                    daftarPesanan.setTotalBelanja(totalbelanja);
                                    daftarPesanan.setStatus(statuspesanan);

                                    try {
                                        format.setTimeZone(TimeZone.getTimeZone(""));
                                        Date date = format.parse(created_at);
                                        daftarPesanan.setTanggalTransaksi(String.valueOf(date));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    modelDaftarPesanan.add(daftarPesanan);
                                    recyclerViewAllDataPesananPending.setAdapter(recyclerViewAllDataPesananAdapterPending);
                                    recyclerViewAllDataPesananAdapterPending.notifyDataSetChanged();
                                }
                                animationView.setVisibility(View.GONE);
                                recyclerViewAllDataPesananPending.setVisibility(View.VISIBLE);

                            } else {
                                animationView.setVisibility(View.VISIBLE);
                                recyclerViewAllDataPesananPending.setVisibility(View.GONE);
                                swipeRefreshLayoutPesananPending.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                swipeRefreshLayoutPesananPending.setRefreshing(false);
            }
        });
        mRequestQueue.add(req);
    }

    @Override
    public void onRefresh() {
        modelDaftarPesanan.clear();
        recyclerViewAllDataPesananAdapterPending.notifyDataSetChanged();
        functionGetAllPesanan();
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
}
