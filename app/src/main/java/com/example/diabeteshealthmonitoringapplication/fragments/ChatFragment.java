package com.example.diabeteshealthmonitoringapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.ChatsListAdapterDoctor;
import com.example.diabeteshealthmonitoringapplication.viewmodels.DoctorsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private ChatsListAdapterDoctor adapter;
    private ListView recyclerView;
    private ImageView noChatIV;
    private TextView noChatTV;
    private DoctorsViewModel doctorsViewModel;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        doctorsViewModel = new ViewModelProvider(requireActivity()).get(DoctorsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        noChatIV = view.findViewById(R.id.no_chat_iv);
        noChatTV = view.findViewById(R.id.no_chat_tv);
        String uid = FirebaseAuth.getInstance().getUid();
        Log.e(TAG, "onCreateView: uid -> " + uid);
        updateRecycler();
        return view;
    }

    public void updateRecycler() {
        doctorsViewModel.getHealthWorkers(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .observe(getViewLifecycleOwner(), users -> {
            if (users.isEmpty()) {
                noChatIV.setVisibility(View.VISIBLE);
                noChatTV.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
            } else {
                adapter = new ChatsListAdapterDoctor(requireContext(), R.layout.chat_list_item, users);
                adapter.notifyDataSetChanged();
                recyclerView.setClipToPadding(false);
                recyclerView.setAdapter(adapter);
                noChatIV.setVisibility(View.INVISIBLE);
                noChatTV.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setOnItemClickListener(position -> {
                    Toast.makeText(requireContext(), position + " clicked", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(requireContext(), MessagingActivity.class).putExtra("uid",users.get(position).getUid()));
                });
            }
        });
    }

}