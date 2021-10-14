package com.example.diabeteshealthmonitoringapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatsListAdapterDoctor extends ArrayAdapter<User> {
    private static final String TAG = "ChatsListAdapterDoctor";
    private final Context context;
    private final int resource;
    private final List<User> chatListItems;
    private OnItemClick listener;
    private int lastPosition = -1;
    private User required;

    public interface OnItemClick {
        void onItemClickListener(int position);
    }

    public void setOnItemClickListener(OnItemClick listener) {
        this.listener = listener;
    }

    public ChatsListAdapterDoctor(@NonNull Context context, int resource, @NonNull List<User> chatListItems) {
        super(context, resource, chatListItems);
        this.context = context;
        this.resource = resource;
        this.chatListItems = chatListItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = new ViewHolder();
        View result;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null, false);
            viewHolder.imageViewChat = convertView.findViewById(R.id.patient_pro_pic);
            viewHolder.nameChat = convertView.findViewById(R.id.call_patient_item);
            viewHolder.messageChat = convertView.findViewById(R.id.patients_name);
            viewHolder.timeChat = convertView.findViewById(R.id.phone_number);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        User user = getUserDetails(chatListItems.get(position).getUsername());
        Picasso.get().load(chatListItems.get(position).getImageUrl()).placeholder(R.drawable.outline_account_circle_24).into(viewHolder.imageViewChat);
        viewHolder.nameChat.setText(user.getUsername());
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        convertView.setOnClickListener(v -> listener.onItemClickListener(position));
        return convertView;
    }

    static class ViewHolder {
        ImageView imageViewChat;
        TextView nameChat, messageChat, timeChat;
    }

    User getUserDetails(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(node -> {
                    User user = node.getValue(User.class);
                    if (user != null) {
                        if (user.getUid().equals(uid)) {
                            required = user;
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "onCancelled: " + error.getMessage());
            }
        });
        return required;
    }
}
