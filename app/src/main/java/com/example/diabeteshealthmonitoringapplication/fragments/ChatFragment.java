package com.example.diabeteshealthmonitoringapplication.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.ChatsListAdapterDoctor;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private ChatsListAdapterDoctor adapter;
    private List<User> myDoctors = new ArrayList<>();
    private List<User> doctors = new ArrayList<>();
    private ListView recyclerView;
    private ImageView noChatIV;
    private TextView noChatTV;
    private ListView recyclerView1;
    private ImageView noChatIV1;
    private TextView noChatTV1;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        getMyDoctors(FirebaseAuth.getInstance().getUid());
        adapter = new ChatsListAdapterDoctor(requireContext(), R.layout.chat_list_item, myDoctors);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        noChatIV = view.findViewById(R.id.no_chat_iv);
        noChatTV = view.findViewById(R.id.no_chat_tv);
        getMyDoctors(FirebaseAuth.getInstance().getUid());
        recyclerView.setClipToPadding(false);
        recyclerView.setAdapter(adapter);
        if (myDoctors.isEmpty()) {
            noChatIV.setVisibility(View.VISIBLE);
            noChatTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            noChatIV.setVisibility(View.INVISIBLE);
            noChatTV.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setOnItemClickListener(position -> {
                Toast.makeText(requireContext(), position + " clicked", Toast.LENGTH_SHORT).show();
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,
                                InteractionFragment
                                        .newInstance(FirebaseAuth.getInstance().getUid(),
                                                myDoctors.get(position)
                                                        .getUid()));
            });
        }


        return view;
    }

    public void getAllDoctors() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(doc -> {
                            User user = doc.getValue(User.class);
                            myDoctors.add(user);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    private void getMyDoctors(String uid) {
        FirebaseDatabase.getInstance().getReference(uid + "/doctors")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(doc -> {
                            User user = doc.getValue(User.class);
                            myDoctors.add(user);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

}