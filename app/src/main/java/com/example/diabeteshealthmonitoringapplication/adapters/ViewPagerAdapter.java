package com.example.diabeteshealthmonitoringapplication.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.diabeteshealthmonitoringapplication.models.FragmentComponent;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<FragmentComponent> fragmentComponents = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentComponents.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return fragmentComponents.size();
    }

    public void addFragment(List<FragmentComponent> fragmentComponents) {
        this.fragmentComponents = fragmentComponents;
    }

}
