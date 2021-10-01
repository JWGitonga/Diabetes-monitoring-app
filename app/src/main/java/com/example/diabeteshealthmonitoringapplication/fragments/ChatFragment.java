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
import com.example.diabeteshealthmonitoringapplication.adapters.ChatsListAdapterDocs;
import com.example.diabeteshealthmonitoringapplication.adapters.ChatsListAdapterDoctor;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.example.diabeteshealthmonitoringapplication.notification.APIService;
import com.example.diabeteshealthmonitoringapplication.notification.Client;
import com.example.diabeteshealthmonitoringapplication.notification.Data;
import com.example.diabeteshealthmonitoringapplication.notification.MyResponse;
import com.example.diabeteshealthmonitoringapplication.notification.Sender;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private ChatsListAdapterDoctor adapter;
    private ChatsListAdapterDocs adapter1;
    private List<User> myDoctors = new ArrayList<>();
    private List<User> doctors = new ArrayList<>();
    private ListView recyclerView;
    private ImageView noChatIV;
    private TextView noChatTV;
    private ListView recyclerView1;
    private ImageView noChatIV1;
    private TextView noChatTV1;
    private APIService apiService;
    private User me1;
    private User me2;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        noChatIV = view.findViewById(R.id.no_chat_iv);
        noChatTV = view.findViewById(R.id.no_chat_tv);
        recyclerView1 = view.findViewById(R.id.all_docs_recycler);
        noChatIV1 = view.findViewById(R.id.no_docs_iv);
        noChatTV1 = view.findViewById(R.id.no_docs_tv);
        getUser();
        getMyDoctors(FirebaseAuth.getInstance().getUid());
        getAllDoctors();
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
                            if (user!=null){
                                if (user.getRole().equals("Health worker")){
                                    doctors.add(user);
                                    Log.i(TAG, "onDataChange: user -> "+user.getUsername());
                                }
                            }
                        });
                        if (myDoctors.isEmpty()) {
                            noChatIV.setVisibility(View.VISIBLE);
                            noChatTV.setVisibility(View.VISIBLE);
                            recyclerView1.setVisibility(View.GONE);
                        } else {
                            noChatIV1.setVisibility(View.GONE);
                            noChatTV1.setVisibility(View.GONE);
                            recyclerView1.setVisibility(View.VISIBLE);
                            adapter1 = new ChatsListAdapterDocs(requireContext(),R.layout.doctor_list_item,doctors);
                            recyclerView1.setClipToPadding(false);
                            recyclerView1.setAdapter(adapter1);
                            recyclerView1.setAdapter(adapter1);
                            adapter.setOnItemClickListener(position -> {
                                Toast.makeText(requireContext(), position + " clicked", Toast.LENGTH_SHORT).show();
                                Data data = new Data(FirebaseAuth.getInstance().getUid(),
                                        me1.getUsername() + " wants to be your patient...",
                                        "Healthy Living",
                                        doctors.get(position).getUid(), R.drawable.ic_launcher_foreground);
                                Sender sender = new Sender(data, me1.getDeviceToken());
                                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                        if (response.isSuccessful() || response.code() == 200) {
                                            if (response.body() != null)
                                                if (response.body().success != 1) {
                                                    Snackbar.make(recyclerView1, "Something went wrong try again...", Snackbar.LENGTH_LONG).setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show();
                                                }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                        Log.e(TAG, "onFailure: -> " + t.getMessage());
                                    }
                                });
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

    private void getUser() {
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(user -> {
                    User me = user.getValue(User.class);
                    if (me != null) {
                        if (me.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                            me1 = me;
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: -> " + error.getMessage());
            }
        });
    }

    private void getUser(String uid) {
        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(user -> {
                    User me = user.getValue(User.class);
                    if (me != null) {
                        if (me.getUid().equals(uid)) {
                            me2 = me;
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: -> " + error.getMessage());
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
                        adapter = new ChatsListAdapterDoctor(requireContext(), R.layout.chat_list_item, myDoctors);
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error.getMessage());
                    }
                });
    }

}