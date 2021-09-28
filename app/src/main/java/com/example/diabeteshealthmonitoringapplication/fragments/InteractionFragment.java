package com.example.diabeteshealthmonitoringapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.example.diabeteshealthmonitoringapplication.activities.Registration;
import com.example.diabeteshealthmonitoringapplication.models.Chat;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.example.diabeteshealthmonitoringapplication.notification.APIService;
import com.example.diabeteshealthmonitoringapplication.notification.Client;
import com.example.diabeteshealthmonitoringapplication.notification.Data;
import com.example.diabeteshealthmonitoringapplication.notification.MyResponse;
import com.example.diabeteshealthmonitoringapplication.notification.Sender;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractionFragment extends Fragment {
    private static final String TAG = "InteractionFragment";
    private RecyclerView chatsRecycler;
    private EditText messageET;
    private static final String FROM_UID = "fromUid";
    private static final String TO_UID = "toUid";
    private String fromUid;
    private String toUid;
    private APIService apiService;
    private User him;
    private User me;
    private User mUser;

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
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        if (getArguments() != null) {
            fromUid = getArguments().getString(FROM_UID);
            toUid = getArguments().getString(TO_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interraction, container, false);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
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
        me = getNames(FirebaseAuth.getInstance().getUid());
        him = getNames(toUid);
        String uid = FirebaseAuth.getInstance().getUid();
        List<Chat> chats = getData();
        for (Chat chat : chats) {
            if (chat.getFromUid().equals(uid)) {
                myName.setText(getNames(uid).getUsername());
                myText.setText(chat.getMessage());
                myTime.setText(simpleDateFormat.format(chat.getTime()));
                sent.setVisibility(View.VISIBLE);
                chatsRecycler.addView(myChatView);
            } else {
                hisName.setText(getNames(chat.getFromUid()).getUsername());
                hisText.setText(chat.getMessage());
                hisTime.setText(simpleDateFormat.format(chat.getTime()));
                chatsRecycler.addView(hisChatView);
            }
        }
        send.setOnClickListener(v -> {
            String text = messageET.getText().toString().trim();
            if (text.isEmpty()) return;
            Long date = System.currentTimeMillis();
            FirebaseDatabase.getInstance().getReference("messages/" + fromUid + "/" + toUid + "/messages/" + System.currentTimeMillis())
                    .setValue(new Chat(FirebaseAuth.getInstance().getUid(), toUid, text, date))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            myName.setText(getNames(uid).getUsername());
                            myText.setText(text);
                            myTime.setText(simpleDateFormat.format(date));
                            sent.setVisibility(View.VISIBLE);
                            chatsRecycler.addView(myChatView);
                            Data data = new Data(me.getUid(),"New message "+text,"Health Living",him.getUid(),R.drawable.ic_launcher_foreground);
                            Sender sender = new Sender(data, him.getDeviceToken());
                            apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                    if (response.isSuccessful()){
                                        if (response.body() != null) {
                                            if (response.body().success!=1){
                                                Toast.makeText(requireContext(), "Failed to send notification check internet and try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                    Log.i(TAG, "onFailure: error -> "+t.getMessage());
                                }
                            });
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
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.getUid().equals(uid)) {
                    mUser = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "onCancelled: "+error.getMessage());
            }
        });
        return mUser;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order_drugs:
                Toast.makeText(requireContext(), "Order drugs clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.jumia.com"));
                requireContext().startActivity(intent);
                break;
            case R.id.exit:
                FirebaseAuth.getInstance().signOut();
                requireContext().startActivity(new Intent(requireActivity(), Registration.class));
                requireActivity().finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}