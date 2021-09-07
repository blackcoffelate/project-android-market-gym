package com.example.gym_market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.model.ModelPesanan;

import java.util.List;

public class AdapterPesanan extends RecyclerView.Adapter<AdapterPesanan.ViewHolder> {

    private Context context;
    private List<ModelPesanan> pesananList;
    private RequestQueue mRequestQueue;

    public AdapterPesanan(Context context, List<ModelPesanan> pesananList) {
        this.context = context;
        this.pesananList = pesananList;
    }

    @NonNull
    @Override
    public AdapterPesanan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_pesanan, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPesanan.ViewHolder holder, int position) {
        final ModelPesanan pesanan = pesananList.get(position);
        holder.qty.setText(pesanan.getQty());
        holder.namaBarang.setText(pesanan.getNamaBarang());
        holder.hargaBarang.setText(pesanan.getHargaBarang());
    }

    @Override
    public int getItemCount() {
        return pesananList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaBarang, hargaBarang, qty;
        CardView cardBarang;
        String idBarang;

        public ViewHolder(View itemView) {
            super(itemView);

            cardBarang = itemView.findViewById(R.id.card_barang);
            namaBarang = itemView.findViewById(R.id.nama_barang_pesanan);
            qty = itemView.findViewById(R.id.qty_barang_pesanan);
            hargaBarang = itemView.findViewById(R.id.harga_barang_pesanan);
        }
    }

}
