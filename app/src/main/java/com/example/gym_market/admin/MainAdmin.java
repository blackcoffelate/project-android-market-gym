package com.example.gym_market.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.gym_market.R;
import com.example.gym_market.access.Login;
import com.example.gym_market.model.ModelUser;
import com.example.gym_market.utils.App;
import com.example.gym_market.utils.GsonHelper;
import com.example.gym_market.utils.Prefs;
import com.google.android.material.navigation.NavigationView;

public class MainAdmin extends AppCompatActivity {

    private TextView fullnameAdmin, phoneAdmin;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;

    ModelUser modelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_admin);

        drawerLayout = findViewById(R.id.draweradmin);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        loadFragment(new FragmentDashboard());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.dashboard:
                        loadFragment(new FragmentDashboard());
                        break;
                    case R.id.store:
                        loadFragment(new FragmentStore());
                        break;
                    case R.id.profile:
                        loadFragment(new FragmentProfile());
                        break;
                    case R.id.signout:
                        App.getPref().clear();
                        startActivity(new Intent(MainAdmin.this, Login.class));
                        Animatoo.animateSlideUp(MainAdmin.this);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        View headerview = navigationView.getHeaderView(0);

        fullnameAdmin = headerview.findViewById(R.id.fullname_admin);
        phoneAdmin = headerview.findViewById(R.id.phone_admin);

        modelUser = (ModelUser) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUser()
        );

        fullnameAdmin.setText(modelUser.getFullname());
        phoneAdmin.setText(modelUser.getPhone());

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        fragmentTransaction.addToBackStack(null);
    }
}
