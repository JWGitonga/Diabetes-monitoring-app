package com.example.diabeteshealthmonitoringapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.Registration;
import com.example.diabeteshealthmonitoringapplication.models.Chat;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InteractionFragment extends Fragment {
    private RecyclerView chatsRecycler;
    private EditText messageET;
    private static final String FROM_UID = "param1";
    private static final String TO_UID = "param2";

    private String fromUid;
    private String toUid;

    public InteractionFragment() {
        // Required empty public constructor
    }


    public static InteractionFragment newInstance(String param1, String param2) {
        InteractionFragment fragment = new InteractionFragment();
        Bundle args = new Bundle();
        args.putString(FROM_UID, param1);
        args.putString(TO_UID, param2);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fromUid = getArguments().getString(FROM_UID);
            toUid = getArguments().getString(TO_UID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interraction, container, false);
        chatsRecycler = view.findViewById(R.id.message_recycler);
        messageET = view.findViewById(R.id.message_et);
        ImageView send = view.findViewById(R.id.send);
        View myChatView = getLayoutInflater().inflate(R.layout.my_chat, container, false);
        View hisChatView = getLayoutInflater().inflate(R.layout.my_chat, container, false);
        TextView myName = myChatView.findViewById(R.id.my_name);
        TextView myText = myChatView.findViewById(R.id.my_text);
        ImageView sent = myChatView.findViewById(R.id.sent);
        TextView myTime = myChatView.findViewById(R.id.his_time);
        TextView hisName = hisChatView.findViewById(R.id.his_name);
        TextView hisText = hisChatView.findViewById(R.id.his_text);
        TextView hisTime = hisChatView.findViewById(R.id.my_time);
        String uid = FirebaseAuth.getInstance().getUid();
        List<Chat> chats = getData();
        for (Chat chat : chats) {
            if (chat.getFromUid().equals(uid)) {
                myName.setText(getNames(uid).getUsername());
                myText.setText(chat.getMessage());
                myTime.setText(chat.getTime());
                sent.setVisibility(View.VISIBLE);
                chatsRecycler.addView(myChatView);
            } else {
                hisName.setText(getNames(chat.getFromUid()).getUsername());
                hisText.setText(chat.getMessage());
                myTime.setText(chat.getTime());
                hisTime.setText(chat.getTime());
                chatsRecycler.addView(hisChatView);
            }
        }
        send.setOnClickListener(v -> {
            String text = messageET.getText().toString().trim();
            if (text.isEmpty()) return;
            String day = String.valueOf(Calendar.DAY_OF_MONTH);
            String month = String.valueOf(Calendar.MONTH);
            String year = String.valueOf(Calendar.YEAR);
            String hour = String.valueOf(Calendar.HOUR_OF_DAY);
            String minute = String.valueOf(Calendar.MINUTE);
            String date = day + "/" + month + "/" + year + " " + hour + ":" + minute;
            FirebaseDatabase.getInstance().getReference("messages/" + fromUid + "/" + toUid + "/messages" + System.currentTimeMillis())
                    .setValue(new Chat(FirebaseAuth.getInstance().getUid(), toUid, text, date))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            myName.setText(getNames(uid).getUsername());
                            myText.setText(text);
                            myTime.setText(date);
                            sent.setVisibility(View.VISIBLE);
                            chatsRecycler.addView(myChatView);
                        }
                    });
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.interaction_menu, menu);
    }

    private List<Chat> getData() {
        List<Chat> chats = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("messages/" + fromUid + "/" + toUid + "/messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Chat chat = ds.getValue(Chat.class);
                            chats.add(chat);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return chats;
    }

    private User getNames(String uid) {
        final User[] mUser = {null};
        FirebaseDatabase.getInstance().getReference("users/" + uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.getUid().equals(uid)) {
                    mUser[0] = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return mUser[0];
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_drugs:
                Toast.makeText(requireContext(), "Order drugs clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit:
                FirebaseAuth.getInstance().signOut();
                requireContext().startActivity(new Intent(requireActivity(), Registration.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}