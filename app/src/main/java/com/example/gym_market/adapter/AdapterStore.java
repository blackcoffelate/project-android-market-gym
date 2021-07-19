package com.example.gym_market.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.DetailData;
import com.example.gym_market.R;
import com.example.gym_market.admin.FragmentStore;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.server.BaseURL;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterStore extends RecyclerView.Adapter<AdapterStore.ViewHolder> {

    private Context context;
    private List<ModelStore> storeList;
    private RequestQueue mRequestQueue;

    public AdapterStore(Context context, List<ModelStore> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public AdapterStore.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_admin, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ModelStore store = storeList.get(position);

        Picasso.get().load(BaseURL.baseUrl + store.getFotoBarang()).resize(500, 400).centerCrop().into(holder.fotoBarang);
        holder.namaBarang.setText(store.getNamaBarang());
        holder.stokBarang.setText(store.getStokBarang());
        holder.hargaBarang.setText(store.getHargaBrang());

        holder.textDeskripsi = store.getDeskripsiBarang();

        if (holder.textDeskripsi.length() > 30) {
            holder.textDeskripsi = holder.textDeskripsi.substring(0, 30) + "...";
            holder.deskripsiBarang.setText(holder.textDeskripsi);
        }else {
            holder.deskripsiBarang.setText(store.getDeskripsiBarang());
        }

        holder.idData = store.get_id();

        holder.editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailData.class).putExtra("fotoBarang", store.getFotoBarang()));
            }
        });

        holder.deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus data");
                builder.setMessage("Apakah anda yakin menghapus data ini ?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       deleteDataBarang(holder.idData);
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaBarang, deskripsiBarang, hargaBarang, stokBarang;
        ImageView fotoBarang;
        CardView cardBarang;
        String textDeskripsi;
        LinearLayout editData, deleteData;
        String idData;

        public ViewHolder(View itemView) {
            super(itemView);

            editData = itemView.findViewById(R.id.edit_data);
            deleteData = itemView.findViewById(R.id.delete_data);

            cardBarang = itemView.findViewById(R.id.card_barang);
            fotoBarang = itemView.findViewById(R.id.foto_barang);
            namaBarang = itemView.findViewById(R.id.nama_barang);
            deskripsiBarang = itemView.findViewById(R.id.deskripsi_barang);
            stokBarang = itemView.findViewById(R.id.stok_barang);
            hargaBarang = itemView.findViewById(R.id.harga_barang);
        }
    }

    private void deleteDataBarang(String idData) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, BaseURL.delete_store_by_id + idData, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                StyleableToast.makeText(context, "Data berhasil dihapus", R.style.toastStyleSuccess).show();
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
}
