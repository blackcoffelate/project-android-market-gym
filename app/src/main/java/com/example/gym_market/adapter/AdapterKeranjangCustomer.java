package com.example.gym_market.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.customer.FragmentKeranjangCustomer;
import com.example.gym_market.model.ModelKeranjang;
import com.example.gym_market.server.BaseURL;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdapterKeranjangCustomer extends RecyclerView.Adapter<AdapterKeranjangCustomer.ViewHolder> {

    private Context context;
    private List<ModelKeranjang> keranjangList;
    private RequestQueue mRequestQueue;
    private HashMap<Integer, Boolean> isChecked = new HashMap<>();
    int totalPrice;
    int grandTotalPrice = 0;
    String grandD, namabarang, hargabarang, deskripsibarang, qtybarang, idbarang;

    public AdapterKeranjangCustomer(Context context, List<ModelKeranjang> keranjangList) {
        this.context = context;
        this.keranjangList = keranjangList;
    }

    @NonNull
    @Override
    public AdapterKeranjangCustomer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_keranjang, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ModelKeranjang keranjang = keranjangList.get(position);

        holder.namaBarang.setText(keranjang.getNamaBarang());
        holder.qty.setText(keranjang.getQty());
        holder.hargaBarang.setText(keranjang.getHargaBrang());
        holder.deskripsiBarang.setText(keranjang.getDeskripsiBarang());

        holder.keranjangCheck.setChecked(keranjangList.get(position).isSelected());

        keranjang.setSelected(false);
        if (isChecked.containsKey(position)){
            holder.keranjangCheck.setChecked(isChecked.get(position));
        } else {
            holder.keranjangCheck.setChecked(false);
        }

        holder.keranjangCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                namabarang = keranjangList.get(position).getNamaBarang();
                hargabarang = keranjangList.get(position).getHargaBrang();
                deskripsibarang = keranjangList.get(position).getDeskripsiBarang();
                qtybarang = keranjangList.get(position).getQty();
                idbarang = keranjangList.get(position).getIdBarang();

                int hargaBarang = Integer.parseInt(keranjangList.get(position).getHargaBrang());
                int qty = Integer.parseInt(keranjangList.get(position).getQty());
                totalPrice = hargaBarang * qty;

                boolean isSelected = !keranjangList.get(position).isSelected();
                keranjangList.get(position).setSelected(isSelected);

                if (isSelected){
                    grandTotalPrice += totalPrice;
                    grandD = String.valueOf(grandTotalPrice);
                    keranjang.setSelected(true);
                }else {
                    grandTotalPrice -= totalPrice;
                    grandD = String.valueOf(grandTotalPrice);
                    keranjang.setSelected(false);
                }

                String hargaTotal = grandD;
                Intent intent = new Intent("custom-message");
                intent.putExtra("total", hargaTotal);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

        holder.idBarang = keranjang.getIdBarang();
        holder.deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("INFORMASI");
                builder.setMessage("Apakah anda yakin akan menghapus data ini ?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDataKeranjang(holder.idBarang);
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
        return keranjangList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaBarang, deskripsiBarang, hargaBarang, qty;
        ImageView fotoBarang;
        CardView cardBarang;
        LinearLayout editData;
        CheckBox keranjangCheck;
        String idBarang;
        ImageButton deleteData;

        public ViewHolder(View itemView) {
            super(itemView);

            editData = itemView.findViewById(R.id.edit_data);
            deleteData = itemView.findViewById(R.id.delete_data);

            cardBarang = itemView.findViewById(R.id.card_barang);
            fotoBarang = itemView.findViewById(R.id.foto_barang);
            namaBarang = itemView.findViewById(R.id.nama_barang);
            deskripsiBarang = itemView.findViewById(R.id.deskripsi_barang);
            qty = itemView.findViewById(R.id.qty_barang);
            hargaBarang = itemView.findViewById(R.id.harga_barang);
            keranjangCheck = itemView.findViewById(R.id.checkbox_item);
            deleteData = itemView.findViewById(R.id.delete_item);
        }
    }

    private void deleteDataKeranjang(String idBarang) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, BaseURL.delete_cart + idBarang, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                StyleableToast.makeText(context, "Refresh halaman...", R.style.toastStyleSuccess).show();
                                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.keranjang, new FragmentKeranjangCustomer()).addToBackStack(null).commit();
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
