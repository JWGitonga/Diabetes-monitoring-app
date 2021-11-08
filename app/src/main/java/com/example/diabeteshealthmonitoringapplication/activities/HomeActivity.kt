package com.example.diabeteshealthmonitoringapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.adapters.PagerAdapter
import com.example.diabeteshealthmonitoringapplication.fragments.*
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_layout_activity)
        val list = ArrayList<FragmentObject>()
        list.add(FragmentObject(name = "Readings", icon =R.drawable.reading, fragment = ReadingsFragment()))
        list.add(FragmentObject(name = "Interaction", icon =R.drawable.chat, fragment = ChatFragment()))
        list.add(FragmentObject(name = "Doctors", icon =R.drawable.register_with_doctor, fragment = DoctorsFragment()))
        list.add(FragmentObject(name = "Hospital", icon =R.drawable.outline_local_hospital_24, fragment = HospitalFragment()))
        list.add(FragmentObject(name = "Bookings", icon =R.drawable.outline_local_hospital_24, fragment = HospitalFragment()))
        list.add(FragmentObject(name = "Readings", icon =R.drawable.readings_list, fragment = HospitalFragment()))
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val mAdapter = PagerAdapter(ArrayList(), this, null)
        val viewPager = findViewById<ViewPager>(R.id.viewPager);
        viewPager.adapter = mAdapter
        tabLayout.setupWithViewPager(viewPager)
    }
}