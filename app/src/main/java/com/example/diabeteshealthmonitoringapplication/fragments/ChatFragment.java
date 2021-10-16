package com.example.diabeteshealthmonitoringapplication.fragments;

import android.content.Intent;
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
    private ListView recyclerView;
    private ImageView noChatIV;
    private TextView noChatTV;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        noChatIV = view.findViewById(R.id.no_chat_iv);
        noChatTV = view.findViewById(R.id.no_chat_tv);
        String uid = FirebaseAuth.getInstance().getUid();
        Log.e(TAG, "onCreateView: uid -> " + uid);
        getMyDoctors();
        return view;
    }

    public void getMyDoctors() {
        List<User> doctors = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("doctors/" + FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User user1 = user.getValue(User.class);
                            if (user1 != null) {
                                doctors.add(user1);
                            }
                        });
                        if (doctors.isEmpty()) {
                            noChatIV.setVisibility(View.VISIBLE);
                            noChatTV.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.INVISIBLE);
                        } else {
                            adapter = new ChatsListAdapterDoctor(requireContext(), R.layout.chat_list_item, doctors);
                            adapter.notifyDataSetChanged();
                            recyclerView.setClipToPadding(false);
                            recyclerView.setAdapter(adapter);
                            noChatIV.setVisibility(View.INVISIBLE);
                            noChatTV.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.setOnItemClickListener(position -> {
                                Toast.makeText(requireContext(), position + " clicked", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(requireContext(), MessagingActivity.class).putExtra("uid",doctors.get(position).getUid()));
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}