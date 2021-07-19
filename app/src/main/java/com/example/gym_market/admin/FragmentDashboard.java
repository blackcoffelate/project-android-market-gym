package com.example.gym_market.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.gym_market.adapter.AdapterDashboard;
import com.example.gym_market.R;
import com.example.gym_market.model.ModelStore;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentDashboard extends Fragment {

    private TextView greetingDay, fullnameAdmin, viewAll;
    private ImageView greetingImg, greetingIcon;
    private LinearLayout animationView;

    ModelUser modelUser;

    RecyclerView recyclerViewDashboard;
    RecyclerView.Adapter recyclerViewDashboardAdapter;
    List<ModelStore> modelDashboard;

    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        greetingDay = v.findViewById(R.id.greeting);
        greetingImg = v.findViewById(R.id.greeting_img);
        greetingIcon = v.findViewById(R.id.greeting_icon);
        fullnameAdmin = v.findViewById(R.id.fullname_admin);
        animationView = v.findViewById(R.id.empty_dashboard);
        viewAll = v.findViewById(R.id.view_all);

        greeting();

        modelUser = (ModelUser) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUser()
        );

        fullnameAdmin.setText(modelUser.getFullname());

        recyclerViewDashboard = v.findViewById(R.id.data_dashboard);
        recyclerViewDashboard.setHasFixedSize(true);
        recyclerViewDashboard.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        modelDashboard = new ArrayList<>();
        recyclerViewDashboardAdapter = new AdapterDashboard(getActivity(), modelDashboard);

        functionCheckStore();

        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AllDataStore.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        return v;
    }

    private void functionCheckStore() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.get_store, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String toko = response.getString("result");
                                JSONArray jsonArray = new JSONArray(toko);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelStore dataStore = new ModelStore();
                                    final String _id = jsonObject.getString("_id");
                                    final String namabarang = jsonObject.getString("namaBarang");
                                    final String deskripsibarang = jsonObject.getString("deskripsiBarang");
                                    final String hargabarang = jsonObject.getString("hargaBarang");
                                    final String stokbarang = jsonObject.getString("stokBarang");
                                    final String fotobarang = jsonObject.getString("fotoBarang");
                                    dataStore.setNamaBarang(namabarang);
                                    dataStore.setHargaBrang(hargabarang);
                                    dataStore.setStokBarang(stokbarang);
                                    dataStore.setDeskripsiBarang(deskripsibarang);
                                    dataStore.setFotoBarang(fotobarang);
                                    dataStore.set_id(_id);
                                    modelDashboard.add(dataStore);
                                    recyclerViewDashboard.setAdapter(recyclerViewDashboardAdapter);
                                }
                                animationView.setVisibility(View.GONE);
                                recyclerViewDashboard.setVisibility(View.VISIBLE);
                            } else {
                                animationView.setVisibility(View.VISIBLE);
                                recyclerViewDashboard.setVisibility(View.GONE);
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

    private void greeting() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.HOUR_OF_DAY);

        if (day >= 0 && day < 12) {
            greetingDay.setText("Selamat Pagi");
            greetingImg.setImageResource(R.drawable.img_default_half_morning);
            greetingIcon.setImageResource(R.drawable.ic_morning_icon);
            fullnameAdmin.setTextColor(getResources().getColor(R.color.blue));
            greetingDay.setTextColor(getResources().getColor(R.color.blue));
            greetingIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (day >= 12 && day < 15) {
            greetingDay.setText("Selamat Siang");
            greetingImg.setImageResource(R.drawable.img_default_half_afternoon);
            greetingIcon.setImageResource(R.drawable.ic_day_icon);
            fullnameAdmin.setTextColor(getResources().getColor(R.color.blue));
            greetingDay.setTextColor(getResources().getColor(R.color.blue));
            greetingIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (day >= 15 && day < 18) {
            greetingDay.setText("Selamat Sore");
            greetingImg.setImageResource(R.drawable.img_default_half_without_sun);
            greetingIcon.setImageResource(R.drawable.ic_afternoon_icon);
            fullnameAdmin.setTextColor(getResources().getColor(R.color.blue));
            greetingDay.setTextColor(getResources().getColor(R.color.blue));
            greetingIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (day >= 18 && day < 24) {
            greetingDay.setText("Selamat Malam");
            greetingImg.setImageResource(R.drawable.img_default_half_night);
            greetingIcon.setImageResource(R.drawable.ic_night_icon);
            fullnameAdmin.setTextColor(getResources().getColor(R.color.white));
            greetingDay.setTextColor(getResources().getColor(R.color.white));
            greetingIcon.setColorFilter(ContextCompat.getColor(getActivity(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }
}
