package com.example.diabeteshealthmonitoringapplication.adapters;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PatientListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "PatientListAdapter";
    private final Context context;
    private final int resource;
    private final List<User> userList;
    private int lastPosition = -1;
    private User mUser;


    public PatientListAdapter(@NonNull Context context, int resource, @NonNull List<User> userList) {
        super(context, resource, userList);
        this.context = context;
        this.resource = resource;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder = new ViewHolder();
        View result;
        if (convertView == null) {
            convertView = inflater.inflate(resource, null, false);
            viewHolder.imageView = convertView.findViewById(R.id.patient_pro_pic);
            viewHolder.call = convertView.findViewById(R.id.call_patient_item);
            viewHolder.name = convertView.findViewById(R.id.patients_name);
            viewHolder.number = convertView.findViewById(R.id.phone_number);
            viewHolder.accept = convertView.findViewById(R.id.patient_request_accept);
            viewHolder.reject = convertView.findViewById(R.id.patient_request_reject);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Picasso.get().load(userList.get(position).getImageUrl()).placeholder(R.drawable.outline_account_circle_24).into(viewHolder.imageView);
        viewHolder.name.setText(userList.get(position).getUsername());
        viewHolder.number.setText(userList.get(position).getPhone());
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.call.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.putExtra("number", userList.get(position).getPhone());
            context.startActivity(intent);
        });
        viewHolder.accept.setOnClickListener(v -> addToPatients(userList.get(position)));
        viewHolder.reject.setOnClickListener(v -> rejectRequest(userList.get(position).getUid()));
        return convertView;
    }

    private void rejectRequest(String uid) {
        FirebaseDatabase.getInstance().getReference("patients/" + FirebaseAuth.getInstance().getUid() + "/" + uid)
                .setValue(null).addOnCompleteListener(task -> {
            if (task.isComplete() && task.isComplete()) {
                Toast.makeText(context, "Successfully declined", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToPatients(User user) {
        String uid = FirebaseAuth.getInstance().getUid();
        FirebaseDatabase.getInstance().getReference("patients/" + uid + "/" + user.getUid())
                .setValue(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.isComplete()) {
                        Toast.makeText(context, "Patient added successfully", Toast.LENGTH_SHORT).show();
                        User user1 = getDoctor(uid);
                        FirebaseDatabase.getInstance().getReference(user.getUid() + "/doctors/" + uid)
                                .setValue(user1)
                                .addOnCompleteListener(task1 -> Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Log.i(TAG, "addToPatients: " + e.getMessage()));
                    }
                }).addOnFailureListener(e -> Log.i(TAG, "addToPatients: " + e.getMessage()));
    }

    private User getDoctor(String uid) {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User u = user.getValue(User.class);
                            if (u != null) {
                                if (u.getRole().equals("Doctor") && u.getUid().equals(uid)) {
                                    mUser = u;
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error.getMessage());
                    }
                });
        return mUser;
    }


    static class ViewHolder {
        ImageView imageView, call, accept, reject;
        TextView name, number;
    }
}
