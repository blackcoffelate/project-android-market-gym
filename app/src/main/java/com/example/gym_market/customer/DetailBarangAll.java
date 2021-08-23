package com.example.gym_market.customer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.gym_market.R;
import com.example.gym_market.adapter.AdapterDashboardCustomer;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.server.VolleyMultipart;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailBarangAll extends AppCompatActivity {

    RecyclerView recyclerViewAllDataStore;
    RecyclerView.Adapter recyclerViewAllDataStoreAdapter;
    List<ModelStore> modelStores;
    ModelUser modelUser;

    private RequestQueue mRequestQueue;

    Toolbar toolbar;
    ProgressDialog progressDialog, progressSuccess;

    Button addCart;
    ImageView fotoBarangD;
    TextView namaBarangD, hargaBarangD, stokBarangD, deskripsiBarangD;
    String idBarangShow, namaBarangShow, deskripsiBarangShow, hargabarangShow, stokBarangShow, fotoBarangShow, _idBarang, _idPembeli;
    Bitmap bitmap;
    String mCurrentPhotoPath;

    private int time_milis = 1000;
    private final int CameraR_PP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang_all);

        mRequestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        progressSuccess = new ProgressDialog(this);
        progressSuccess.setCancelable(false);

        recyclerViewAllDataStore = findViewById(R.id.data_all_data_customer);

        recyclerViewAllDataStore.setHasFixedSize(true);
        recyclerViewAllDataStore.setLayoutManager(new GridLayoutManager(this, 2));
        modelStores = new ArrayList<>();
        recyclerViewAllDataStoreAdapter = new AdapterDashboardCustomer(this, modelStores);

        namaBarangD = findViewById(R.id.nama_barang);
        hargaBarangD = findViewById(R.id.harga_barang);
        stokBarangD = findViewById(R.id.stok_barang);
        deskripsiBarangD = findViewById(R.id.deskripsi_barang);
        fotoBarangD = findViewById(R.id.foto_barang);
        addCart = findViewById(R.id.add_barang);

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

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            idBarangShow = extra.getString("_id");
            namaBarangShow = extra.getString("namaBarang");
            hargabarangShow = extra.getString("hargaBarang");
            stokBarangShow = extra.getString("stokBarang");
            deskripsiBarangShow = extra.getString("deskripsiBarang");
            fotoBarangShow = extra.getString("fotoBarang");

            namaBarangD.setText(namaBarangShow);
            hargaBarangD.setText(hargabarangShow);
            stokBarangD.setText("STOK - " + stokBarangShow);
            deskripsiBarangD.setText(deskripsiBarangShow);
            String photoTokoData = fotoBarangShow;
            _idBarang = idBarangShow;

            if (photoTokoData == null) {
                fotoBarangD.setImageResource(R.drawable.gym);
            } else {
                Picasso.get().load(BaseURL.baseUrl + photoTokoData).into(fotoBarangD);
            }

            getSupportActionBar().setTitle(namaBarangShow);
        }

        modelUser = (ModelUser) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUser());
        _idPembeli = modelUser.get_id();

        functionCheckStore();
        
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionAddBarang(bitmap);
            }
        });
    }

    private void functionAddBarang(final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.POST, BaseURL.add_cart,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        System.out.println("DATA KITA" + response);
                        mRequestQueue.getCache().clear();
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("message");
                            boolean status = jsonObject.getBoolean("status");
                            if (status == true) {
                                showDialogSuccess();
//                                Handler handler = new Handler();
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        hideDialogSuccess();
//                                    }
//                                }, time_milis);
                            } else {
                                StyleableToast.makeText(getApplicationContext(), strMsg, R.style.toastStyleWarning).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        StyleableToast.makeText(DetailBarangAll.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idPembeli", _idPembeli);
                params.put("idBarang", _idBarang);
                params.put("namaBarang", namaBarangD.getText().toString());
                params.put("deskripsiBarang", deskripsiBarangD.getText().toString());
                params.put("hargaBarang", hargaBarangD.getText().toString());
                params.put("stokBarang", stokBarangD.getText().toString());
                return params;
            }

//            @Override
//            protected Map<String, DataPart> getByteData() {
//                Map<String, DataPart> params = new HashMap<>();
//                long imagename = System.currentTimeMillis();
//                params.put("fotoBarang", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
//                return params;
//            }
        };

//        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        mRequestQueue = Volley.newRequestQueue(DetailBarangAll.this);
//        mRequestQueue.add(volleyMultipartRequest);
        mRequestQueue.add(volleyMultipartRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == CameraR_PP) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                fotoBarangD.setImageBitmap(bitmap);
                if (fotoBarangD.getDrawable() != null) {
                    fotoBarangD.requestLayout();
                    fotoBarangD.setScaleType(ImageView.ScaleType.FIT_XY);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) fotoBarangD.getLayoutParams();
                }
            } catch (IOException e) {
                e.printStackTrace();
                StyleableToast.makeText(this, "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private void functionCheckStore() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.get_store, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
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

//                                    modelStores.clear();
                                    modelStores.add(dataStore);
                                    recyclerViewAllDataStore.setAdapter(recyclerViewAllDataStoreAdapter);
                                    recyclerViewAllDataStoreAdapter.notifyDataSetChanged();
                                }
                                recyclerViewAllDataStore.setVisibility(View.VISIBLE);

                            } else {
                                recyclerViewAllDataStore.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
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
}
