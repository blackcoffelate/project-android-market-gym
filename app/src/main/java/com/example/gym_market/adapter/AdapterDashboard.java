package com.example.gym_market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym_market.R;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.server.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterDashboard extends RecyclerView.Adapter<AdapterDashboard.ViewHolder> {

    private Context context;
    private List<ModelStore> storeList;
    private final int limit = 4;

    public AdapterDashboard(Context context, List<ModelStore> storeList) {
        this.context = context;
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public AdapterDashboard.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_default, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelStore store = storeList.get(position);

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

    }

    @Override
    public int getItemCount() {
        if (storeList.size() > limit){
            return limit;
        }else {
            return storeList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaBarang, deskripsiBarang, hargaBarang, stokBarang;
        ImageView fotoBarang;
        CardView cardBarang;
        String textDeskripsi;

        public ViewHolder(View itemView) {
            super(itemView);

            cardBarang = itemView.findViewById(R.id.card_barang);
            fotoBarang = itemView.findViewById(R.id.foto_barang);
            namaBarang = itemView.findViewById(R.id.nama_barang);
            deskripsiBarang = itemView.findViewById(R.id.deskripsi_barang);
            stokBarang = itemView.findViewById(R.id.stok_barang);
            hargaBarang = itemView.findViewById(R.id.harga_barang);
        }
    }
}
