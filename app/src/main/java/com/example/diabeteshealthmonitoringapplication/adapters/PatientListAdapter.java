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
import com.example.diabeteshealthmonitoringapplication.notification.APIService;
import com.example.diabeteshealthmonitoringapplication.notification.Client;
import com.example.diabeteshealthmonitoringapplication.notification.Data;
import com.example.diabeteshealthmonitoringapplication.notification.MyResponse;
import com.example.diabeteshealthmonitoringapplication.notification.Sender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientListAdapter extends ArrayAdapter<User> {
    private static final String TAG = "PatientListAdapter";
    private final Context context;
    private final int resource;
    private final List<User> userList;
    private int lastPosition = -1;
    private User mUser;
    private APIService apiService;


    public PatientListAdapter(@NonNull Context context, int resource, @NonNull List<User> userList) {
        super(context, resource, userList);
        this.context = context;
        this.resource = resource;
        this.userList = userList;
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mUser = getDoctor(FirebaseAuth.getInstance().getUid());
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
        viewHolder.reject.setOnClickListener(v -> rejectRequest(userList.get(position)));
        return convertView;
    }

    private void rejectRequest(User user) {
        String uid = FirebaseAuth.getInstance().getUid();
                Toast.makeText(context, "Successfully declined", Toast.LENGTH_SHORT).show();
                Data data = new Data(uid, "Doctor " + mUser.getUsername() + " refused your request", "Healthy Living", uid, R.drawable.ic_launcher_foreground);
                Sender sender = new Sender(data, mUser.getDeviceToken());
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().success != 1) {
                                    Toast.makeText(context, "Failed to send notification check internet and try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                        Log.i(TAG, "onFailure: error -> " + t.getMessage());
                    }
                });

    }
    private void addDoctors(User doctor,User you){
        FirebaseDatabase.getInstance().getReference("doctors/"+you.getUid()+"/"+doctor.getUid())
                .setValue(doctor)
                .addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful()){
                        Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Log.i(TAG, "onFailure: error -> "+e.getMessage());
                    Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
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
                        FirebaseDatabase.getInstance().getReference("doctors/" + user.getUid()+"/"+uid)
                                .setValue(user1)
                                .addOnCompleteListener(task1 -> {
                                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                                    addDoctors(user,user1);
                                    FirebaseDatabase.getInstance().getReference("users")
                                            .addValueEventListener(new ValueEventListener() {
                                                @RequiresApi(api = Build.VERSION_CODES.N)
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    snapshot.getChildren().forEach(user1 -> {
                                                        User u = user1.getValue(User.class);
                                                        if (u != null) {
                                                            if (u.getRole().equals("Doctor") && u.getUid().equals(uid)) {
                                                                Data data = new Data(uid, "Doctor " + mUser.getUsername() + " accepted your request", "Healthy Living", uid, R.drawable.ic_launcher_foreground);
                                                                Sender sender = new Sender(data, user.getDeviceToken());
                                                                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                                                    @Override
                                                                    public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                                                        if (response.isSuccessful()) {
                                                                            if (response.body() != null) {
                                                                                if (response.body().success != 1) {
                                                                                    Toast.makeText(context, "Failed to send notification check internet and try again", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                                                        Log.i(TAG, "onFailure: error -> " + t.getMessage());
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.i(TAG, "onCancelled: " + error.getMessage());
                                                }
                                            });
                                })
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
