package com.example.diabeteshealthmonitoringapplication.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.diabeteshealthmonitoringapplication.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "DoctorsViewModel"

class DoctorsViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var healthWorkers: ArrayList<User>
    private var doctors: MutableLiveData<ArrayList<User>> = MutableLiveData()

    fun getHealthWorkers(uid: String): LiveData<ArrayList<User>> {
        healthWorkers = ArrayList()
        FirebaseDatabase.getInstance().getReference("doctors/$uid")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val doc = ds.getValue(User::class.java)
                        if (doc != null) {
                            healthWorkers.add(doc)
                        }
                    }
                    doctors.postValue(healthWorkers)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })
        return doctors
    }
}
