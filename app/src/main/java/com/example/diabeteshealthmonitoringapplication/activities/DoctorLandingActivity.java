package com.example.diabeteshealthmonitoringapplication.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.ViewPagerAdapter;
import com.example.diabeteshealthmonitoringapplication.fragments.ChatFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.DoctorReadingFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.PatientsFragment;
import com.example.diabeteshealthmonitoringapplication.models.FragmentComponent;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorLandingActivity extends AppCompatActivity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_landing);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        List<FragmentComponent> fragmentComponents = new ArrayList<>();
        ViewPager viewPager = findViewById(R.id.frag_view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentComponents.add(new FragmentComponent(new ChatFragment(), "Chats"));
        fragmentComponents.add(new FragmentComponent(new DoctorReadingFragment(), "Readings"));
        fragmentComponents.add(new FragmentComponent(new PatientsFragment(), "Patients"));
        adapter.addFragment(fragmentComponents);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                } else if (position == 1) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Readings");
                } else if (position == 2) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Patients");
                } else {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                } else if (position == 1) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Readings");
                } else if (position == 2) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Patients");
                } else {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getId() == R.id.chats_doctor) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                    tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.readings_doctor) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Readings");
                    tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.patients_doctor) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Patients");
                    tabLayout.selectTab(tab);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getId() == R.id.chats_doctor) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                    tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.readings_doctor) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Readings");
                    tabLayout.selectTab(tab);
                } else if (tab.getId() == R.id.patients_doctor) {
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Patients");
                    tabLayout.selectTab(tab);
                }
            }
        });
    }
}