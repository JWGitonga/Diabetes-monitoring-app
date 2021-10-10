package com.example.diabeteshealthmonitoringapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DoctorLandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_landing);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");
        List<FragmentComponent> fragmentComponents = new ArrayList<>();
        ViewPager viewPager = findViewById(R.id.frag_view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Chats").setIcon(R.drawable.chat);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Readings").setIcon(R.drawable.reading);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("Patients").setIcon(R.drawable.patients);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentComponents.add(new FragmentComponent(new ChatFragment(), "Chats"));
        fragmentComponents.add(new FragmentComponent(new DoctorReadingFragment(), "Readings"));
        fragmentComponents.add(new FragmentComponent(new PatientsFragment(), "Patients"));
        adapter.addFragments(fragmentComponents);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doctor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit_doc) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Registration.class));
            finish();
        }
        return true;
    }
}