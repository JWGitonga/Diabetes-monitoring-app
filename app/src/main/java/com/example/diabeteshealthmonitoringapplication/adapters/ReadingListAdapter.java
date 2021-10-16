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
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.example.diabeteshealthmonitoringapplication.models.ReadingNode;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReadingListAdapter extends ArrayAdapter<ReadingNode> {
    private static final String TAG = "ReadingListAdapter";
    private final Context context;
    private final int resource;
    private final List<ReadingNode> userList;
    private int lastPosition = -1;
    private OnItemClick listener;
    private User me;

    public interface OnItemClick {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClick listener) {
        this.listener = listener;
    }

    public ReadingListAdapter(@NonNull Context context, int resource, @NonNull List<ReadingNode> userList) {
        super(context, resource, userList);
        this.context = context;
        this.resource = resource;
        this.userList = userList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = new ViewHolder();
        View result;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null, false);
            viewHolder.imageView = convertView.findViewById(R.id.patient_pro_pic_reading);
            viewHolder.name = convertView.findViewById(R.id.patients_name_reading);
            viewHolder.lastReading = convertView.findViewById(R.id.last_reading_value);
            viewHolder.lastReadingDate = convertView.findViewById(R.id.last_reading_date);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        ViewHolder finalViewHolder = viewHolder;
        FirebaseDatabase.getInstance().getReference("user")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User u = user.getValue(User.class);
                            if (u != null) {
                                if (u.getUid().equals(userList.get(position).getReadings().get(0).getFrom())) {
                                    Log.i(TAG, "onDataChange: uid -> "+userList.get(position).getReadings().get(0).getFrom());
                                    Picasso.get().load(me.getImageUrl()).placeholder(R.drawable.outline_account_circle_24).into(finalViewHolder.imageView);
                                    finalViewHolder.name.setText(me.getUsername());
                                    finalViewHolder.lastReadingDate.setText(userList.get(position).getReadings().get(0).getDate());
                                    finalViewHolder.lastReading.setText(userList.get(position).getReadings().get(0).getReading());
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "onCancelled: -> " + error.getMessage());
                    }
                });

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        convertView.setOnClickListener(v -> listener.onItemClick(position));
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView name, lastReading,lastReadingDate;
    }

    private void getUser(String uid) {
        FirebaseDatabase.getInstance().getReference("user")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User u = user.getValue(User.class);
                            if (u != null) {
                                if (u.getUid().equals(uid)) {
                                    me = u;
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
}
