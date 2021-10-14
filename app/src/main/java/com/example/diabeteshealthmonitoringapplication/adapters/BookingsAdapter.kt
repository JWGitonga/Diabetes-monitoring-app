package com.example.diabeteshealthmonitoringapplication.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.models.Appointment
import com.example.diabeteshealthmonitoringapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "BookingsAdapter"

class BookingsAdapter(private val appointments: List<Appointment>) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title_name_tv)
        private val hospital: TextView = itemView.findViewById(R.id.hospital_name_tv)
        private val time: TextView = itemView.findViewById(R.id.time_name_tv)
        private val date: TextView = itemView.findViewById(R.id.date_name_tv)
        fun bind(appointment: Appointment) {
            FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val user = it.getValue(User::class.java)
                            if (user != null) {
                                if (user.uid == FirebaseAuth.getInstance().uid){
                                    title.text=user.username
                                    time.text=appointment.time
                                    date.text=appointment.date
                                    hospital.text=appointment.hospital
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "onCancelled: -> ${error.message}")
                    }
                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        return BookingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.booking_item,parent,false)
        )
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(appointments[position])
    }

    override fun getItemCount()=appointments.size
}