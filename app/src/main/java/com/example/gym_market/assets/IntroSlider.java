package com.example.gym_market.assets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.gym_market.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class IntroSlider extends AppCompatActivity {

    private ViewPager screenPager;
    IntroSliderViewAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (restorePrefData()) {
            startActivity(new Intent(IntroSlider.this, SplashScreen.class));
            Animatoo.animateSlideDown(IntroSlider.this);
        }

        setContentView(R.layout.activity_intro_slider);

        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);
        screenPager =findViewById(R.id.screen_viewpager);

        final List<ViewIntroSlider> mList = new ArrayList<>();
        mList.add(new ViewIntroSlider("Selamat Datang.","Layanan aplikasi toko sekawan, membantu anda dalam menemukan kebutuhan olahraga melalui handphone.",R.drawable.gym));
        mList.add(new ViewIntroSlider("Belanja Olahraga","Tersedia beragam peralatan dan kebutuhan olahraga.",R.drawable.ic_slider_a));
        mList.add(new ViewIntroSlider("Akses Online","Sekarang belanja lebih mudah melalui layanan online.",R.drawable.ic_slider_b));
        mList.add(new ViewIntroSlider("Daftar Sekarang","Masuk dan daftarkan diri anda untuk menikmati layanan belanja peralatan olahraga secara online.",R.drawable.ic_slider_c));
        mList.add(new ViewIntroSlider("Kapan dan Dimana Saja","Lakukan pembelian dimanapun anda berada, bugarkan badan anda sekarang.",R.drawable.ic_slider_d));

        introViewPagerAdapter = new IntroSliderViewAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }
                if (position == mList.size()-1) {
                    // TODO : show the GETSTARTED Button and hide the indicator and the next button
                    loaddLastScreen();
                }
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroSlider.this, SplashScreen.class));
                Animatoo.animateSlideDown(IntroSlider.this);
                savePrefsData();
            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });
    }

    private void loaddLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnGetStarted.setAnimation(btnAnim);
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.commit();
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;
    }
}
