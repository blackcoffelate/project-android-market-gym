package com.example.gym_market.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.model.ModelDaftarPesanan;
import com.example.gym_market.server.BaseURL;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdapterDaftarPesanan extends RecyclerView.Adapter<AdapterDaftarPesanan.ViewHolder> {

    private Context context;
    private List<ModelDaftarPesanan> daftarPesananList;
    private RequestQueue mRequestQueue;
    AlertDialog.Builder myDialog;
    AlertDialog alertDialog;

    public AdapterDaftarPesanan(Context context, List<ModelDaftarPesanan> daftarPesananList) {
        this.context = context;
        this.daftarPesananList = daftarPesananList;
    }

    @NonNull
    @Override
    public AdapterDaftarPesanan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_pesanan_pending, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterDaftarPesanan.ViewHolder holder, int position) {
        final ModelDaftarPesanan daftarPesanan = daftarPesananList.get(position);
        holder.tanggalPesanan.setText(daftarPesanan.getTanggalTransaksi());
        holder.idTransaksi.setText(daftarPesanan.getIdTransaksi());
        holder.hargaBarang.setText(daftarPesanan.getTotalBelanja());

        int statusPending = Integer.parseInt(daftarPesanan.getStatus());

        Log.d("STATU BARANG", String.valueOf(statusPending));

        holder.id_transaksi = daftarPesanan.getIdTransaksi();

        if (statusPending == 1){
            holder.statusPesanan.setText("Belum di bayar");
        }else if (statusPending == 2){
            holder.statusPesanan.setText("Menunggu konfirmasi");
        }else if(statusPending == 3){
            holder.statusPesanan.setText("Barang di antar");
            holder.informasiPengiriman.setVisibility(View.VISIBLE);
            holder.cardBarang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog = new AlertDialog.Builder(context);
                    myDialog.setTitle("Konfirmasi Pengiriman");
                    myDialog.setMessage("Barang sudah sampai dengan selamat ?");
                    myDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            functionUpdateStatus(holder.id_transaksi);
                        }
                    });
                    myDialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog = myDialog.create();
                    alertDialog.show();
                }
            });
        }else if(statusPending == 4){
            holder.statusPesanan.setText("Pesanan selesai");
            holder.informasiPengiriman.setVisibility(View.VISIBLE);
            holder.informasiPengiriman.setText("Ayo pesan barang lagi!");
        }

    }

    @Override
    public int getItemCount() {
        return daftarPesananList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView idTransaksi, tanggalPesanan, hargaBarang, statusPesanan, informasiPengiriman;
        CardView cardBarang;
        String id_transaksi;

        public ViewHolder(View itemView) {
            super(itemView);

            cardBarang = itemView.findViewById(R.id.card_barang_pending);
            idTransaksi = itemView.findViewById(R.id.id_transaksi);
            hargaBarang = itemView.findViewById(R.id.harga_barang_pending);
            tanggalPesanan = itemView.findViewById(R.id.tanggal_pesanan_pending);
            statusPesanan = itemView.findViewById(R.id.status_barang);
            informasiPengiriman = itemView.findViewById(R.id.informasi);
        }
    }

    private void functionUpdateStatus(String id_transaksi) {
        HashMap<String, String> params = new HashMap<String, String>();
        String statusLast = "4";
        params.put("status", statusLast);

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.transfer + id_transaksi, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("message");
                            boolean status = jsonObject.getBoolean("status");
                            if (status == true) {
                                alertDialog.dismiss();
                                StyleableToast.makeText(context, "Pesanan di konfirmasi!", R.style.toastStyleSuccess).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
        mRequestQueue.add(req);
    }

}
