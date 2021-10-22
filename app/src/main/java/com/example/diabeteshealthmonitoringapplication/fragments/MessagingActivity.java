package com.example.diabeteshealthmonitoringapplication.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.ChatAdapter;
import com.example.diabeteshealthmonitoringapplication.models.Chat;
import com.example.diabeteshealthmonitoringapplication.viewmodels.MessagesViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {
    private static final String TAG = "MessagingActivity";
    private RecyclerView chatsRecycler;
    private TextView noChatsYetTv;
    private ImageView noChatsYetIv;
    private EditText messageEt;
    private MessagesViewModel messagesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        String uid = getIntent().getStringExtra("uid");
        Log.i(TAG, "onCreate: uid -> "+uid);
        chatsRecycler = findViewById(R.id.messages_recycler);
        noChatsYetTv = findViewById(R.id.no_chats_yet_tv);
        noChatsYetIv = findViewById(R.id.no_chats_yet_iv);
        messageEt = findViewById(R.id.message_et);
        messagesViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        ImageView send = findViewById(R.id.send);
        List<Chat> chatList = new ArrayList<>();

        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference("messages")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(chat -> {
                            Chat ch = chat.getValue(Chat.class);
                            if (ch != null) {
                                if (ch.getFromUid()==myId && ch.getToUid()==uid || ch.getFromUid()==uid && ch.getToUid()==myId) {
                                    chatList.add(ch);
                                }
                            }
                        });
                        if (chatList.isEmpty()) {
                            noChatsYetIv.setVisibility(View.VISIBLE);
                            noChatsYetTv.setVisibility(View.VISIBLE);
                            chatsRecycler.setVisibility(View.GONE);
                        } else {
                            noChatsYetIv.setVisibility(View.GONE);
                            noChatsYetTv.setVisibility(View.GONE);
                            chatsRecycler.setVisibility(View.VISIBLE);
                            ChatAdapter adapter = new ChatAdapter(MessagingActivity.this, chatList);
                            chatsRecycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: error -> " + error.getMessage());
                    }
                });

        String Id = FirebaseAuth.getInstance().getUid();
        assert myId != null;
        messagesViewModel.getMessages(myId,uid).observe(this, chats -> {
                  if (chats.isEmpty()) {
                      noChatsYetIv.setVisibility(View.VISIBLE);
                      noChatsYetTv.setVisibility(View.VISIBLE);
                      chatsRecycler.setVisibility(View.GONE);
                  } else {
                      noChatsYetIv.setVisibility(View.GONE);
                      noChatsYetTv.setVisibility(View.GONE);
                      chatsRecycler.setVisibility(View.VISIBLE);
                      ChatAdapter adapter = new ChatAdapter(MessagingActivity.this, chatList);
                      chatsRecycler.setAdapter(adapter);
                      adapter.notifyDataSetChanged();
                  }
              });

        send.setOnClickListener(view -> {
            String message = messageEt.getText().toString();
            Chat chat = new Chat(FirebaseAuth.getInstance().getUid(), uid,message,System.currentTimeMillis());
            FirebaseDatabase.getInstance().getReference("messages/" +chat.getTime())
                    .setValue(chat)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.isComplete()){
                            messageEt.setText("");
                        }
                    })
                    .addOnFailureListener(e ->
                            Snackbar.make(messageEt,"Something went wrong try again",Snackbar.LENGTH_SHORT)
                            .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show());
        });
    }

}