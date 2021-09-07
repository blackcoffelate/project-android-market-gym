package com.example.gym_market.customer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.example.gym_market.adapter.AdapterPesanan;
import com.example.gym_market.admin.ProductAdd;
import com.example.gym_market.model.ModelPesanan;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.server.VolleyMultipart;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PesananCustomer extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerViewAllDataPesanan;
    RecyclerView.Adapter recyclerViewAllDataPesananAdapter;
    ModelUser modelUser;
    List<ModelPesanan> modelPesanan;
    private ImageView photoItem;
    private LinearLayout takePhoto, photoResult;
    private Button doSubmit;

    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout swipeRefreshLayoutPesanan;
    private LinearLayout animationView;

    private final int CAMERA_P = 1;
    Bitmap bitmap;
    String mCurrentPhotoPath;

    Toolbar toolbar;
    ProgressDialog progressDialog;
    String fullnameUser, _idPembeli, idtransaksi;
    int status = 1;

    TextView fullnameCustomer, tanggalTransaksi, idTransaksi, totalBelanja;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_customer);

        mRequestQueue = Volley.newRequestQueue(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        takePhoto = findViewById(R.id.take_photo_transfer);
        photoItem = findViewById(R.id.photo_item_tranfer);
        photoResult = findViewById(R.id.photo_result_tranfer);
        doSubmit = findViewById(R.id.transfer);

        fullnameCustomer = findViewById(R.id.fullname_customer);
        tanggalTransaksi = findViewById(R.id.tgl_transaksi);
        idTransaksi = findViewById(R.id.id_transaksi);
        totalBelanja = findViewById(R.id.total_belanja);

        animationView = findViewById(R.id.empty_all_data_pesanan);
        swipeRefreshLayoutPesanan = findViewById(R.id.refresh_all_data_pesanan);

        recyclerViewAllDataPesanan = findViewById(R.id.data_all_data_pesanan);
        recyclerViewAllDataPesanan.setHasFixedSize(false);
        recyclerViewAllDataPesanan.setLayoutManager(new GridLayoutManager(this, 1));
        modelPesanan = new ArrayList<>();
        recyclerViewAllDataPesananAdapter = new AdapterPesanan(this, modelPesanan);

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
        _idPembeli = modelUser.get_id();

        fullnameCustomer.setText(fullnameUser);

        swipeRefreshLayoutPesanan.setOnRefreshListener(this);
        swipeRefreshLayoutPesanan.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayoutPesanan.setRefreshing(true);
                modelPesanan.clear();
                recyclerViewAllDataPesananAdapter.notifyDataSetChanged();
                functionGetAllPesanan();
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionTakePhoto();
            }
        });

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        doSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionTransfer(bitmap);
            }
        });

    }

    private void functionGetAllPesanan() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.get_pesanan + _idPembeli + "/" + status, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        swipeRefreshLayoutPesanan.setRefreshing(false);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String toko = response.getString("result");
                                JSONObject jsonObject = new JSONObject(toko);
                                JSONArray datas = jsonObject.getJSONArray("data");

                                idtransaksi = jsonObject.getString("idTransaksi");
                                String total = jsonObject.getString("total");
                                String created_at = jsonObject.getString("created_at");
                                idTransaksi.setText(idtransaksi);
                                totalBelanja.setText(total);
                                try {
                                    format.setTimeZone(TimeZone.getTimeZone(""));
                                    Date date = format.parse(created_at);
                                    tanggalTransaksi.setText(String.valueOf(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                for (int i = 0; i < datas.length(); i++) {
                                    JSONObject jsonObjects = datas.getJSONObject(i);

                                    ModelPesanan pesanan = new ModelPesanan();
                                    String idbarang = jsonObjects.getString("idBarang");
                                    String namabarang = jsonObjects.getString("namaBarang");
                                    int hargabarang = jsonObjects.getInt("hargaBarang");
                                    String qty = jsonObjects.getString("qty");

                                    pesanan.setIdBarang(idbarang);
                                    pesanan.setNamaBarang(namabarang);
                                    pesanan.setHargaBarang(String.valueOf(hargabarang));
                                    pesanan.setQty(qty);

                                    modelPesanan.add(pesanan);

                                    recyclerViewAllDataPesanan.setAdapter(recyclerViewAllDataPesananAdapter);
                                    recyclerViewAllDataPesananAdapter.notifyDataSetChanged();
                                }

                                animationView.setVisibility(View.GONE);
                                recyclerViewAllDataPesanan.setVisibility(View.VISIBLE);

                            } else {
                                animationView.setVisibility(View.VISIBLE);
                                recyclerViewAllDataPesanan.setVisibility(View.GONE);
                                swipeRefreshLayoutPesanan.setRefreshing(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                swipeRefreshLayoutPesanan.setRefreshing(false);
            }
        });
        mRequestQueue.add(req);
    }

    private void functionTransfer(final Bitmap bitmap){
        progressDialog.setTitle("Mohon tunggu sebentar...");
        VolleyMultipart volleyMultipartReq = new VolleyMultipart(Request.Method.PUT, BaseURL.transfer + idtransaksi, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                mRequestQueue.getCache().clear();
                hideDialog();
                try {
                    JSONObject jsonObject = new JSONObject(new String(response.data));
                    String msg = jsonObject.getString("message");
                    boolean status = jsonObject.getBoolean("status");
                    if (status == true) {
                        startActivity(new Intent(PesananCustomer.this, MainCustomer.class));
                        Animatoo.animateSlideUp(PesananCustomer.this);
                    } else {
                        StyleableToast.makeText(getApplicationContext(), msg, R.style.toastStyleWarning).show();
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
                        StyleableToast.makeText(PesananCustomer.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }){
            @Override
            protected Map<String, DataPart> getByteData() throws AuthFailureError {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("buktiBayar", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String statusTranfer = "2";
                params.put("status", statusTranfer);
                return params;
            }
        };
        volleyMultipartReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        mRequestQueue = Volley.newRequestQueue(PesananCustomer.this);
        mRequestQueue.add(volleyMultipartReq);
    }

    private void functionTakePhoto() {
        addPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(PesananCustomer.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("Tags", "IOException");
            }
            if (photoFile != null) {
                Uri fileuri = FileProvider.getUriForFile(PesananCustomer.this, "com.example.gym_market.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileuri);
                startActivityForResult(cameraIntent, CAMERA_P);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + "sekawan" + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ProductAdd.RESULT_CANCELED) {
            return;
        }

        if (requestCode == CAMERA_P) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(PesananCustomer.this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                photoItem.setImageBitmap(bitmap);
                if (photoItem.getDrawable() != null) {
                    photoItem.requestLayout();
                    photoItem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) photoItem.getLayoutParams();
                    photoResult.setVisibility(View.VISIBLE);
                    takePhoto.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                StyleableToast.makeText(PesananCustomer.this, "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public void addPermission() {
        Dexter.withActivity(PesananCustomer.this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(PesananCustomer.this, "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onRefresh() {
        modelPesanan.clear();
        recyclerViewAllDataPesananAdapter.notifyDataSetChanged();
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
