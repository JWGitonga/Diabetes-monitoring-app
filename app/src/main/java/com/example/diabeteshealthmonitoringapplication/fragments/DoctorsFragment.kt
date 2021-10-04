package com.example.diabeteshealthmonitoringapplication.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.diabeteshealthmonitoringapplication.viewmodels.DoctorLandingViewModel
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.adapters.DoctorsAdapter
import com.example.diabeteshealthmonitoringapplication.models.User
import com.example.diabeteshealthmonitoringapplication.notification.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.function.Consumer

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "DoctorsFragment"

class DoctorsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var apiService: APIService
    private lateinit var me1: User
    private lateinit var docsViewModel: DoctorLandingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docsViewModel = ViewModelProvider(this)[DoctorLandingViewModel::class.java]
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctors, container, false)
        val listView = view.findViewById<ListView>(R.id.doctors_list)
        val noChatIV = view.findViewById<ImageView>(R.id.no_docs_iv)
        val noChatTV = view.findViewById<TextView>(R.id.no_docs_tv)
        getUser()
        listView.clipToPadding = false
        docsViewModel.getAllDoctors().observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                noChatIV.visibility = View.VISIBLE
                noChatTV.visibility = View.VISIBLE
                listView.visibility = View.GONE
            } else {
                noChatIV.visibility = View.GONE
                noChatTV.visibility = View.GONE
                listView.visibility = View.VISIBLE
                val doctorsAdapter =
                    DoctorsAdapter(
                        requireContext(),
                        R.layout.doctor_list_item,
                        it
                    )
                listView.clipToPadding = false
                listView.adapter = doctorsAdapter
                doctorsAdapter.setOnItemClickListener { position: Int ->
                    Toast.makeText(requireContext(), "$position clicked", Toast.LENGTH_SHORT)
                        .show()
                    FirebaseDatabase.getInstance().getReference("requests/${it[position].uid}/${me1.uid}").setValue(me1)
                        .addOnCompleteListener { task->
                            if (task.isSuccessful && task.isComplete){
                                val data =
                                    Data(
                                        FirebaseAuth.getInstance().uid,
                                        me1.username + " wants to be your patient...",
                                        "Healthy Living",
                                        it[position].uid, R.drawable.ic_launcher_foreground
                                    )
                                val sender = Sender(data, me1.deviceToken)
                                apiService.sendNotification(sender).enqueue(object : Callback<MyResponse?> {
                                    override fun onResponse(
                                        call: Call<MyResponse?>,
                                        response: Response<MyResponse?>
                                    ) {
                                        if (response.isSuccessful || response.code() == 200) {
                                            if (response.body() != null) if (response.body()!!.success != 1) {
                                                Snackbar.make(
                                                    listView,
                                                    "Something went wrong try again...",
                                                    Snackbar.LENGTH_LONG
                                                ).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show()
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<MyResponse?>, t: Throwable) {
                                        Log.e(TAG, "onFailure: -> " + t.message)
                                    }
                                })
                            }
                        }
                }
            }
        })
        return view
    }

    private fun getUser() {
        FirebaseDatabase.getInstance().getReference("users")
            .addValueEventListener(object : ValueEventListener {
                @RequiresApi(api = Build.VERSION_CODES.N)
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach(Consumer { user: DataSnapshot ->
                        val me =
                            user.getValue(
                                User::class.java
                            )
                        if (me != null) {
                            if (me.uid == FirebaseAuth.getInstance().uid) {
                                me1 = me
                            }
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: -> $error.message")
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DoctorsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}