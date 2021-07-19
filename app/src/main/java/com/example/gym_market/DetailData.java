package com.example.gym_market;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.gym_market.server.BaseURL;
import com.squareup.picasso.Picasso;

public class DetailData extends AppCompatActivity {

    private ImageView fotoBarang;
    private String fotoBarangData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_data);

        fotoBarang = findViewById(R.id.foto_barang);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            fotoBarangData = extra.getString("fotoBarang");

            String dataFotoBarang = fotoBarangData;

            if (dataFotoBarang == null) {
                fotoBarang.setImageResource(R.drawable.img_default_half_afternoon);
            } else {
                Picasso.get().load(BaseURL.baseUrl + dataFotoBarang).into(fotoBarang);
            }

        }

    }
}
