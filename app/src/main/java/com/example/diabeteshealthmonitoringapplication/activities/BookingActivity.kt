package com.example.diabeteshealthmonitoringapplication.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.adapters.BookingsAdapter
import com.example.diabeteshealthmonitoringapplication.models.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

private const val TAG = "BookingActivity"

class BookingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Bookings"
        val recyclerView: RecyclerView = findViewById(R.id.appointment_recyclerview)
        val noBookingIv: ImageView = findViewById(R.id.no_bookings_iv)
        val noBookingTv: TextView = findViewById(R.id.no_bookings_tv)
        val appointments: ArrayList<Appointment> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("appointments/" + FirebaseAuth.getInstance().uid + "/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val appointment = it.getValue(Appointment::class.java)
                        if (appointment != null) {
                            appointments.add(appointment)
                        }
                    }
                    if (appointments.isEmpty()) {
                        noBookingIv.visibility = View.VISIBLE
                        noBookingTv.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    } else {
                        noBookingIv.visibility = View.GONE
                        noBookingTv.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        val appointmentsAdapter = BookingsAdapter(appointments)
                        recyclerView.setHasFixedSize(true)
                        recyclerView.adapter = appointmentsAdapter
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: -> ${error.message}")
                }
            })
    }
}