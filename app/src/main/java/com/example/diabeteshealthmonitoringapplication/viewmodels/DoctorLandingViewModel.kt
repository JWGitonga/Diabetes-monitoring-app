package com.example.diabeteshealthmonitoringapplication.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.diabeteshealthmonitoringapplication.models.Reading
import com.example.diabeteshealthmonitoringapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val TAG = "DoctorLandingViewModel"

class DoctorLandingViewModel(application: Application) : AndroidViewModel(application) {
    private val doctors: MutableLiveData<List<User>> = MutableLiveData()
    private val myDoctors: MutableLiveData<List<User>> = MutableLiveData()
    private val readings: MutableLiveData<List<ReadingNode>> = MutableLiveData()
    private val patientReadingz: MutableLiveData<List<Reading>> = MutableLiveData()
    private val doctorsList: ArrayList<User> = ArrayList()
    private val myDoctorsList: ArrayList<User> = ArrayList()
    private val requestList: ArrayList<User> = ArrayList()
    private val myReadingz: ArrayList<Reading> = ArrayList()
    private val patientReadings: ArrayList<Reading> = ArrayList()
    private val myReadings: MutableLiveData<List<Reading>> = MutableLiveData()
    private val requestz: MutableLiveData<List<User>> = MutableLiveData()

    fun getAllDoctors(): LiveData<List<User>> {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val doctor = it.getValue(User::class.java)
                        if (doctor != null) {
                            if (doctor.role == "Health worker") {
                                doctorsList.add(doctor)
                            }
                        }
                    }
                    doctors.postValue(doctorsList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })
        return doctors
    }

    fun getMyDoctors(uid: String): LiveData<List<User>> {
        if (myDoctors == null) {
            FirebaseDatabase.getInstance().getReference("doctors/$uid")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            val doc = it.getValue(User::class.java)
                            if (doc != null) {
                                myDoctorsList.add(doc)
                            }
                        }
                        myDoctors.postValue(myDoctorsList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "onCancelled: ${error.message}")
                    }
                })
        }
        return myDoctors
    }

    fun getMyPatientsReadings(): LiveData<List<ReadingNode>> {
        val readingNodez = ArrayList<ReadingNode>()
        FirebaseDatabase.getInstance()
            .getReference("patients/${FirebaseAuth.getInstance().currentUser!!.uid}/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach { dataSnapshot ->
                        val patient = dataSnapshot.getValue(User::class.java)
                        if (patient != null) {
                            FirebaseDatabase.getInstance().getReference("readings")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach {
                                            val readingNode = it.getValue(ReadingNode::class.java)
                                            if (readingNode != null) {
                                                if (it.key == patient.uid) {
                                                    readingNodez.add(readingNode)
                                                }
                                            }
                                            readings.postValue(readingNodez)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(TAG, "onCancelled: ${error.message}")
                                    }
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })
        return readings
    }

    fun getPatientReadings(uid: String): LiveData<List<Reading>> {
        FirebaseDatabase.getInstance().getReference("readings/$uid/")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val reading = it.getValue(Reading::class.java)
                        if (reading != null) {
                            patientReadings.add(reading)
                        }
                    }
                    patientReadingz.postValue(patientReadings)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })
        return patientReadingz
    }


    fun getPatientRequests(uid: String): LiveData<List<User>> {
        FirebaseDatabase.getInstance().getReference("requests/$uid")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val request = it.getValue(User::class.java)
                        if (request != null) {
                            requestList.add(request)
                        }
                    }
                    requestz.postValue(requestList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })
        return requestz
    }


    fun getReadings(uid: String): LiveData<List<Reading>> {
        FirebaseDatabase.getInstance().getReference("readings/$uid/readings")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val reading = it.getValue(Reading::class.java)
                        if (reading != null) {
                            myReadingz.add(reading)
                        }
                    }
                    myReadings.postValue(myReadingz)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: getReadings -> ${error.message}")
                }
            })
        return myReadings
    }
}

data class ReadingNode(val uid: String, val readings: List<Reading>)