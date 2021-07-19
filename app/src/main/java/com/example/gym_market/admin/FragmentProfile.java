package com.example.gym_market.admin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gym_market.R;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;

public class FragmentProfile extends Fragment {

    ModelUser modelUser;

    private TextView fullnameAdmin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        modelUser = (ModelUser) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUser()
        );

        fullnameAdmin = v.findViewById(R.id.fullname_admin);
        fullnameAdmin.setText(modelUser.getFullname());

        return v;
    }
}
