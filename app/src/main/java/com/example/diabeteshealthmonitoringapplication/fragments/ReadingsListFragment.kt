package com.example.diabeteshealthmonitoringapplication.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.adapters.PatientReadingsAdapter
import com.example.diabeteshealthmonitoringapplication.models.Reading
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "ReadingsListFragment"

class ReadingsListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var readingList: ArrayList<Reading>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readingList = ArrayList()
        getReadings()
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_readings_list, container, false)
        val readingListAdapter = PatientReadingsAdapter(readingList, requireContext())
        val recyclerView: RecyclerView = view.findViewById(R.id.reading_recycler)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = readingListAdapter
        readingListAdapter.setOnItemClickListener {
            Snackbar.make(recyclerView,"clicked item position $it",Snackbar.LENGTH_SHORT).apply {
                animationMode=Snackbar.ANIMATION_MODE_FADE
                show()
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReadingsListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getReadings() {
        FirebaseDatabase.getInstance().getReference("reading/${FirebaseAuth.getInstance().uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val reading = it.getValue(Reading::class.java)
                        if (reading != null) {
                            readingList.add(reading)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: getReadings -> $error")
                }
            })
    }
}