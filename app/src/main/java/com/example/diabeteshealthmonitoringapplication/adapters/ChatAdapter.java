package com.example.diabeteshealthmonitoringapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.Chat;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final String TAG = "ChatAdapter";
    private final Context context;
    private final List<Chat> chats;

    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(
                LayoutInflater.from(context).inflate(R.layout.chat_item_row, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(chats.get(position));
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView hisName;
        private final TextView hisText;
        private final TextView hisDate;
        private final TextView myName;
        private final TextView myText;
        private final TextView myDate;
        private final CircleImageView hisPic;
        private final RelativeLayout myBubble;
        private final RelativeLayout hisBubble;

        public ChatViewHolder(View view) {
            super(view);
            myBubble = view.findViewById(R.id.my_bubble);
            hisBubble = view.findViewById(R.id.his_bubble);
            hisName = view.findViewById(R.id.other_name);
            hisText = view.findViewById(R.id.other_message);
            hisDate = view.findViewById(R.id.other_time);
            myName = view.findViewById(R.id.my_mName);
            myText = view.findViewById(R.id.my_message);
            myDate = view.findViewById(R.id.my_tTime);
            hisPic = view.findViewById(R.id.other_image);
        }

        public void bind(Chat chat) {
            if (chat.getFromUid().equals(FirebaseAuth.getInstance().getUid())) {
                hisBubble.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference("users")
                        .addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getChildren().forEach(user -> {
                                    User user1 = user.getValue(User.class);
                                    if (user1 != null) {
                                        myName.setText(user1.getUsername());
                                        myText.setText(chat.getMessage());
                                        myDate.setText(new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(chat.getTime()));
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "onCancelled: error -> " + error.getMessage());
                            }
                        });
            } else {
                myBubble.setVisibility(View.GONE);
                FirebaseDatabase.getInstance().getReference("users")
                        .addValueEventListener(new ValueEventListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                snapshot.getChildren().forEach(user -> {
                                    User user1 = user.getValue(User.class);
                                    if (user1 != null) {
                                        hisName.setText(user1.getUsername());
                                        hisText.setText(chat.getMessage());
                                        hisDate.setText(new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(chat.getTime()));
                                        Picasso.get().load(user1.getImageUrl()).placeholder(R.drawable.outline_account_circle_24).into(hisPic);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e(TAG, "onCancelled: error -> " + error.getMessage());
                            }
                        });
            }
        }
    }
}
