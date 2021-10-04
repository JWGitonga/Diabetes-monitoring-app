package com.example.diabeteshealthmonitoringapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diabeteshealthmonitoringapplication.viewmodels.DoctorLandingViewModel;
import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.DoctorsAdapter;
import com.example.diabeteshealthmonitoringapplication.adapters.ChatsListAdapterDoctor;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.example.diabeteshealthmonitoringapplication.notification.APIService;
import com.example.diabeteshealthmonitoringapplication.notification.Client;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private ChatsListAdapterDoctor adapter;
    private DoctorsAdapter adapter1;
    private DoctorLandingViewModel doctorLandingViewModel;
    private ListView recyclerView;
    private ImageView noChatIV;
    private TextView noChatTV;
    private ListView recyclerView1;
    private ImageView noChatIV1;
    private TextView noChatTV1;
    private APIService apiService;
    private User me2;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        doctorLandingViewModel = new ViewModelProvider(this).get(DoctorLandingViewModel.class);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_list);
        noChatIV = view.findViewById(R.id.no_chat_iv);
        noChatTV = view.findViewById(R.id.no_chat_tv);

        doctorLandingViewModel.getMyDoctors(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .observe(getViewLifecycleOwner(), doctors -> {
                    adapter = new ChatsListAdapterDoctor(requireContext(), R.layout.chat_list_item, doctors);
                    recyclerView.setClipToPadding(false);
                    recyclerView.setAdapter(adapter);
                    if (doctors.isEmpty()) {
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
                                                            doctors.get(position)
                                                                    .getUid()));
                        });
                    }
                });
        return view;
    }




}