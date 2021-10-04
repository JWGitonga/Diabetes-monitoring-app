package com.example.diabeteshealthmonitoringapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.diabeteshealthmonitoringapplication.DoctorLandingViewModel
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.adapters.PatientReadingsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ReadingsListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var docViewModel:DoctorLandingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        docViewModel = ViewModelProvider(this).get(DoctorLandingViewModel::class.java)
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
        val recyclerView: RecyclerView = view.findViewById(R.id.reading_recycler)
        recyclerView.setHasFixedSize(true)
        docViewModel.getReadings(FirebaseAuth.getInstance().uid!!).observe(viewLifecycleOwner,{ readings ->
            val readingListAdapter = PatientReadingsAdapter(readings, requireContext())
            recyclerView.adapter = readingListAdapter
            readingListAdapter.setOnItemClickListener {
                Snackbar.make(recyclerView,"clicked item position $it",Snackbar.LENGTH_SHORT).apply {
                    animationMode=Snackbar.ANIMATION_MODE_FADE
                    show()
                }
            }
        })
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
}