package com.example.gym_market.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gym_market.R;
import com.example.gym_market.model.ModelUser;

import java.util.List;

public class AdapterCustomer extends RecyclerView.Adapter<AdapterCustomer.ViewHolder> {

    private Context context;
    private List<ModelUser> customerList;

    public AdapterCustomer(Context context, List<ModelUser> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public AdapterCustomer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_data_customer, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterCustomer.ViewHolder holder, int position) {
        final ModelUser customer = customerList.get(position);

//        Picasso.get().load(BaseURL.baseUrl + store.getFotoBarang()).resize(500, 400).centerCrop().into(holder.fotoBarang);
        holder.fullnameCustomer.setText(customer.getFullname());
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView fullnameCustomer;

        public ViewHolder(View itemView) {
            super(itemView);
            fullnameCustomer = itemView.findViewById(R.id.fullname_customer);
        }
    }
}
