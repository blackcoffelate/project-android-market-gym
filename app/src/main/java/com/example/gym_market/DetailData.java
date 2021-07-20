package com.example.gym_market;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.admin.ProductAdd;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.server.VolleyMultipart;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailData extends AppCompatActivity {

    private ImageView fotoBarang, photoItem;
    private TextView namaBarang, hargaBarang, deskripsiBarang, stokBarang, editData;
    private String fotoBarangData, namaBarangData, hargaBarangData, deskripsiBarangData, stokBarangData, _idData;
    private CardView formEditData;
    private TextInputEditText namaBarangEdit, hargaBarangEdit, stokBarangEdit, deskripsiBarangEdit;
    private Button doSubmit;
    ProgressDialog progressDialog;
    private LinearLayout takePhoto, photoResult;

    private RequestQueue mRequestQueue;
    private final int CAMERA_P = 1;
    Bitmap bitmap;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_data);

        mRequestQueue = Volley.newRequestQueue(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        fotoBarang = findViewById(R.id.foto_barang);
        namaBarang = findViewById(R.id.nama_barang);
        hargaBarang = findViewById(R.id.harga_barang);
        deskripsiBarang = findViewById(R.id.deskripsi_barang);
        stokBarang = findViewById(R.id.stok_barang);
        editData = findViewById(R.id.edit_data);
        formEditData = findViewById(R.id.form_edit);
        namaBarangEdit = findViewById(R.id.nama_barang_edit);
        namaBarangEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        deskripsiBarangEdit = findViewById(R.id.deskripsi_edit);
        stokBarangEdit = findViewById(R.id.stok_edit);
        hargaBarangEdit = findViewById(R.id.harga_edit);
        doSubmit = findViewById(R.id.do_submit);

        takePhoto = findViewById(R.id.take_photo);
        photoItem = findViewById(R.id.photo_item);
        photoResult = findViewById(R.id.photo_result);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            fotoBarangData = extra.getString("fotoBarang");
            namaBarangData = extra.getString("namaBarang");
            hargaBarangData = extra.getString("hargaBarang");
            deskripsiBarangData = extra.getString("deskripsiBarang");
            stokBarangData = extra.getString("stokBarang");
            _idData = extra.getString("_id");

            namaBarang.setText(namaBarangData);
            hargaBarang.setText(hargaBarangData);
            deskripsiBarang.setText(deskripsiBarangData);
            stokBarang.setText(stokBarangData);

            if (fotoBarangData == null) {
                fotoBarang.setImageResource(R.drawable.img_default_half_afternoon);
            } else {
                Picasso.get().load(BaseURL.baseUrl + fotoBarangData).into(fotoBarang);
            }

            namaBarangEdit.setText(namaBarangData);
            hargaBarangEdit.setText(hargaBarangData);
            stokBarangEdit.setText(stokBarangData);
            deskripsiBarangEdit.setText(deskripsiBarangData);
        }

        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formEditData.setVisibility(View.VISIBLE);
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                functionTakePhoto();
            }
        });

        doSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataNamaBarang = namaBarangEdit.getText().toString();
                String dataHargaBarang = hargaBarangEdit.getText().toString();
                String dataStokBarang = stokBarangEdit.getText().toString();
                String dataDeskripsiBarang = deskripsiBarangEdit.getText().toString();

                if (dataNamaBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Nama barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataHargaBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Harga barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataStokBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Stok barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataDeskripsiBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Deskripsi barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else {
                    functionUpdateItemStore(bitmap);
                }
            }
        });
    }

    private void functionUpdateItemStore(final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.PUT, BaseURL.update_store_by_id + _idData,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        System.out.println("data barang" + bitmap);
                        mRequestQueue.getCache().clear();
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("message");
                            boolean status = jsonObject.getBoolean("status");
                            if (status == true) {
                                StyleableToast.makeText(getApplicationContext(), strMsg, R.style.toastStyleSuccess).show();
                                finish();
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
                        StyleableToast.makeText(DetailData.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("namaBarang", namaBarangEdit.getText().toString());
                params.put("hargaBarang", hargaBarangEdit.getText().toString());
                params.put("stokBarang", stokBarangEdit.getText().toString());
                params.put("deskripsiBarang", deskripsiBarangEdit.getText().toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("fotoBarang", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue = Volley.newRequestQueue(DetailData.this);
        mRequestQueue.add(volleyMultipartRequest);
    }

    private void functionTakePhoto() {
        addPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(DetailData.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("Tags", "IOException");
            }
            if (photoFile != null) {
                Uri fileuri = FileProvider.getUriForFile(DetailData.this, "com.example.gym_market.fileprovider", photoFile);
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
                bitmap = MediaStore.Images.Media.getBitmap(DetailData.this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
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
                StyleableToast.makeText(DetailData.this, "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public void addPermission() {
        Dexter.withActivity(DetailData.this)
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
                        Toast.makeText(DetailData.this, "Some Error! ", Toast.LENGTH_SHORT).show();
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
