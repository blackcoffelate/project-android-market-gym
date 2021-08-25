package com.example.gym_market.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.customer.DetailBarangAll;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.server.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterStoreCustomer extends RecyclerView.Adapter<AdapterStoreCustomer.ViewHolder> {

    Context context;
    List<ModelStore> storeList;
    RequestQueue mRequestQueue;

    public AdapterStoreCustomer(Context context, List<ModelStore> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public AdapterStoreCustomer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_dashboard_customer, null);
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

        holder.cardBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, DetailBarangAll.class).putExtra("_id", store.get_id()).putExtra("namaBarang", store.getNamaBarang()).putExtra("hargaBarang", store.getHargaBrang()).putExtra("stokBarang", store.getStokBarang()).putExtra("deskripsiBarang", store.getDeskripsiBarang()).putExtra("fotoBarang", store.getFotoBarang()));
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
}
