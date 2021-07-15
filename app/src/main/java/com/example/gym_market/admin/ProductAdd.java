package com.example.gym_market.admin;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
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

public class ProductAdd extends AppCompatActivity {

    private ImageView backArrow, photoItem;
    private LinearLayout takePhoto, photoResult;
    private Button doSubmit;
    private ProgressDialog progressDialog;

    private TextInputEditText namaBarang, hargaBarang, stokBarang, deskripsiBarang;

    private RequestQueue mRequestQueue;
    private final int CAMERA_P = 1;
    Bitmap bitmap;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);

        if (Build.VERSION.SDK_INT >= 23){
            requestPermissions(new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

        mRequestQueue = Volley.newRequestQueue(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        
        backArrow = findViewById(R.id.back_arrow);
        takePhoto = findViewById(R.id.take_photo);
        photoItem = findViewById(R.id.photo_item);
        photoResult = findViewById(R.id.photo_result);
        doSubmit = findViewById(R.id.do_submit);

        namaBarang = findViewById(R.id.nama_barang);
        namaBarang.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        hargaBarang = findViewById(R.id.harga);
        stokBarang = findViewById(R.id.stok);
        deskripsiBarang = findViewById(R.id.deskripsi);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                String dataNamaBarang = namaBarang.getText().toString();
                String dataHargaBarang = hargaBarang.getText().toString();
                String dataStokBarang = stokBarang.getText().toString();
                String dataDeskripsiBarang = deskripsiBarang.getText().toString();

                if (dataNamaBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Nama barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataHargaBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Harga barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataStokBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Stok barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else if (dataDeskripsiBarang.isEmpty()){
                    StyleableToast.makeText(getApplicationContext(), "Deskripsi barang tidak boleh kosong ...", R.style.toastStyleWarning).show();
                }else {
                    functionAddItemStore(dataNamaBarang, dataHargaBarang, dataStokBarang, dataDeskripsiBarang, bitmap);
                }
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

    }

    private void functionAddItemStore(final String dataNamaBarang, final String dataHargaBarang, final String dataStokBarang, final String dataDeskripsiBarang, final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
//        Log.d("data", String.valueOf(bitmap));
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.POST, BaseURL.add_store,
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
                        StyleableToast.makeText(ProductAdd.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("namaBarang", dataNamaBarang);
                params.put("hargaBarang", dataHargaBarang);
                params.put("stokBarang", dataStokBarang);
                params.put("deskripsiBarang", dataDeskripsiBarang);
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
        mRequestQueue = Volley.newRequestQueue(ProductAdd.this);
        mRequestQueue.add(volleyMultipartRequest);
    }

    private void functionTakePhoto() {
        addPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(ProductAdd.this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("Tags", "IOException");
            }
            if (photoFile != null) {
                Uri fileuri = FileProvider.getUriForFile(ProductAdd.this, "com.example.gym_market.fileprovider", photoFile);
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
                bitmap = MediaStore.Images.Media.getBitmap(ProductAdd.this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
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
                StyleableToast.makeText(ProductAdd.this, "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public void addPermission() {
        Dexter.withActivity(ProductAdd.this)
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
                        Toast.makeText(ProductAdd.this, "Some Error! ", Toast.LENGTH_SHORT).show();
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
