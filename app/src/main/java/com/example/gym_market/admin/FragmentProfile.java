package com.example.gym_market.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gym_market.R;
import com.example.gym_market.adapter.AdapterCustomer;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.server.BaseURL;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentProfile extends Fragment {

    RecyclerView recyclerViewCustomer;
    RecyclerView.Adapter recyclerViewCustomerAdapter;
    List<ModelUser> modelCustomer;

    private RequestQueue mRequestQueue;

    ModelUser modelUser;
    private TextView fullnameAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        recyclerViewCustomer = v.findViewById(R.id.customer_all);

        recyclerViewCustomer.setHasFixedSize(true);
        recyclerViewCustomer.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        modelCustomer = new ArrayList<>();
        recyclerViewCustomerAdapter = new AdapterCustomer(getActivity(), modelCustomer);


        modelUser = (ModelUser) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUser()
        );

        fullnameAdmin = v.findViewById(R.id.fullname_admin);
        fullnameAdmin.setText(modelUser.getFullname());

        getDataCustomer();

        return v;
    }

    private void getDataCustomer() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.get_users, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String customer = response.getString("result");
                                JSONArray jsonArray = new JSONArray(customer);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelUser dataCustomer = new ModelUser();
                                    final String _id = jsonObject.getString("_id");
                                    final String fullnameCustomer = jsonObject.getString("fullname");
                                    dataCustomer.setFullname(fullnameCustomer);
                                    dataCustomer.set_id(_id);

                                    modelCustomer.add(dataCustomer);
                                    recyclerViewCustomer.setAdapter(recyclerViewCustomerAdapter);
                                    recyclerViewCustomerAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (
                                JSONException e) {
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
